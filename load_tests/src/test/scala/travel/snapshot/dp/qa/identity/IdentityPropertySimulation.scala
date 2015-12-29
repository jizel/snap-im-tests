package travel.snapshot.dp.qa.identity

import io.gatling.core.Predef._

class IdentityPropertySimulation extends AbstractIdentitySimulation {

  runScenario(scenario("create and list customers and their properties").exec(
    CreateCustomer(),
    pause(1),
    CreateAndAssignPropertiesToCustomer(20),
    pause(1),
    GetCustomersProperties()))

}
