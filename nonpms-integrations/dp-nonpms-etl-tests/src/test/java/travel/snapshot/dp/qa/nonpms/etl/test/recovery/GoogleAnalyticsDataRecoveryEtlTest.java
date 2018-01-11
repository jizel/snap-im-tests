package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.WEBPERFORMANCE_GOOGLEANALYTICS;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.GoogleAnalyticsDwhDao;

import java.util.Set;

@Getter
public class GoogleAnalyticsDataRecoveryEtlTest extends AbstractReloadDataRecoveryEtlTest {

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