@Identity
@Properties
Feature: Properties Application access check feature
  - Checking when certain application should and should not have access to certain properties
  - Only Properties for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name                        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | type     | username | firstName | lastName | email                | timezone      | languageCode | isActive |
      | customer | user1    | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | name                     | id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given Application permission table is populated for application "App With Subscription"
    Given Application permission table is populated for application "App Without Subscription"
    Given The following application versions exists
      | Id                                   | apiManagerId | name                    | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    Given The following application versions exists
      | id                                   | apiManagerId | name                       | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId   | name                          | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 33300000-0000-4000-a000-000000000111 | salesforceid_1 | property_with_subscription    | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
      | 33300000-0000-4000-a000-000000000222 | salesforceid_2 | property_without_subscription | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 33300000-0000-4000-a000-000000000111 | 22200000-0000-4000-a000-000000000222 |
    Given Relation between user "user1" and property with code "p1_code" exists with is_active "true"
    Given Relation between user "user1" and property with code "p2_code" exists with is_active "true"


    Scenario: GET property by application with and without access
      When Property with code "p1_code" is requested by user "user1" for application version "versionWithSubscription"
      Then Response code is "200"
      When Property with code "p1_code" is requested by user "user1" for application version "versionWithoutSubscription"
      Then Response code is "403"
      When Property with code "p2_code" is requested by user "user1" for application version "versionWithSubscription"
      Then Response code is "404"

  Scenario Outline: Filtering properties with application access checks
    Given The following properties exist with random address and billing address
      | name     | code         | email              | website                    | isDemo         | timezone      | customerId                           | ttiId |
      | p3_name  | p3_code      | p3@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 | 4231  |
      | p4_name  | p4_code      | p4@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 | 5678  |
      | p5_name  | p5_cedo      | p5@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 | 8765  |
      | p6_name  | p6_cedo      | p6@snapshot.travel | http://snapshot.travel     | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 | 1234  |
    Given Relation between user "user1" and property with code "p3_code" exists
    Given Relation between user "user1" and property with code "p4_code" exists
    Given Relation between user "user1" and property with code "p5_cedo" exists
    Given Relation between user "user1" and property with code "p6_cedo" exists
    When List of properties is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "user1" for application version "DefaultVersion"
    Then Response code is "200"
    And There are <returned> properties returned
    When List of properties is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    Examples:
      | limit | cursor | filter                                                     | sort           | sort_desc           | returned    |
      | /null | 0      | name=='*'                                                  | /null          | website             | 6           |
      | /null | 0      | name=='p5_name'                                            | /null          | /null               | 1           |
      | 2     | 0      | website=='*www.*'                                          | /null          | /null               | 2           |
      | /null | 0      | website=='*www.*'                                          | /null          | /null               | 5           |
      | /null | 0      | property_code=='*_code'                                    | property_code  | /null               | 4           |
      | /null | 0      | property_code=='p3_code'                                   | /null          | /null               | 1           |
      | /null | 0      | is_active=='true'                                          | /null          | salesforce_id       | 6           |
      | 5     | 0      | anchor_customer_id=='12300000-0000-4000-a000-000000000000' | /null          | anchor_customer_id  | 5           |
      | /null | 0      | email=='*p3*@snapshot.travel'                              | property_id    | /null               | 1           |

  Scenario: Application with and without access updates property
    When Property with code "p1_code" is updated with data by user "user1" for application version "versionWithoutSubscription"
      | salesforceId   | name         | website                  | email            | isDemo         |
      | updated_sf_id  | updated_name | https://www.upddated.com | updated@email.cz | false          |
    Then Response code is "403"
    When Property with code "p2_code" is updated with data by user "user1" for application version "versionWithSubscription"
      | salesforceId   | name         | website                  | email            | isDemo         |
      | updated_sf_id  | updated_name | https://www.upddated.com | updated@email.cz | false          |
    Then Response code is "404"
    When Property with code "p1_code" is updated with data by user "user1" for application version "versionWithSubscription"
      | salesforceId   | name         | website                  | email            | isDemo         |
      | updated_sf_id  | updated_name | https://www.upddated.com | updated@email.cz | false          |
    Then Response code is "204"

  Scenario: Deleting Property by application with and without access
    When Property with code "p1_code" is deleted by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    When Property with code "p2_code" is deleted by user "user1" for application version "versionWithSubscription"
    Then Response code is "404"
    When Property with code "p1_code" is deleted by user "user1" for application version "versionWithSubscription"
    Then Response code is "409"
    
  Scenario: Anchor_customer_id of customer without commercial subscription cannot be used when creating or updating property
    Given The following customers exist with random address
      | id                                   | name                          | email          | salesforceId   | vatId      | isDemo         | timezone      |
      | 23400000-0000-4000-a000-000000000111 | Customer Without Subscription | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    When The following property is created with random address and billing address for user "user1"
      | salesforceId   | name         | code         | email          | isDemo         | timezone      | customerId                           |
      | salesforceid_3 | p3_name      | p3_code      | p3@tenants.biz | true           | Europe/Prague | 23400000-0000-4000-a000-000000000111 |
    Then Response code is "422"
    And Custom code is 42202
    When Property with code "p1_code" is updated with data by user "user1"
      | customerId                           |
      | 23400000-0000-4000-a000-000000000111 |
    Then Response code is "422"
    And Custom code is 42202

    #      -----------------------------< Second level entities General negative scenarios >------------------------------------

  Scenario Outline: Application without subscription to property sends GET request to all general second level endpoints
    When GET request is sent to "<url>" on module "identity" by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is "40301"
    Examples:
      | url                                                                               |
      | identity/properties/33300000-0000-4000-a000-000000000111/commercial_subscriptions |
      | identity/properties/33300000-0000-4000-a000-000000000111/users                    |
      | identity/properties/33300000-0000-4000-a000-000000000111/customers                |
      | identity/properties/33300000-0000-4000-a000-000000000111/property_sets            |

  Scenario Outline: Unauthorized request - GET request is send to all endpoints without X-Auth-UserId header
    When GET request is sent to "<url>" on module "identity" without X-Auth-AppId header
    Then Response code is "403"
    And Custom code is "40301"
    Examples:
      | url                                                                               |
      | identity/properties                                                               |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/                         |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/commercial_subscriptions |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/users                    |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/customers                |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/property_sets            |