package travel.snapshot.dp.qa.nonpms.jms.test.etl;

import static travel.snapshot.dp.qa.nonpms.jms.messages.Provider.WEBPERFORMANCE_GOOGLEANALYTICS;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.jms.messages.Provider;

import java.time.Instant;
import java.util.Set;

@Getter
public class GoogleAnalyticsEtlTest extends AbstractEtlTest {

    Provider provider = WEBPERFORMANCE_GOOGLEANALYTICS;

    @Value("${integration.googleanalytics.affected.date}")
    String affectedDate;
    @Value("${integration.googleanalytics.start.queue}")
    String startQueue;
    @Value("${integration.googleanalytics.start.message} ")
    String startMessage;
    @Value("#{'${integration.googleanalytics.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.googleanalytics.end-date}")
    String endDate;

    private Instant timestamp = Instant.now();

}
