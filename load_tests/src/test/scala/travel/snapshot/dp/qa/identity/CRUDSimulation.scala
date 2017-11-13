package travel.snapshot.dp.qa.identity

import io.gatling.core.Predef._

class CRUDSimulation extends AbstractIdentitySimulation {

  val NO_OF_GET_REQUESTS = 99
  val PAUSE = 1

  runScenario(scenario("Create, get update and delete entities. Get to other operations ratio is 10:1").exec(
    getToken(),
    GetAllCustomers(NO_OF_GET_REQUESTS),
    pause(PAUSE),
    CreateCustomer(),
    pause(PAUSE),
    getToken(),
    GetAllUsers(NO_OF_GET_REQUESTS),
    pause(PAUSE),
    CreatePartnerUser(),
    pause(PAUSE),
    getToken(),
    GetAllPartners(NO_OF_GET_REQUESTS),
    pause(PAUSE),
    CreatePartner()
  ))

}
