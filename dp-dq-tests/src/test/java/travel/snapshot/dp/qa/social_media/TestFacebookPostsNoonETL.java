package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

public class TestFacebookPostsNoonETL {

	public static final Logger logger = LoggerFactory.getLogger(TestFacebookPagesNoonETL.class);

    @Test
    public void testFactLoad() throws Exception {
        String sqlQueryForSource = "select count(*) from RawImportedFacebookPostStatistics where date=curdate() and data_collection_run = 2";
        String sqlQueryForTarget = "select count(*) from FactFacebookPostStatsCurrDay where date=curdate()";

        logger.info("\nStart control checks on table 'FactFacebookPostStatsCurrDay'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add("select sum(reach) from RawImportedFacebookPostStatistics where date = curdate() and data_collection_run = 2");
        followUpListToSource.add("select sum(engagement) from RawImportedFacebookPostStatistics where date = curdate() and data_collection_run = 2");

        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select sum(reach) from FactFacebookPostStatsCurrDay where date = curdate()");
        followUpListToTarget.add("select sum(engagement) from FactFacebookPostStatsCurrDay where date = curdate()");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

}
