@Identity
Feature: Customers Application access check feature - GET
  - Checking when certain application should and should not have access to certain customers
  - Only Customers for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - Only Customers for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) to any parent Customer are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name                        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 00000000-0000-4000-8000-123000000abc | CustomerWithoutSubscription | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | type     | username      | firstName | lastName | email                | timezone      | languageCode | isActive |
      | customer | userWithCust1 | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | name                     | id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | id                                   | apiManagerId | name                    | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    Given The following application versions exists
      | id                                   | isNonCommercial | apiManagerId | name                       | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000444 | false           | 2            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
      | 22200000-0000-4000-a000-000000000555 | true            | 3            | nonCommercialversion       | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    And Relation between user "userWithCust1" and default property exists


  Scenario: There is active CommercialSubscription linking to the ApplicationVersion (through Application)
    When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "200"
    When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "nonCommercialversion"
    Then Response code is "200"
    When Customer with customerId "00000000-0000-4000-8000-123000000abc" is requested by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "404"
    When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "403"

  Scenario: There is active CommercialSubscription with parent customer entity
    Given The following customers exist with random address
      | parentId                             | id                                   | name        | email          | salesforceId   | vatId      | isDemo         | timezone      |
      | 12300000-0000-4000-a000-000000000000 | 22245678-0000-4000-a000-000000000000 | Company 222 | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | Europe/Prague |
      | 22245678-0000-4000-a000-000000000000 | 33345678-0000-4000-a000-000000000000 | Company 333 | c3@tenants.biz | salesforceid_3 | CZ10000003 | true           | Europe/Prague |
    When Customer with customerId "33345678-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "200"
    When Customer with customerId "33345678-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "nonCommercialversion"
    Then Response code is "200"
    When Customer with customerId "33345678-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "403"

  Scenario Outline: Filtering customers with application access checks
    Given The following customers exist with random address
      | id                                   | name           | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 23445678-0000-4000-a000-000000000000 | Company 3      | c3@tenants.biz | salesforceid_3 | CZ10000003 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 34545678-0000-4000-a000-000000000000 | Company 4      | c4@tenants.biz | salesforceid_4 | CZ10000004 | false          | +420987654321 | http://www.snapshot.travel | Europe/Prague |
      | 45645678-0000-4000-a000-000000000000 | NoSubscription | c5@tenants.biz | salesforceid_5 | CZ10000005 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
    Given Relation between user "userWithCust1" and customer with id "23445678-0000-4000-a000-000000000000" exists with isPrimary "true"
    Given Relation between user "userWithCust1" and customer with id "34545678-0000-4000-a000-000000000000" exists with isPrimary "true"
    Given Relation between user "userWithCust1" and customer with id "45645678-0000-4000-a000-000000000000" exists with isPrimary "true"
    When List of customers is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "200"
    And There are <returned> customers returned
    When List of customers is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithCust1" for application version "nonCommercialversion"
    Then Response code is "200"

    Examples:
      | limit | cursor | filter                          | sort           | sort_desc           | returned    |
      | /null | 0      | name=='*'                       | /null          | website             | 3           |
      | /null | 0      | name=='Company 5'               | /null          | /null               | 0           |
      | 2     | 0      | website=='*www.*'               | /null          | /null               | 2           |
      | /null | 0      | is_demo_customer=='true'        | /null          | salesforce_id       | 2           |
      | /null | 0      | email=='*@tenants.biz'          | website        | /null               | 3           |
      | /null | 0      | vat_id=='CZ10000005'            | website        | /null               | 0           |
      | /null | 0      | customer_id=='23445678-*'       | salesforce_id  | /null               | 1           |

  Scenario: Update customer with and without application access
    When Customer with id "12300000-0000-4000-a000-000000000000" is updated with data by user "userWithCust1" for application version "versionWithSubscription"
      | name          | email               | salesforceId   | vatId        | phone         | website                           |
      | updatedName   | updated@tenants.biz | updated_sf_id  | CZ0123456789 | +420999666999 | http://www.update.snapshot.travel |
    Then Response code is "204"
    When Customer with customerId "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "200"
    And Body contains entity with attribute "name" value "updatedName"
    And Body contains entity with attribute "website" value "http://www.update.snapshot.travel"
    And Body contains entity with attribute "email" value "updated@tenants.biz"
    And Body contains entity with attribute "vat_id" value "CZ0123456789"
    When Customer with id "12300000-0000-4000-a000-000000000000" is updated with data by user "userWithCust1" for application version "versionWithoutSubscription"
      | name          | email               | salesforceId   | vatId      | phone         | website                           |
      | updatedName   | updated@tenants.biz | updated_sf_id  | CZ11223344 | +420999666999 | http://www.update.snapshot.travel |
    Then Response code is "403"
    And Custom code is 40301
    When Customer with id "12300000-0000-4000-a000-000000000000" is updated with data by user "userWithCust1" for application version "nonCommercialversion"
      | name                       |
      | updatedNameByNonCommercial |
    Then Response code is "204"

  Scenario: Deleting Customer by application with and without access
    When Customer with id "12300000-0000-4000-a000-000000000000" is deleted by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is 403
    And Custom code is 40301
    When Customer with id "12300000-0000-4000-a000-000000000000" is deleted by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is 409
    And Custom code is 40915
    When Customer with id "12300000-0000-4000-a000-000000000000" is deleted by user "userWithCust1" for application version "nonCommercialversion"
    Then Response code is 409

  Scenario Outline: Application with no access rights to property sends GET request to all general second level endpoints
    When GET request is sent to "<url>" on module "identity" by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is "40301"
    When GET request is sent to "<url>" on module "identity" by user "userWithCust1" for application version "nonCommercialversion"
    Then Response code is "200"
    Examples:
      | url                                                                              |
      | identity/customers/12300000-0000-4000-a000-000000000000/commercial_subscriptions |
      | identity/customers/12300000-0000-4000-a000-000000000000/users                    |
      | identity/customers/12300000-0000-4000-a000-000000000000/properties               |
      | identity/customers/12300000-0000-4000-a000-000000000000/property_sets            |

  Scenario Outline: Unauthorized request - GET request is send to all endpoints without X-Auth-UserId header
    When GET request is sent to "<url>" on module "identity" without X-Auth-AppId header
    Then Response code is "403"
    And Custom code is "40301"
    When GET request is sent to "<url>" on module "identity" with empty X-Auth-AppId header
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