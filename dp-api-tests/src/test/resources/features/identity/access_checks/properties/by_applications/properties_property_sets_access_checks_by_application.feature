@Identity
@Properties
Feature: Properties-Property Sets Application access check feature
  - Checking when certain application should and should not have access to certain properties
  - Only Property Sets of Properties for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users

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
      | id                                   | apiManagerId | name                    | status    | description                  | applicationId                        |
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
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "user1" with is_active "false"
      | name            | type            |
      | prop_set1       | brand           |
    When Property with code "p1_code" is added to property set "prop_set1"
    When Property with code "p2_code" is added to property set "prop_set1"

  Scenario: Application can only access property sets for property with valid commercial subscription
    When Property set with name "prop_set1" for property with code "p1_code" is requested by user "user1" for application version "versionWithSubscription"
    Then Response code is "200"
    When Property set with name "prop_set1" for property with code "p1_code" is requested by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When Property set with name "prop_set1" for property with code "p2_code" is requested by user "user1" for application version "versionWithSubscription"
    Then Response code is "404"
    And Custom code is 40402
    When List of all property sets is got for property with code "p1_code" by user "user1" for application version "versionWithSubscription"
    Then Response code is "200"
    And Total count is "1"
    When List of all property sets is got for property with code "p1_code" by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301


  Scenario: Update property - propertySet relationship by application with and without access
    When  Relation between property with code "p1_code" and property set "prop_set1" is updated by user "user1" for application version "versionWithoutSubscription" with
      | isActive |
      | true     |
    Then Response code is "403"
    And Custom code is 40301
    When  Relation between property with code "p1_code" and property set "prop_set1" is updated by user "user1" for application version "versionWithSubscription" with
      | isActive |
      | true     |
    Then Response code is "204"

  Scenario: Delete property - PropertySet relationship by application with and without access
    When Relation between property with code "p1_code" and property set "prop_set1" is deleted by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When Relation between property with code "p2_code" and property set "prop_set1" is deleted by user "user1" for application version "versionWithSubscription"
    Then Response code is "404"
    And Custom code is 40402
    When Relation between property with code "p1_code" and property set "prop_set1" is deleted by user "user1" for application version "versionWithSubscription"
    Then Response code is "204"
