package travel.snapshot.dp.qa.nonpms.etl.test.basic;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_TWITTER;

import lombok.Getter;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;

import java.util.Set;

@Ignore
@Getter
public class TwitterEtlTest extends BasicEtlTest {

    Provider provider = SOCIALMEDIA_TWITTER;

    @Value("${integration.twitter.start.queue}")
    String startQueue;
    @Value("#{'${integration.twitter.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.twitter.timezone}")
    String timezone;

}
