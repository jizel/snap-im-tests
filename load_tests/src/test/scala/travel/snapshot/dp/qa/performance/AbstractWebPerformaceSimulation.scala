package travel.snapshot.dp.qa.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils.Granularity
import travel.snapshot.dp.qa.utils.Granularity._

class AbstractWebPerformaceSimulation extends AbstractSimulation {

  object GetAnalytics {

    def apply(suffix: String = "analytics",
              requestMessage: String = "get aggregated web_performance analytics with random granularity in range of 20 days",
              granularity: Granularity = Granularity.getRandom,
              range: Int = 20) =
      request(suffix, requestMessage, granularity, range)

    def request(suffix: String, request: String, granularity: Granularity, range: Int) = exec(http(request)
      .get(session => s"web_performance/$suffix?access_token=$accessToken&granularity=$granularity&${randomUtils.randomSinceUntil(range)}")
      .header("X-Property", session => randomUtils.randomPropertyId)
      .check(status.is(200)))
  }
}
