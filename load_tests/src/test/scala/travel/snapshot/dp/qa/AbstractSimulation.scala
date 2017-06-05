package travel.snapshot.dp.qa

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.utils.rateshopper.RateShopperPropertyCodeParser
import travel.snapshot.dp.qa.utils.{BaseUrlResolver, RandomUtils}

/**
  * Basic class with reusable code and configuration for gatling simulations.
  * The request path is formatted as follows: {host}/{moduleName}/api/{basePath}
  */
abstract class AbstractSimulation extends Simulation {

  val protocol = System.getProperty("protocol", "http")
  val host = System.getProperty("host", "localhost")
  val port = Integer.getInteger("port", 8080)

  val startUsers = Integer.getInteger("startUsers", 10)
  val endUsers = Integer.getInteger("endUsers", 30)
  val rampTime = Integer.getInteger("ramp", 60)

  // oauth related
  val oauthClientId = System.getProperty("oauthClientId", "TestClient")
  val oauthClientSecret = System.getProperty("oauthClientSecret")

  // access_token query property for social modules
  val accessToken = System.getProperty("access_token")

  val httpConf = http
    .baseURL(BaseUrlResolver())
    .contentTypeHeader("application/json")
    .acceptHeader("application/json")
    .userAgentHeader("Gatling / API Load Test")
    .doNotTrackHeader("1")

  val randomUtils = new RandomUtils

  val rateShopperPropertyCodes = new RateShopperPropertyCodeParser().parse()

  val request_headers = Map(
    "Accept-Encoding" -> "gzip, deflate, br",
    "Authorization" -> "Bearer",
    "Content-Type" -> "application/json;charset=UTF-8",
    "X-Auth-AppId" -> "11111111-0000-4000-a000-444444444444",
    "X-Auth-UserId" -> "11111111-0000-4000-a000-000000000000")

  /** Executes the scenario defined in descendant */
  protected def runScenario(scn: ScenarioBuilder): Unit = {
    setUp(scn.inject(rampUsersPerSec(startUsers.toDouble) to endUsers.toDouble during (rampTime seconds))).protocols(httpConf)
  }

}
