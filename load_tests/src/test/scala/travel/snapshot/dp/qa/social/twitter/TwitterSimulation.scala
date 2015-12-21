package travel.snapshot.dp.qa.social.twitter

import io.gatling.core.Predef._

class TwitterSimulation extends AbstractTwitterSimulation {

  runScenario(
    scenario("Twitter analytics").exec(
      GetOverallTwitter(),
      GetTwitterMetric(),
      GetTwitterTweets()))
}
