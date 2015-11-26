package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

public class TestFacebookPagesMidnightETL {

    public static final Logger logger = LoggerFactory.getLogger(TestFacebookPagesMidnightETL.class);

    @Test
    public void testStagingLoad() throws Exception {
        String sqlQueryForSource = "select count(*)*2 from RawImportedFacebookPageStatistics where date=date_sub(curdate(), interval 1 day)";
        String sqlQueryForTarget = "select count(*)*3 from IncrementalFacebookPageStatistics where date=date_sub(curdate(), interval 1 day)";

        logger.info("\nStart control checks on table 'IncrementalFacebookPageStatistics'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add("select sum(total_followers) from RawImportedFacebookPageStatistics where date = curdate()");
        followUpListToSource.add(
        		"select sum(yr2.incremental_engagements - yr1.incremental_engagements + tdr1.incremental_engagements) "
        		+ "from "
        		+ "(select from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate()");
        followUpListToSource.add(
        		"select sum(yr2.incremental_impressions - yr1.incremental_impressions + tdr1.incremental_impressions) "
        		+ "from "
        		+ "(select from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate()");
        followUpListToSource.add(
        		"select sum(yr2.incremental_reached - yr1.incremental_reached + tdr1.incremental_reached) "
        		+ "from "
        		+ "(select from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate()");
        followUpListToSource.add(
        		"select sum(yr2.incremental_likes - yr1.incremental_likes + tdr1.incremental_likes) "
        		+ "from "
        		+ "(select from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate()");
        followUpListToSource.add(
        		"select sum(yr2.incremental_unlikes - yr1.incremental_unlikes + tdr1.incremental_unlikes) "
        		+ "from "
        		+ "(select from RawImportedFacebookPageStatistics fbr1 where fbr1.data_collection_run = 1) tdr1 "
        		+ "inner join "
        		+ "(select from RawImportedFacebookPageStatistics fbr2 where fbr2.data_collection_run = 1) yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY) "
        		+ "inner join "
        		+ "(select from RawImportedFacebookPageStatistics fbr3 where fbr3.data_collection_run = 2) yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date "
        		+ "where tdr1.date = curdate()");
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToSource.add("select sum(total_followers) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day)");
        followUpListToTarget.add("select sum(incremental_engagements) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day)");
        followUpListToTarget.add("select sum(incremental_impressions) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day)");
        followUpListToTarget.add("select sum(incremental_reached) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day)");
        followUpListToTarget.add("select sum(incremental_likes) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day)");
        followUpListToTarget.add("select sum(incremental_unlikes) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day)");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testFactLoad() throws Exception {
        String sqlQueryForSource = "select count(*) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day)";
        String sqlQueryForTarget = "select count(*) from FactFacebookPageStats where dim_date_id = (curdate() - interval 1 day) + 0";
        
        String sqlQueryForSourceFollowers = "select sum(total_followers) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day)";
        String sqlQueryForTargetFollowers = "select sum(followers) from FactFacebookPageStats where dim_date_id = (curdate() - interval 1 day) + 0";
        
        logger.info("\nStart control checks on table 'FactFacebookPageStats'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        testLoad(sqlQueryForSourceFollowers, sqlQueryForTargetFollowers);
        
        List<String> factsYesterdayList = new ArrayList<String>();
        factsYesterdayList.add("select sum(engagements) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsYesterdayList.add("select sum(impressions) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsYesterdayList.add("select sum(reached) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsYesterdayList.add("select sum(likes) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
        factsYesterdayList.add("select sum(unlikes) from FactFacebookPageStats where dim_date_id = (curdate() - interval 2 day) + 0");
   
        List<String> incrementalsTodayList = new ArrayList<String>();
        incrementalsTodayList.add("select sum(incremental_engagements) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day");
        incrementalsTodayList.add("select sum(incremental_impressions) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day");
        incrementalsTodayList.add("select sum(incremental_reached) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day");
        incrementalsTodayList.add("select sum(incremental_likes) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day");
        incrementalsTodayList.add("select sum(incremental_unlikes) from IncrementalFacebookPageStatistics where date = date_sub(curdate(), interval 1 day");        
        
        List<String> factsTodayList = new ArrayList<String>();
        factsTodayList.add("select sum(engagements) from FactFacebookPageStats where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select sum(impressions) from FactFacebookPageStats where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select sum(reached) from FactFacebookPageStats where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select sum(likes) from FactFacebookPageStats where dim_date_id = (curdate() - interval 1 day) + 0");
        factsTodayList.add("select sum(unlikes) from FactFacebookPageStats where dim_date_id = (curdate() - interval 1 day) + 0");
        
        followUpLoadTestFacebook(factsYesterdayList, incrementalsTodayList, factsTodayList);
    }

}
