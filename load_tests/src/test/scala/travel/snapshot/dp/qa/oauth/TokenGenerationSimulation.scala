package travel.snapshot.dp.qa.oauth

import io.gatling.core.Predef._

class TokenGenerationSimulation extends AbstractOauthSimulation {

  runScenario(scenario("create oauth token").exec(CreateOauthTokenWithoutUserContext()))
}
