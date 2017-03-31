@Identity
Feature: User Groups Property Sets access check feature
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
      | id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
    Given The following user groups exist
      | id                                   | customerId                           | name        | isActive | description          |
      | 12345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | id                                   | userType | userName            | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithUserGroup   | Customer  | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoUserGroup | Customer  | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"


    Scenario: Second level entities - User sees only user group-property sets relations for user groups and property sets he can access and is active
      Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithUserGroup"
        | name            | type            |
        | prop_set1       | brand           |
        | prop_set2       | brand           |
      Given Relation between user group "userGroup_1" and property set "prop_set1" exists
      When Relation between user group "userGroup_1" and property set "prop_set1" is requested by user "userWithNoUserGroup"
      Then Response code is 404
      When Relation between user group "userGroup_1" and property set "prop_set1" is requested by user "userWithUserGroup"
      Then Response code is 200
      Given Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
      When Relation between user group "userGroup_1" and property set "prop_set1" is requested by user "userWithUserGroup"
      Then Response code is 404

    Scenario: Second level entities - User does not see user group-property sets relations when he cannot access the user group
      Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithUserGroup"
        | name            | type            |
        | prop_set1       | brand           |
        | prop_set2       | brand           |
      Given Relation between user group "userGroup_1" and property set "prop_set1" exists with isActive "true"
      When Relation between user group "userGroup_1" and property set "prop_set1" is requested by user "userWithNoUserGroup"
      Then Response code is 404
      And Custom code is 40402
      When List of all property sets for user group "userGroup_1" is requested by user "userWithNoUserGroup"
      Then Response code is 404
      And Custom code is 40402


  Scenario: Relationship is created between user group and property set by user with access
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithUserGroup"
      | id                                   | name                  | description            | type            |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "true" by user "userWithUserGroup"
    Then Response code is 201
    And Body contains entity with attribute "property_set_id" value "e11352e6-44ff-45bb-bd51-28f62ca8f33c"

  Scenario: Relationship is created between user group and property set by user without access to user group, or the property set
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithNoUserGroup"
      | id                                   | name                  | description            | type            |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "true" by user "userWithNoUserGroup"
    Then Response code is 404
    And Custom code is 40402
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "true" by user "userWithUserGroup"
    Then Response code is 422
    And Custom code is 42202

  Scenario: Add user group to property set by user whose access to the user group - or the property - is inactive
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithUserGroup"
      | id                                   | name                  | description            | type            |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    Given Relation between user "userWithUserGroup" and property set "PropertySet_UserGroup" is inactivated
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "true" by user "userWithUserGroup"
    Then Response code is "422"
    And Custom code is 42202
    Given Relation between user "userWithUserGroup" and property set "PropertySet_UserGroup" is activated
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "true" by user "userWithUserGroup"
    Then Response code is "201"
    Given Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "true" by user "userWithUserGroup"
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Update relationship userGroup-propertySet by user with access
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithUserGroup"
      | id                                   | name                  | description            | type            |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "false"
    When IsActive relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is set to "true" by user "userWithUserGroup"
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is active

  Scenario: Update relationship userGroup-propertySet by user without access to user group
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithNoUserGroup"
      | id                                   | name                  | description            | type            |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" exists with isActive "false"
    When IsActive relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is set to "true" by user "userWithNoUserGroup"
    Then Response code is 404
    And Custom code is 40402
    Given Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
    When IsActive relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is set to "true" by user "userWithUserGroup"
    Then Response code is 404
    And Custom code is 40402

  Scenario: Delete userGroup-propertySet relationship by user with access
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithUserGroup"
      | id                                   | name                  | description            | type            |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "true"
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is deleted by user "userWithUserGroup"
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" no more exists

  Scenario: Delete userGroup-propertySet relationship by user without (or with inactive) access to user group
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithNoUserGroup"
      | id                                   | name                  | description            | type            |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is deleted by user "userWithNoUserGroup"
    Then Response code is 404
    And Custom code is 40402
    Given Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is deleted by user "userWithUserGroup"
    Then Response code is 404
    And Custom code is 40402