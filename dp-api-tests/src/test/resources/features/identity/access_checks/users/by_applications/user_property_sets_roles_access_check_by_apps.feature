@Identity
Feature: User-propertyset roles access check by app feature
  - Checking when application can have access to users-propertyset roles

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
    Given Switch for user property set role tests
    Given The following roles exist
      | Id                                   | roleName | description      |
      | 11111111-0000-4000-a000-111111111111 | NewRole  | Some description |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName  | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user1     | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | user2     | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "user1OfC1"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
    And Relation between property with code "defaultPropertyCode" and property set with name "<string>" exists with is_active "<string>"
     #    Must be here - DP-1846
    Given Relation between user "user1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user2" and property with code "defaultPropertyCode" exists with is_active "false"
#    Given The following properties exist with random address and billing address
#      | Id                                   | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
#      | 22222222-0000-4000-a000-666666666666 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.com    | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
#    Given Relation between user "user1" and property with code "p1_code" exists

  Scenario: App can view only list of propertyset-user roles of customer and property set it has access to
    When User "user1OfC1" requests roles of user "user2OfC1" for property set "00002111-cdaf-439a-8bef-3140f56c657e" for application version "versionWithoutSubscription"
    Then Response code is "404"
    When User "user1OfC1" requests roles of user "user2OfC1" for property set "00002111-cdaf-439a-8bef-3140f56c657e" for application version "versionWithSubscription"
    Then Response code is "200"
    # Add a new property to this property set
    Given The following properties exist with random address and billing address
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.com    | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 | 
    And Relation between property with code "p1_code" and property set with name "ps1_name" exists
    
# DP-1781
    @skipped
  Scenario: User can assign and revoke roles to propertyset-users only when he has access to both user and propertyset
    When User "user2OfC1" assigns role "NewRole" to relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    When Relation between property set "ps1_name" and user "user1OfC1" is inactivated
    When User "user1OfC1" assigns role "NewRole" to relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    When Relation between property set "ps1_name" and user "user1OfC1" is activated
    When User "user1OfC1" assigns role "NewRole" to relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "201"
    When User "user1OfC1" assigns role "NewRole" to relation between user "user2OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    Given User "user1OfC2" is added to property set with name "ps1_name"
    When User "user1OfC1" assigns role "NewRole" to relation between user "user1OfC2" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    When User "user1OfC2" deletes role "NewRole" from relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    When Relation between property set "ps1_name" and user "user1OfC1" is inactivated
    When User "user1OfC1" deletes role "NewRole" from relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    When Relation between property set "ps1_name" and user "user1OfC1" is activated
    When User "user1OfC1" deletes role "NewRole" from relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "204"
