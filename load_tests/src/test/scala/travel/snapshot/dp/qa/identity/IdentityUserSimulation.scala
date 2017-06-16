package travel.snapshot.dp.qa.identity

import io.gatling.core.Predef._

class IdentityUserSimulation extends AbstractIdentitySimulation {

  runScenario(scenario("create and list customers and their users").exec(
    getTokenByUser("exampleCustomerUser", "pass1"),
    CreateCustomer(),
    GetAllCustomers(2)
    pause 1,
    CreateAndAssignUsersToCustomer(),
    pause(1),
    GetCustomersUsers(),
    pause(1),
    GetAllCustomers(10)
  ))
}
