Feature: User groups property sets

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | companyName        | email          | vatId      | phone         | timezone      | isDemoCustomer |
      | 67adbc2d-f6ad-4e6a-9ed8-8ba93c430481 | UserGroupsCustomer | ug@tenants.biz | CZ10000001 | +420123456789 | Europe/Prague | true           |
    Given The following user groups exist
      | id                                   | customerId                           | name        | isActive |
      | 922913b0-877c-45f3-b650-df8022608d61 | 67adbc2d-f6ad-4e6a-9ed8-8ba93c430481 | userGroup_1 | false    |
    Given The following property sets exist for customer with id "67adbc2d-f6ad-4e6a-9ed8-8ba93c430481"
      | id                                   | name                  | description            | type            |
      | fb141231-4d8c-4d75-9433-5d01cc665556 | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    Given Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" exists with isActive "false"


  @Smoke
  Scenario: Getting relationship between user group and property set
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is got
    Then Response code is 200
    And Content type is "application/json"
    And Body contains entity with attribute "property_set_id" value "fb141231-4d8c-4d75-9433-5d01cc665556"
    And Body contains entity with attribute "is_active" value "false"


  Scenario: Getting relationship between user group and property set - invalid
    When Relation between user group "userGroup_1" and nonexistent property set is requested
    Then Response code is 404
    And Custom code is 40402


  Scenario: Relationship creation between user group and property set - valid
    Given The following property sets exist for customer with id "67adbc2d-f6ad-4e6a-9ed8-8ba93c430481"
      | id                                   | name                  | description            | type            |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "true"
    Then Response code is 201
    And Content type is "application/json"
    And Body contains entity with attribute "property_set_id" value "e11352e6-44ff-45bb-bd51-28f62ca8f33c"
    And Body contains entity with attribute "is_active" value "true"


  Scenario Outline: Relationship creation between user group and property - invalid
    When Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set with id "<property_set_id>" is created with isActive "<is_active>" with error "true"
    Then Response code is "<error_response>"
    And Custom code is "<code>"
    Examples:
      | property_set_id                      | is_active | error_response | code  | #note                             |
      | NotValidFormat                       | /null     | 422            | 42201 | # property_set_id not in UUID     |
      | d11352e6-44ff-45bb-bd51-28f62ca8f33c | /null     | 422            | 42202 | # notExisting property_set_id     |
      |                                      | /null     | 422            | 42201 | # property_set_id cannot be empty |
      | /null                                | /null     | 422            | 42201 | # property_set_id cannot be empty |


  Scenario: Create duplicate relationship userGroup-propertySet
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" exists with isActive "false"
    Then Response code is "409"
    And Custom code is "40902"


  Scenario: Delete userGroup-propertySet relationship - valid
    When Relation between user group "userGroup_1" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" is deleted
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" no more exists


  Scenario Outline: Delete userGroup-propertySet not existent relationship
    When Relation between user group with id "<id>" and property set "<propertyId>" is deleted with error "true"
    Then Response code is 404
    And Body contains entity with attribute "message" value "Resource UserGroupPropertySetRelationship with ID (user_group_id: <id>, property_set_id: <propertyId>) was not found."
    Examples:
      | id                                   | propertyId                           |
      | 00000000-0000-4000-a000-000000000000 | fb141231-4d8c-4d75-9433-5d01cc665556 |
      | 922913b0-877c-45f3-b650-df8022608d61 | 00000000-0000-4000-a000-000000000000 |
      | 00000000-0000-4000-a000-000000000000 | 00000000-0000-4000-a000-000000000000 |

  Scenario: Activate relationship userGroup-propertySet
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is activated
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is active
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is deactivated
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is not active

  Scenario Outline: Send POST request with empty body to all user group-property endpoints
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                                |
      | identity/user_groups/922913b0-877c-45f3-b650-df8022608d61/property_sets/fb141231-4d8c-4d75-9433-5d01cc665556       |
      | identity/user_groups/922913b0-877c-45f3-b650-df8022608d61/property_sets/fb141231-4d8c-4d75-9433-5d01cc665556/roles |

  Scenario: Duplicate relationship creation between user group and property set - DP-1661
    Given The following property sets exist for customer with id "67adbc2d-f6ad-4e6a-9ed8-8ba93c430481"
      | id                                   | name                  | description            | type            |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | brand           |
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "true"
    Then Response code is 201
    When Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is created with isActive "true"
    Then Response code is 409
    And Custom code is 40902

  Scenario: Property Set cannot be deleted until User Group is (and vice versa)
    When Property set "PropertySet_UserGroup" is deleted
    Then Response code is 409
    And Custom code is 40915
    When User group "userGroup_1" is deleted
    Then Response code is 409
    And Custom code is 40915
    Given Relation between user group "userGroup_1" and property set "PropertySet_UserGroup" is deleted
    When Property set "PropertySet_UserGroup" is deleted
    Then Response code is 204
    When User group "userGroup_1" is deleted
    Then Response code is 204

#  TODO: Getting list of relationships, sort, filter, sortdesc

#  TODO: Getting list of relationships, limit, cursor