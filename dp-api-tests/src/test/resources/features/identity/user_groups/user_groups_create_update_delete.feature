Feature: User groups create update delete

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName        | email          | salesforceId | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | UserGroupsCustomer | ug@tenants.biz | ug_sf_1      | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given Default Snapshot user is created
    Given The following user groups exist
      | userGroupId                          | customerId                           | name        | isActive | description          |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_1 | false    | userGroupDescription |


  Scenario: Create user group with ID that already exists
    When The following user group is created
      | userGroupId                          | customerId                           | name        | isActive | description          |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_1 | false    | userGroupDescription |
    Then Response code is "409"
    And Custom code is 40901


  @Smoke
  Scenario: Create user group
    When The following user group is created
      | userGroupId                          | customerId                           | name                | description          |
      | b8b40d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_created_1 | userGroupDescription |
    Then Response code is "201"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "user_group_id" value "b8b40d08-de38-4246-bb69-ad39c31c025c"
    And Body contains entity with attribute "name" value "userGroup_created_1"
    And Body contains entity with attribute "customer_id" value "45a5f9e4-5351-4e41-9d20-fdb4609e9353"
    And Body contains entity with attribute "is_active" value "false"
    And Body contains entity with attribute "description" value "userGroupDescription"


  @Smoke
  Scenario: Activate user group
    When User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is activated
    Then Response code is 204
    And Body is empty
    And User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is active


  Scenario: Deactivate user group
    When User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is activated
    When User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is deactivated
    Then Response code is 204
    And Body is empty
    And User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is not active


  @Smoke
  Scenario: Delete user group
    When User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is deleted
    Then Response code is 204
    And Body is empty
    And User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is no more exists


  Scenario: Delete non-existing user group
    When User group with id "NonExistentUserGroup" is deleted
    Then Response code is 204
    And Body is empty


  Scenario: Cascade delete (customer is deleted, all his user groups as well)
    When Customer with customer id "45a5f9e4-5351-4e41-9d20-fdb4609e9353" is deleted
    Then Response code is 204
    And User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is no more exists


  Scenario Outline: Creating user group with invalid data
    When The following user group is created
      | userGroupId   | customerId   | name   | isActive   | description   |
      | <userGroupId> | <customerId> | <name> | <isActive> | <description> |
    Then Response code is <response_code>
    And Custom code is <error_code>
    Examples:
      | userGroupId | customerId                           | name          | isActive | description | response_code | error_code | #note                        |
      | NotExisting | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroupName | /null    | /null       | 422           | 42201      | # UUID format                |
      | \w{65}      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroupName | /null    | /null       | 422           | 42201      | # UUID format                |
      | /null       | /null                                | /null         | /null    | /null       | 422           | 42201      | # customerId is mandatory    |
      | /null       |                                      | /null         | /null    | /null       | 422           | 42201      | # customerId cannot be empty |
      | /null       | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | /null         | /null    | /null       | 422           | 42201      | # name is mandatory          |
      | /null       | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 |               | /null    | /null       | 422           | 42201      | # name cannot be empty       |
      | /null       | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | \w{256}       | /null    | /null       | 422           | 42201      | # too long name              |
      | /null       | NotExisting                          | userGroupName | /null    | /null       | 422           | 42201      | # customerId not in UUID     |
      | /null       | 11a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroupName | /null    | /null       | 404           | 40402      | # customerId not found       |
      | /null       | NotExisting                          | userGroupName | /null    | \w{501}     | 422           | 42201      | # too long description       |


  Scenario Outline: Updating user group with valid data
    When User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is updated with following data
      | name   | isActive   | description   |
      | <name> | <isActive> | <description> |
    Then Response code is 204
    And Etag header is present
    And User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" contains following data
      | name   | isActive   | description   |
      | <name> | <isActive> | <description> |
    Examples:
      | name                                                                   | isActive | description                                                                                         |
      | /null                                                                  | /null    | /null                                                                                               |
      | a                                                                      | /null    | /null                                                                                               |
      | updatedName                                                            | /null    | /null                                                                                               |
      | Pretty.Long.Name.To.Be.Updated.For.Mentioned.User.Group.Including.Dots | /null    | /null                                                                                               |
      | /null                                                                  | true     | /null                                                                                               |
      | /null                                                                  | /null    |                                                                                                     |
      | /null                                                                  | /null    | a                                                                                                   |
      | /null                                                                  | /null    | shortUpdate                                                                                         |
      | /null                                                                  | /null    | AnotherFuckingLongDescriptionThatCouldOccurDuringUpdate.AndImagineThatSomeoneWillTypeAndTypeAndType |
      | updatedName                                                            | /null    | updatedDesc                                                                                         |
      | updatedName                                                            | true     | updatedDesc                                                                                         |
      | updatedName                                                            | true     | /null                                                                                               |


  Scenario Outline: Updating user group with valid data - customer
    Given The following customers exist with random address
      | customerId                           | companyName         | email           | salesforceId | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 55a5f9e4-5351-4e41-9d20-fdb4609e9353 | UserGroupsCustomer2 | ug2@tenants.biz | ug_sf_1      | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 65a5f9e4-5351-4e41-9d20-fdb4609e9353 | UserGroupsCustomer3 | ug3@tenants.biz | ug_sf_1      | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    When User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is updated with following data
      | customerId   | name   | isActive   | description   |
      | <customerId> | <name> | <isActive> | <description> |
    Then Response code is 204
    And Etag header is present
    And User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" contains following data
      | customerId   | name   | isActive   | description   |
      | <customerId> | <name> | <isActive> | <description> |
    Examples:
      | customerId                           | name                        | isActive | description                |
      | 55a5f9e4-5351-4e41-9d20-fdb4609e9353 | /null                       | /null    | /null                      |
      | 65a5f9e4-5351-4e41-9d20-fdb4609e9353 | updatedNameForThirdCustomer | true     | updatedDescriptionForThird |


  Scenario Outline: Updating user group with invalid data
    When User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is updated with following data
      | customerId   | name   | description   |
      | <customerId> | <name> | <description> |
    Then Response code is "<error_response>"
    And Custom code is "<error_code>"
    Examples:
      | customerId | name     | description | error_response | error_code | #note                    |
      | \w{63}     | /null    | /null       | 400            | 63         | # customerId not in UUID |
      | \w{65}     | /null    | /null       | 400            | 63         | # customerId not in UUID |
      | karel      | /null    | /null       | 400            | 63         | # customerId not in UUID |
      | /null      |          | /null       | 400            | 61         | # empty name             |
      | /null      | \w{256}  | /null       | 400            | 63         | # long name              |
      | /null      | \w{2048} | /null       | 400            | 63         | # very long name         |
      | /null      | /null    | \w{501}     | 400            | 63         | # long description       |
      | /null      | /null    | \w{2048}    | 400            | 63         | # very long description  |
      | \w{65}     | \w{256}  | \w{501}     | 400            | 63         | # long all params        |

  Scenario Outline: Send POST request with empty body to all user groups endpoints
    Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                  |
      | identity/user_groups/                                                                                |
      | identity/user_groups/a8b40d08-de38-4246-bb69-ad39c31c025c                                            |
      | identity/user_groups/a8b40d08-de38-4246-bb69-ad39c31c025c/users/                                     |
      | identity/user_groups/a8b40d08-de38-4246-bb69-ad39c31c025c/properties/                                |
      | identity/user_groups/a8b40d08-de38-4246-bb69-ad39c31c025c/property_sets/                             |
      | identity/user_groups/a8b40d08-de38-4246-bb69-ad39c31c025c/roles/                                     |