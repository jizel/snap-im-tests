package travel.snapshot.dp.qa.social.twitter

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import org.apache.http.HttpStatus
import travel.snapshot.dp.qa.{AbstractIntegrationsSimulation, AbstractSimulation}
import travel.snapshot.dp.qa.utils.Granularity.Granularity
import travel.snapshot.dp.qa.utils.Metrics.TwitterMetric.TwitterMetric
import travel.snapshot.dp.qa.utils.{AbstractRequest, QueryUtils}

/**
  * Encapsulates all steps for Twitter simulation
  */
abstract class AbstractTwitterSimulation extends AbstractIntegrationsSimulation {

  object GetOverallTwitter extends AbstractRequest {

    def apply(): ChainBuilder = request("get Twitter overall daily analytics of random property in range of 100 days in random granularity",
      randomUtils.randomGranularity, 100)

    def request(request: String, granularity: Granularity, range: Int): ChainBuilder = exec(getRequest(
      request,
      None,
      s"social_media/analytics/twitter?granularity=$granularity&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

  object GetTwitterMetric extends AbstractRequest {

    def apply(): ChainBuilder = request("get random Twitter metric of random property in random range of 100 days in random granularity",
      randomUtils.randomTwitterMetric, randomUtils.randomGranularity, 100)

    def request(request: String, metric: TwitterMetric, granularity: Granularity, range: Int): ChainBuilder = exec(getRequest(
      request,
      None,
      s"social_media/analytics/twitter/$metric?granularity=$granularity&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

  object GetTwitterTweets extends AbstractRequest {
    def apply(): ChainBuilder = request("get random number of tweets of random property starting from random cursor",
      randomUtils.randomInt(200), randomUtils.randomInt(50))

    def request(request: String, limit: Int, cursor: Int): ChainBuilder = exec(getRequest(
      request,
      Option(new QueryUtils().buildAdditionalQueries(null, null, cursor, limit)),
      s"social_media/analytics/twitter/tweets?",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

}