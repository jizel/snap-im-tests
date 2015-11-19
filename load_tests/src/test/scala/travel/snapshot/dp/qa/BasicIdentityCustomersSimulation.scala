package travel.snapshot.dp.qa

import io.gatling.core.Predef._
import io.gatling.http.Predef._

/**
  * Gatling scenario exercising the Identity module rest API.
  */
class BasicIdentityCustomersSimulation extends AbstractSimulation("IdentityModule-1.0", "identity") {

  // upper bound for generating random numbers in data
  private val randomBound = 10000000

  val scn = scenario("Create and get customers") // A scenario is a chain of requests and pauses
      .exec(http("add customer") // Here's an example of a POST request
      .post("/customers").body(StringBody(session =>
        s"""
          {
            "company_name": "Company ${randomInt(randomBound)}",
            "code": "perf_${randomInt(randomBound)}",
            "phone": "+42012345679",
            "email": "perfcompany_${randomInt(randomBound)}@aaa.cz",
            "website" : "http://www.snapshot.travel",
            "is_demo_customer": true,
            "address": {
              "address_line1": "string",
              "city": "string",
              "zip_code": "string",
              "country": "CZ"
            },
            "notes": "string"
          }""")).asJSON
    .check(status.is(201)))

    .pause(1) // Note that Gatling has recorder real time pauses
    .exec(http("get 50 customers")
      .get("/customers")
      .check(status.is(200)))

  runScenario(scn)
}
