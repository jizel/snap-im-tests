package travel.snapshot.dp.qa.social.instagram

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils.Granularity
import travel.snapshot.dp.qa.utils.Granularity.Granularity
import travel.snapshot.dp.qa.utils.Metrics.InstagramMetric.InstagramMetric

/**
  * Encapsulates all steps for Instagram simulation
  */
class AbstractInstagramSimulation extends AbstractSimulation {

  object GetOverallInstagram {

    def apply() = request("get Instagram overall daily metrics in range of 20 days in daily granularity", Granularity.DAY, 20)

    def request(request: String, granularity: Granularity, range: Int) = exec(http(request)
      .get(session => s"social_media/analytics/instagram?access_token=$accessToken&granularity=$granularity&${randomUtils.randomSinceUntil(range)}")
      .header("X-Property", session => randomUtils.randomPropertyId)
      .check(status.is(200)))
  }

  object GetInstagramMetric {

    def apply() = request("get random Instagram metric in random range of 100 days in random granularity",
      randomUtils.randomInstagramMetric, randomUtils.randomGranularity, 100)

    def request(request: String, metric: InstagramMetric, granularity: Granularity, range: Int) = exec(http(request)
      .get(session => s"social_media/analytics/instagram/$metric?access_token=$accessToken&granularity=$granularity&${randomUtils.randomSinceUntil(range)}")
      .header("x-property", session => randomUtils.randomPropertyId)
      .check(status.is(200)))
  }

}
