package travel.snapshot.dp.qa

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.StringBody

/**
  * Gatling scenario exercising the Configuration module rest API.
  */
class BasicConfigurationSimulation extends AbstractSimulation("ConfigurationModule-1.0") {

  val scn = scenario("Create and get configuration types") // A scenario is a chain of requests and pauses
      .exec(http("add configuration_type") // Here's an example of a POST request
      .post("/configuration").body(newConfigurationTypeJson).asJSON
    .check(status.is(201)).check(header("ETag").exists))

    .pause(1) // Note that Gatling has recorder real time pauses
    .exec(http("get 50 configuration_types")
      .get(StringBody(session => s"/configuration?limit=${randomInt(50) + 20}&cursor=${randomInt(10) + randomInt(20)}"))
      .check(status.is(200)))

  runScenario(scn)


  /**
    * Creates new unique configuration type.
    * @return JSON representation of new configuration type
    */
  private def newConfigurationTypeJson: StringBody = {
    StringBody(session => s"""
          {
            "identifier": "config_${randomInt(10000000)}",
            "description": "description"
          }
      """)
  }
}
