@Identity
Feature: Property sets Application access check feature
  - Checking when certain application should and should not have access to certain property set
  - Only Property sets for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - Only Property sets for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) to any parent Customer are accessible
  - Only Property sets for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) to any parent Property Set are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-000000000111 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user1    | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | Id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName          | Id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName                | status    | description                | Id                                   |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription    | certified | Active version description | 22200000-0000-4000-a000-000000000222 |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description | 00000000-0000-4000-a000-000000000222 |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "user1"
      | name            | type            | Id                                   |
      | ps1_name        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000111" and user "user1"
      | name            | type            | Id                                   |
      | ps2_name        | brand           | 23400000-1111-4c57-91bd-30230d2c1bd0 |
    Given The following commercial subscriptions exist
      | Id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | Id                                   | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |

    Scenario: Only property sets with valid commercial subscription are accessible
      When Property set "ps1_name" is requested by user "user1" for application version "versionWithSubscription"
      Then Response code is "200"
      When Property set "ps1_name" is requested by user "user1" for application version "versionWithoutSubscription"
      Then Response code is "404"
      And Custom code is 40402
      When Property set "ps2_name" is requested by user "user1" for application version "versionWithSubscription"
      Then Response code is "404"
      And Custom code is 40402

    Scenario: Application has access to any child property set of child customer if it has access to parent PS and Customer
      Given The following customers exist with random address
        | parentId                             | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
        | 12300000-0000-4000-a000-000000000000 | 22245678-0000-4000-a000-000000000000 | Company 222 | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | Europe/Prague |
        | 22245678-0000-4000-a000-000000000000 | 33345678-0000-4000-a000-000000000000 | Company 333 | c3@tenants.biz | salesforceid_3 | CZ10000003 | true           | Europe/Prague |
      Given The following property sets exist for customer with id "33345678-0000-4000-a000-000000000000" and user "user1"
        | name            | type            | parentId                             | Id                                   |
        | childPS1        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
        | childPS2        | brand           | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 | d119e3b0-69bf-4c57-91bd-30230d2c1bd1 |
      When Property set "childPS2" is requested by user "user1" for application version "versionWithSubscription"
      Then Response code is "200"
      When Property set "childPS2" is requested by user "user1" for application version "versionWithoutSubscription"
      Then Response code is "404"
      And Custom code is 40402

  Scenario Outline: Filtering property sets with application access checks
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "user1"
      | name            | type            | Id                                   | description            | isActive |
      | ps2_name        | brand           | 23400000-1111-4c57-91bd-30230d2c1bd1 | desc2                  | true     |
      | ps3_name        | HOTEL_TYPE      | 33400000-1111-4c57-91bd-30230d2c1bd0 | desc3                  | false    |
      | ps4_name        | GEOLOCATION     | 43400000-1111-4c57-91bd-30230d2c1bd0 | desc4                  | false    |
    When List of property sets is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "user1" for application version "versionWithSubscription"
    Then Response code is "200"
    And There are <returned> property sets returned
    When List of property sets is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "200"
    And Total count is "0"
    Examples:
      | limit | cursor | filter                        | sort        | sort_desc         | returned    |
      | /null | 0      | name=='*'                     | /null       | name              | 5           |
      | /null | 0      | name=='ps4_name'              | /null       | /null             | 1           |
      | /null | 0      | is_active=='true'             | /null       | property_set_type | 3           |
      | /null | 0      | property_set_type=='brand'    | /null       | property_set_type | 3           |
      | /null | 0      | description=='desc*'          | description | /null             | 3           |
      | /null | 0      | property_set_id=='43400000-*' | /null       | /null             | 1           |

  Scenario: Application with and without subscription updates property set
    When Property set "ps1_name" is updated with following data by user "user1"
      | name            | description            | type              |
      | updated_name    | updated description    | geolocation       |
    Then Response code is "404"
    And Custom code is 40402
    When Property set "ps1_name" is updated with following data by user "user1" for application version "versionWithSubscription"
      | name            | description            | type              |
      | updated_name    | updated description    | geolocation       |
    Then Response code is "204"

  Scenario: Deleting Property set by Application with and without subscription
    When Property set "ps1_name" is deleted by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "404"
    And Custom code is 40402
    When Property set "ps2_name" is deleted by user "user1" for application version "versionWithSubscription"
    Then Response code is "404"
    And Custom code is 40402
    When Property set "ps1_name" is deleted by user "user1" for application version "versionWithSubscription"
#    User cannot delete entity when he has relation with it (but if he hasn't he gets 404). 409 is enough to see that access checks work
    Then Response code is "409"
    And Custom code is 40915

    #      -----------------------------< General negative scenarios >------------------------------------

  Scenario Outline: User with no access rights to property sends GET request to all general second level endpoints
    When GET request is sent to "<url>" on module "identity" by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "404"
    And Custom code is "40402"
    Examples:
      | url                                                                       |
      | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/users         |
      | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/properties    |
      | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/property_sets |

  Scenario Outline: Unauthorized request - GET request is send to all endpoints without X-Auth-AppId header
    When GET request is sent to "<url>" on module "identity" without X-Auth-UserId header
    Then Response code is "403"
    And Custom code is "40301"
    Examples:
      | url                                                                       |
      | identity/property_sets                                                    |
      | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/              |
      | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/users         |
      | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/properties    |
      | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/property_sets |
