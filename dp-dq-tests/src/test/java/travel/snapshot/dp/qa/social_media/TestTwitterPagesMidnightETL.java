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
 * Please consult the page in Confluence to get a basic overview of the Twitter data collection.
 * All values are total
 * 
 * This is the path of the data for midnight ETLs:
 * Twitter (2x/day)-> Raw data tables (1x/day)-> Fact table
 * 
 * Midnight ETLs occur at 00:01 local time for hotels in time zones [-6;+4].
 * 
 * The tests validate ETLs for the previous day and can be executed at any time
*/

public class TestTwitterPagesMidnightETL {

	public static final Logger logger = LoggerFactory.getLogger(TestTwitterPagesMidnightETL.class);

    @Test
    public void testFactLoad() throws Exception {
    	//The midnight ETL for each hotel is triggered after the second run to get data from the Twitter API has passed

        logger.info("\nStart control checks on table 'FactTwitterPageStats'");
        logger.info("Total counts: ");
        logger.info("Source: " + getQueryResultInt("select count(*) from RawImportedTwitterPageStatistics where date = curdate() - interval 2 day and data_collection_run = 2"));
        logger.info("Target: " + getQueryResultInt("select count(*) from FactTwitterPageStats where dim_date_id = curdate() - interval 2 day"));
        assertEquals(
        		getQueryResultInt("select count(*) from RawImportedTwitterPageStatistics where date = curdate() - interval 2 day and data_collection_run = 2"),
        		getQueryResultInt("select count(*) from FactTwitterPageStats where dim_date_id = curdate() - interval 2 day"));
        
        List<Integer> followUpListToSource = new ArrayList<Integer>();
        followUpListToSource.add( // account impressions
        		getQueryResultInt("select sum(followers) from RawImportedTwitterRetweets where date = curdate() - interval 2 day and data_collection_run = 2") +
        		getQueryResultInt("select sum(total_followers) from RawImportedTwitterPageStatistics where date = curdate() - interval 2 day and data_collection_run = 2") +
        		getQueryResultInt("select count(*) from RawImportedTwitterReplies where date = curdate() - interval 2 day and data_collection_run = 2"));
        followUpListToSource.add( // account engagement
        		getQueryResultInt("select sum(favorites) + sum(retweet_count) from RawImportedTwitterTweets where date = curdate() - interval 2 day and data_collection_run = 2") +
        		getQueryResultInt("select count(*) from RawImportedTwitterReplies where date = curdate() - interval 2 day and data_collection_run = 2"));
        followUpListToSource.add(getQueryResultInt("select sum(total_followers) from RawImportedTwitterPageStatistics where date = curdate() - interval 2 day and data_collection_run = 2")); // followers
        followUpListToSource.add(getQueryResultInt("select sum(total_tweets) from RawImportedTwitterPageStatistics where date = curdate() - interval 2 day and data_collection_run = 2")); // tweets
        followUpListToSource.add(getQueryResultInt("select sum(retweet_count) from RawImportedTwitterTweets where date = curdate() - interval 2 day and data_collection_run = 2")); // retweets
        followUpListToSource.add(getQueryResultInt("select sum(followers) from RawImportedTwitterRetweets where date = curdate() - interval 2 day and data_collection_run = 2")); // retweet reach
        followUpListToSource.add(getQueryResultInt("select count(*) from RawImportedTwitterMentions where date = curdate() - interval 2 day and data_collection_run = 2")); // mentions
        followUpListToSource.add(getQueryResultInt("select sum(followers) from RawImportedTwitterMentions where date = curdate() - interval 2 day and data_collection_run = 2")); // mention reach
        followUpListToSource.add(getQueryResultInt( //reach
        		"select sum(reach) from RawImportedTwitterAccountReach "
        		+ "where date > curdate() - interval 9 day and date < curdate() - interval 1 day"));

        List<Integer> followUpListToTarget = new ArrayList<Integer>();
        followUpListToTarget.add(getQueryResultInt("select sum(impressions) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
        followUpListToTarget.add(getQueryResultInt("select sum(engagement) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
        followUpListToTarget.add(getQueryResultInt("select sum(followers) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
        followUpListToTarget.add(getQueryResultInt("select sum(number_of_tweets) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
        followUpListToTarget.add(getQueryResultInt("select sum(retweets) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
        followUpListToTarget.add(getQueryResultInt("select sum(retweet_reach) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
        followUpListToTarget.add(getQueryResultInt("select sum(mentions) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
        followUpListToTarget.add(getQueryResultInt("select sum(mention_reach) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
        followUpListToTarget.add(getQueryResultInt("select sum(reach) from FactTwitterPageStats where dim_date_id = (curdate() - interval 2 day) + 0"));
        
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
        
        testLoadTwitter(followUpListToSource, followUpListToTarget, metrics);
    }

}
