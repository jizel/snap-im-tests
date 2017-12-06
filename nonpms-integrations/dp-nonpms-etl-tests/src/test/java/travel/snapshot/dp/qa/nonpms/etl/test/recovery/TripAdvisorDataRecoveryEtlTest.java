package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.REVIEW_TRIPADVISOR;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.IntegrationDwhDao;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.TripAdvisorDwhDao;

import java.util.Set;

@Getter
public class TripAdvisorDataRecoveryEtlTest extends AbstractApproximationDataRecoveryEtlTest {

	Provider provider = REVIEW_TRIPADVISOR;

	@Value("${integration.tripadvisor.start.queue}")
	String startQueue;
	@Value("#{'${integration.tripadvisor.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
	@Value("${integration.tripadvisor.timezone}")
	String timezone;

	@Autowired
	TripAdvisorDwhDao integrationDwhDao;

}
