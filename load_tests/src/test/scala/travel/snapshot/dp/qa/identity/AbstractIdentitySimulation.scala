package travel.snapshot.dp.qa.identity

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils._
import travel.snapshot.dp.qa.utils.SessionUtils._

/**
  * Encapsulates all steps for some concrete identity simulation
  */
abstract class AbstractIdentitySimulation extends AbstractSimulation {

  // upper bound for generating random numbers in data
  private val randomBound = 10000000

  /**
    * Creates customer. After customer is created, its customerId and customerEmail are saved to session for future
    * reference in other scenario objects.
    */
  object CreateCustomer {
    def apply() = exec(http("add customer")
      .post(session => s"identity/customers?access_token=$accessToken").body(StringBody(_ =>
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
            "timezone": "${randomUtils.randomTimezone}"
          }"""))
      .check(status.is(201))
      .check(jsonPath("$..customer_id").saveAs("customerId"))
      .check(jsonPath("$..email").saveAs("customerEmail"))
    )
  }

  /**
    * Creates properties and assigns it to a customer with id which was created in CreateCustomer object and saves
    * property id to session.
    *
    * By default, it creates 1 property, this can be overridden by number specified in a constructor.
    *
    * Validity of a property will be randomly determined, roughly +50 years to the future from now.
    */
  object CreateAndAssignPropertiesToCustomer {

    def apply() = repeat(1, "propertyNumber") {
      exec(createProperty, assignPropertyToCustomer)
    }

    def apply(numberOfProperties: Integer) = repeat(numberOfProperties.toInt, "propertyNumber") {
      exec(createProperty, assignPropertyToCustomer)
    }

    def createProperty: ChainBuilder = exec(http("create property")
      .post(session => s"identity/properties?access_token=$accessToken")
      .body(StringBody(session => {

        val propertyId = randomUtils.randomUUIDAsString

        s"""
         {
           "property_id": "$propertyId",
           "salesforce_id": "salesforce_id",
           "property_name": "testing property",
           "property_code": "${propertyId}_code",
           "website": "http://example.org",
           "email": "${getValue(session, "customerEmail")}",
           "is_demo_property": true,
           "timezone": "${randomUtils.randomTimezone}"
         }
       """
      }))
      .check(status.is(201))
      .check(jsonPath("$..property_id").saveAs("propertyId"))
    )

    def assignPropertyToCustomer: ChainBuilder = exec(http("assign property to customer")
      .post(session => s"identity/customers/${SessionUtils.getValue(session, "customerId")}/properties?access_token=$accessToken")
      .body(StringBody(session => {
        // random 50 years ahead
        val (validFrom, validTo) = randomUtils.randomValidFromAndTo(randomUtils.randomInt(20000))

        s"""
         {
           "relationship_id": "${randomUtils.randomUUIDAsString}",
           "property_id": "${getValue(session, "propertyId")}",
           "type": "anchor",
           "valid_from": "$validFrom",
           "valid_to": "$validTo"
         }
       """
      }))
      .check(status.is(201))
    )
  }

  /**
    * Creates users and assigns it to a customer with id which was created in CreateCustomer object. Id of a user
    * is saved to session.
    *
    * By default, it creates 1 user, this can be overridden by number specified in a constructor
    */
  object CreateAndAssignUsersToCustomer {

    def apply() = repeat(1, "userNumber") {
      exec(createUser, assignUserToCustomer)
    }

    def apply(numberOfUsers: Integer) = repeat(numberOfUsers.toInt, "userNumber") {
      exec(createUser, assignUserToCustomer)
    }

    def createUser: ChainBuilder = exec(http("create user")
      .post(session => s"identity/users?access_token=$accessToken")
      .body(StringBody(session => {

        val userId = randomUtils.randomUUIDAsString
        val randomString = userId.reverse

        s"""
         {
           "user_id": "$userId",
           "partner_id": "partner_id",
           "salesforce_id": "salesforce_id",
           "user_type": "snapshot",
           "user_name": "${randomUtils.randomUUIDAsString}",
           "first_name": "FirstName",
           "last_name": "LastName",
           "phone": "+420123456789",
           "email": "user_${randomUtils.randomInt(randomBound)}@aaa.cz",
           "timezone": "${randomUtils.randomTimezone}",
           "culture": "${randomUtils.randomCulture}",
           "comment": "$randomString",
           "picture": "$randomString",
           "is_active": ${randomUtils.randomBooleanAsBinary}
         }
       """.stripMargin
      }))
      .check(status.is(201))
      .check(jsonPath("$..user_id").saveAs("userId"))
    )

    def assignUserToCustomer: ChainBuilder = exec(http("assign user to customer")
      .post(session => s"identity/customers/${SessionUtils.getValue(session, "customerId")}/users?access_token=$accessToken")
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

  /**
    * Creates by default 1 property set and assigns it to a customer. Property set id will be saved to session.
    */
  object CreateAndAssignPropertySetsToCustomers {
    def apply() = repeat(1, "propertySetsNumber") {
      exec(createAndAssignPropertySet)
    }

    def apply(numberOfPropertySets: Integer) = repeat(numberOfPropertySets.toInt, "propertySetsNumber") {
      exec(createAndAssignPropertySet)
    }

    def createAndAssignPropertySet: ChainBuilder = exec(http("create property set")
      .post(session => s"identity/property_sets?access_token=$accessToken")
      .body(StringBody(session => {

        val userId = randomUtils.randomUUIDAsString
        val randomString = userId.reverse

        s"""
           {
             "property_set_id": "${randomUtils.randomUUIDAsString}",
             "property_set_name": "${randomUtils.randomUUIDAsString}",
             "property_set_description": "$randomString",
             "customer_id": "${SessionUtils.getValue(session, "customerId")}",
             "property_set_type": "Brand"
           }
       """
      }))
      .check(status.is(201))
      .check(jsonPath("$..property_set_id").saveAs("propertySetId"))
    )
  }

  /**
    * Creates by default 1 property set for a customer saved in a session and assigns user saved in a session
    * to such created property set.
    */
  object CreateAndAssignPropertySetsToCustomerAndAssignUser {
    def apply() = repeat(1, "propertySetsNumber") {
      exec(createAndAssignPropertySet, assignUser)
    }

    def apply(numberOfPropertySets: Integer) = repeat(numberOfPropertySets.toInt, "propertySetsNumber") {
      exec(createAndAssignPropertySet, assignUser)
    }

    def createAndAssignPropertySet: ChainBuilder = exec(http("create property set")
      .post(session => s"identity/property_sets?access_token=$accessToken")
      .body(StringBody(session => {

        val userId = randomUtils.randomUUIDAsString
        val randomString = userId.reverse

        s"""
           {
             "property_set_id": "${randomUtils.randomUUIDAsString}",
             "property_set_name": "${randomUtils.randomUUIDAsString}",
             "property_set_description": "$randomString",
             "customer_id": "${SessionUtils.getValue(session, "customerId")}",
             "property_set_type": "Brand"
           }
       """
      }))
      .check(status.is(201))
      .check(jsonPath("$..property_set_id").saveAs("propertySetId"))
    )

    def assignUser: ChainBuilder = exec(http("assign user to property set")
      .post(session => s"identity/property_sets/${SessionUtils.getValue(session, "propertySetId")}/users")
      .body(StringBody(session => {
        s"""
          {
            "user_id": "${SessionUtils.getValue(session, "userId")}"
          }
         """
      }))
      .check(status.is(204))
    )
  }

  /**
    * Gets list of customers
    */
  object GetCustomers {
    def getCustomers = exec(http("get 50 customers")
      .get(s"identity/customers?access_token=$accessToken")
      .check(status.is(200)))

    def apply = getCustomers
  }

  /**
    * Gets single customer according to its ID saved in a session if ID is not specified in constructor
    */
  object GetSingleCustomer {
    def getCustomer(customerId: String = null) = {
      exec(http("get 1 customer")
        .get(session => {
          val id = if (customerId == null) SessionUtils.getValue(session, "customerId") else customerId
          s"identity/customers/$id?access_token=$accessToken"
        })
        .check(status.is(200)))
    }

    def apply() = getCustomer()

    def apply(customerId: String) = getCustomer(customerId)
  }

  /**
    * Gets customers properties, it is possible to define filter, number of filters, sort, cursor and limit.
    *
    * In case filter is null, random filter is created. Number of clauses in filter is equal to "numbeOfFilters" parameters.
    * If numberOfFilters is not defined, filtering is not used
    */
  object GetCustomersProperties {
    def apply() = request("get customer's properties")

    def request(request: String, filter: String = null, numberOfFilters: Integer = -1, sort: CustomerPropertyFilter.Value = null, cursor: Integer = -1, limit: Integer = -1) = {

      val builtFilter = if (filter == null) new CustomerPropertyFilterBuilder(numberOfFilters).build() else filter
      val additionalQueries = new QueryUtils().buildAdditionalQueries(builtFilter, sort, cursor, limit)

      exec(http(request)
        .get(session => s"identity/customers/${SessionUtils.getValue(session, "customerId")}/properties?access_token=$accessToken$additionalQueries")
        .check(status.is(200)))
    }
  }

  /**
    * Gets customers users, it is possible to define cursor, limit and sort
    */
  object GetCustomersUsers {
    def apply() = request("get customer's users")

    def request(request: String, sort: String = null, cursor: Integer = -1, limit: Integer = -1) = {

      val additionalQueries = new QueryUtils().buildAdditionalQueries("", sort, cursor, limit)

      exec(http(request)
        .get(session => s"identity/customers/${SessionUtils.getValue(session, "customerId")}/users?access_token=$accessToken$additionalQueries")
        .check(status.is(200)))
    }
  }

  /**
    * Gets customers property sets, it is possible to define cursor, limit and sort
    */
  object GetCustomersPropertySets {
    def apply() = request("get customer's propery sets")

    def request(request: String, sort: String = null, cursor: Integer = -1, limit: Integer = -1) = {

      val additionalQueries = new QueryUtils().buildAdditionalQueries("", sort, cursor, limit)

      exec(http(request)
        .get(session => s"identity/customers/${SessionUtils.getValue(session, "customerId")}/property_sets?access_token=$accessToken$additionalQueries")
        .check(status.is(200)))
    }
  }

}
