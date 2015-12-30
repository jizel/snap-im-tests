package travel.snapshot.dp.qa.rateshopper

import io.gatling.core.Predef._

class RateShopperSimulation extends AbstractRateShopperSimulation {

  runScenario(
    scenario("RateShopper analytics").exec(
      GetLARForProperty(),
      GetAnalyticsMarket(),
      GetAnalyticsMarketProperties()
    ))
}
