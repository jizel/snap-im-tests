package travel.snapshot.dp.qa.identity

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils.SessionUtils
import travel.snapshot.dp.qa.utils.SessionUtils._

/**
  * Encapsulates all steps for some concrete identity simulation
  */
abstract class AbstractIdentitySimulation extends AbstractSimulation("IdentityModule-1.0", "identity") {

  // upper bound for generating random numbers in data
  private val randomBound = 10000000

  object CreateCustomer {
    def apply() = exec(http("add customer")
      .post(session => s"customers?access_token=$accessToken").body(StringBody(_ =>
      s"""
          {
            "company_name": "Company ${randomUtils.randomInt(randomBound)}",
            "code": "perf_${randomUtils.randomInt(randomBound)}",
            "phone": "+42012345679",
            "email": "perfcompany_${randomUtils.randomInt(randomBound)}@aaa.cz",
            "website" : "http://www.snapshot.travel",
            "is_demo_customer": true,
            "address": {
              "address_line1": "string",
              "city": "string",
              "zip_code": "string",
              "country": "CZ"
            },
            "notes": "string",
            "timezone": "Europe/Prague"
          }"""))
      .check(status.is(201))
      .check(jsonPath("$..customer_id").saveAs("customerId"))
      .check(jsonPath("$..email").saveAs("customerEmail"))
    )
  }

  object GetCustomers {
    def getCustomers = exec(http("get 50 customers")
      .get(s"customers?access_token=$accessToken")
      .check(status.is(200)))

    def getCustomer = exec(http("get 1 customer")
      .get(session => s"customers/${SessionUtils.getValue(session, "customerId")}?access_token=$accessToken")
      .check(status.is(200)))

    def apply() = exec(getCustomers, getCustomer)
  }

  object CreateAndAssignPropertiesToCustomer {
    // create 5 new properties and assign them to the existing customer
    def apply() = repeat(5, "propertyNumber") {
      exec(createProperty, assignPropertyToCustomer)
    }

    def createProperty: ChainBuilder = exec(http("create property")
      .post(session => s"properties?access_token=$accessToken")
      .body(StringBody(session => {

        val propertyId = randomUtils.randomUUIDAsString

        // need to use "session.get" because short EL syntax doesn't work inside the body
        s"""
         {
           "property_id": "$propertyId",
           "salesforce_id": "salesforce_id",
           "property_name": "testing property",
           "property_code": "${propertyId}_code",
           "website": "http://example.org",
           "email": "${getValue(session, "customerEmail")}",
           "is_demo_property": true,
           "timezone": "Europe/Prague"
         }
       """
      }))
      .check(status.is(201))
      .check(jsonPath("$..property_id").saveAs("propertyId"))
    )

    def assignPropertyToCustomer: ChainBuilder = exec(http("assign property to customer")
      .post(session => s"customers/${SessionUtils.getValue(session, "customerId")}/properties?access_token=$accessToken")
      .body(StringBody(session =>
        s"""
         {
           "relationship_id": "${randomUtils.randomUUIDAsString}",
           "property_id": "${getValue(session, "propertyId")}",
           "type": "anchor",
           "valid_from": "2015-11-20",
           "valid_to": "2099-11-20"
         }
       """))
      .check(status.is(201))
    )

  }

  // TODO GetCustomersProperties with filter and cursor

  object GetCustomersProperties {
    def apply() = exec(http("get customer's properties")
      .get(session => s"customers/${SessionUtils.getValue(session, "customerId")}/properties?access_token=$accessToken")
      .check(status.is(200)))
  }

  object CreateAndAssignUsersToCustomer {
    // create 5 new users and assign them to the existing customer
    def apply() = repeat(5, "userNumber") {
      exec(createUser, assignUserToCustomer)
    }

    def createUser: ChainBuilder = exec(http("create user")
      .post(session => s"users?access_token=$accessToken")
      .body(StringBody(session => {

        val userId = randomUtils.randomUUIDAsString
        val randomString = userId.reverse

        // need to use "session.get" because short EL syntax doesn't work inside the body
        s"""
         {
           "user_id": "$userId",
           "partner_id": "partner_id"
           "salesforce_id": "salesforce_id",
           "user_type": "snapshot",
           "user_name": "${randomUtils.randomUUIDAsString}",
           "first_name": "FirstName",
           "last_name": "LastName",
           "phone": "+420123456789",
           "email": "user_${randomUtils.randomInt(randomBound)}@aaa.cz",
           "timezone": "${randomUtils.randomTimezone}",
           "culture": "${randomUtils.randomCulture}",
           "comment": "${randomString}",
           "picture": "${randomString}",
           "is_active": "${randomUtils.randomBoolean}"
         }
       """
      }))
      .check(status.is(201))
      .check(jsonPath("$..user_id").saveAs("userId"))
    )

    def assignUserToCustomer: ChainBuilder = exec(http("assign user to customer")
      .post(session => s"customers/${SessionUtils.getValue(session, "customerId")}/users?access_token=$accessToken")
      .body(StringBody(session =>
        s"""
         {
           "user_id": "${getValue(session, "userId")}",
           "is_primary": ${randomUtils.randomBoolean}
         }
       """))
      .check(status.is(204))
    )
  }

  // TODO GetCustomersUsers with filter and cursor

  object GetCustomersUsers {
    def apply() = exec(http("get customer's users")
      .get(session => s"customers/${SessionUtils.getValue(session, "customerId")}/users?access_token=$accessToken")
      .check(status.is(200)))
  }

  object CreateAndAssignPropertySetsToCustomers {
    def apply() = null
  }

  // TODO GetCustomerPropertySets with filter and cursor

  object GetCustomersPropertySets {
    def apply() = null
  }

}
