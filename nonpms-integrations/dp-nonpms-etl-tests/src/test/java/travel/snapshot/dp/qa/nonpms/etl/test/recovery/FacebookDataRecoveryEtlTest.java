package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_FACEBOOK;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.FacebookDwhDao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

@Getter
public class FacebookDataRecoveryEtlTest extends AbstractDataRecoveryEtlTest {

	Provider provider = SOCIALMEDIA_FACEBOOK;

	@Value("${integration.facebook.start.queue}")
	String startQueue;
	@Value("${integration.facebook.start.firetime}")
	String fireTime;
	@Value("#{'${integration.facebook.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
//	@Value("${integration.facebook.end-date}")
//	String endDate;
	@Value("${integration.facebook.timezone}")
	String timezone;

	private Instant timestamp = Instant.now();

	@Autowired
	FacebookDwhDao integrationDwhDao;

	// Workaround for DPNP-127
	@Override
	protected String getEndDate() {
		return LocalDate.now(ZoneId.of(timezone)).toString();
	}

}
