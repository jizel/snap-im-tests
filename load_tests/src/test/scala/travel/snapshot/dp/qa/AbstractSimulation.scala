package travel.snapshot.dp.qa

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.utils.rateshopper.RateShopperPropertyCodeParser
import travel.snapshot.dp.qa.utils.{BaseUrlResolver, RandomUtils, StagingUrlResolver}
import travel.snapshot.dp.qa.utils.{BaseUrlResolver, RandomUtils}
import travel.snapshot.dp.qa.utils._

/**
  * Basic class with reusable code and configuration for gatling simulations.
  * The request path is formatted as follows: {host}/{moduleName}/api/{basePath}
  */
abstract class AbstractSimulation extends Simulation {

  val defaultClientId = "bfca82e6-984e-4b60-a400-1df0b49c70b3"
  val defaultClientSecret = "3aa4d35c-fecf-48ba-a861-912949cf3336"

  val protocol = System.getProperty("protocol", "http")
  val host = System.getProperty("host", "localhost")
  val port = Integer.getInteger("port", 8080)

  val startUsers = Integer.getInteger("startUsers", 10)
  val endUsers = Integer.getInteger("endUsers", 30)
  val rampTime = Integer.getInteger("ramp", 60)

  // Default clientId and clientSecret values (staging) - can be overridden by command line params
  val clientId = System.getProperty("clientId", defaultClientId)
  val clientSecret = System.getProperty("clientSecret", defaultClientSecret)

  // access_token query property for social modules
  val accessTokenParam = System.getProperty("access_token", "default_access_token")

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
    "Content-Type" -> "application/json;charset=UTF-8",
    "X-Auth-AppId" -> "11111111-0000-4000-a000-444444444444",
    "X-Auth-UserId" -> "11111111-0000-4000-a000-000000000000"
  )

  /** Executes the scenario defined in descendant */
  protected def runScenario(scn: ScenarioBuilder): Unit = {
    setUp(scn.inject(rampUsersPerSec(startUsers.toDouble) to endUsers.toDouble during (rampTime seconds))).protocols(httpConf)
//    Alternative (simpler) ramping. Both approaches can be used
//    setUp(scn.inject(rampUsers(startUsers) over rampTime)).protocols(httpConf)
  }


  object getTokenByUser {
    def apply(userName: String, userPassword: String) = exec(http("Get Keycloak token")
      .post(OAuthUrlResolver())
      .asFormUrlEncoded
      .body(StringBody(_ => s"""grant_type=password&client_id=${clientId}&client_secret=${clientSecret}&username=${userName}&password=${userPassword}"""))
      .check(status.is(200))
      .check(jsonPath("$..access_token").saveAs("accessToken"))
      .check(jsonPath("$..refresh_token").saveAs("refreshToken"))
    )
  }

}
