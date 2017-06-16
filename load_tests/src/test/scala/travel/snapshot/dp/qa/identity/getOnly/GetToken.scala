package travel.snapshot.dp.qa.identity.getOnly

import io.gatling.core.Predef._
import travel.snapshot.dp.qa.identity.AbstractIdentitySimulation

/**
  * Created by zelezny on 6/7/2017.
  */
class GetToken extends AbstractIdentitySimulation {

  runScenario(scenario("Get token and get all customers").exec(
  getTokenByUser("exampleCustomerUser", "pass1"),
//  getTokenByUser("testSnapshot", "Pass1234"),
  GetAllCustomers()
  ))

}
