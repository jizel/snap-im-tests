@Identity
Feature: User Customers Role by app access check feature
  Users must have access only to user-customer relations of their customer by app that has commercial subscription

  Background:
    Given Database is cleaned and default entities are created
    Given The following partner exist
      | Id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following customers exist with random address
      | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-000000000001 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456780 | http://www.snapshot.com | Europe/Prague |
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
      | customer | user1OfC1  | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | user2OfC1  | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000001" as primary "false"
      | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user1OfC2  | Customer  | User1C2  | usr1@snapshot.com    | Europe/Prague | cs-CZ   | true     |
    Given Relation between user "user1OfC1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user2OfC1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user1OfC2" and property with code "defaultPropertyCode" exists with is_active "true"

  Scenario: User can view only list of customer-user roles of his own customer (with active relation)
    Given Switch for user customer role tests
    Given The following roles exist
      | applicationId                        | roleName | description      |
      | 11111111-0000-4000-a000-111111111111 | NewRole  | Some description |
    When User "user1OfC1" requests roles of user "user1OfC1" for customer "12300000-0000-4000-a000-000000000000" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1OfC1" requests roles of user "user2OfC1" for customer "12300000-0000-4000-a000-000000000000" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1OfC1" requests roles of user "user1OfC1" for customer "12300000-0000-4000-a000-000000000000" for application version "versionWithSubscription"
    Then Response code is "200"
    And Total count is "0"

  Scenario: User can assign and revoke roles to customer-users only when accessing app has subscription
    Given Switch for user customer role tests
    Given The following roles exist
      | applicationId                        | roleName | description      |
      | 11111111-0000-4000-a000-111111111111 | NewRole  | Some description |
    When User "user1OfC1" assigns role "NewRole" to relation between user "user1OfC1" and customer "12300000-0000-4000-a000-000000000000" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1OfC1" assigns role "NewRole" to relation between user "user1OfC1" and customer "12300000-0000-4000-a000-000000000000" for application version "versionWithSubscription"
    Then Response code is "201"
    When User "user1OfC1" deletes role "NewRole" from relation between user "user1OfC1" and customer "12300000-0000-4000-a000-000000000000" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1OfC1" deletes role "NewRole" from relation between user "user1OfC1" and customer "12300000-0000-4000-a000-000000000000" for application version "versionWithSubscription"
    Then Response code is "204"
