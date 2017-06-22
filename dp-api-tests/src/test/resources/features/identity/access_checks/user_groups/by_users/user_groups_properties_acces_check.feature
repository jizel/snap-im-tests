
@Identity
  Feature: User Groups Properties access check feature
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
      | id                                   | name        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following user groups exist
      | id                                   | customerId                           | name        | isActive | description          |
      | 12345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | id                                   | type     | username            | firstName | lastName | email                | timezone      | languageCode | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithUserGroup   | Customer  | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoUserGroup | Customer  | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"
    Given The following properties exist with random address and billing address for user "userWithUserGroup"
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |

    Scenario: Second level entities - User sees only user group-properties relations for user groups and properties he can access if the relation is active
      Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "false"
      When Relation between user group "userGroup_1" and property with code "p1_code" is requested by user "userWithUserGroup"
      Then Response code is 200

    Scenario: Second level entities - User does not see user group-properties relations when he cannot access the user group
      Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "true"
      When Relation between user group "userGroup_1" and property with code "p1_code" is requested by user "userWithNoUserGroup"
      Then Response code is 404
      And Custom code is 40402
      When List of all properties for user group "userGroup_1" is requested by user "userWithNoUserGroup"
      Then Response code is 404
      And Custom code is 40402

    Scenario: Add user group to property relationship by user who can access the property and user group
      When Relation between user group "userGroup_1" and property with code "p1_code" is created with isActive "false" by user "userWithUserGroup"
      Then Response code is "201"
      And Body contains entity with attribute "property_id" value "999e833e-50e8-4854-a233-289f00b54a09"


    Scenario: Add user group to property by user who cannot access the property, or the user group
      Given Relation between user "userWithUserGroup" and property "p1_code" is inactivated
      When Relation between user group "userGroup_1" and property with code "p1_code" is created with isActive "false" by user "userWithUserGroup"
      Then Response code is "422"
      And Custom code is 42202
      When Relation between user group "userGroup_1" and property with code "p1_code" is created with isActive "false" by user "userWithNoUserGroup"
      Then Response code is "404"
      And Custom code is 40402

    Scenario: Add user group to property by user whose access to the user group - or the property - is inactive
      Given Relation between user "userWithUserGroup" and property with code "p1_code" is inactivated
      When Relation between user group "userGroup_1" and property with code "p1_code" is created with isActive "false" by user "userWithUserGroup"
      Then Response code is "422"
      And Custom code is 42202
      Given Relation between user "userWithUserGroup" and property with code "p1_code" is activated
      When Relation between user group "userGroup_1" and property with code "p1_code" is created with isActive "false" by user "userWithUserGroup"
      Then Response code is "201"
      Given Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
      When Relation between user group "userGroup_1" and property with code "p1_code" is created with isActive "false" by user "userWithUserGroup"
      Then Response code is "404"
      And Custom code is 40402

    Scenario: Update user group to property relationship by user who has access
      Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "true"
      When IsActive for relation between user group "userGroup_1" and property with code "p1_code" is set to "false" by user "userWithUserGroup"
      Then Response code is 204
      And Body is empty
      And Relation between user group "userGroup_1" and property with code "p1_code" is not active

    Scenario: Update user group to property relationship by user who does not have access to the user group, or the property
      Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "true"
      When IsActive for relation between user group "userGroup_1" and property with code "p1_code" is set to "false" by user "userWithNoUserGroup"
      Then Response code is 404
      And Custom code is 40402

    Scenario: Delete userGroup-property relationship by user with access
      Given Relation between user group "userGroup_1" and property with code "p1_code" exists
      When Relation between user group "userGroup_1" and property with code "p1_code" is deleted by user "userWithUserGroup"
      Then Response code is 204
      And Body is empty
      And Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" exists no more

    Scenario: Delete userGroup-property relationship by user without access
      Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "false"
      When Relation between user group "userGroup_1" and property with code "p1_code" is deleted by user "userWithNoUserGroup"
      Then Response code is 404
      And Custom code is 40402
      When Relation between user group "userGroup_1" and property with code "p1_code" is requested by user "userWithUserGroup"
      Then Response code is 200

    Scenario: Delete user group to property relationship by user whose relation with the user group is inactive
      Given Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
      Given Relation between user group "userGroup_1" and property "p1_code" exists
      When Relation between user group "userGroup_1" and property "p1_code" is deleted by user "userWithUserGroup"
      Then Response code is 404
      And Custom code is 40402
