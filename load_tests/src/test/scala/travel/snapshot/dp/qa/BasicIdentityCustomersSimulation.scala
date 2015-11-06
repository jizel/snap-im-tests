package travel.snapshot.dp.qa

import java.util.concurrent.ThreadLocalRandom

import com.sun.javafx.Utils
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

class BasicIdentityCustomersSimulation extends Simulation {

  val startUsers = Integer.getInteger("startUsers", 10)
  val endUsers = Integer.getInteger("endUsers", 30)
  val rampTime = Integer.getInteger("ramp", 60)

  val httpConf = http
    .baseURL("http://localhost:8080/IdentityModule-1.0/api/identity") // Here is the root for all relative URLs
    .acceptHeader("application/json") // Here are the common headers
    .doNotTrackHeader("1")

  val scn = scenario("Create and get customers") // A scenario is a chain of requests and pauses
      .exec(http("add customer") // Here's an example of a POST request
      .post("/customers").body(StringBody(session =>
        """
          {
   "company_name": "Company """ +  ThreadLocalRandom.current.nextInt(10000000) + """",
   "code": "perf_""" + ThreadLocalRandom.current.nextInt(10000000) + """",
   "phone": "+42012345679",
   "email": "perfcompany_""" + ThreadLocalRandom.current.nextInt(10000000) + """@aaa.cz",
   "website" : "http://www.snapshot.travel",
   "is_demo_customer": true,

   "address": {
     "address_line1": "string",

   "city": "string",
     "zip_code": "string",
     "country": "CZ"
   },
   "notes": "string"
 }        """)).asJSON
    .check(status.is(201)))

    .pause(1) // Note that Gatling has recorder real time pauses
    .exec(http("get 50 customers")
      .get("/customers")
      .check(status.is(200)))

  setUp(scn.inject(rampUsersPerSec(startUsers.toDouble) to(endUsers.toDouble) during(rampTime seconds))).protocols(httpConf)
    //.throttle(reachRps(100) in (10 seconds), holdFor(10 minutes))).protocols(httpConf)
}
