package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.etl.util.DateUtils.toDateId;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.extractLocalDate;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.extractTimestamp;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.getCurrentDate;

import org.junit.Before;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import travel.snapshot.dp.qa.nonpms.etl.test.AbstractEtlTest;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.IntegrationDwhDao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    static final Instant IGNORED_FIRE_TIME = Instant.parse("2017-01-01T00:00:00Z");

    protected abstract IntegrationDwhDao getIntegrationDwhDao();

    protected abstract String getTimezone();

    LocalDate DAY1, DAY2, DAY3, DAY4, DAY5;

    @Before
    public void setUp() {
        super.setUp();

        DAY5 = getCurrentDate(getTimezone());
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

    void checkReloadedData(List<LocalDate> days, LocalDateTime timestamp) {
        for (LocalDate day : days) {
            checkReloadedData(day, timestamp);
        }
    }

    void checkReloadedData(LocalDate day, LocalDateTime timestamp) {
        getAffectedProperties().forEach(propertyId -> {
            Map<String, Object> data = getIntegrationDwhDao().getData(propertyId, toDateId(day));
            validateData(data, day, timestamp);
        });
    }

    protected void validateData(Map<String, Object> data, LocalDate day, LocalDateTime timestamp) {
        assertThat(extractLocalDate(data, "date_id")).isEqualTo(day);
        assertThat(extractTimestamp(data, "inserted_time_stamp")).isAfter(timestamp);
        assertThat((data.get("is_amended"))).isEqualTo(true);
    }

}
