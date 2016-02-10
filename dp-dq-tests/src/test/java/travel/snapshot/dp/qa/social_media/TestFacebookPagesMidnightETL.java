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
        followUpListToSource.add("select coalesce(sum(total_followers), 0) from RawImportedFacebookPageStatistics where date = curdate() - interval 2 day and data_collection_run = 1");
        followUpListToSource.add(
        		"select coalesce(sum(yr2.incremental_number_of_posts - yr1.incremental_number_of_posts + tdr1.incremental_number_of_posts), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
        followUpListToSource.add(
        		"select coalesce(sum(yr2.incremental_engagements - yr1.incremental_engagements + tdr1.incremental_engagements), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
        followUpListToSource.add(
        		"select coalesce(sum(yr2.incremental_impressions - yr1.incremental_impressions + tdr1.incremental_impressions), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
        followUpListToSource.add(
        		"select coalesce(sum(yr2.incremental_reached - yr1.incremental_reached + tdr1.incremental_reached), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
        followUpListToSource.add(
        		"select coalesce(sum(yr2.incremental_likes - yr1.incremental_likes + tdr1.incremental_likes), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
        followUpListToSource.add(
        		"select coalesce(sum(yr2.incremental_unlikes - yr1.incremental_unlikes + tdr1.incremental_unlikes), 0) "
        		+ "from "
        		+ "(select * from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select * from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate() - interval 2 day");
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToTarget.add(
        		"select coalesce(sum(total_followers), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_number_of_posts), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_engagements), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_impressions), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_reached), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_likes), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 3 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        followUpListToTarget.add(
        		"select coalesce(sum(incremental_unlikes), 0) "
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
        		"select coalesce(sum(total_followers), 0) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date <= date_sub(curdate(), interval 2 day) "
        		+ "and total_followers is not null "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp";
        String sqlQueryForTargetFollowers = "select coalesce(sum(followers), 0) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0";
        
        logger.info("\nStart control checks on table 'FactFacebookPageStats'");
        testLoad(sqlQueryForSource, sqlQueryForTarget, "Total counts: ");
        testLoad(sqlQueryForSourceFollowers, sqlQueryForTargetFollowers, "Metric: followers");
        
        List<String> factsYesterdayList = new ArrayList<String>();
        factsYesterdayList.add(
        		"select coalesce(sum(number_of_posts), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 3 day) + 0 "
        		+ "and number_of_posts is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        factsYesterdayList.add(
        		"select coalesce(sum(engagements), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 3 day) + 0 "
        		+ "and engagements is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        factsYesterdayList.add(
        		"select coalesce(sum(impressions), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 3 day) + 0 "
        		+ "and impressions is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        factsYesterdayList.add(
        		"select coalesce(sum(reach), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 3 day) + 0 "
        		+ "and reach is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        factsYesterdayList.add(
        		"select coalesce(sum(likes), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 3 day) + 0 "
        		+ "and likes is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
        factsYesterdayList.add(
        		"select coalesce(sum(unlikes), 0) "
        		+ "from FactFacebookPageStats t "
        		+ "inner join ("
        		+ "select dim_property_id, max(inserted_time_stamp) as max_time_stamp "
        		+ "from FactFacebookPageStats "
        		+ "where dim_date_id <= (curdate() - interval 3 day) + 0 "
        		+ "and unlikes is not null "
        		+ "group by dim_property_id) tt "
        		+ "on t.dim_property_id = tt.dim_property_id and t.inserted_time_stamp = max_time_stamp ");
   
        List<String> incrementalsTodayList = new ArrayList<String>();
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_number_of_posts,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_engagements,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_impressions,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_reached,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_likes,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");
        incrementalsTodayList.add(
        		"select sum(coalesce(incremental_unlikes,0)) "
        		+ "from IncrementalFacebookPageStatistics t "
        		+ "inner join ("
        		+ "select property_id, max(time_stamp) as max_time_stamp "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate(), interval 2 day) "
        		+ "group by property_id) tt "
        		+ "on t.property_id = tt.property_id and t.time_stamp = max_time_stamp");      
        
        List<String> factsTodayList = new ArrayList<String>();
        factsTodayList.add("select coalesce(sum(number_of_posts), 0) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsTodayList.add("select coalesce(sum(engagements), 0) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsTodayList.add("select coalesce(sum(impressions), 0) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsTodayList.add("select coalesce(sum(reach), 0) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsTodayList.add("select coalesce(sum(likes), 0) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsTodayList.add("select coalesce(sum(unlikes), 0) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        
        List<String> metrics = new ArrayList<String>();
        metrics.add("number of posts");
        metrics.add("engagement");
        metrics.add("impressions");
        metrics.add("reach");
        metrics.add("likes");
        metrics.add("unlikes");
        
        verifyLoad(factsYesterdayList, incrementalsTodayList, factsTodayList, metrics);
    }

}