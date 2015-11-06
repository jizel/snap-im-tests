package travel.snapshot.dp.qa

import java.util.concurrent.ThreadLocalRandom

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class BasicConfigurationSimulation extends Simulation {

  val startUsers = Integer.getInteger("startUsers", 10)
  val endUsers = Integer.getInteger("endUsers", 30)
  val rampTime = Integer.getInteger("ramp", 60)

  val httpConf = http
    .baseURL("http://localhost:8080/ConfigurationModule-1.0/api/") // Here is the root for all relative URLs
    .acceptHeader("application/json") // Here are the common headers
    .doNotTrackHeader("1")

  val scn = scenario("Create and get configuration types") // A scenario is a chain of requests and pauses
      .exec(http("add configuration_type") // Here's an example of a POST request
      .post("/configuration").body(StringBody(session =>
        """
          {
   "identifier": "config_""" +  ThreadLocalRandom.current.nextInt(10000000) + """",
   "description": "description"
 }        """)).asJSON
    .check(status.is(201)))

    .pause(1) // Note that Gatling has recorder real time pauses
    .exec(http("get 50 configuration_types")
      .get("/configuration")
      .check(status.is(200)))


  setUp(scn.inject(rampUsersPerSec(startUsers.toDouble) to (endUsers.toDouble) during (rampTime seconds))).protocols(httpConf)
  //.throttle(reachRps(100) in (10 seconds), holdFor(10 minutes))).protocols(httpConf)
}
