package travel.snapshot.dp.qa.nonpms.etl.test.basic;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_INSTAGRAM;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;

import java.util.Set;

@Getter
public class InstagramEtlTest extends BasicEtlTest {

    Provider provider = SOCIALMEDIA_INSTAGRAM;

    @Value("${integration.instagram.start.queue}")
    String startQueue;
    @Value("#{'${integration.instagram.affected.properties}'.split(',')}")
    Set<String> affectedProperties;
    @Value("${integration.instagram.timezone}")
    String timezone;

}
