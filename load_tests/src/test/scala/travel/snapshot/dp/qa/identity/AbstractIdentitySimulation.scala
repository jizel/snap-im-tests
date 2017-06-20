package travel.snapshot.dp.qa.identity

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils._
import travel.snapshot.dp.qa.utils.SessionUtils._
import org.apache.http.HttpStatus

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
  object CreateCustomer extends AbstractRequest {
    def apply() = exec(postRequest("Create customer", generateCustomer(), "identity/customers", HttpStatus.SC_CREATED)
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
      .post(session => s"identity/properties?access_token=$accessTokenParam")
      .headers(request_headers)
      .body(StringBody(session => {
        val propertyId = randomUtils.randomUUIDAsString
        s"""
        {
          "property_id": "$propertyId",
          "name": "testing property",
          "property_code": "${propertyId}_code",
          "website": "http://example.org",
          "email": "${getValue(session, "customerEmail")}",
          "is_demo_property": true,
          "timezone": "${randomUtils.randomTimezone}",
          "address": {
             "address_line1": "string",
             "city": "string",
             "zip_code": "string",
             "country": "CZ"
             },
          "anchor_customer_id": "${SessionUtils.getValue(session, "customerId")}"
        }
      """
      }))
      .check(status.is(201))
      .check(jsonPath("$..property_id").saveAs("propertyId"))
    )


    def assignPropertyToCustomer: ChainBuilder = exec(http("assign property to customer")
      .post(session => s"identity/customers/${SessionUtils.getValue(session, "customerId")}/properties?access_token=$accessTokenParam")
      .headers(request_headers)
      .body(StringBody(session => {
        // random 50 years ahead
        val (validFrom, validTo) = randomUtils.randomValidFromAndTo(randomUtils.randomInt(20000))
        s"""
        {
          "relationship_id": "${randomUtils.randomUUIDAsString}",
          "property_id": "${getValue(session, "propertyId")}",
          "relationship_type": "owner",
          "valid_from": "$validFrom",
          "valid_to": "$validTo"
        }
      """
      }))
      .check(status.is(201))
    )
  }

  /**
    * Gets list of all properties
    */
  object GetAllProperties extends AbstractRequest {
    def apply() = getRequest("Get all properties", None, "identity/properties", HttpStatus.SC_OK)

    def apply(numberOfProperties: Integer) = repeat(numberOfProperties.toInt, "requestNumber") {
      getRequest(s"Get all properties $numberOfProperties times", None, "identity/properties", HttpStatus.SC_OK)
    }
  }

  /**
    * Gets list of all properties
    */
  object GetAllPropertySets extends AbstractRequest {
    def apply() = getRequest("Get all property sets", None, "identity/property_sets", HttpStatus.SC_OK)

    def apply(numberOfProperties: Integer) = repeat(numberOfProperties.toInt, "requestNumber") {
      getRequest(s"Get all property sets $numberOfProperties times", None, "identity/property_sets", HttpStatus.SC_OK)
    }
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
      .post(session => s"identity/users?access_token=$accessTokenParam")
      .headers(request_headers)
      .body(StringBody(session => {
        val userId = randomUtils.randomUUIDAsString
        val randomString = userId.reverse
        s"""
        {
          "user_id": "$userId",
          "user_type": "snapshot",
          "user_name": "${randomUtils.randomUUIDAsString}",
          "first_name": "FirstName",
          "last_name": "LastName",
          "phone": "+420123456789",
          "email": "user_${randomUtils.randomInt(randomBound)}@aaa.cz",
          "timezone": "${randomUtils.randomTimezone}",
          "culture": "${randomUtils.randomCulture}",
          "comment": "$randomString",
          "picture": "http://www.validurl.com",
          "is_active": ${randomUtils.randomBooleanAsBinary}
        }
      """.stripMargin
      }))
      .check(status.is(201))
      .check(jsonPath("$..user_id").saveAs("userId"))
    )

    def assignUserToCustomer: ChainBuilder = exec(http("assign user to customer")
      .post(session => s"identity/customers/${SessionUtils.getValue(session, "customerId")}/users?access_token=$accessTokenParam")
      .headers(request_headers)
      .body(StringBody(session =>
        s"""
        {
          "user_id": "${getValue(session, "userId")}",
          "is_primary": ${randomUtils.randomBoolean}
        }
      """))
      .check(status.is(201))
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
      .post(session => s"identity/property_sets?access_token=$accessTokenParam")
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
      .post(session => s"identity/property_sets?access_token=$accessTokenParam")
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
  object GetAllCustomers extends AbstractRequest {
    val customersUrl = "identity/customers"

    def apply() = getRequest("Get all customers", None, customersUrl, HttpStatus.SC_OK)

    def apply(numberOfCustomers: Integer) = repeat(numberOfCustomers.toInt, "requestNumber") {
      getRequest(s"Get all customers $numberOfCustomers times", None, customersUrl, HttpStatus.SC_OK)
    }
  }

  /**
    * Gets single customer according to its ID saved in a session if ID is not specified in constructor
    */
  object GetSingleCustomer {
    def getCustomer(customerId: String = null) = {
      exec(http("get 1 customer")
        .get(session => {
          val id = if (customerId == null) SessionUtils.getValue(session, "customerId") else customerId
          s"identity/customers/$id"
        })
        .header("Authorization", session => {
          if (SessionUtils.containsValue(session, "access_token")) s"Bearer ${SessionUtils.getValue(session, "access_token")}" else accessTokenParam
        })
        .check(status.is(HttpStatus.SC_OK)))
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
        .get(session => s"identity/customers/${SessionUtils.getValue(session, "customerId")}/properties?access_token=$accessTokenParam$additionalQueries")
        .headers(request_headers)
        .check(status.is(200)))
    }
  }

  /**
    * Gets customers users, it is possible to define cursor, limit and sort
    */
  object GetCustomersUsers {
    def apply() = request("get customer's users")

    def request(request: String, sort: String = null, cursor: Integer = -1, limit: Integer = -1) = {
      val additionalQueries = new QueryUtils().buildAdditionalQueries(null, sort, cursor, limit)
      exec(http(request)
        .get(session => s"identity/customers/${SessionUtils.getValue(session, "customerId")}/users?access_token=$accessTokenParam$additionalQueries")
        .headers(request_headers)
        .check(status.is(200)))
    }
  }

  /**
    * Gets customers property sets, it is possible to define cursor, limit and sort
    */
  object GetCustomersPropertySets {
    def apply() = request("get customer's propery sets")

    def request(request: String, sort: String = null, cursor: Integer = -1, limit: Integer = -1) = {
      val additionalQueries = new QueryUtils().buildAdditionalQueries(null, sort, cursor, limit)
      exec(http(request)
        .get(session => s"identity/customers/${SessionUtils.getValue(session, "customerId")}/property_sets?access_token=$accessTokenParam$additionalQueries")
        .check(status.is(200)))
    }
  }

  /**
    * Gets all users
    */
  object GetAllUsers extends AbstractRequest {
    val usersUrl = "identity/users"

    def apply() = getRequest("Get all users", None, usersUrl, HttpStatus.SC_OK)

    def apply(numberOfUsers: Integer) = repeat(numberOfUsers.toInt, "requestNumber") {
      getRequest(s"Get all users $numberOfUsers times", None, usersUrl, HttpStatus.SC_OK)
    }
  }

  /**
    * Gets all partners
    */
  object GetAllPartners extends AbstractRequest {
    val partnersUrl = "identity/partners"

    def apply() = getRequest("Get all partners", None, partnersUrl, HttpStatus.SC_OK)

    def apply(numberOfPartners: Integer) = repeat(numberOfPartners.toInt, "requestNumber") {
      getRequest(s"Get all partners $numberOfPartners times", None, partnersUrl, HttpStatus.SC_OK)
    }
  }

  /**
    * Gets all applications
    */
  object GetAllApplications extends AbstractRequest {
    val applicationsUrl = "identity/applications"

    def apply() = getRequest("Get all applications", None, applicationsUrl, HttpStatus.SC_OK)

    def apply(numberOfPartners: Integer) = repeat(numberOfPartners.toInt, "requestNumber") {
      getRequest(s"Get all applications $numberOfPartners times", None, applicationsUrl, HttpStatus.SC_OK)
    }
  }

  //  Helpers

  /**
    * Generates random customer
    */
  object generateCustomer {
    def apply() = {
      s"""
          {
            "name": "Company ${randomUtils.randomInt(randomBound)}",
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
            "type": "hotel",
            "headquarters_timezone": "${randomUtils.randomTimezone}"
          }"""
    }
  }


  /**
    * Generates random property
    */
  object generateProperty {
    def apply(session: Session) = {
      val propertyId = randomUtils.randomUUIDAsString

      s"""
         {
           "property_id": "$propertyId",
           "name": "testing property",
           "property_code": "${propertyId}_code",
           "website": "http://example.org",
           "email": "${getValue(session, "customerEmail")}",
           "is_demo_property": true,
           "timezone": "${randomUtils.randomTimezone}",
           "address": {
              "address_line1": "string",
              "city": "string",
              "zip_code": "string",
              "country": "CZ"
              },
           "anchor_customer_id": "${SessionUtils.getValue(session, "customerId")}"
         }
       """
    }
  }

}
