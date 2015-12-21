package travel.snapshot.dp.qa.identity

import io.gatling.core.Predef._

class IdentityPropertySetSimulation extends AbstractIdentitySimulation {

  runScenario(scenario("create and list customers and their property sets").exec(
    CreateCustomer(),
    pause(1),
    CreateAndAssignPropertySetsToCustomers(),
    pause(1),
    GetCustomersPropertySets()))
}
