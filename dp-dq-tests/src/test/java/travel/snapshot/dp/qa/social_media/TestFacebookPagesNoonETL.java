package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

/*
 * Automated Tests for Facebook pages Noon ETLs.
 * 
 * Noon ETLs occur at 12:01 local time for hotels in time zones [-6;+4]. Tests are meant to be run after all ETLs have passed,
 * and before midnight ETLs for the next day have started.
*/

public class TestFacebookPagesNoonETL {

    public static final Logger logger = LoggerFactory.getLogger(TestFacebookPagesNoonETL.class);

    @Test
    public void testStagingLoad() throws Exception {
    	//Noon ETL for each hotel is triggered after the third run to get data from Facebook API has passed.
    	//The check for counts should pass at any point in time.
        String sqlQueryForSource = "select count(*) from RawImportedFacebookPageStatistics where date=curdate() and data_collection_run = 3";
        String sqlQueryForTarget = "select count(*) from IncrementalFacebookPageStatistics where date=curdate()";

        logger.info("\nStart control checks on table 'IncrementalFacebookPageStatistics'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        //separate checks for followers are used, since the values are total, instead of incremental
        followUpListToSource.add("select sum(total_followers) from RawImportedFacebookPageStatistics where date = curdate() and data_collection_run = 3");
        followUpListToSource.add(
        		"select sum(r2.incremental_number_of_posts - r1.incremental_number_of_posts + r3.incremental_number_of_posts) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate()");
        followUpListToSource.add(
        		"select sum(r2.incremental_engagements - r1.incremental_engagements + r3.incremental_engagements) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate()");
        followUpListToSource.add(
        		"select sum(r2.incremental_impressions - r1.incremental_impressions + r3.incremental_impressions) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate()");
        followUpListToSource.add(
        		"select sum(r2.incremental_reached - r1.incremental_reached + r3.incremental_reached) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate()");
        followUpListToSource.add(
        		"select sum(r2.incremental_likes - r1.incremental_likes + r3.incremental_likes) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate()");
        followUpListToSource.add(
        		"select sum(r2.incremental_unlikes - r1.incremental_unlikes + r3.incremental_unlikes) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) r1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 2) r2 ON r1.property_id = r2.property_id AND r1.date_id = r2.date_id "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 3) r3 ON r2.property_id = r3.property_id AND r2.date_id = r3.date_id "
        		+ "where r1.date = curdate()");
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add("select sum(total_followers) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_number_of_posts) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_engagements) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_impressions) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_reached) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_likes) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_unlikes) from IncrementalFacebookPageStatistics where date = curdate()");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testFactLoad() throws Exception {
    	//The transfer to the fact table is triggered immediately after the transfer to the staging table has finished
        String sqlQueryForSource = "select count(*) from IncrementalFacebookPageStatistics where date = curdate()";
        String sqlQueryForTarget = "select count(*) from T_FactFacebookPageStatsCurrDay where dim_date_id = curdate()+0";

        //separate checks for followers are used, since the values are total, instead of incremental
        String sqlQueryForSourceFollowers = "select sum(total_followers) from IncrementalFacebookPageStatistics where date = curdate()";
        String sqlQueryForTargetFollowers = "select sum(followers) from T_FactFacebookPageStatsCurrDay where dim_date_id = curdate()+0";
        
        logger.info("\nStart control checks on table 'T_FactFacebookPageStatsCurrDay'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        testLoad(sqlQueryForSourceFollowers, sqlQueryForTargetFollowers);
        
        List<String> factsYesterdayList = new ArrayList<String>();
        factsYesterdayList.add("select sum(number_of_posts) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");       		
        factsYesterdayList.add("select sum(engagements) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsYesterdayList.add("select sum(impressions) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsYesterdayList.add("select sum(reach) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsYesterdayList.add("select sum(likes) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsYesterdayList.add("select sum(unlikes) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        
        List<String> incrementalsTodayList = new ArrayList<String>();
        incrementalsTodayList.add(
        		"select sum(incremental_number_of_posts) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        incrementalsTodayList.add(
        		"select sum(incremental_engagements) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        incrementalsTodayList.add(
        		"select sum(incremental_impressions) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        incrementalsTodayList.add(
        		"select sum(incremental_reached) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        incrementalsTodayList.add(
        		"select sum(incremental_likes) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp");
        incrementalsTodayList.add(
        		"select sum(incremental_unlikes) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, min(time_stamp) as min_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = curdate() - interval 1 day "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = min_time_stamp"); 
        
        List<String> factsTodayList = new ArrayList<String>();
        factsTodayList.add("select sum(number_of_posts) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select sum(engagements) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select sum(impressions) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select sum(reach) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select sum(likes) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select sum(unlikes) from T_FactFacebookPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
        
        followUpLoadTestFacebook(factsYesterdayList, incrementalsTodayList, factsTodayList);
        
    }

}
