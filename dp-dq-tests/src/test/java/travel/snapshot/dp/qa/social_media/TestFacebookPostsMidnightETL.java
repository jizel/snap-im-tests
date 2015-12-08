package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

/*
 * Automated Tests for Facebook posts Midnight ETLs.
 * 
 * Please consult the page in Confluence to get a basic overview of the Facebook data collection.
 * All values are total, there are not incremental values
 * 
 * This is the path of the data for midnight ETLs:
 * Facebook (2x/day)-> Raw data table (1x/day)-> Fact table
 * 
 * Midnight ETLs occur at 00:01 local time for hotels in time zones [-6;+4].
 * 
 * The tests validate ETLs for the previous day and can be executed at any time
*/

public class TestFacebookPostsMidnightETL {

    public static final Logger logger = LoggerFactory.getLogger(TestFacebookPostsMidnightETL.class);

    @Test
    public void testStagingLoad() throws Exception {
    	//The midnight ETL for each hotel is triggered after the first run to get data from the Facebook API has passed
        String sqlQueryForSource = "select count(*) from RawImportedFacebookPostStatistics where date = curdate() - interval 2 day and data_collection_run = 1";
        String sqlQueryForTarget = "select count(*) from FactFacebookPostStats where dim_date_id = (curdate() - interval 2 day) + 0";

        logger.info("\nStart control checks on table 'FactFacebookPostStats'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add("select sum(reach) from RawImportedFacebookPostStatistics where date = curdate() - interval 2 day and data_collection_run = 1");
        followUpListToSource.add("select sum(engagement) from RawImportedFacebookPostStatistics where date = curdate() - interval 2 day and data_collection_run = 1");
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select sum(reach) from FactFacebookPostStats where dim_date_id = (curdate() - interval 2 day) + 0");
        followUpListToTarget.add("select sum(engagement) from FactFacebookPostStats where dim_date_id = (curdate() - interval 2 day) + 0");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

}
