package travel.snapshot.dp.qa.nonpms.dwh.test;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import travel.snapshot.dp.qa.nonpms.dwh.test.dal.FacebookDwhDao;

public class FacebookDwhTest extends AbstractDwhTest {

	@Getter
	@Autowired
	FacebookDwhDao dataGapProvider;

}

