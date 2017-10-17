package travel.snapshot.dp.qa.jms;

import static travel.snapshot.dp.qa.jms.messages.Provider.SOCIALMEDIA_FACEBOOK;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.jms.messages.Provider;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

@Getter
public class FacebookEtlTest extends AbstractEtlTest {

	Provider provider = SOCIALMEDIA_FACEBOOK;

	@Value("${integration.facebook.affected.date}")
	String affectedDate;
	@Value("${integration.facebook.start.queue}")
	String startQueue;
	@Value("${integration.facebook.start.message} ")
	String startMessage;
	@Value("#{'${integration.facebook.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
//	@Value("${integration.facebook.end-date}")
//	String endDate;
	@Value("${integration.facebook.timezone}")
	String timezone;

	private Instant timestamp = Instant.now();

	// Workaround for DPNP-127
	@Override
	protected String getEndDate() {
		return LocalDate.now(ZoneId.of(timezone)).toString();
	}
}
