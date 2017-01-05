Feature: User groups properties

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName        | email          | vatId      | phone         | timezone      | isDemoCustomer |
      | 5be73595-bf57-4b10-8725-dad80531dbc3 | UserGroupsCustomer | ug@tenants.biz | CZ10000001 | +420123456789 | Europe/Prague | true           |
    Given The following users exist for customer "5be73595-bf57-4b10-8725-dad80531dbc3" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                         | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snapshotUser1 | Snapshot  | User1    | snapshotuser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Default Snapshot user is created
    Given The following user groups exist
      | userGroupId                          | customerId                           | name        | isActive |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | 5be73595-bf57-4b10-8725-dad80531dbc3 | userGroup_1 | false    |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | propertyName        | propertyCode        | email          | timezone      | anchorCustomerId                     | isDemoProperty |
      | 896c2eac-4ef8-45d1-91fc-79a5933a0ed3 | property_userGroup1 | property_userGroup1 | p1@tenants.biz | Europe/Prague | 5be73595-bf57-4b10-8725-dad80531dbc3 | true           |

    Given Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" exists with isActive "false"

  @Smoke
  Scenario: Getting relationship between user group and property
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" is got
    Then Response code is 200
    And Content type is "application/json"
    And Body contains entity with attribute "property_id" value "896c2eac-4ef8-45d1-91fc-79a5933a0ed3"
    And Body contains entity with attribute "is_active" value "false"

  Scenario: Getting relationship between user group and property - invalid
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "notExistent" is got
    Then Response code is 404
    And Custom code is 40402

  Scenario: Relationship creation between user group and property - valid
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | propertyName        | propertyCode        | email          | timezone      | anchorCustomerId                     | isDemoProperty |
      | 30f983ea-7a69-4e50-a369-d1278f1a0c40 | property_userGroup2 | property_userGroup2 | p2@tenants.biz | Europe/Prague | 5be73595-bf57-4b10-8725-dad80531dbc3 | true           |
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "30f983ea-7a69-4e50-a369-d1278f1a0c40" is created with isActive "false"
    Then Response code is 201
    And Content type is "application/json"
    And Body contains entity with attribute "property_id" value "30f983ea-7a69-4e50-a369-d1278f1a0c40"
    And Body contains entity with attribute "is_active" value "false"

  Scenario Outline: Relationship creation between user group and property - invalid
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "<property_id>" is created with isActive "<is_active>"
    Then Response code is "<error_response>"
    And Custom code is "<code>"
    Examples:
      | property_id                          | is_active | error_response | code  | #note                         |
      | NotValidFormat                       | /null     | 422            | 42201 | # property_id not in UUID     |
      | 30f983ea-7a69-4e50-a369-d1278f1a0c40 | /null     | 422            | 42202 | # notExisting property_id     |
      |                                      | /null     | 422            | 42201 | # property_id cannot be empty |
      | /null                                | /null     | 422            | 42201 | # property_id cannot be empty |

  Scenario: Delete userGroup-property relationship - valid
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" is deleted
    Then Response code is 204
    And Body is empty
    And Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" is no more exists

  Scenario Outline: Delete userGroup-property not existent relationship
    When Relation between user group "<userGroupId>" and property "<propertyId>" is deleted
    Then Response code is 412
    And Body contains entity with attribute "message" value "Precondition failed: ETag not present."
    Examples:
      | userGroupId                          | propertyId                           |
      | notExistent                          | 896c2eac-4ef8-45d1-91fc-79a5933a0ed3 |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | notExistent                          |
      | notExistent                          | notExistent                          |

  Scenario: Activate relationship userGroup-property
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" is activated
    Then Response code is 204
    And Body is empty
    And Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" is activate

  Scenario: Deactivate relationship userGroup-property
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" is activated
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" is deactivated
    Then Response code is 204
    And Body is empty
    And Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" is not activate

  Scenario Outline: Send POST request with empty body to all user group-property endpoints
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                             |
      | identity/user_groups/a8b40d08-de38-4246-bb69-ad39c31c025c/properties/896c2eac-4ef8-45d1-91fc-79a5933a0ed3       |
      | identity/user_groups/a8b40d08-de38-4246-bb69-ad39c31c025c/properties/896c2eac-4ef8-45d1-91fc-79a5933a0ed3/roles |

#    TODO: Getting list of relationships, sort, filter, sortdesc

#    TODO: Getting list of relationships, limit, cursor