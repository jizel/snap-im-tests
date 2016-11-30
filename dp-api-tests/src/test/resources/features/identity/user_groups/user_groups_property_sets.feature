Feature: User groups property sets

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName        | email          | vatId      | phone         | timezone      | isDemoCustomer |
      | 67adbc2d-f6ad-4e6a-9ed8-8ba93c430481 | UserGroupsCustomer | ug@tenants.biz | CZ10000001 | +420123456789 | Europe/Prague | true           |
    Given The following users exist for customer "67adbc2d-f6ad-4e6a-9ed8-8ba93c430481" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                         | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snapshotUser1 | Snapshot  | User1    | snapshotuser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Default Snapshot user is created for customer "67adbc2d-f6ad-4e6a-9ed8-8ba93c430481"
    Given The following user groups exist
      | userGroupId                          | customerId                           | name        | isActive |
      | 922913b0-877c-45f3-b650-df8022608d61 | 67adbc2d-f6ad-4e6a-9ed8-8ba93c430481 | userGroup_1 | false    |
    Given The following property sets exist for customer with id "67adbc2d-f6ad-4e6a-9ed8-8ba93c430481" and user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertySetId                        | propertySetName       | propertySetDescription | propertySetType |
      | fb141231-4d8c-4d75-9433-5d01cc665556 | PropertySet_UserGroup | PropertySet_UserGroup1 | branch          |
    Given Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" exists with isActive "false"


  @Smoke
  Scenario: Getting relationship between user group and property set
    When Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" is got
    Then Response code is 200
    And Content type is "application/json"
    And Body contains entity with attribute "property_set_id" value "fb141231-4d8c-4d75-9433-5d01cc665556"
    And Body contains entity with attribute "is_active" value "false"


  Scenario: Getting relationship between user group and property set - invalid
    When Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property "notExistent" is got
    Then Response code is 404
    And Custom code is 40402


  Scenario: Relationship creation between user group and property set - valid
    Given The following property sets exist for customer with id "67adbc2d-f6ad-4e6a-9ed8-8ba93c430481" and user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertySetId                        | propertySetName       | propertySetDescription | propertySetType |
      | e11352e6-44ff-45bb-bd51-28f62ca8f33c | PropertySet_UserGroup | PropertySet_UserGroup1 | branch          |
    When Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "e11352e6-44ff-45bb-bd51-28f62ca8f33c" is created with isActive "true"
    Then Response code is 201
    And Content type is "application/json"
    And Body contains entity with attribute "property_set_id" value "e11352e6-44ff-45bb-bd51-28f62ca8f33c"
    And Body contains entity with attribute "is_active" value "true"


  Scenario Outline: Relationship creation between user group and property - invalid
    When Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "<property_set_id>" is created with isActive "<is_active>"
    Then Response code is "<error_response>"
    And Custom code is "<code>"
    Examples:
      | property_set_id                      | is_active | error_response | code  | #note                             |
      | NotValidFormat                       | /null     | 400            | 63    | # property_set_id not in UUID     |
      | d11352e6-44ff-45bb-bd51-28f62ca8f33c | /null     | 422            | 42202 | # notExisting property_set_id     |
      |                                      | /null     | 400            | 63    | # property_set_id cannot be empty |
      | /null                                | /null     | 400            | 53    | # property_set_id cannot be empty |


  Scenario: Create duplicate relationship userGroup-propertySet
    When Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" exists with isActive "false"
    Then Response code is "409"
    And Custom code is "40901"


  Scenario: Delete userGroup-propertySet relationship - valid
    When Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" is deleted
    Then Response code is 204
    And Body is empty
    And Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" is no more exists


  Scenario Outline: Delete userGroup-propertySet not existent relationship
    When Relation between user group "<userGroupId>" and property set "<propertyId>" is deleted
    Then Response code is 204
    And Body is empty
    Examples:
      | userGroupId                          | propertyId                           |
      | notExistent                          | fb141231-4d8c-4d75-9433-5d01cc665556 |
      | 922913b0-877c-45f3-b650-df8022608d61 | notExistent                          |
      | notExistent                          | notExistent                          |


  Scenario: Activate relationship userGroup-propertySet
    When Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" is activated
    Then Response code is 204
    And Body is empty
    And Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" is activate


  Scenario: Deactivate relationship userGroup-propertySet
    When Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" is activated
    When Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" is deactivated
    Then Response code is 204
    And Body is empty
    And Relation between user group "922913b0-877c-45f3-b650-df8022608d61" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" is not activate

#  TODO: Getting list of relationships, sort, filter, sortdesc

#  TODO: Getting list of relationships, limit, cursor