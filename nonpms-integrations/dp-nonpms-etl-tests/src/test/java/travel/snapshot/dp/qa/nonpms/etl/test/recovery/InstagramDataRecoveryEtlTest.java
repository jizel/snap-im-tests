package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.InstagramDwhDao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_INSTAGRAM;

@Getter
public class InstagramDataRecoveryEtlTest extends AbstractApproximationDataRecoveryEtlTest {

	Provider provider = SOCIALMEDIA_INSTAGRAM;

	@Value("${integration.instagram.start.queue}")
	String startQueue;
	@Value("${integration.instagram.start.firetime}")
	String fireTime;
	@Value("#{'${integration.instagram.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
//	@Value("${integration.instagram.end-date}")
//	String endDate;
	@Value("${integration.instagram.timezone}")
	String timezone;

	private Instant timestamp = Instant.now();

	@Autowired
	InstagramDwhDao integrationDwhDao;

	// Workaround for DPNP-127
	@Override
	protected String getEndDate() {
		return LocalDate.now(ZoneId.of(timezone)).toString();
	}

}
