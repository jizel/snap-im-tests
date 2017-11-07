package travel.snapshot.dp.qa.nonpms.dwh.test;

import lombok.Getter;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import travel.snapshot.dp.qa.nonpms.dwh.test.dal.FacebookDwhDao;
import travel.snapshot.dp.qa.nonpms.dwh.test.dal.TwitterDwhDao;

@Getter
public class TwitterDwhTest extends AbstractDwhTest {

    @Getter
    @Autowired
    TwitterDwhDao dataGapProvider;

}
