@Identity
Feature: User Groups Properties Roles access check feature
  - Checking when certain user should and should not have access to certain User Group
  - User can update/delete specific user group only if he has a relationship
  - Check positive scenarios
  - In negative tests, ETAG needs to be obtained by different user first
  - User type Snapshot has access to all entities (other user types are equal)
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)
  - All rules apply also to second level entities in both ways (e.g. user_groups/ug_id/properties, properties/ug_id/user_groups) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given Default Snapshot user is created
    Given The following user groups exist
      | userGroupId                          | customerId                           | name        | isActive | description          |
      | 12345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userId                               | userType | userName            | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithUserGroup   | Customer  | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoUserGroup | Customer  | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"

  Scenario: Get relationship UserGroup property and Role by user
    Given The following property is created with random address and billing address for user "12329079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given The following partner exist
      | partnerId                            | name         | email                   | website                    |
      | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | PartnerName1 | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName                       | website                    | applicationId                        | partnerId                            | isInternal |
      | Application for UserGroup-Roles tests | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | true       |
    Given Switch for user property role tests
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | a318fd9a-a05d-42d8-8e84-42e904ace123 | role1    |
    Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "false"
    When Relation between user group "userGroup_1", property with code "p1_code" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup"
    When List of all roles for user group "userGroup_1" and property with code "p1_code" is requested by user "userWithUserGroup"
    Then Response code is "200"
    And Total count is "1"
    When List of all roles for user group "userGroup_1" and property with code "p1_code" is requested by user "userWithNoUserGroup"
    Then Response code is "404"


  Scenario: Create relationship UserGroup property and Role is created by user with access
    Given The following property is created with random address and billing address for user "12329079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given The following partner exist
      | partnerId                            | name         | email                   | website                    |
      | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | PartnerName1 | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName                       | website                    | applicationId                        | partnerId                            | isInternal |
      | Application for UserGroup-Roles tests | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | true       |
    Given Switch for user property role tests
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | a318fd9a-a05d-42d8-8e84-42e904ace123 | role1    |
    Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "false"
    When Relation between user group "userGroup_1", property with code "p1_code" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup"
    Then Response code is "201"
    And Body contains entity with attribute "role_id" value "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8"
    And Body contains entity with attribute "name" value "role1"
    And Body contains entity with attribute "application_id" value "a318fd9a-a05d-42d8-8e84-42e904ace123"

  Scenario: Create relationship UserGroup property and Role is created by user without access
    Given The following property is created with random address and billing address for user "12329079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given The following property is created with random address and billing address for user "32129079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 789e833e-50e8-4854-a233-289f00b54a09 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given The following partner exist
      | partnerId                            | name         | email                   | website                    |
      | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | PartnerName1 | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName                       | website                    | applicationId                        | partnerId                            | isInternal |
      | Application for UserGroup-Roles tests | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | true       |
    Given Switch for user property role tests
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | a318fd9a-a05d-42d8-8e84-42e904ace123 | role1    |
    Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "false"
    When Relation between user group "userGroup_1", property with code "p1_code" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithNoUserGroup"
    Then Response code is "404"
    And Custom code is 40402
    When Relation between user group "userGroup_1", property with code "p2_code" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup"
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Delete relationship UserGroup property and Role is updated by user with access
    Given The following property is created with random address and billing address for user "12329079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given The following partner exist
      | partnerId                            | name         | email                   | website                    |
      | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | PartnerName1 | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName                       | website                    | applicationId                        | partnerId                            | isInternal |
      | Application for UserGroup-Roles tests | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | true       |
    Given Switch for user property role tests
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | a318fd9a-a05d-42d8-8e84-42e904ace123 | role1    |
    Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "false"
    When Relation between user group "userGroup_1", property with code "p1_code" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup"
    When Relation between user group "userGroup_1", property with code "p1_code" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is deleted by user "userWithUserGroup"
#    Fails because of DP-1703
    Then Response code is "204"

  Scenario: Delete relationship UserGroup property and Role is updated by user with access
    Given The following property is created with random address and billing address for user "12329079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given The following partner exist
      | partnerId                            | name         | email                   | website                    |
      | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | PartnerName1 | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName                       | website                    | applicationId                        | partnerId                            | isInternal |
      | Application for UserGroup-Roles tests | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | true       |
    Given Switch for user property role tests
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | a318fd9a-a05d-42d8-8e84-42e904ace123 | role1    |
    Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "false"
    When Relation between user group "userGroup_1", property with code "p1_code" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is created by user "userWithUserGroup"
    When Relation between user group "userGroup_1", property with code "p1_code" and role with id "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is deleted by user "userWithNoUserGroup"
#    Fails because of DP-1703
    Then Response code is "404"