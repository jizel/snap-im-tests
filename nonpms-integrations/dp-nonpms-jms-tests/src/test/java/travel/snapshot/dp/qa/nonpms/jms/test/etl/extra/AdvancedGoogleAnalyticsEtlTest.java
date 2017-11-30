package travel.snapshot.dp.qa.nonpms.jms.test.etl.extra;

import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.jms.messages.Provider.WEBPERFORMANCE_GOOGLEANALYTICS;
import static travel.snapshot.dp.qa.nonpms.jms.util.DateUtils.toDateId;
import static travel.snapshot.dp.qa.nonpms.jms.util.Helpers.createSchedulerMessageJson;
import static travel.snapshot.dp.qa.nonpms.jms.util.Helpers.extractTimestamp;

import lombok.Getter;
import org.junit.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;
import travel.snapshot.dp.qa.nonpms.jms.messages.Provider;
import travel.snapshot.dp.qa.nonpms.jms.test.etl.AbstractEtlTest;
import travel.snapshot.dp.qa.nonpms.jms.test.etl.extra.dal.GoogleAnalyticsDwhDao;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Getter
@Transactional
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MapperScan("travel.snapshot.dp.qa.nonpms.jms.test.etl.extra.dal")
public class AdvancedGoogleAnalyticsEtlTest extends AbstractEtlTest {

    Provider provider = WEBPERFORMANCE_GOOGLEANALYTICS;

    @Value("${integration.googleanalytics.affected.date}")
    String affectedDate;
    @Value("${integration.googleanalytics.start.queue}")
    String startQueue;
    @Value("${integration.googleanalytics.start.firetime}")
    String fireTime;
    @Value("#{'${integration.googleanalytics.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.googleanalytics.end-date}")
    String endDate;

    private Instant timestamp = Instant.now();

    @Autowired
    GoogleAnalyticsDwhDao dwhDao;

    @Test(timeout = 60000)
    public void testEtlWithOverrideData() throws Exception {
        LocalDateTime start = LocalDateTime.now(Clock.systemUTC());

        jms.send(getStartQueue(), createSchedulerMessageJson(getFireTime(), true));

        checkNotifications();

        affectedProperties.forEach(propertyId -> {
            Map<String, Object> data = dwhDao.getData(propertyId, toDateId(affectedDate));
            assertThat(extractTimestamp(data, "inserted_time_stamp")).isAfter(start);
        });
    }

}
