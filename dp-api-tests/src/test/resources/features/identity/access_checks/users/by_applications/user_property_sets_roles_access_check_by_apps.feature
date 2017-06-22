@Identity
Feature: User-propertyset roles access check by app feature
  - Checking when application can have access to users-propertyset roles

  Background:
    Given Database is cleaned and default entities are created
    Given The following partner exist
      | id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following customers exist with random address
      | id                                   | name        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-111111111111 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456790 | http://www.snapshot.stay   | Europe/Prague |
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
    Given Switch for user property set role tests
    Given The following roles exist
      | id                                   | roleName | description      |
      | 11111111-0000-4000-a000-111111111111 | NewRole  | Some description |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | type     | username  | firstName | lastName | email                | timezone      | languageCode | isActive |
      | customer | user1     | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | user2     | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "user1"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
    And Relation between property with code "defaultPropertyCode" and property set with name "ps1_name" exists
    Given Relation between user "user1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user2" and property with code "defaultPropertyCode" exists with is_active "false"

  Scenario: App can view only list of propertyset-user roles of customer and property set it has access to
    Given Relation between user "user2" and property set "ps1_name" exists
    When User "user1" requests roles of user "user2" for property set "ps1_name" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1" requests roles of user "user2" for property set "ps1_name" for application version "versionWithSubscription"
    Then Response code is "404"
    Given Relation between user "user2" and property "defaultPropertyCode" is activated
    When User "user1" requests roles of user "user2" for property set "ps1_name" for application version "versionWithSubscription"
    Then Response code is "200"

  Scenario: Application can assign and revoke roles to propertyset-users only when it has access to both user and propertyset
    When User "user1" assigns role "NewRole" to relation between user "user1" and property set "ps1_name" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1" assigns role "NewRole" to relation between user "user1" and property set "ps1_name" for application version "versionWithSubscription"
    Then Response code is "201"
    When User "user1" deletes role "NewRole" from relation between user "user1" and property set "ps1_name" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1" deletes role "NewRole" from relation between user "user1" and property set "ps1_name" for application version "versionWithSubscription"
    Then Response code is "204"
