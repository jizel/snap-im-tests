package travel.snapshot.dp.qa.nonpms.jms.test.etl;

import static travel.snapshot.dp.qa.nonpms.jms.messages.Provider.SOCIALMEDIA_TWITTER;

import lombok.Getter;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.jms.messages.Provider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

@Ignore
@Getter
public class TwitterEtlTest extends BasicEtlTest {

    Provider provider = SOCIALMEDIA_TWITTER;

    @Value("${integration.twitter.start.queue}")
    String startQueue;
    @Value("${integration.twitter.start.firetime}")
    String fireTime;
    @Value("#{'${integration.twitter.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.twitter.timezone}")
    String timezone;

    private Instant timestamp = Instant.now();

    @Override
    protected String getAffectedDate() {
        return LocalDate.now(ZoneId.of(timezone)).toString();
    }

    @Override
    protected String getEndDate() {
        return getAffectedDate();
    }

}
