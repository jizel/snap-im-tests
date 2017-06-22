@Identity
Feature: User-property access check by app feature - GET
  - Checking when application can have access to users-properties

  Background:
    Given Database is cleaned and default entities are created
    Given The following partner exist
      | id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following customers exist with random address
      | id                                   | name        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following applications exist
      | name                     | id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | id                                   | apiManagerId | name                       | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription    | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | type     | username | firstName | lastName | email                | timezone      | languageCode | isActive |
      | customer | user1    | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | user2    | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given Relation between user "user1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user2" and property with code "defaultPropertyCode" exists with is_active "false"
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 22222222-0000-4000-a000-666666666666 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.com    | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given Relation between user "user1" and property with code "p1_code" exists

  Scenario: Application can view only list of property-users of accessible customer and property
    When User "user1" requests list of users for property "p1_code" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1" requests list of users for property "p1_code" for application version "versionWithSubscription"
    Then Response code is "404"
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 55500000-0000-4000-a000-000000000555 | 12300000-0000-4000-a000-000000000000 | 22222222-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    When User "user1" requests list of users for property "p1_code" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1" requests list of users for property "p1_code" for application version "versionWithSubscription"
    Then Response code is "200"
    And There are "1" users returned

  Scenario: Application can add-remove users from properties only when it has access to them both
    When User "user1" adds user "user2" to property "p1_code" for application version "versionWithoutSubscription"
    # Property not found
    Then Response code is "403"
    And Custom code is 40301
    When User "user1" adds user "user2" to property "p1_code" for application version "versionWithSubscription"
    # Property not found
    Then Response code is "404"
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 55500000-0000-4000-a000-000000000555 | 12300000-0000-4000-a000-000000000000 | 22222222-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    When User "user1" adds user "user2" to property "p1_code" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1" adds user "user2" to property "p1_code" for application version "versionWithSubscription"
    # User not found
    Then Response code is "422"
    Given Relation between user "user2" and property "defaultPropertyCode" is activated
    When User "user1" adds user "user2" to property "p1_code" for application version "versionWithSubscription"
    Then Response code is "201"
    When User "user1" deletes user "user2" from property "p1_code" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1" deletes user "user2" from property "p1_code" for application version "versionWithSubscription"
    Then Response code is "204"
