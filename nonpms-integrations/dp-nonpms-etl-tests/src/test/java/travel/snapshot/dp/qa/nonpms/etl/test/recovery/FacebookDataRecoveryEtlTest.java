package travel.snapshot.dp.qa.nonpms.etl.test.recovery;

import static travel.snapshot.dp.qa.nonpms.etl.messages.Provider.SOCIALMEDIA_FACEBOOK;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import travel.snapshot.dp.qa.nonpms.etl.messages.Provider;
import travel.snapshot.dp.qa.nonpms.etl.test.dal.FacebookDwhDao;

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

}
