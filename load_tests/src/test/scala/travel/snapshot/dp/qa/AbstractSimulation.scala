package travel.snapshot.dp.qa

import io.gatling.core.Predef._
import io.gatling.core.controller.inject.InjectionStep
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.config.HttpProtocolBuilder
import travel.snapshot.dp.qa.utils.rateshopper.{RateShopperPropertyCodeParser, RateShopperPropertyCodes}
import travel.snapshot.dp.qa.utils.{BaseUrlResolver, RandomUtils}
import travel.snapshot.dp.qa.utils._

/**
  * Basic class with reusable code and configuration for gatling simulations.
  * The request path is formatted as follows: {host}/{moduleName}/api/{basePath}
  */
abstract class AbstractSimulation extends Simulation {

  private val defaultClientId = "04000000-0000-4444-8888-000000000000"
  private val defaultClientSecret = "6e456324-a911-40c7-a515-37981cf48961"
  private val defaultUserName = "jiriCustomer"
  private val defaultUserPassword = "Pass12345!"

  val protocol: String = System.getProperty("protocol", "http")
  val host: String = System.getProperty("host", "localhost")
  val port: Integer = Integer.getInteger("port", 8080)

  val startUsers: Integer = Integer.getInteger("startUsers", 10)
  val endUsers: Integer = Integer.getInteger("endUsers", 30)
  val rampTime: Integer = Integer.getInteger("ramp", 60)

  // Default values for tests can be overridden by command line params
  val clientId: String = System.getProperty("clientId", defaultClientId)
  val clientSecret: String = System.getProperty("clientSecret", defaultClientSecret)
  val username: String = System.getProperty("username", defaultUserName)
  val userPassword: String = System.getProperty("userPassword", defaultUserPassword)
  val injectionMethod: String = System.getProperty("injectionMethod")

  // access_token query property for social modules
  val accessTokenParam: String = System.getProperty("accessToken", "default_access_token")

  // Default property for integrations testing
  val propertyId: String = System.getProperty("propertyId", "")

  val httpConf: HttpProtocolBuilder = http
    .baseURL(BaseUrlResolver())
    .contentTypeHeader("application/json")
    .acceptHeader("application/json")
    .userAgentHeader("Gatling / API Load Test")
    .doNotTrackHeader("1")

  val randomUtils = new RandomUtils

  val rateShopperPropertyCodes: RateShopperPropertyCodes = new RateShopperPropertyCodeParser().parse()

  val request_headers = Map(
    "Accept-Encoding" -> "gzip, deflate, br",
    "Content-Type" -> "application/json;charset=UTF-8",
    "X-Auth-AppId" -> "04000000-0000-4444-8888-000000000000",
    "X-Auth-UserId" -> "0b000000-0000-4444-8888-000000000000"
  )

  /** Executes the scenario defined in descendant
    * Ramps users per second from startUsers to endUsers */
  private val rampPerSec: InjectionStep = rampUsersPerSec(startUsers.toDouble) to endUsers.toDouble during (rampTime seconds)

  /** Executes the scenario defined in descendant
    * Using alternative (simpler) ramping. Only endUsers and rampTime parameters are used.
    * Users (threads) are ramped from one to endUsers */
  private val rampSimple: InjectionStep = rampUsers(endUsers) over rampTime

  /** Executes the scenario defined in descendant.
    * startUser number of threads is activated at the beginning and each thread performs all actions from scn once. */
  private val atOnce: InjectionStep = atOnceUsers(startUsers)

  private val constantPerSec: InjectionStep = constantUsersPerSec(startUsers.toDouble) during(rampTime)


  /** Executes the scenario defined in descendant with specified injection method */
  protected def runScenario(scn: ScenarioBuilder): Unit = {
    injectionMethod match {
      case "atOnce" => setUp(scn.inject(atOnce).protocols(httpConf))
      case "rampSimple" => setUp(scn.inject(rampSimple).protocols(httpConf))
      case _ => setUp(scn.inject(rampPerSec).protocols(httpConf))
    }
  }


  object getToken {
    def apply() = exec(http("Get Keycloak token")
      .post(OAuthUrlResolver())
      .asFormUrlEncoded
      .body(StringBody(_ => s"""grant_type=password&client_id=$clientId&client_secret=$clientSecret&username=$username&password=$userPassword"""))
      .check(status.is(200))
      .check(jsonPath("$..access_token").saveAs("accessToken"))
      .check(jsonPath("$..refresh_token").saveAs("refreshToken"))
    )
  }

}
