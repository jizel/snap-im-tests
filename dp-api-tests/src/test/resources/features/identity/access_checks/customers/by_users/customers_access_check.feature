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
    | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
    | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    | 00000000-0000-4000-8000-123000000abc | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
  Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false" with is_active "false"
    | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
    | customer | userWithCust1 | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
  Given The following users exist for customer "00000000-0000-4000-8000-123000000abc" as primary "false" with is_active "false"
    | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
    | customer | userWithCust2 | Customer  | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
  Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
  Given API subscriptions exist for default application and customer with id "00000000-0000-4000-8000-123000000abc"


    Scenario: User has direct relationship to customer
      When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1"
      Then Response code is "404"
      Given Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is activated
      When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1"
      Then Response code is "200"
      When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust2"
      Then Response code is "404"
      And Custom code is 40402


    Scenario: User belongs to User Group that has access to customer
      Given The following user groups exist
        | Id                                   | customerId                           | name        | isActive |
        | a8b40d08-de38-4246-bb69-ad39c31c025c | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    |
      When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust2"
      Then Response code is "404"
      When User "userWithCust2" is added to userGroup "userGroup_1" with is_active "false"
      When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust2"
      Then Response code is "404"
      Given Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is activated
      When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust2"
      Then Response code is "404"
      Given Relation between user "userWithCust2" and group "userGroup_1" is activated
      When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust2"
      Then Response code is "200"


    Scenario: User has a relationship to any customer that has a successor that has a successor which is the requested customer
      Given The following customers exist with random address
        | parentId                             | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
        | 12300000-0000-4000-a000-000000000000 | 22245678-0000-4000-a000-000000000000 | Company 222 | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | Europe/Prague |
        | 22245678-0000-4000-a000-000000000000 | 33345678-0000-4000-a000-000000000000 | Company 333 | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | Europe/Prague |
      When Customer with customerId "33345678-0000-4000-a000-000000000000" is requested by user "userWithCust1"
      Then Response code is "404"
      Given Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is activated
      When Customer with customerId "33345678-0000-4000-a000-000000000000" is requested by user "userWithCust1"
      Then Response code is "200"
      Given Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is deactivated
      When Customer with customerId "33345678-0000-4000-a000-000000000000" is requested by user "userWithCust1"
      Then Response code is "404"


    Scenario: User belongs to User Group that has a relation to customer that whose successor is requested
      Given The following user groups exist
        | Id                                   | customerId                           | name        | isActive |
        | a8b40d08-de38-4246-bb69-ad39c31c025c | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    |
      Given The following customers exist with random address
        | parentId                             | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
        | 12300000-0000-4000-a000-000000000000 | 22245678-0000-4000-a000-000000000000 | Company 222 | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | Europe/Prague |
      When Customer with customerId "22245678-0000-4000-a000-000000000000" is requested by user "userWithCust2"
      Then Response code is "404"
      When User "userWithCust2" is added to userGroup "userGroup_1" with is_active "false"
      When Customer with customerId "22245678-0000-4000-a000-000000000000" is requested by user "userWithCust2"
      Then Response code is "404"
      Given Relation between user "userWithCust2" and group "userGroup_1" is activated
      When Customer with customerId "22245678-0000-4000-a000-000000000000" is requested by user "userWithCust2"
      Then Response code is "200"

    Scenario: User type Snapshot has access to all entities (other user types are equal)
      Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
        | userType | userName  | firstName | lastName | email                | timezone      | culture | isActive |
        | snapshot | snapshot1 | Snapshot1 | User1    | sna1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
        | guest    | guest1    | Guest1    | User1    | gue1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
        | partner  | partner1  | Partner1  | User1    | par1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      When Customer with customerId "00000000-0000-4000-8000-123000000abc" is requested by user "snapshot1"
      Then Response code is "200"
      When Customer with customerId "00000000-0000-4000-8000-123000000abc" is requested by user "guest1"
      Then Response code is "404"
      And Custom code is 40402
      When Customer with customerId "00000000-0000-4000-8000-123000000abc" is requested by user "partner1"
      Then Response code is "404"
      And Custom code is 40402


     Scenario Outline: Filtering customers with access checks
       Given The following customers exist with random address
         | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
         | 23445678-0000-4000-a000-000000000000 | Company 3   | c3@tenants.biz | salesforceid_3 | CZ10000003 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
         | 34545678-0000-4000-a000-000000000000 | Company 4   | c4@tenants.biz | salesforceid_4 | CZ10000004 | false          | +420987654321 | http://www.snapshot.travel | Europe/Prague |
         | 45645678-0000-4000-a000-000000000000 | Company 5   | c5@tenants.biz | salesforceid_5 | CZ10000005 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
       Given API subscriptions exist for default application and customer with id "23445678-0000-4000-a000-000000000000"
       Given API subscriptions exist for default application and customer with id "34545678-0000-4000-a000-000000000000"
       Given API subscriptions exist for default application and customer with id "45645678-0000-4000-a000-000000000000"
       Given Relation between user "userWithCust1" and customer "12300000-0000-4000-a000-000000000000" is activated
       Given Relation between user "userWithCust1" and customer with id "23445678-0000-4000-a000-000000000000" exists with isPrimary "true"
       Given Relation between user "userWithCust1" and customer with id "34545678-0000-4000-a000-000000000000" exists with isPrimary "true"
       Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
       Given API subscriptions exist for default application and customer with id "23445678-0000-4000-a000-000000000000"
       Given API subscriptions exist for default application and customer with id "34545678-0000-4000-a000-000000000000"
       Given API subscriptions exist for default application and customer with id "45645678-0000-4000-a000-000000000000"
       When List of customers is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithCust1"
       Then Response code is "200"
       And There are <returned> customers returned

       Examples:
         | limit | cursor | filter                          | sort           | sort_desc           | returned    |
         | /null | 0      | name=='*'                       | /null          | website             | 3           |
         | /null | 0      | name=='Company 4'               | /null          | /null               | 1           |
         | 1     | 0      | website=='*www.*'               | /null          | /null               | 1           |
         | /null | 0      | is_demo_customer=='true'        | /null          | salesforce_id       | 2           |
         | /null | 0      | salesforce_id=='salesforceid_3' | /null          | /null               | 1           |
         | /null | 0      | email=='*@tenants.biz'          | website        | /null               | 2           |
         | /null | 0      | vat_id=='CZ10000005'            | website        | /null               | 0           |
         | /null | 0      | customer_id=='23445678-*'       | salesforce_id  | /null               | 1           |


  Scenario: User with access updates customer
    When Customer with id "12300000-0000-4000-a000-000000000000" is updated with data by user "userWithCust1"
      | companyName   | email               | salesforceId   | vatId      | phone         | website                           |
      | updatedName   | updated@tenants.biz | updated_sf_id  | CZ01111110 | +420999666999 | http://www.update.snapshot.travel |
    Then Response code is "404"
    Given Relation between user "userWithCust1" and customer "12300000-0000-4000-a000-000000000000" is activated
    When Customer with id "12300000-0000-4000-a000-000000000000" is updated with data by user "userWithCust1"
      | companyName   | email               | salesforceId   | vatId      | phone         | website                           |
      | updatedName   | updated@tenants.biz | updated_sf_id  | CZ01111110 | +420999666999 | http://www.update.snapshot.travel |
    Then Response code is "204"
    When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1"
    Then Response code is "200"
    And Body contains entity with attribute "name" value "updatedName"
    And Body contains entity with attribute "salesforce_id" value "updated_sf_id"
    And Body contains entity with attribute "website" value "http://www.update.snapshot.travel"
    And Body contains entity with attribute "email" value "updated@tenants.biz"
    And Body contains entity with attribute "vat_id" value "CZ01111110"

  Scenario: User without access tries to update customer
    When Customer with id "12300000-0000-4000-a000-000000000000" is updated with data by user "userWithCust2"
      | companyName   | email               | salesforceId   | vatId      | phone         | website                           |
      | updatedName   | updated@tenants.biz | updated_sf_id  | CZ01111110 | +420999666999 | http://www.update.snapshot.travel |
    Then Response code is "404"

  Scenario: Deleting Customer by user with access
    When Customer with id "12300000-0000-4000-a000-000000000000" is deleted by user "userWithCust1"
    Then Response code is 204
    And Body is empty
    And Customer with id "12300000-0000-4000-a000-000000000000" doesn't exist

  Scenario: Deleting Customer by user without access to it
    When Customer "12300000-0000-4000-a000-000000000000" is deleted by user "userWithCust2"
    Then Response code is 404
    When Customer "12300000-0000-4000-a000-000000000000" is deleted by user "userWithCust1"
    Then Response code is "404"
    Given Relation between user "userWithCust1" and customer "12300000-0000-4000-a000-000000000000" is activated
    When Customer "12300000-0000-4000-a000-000000000000" is deleted by user "userWithCust1"
    Then Response code is "409"

  Scenario: User loses access to customer when relation is deleted - DP-1811
    Given Relation between user "userWithCust1" and customer "12300000-0000-4000-a000-000000000000" is deleted
    When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1"
    Then Response code is "404"
    And Custom code is 40402
    Given Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" exists with isPrimary "true"
    When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1"
    Then Response code is "200"

#      -----------------------------< General negative scenarios for second level endpoints >------------------------------------

    @skipped
    Scenario Outline: User with no access rights to property sends GET request to all general second level endpoints
      When GET request is sent to "<url>" on module "identity" by user "userWithCust2"
      Then Response code is "404"
      And Custom code is "40402"
      Examples:
        | url                                                                              |
        | identity/customers/12300000-0000-4000-a000-000000000000/api_subscriptions        |
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
       | identity/customers/12300000-0000-4000-a000-000000000000/api_subscriptions?sort=application_version_id&filter=is_active=='true' |
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
        | identity/customers/12300000-0000-4000-a000-000000000000/api_subscriptions        |
        | identity/customers/12300000-0000-4000-a000-000000000000/commercial_subscriptions |
        | identity/customers/12300000-0000-4000-a000-000000000000/users                    |
        | identity/customers/12300000-0000-4000-a000-000000000000/properties               |
        | identity/customers/12300000-0000-4000-a000-000000000000/property_sets            |