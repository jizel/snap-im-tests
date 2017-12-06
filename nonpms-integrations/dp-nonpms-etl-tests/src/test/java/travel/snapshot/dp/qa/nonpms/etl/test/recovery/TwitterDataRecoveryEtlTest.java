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
	@Value("#{'${integration.twitter.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
	@Value("${integration.twitter.timezone}")
	String timezone;

	@Autowired
	TwitterDwhDao integrationDwhDao;

}
