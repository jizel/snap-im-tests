package travel.snapshot.dp.qa.social_media;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static travel.snapshot.dp.qa.base.TestUtils.*;

import java.util.ArrayList;
import java.util.List;

/*
 * Automated Tests for Twitter Midnight ETLs.
 * 
 * Please consult the page in Confluence to get a basic overview of the Twitter data collection. All
 * values are total
 * 
 * This is the path of the data for midnight ETLs: Twitter (2x/day)-> Raw data tables (1x/day)->
 * Fact table
 * 
 * Midnight ETLs occur at 00:01 local time for hotels in time zones [-6;+4].
 * 
 * The tests validate ETLs for the previous day and can be executed at any time
 */

public class TestTwitterPagesMidnightETL {

  public static final Logger logger = LoggerFactory.getLogger(TestTwitterPagesMidnightETL.class);

  @Test
  public void testFactLoad() throws Exception {
    // The midnight ETL for each hotel is triggered after the second run to get data from the
    // Twitter API has passed

    logger.info("\nStart control checks on table 'FactTwitterPageStats'");
    logger.info("Total counts: ");
    Integer outcomeSource = getQueryResultInt(templateToTarget,
        "select count(*) from RawImportedTwitterPageStatistics where date = curdate() - interval 2 day and data_collection_run = 2");
    Integer outcomeTarget = getQueryResultInt(templateToTarget,
        "select count(*) from FactTwitterPageStats where dim_date_id = curdate() - interval 2 day");
    logger.info("Source: " + outcomeSource);
    logger.info("Target: " + outcomeTarget);
    assertEquals(outcomeSource, outcomeTarget);

    List<Integer> followUpListToSource = new ArrayList<Integer>();
    followUpListToSource.add( // account impressions
        getQueryResultInt(templateToTarget,
            "select coalesce(sum(followers), 0) from RawImportedTwitterRetweets where date = curdate() - interval 2 day and data_collection_run = 2")
            + getQueryResultInt(templateToTarget,
                "select coalesce(sum(total_followers), 0) from RawImportedTwitterPageStatistics where date = curdate() - interval 2 day and data_collection_run = 2")
            + getQueryResultInt(templateToTarget,
                "select count(*) from RawImportedTwitterReplies where date = curdate() - interval 2 day and data_collection_run = 2"));
    followUpListToSource.add( // account engagement
        getQueryResultInt(templateToTarget,
            "select coalesce(sum(favorites), 0) + coalesce(sum(retweet_count), 0) from RawImportedTwitterTweets where date = curdate() - interval 2 day and data_collection_run = 2")
            + getQueryResultInt(templateToTarget,
                "select count(*) from RawImportedTwitterReplies where date = curdate() - interval 2 day and data_collection_run = 2"));
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(total_followers), 0) from RawImportedTwitterPageStatistics where date = curdate() - interval 2 day and data_collection_run = 2")); // followers
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(total_tweets), 0) from RawImportedTwitterPageStatistics where date = curdate() - interval 2 day and data_collection_run = 2")); // tweets
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(retweet_count), 0) from RawImportedTwitterTweets where date = curdate() - interval 2 day and data_collection_run = 2")); // retweets
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(followers), 0) from RawImportedTwitterRetweets where date = curdate() - interval 2 day and data_collection_run = 2")); // retweet
                                                                                                                                                    // reach
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select count(*) from RawImportedTwitterMentions where date = curdate() - interval 2 day and data_collection_run = 2")); // mentions
    followUpListToSource.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(followers), 0) from RawImportedTwitterMentions where date = curdate() - interval 2 day and data_collection_run = 2")); // mention
                                                                                                                                                    // reach
    followUpListToSource.add(// reach
        getQueryResultInt(templateToTarget,
            "select coalesce(sum(reach), 0) from RawImportedTwitterAccountReach where date > curdate() - interval 9 day and date < curdate() - interval 1 day"));

    List<Integer> followUpListToTarget = new ArrayList<Integer>();
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(impressions), 0) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(engagement), 0) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(followers), 0) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(number_of_tweets), 0) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(retweets), 0) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(retweet_reach), 0) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(mentions), 0) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(mention_reach), 0) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
    followUpListToTarget.add(getQueryResultInt(templateToTarget,
        "select coalesce(sum(reach), 0) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));

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
