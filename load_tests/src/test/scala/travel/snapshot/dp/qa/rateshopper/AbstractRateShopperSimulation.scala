package travel.snapshot.dp.qa.rateshopper

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils.QueryUtils

abstract class AbstractRateShopperSimulation extends AbstractSimulation {

  object GetLARForProperty {

    def apply(range: Integer = 20) = request(range)

    def request(range: Integer) = exec(http("Return LAR (lowest available rate) for random property in random range of 20 days")
      .get(session => s"rate_shopper/analytics/property/${rateShopperPropertyCodes.getRandomPropertyCode()}?access_token=$accessToken&${randomUtils.randomSinceUntil(range)}")
      .check(status.is(200)))
  }

  object GetAnalyticsMarket {

    def apply(range: Integer = 20) = request(range)

    def request(range: Integer) = exec(http("Return BAR values for the given market - marker high, low and average in random range of 20 days")
      .get(session => s"rate_shopper/analytics/market?access_token=$accessToken&property_id=${rateShopperPropertyCodes.getRandomPropertyCode()}&${randomUtils.randomSinceUntil(range)}")
      .check(status.is(200)))
  }

  object GetAnalyticsMarketProperties {

    def apply(range: Integer = 20, limit: Integer = 50, cursor: Integer = 0) = request(range, limit, cursor)

    def request(range: Integer, limit: Integer = 50, cursor: Integer = 0) =
      exec(http("Returns list of properties of the given market with random limit up to 50 and random cursor up to 10")
        .get(session => {

          val additionalQueries = new QueryUtils().buildAdditionalQueries(null, null, randomUtils.randomInt(10), randomUtils.randomInt(50))

          s"rate_shopper/analytics/market/properties?access_token=$accessToken&property_id=${rateShopperPropertyCodes.getRandomPropertyCode()}$additionalQueries"
        })
        .check(status.is(200)))
  }

}
