package travel.snapshot.dp.qa.identity

import io.gatling.core.Predef._

class IdentityUserSimulation extends AbstractIdentitySimulation {

  runScenario(scenario("create and list customers and their users").exec(
    CreateCustomer(),
    pause(1),
    CreateAndAssignUsersToCustomer(10),
    pause(1),
    GetCustomersUsers()))
}
