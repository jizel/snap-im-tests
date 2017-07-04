@Identity
Feature: User-property roles access check by app feature - GET
  - Checking when application can have access to users-propertiy roles

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
    Given Application permission table is populated for application "App With Subscription"
    Given Application permission table is populated for application "App Without Subscription"
    Given The following application versions exists
      | id                                   | apiManagerId | name                       | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription    | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 08000000-0000-4444-8888-000000000001 | 22200000-0000-4000-a000-000000000222 |
    Given Switch for user property role tests
    Given The following roles exist
      | id                                   | applicationId                        | roleName | description      |
      | 0d07159e-855a-4fc3-bcf2-a0cdbf54a44d | 03000000-0000-4444-8888-000000000000 | NewRole  | Some description |
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

  @skipped
  Scenario: Application can view only list of property-user roles of the customer and property it can access through a CS
    When User "user1" requests roles of user "user2" for property "defaultPropertyCode" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1" requests roles of user "user2" for property "defaultPropertyCode" for application version "versionWithSubscription"
    Then Response code is "404"
    Given Relation between user "user2" and property "defaultPropertyCode" is activated
    When User "user1" requests roles of user "user2" for property "defaultPropertyCode" for application version "versionWithSubscription"
    Then Response code is "200"
    # Now let's give the second app access to the same property and let it try accessing user-property roles for the user of the inaccessible customer
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 55500000-0000-4000-a000-000000000555 | 12300000-0000-4000-a000-111111111111 | 08000000-0000-4444-8888-000000000001 | 00000000-0000-4000-a000-000000000222 |
    And Relation between user "user1" and customer with id "12300000-0000-4000-a000-111111111111" exists
    When User "user1" requests roles of user "user2" for property "defaultPropertyCode" for application version "versionWithoutSubscription"
    Then Response code is "404"

  Scenario: Application can assign and revoke roles to property-users only when it has access to both user and property
    Given Switch for user property role tests
    When User "user1" assigns role "NewRole" to relation between user "user1" and property "defaultPropertyCode" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "user1" assigns role "NewRole" to relation between user "user1" and property "defaultPropertyCode" for application version "versionWithSubscription"
    Then Response code is "201"

  Scenario: Meaningful error messages at attempts to attach role of wrong type
    Given Switch for user property set role tests
    Given The following roles exist
      | id                                   | applicationId                        | roleName  | description      |
      | 1d07159e-855a-4fc3-bcf2-a0cdbf54a44d | 03000000-0000-4444-8888-000000000000 | FalseRole | Some description |
    When User "defaultSnapshotUser" assigns role "1d07159e-855a-4fc3-bcf2-a0cdbf54a44d" to relation between user "user1" and property "defaultPropertyCode" for application version "versionWithSubscription"
    Then Response code is "422"
    And Body contains property with attribute "message" value "Reference does not exist. The entity PropertyRole with ID 1d07159e-855a-4fc3-bcf2-a0cdbf54a44d cannot be found."

