package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.TwitterDwhDao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_TWITTER;

@Getter
public class TwitterDataRecoveryEtlTest extends AbstractApproximationDataRecoveryEtlTest {

	Provider provider = SOCIALMEDIA_TWITTER;

	@Value("${integration.twitter.start.queue}")
	String startQueue;
	@Value("${integration.twitter.start.firetime}")
	String fireTime;
	@Value("#{'${integration.twitter.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
//	@Value("${integration.twitter.end-date}")
//	String endDate;
	@Value("${integration.twitter.timezone}")
	String timezone;

	private Instant timestamp = Instant.now();

	@Autowired
	TwitterDwhDao integrationDwhDao;

	// Workaround for DPNP-127
	@Override
	protected String getEndDate() {
		return LocalDate.now(ZoneId.of(timezone)).toString();
	}

}
