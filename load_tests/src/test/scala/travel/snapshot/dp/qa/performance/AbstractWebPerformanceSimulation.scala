package travel.snapshot.dp.qa.performance

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.apache.http.HttpStatus
import travel.snapshot.dp.qa.{AbstractIntegrationsSimulation, AbstractSimulation}
import travel.snapshot.dp.qa.utils.{AbstractRequest, Granularity, QueryUtils}
import travel.snapshot.dp.qa.utils.Granularity._
import travel.snapshot.dp.qa.utils.WebPerformanceReferralsSortKey.WebPerformanceReferralsSortKey

abstract class AbstractWebPerformanceSimulation extends AbstractIntegrationsSimulation {

  object GetAnalytics extends AbstractRequest {

    def apply(suffix: String = "analytics",
              sortKey: WebPerformanceReferralsSortKey = null,
              requestMessage: String = "get aggregated web_performance analytics with random granularity in range of 20 days",
              granularity: Granularity = Granularity.getRandom,
              range: Int = 20): ChainBuilder =
      request(suffix, sortKey, requestMessage, granularity, range)

    def request(suffix: String, sortKey: WebPerformanceReferralsSortKey, request: String, granularity: Granularity, range: Int): ChainBuilder = exec(getRequest(
      request,
      Option(new QueryUtils().buildAdditionalQueries(null, sortKey)),
      s"web_performance/$suffix?granularity=$granularity&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))

  }

}
