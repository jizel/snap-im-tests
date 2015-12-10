package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

/*
 * Automated Tests for Facebook pages Midnight ETLs.
 * 
 * Please consult the page in Confluence to get a basic overview of the Facebook data collection.
 * All values are incremental, with the exception of values for followers, which are total
 * 
 * This is the path of the data for midnight ETLs:
 * Facebook (3x/day)-> Raw data table (2x/day)-> Staging table (2x/day)-> Intermediate table (1x/day)-> Fact table
 * 
 * The following transfers are covered by tests:
 * Raw data table -> Staging table
 * Staging table -> Fact table
 * 
 * Midnight ETLs occur at 00:01 local time for hotels in time zones [-6;+4].
 * 
 * The tests validate ETLs for the previous day and can be executed at any time
*/

public class TestFacebookPagesMidnightETL {

    public static final Logger logger = LoggerFactory.getLogger(TestFacebookPagesMidnightETL.class);

    @Test
    public void testStagingLoad() throws Exception {
    	//The midnight ETL for each hotel is triggered after the first run to get data from the Facebook API has passed
    	//This ETL isn't triggered on the very first run
        String sqlQueryForSource = "select count(*)*2 from RawImportedFacebookPageStatistics where date=date_sub(curdate(), interval 2 day)";
        String sqlQueryForTarget = "select count(*)*3 from IncrementalFacebookPageStatistics where date=date_sub(curdate(), interval 2 day)";

        logger.info("\nStart control checks on table 'IncrementalFacebookPageStatistics'");
        testLoad(sqlQueryForSource, sqlQueryForTarget, "Total counts: ");
        
        List<String> followUpListToSource = new ArrayList<String>();
        //Separate checks for followers are used, since the values are total, instead of incremental
        followUpListToSource.add("select sum(total_followers) from RawImportedFacebookPageStatistics where date = curdate() - interval 2 day and data_collection_run = 1");
        followUpListToSource.add(
        		"select sum(yr2.incremental_number_of_posts - yr1.incremental_number_of_posts + tdr1.incremental_number_of_posts) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
        followUpListToSource.add(
        		"select sum(yr2.incremental_engagements - yr1.incremental_engagements + tdr1.incremental_engagements) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
        followUpListToSource.add(
        		"select sum(yr2.incremental_impressions - yr1.incremental_impressions + tdr1.incremental_impressions) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
        followUpListToSource.add(
        		"select sum(yr2.incremental_reached - yr1.incremental_reached + tdr1.incremental_reached) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
        followUpListToSource.add(
        		"select sum(yr2.incremental_likes - yr1.incremental_likes + tdr1.incremental_likes) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
        followUpListToSource.add(
        		"select sum(yr2.incremental_unlikes - yr1.incremental_unlikes + tdr1.incremental_unlikes) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add(
        		"select sum(total_followers) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select sum(incremental_number_of_posts) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select sum(incremental_engagements) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select sum(incremental_impressions) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select sum(incremental_reached) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select sum(incremental_likes) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select sum(incremental_unlikes) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        
        List<String> metrics = new ArrayList<String>();
        metrics.add("Metric: followers");
        metrics.add("Metric: number of posts");
        metrics.add("Metric: engagement");
        metrics.add("Metric: impressions");
        metrics.add("Metric: reach");
        metrics.add("Metric: likes");
        metrics.add("Metric: unlikes");
        
        followUpLoadTest(followUpListToSource, followUpListToTarget, metrics);
    }

    @Test
    public void testFactLoad() throws Exception {
    	//The transfer to the fact table is triggered immediately after the transfer to the staging table has finished
        String sqlQueryForSource = "select count(*) from IncrementalFacebookPageStatistics where date = curdate() - interval 2 day";
        String sqlQueryForTarget = "select count(*)*2 from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0";
        
        //Separate checks for followers are used, since the values are total, instead of incremental
        String sqlQueryForSourceFollowers = 
        		"select sum(total_followers) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp";
        String sqlQueryForTargetFollowers = "select sum(followers) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0";
        
        logger.info("\nStart control checks on table 'FactFacebookPageStats'");
        testLoad(sqlQueryForSource, sqlQueryForTarget, "Total counts: ");
        testLoad(sqlQueryForSourceFollowers, sqlQueryForTargetFollowers, "Metric: followers");
        
        List<String> factsYesterdayList = new ArrayList<String>();
        factsYesterdayList.add("select sum(number_of_posts) from FactFacebookPageStats where dim_date_id = (curdate() - interval 3 day) + 0");
        factsYesterdayList.add("select sum(engagements) from FactFacebookPageStats where dim_date_id = (curdate() - interval 3 day) + 0");
        factsYesterdayList.add("select sum(impressions) from FactFacebookPageStats where dim_date_id = (curdate() - interval 3 day) + 0");
        factsYesterdayList.add("select sum(reach) from FactFacebookPageStats where dim_date_id = (curdate() - interval 3 day) + 0");
        factsYesterdayList.add("select sum(likes) from FactFacebookPageStats where dim_date_id = (curdate() - interval 3 day) + 0");
        factsYesterdayList.add("select sum(unlikes) from FactFacebookPageStats where dim_date_id = (curdate() - interval 3 day) + 0");
   
        List<String> incrementalsTodayList = new ArrayList<String>();
        incrementalsTodayList.add(
        		"select sum(incremental_number_of_posts) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        incrementalsTodayList.add(
        		"select sum(incremental_engagements) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        incrementalsTodayList.add(
        		"select sum(incremental_impressions) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        incrementalsTodayList.add(
        		"select sum(incremental_reached) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        incrementalsTodayList.add(
        		"select sum(incremental_likes) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        incrementalsTodayList.add(
        		"select sum(incremental_unlikes) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");      
        
        List<String> factsTodayList = new ArrayList<String>();
        factsTodayList.add("select sum(number_of_posts) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsTodayList.add("select sum(engagements) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsTodayList.add("select sum(impressions) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsTodayList.add("select sum(reach) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsTodayList.add("select sum(likes) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsTodayList.add("select sum(unlikes) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        
        List<String> metrics = new ArrayList<String>();
        metrics.add("number of posts");
        metrics.add("engagement");
        metrics.add("impressions");
        metrics.add("reach");
        metrics.add("likes");
        metrics.add("unlikes");
        
        testLoadFacebook(factsYesterdayList, incrementalsTodayList, factsTodayList, metrics);
    }

}
