package travel.snapshot.dp.qa.social.instagram

import io.gatling.core.Predef._

class InstagramSimulation extends AbstractInstagramSimulation {

  runScenario(
    scenario("Instagram analytics").exec(
      getToken(),
      GetOverallInstagram(),
      GetInstagramMetric()))
}
