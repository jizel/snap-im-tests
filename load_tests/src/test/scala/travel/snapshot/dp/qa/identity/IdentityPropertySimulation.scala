package travel.snapshot.dp.qa.identity

import io.gatling.core.Predef._

class IdentityPropertySimulation extends AbstractIdentitySimulation {

  runScenario(scenario("create and list customers and their properties").exec(
    CreateCustomer(),
    pause(1),
    CreateAndAssignPropertiesToCustomer(randomUtils.randomInt(20)),
    pause(1),
    GetCustomersProperties()))

}
