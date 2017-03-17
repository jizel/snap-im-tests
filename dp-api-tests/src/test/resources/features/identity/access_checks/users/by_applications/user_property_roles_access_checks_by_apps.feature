@Identity
Feature: User-property roles access check by app feature - GET
  - Checking when application can have access to users-propertiy roles

  Background:
    Given Database is cleaned and default entities are created
    Given The following partner exist
      | Id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following customers exist with random address
      | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-111111111111 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456790 | http://www.snapshot.stay   | Europe/Prague |
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
      | Id                                   | commercialSubscriptionId             |
      | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
    Given Switch for user property role tests
    Given The following roles exist
      | Id                                   | applicationId                        | roleName | description      |
      | 0d07159e-855a-4fc3-bcf2-a0cdbf54a44d | 11111111-0000-4000-a000-111111111111 | NewRole  | Some description |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user1    | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | user2    | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
     #    Must be here - DP-1846
    Given Relation between user "user1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user2" and property with code "defaultPropertyCode" exists with is_active "false"
    Given The following properties exist with random address and billing address
      | Id                                   | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 22222222-0000-4000-a000-666666666666 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.com    | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given Relation between user "user1" and property with code "p1_code" exists

  @skipped
  Scenario: Application can view only list of property-user roles of the customer and property it can access through a CS
    When User "user1" requests roles of user "user2" for property "defaultPropertyCode" for application version "versionWithoutSubscription"
    # DP-1898
    Then Response code is "404"
    When User "user1" requests roles of user "user2" for property "defaultPropertyCode" for application version "versionWithSubscription"
    Then Response code is "200"
    # Now let's give the second app access to the same property and let it try accessing user-property roles for the user of the inaccessible customer
    Given The following commercial subscriptions exist
      | Id                                   | customerId                           | propertyId                           | applicationId                        |
      | 55500000-0000-4000-a000-000000000555 | 12300000-0000-4000-a000-111111111111 | 11111111-0000-4000-a000-666666666666 | 00000000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | Id                                   | commercialSubscriptionId             |
      | 00000000-0000-4000-a000-000000000333 | 55500000-0000-4000-a000-000000000555 |
    And Relation between user "user1" and customer with id "12300000-0000-4000-a000-111111111111" exists
    When User "user1" requests roles of user "user2" for property "defaultPropertyCode" for application version "versionWithoutSubscription"
    Then Response code is "404"

  Scenario: Application can assign and revoke roles to property-users only when it has access to both user and property
    Given Switch for user property role tests
    When User "user1" assigns role "NewRole" to relation between user "user1" and property "defaultPropertyCode" for application version "versionWithoutSubscription"
    Then Response code is "404"
    When User "user1" assigns role "NewRole" to relation between user "user1" and property "defaultPropertyCode" for application version "versionWithSubscription"
    Then Response code is "201"