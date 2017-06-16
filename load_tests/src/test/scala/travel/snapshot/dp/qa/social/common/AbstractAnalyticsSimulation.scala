package travel.snapshot.dp.qa.social.common

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils.Granularity._

/**
  * Encapsulates all steps for common analytics simulation
  */
abstract class AbstractAnalyticsSimulation extends AbstractSimulation {

  object GetAllAnalytics {

    def apply() = request("get aggregated analytics for random property in random granularity in range of random 100 days",
      randomUtils.randomGranularity, 100)

    def request(request: String, granularity: Granularity, range: Int) = exec(http(request)
      .get(session => s"social_media/analytics?access_token=$accessTokenParam&granularity=$granularity&${randomUtils.randomSinceUntil(range)}")
      .header("X-Property", session => randomUtils.randomPropertyId)
      .check(status.is(200)))
  }

}
