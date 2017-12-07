package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_FACEBOOK;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.extractLocalDate;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.extractTimestamp;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.FacebookDwhDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Getter
public class FacebookDataRecoveryEtlTest extends AbstractReloadDataRecoveryEtlTest {

	Provider provider = SOCIALMEDIA_FACEBOOK;

	@Value("${integration.facebook.start.queue}")
	String startQueue;
	@Value("#{'${integration.facebook.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
	@Value("${integration.facebook.timezone}")
	String timezone;

	@Autowired
	FacebookDwhDao integrationDwhDao;

	@Override
	protected void validateData(Map<String, Object> data, LocalDate day, LocalDateTime timestamp) {
		assertThat(extractLocalDate(data, "date_id")).isEqualTo(day);
		assertThat(extractTimestamp(data, "inserted_time_stamp")).isAfter(timestamp);
	}

}
