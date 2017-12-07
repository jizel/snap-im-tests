package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_INSTAGRAM;
import static travel.snapshot.dp.qa.nonpms.etl.util.Helpers.extractLocalDate;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.InstagramDwhDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Getter
public class InstagramDataRecoveryEtlTest extends AbstractApproximationDataRecoveryEtlTest {

	Provider provider = SOCIALMEDIA_INSTAGRAM;

	@Value("${integration.instagram.start.queue}")
	String startQueue;
	@Value("#{'${integration.instagram.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
	@Value("${integration.instagram.timezone}")
	String timezone;

	@Autowired
	InstagramDwhDao integrationDwhDao;


	@Override
	protected void validateData(Map<String, Object> data, LocalDate day, LocalDateTime timestamp) {
		assertThat(extractLocalDate(data, "date_id")).isEqualTo(day);
	}

}
