@Identity
Feature: User Groups - Properties Application access check feature
  - Checking when certain application should and should not have access to certain Property sets of User Groups
  - Only Propertiy sets of User Groups for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName          | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithUserGroup | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | Id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName          | Id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName             | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName                | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | Id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | Id                                   | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
    Given The following user groups exist
      | customerId                                   | name        | isActive | description          |
      | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithUserGroup"
      | Id                                   | name | type |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 | prop_set1       | brand           |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd1 | prop_set2       | brand           |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"


  Scenario: Application sees only user group-property sets relations for user groups and property sets it has subscribed to
    Given Relation between user group "userGroup_1" and property set "prop_set1" exists
    When Relation between user group "userGroup_1" and property set "prop_set1" is requested by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When Relation between user group "userGroup_1" and property set "prop_set1" is requested by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 200
    When List of all property sets for user group "userGroup_1" is requested by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    When List of all property sets for user group "userGroup_1" is requested by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 200
    And Total count is "1"

  Scenario: Relationship is created between user group and property set by application with and without access
    When Relation between user group "userGroup_1" and property set "prop_set1" is created by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When Relation between user group "userGroup_1" and property set "prop_set1" is created by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 201

  Scenario: Update relationship userGroup-propertySet by application with and without access
    Given Relation between user group "userGroup_1" and property set "prop_set1" exists
    When IsActive relation between user group "userGroup_1" and property set "prop_set1" is set to "true" by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When IsActive relation between user group "userGroup_1" and property set "prop_set1" is set to "true" by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 204

  Scenario: Delete userGroup-propertySet relationship by user with access
    Given Relation between user group "userGroup_1" and property set "prop_set1" exists
    When Relation between user group "userGroup_1" and property set "prop_set1" is deleted by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When Relation between user group "userGroup_1" and property set "prop_set1" is deleted by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 204