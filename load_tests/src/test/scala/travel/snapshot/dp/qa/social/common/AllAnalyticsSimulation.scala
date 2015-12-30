package travel.snapshot.dp.qa.social.common

import io.gatling.core.Predef._

class AllAnalyticsSimulation extends AbstractAnalyticsSimulation {

  runScenario(
    scenario("Aggregated social analytics").exec(
      GetAllAnalytics()
    ))
}
