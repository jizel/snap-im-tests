package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

/*
 * Automated Tests for Twitter Noon ETLs.
 * 
 * Please consult the page in Confluence to get a basic overview of the Twitter data collection. All
 * values are total
 * 
 * This is the path of the data for noon ETLs: Twitter (2x/day)-> Raw data tables (1x/day)-> Fact
 * table for noon runs
 * 
 * Noon ETLs occur at 12:01 local time for hotels in time zones [-6;+4].
 * 
 * The tests validate ETLs for the previous day and can be executed at any time
 */

public class TestTwitterPagesNoonETL {

  public static final Logger logger = LoggerFactory.getLogger(TestTwitterPagesNoonETL.class);

  @Test
  public void testFactLoad() throws Exception {
    // The noon ETL for each hotel is triggered after the first run to get data from the Twitter API
    // has passed
    Integer outcomeSource = getQueryResultInt(templateToTarget,
        "select count(*) from RawImportedTwitterPageStatistics where date=curdate() - interval 1 day and data_collection_run = 1");
    Integer outcomeTarget = getQueryResultInt(templateToTarget,
        "select count(*) from T_FactTwitterPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0");
    logger.info("\nStart control checks on table 'T_FactTwitterPageStatsCurrDay'");
    logger.info("Total counts: ");
    logger.info("Source: " + outcomeSource);
    logger.info("Target: " + outcomeTarget);
    assertEquals(outcomeSource, outcomeTarget);

    List<Integer> followUpListToSource = new ArrayList<Integer>();
    followUpListToSource.add( // account impressions
        getQueryResultInt(templateToTarget,
            "select coalesce(sum(followers), 0) from RawImportedTwitterRetweets where date = curdate() - interval 1 day and data_collection_run = 1")
            + getQueryResultInt(templateToTarget,
                "select coalesce(sum(total_followers), 0) from RawImportedTwitterPageStatistics where date = curdate() - interval 1 day and data_collection_run = 1")
            + getQueryResultInt(templateToTarget,
                "select count(*) from RawImportedTwitterReplies where date = curdate() - interval 1 day and data_collection_run = 1"));
    followUpListToSource.add( // account engagement
        getQueryResultInt(templateToTarget,
            "select coalesce(sum(favorites), 0) + coalesce(sum(retweet_count), 0) from RawImportedTwitterTweets where date = curdate() - interval 1 day and data_collection_run = 1")
            + getQueryResultInt(templateToTarget,
                "select count(*) from RawImportedTwitterReplies where date = curdate() - interval 1 day and data_collection_run = 1"));
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(total_followers), 0) from RawImportedTwitterPageStatistics where date = curdate() - interval 1 day and data_collection_run = 1")); // followers
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(total_tweets), 0) from RawImportedTwitterPageStatistics where date = curdate() - interval 1 day and data_collection_run = 1")); // tweets
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(retweet_count), 0) from RawImportedTwitterTweets where date = curdate() - interval 1 day and data_collection_run = 1")); // retweets
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(followers), 0) from RawImportedTwitterRetweets where date = curdate() - interval 1 day and data_collection_run = 1")); // retweet
                                                                                                                                                    // reach
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select count(*) from RawImportedTwitterMentions where date = curdate() - interval 1 day and data_collection_run = 1")); // mentions
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(followers), 0) from RawImportedTwitterMentions where date = curdate() - interval 1 day and data_collection_run = 1")); // mention
                                                                                                                                                    // reach
    // reach
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(reach), 0) from RawImportedTwitterAccountReach where date > curdate() - interval 8 day and date < curdate()"));

    List<Integer> followUpListToTarget = new ArrayList<Integer>();
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(impressions), 0) from T_FactTwitterPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(engagement), 0) from T_FactTwitterPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(followers), 0) from T_FactTwitterPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(number_of_tweets), 0) from T_FactTwitterPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(retweets), 0) from T_FactTwitterPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(retweet_reach), 0) from T_FactTwitterPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(mentions), 0) from T_FactTwitterPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(mention_reach), 0) from T_FactTwitterPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(reach), 0) from T_FactTwitterPageStatsCurrDay where dim_date_id = (curdate() - interval 1 day) + 0"));

    List<String> metrics = new ArrayList<String>();
    metrics.add("impressions");
    metrics.add("engagement");
    metrics.add("followers");
    metrics.add("tweets");
    metrics.add("retweets");
    metrics.add("retweets reach");
    metrics.add("mentions");
    metrics.add("mentions reach");
    metrics.add("reach");

    verifyLoad(followUpListToSource, followUpListToTarget, metrics);
  }
}
