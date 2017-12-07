package travel.snapshot.dp.qa.nonpms.etl.test.basic;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.WEBPERFORMANCE_GOOGLEANALYTICS;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;

import java.util.Set;

@Getter
public class GoogleAnalyticsEtlTest extends BasicEtlTest {

    Provider provider = WEBPERFORMANCE_GOOGLEANALYTICS;

    @Value("${integration.googleanalytics.start.queue}")
    String startQueue;
    @Value("#{'${integration.googleanalytics.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.googleanalytics.timezone}")
    String timezone;

}
