package travel.snapshot.dp.qa.social.instagram

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.apache.http.HttpStatus
import travel.snapshot.dp.qa.{AbstractIntegrationsSimulation, AbstractSimulation}
import travel.snapshot.dp.qa.utils.{AbstractRequest, Granularity}
import travel.snapshot.dp.qa.utils.Granularity.Granularity
import travel.snapshot.dp.qa.utils.Metrics.InstagramMetric.InstagramMetric

/**
  * Encapsulates all steps for Instagram simulation
  */
abstract class AbstractInstagramSimulation extends AbstractIntegrationsSimulation {

  object GetOverallInstagram extends AbstractRequest {

    def apply(): ChainBuilder = request("get Instagram overall daily metrics in range of 20 days in daily granularity", Granularity.DAY, 20)

    def request(request: String, granularity: Granularity, range: Int): ChainBuilder = exec(getRequest(
      request,
      None,
      s"social_media/analytics/instagram?granularity=$granularity&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

  object GetInstagramMetric extends AbstractRequest {
//    TODO: Make parametrized apply and give all metrics one by one in InstagramSimulation. Do this for all soc. media that use any metrics.
    def apply(): ChainBuilder = request("get random Instagram metric in random range of 100 days in random granularity",
      randomUtils.randomInstagramMetric, randomUtils.randomGranularity, 100)

    def request(request: String, metric: InstagramMetric, granularity: Granularity, range: Int): ChainBuilder = exec(getRequest(
      request,
      None,
      s"social_media/analytics/instagram/$metric?granularity=$granularity&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

}
