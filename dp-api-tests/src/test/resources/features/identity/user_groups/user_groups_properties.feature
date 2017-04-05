Feature: User groups properties

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | id                                   | companyName        | email          | vatId      | phone         | timezone      | isDemoCustomer |
      | 5be73595-bf57-4b10-8725-dad80531dbc3 | UserGroupsCustomer | ug@tenants.biz | CZ10000001 | +420123456789 | Europe/Prague | true           |
    Given The following user groups exist
      | id                                   | customerId                           | name        | isActive |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | 5be73595-bf57-4b10-8725-dad80531dbc3 | userGroup_1 | false    |
    Given The following properties exist with random address and billing address
      | id                                   | name                | propertyCode        | email          | timezone      | anchorCustomerId                     | isDemoProperty |
      | 896c2eac-4ef8-45d1-91fc-79a5933a0ed3 | property_userGroup1 | property_userGroup1 | p1@tenants.biz | Europe/Prague | 5be73595-bf57-4b10-8725-dad80531dbc3 | true           |
    Given Relation between user group "userGroup_1" and property with code "property_userGroup1" exists with isActive "false"

  @Smoke
  Scenario: Getting relationship between user group and property
    When Relation between user group "userGroup_1" and property with code "property_userGroup1" is requested
    Then Response code is 200
    And Content type is "application/json"
    And Body contains entity with attribute "property_id" value "896c2eac-4ef8-45d1-91fc-79a5933a0ed3"
    And Body contains entity with attribute "is_active" value "false"

  Scenario: Getting relationship between user group and property - invalid
    When Relation between user group "userGroup_1" and non existent property is requested
    Then Response code is 404
    And Custom code is 40402

  Scenario: Relationship creation between user group and property - valid
    Given The following properties exist with random address and billing address
      | id                                   | name                | propertyCode        | email          | timezone      | anchorCustomerId                     | isDemoProperty |
      | 30f983ea-7a69-4e50-a369-d1278f1a0c40 | property_userGroup2 | property_userGroup2 | p2@tenants.biz | Europe/Prague | 5be73595-bf57-4b10-8725-dad80531dbc3 | true           |
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "30f983ea-7a69-4e50-a369-d1278f1a0c40" is created with isActive "false"
    Then Response code is 201
    And Content type is "application/json"
    And Body contains entity with attribute "property_id" value "30f983ea-7a69-4e50-a369-d1278f1a0c40"
    And Body contains entity with attribute "is_active" value "false"

  Scenario Outline: Relationship creation between user group and property - invalid
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "<property_id>" is created with isActive "<is_active>" with error "true"
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
    And Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" exists no more

  Scenario Outline: Delete userGroup-property not existent relationship
    When Relation between user group "<id>" and property "<propertyId>" is deleted with error "true"
    Then Response code is <responseCode>
    And Custom code is <customCode>
    Examples:
      | id                                   | propertyId                           | responseCode | customCode |
      | notExistent                          | 896c2eac-4ef8-45d1-91fc-79a5933a0ed3 | 400          | 40003      |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | notExistent                          | 400          | 40003      |
      | 00000000-0000-4000-a000-000000000000 | 00000000-0000-4000-a000-000000000000 | 404          | 40402      |
      | 00000000-0000-4000-a000-000000000000 | 896c2eac-4ef8-45d1-91fc-79a5933a0ed3 | 404          | 40402      |
      | 00000000-0000-4000-a000-000000000000 | 896c2eac-4ef8-45d1-91fc-79a5933a0ed3 | 404          | 40402      |

  Scenario: Activate relationship userGroup-property
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" is activated
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and property with code "property_userGroup1" is active
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "896c2eac-4ef8-45d1-91fc-79a5933a0ed3" is deactivated
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and property with code "property_userGroup1" is not active

  Scenario Outline: Send POST request with empty body to all user group-property endpoints
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                             |
      | identity/user_groups/a8b40d08-de38-4246-bb69-ad39c31c025c/properties/896c2eac-4ef8-45d1-91fc-79a5933a0ed3       |
      | identity/user_groups/a8b40d08-de38-4246-bb69-ad39c31c025c/properties/896c2eac-4ef8-45d1-91fc-79a5933a0ed3/roles |

  Scenario: Duplicate relationship creation between user group and property - DP-1661
    Given The following properties exist with random address and billing address
      | id                                   | name                | propertyCode        | email          | timezone      | anchorCustomerId                     | isDemoProperty |
      | 30f983ea-7a69-4e50-a369-d1278f1a0c40 | property_userGroup2 | property_userGroup2 | p2@tenants.biz | Europe/Prague | 5be73595-bf57-4b10-8725-dad80531dbc3 | true           |
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "30f983ea-7a69-4e50-a369-d1278f1a0c40" is created with isActive "true"
    Then Response code is 201
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property "30f983ea-7a69-4e50-a369-d1278f1a0c40" is created with isActive "true"
    Then Response code is 409
    And Custom code is 40902

  Scenario: Property cannot be deleted until User Group is (and vice versa)
    When Property with code "property_userGroup1" is deleted
    Then Response code is 409
    And Custom code is 40915
    When User group "userGroup_1" is deleted
    Then Response code is 409
    And Custom code is 40915
    Given Relation between user group "userGroup_1" and property with code "property_userGroup1" is deleted
    When Property with code "property_userGroup1" is deleted
    Then Response code is 204
    When User group "userGroup_1" is deleted
    Then Response code is 204


#    TODO: Getting list of relationships, sort, filter, sortdesc

#    TODO: Getting list of relationships, limit, cursor