package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.GoogleAnalyticsDwhDao;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.WEBPERFORMANCE_GOOGLEANALYTICS;

@Getter
public class WebPerformanceDataRecoveryEtlTest extends AbstractApproximationDataRecoveryEtlTest {

	Provider provider = WEBPERFORMANCE_GOOGLEANALYTICS;

	@Value("${integration.googleanalytics.start.queue}")
	String startQueue;
	@Value("#{'${integration.googleanalytics.affected.properties}'.split(',')}")
	Set<String> affectedProperties;
	@Value("${integration.googleanalytics.timezone}")
	String timezone;

	@Autowired
	GoogleAnalyticsDwhDao integrationDwhDao;

}
