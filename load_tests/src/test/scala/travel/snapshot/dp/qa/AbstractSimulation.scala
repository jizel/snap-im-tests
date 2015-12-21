package travel.snapshot.dp.qa

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.utils.RandomUtils

/**
  * Basic class with reusable code and configuration for gatling simulations.
  * The request path is formatted as follows: {host}/{moduleName}/api/{basePath}
  *
  * @param moduleName name of packaged module as deployed to the application server - e.g. "ConfiguratiomModule-1.0"
  * @param basePath common path to be used by all requests, default is empty path
  */
abstract class AbstractSimulation(val moduleName: String = "": String, val basePath: String = "") extends Simulation {

  val protocol = System.getProperty("protocol", "http")
  val host = System.getProperty("host", "localhost")
  val port = Integer.getInteger("port", 8080)

  val startUsers = Integer.getInteger("startUsers", 10)
  val endUsers = Integer.getInteger("endUsers", 30)
  val rampTime = Integer.getInteger("ramp", 60)

  // access_token query property for social modules
  val accessToken = System.getProperty("access_token")

  val httpConf = http
    .baseURL(s"$protocol://$host:$port/$moduleName/api/$basePath/")
    .contentTypeHeader("application/json")
    .acceptHeader("application/json")
    .userAgentHeader("Gatling / API Load Test")
    .doNotTrackHeader("1")

  val randomUtils = new RandomUtils

  /** Executes the scenario defined in descendant */
  protected def runScenario(scn: ScenarioBuilder): Unit = {
    setUp(scn.inject(rampUsersPerSec(startUsers.toDouble) to endUsers.toDouble during (rampTime seconds))).protocols(httpConf)
  }

}
