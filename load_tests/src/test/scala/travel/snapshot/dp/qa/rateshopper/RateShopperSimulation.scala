package travel.snapshot.dp.qa.rateshopper

import io.gatling.core.Predef._

class RateShopperSimulation extends AbstractRateShopperSimulation {

  runScenario(
    scenario("RateShopper analytics").exec(
      getToken(),
      GetLARForProperty(),
      GetAnalyticsMarket(),
      GetAnalyticsMarketProperties()
    ))
}
