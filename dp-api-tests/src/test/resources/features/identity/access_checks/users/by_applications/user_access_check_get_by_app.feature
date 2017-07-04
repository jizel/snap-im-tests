@Identity
Feature: User access check by app feature - GET
  - Checking when application can have access to users
  - App can only access users when it has commercial subscription

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
    Given Application permission table is populated for application "App With Subscription"
    Given Application permission table is populated for application "App Without Subscription"
    Given The following application versions exists
      | id                                   | apiManagerId | name                       | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription    | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 08000000-0000-4444-8888-000000000001 | 22200000-0000-4000-a000-000000000222 |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | type     | username   | firstName | lastName | email                | timezone      | languageCode | isActive |
      | customer | user1      | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | user2      | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given Relation between user "user1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user2" and property with code "defaultPropertyCode" exists with is_active "false"

  Scenario: Application should not see users if it has no commercial subscription
    When List of users is got by user "user1" for application version "versionWithSubscription"
    Then There are "1" users returned
    When List of users is got by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301

  Scenario: Application does not see users not belonging to the customer it is attached to via commercial subscription
    When List of users is got by user "user1" for application version "versionWithSubscription"
    Then There are "1" users returned
    Given Relation between user "user2" and property "defaultPropertyCode" is activated
    When List of users is got by user "user1" for application version "versionWithSubscription"
    Then There are "2" users returned
    When List of users is got by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
