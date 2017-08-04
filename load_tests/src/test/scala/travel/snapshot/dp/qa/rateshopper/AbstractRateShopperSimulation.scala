package travel.snapshot.dp.qa.rateshopper

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.apache.http.HttpStatus
import travel.snapshot.dp.qa.{AbstractIntegrationsSimulation, AbstractSimulation}
import travel.snapshot.dp.qa.utils.{AbstractRequest, QueryUtils}

abstract class AbstractRateShopperSimulation extends AbstractIntegrationsSimulation {

  object GetLARForProperty extends AbstractRequest {

    def apply(range: Integer = 20): ChainBuilder = request(range)

    def request(range: Integer): ChainBuilder = exec(getRequest(
      s"Return LAR (lowest available rate) for random property in random range of $range days",
      None,
      s"rate_shopper/analytics/property/$integrationsPropertyId?${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ))
  }

  object GetAnalyticsMarket extends AbstractRequest {

    def apply(range: Integer = 20): ChainBuilder = request(range)

    def request(range: Integer): ChainBuilder = exec(getRequest(
      s"Return BAR values for the given market - marker high, low and average in random range of $range days",
      None,
      s"rate_shopper/analytics/market?property_id=$integrationsPropertyId&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ))
  }

  object GetAnalyticsMarketProperties extends AbstractRequest {

    def apply(range: Integer = 20, limit: Integer = 50, cursor: Integer = 10): ChainBuilder = request(range, limit, cursor)

    def request(range: Integer, limit: Integer, cursor: Integer): ChainBuilder = exec(getRequest(
      s"Returns list of properties of the given market with random limit up to $limit and random cursor up to $cursor",
      Option(new QueryUtils().buildAdditionalQueries(null, null, randomUtils.randomInt(cursor), randomUtils.randomInt(limit))),
      s"rate_shopper/analytics/market/properties?property_id=$integrationsPropertyId&${randomUtils.randomSinceUntil(range)}",
      HttpStatus.SC_OK
    ))
  }

}
