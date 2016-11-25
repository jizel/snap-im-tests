@Identity
Feature: Roles create update delete user property set

  Background:
    Given Switch for user property set role tests

    Given Database is cleaned
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
      | Application test company 2 | Application description 2 | http://www.snapshot.travel | b318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following roles exist
      | applicationId                        | roleName    | description            |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 1 | optional description 1 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 2 | optional description 2 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 3 | optional description 3 |
    Given The following roles don't exist
      | applicationId                        | roleName          |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Updated role name |


  @Smoke
  Scenario: Creating role
    When Role is created
      | applicationId                        | roleName            | description            |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Created role name 1 | optional description 1 |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "Created role name 1"
    And Body contains entity with attribute "role_description" value "optional description 1"
    And "Location" header is set and contains the same role
    And Etag header is present


  Scenario Outline: Checking error codes for creating role
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"
    Examples:
      | json_input_file                                                    | method | module   | url                               | error_code | custom_code |
      | /messages/identity/roles/create_role_missing_application_id.json   | POST   | identity | /identity/user_property_set_roles | 422        | 42201       |
      | /messages/identity/roles/create_role_missing_role_name.json        | POST   | identity | /identity/user_property_set_roles | 422        | 42201       |
      | /messages/identity/roles/create_role_not_existing_application.json | POST   | identity | /identity/user_property_set_roles | 422        | 42201       |
      | /messages/identity/roles/create_role_not_recognized_field.json     | POST   | identity | /identity/user_property_set_roles | 422        | 42201       |
      | /messages/identity/roles/create_role_not_unique_role_name.json     | POST   | identity | /identity/user_property_set_roles | 422        | 42201       |
      | /messages/identity/roles/create_role_not_valid_json.json           | POST   | identity | /identity/user_property_set_roles | 422        | 42201       |


  @Smoke
  Scenario: Deleting role
    When Role with name "Role name 1" for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" is deleted
    Then Response code is "204"
    And Body is empty
    And Role with same id doesn't exist for application id "a318fd9a-a05d-42d8-8e84-42e904ace123"


  Scenario: Checking error code for deleting role
    When Nonexistent role id is deleted
    Then Response code is "204"


  Scenario Outline: Updating role
    When Role with name "<roleName>" for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" is updated with data
      | applicationId   | roleName           | description       |
      | <applicationId> | <updated_roleName> | <roleDescription> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated role with name "<updated_roleName>" for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" has data
      | applicationId   | roleName           | description       |
      | <applicationId> | <updated_roleName> | <roleDescription> |
    Examples:
      | applicationId | roleName    | updated_roleName  | roleDescription                |
      |               | Role name 1 | Updated role name |                                |
      |               | Role name 1 | Updated role name | Updated optional description   |
      |               | Role name 1 | Role name 1       | Updated optional description 1 |


  Scenario: Updating role with outdated etag
    When Role with name "Role name 1" for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" is updated with data if updated before
      | applicationId | roleName          | description |
      |               | Updated role name |             |
    Then Response code is "412"
    And Custom code is "57"


  Scenario Outline: Updating with invalid data
    When Role with name "<roleName>" for application id "<applicationId>" is updated with data
      | applicationId           | roleName           | description               |
      | <updated_applicationId> | <updated_roleName> | <updated_roleDescription> |
    Then Response code is "<responseCode>"
    And Custom code is "<customCode>"
    Examples:
      | applicationId                        | updated_applicationId | roleName    | updated_roleName | updated_roleDescription | responseCode | customCode |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 |                       | Role name 3 | Role name 2      |                         | 400          | 40002      |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | NonExistent           | Role name 3 |                  |                         | 400          | 40002      |
