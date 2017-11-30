package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.etl.util.DateUtils.toDateId;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.createSchedulerMessageJson;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.extractLocalDate;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.extractTimestamp;

import lombok.Getter;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import travel.snapshot.dp.qa.nonpms.etl.test.AbstractEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.IntegrationDwhDao;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@ImportAutoConfiguration({
        DataSourceTransactionManagerAutoConfiguration.class,
        JdbcTemplateAutoConfiguration.class,
        MybatisAutoConfiguration.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MapperScan("travel.snapshot.dp.qa.nonpms.etl.test.dal")
public abstract class AbstractDataRecoveryEtlTest extends AbstractEtlTest {

    static final String IGNORED_FIRE_TIME = "2017-01-01T00:00:00Z";

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

    @Test(timeout = 60000)
    public void testEtlForDataRecovery() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);

        jms.send(getStartQueue(), createSchedulerMessageJson(createFireTimeForMidnight(DAY5, getTimezone())));

        checkReloadedDays(asList(DAY2, DAY3, DAY4, DAY5), start);
    }

    @Test(timeout = 60000)
    public void testEtlForDataRecoveryForCustomIntegration() throws Exception {
        LocalDateTime start = getCurrentTimestamp();

        createLastDataGap(DAY1, DAY5);

        for (String propertyId : getAffectedProperties()) {
            jms.send(getStartQueue(), createSchedulerMessageJson(IGNORED_FIRE_TIME, propertyId, DAY5.toString()));
        }

        checkReloadedDays(asList(DAY2, DAY3, DAY4, DAY5), start);
    }

    private void createLastDataGap(LocalDate since, LocalDate until) {
        getAffectedProperties().forEach(propertyId -> {
            getIntegrationDwhDao().deleteData(propertyId, toDateId(since), toDateId(until));
            getIntegrationDwhDao().insertData(propertyId, toDateId(since));
        });
    }

    private void checkReloadedDays(List<LocalDate> days, LocalDateTime timestamp) throws Exception {
        for (LocalDate day : days) {
            affectedDate = day.toString();

            checkNotifications();

            checkReloadedData(day, timestamp);
        }
    }
    private void checkReloadedData(LocalDate day, LocalDateTime timestamp) {
        getAffectedProperties().forEach(propertyId -> {
            Map<String, Object> data = getIntegrationDwhDao().getData(propertyId, toDateId(affectedDate));
            assertThat(extractLocalDate(data, "date_id")).isEqualTo(day);
            assertThat(extractTimestamp(data, "inserted_time_stamp")).isAfter(timestamp);
        });
    }

    private static String createFireTimeForMidnight(LocalDate date, String timezone) {
        return date.plusDays(1).atStartOfDay(ZoneId.of(timezone)).toInstant().toString();
    }

    private static LocalDateTime getCurrentTimestamp() {
        return LocalDateTime.now(Clock.systemUTC());
    }

}
