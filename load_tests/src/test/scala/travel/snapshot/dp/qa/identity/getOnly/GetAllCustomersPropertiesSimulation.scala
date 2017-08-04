package travel.snapshot.dp.qa.identity.getOnly

import io.gatling.core.Predef._
import travel.snapshot.dp.qa.identity.AbstractIdentitySimulation

class GetAllCustomersPropertiesSimulation extends AbstractIdentitySimulation {

  runScenario(scenario("create and list customers and their users").exec(
    getToken(),
    GetAllCustomers(),
    pause(1),
    GetAllProperties(),
    pause(1),
    GetAllCustomers(10),
    pause(1),
    GetAllProperties(10)
  ))
}

