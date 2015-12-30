package travel.snapshot.dp.qa.performance

import io.gatling.core.Predef._
import travel.snapshot.dp.qa.utils.WebPerformanceReferralsSortKey

class WebPerformanceSimulation extends AbstractWebPerformaceSimulation {

  runScenario(
    scenario("WebPerformance analytics").exec(
      GetAnalytics(),
      GetAnalytics("analytics/visits"),
      GetAnalytics("analytics/visits/countries"),
      GetAnalytics("analytics/visits_unique"),
      GetAnalytics("analytics/visits_unique/countries"),
      GetAnalytics("analytics/revenue"),
      GetAnalytics("analytics/conversion_rates"),
      GetAnalytics("analytics/conversion_rates/countries"),
      GetAnalytics("analytics/referrals", WebPerformanceReferralsSortKey.getRandom)
    ))
}
