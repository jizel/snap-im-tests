package travel.snapshot.dp.qa.nonpms.etl.test.basic;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.REVIEW_TRIPADVISOR;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.basic.BasicEtlTest;

import java.util.Set;

@Getter
public class TripAdvisorEtlTest extends BasicEtlTest {

    Provider provider = REVIEW_TRIPADVISOR;

    @Value("${integration.tripadvisor.start.queue}")
    String startQueue;
    @Value("#{'${integration.tripadvisor.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.tripadvisor.timezone}")
    String timezone;

}
