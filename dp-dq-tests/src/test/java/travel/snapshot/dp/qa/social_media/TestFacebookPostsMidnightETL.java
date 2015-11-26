package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

public class TestFacebookPostsMidnightETL {

    public static final Logger logger = LoggerFactory.getLogger(TestFacebookPostsMidnightETL.class);

    @Test
    public void testStagingLoad() throws Exception {
        String sqlQueryForSource = "select count(*) from RawImportedFacebookPostStatistics where date = date_sub(curdate(), interval 1 day)";
        String sqlQueryForTarget = "select count(*) from FactFacebookPostStats where dim_date_id = date_sub(curdate() - interval 1 day) + 0";

        logger.info("\nStart control checks on table 'FactFacebookPostStats'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add("select sum(reach) from RawImportedFacebookPostStatistics where date = curdate()");
        followUpListToSource.add("select sum(engagement) from RawImportedFacebookPostStatistics where date = curdate()");
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select sum(reach) from FactFacebookPostStats where dim_date_id = date_sub(curdate() - interval 1 day) + 0");
        followUpListToTarget.add("select sum(engagement) from FactFacebookPostStats where dim_date_id = date_sub(curdate() - interval 1 day) + 0");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

}
