package travel.snapshot.dp.qa.identity.getOnly

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import travel.snapshot.dp.qa.identity.AbstractIdentitySimulation

/**
  * Get all entities (all basic endpoints)
  */
class GetAll extends AbstractIdentitySimulation {

  val getAllScenario: ScenarioBuilder = scenario("Get token and get all customers").exec(
//    getToken(),
    GetAllCustomers(),
    pause(1),
    GetAllProperties(),
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
  )

  runScenario(getAllScenario)

}
