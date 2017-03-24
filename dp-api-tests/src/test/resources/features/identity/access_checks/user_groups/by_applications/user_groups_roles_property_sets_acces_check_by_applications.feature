@Identity
Feature: User Groups Property sets Roles Application access check feature
  - Checking when certain application should and should not have access to certain User Group-Property Set role
  - Only Property Sets of User Groups for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
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
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithUserGroup"
      | Id                                   | name       | description | type |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    Given Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" exists
    Given Switch for user property set role tests
    Given The following roles exist
      | roleId                               | Id                                   | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | 22200000-0000-4000-a000-000000000222 | role1    |


  Scenario: UserGroup-PropertySet-Role relation is created, got and deleted by application with and withou access
    When Relation between user group "userGroup_1", property set "PropertySet_UserGroup" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is "404"
    And Custom code is 40402
    When Relation between user group "userGroup_1", property set "PropertySet_UserGroup" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is "201"
    When List of all roles for user group "userGroup_1" and property set "PropertySet_UserGroup" is requested by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is "200"
    And Total count is "1"
    When List of all roles for user group "userGroup_1" and property set "PropertySet_UserGroup" is requested by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is "404"
    And Custom code is 40402
    When Relation between user group "userGroup_1", property set "PropertySet_UserGroup" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is deleted by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is "404"
    And Custom code is 40402
    When Relation between user group "userGroup_1", property set "PropertySet_UserGroup" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is deleted by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is "204"