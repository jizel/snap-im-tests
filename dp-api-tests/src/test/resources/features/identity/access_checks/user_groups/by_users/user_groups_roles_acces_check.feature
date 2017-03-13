@Identity
Feature: User Group Roles access check feature
  - Checking when certain user should and should not have access to certain User Group
  - User can update/delete specific user group only if he has a relationship
  - Check positive scenarios
  - In negative tests, ETAG needs to be obtained by different user first
  - User type Snapshot has access to all entities (other user types are equal)
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)
  - All rules apply also to second level entities in both ways (e.g. user_groups/ug_id/properties, properties/ug_id/user_groups) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
    Given The following user groups exist
      | userGroupId                          | customerId                           | name        | isActive | description          |
      | 12345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userId                               | userType | userName            | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithUserGroup   | Customer  | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoUserGroup | Customer  | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"


  Scenario: Get list of userGroup's roles by user with/without access
    Given The following partner exist
      | partnerId                            | name         | email                   | website                    |
      | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | PartnerName1 | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName                       | website                    | applicationId                        | partnerId                            | isInternal |
      | Application for UserGroup-Roles tests | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | true       |
    Given Switch for user customer role tests
    Given The following roles exist
      | roleId                               | applicationId                        | roleName        |
      | 5184fb6b-0ebd-4726-9481-4858a15a37a0 | a318fd9a-a05d-42d8-8e84-42e904ace123 | UG_filter_role1 |
      | 19e8d1c2-c4f7-44d7-b436-dd4e9249065d | a318fd9a-a05d-42d8-8e84-42e904ace123 | UG_filter_role2 |
      | 540be550-1702-4e2e-b094-394de63f6c48 | a318fd9a-a05d-42d8-8e84-42e904ace123 | UG_filter_role3 |
    When Relation between user group "userGroup_1" and role "5184fb6b-0ebd-4726-9481-4858a15a37a0" exists with is_active "true"
    When Relation between user group "userGroup_1" and role "19e8d1c2-c4f7-44d7-b436-dd4e9249065d" exists with is_active "true"
    When Relation between user group "userGroup_1" and role "540be550-1702-4e2e-b094-394de63f6c48" exists with is_active "false"
    When List of all relationships userGroups-Roles for userGroup "userGroup_1" is requested by user "userWithUserGroup"
    Then Response code is 200
    And Total count is "3"
    When List of all relationships userGroups-Roles for userGroup "userGroup_1" is requested by user "userWithNoUserGroup"
    Then Response code is 404


  Scenario: Create relationship UserGroup-Role by user with access
    Given The following partner exist
      | partnerId                            | name         | email                   | website                    |
      | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | PartnerName1 | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName                       | website                    | applicationId                        | partnerId                            | isInternal |
      | Application for UserGroup-Roles tests | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | true       |
    Given Switch for user customer role tests
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | a318fd9a-a05d-42d8-8e84-42e904ace123 | role1    |
    When Relation between user group "userGroup_1" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup"
    Then Response code is 201
    And Body contains entity with attribute "role_id" value "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8"
    And Relation between user group "userGroup_1" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is established

  Scenario: Create relationship UserGroup-Role by user without access to the user group (or inactive relation)
    Given The following partner exist
      | partnerId                            | name         | email                   | website                    |
      | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | PartnerName1 | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName                       | website                    | applicationId                        | partnerId                            | isInternal |
      | Application for UserGroup-Roles tests | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | true       |
    Given Switch for user customer role tests
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | a318fd9a-a05d-42d8-8e84-42e904ace123 | role1    |
    When Relation between user group "userGroup_1" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithNoUserGroup"
    Then Response code is 404
    And Custom code is 40402
    Given Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
    When Relation between user group "userGroup_1" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup"
    Then Response code is 404
    And Custom code is 40402

  Scenario: Delete relationship UserGroup-Role is possible only for user who can see the entities (with active relations)
    Given The following partner exist
      | partnerId                            | name         | email                   | website                    |
      | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | PartnerName1 | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName                       | website                    | applicationId                        | partnerId                            | isInternal |
      | Application for UserGroup-Roles tests | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | true       |
    Given Switch for user customer role tests
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | a318fd9a-a05d-42d8-8e84-42e904ace123 | role1    |
    When Relation between user group "userGroup_1" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" exists with is_active "false"
    When Relation between user group "userGroup_1" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is deleted by user "userWithNoUserGroup"
    Then Response code is "404"
    When Relation between user group "userGroup_1" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is deleted by user "userWithUserGroup"
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is not established

