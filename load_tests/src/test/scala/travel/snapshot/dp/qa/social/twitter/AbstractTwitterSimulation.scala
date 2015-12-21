package travel.snapshot.dp.qa.social.twitter

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.social.AbstractSocialMediaSimulation
import travel.snapshot.dp.qa.utils.Granularity.Granularity
import travel.snapshot.dp.qa.utils.Metrics.TwitterMetric.TwitterMetric

/**
  * Encapsulates all steps for Twitter simulation
  */
abstract class AbstractTwitterSimulation extends AbstractSocialMediaSimulation {

  object GetOverallTwitter {

    def apply() = request("get Twitter overall daily analytics of random property in range of 100 days in random granularity",
      randomUtils.randomGranularity, 100)

    def request(request: String, granularity: Granularity, range: Int) =
      exec(http(request)
        .get(session => s"social_media/analytics/twitter?access_token=$accessToken&granularity=$granularity&${randomUtils.randomSinceUntil(range)}")
        .header("X-Property", session => randomUtils.randomPropertyId)
        .check(status.is(200)))
  }

  object GetTwitterMetric {

    def apply() = request("get random Twitter metric of random property in random range of 100 days in random granularity",
      randomUtils.randomTwitterMetric, randomUtils.randomGranularity, 100)

    def request(request: String, metric: TwitterMetric, granularity: Granularity, range: Int) = exec(http(request)
      .get(session => s"social_media/analytics/twitter/$metric?access_token=$accessToken&granularity=$granularity&${randomUtils.randomSinceUntil(range)}")
      .header("X-Property", session => randomUtils.randomPropertyId)
      .check(status.is(200)))
  }

  object GetTwitterTweets {
    def apply() = request("get random number of tweets of random property starting from random cursor",
      randomUtils.randomInt(200), randomUtils.randomInt(50))

    def request(request: String, limit: Int, cursor: Int) = exec(http(request)
      .get(session => s"social_media/analytics/twitter/tweets?access_token=$accessToken&limit=$limit&cursor=$cursor")
      .header("X-Property", session => randomUtils.randomPropertyId)
      .check(status.is(200)))
  }

}