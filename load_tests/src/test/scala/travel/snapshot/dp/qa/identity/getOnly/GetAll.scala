package travel.snapshot.dp.qa.identity.getOnly

import io.gatling.core.Predef._
import travel.snapshot.dp.qa.identity.AbstractIdentitySimulation

/**
  * Get all entities (all basic endpoints)
  */
class GetAll extends AbstractIdentitySimulation {

  runScenario(scenario("Get token and get all customers").exec(
//    getTokenByUser("testSnapshot", "Pass1234"),
    getTokenByUser("exampleCustomerUser", "pass1"),
//    getTokenByUser("uqbosb9zp2", "pass1"),
    GetAllCustomers(),
    pause(1),
    GetAllProperties()
    pause(1),
    GetAllUsers(),
    pause(1),
    GetAllPartners(),
    pause(1),
    GetAllApplications(),
    pause(1),
    GetAllPropertySets(),
    pause(1),
    GetAllCommercialSubscriptions(),
    pause(1),
    GetAllCustomers(10),
    pause(1),
    GetAllProperties(10),
    pause(1),
    GetAllUsers(10),
    pause(1),
    GetAllPartners(10),
    pause(1),
    GetAllApplications(10),
    pause(1),
    GetAllPropertySets(10),
    pause(1),
    GetAllCommercialSubscriptions(10)
  ))
}
