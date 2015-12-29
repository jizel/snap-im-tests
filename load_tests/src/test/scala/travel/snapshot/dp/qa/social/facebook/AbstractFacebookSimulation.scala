package travel.snapshot.dp.qa.social.facebook

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils.Granularity.Granularity
import travel.snapshot.dp.qa.utils.Metrics.FacebookMetric.FacebookMetric

/**
  * Encapsulates all steps for Facebook analytics simulation
  */
abstract class AbstractFacebookSimulation extends AbstractSimulation {

  object GetOverallFacebook {

    def apply() = request("get Facebook overall daily metrics for random property in range of random 100 days in random granularity",
      randomUtils.randomGranularity, 100)

    def request(request: String, granularity: Granularity, range: Int) = exec(http(request)
      .get(session => s"social_media/analytics/facebook?access_token=$accessToken&granularity=$granularity&${randomUtils.randomSinceUntil(range)}")
      .header("X-Property", session => randomUtils.randomPropertyId)
      .check(status.is(200)))
  }

  object GetFacebookPosts {

    def apply() = request("get random number of Facebook posts of random property starting from random cursor", randomUtils.randomInt(200), randomUtils.randomInt(50))

    def request(request: String, limit: Int, cursor: Int) = exec(http(request)
      .get(session => s"social_media/analytics/facebook/posts?access_token=$accessToken&limit=$limit&cursor=$cursor")
      .header("X-Property", session => randomUtils.randomPropertyId)
      .check(status.is(200)))
  }

  object GetFacebookMetric {

    def apply() = request("get random Facebook metric of random property in random range of 100 days in random granularity",
      randomUtils.randomFacebookMetric, randomUtils.randomGranularity, 100)

    def request(request: String, metric: FacebookMetric, granularity: Granularity, range: Int) = exec(http(request)
      .get(session => s"social_media/analytics/facebook/$metric?access_token=$accessToken&granularity=$granularity&${randomUtils.randomSinceUntil(range)}")
      .header("X-Property", session => randomUtils.randomPropertyId)
      .check(status.is(200)))
  }

}
