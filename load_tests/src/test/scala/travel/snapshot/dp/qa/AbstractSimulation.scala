package travel.snapshot.dp.qa

import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

/**
  * Basic class with reusable code and configuration for gatling simulations.
  * The request path is formatted as follows: {host}/{moduleName}/api/{basePath}
  *
  * @param moduleName name of packaged module as deployed to the application server - e.g. "ConfiguratiomModule-1.0"
  * @param basePath common path to be used by all requests, default is empty path
  */
abstract class AbstractSimulation(val moduleName: String, val basePath: String = "") extends Simulation {
  val startUsers = Integer.getInteger("startUsers", 10)
  val endUsers = Integer.getInteger("endUsers", 30)
  val rampTime = Integer.getInteger("ramp", 60)

  val httpConf = http
    .baseURL(s"http://localhost:8080/$moduleName/api/$basePath") // Here is the root for all relative URLs
    .acceptHeader("application/json") // Here are the common headers
    .userAgentHeader("Gatling / API Load Test")
    .doNotTrackHeader("1")

  protected def randomInt(bound: Int): Int = ThreadLocalRandom.current.nextInt(bound)

  /** Executes the scenario defined in descendant */
  protected def runScenario(scn: ScenarioBuilder): Unit = {
    setUp(scn.inject(rampUsersPerSec(startUsers.toDouble) to endUsers.toDouble during(rampTime seconds))).protocols(httpConf)
  }

}
