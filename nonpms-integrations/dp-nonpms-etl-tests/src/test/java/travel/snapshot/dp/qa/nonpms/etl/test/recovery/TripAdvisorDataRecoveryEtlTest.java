package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.TripAdvisorDwhDao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.REVIEW_TRIPADVISOR;

@Getter
public class TripAdvisorDataRecoveryEtlTest extends AbstractApproximationDataRecoveryEtlTest {

	Provider provider = REVIEW_TRIPADVISOR;

	@Value("${integration.tripadvisor.start.queue}")
	String startQueue;
	@Value("${integration.tripadvisor.start.firetime}")
	String fireTime;
	@Value("#{'${integration.tripadvisor.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
//	@Value("${integration.tripadvisor.end-date}")
//	String endDate;
	@Value("${integration.tripadvisor.timezone}")
	String timezone;

	private Instant timestamp = Instant.now();

	@Autowired
	TripAdvisorDwhDao integrationDwhDao;

	// Workaround for DPNP-127
	@Override
	protected String getEndDate() {
		return LocalDate.now(ZoneId.of(timezone)).toString();
	}

}
