package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_TWITTER;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.extractLocalDate;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.TwitterDwhDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

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

	@Override
	protected void validateData(Map<String, Object> data, LocalDate day, LocalDateTime timestamp) {
		assertThat(extractLocalDate(data, "date_id")).isEqualTo(day);
	}

}
