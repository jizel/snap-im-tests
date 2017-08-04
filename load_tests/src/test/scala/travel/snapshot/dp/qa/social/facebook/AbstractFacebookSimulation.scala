package travel.snapshot.dp.qa.social.facebook

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import org.apache.http.HttpStatus
import travel.snapshot.dp.qa.{AbstractIntegrationsSimulation, AbstractSimulation}
import travel.snapshot.dp.qa.utils.Granularity.Granularity
import travel.snapshot.dp.qa.utils.Metrics.FacebookMetric.FacebookMetric
import travel.snapshot.dp.qa.utils.{AbstractRequest, QueryUtils}

/**
  * Encapsulates all steps for Facebook analytics simulation
  */
abstract class AbstractFacebookSimulation extends AbstractIntegrationsSimulation {

  object GetOverallFacebook extends AbstractRequest{

    def apply(): ChainBuilder = request("get Facebook overall daily metrics for random property in range of random 100 days in random granularity",
      randomUtils.randomGranularity, 100)

    def request(request: String, granularity: Granularity, range: Int): ChainBuilder = exec(getRequest(
      request,
      None,
      s"social_media/analytics/facebook?granularity=$granularity&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

  object GetFacebookPosts extends AbstractRequest{

    def apply(): ChainBuilder = request("get random number of Facebook posts of random property starting from random cursor", randomUtils.randomInt(200), randomUtils.randomInt(50))

    def request(request: String, limit: Int, cursor: Int): ChainBuilder = exec(getRequest(
      request,
      Option(new QueryUtils().buildAdditionalQueries(null, null, cursor, limit)),
      s"social_media/analytics/facebook/posts?",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

  object GetFacebookMetric extends AbstractRequest{

    def apply(): ChainBuilder = request("get random Facebook metric of random property in random range of 100 days in random granularity",
      randomUtils.randomFacebookMetric, randomUtils.randomGranularity, 100)

    def request(request: String, metric: FacebookMetric, granularity: Granularity, range: Int): ChainBuilder = exec(getRequest(
      request,
      None,
      s"social_media/analytics/facebook/$metric?granularity=$granularity&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

}
