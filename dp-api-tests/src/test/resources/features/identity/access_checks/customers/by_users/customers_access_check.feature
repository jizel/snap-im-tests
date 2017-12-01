@Identity
Feature: Customers access check feature - GET
  - Checking when certain user should and should not have access to certain customers
  - User should have access to a customer (instance) when
    - User has a relationship to the instance
    - User is a member of a UserGroup which has a relationship to the instance
    - User has a relationship to customer whose child has a relationship to the requested customer
  - User type Snapshot has access to all entities (other user types are equal)
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)
  - User can update/delete specific customer only if he has a relationship
    - Check positive scenarios
    - In negative tests, ETAG needs to be obtained by different user first

  Background:
  Given Database is cleaned and default entities are created
  Given The following customers exist with random address
    | Id                                   | name        | email          | vatId      | isDemo         | phone         | website                    | timezone      |
    | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    | 00000000-0000-4000-8000-123000000abc | Company 2   | c2@tenants.biz | CZ10000002 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
  Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false" with is_active "false"
    | type     | username      | firstName | lastName | email                | timezone      | languageCode | isActive |
    | customer | userWithCust1 | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
  Given The following users exist for customer "00000000-0000-4000-8000-123000000abc" as primary "false"
    | type     | username      | firstName | lastName | email                | timezone      | languageCode | isActive |
    | customer | userWithCust2 | Customer  | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |


    #      -----------------------------< General negative scenarios for second level endpoints >------------------------------------

    @skipped
    Scenario Outline: User with no access rights to property sends GET request to all general second level endpoints
      When GET request is sent to "<url>" on module "identity" by user "userWithCust2"
      Then Response code is "404"
      And Custom code is "40402"
      Examples:
        | url                                                                              |
        | identity/customers/12300000-0000-4000-a000-000000000000/commercial_subscriptions |
        | identity/customers/12300000-0000-4000-a000-000000000000/users                    |
        | identity/customers/12300000-0000-4000-a000-000000000000/properties               |
        | identity/customers/12300000-0000-4000-a000-000000000000/property_sets            |


   Scenario Outline: User with no access rights to property sends GET request with parameters
     When GET request is sent to "<url>" on module "identity" by user "userWithCust2"
     Then Response code is "404"
     And Custom code is "40402"
     Examples:
       | url                                                                                                                            |
       | identity/customers/12300000-0000-4000-a000-000000000000/commercial_subscriptions?filter=is_active=='false'&sort=customer_id    |
       | identity/customers/12300000-0000-4000-a000-000000000000/users?sortDesc=user_id&cursor=0                                        |
       | identity/customers/12300000-0000-4000-a000-000000000000/properties?limit=55&filter=property_code=='*'                          |
       | identity/customers/12300000-0000-4000-a000-000000000000/property_sets?filter=property_id=='*'&sort='property_set_id'           |


    Scenario Outline: Unauthorized request - GET request is send to all endpoints without X-Auth-UserId header
      When GET request is sent to "<url>" on module "identity" without X-Auth-UserId header
      Then Response code is "403"
      And Custom code is "40301"
      When GET request is sent to "<url>" on module "identity" with empty X-Auth-UserId header
      Then Response code is "403"
      And Custom code is "40301"
      Examples:
        | url                                                                              |
        | identity/customers                                                               |
        | identity/customers/12300000-0000-4000-a000-000000000000/                         |
        | identity/customers/12300000-0000-4000-a000-000000000000/commercial_subscriptions |
        | identity/customers/12300000-0000-4000-a000-000000000000/users                    |
        | identity/customers/12300000-0000-4000-a000-000000000000/properties               |
        | identity/customers/12300000-0000-4000-a000-000000000000/property_sets            |
