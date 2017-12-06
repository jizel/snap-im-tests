package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.etl.util.DateUtils.toDateId;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.extractLocalDate;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.extractTimestamp;
import static travel.snapshot.dp.qa.nonpms.etl.util.JsonConverter.convertToJson;

import lombok.Getter;
import org.junit.Before;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import travel.snapshot.dp.qa.nonpms.etl.messages.SchedulerMessage.SchedulerMessageBuilder;
import travel.snapshot.dp.qa.nonpms.etl.test.AbstractEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.IntegrationDwhDao;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ImportAutoConfiguration({
        DataSourceTransactionManagerAutoConfiguration.class,
        JdbcTemplateAutoConfiguration.class,
        MybatisAutoConfiguration.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MapperScan("travel.snapshot.dp.qa.nonpms.etl.test.dal")
public abstract class AbstractDataRecoveryEtlTest extends AbstractEtlTest {

    static final Instant IGNORED_FIRE_TIME = Instant.parse("2017-01-01T00:00:00Z");

    protected abstract IntegrationDwhDao getIntegrationDwhDao();
    protected abstract String getTimezone();

    LocalDate DAY1, DAY2, DAY3, DAY4, DAY5;

    @Getter
    String affectedDate = null;

    @Before
    public void setUp() {
        super.setUp();

        DAY5 = LocalDate.now(ZoneId.of(getTimezone()));
        DAY4 = DAY5.minusDays(1);
        DAY3 = DAY5.minusDays(2);
        DAY2 = DAY5.minusDays(3);
        DAY1 = DAY5.minusDays(4);
    }

    void createLastDataGap(LocalDate since, LocalDate until) {
        deleteData(since, until);
        insertData(since);
    }

    void deleteData(LocalDate since, LocalDate until) {
        runForAffectedProperties(id -> getIntegrationDwhDao().deleteData(id, toDateId(since), toDateId(until)));
    }

    void insertData(LocalDate date) {
        runForAffectedProperties(id -> getIntegrationDwhDao().insertData(id, toDateId(date)));
    }

    void start(Supplier<SchedulerMessageBuilder> schedulerMessageFactory) throws Exception {
        jms.send(getStartQueue(), convertToJson(schedulerMessageFactory.get().build()));
    }

    void startForAffectedProperties(Function<String, SchedulerMessageBuilder> schedulerMessageFactory) throws Exception {
        for (String propertyId : getAffectedProperties()) {
            jms.send(getStartQueue(), convertToJson(schedulerMessageFactory.apply(propertyId).build()));
        }
    }

    void checkNotificationForDay(LocalDate day) throws Exception {
        affectedDate = day.toString();
        checkNotifications();
    }

    void checkReloadedDays(List<LocalDate> days, LocalDateTime timestamp) throws Exception {
        for (LocalDate day : days) {
            checkNotificationForDay(day);
            checkReloadedData(day, timestamp);
        }
    }

    void checkReloadedData(LocalDate day, LocalDateTime timestamp) {
        getAffectedProperties().forEach(propertyId -> {
            Map<String, Object> data = getIntegrationDwhDao().getData(propertyId, toDateId(affectedDate));
            assertThat(extractLocalDate(data, "date_id")).isEqualTo(day);
            assertThat(extractTimestamp(data, "inserted_time_stamp")).isAfter(timestamp);
        });
    }

    private void runForAffectedProperties(Consumer<String> call) {
        getAffectedProperties().forEach(propertyId -> call.accept(propertyId));
    }

    static Instant createFireTimeForMidnight(LocalDate date, String timezone) {
        return date.plusDays(1).atStartOfDay(ZoneId.of(timezone)).toInstant();

    }

    static LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now(Clock.systemUTC());
    }
}
