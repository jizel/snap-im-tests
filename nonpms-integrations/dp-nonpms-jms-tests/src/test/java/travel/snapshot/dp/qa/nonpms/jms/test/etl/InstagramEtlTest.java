package travel.snapshot.dp.qa.nonpms.jms.test.etl;

import static travel.snapshot.dp.qa.nonpms.jms.messages.Provider.SOCIALMEDIA_INSTAGRAM;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.jms.messages.Provider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

@Getter
public class InstagramEtlTest extends AbstractEtlTest {

    Provider provider = SOCIALMEDIA_INSTAGRAM;

    @Value("${integration.instagram.start.queue}")
    String startQueue;
    @Value("${integration.instagram.start.message} ")
    String startMessage;
    @Value("#{'${integration.instagram.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.instagram.timezone}")
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