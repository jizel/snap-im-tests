package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

/*
 * Automated Tests for Facebook posts Noon ETLs.
 * 
 * Please consult the page in Confluence to get a basic overview of the Facebook data collection.
 * All values are total, there are not incremental values
 * 
 * This is the path of the data for noon ETLs:
 * Facebook (2x/day)-> Raw data table (1x/day)-> Fact table for noon runs
 * 
 * Noon ETLs occur at 12:01 local time for hotels in time zones [-6;+4].
 * 
 * The tests validate ETLs for the previous day and can be executed at any time
*/

public class TestFacebookPostsNoonETL {

	public static final Logger logger = LoggerFactory.getLogger(TestFacebookPagesNoonETL.class);

    @Test
    public void testFactLoad() throws Exception {
    	//The noon ETL for each hotel is triggered after the second run to get data from the Facebook API has passed
        String sqlQueryForSource = "select count(*) from RawImportedFacebookPostStatistics where date=curdate() - interval 1 day and data_collection_run = 2";
        String sqlQueryForTarget = "select count(*) from FactFacebookPostStatsCurrDay where date=curdate() - interval 1 day";

        logger.info("\nStart control checks on table 'FactFacebookPostStatsCurrDay'");
        testLoad(sqlQueryForSource, sqlQueryForTarget, "Total counts: ");
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add("select coalesce(sum(reach), 0) from RawImportedFacebookPostStatistics where date = curdate() - interval 1 day and data_collection_run = 2");
        followUpListToSource.add("select coalesce(sum(engagement), 0) from RawImportedFacebookPostStatistics where date = curdate() - interval 1 day and data_collection_run = 2");

        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select coalesce(sum(reach), 0) from FactFacebookPostStatsCurrDay where date = curdate() - interval 1 day");
        followUpListToTarget.add("select coalesce(sum(engagement), 0) from FactFacebookPostStatsCurrDay where date = curdate() - interval 1 day");
        
        List<String> metrics = new ArrayList<String>();
        metrics.add("Metric: reach");
        metrics.add("Metric: engagement");
        
        followUpLoadTest(followUpListToSource, followUpListToTarget, metrics);
    }

}
