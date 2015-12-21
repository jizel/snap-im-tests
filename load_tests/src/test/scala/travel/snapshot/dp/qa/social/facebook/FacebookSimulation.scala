package travel.snapshot.dp.qa.social.facebook

import io.gatling.core.Predef._

class FacebookSimulation extends AbstractFacebookSimulation {

  runScenario(
    scenario("Facebook analytics").exec(
      GetOverallFacebook(),
      GetFacebookMetric(),
      GetFacebookPosts()))
}
