package travel.snapshot.dp.qa.social.twitter

import io.gatling.core.Predef._

class TwitterSimulation extends AbstractTwitterSimulation {

  runScenario(
    scenario("Twitter analytics").exec(
      //GetOverallTwitter(), not implememted as of 30/12/2015
      GetTwitterMetric()))
}
