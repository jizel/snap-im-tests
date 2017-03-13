@Identity
Feature: User Groups Roles Application access check feature
  - Checking when certain application should and should not have access to certain User Group role
  - Only User Groups for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given Switch for user customer role tests
    Given The following customers exist with random address
      | customerId                           | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName          | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithUserGroup | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | partnerId                            | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName          | applicationId                        | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | versionId                            | apiManagerId | versionName             | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    Given The following application versions exists
      | versionId                            | apiManagerId | versionName                | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
    Given The following user groups exist
      | customerId                           | name        | isActive | description          |
      | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"

  Scenario: Get list of userGroup's roles by application with/without access
    Given The following roles exist
      | roleId                               | applicationId                        | roleName        |
      | 5184fb6b-0ebd-4726-9481-4858a15a37a0 | 22200000-0000-4000-a000-000000000222 | UG_filter_role1 |
      | 19e8d1c2-c4f7-44d7-b436-dd4e9249065d | 22200000-0000-4000-a000-000000000222 | UG_filter_role2 |
      | 540be550-1702-4e2e-b094-394de63f6c48 | 00000000-0000-4000-a000-000000000222 | UG_filter_role3 |
    When Relation between user group "userGroup_1" and role "5184fb6b-0ebd-4726-9481-4858a15a37a0" exists
    When Relation between user group "userGroup_1" and role "19e8d1c2-c4f7-44d7-b436-dd4e9249065d" exists
    When Relation between user group "userGroup_1" and role "540be550-1702-4e2e-b094-394de63f6c48" exists
    When List of all relationships userGroups-Roles for userGroup "userGroup_1" is requested by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 200
    And Total count is "3"
    When List of all relationships userGroups-Roles for userGroup "userGroup_1" is requested by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404

  Scenario: Create userGroup role by application with/without access
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | 22200000-0000-4000-a000-000000000222 | role1    |
    When Relation between user group "userGroup_1" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When Relation between user group "userGroup_1" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 201

    Scenario: Delete relationship UserGroup-Role by application with and without access
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | a318fd9a-a05d-42d8-8e84-42e904ace123 | role1    |
    When Relation between user group "userGroup_1" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" exists with is_active "false"
    When Relation between user group "userGroup_1" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is deleted by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is "204"