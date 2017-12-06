package travel.snapshot.dp.qa.nonpms.etl.test;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_FACEBOOK;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;

import java.util.Set;

@Getter
public class FacebookEtlTest extends BasicEtlTest {

	Provider provider = SOCIALMEDIA_FACEBOOK;

	@Value("${integration.facebook.start.queue}")
	String startQueue;
	@Value("#{'${integration.facebook.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
	@Value("${integration.facebook.timezone}")
	String timezone;

}
