@Identity
Feature: User access check by app feature - GET
  - Checking when application can have access to users
  - App can only access users when it has commercial subscription

  Background:
    Given Database is cleaned and default entities are created
    Given The following partner exist
      | Id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following applications exist
      | applicationName          | Id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName                | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription    | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | Id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | Id                                   | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user1      | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | user2      | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
     #    Must be here - DP-1846
    Given Relation between user "user1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user2" and property with code "defaultPropertyCode" exists with is_active "false"

  Scenario: Application should not see users if it has no commercial subscription
    When List of users is got by user "user1" for application version "versionWithSubscription"
    Then There are "1" users returned
    When List of users is got by user "user1" for application version "versionWithoutSubscription"
    Then There are "0" users returned

  Scenario: Application sees only users that have active connection to the property this application is attached to
    Given The following commercial subscriptions exist
      |                       Id             | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000555 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 00000000-0000-4000-a000-000000000222 |
    When List of users is got by user "user2" for application version "versionWithoutSubscription"
    Then There are "1" users returned
    Given Relation between user "user2" and property "defaultPropertyCode" is activated
    When List of users is got by user "user2" for application version "versionWithoutSubscription"
    Then There are "2" users returned

  Scenario: Application does not see users not belonging to the customer it is attached to via commercial subscription
    Given Relation between user "user2" and property "defaultPropertyCode" is activated
    And Relation between user "user2" and customer with id "12300000-0000-4000-a000-000000000000" is inactivated
    When List of users is got by user "user1" for application version "versionWithSubscription"
    Then There are "1" users returned
    Given Relation between user "user2" and customer with id "12300000-0000-4000-a000-000000000000" is activated
    When List of users is got by user "user1" for application version "versionWithSubscription"
    Then There are "2" users returned
