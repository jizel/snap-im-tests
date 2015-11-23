package travel.snapshot.dp.qa

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import Utils._

/**
  * Gatling scenario exercising the "customers" part of Identity module rest API.
  *
  * The basic workflow is to create customer and multiple properties and assigned those properties to the customer.
  */
class BasicIdentitySimulation extends AbstractSimulation("IdentityModule-1.0", "identity") {

  // upper bound for generating random numbers in data
  private val randomBound = 10000000

  object CreateCustomer {
    def apply() = exec(http("add customer")
        .post("customers").body(StringBody(_ =>
        s"""
          {
            "company_name": "Company ${randomInt(randomBound)}",
            "code": "perf_${randomInt(randomBound)}",
            "phone": "+42012345679",
            "email": "perfcompany_${randomInt(randomBound)}@aaa.cz",
            "website" : "http://www.snapshot.travel",
            "is_demo_customer": true,
            "address": {
              "address_line1": "string",
              "city": "string",
              "zip_code": "string",
              "country": "CZ"
            },
            "notes": "string"
          }"""))
        .check(status.is(201))
        .check(jsonPath("$..customer_id").saveAs("customerId"))
        .check(jsonPath("$..email").saveAs("customerEmail"))
    )
  }

  object GetCustomers {
    def getCustomers = exec(http("get 50 customers")
      .get("customers")
      .check(status.is(200)))

    def getCustomer = exec(http("get 50 customers")
      .get("customers/${customerId}")
      .check(status.is(200)))

    def apply() = exec(getCustomers, getCustomer)
  }

  object CreateAndAssignProperties {
    // create 5 new properties and assign them to the existing customer
    def apply() = repeat(5, "propertyNumber") { exec(createProperty, assignPropertyToCustomer) }

    def createProperty: ChainBuilder = exec(http("create property")
        .post("properties").body(StringBody(session => {

        val propertyId = randomUUID

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
           "timezone": "UTC+00:00"
         }
       """}))
        .check(status.is(201))
        .check(jsonPath("$..property_id").saveAs("propertyId"))
      )

    def assignPropertyToCustomer: ChainBuilder = exec(http("assign property to customer")
      .post("customers/${customerId}/properties").body(StringBody(session =>
      s"""
         {
           "relationship_id": "$randomUUID",
           "property_id": "${getValue(session, "propertyId")}",
           "type": "anchor",
           "valid_from": "2015-11-20",
           "valid_to": "2099-11-20"
         }
       """))
      .check(status.is(201))
    )

  }

  object GetProperties {
    def apply() = exec(http("get customer's properties")
      .get("customers/${customerId}/properties")
      .check(status.is(200)))
  }

  runScenario(scenario("create and list customers and their properties").exec(
    CreateCustomer(), GetCustomers(), pause(1), CreateAndAssignProperties(), GetProperties()))
  }
