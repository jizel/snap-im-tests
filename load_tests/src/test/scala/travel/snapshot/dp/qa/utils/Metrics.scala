package travel.snapshot.dp.qa.utils

import java.util.concurrent.ThreadLocalRandom

object Metrics {

  object TwitterMetric extends Enumeration {
    type TwitterMetric = Value
    val NUMBER_OF_TWEETS = Value("number_of_tweets")
    val ENGAGEMENT = Value("engagement")
    val FOLLOWERS = Value("followers")
    val REACH = Value("reach")
    val RETWEETS = Value("retweets")
    val RETWEET_REACH = Value("retweet_reach")
    val MENTIONS = Value("mentions")
    val MENTION_REACH = Value("mention_reach")

    val seq = values.toSeq
    val bound = seq.size - 1

    def getRandom: Value = seq(ThreadLocalRandom.current().nextInt(bound))
  }

  object FacebookMetric extends Enumeration {
    type FacebookMetric = Value
    val NUMBER_OF_POSTS = Value("number_of_posts")
    val ENGAGEMENT = Value("engagement")
    val LIKES = Value("likes")
    val UNLIKES = Value("unlikes")
    val REACH = Value("reach")
    val FOLLOWERS = Value("followers")

    val seq = values.toSeq
    val bound = seq.size - 1

    def getRandom: Value = seq(ThreadLocalRandom.current().nextInt(bound))
  }

  object InstagramMetric extends Enumeration {
    type InstagramMetric = Value
    val PICTURES = Value("pictures")
    val ENGAGEMENT = Value("engagement")
    val FOLLOWERS = Value("followers")
    val TAG_POSTS = Value("tag_posts")
    val REACH = Value("reach")
    val LIKES = Value("likes")
    val COMMENTS = Value("comments")

    val seq = values.toSeq
    val bound = seq.size - 1

    def getRandom: Value = seq(ThreadLocalRandom.current().nextInt(bound))
  }

}
