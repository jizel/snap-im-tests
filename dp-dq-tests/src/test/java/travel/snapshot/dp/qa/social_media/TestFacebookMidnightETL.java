package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

public class TestFacebookMidnightETL {

    public static final Logger logger = LoggerFactory.getLogger(TestFacebookMidnightETL.class);

    @Test
    public void testStagingLoad() throws Exception {
        String sqlQueryForSource = "select count(*)*2 from RawIncrementalFacebookPageStatistics where date=date_sub(curdate() interval 1 day)";
        String sqlQueryForTarget = "select count(*)*3 from IncrementalFacebookPageStatistics where date=date_sub(curdate() interval 1 day)";

        logger.info("\nStart control checks on table 'IncrementalFacebookPageStatistics'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> followUpListToSource = new ArrayList<String>();
        followUpListToSource.add("select sum(total_followers) from RawIncrementalFacebookPageStatistics where date = date_sub(curdate() interval 1 day)");
        followUpListToSource.add(
        		"select sum(r2.incremental_engagements - r1.incremental_engagements + r3.incremental_engagements) "
        		+ "from "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr1 where fbr1.data_collection_run = 'R1') tdr1 "
        		+ "inner join "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr2 where fbr2.data_collection_run = 'R1') yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY)"
        		+ "inner join "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr3 where fbr3.data_collection_run = 'R2') yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date"
        		+ "where tdr1.date = curdate()");
        followUpListToSource.add(
        		"select sum(r2.incremental_impressions - r1.incremental_impressions + r3.incremental_impressions) "
        		+ "from "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr1 where fbr1.data_collection_run = 'R1') tdr1 "
        		+ "inner join "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr2 where fbr2.data_collection_run = 'R1') yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY)"
        		+ "inner join "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr3 where fbr3.data_collection_run = 'R2') yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date"
        		+ "where tdr1.date = curdate()");
        followUpListToSource.add(
        		"select sum(r2.incremental_reached - r1.incremental_reached + r3.incremental_reached) "
        		+ "from "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr1 where fbr1.data_collection_run = 'R1') tdr1 "
        		+ "inner join "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr2 where fbr2.data_collection_run = 'R1') yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY)"
        		+ "inner join "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr3 where fbr3.data_collection_run = 'R2') yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date"
        		+ "where tdr1.date = curdate()");
        followUpListToSource.add(
        		"select sum(r2.incremental_likes - r1.incremental_likes + r3.incremental_likes) "
        		+ "from "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr1 where fbr1.data_collection_run = 'R1') tdr1 "
        		+ "inner join "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr2 where fbr2.data_collection_run = 'R1') yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY)"
        		+ "inner join "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr3 where fbr3.data_collection_run = 'R2') yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date"
        		+ "where tdr1.date = curdate()");
        followUpListToSource.add(
        		"select sum(r2.incremental_unlikes - r1.incremental_unlikes + r3.incremental_unlikes) "
        		+ "from "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr1 where fbr1.data_collection_run = 'R1') tdr1 "
        		+ "inner join "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr2 where fbr2.data_collection_run = 'R1') yr1 ON tdr1.property_id = yr1.property_id AND yr1.date = DATE_SUB(tdr1.date, INTERVAL 1 DAY)"
        		+ "inner join "
        		+ "(select from RawIncrementalFacebookPageStatistics fbr3 where fbr3.data_collection_run = 'R2') yr2 ON yr1.property_id = yr2.property_id AND yr1.date = yr2.date"
        		+ "where tdr1.date = curdate()");
   
        List<String> followUpListToTarget = new ArrayList<String>();
        followUpListToSource.add("select sum(total_followers) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_engagements) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_impressions) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_reached) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_likes) from IncrementalFacebookPageStatistics where date = curdate()");
        followUpListToTarget.add("select sum(incremental_unlikes) from IncrementalFacebookPageStatistics where date = curdate()");
        
        followUpLoadTest(followUpListToSource,followUpListToTarget);
    }

    @Test
    public void testFactLoad() throws Exception {
        String sqlQueryForSource = "select count(*) "
        		+ "from ("
        		+ "select max(time_stamp) "
        		+ "from IncrementalFacebookPageStatistics "
        		+ "where date = date_sub(curdate() interval 1 day "
        		+ "group by property_id)";
        String sqlQueryForTarget = "select count(*) "
        		+ "from ("
        		+ "select max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day "
        		+ "group by property_id)";

        logger.info("\nStart control checks on table 'FactFacebookPageStats'");
        testLoad(sqlQueryForSource, sqlQueryForTarget);
        
        List<String> factsYesterdayList = new ArrayList<String>();
        factsYesterdayList.add("select sum(followers) "
        		+ "from ("
        		+ "select followers, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 2 day) "
        		+ "group by property_id)");
        factsYesterdayList.add("select sum(engagements) "
        		+ "from ("
        		+ "select engagements, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 2 day) "
        		+ "group by property_id)");
        factsYesterdayList.add("select sum(impressions) "
        		+ "from ("
        		+ "select impressions, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 2 day) "
        		+ "group by property_id)");
        factsYesterdayList.add("select sum(reached) "
        		+ "from ("
        		+ "select reached, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 2 day) "
        		+ "group by property_id)");
        factsYesterdayList.add("select sum(likes) "
        		+ "from ("
        		+ "select likes, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 2 day) "
        		+ "group by property_id)");
        factsYesterdayList.add("select sum(unlikes) "
        		+ "from ("
        		+ "select unlikes, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 2 day) "
        		+ "group by property_id)");
   
        List<String> incrementalsTodayList = new ArrayList<String>();
        incrementalsTodayList.add("select sum(total_followers) "
        		+ "from ("
        		+ "select followers, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        incrementalsTodayList.add("select sum(incremental_engagements) "
        		+ "from ("
        		+ "select engagements, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        incrementalsTodayList.add("select sum(incremental_impressions) "
        		+ "from ("
        		+ "select impressions, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        incrementalsTodayList.add("select sum(incremental_reached) "
        		+ "from ("
        		+ "select reached, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        incrementalsTodayList.add("select sum(incremental_likes) "
        		+ "from ("
        		+ "select likes, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        incrementalsTodayList.add("select sum(incremental_unlikes) "
        		+ "from ("
        		+ "select unlikes, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        
        List<String> factsTodayList = new ArrayList<String>();
        factsTodayList.add("select sum(followers) "
        		+ "from ("
        		+ "select followers, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        factsTodayList.add("select sum(engagements) "
        		+ "from ("
        		+ "select engagements, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        factsTodayList.add("select sum(impressions) "
        		+ "from ("
        		+ "select impressions, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        factsTodayList.add("select sum(reached) "
        		+ "from ("
        		+ "select reached, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        factsTodayList.add("select sum(likes) "
        		+ "from ("
        		+ "select likes, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        factsTodayList.add("select sum(unlikes) "
        		+ "from ("
        		+ "select unlikes, max(time_stamp) "
        		+ "from FactFacebookPageStats "
        		+ "where date = date_sub(curdate() interval 1 day) "
        		+ "group by property_id)");
        
        followUpLoadTestFacebook(factsYesterdayList, incrementalsTodayList, factsTodayList);
    }

}
