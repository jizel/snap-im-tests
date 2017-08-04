package travel.snapshot.dp.qa.social.common

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.apache.http.HttpStatus
import travel.snapshot.dp.qa.{AbstractIntegrationsSimulation, AbstractSimulation}
import travel.snapshot.dp.qa.utils.AbstractRequest
import travel.snapshot.dp.qa.utils.Granularity._

/**
  * Encapsulates all steps for common analytics simulation
  */
abstract class AbstractAnalyticsSimulation extends AbstractIntegrationsSimulation {

  object GetAllAnalytics extends AbstractRequest{

    def apply(): ChainBuilder = request("get aggregated analytics for given property in random granularity in range of random 100 days",
      randomUtils.randomGranularity, 100)

    def request(request: String, granularity: Granularity, range: Int): ChainBuilder = exec(getRequest(
      request,
      None,
      s"social_media/analytics?granularity=$granularity&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

  object GetAllAnalyticsReach extends AbstractRequest{

    def apply(): ChainBuilder = request("Get aggregated analytics reach for given property in random granularity in range of random 100 days",
      randomUtils.randomGranularity, 100)

    def request(request: String, granularity: Granularity, range: Int): ChainBuilder = exec(getRequest(
      request,
      None,
      s"social_media/analytics/reach?granularity=$granularity&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

}
