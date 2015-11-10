Feature: Roles_create_update_delete

  Background:
    Given The following roles exist
      | applicationId | roleName    | roleDescription        |
      | 1             | Role name 1 | optional description 1 |
      | 1             | Role name 2 | optional description 2 |
      | 1             | Role name 3 | optional description 3 |
    Given The following roles don't exist
      | applicationId | roleName          |
      | 1             | Updated role name |


  Scenario: Creating role

    When Role is created
      | applicationId | roleName            | roleDescription        |
      | 1             | Created role name 1 | optional description 1 |
    Then Response code is "201"
    And Body contains role type with "role_name" value "Created role name 1"
    And Body contains role type with "role_description" value "optional description 1"
    And "Location" header is set and contains the same role

  Scenario Outline: Checking error codes for creating role

    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                                                | method | module   | url             | error_code | custom_code |
      | /messages/identity/roles/create_role_missing_role_name.json    | POST   | identity | /identity/roles | 400        | 53          |
      | /messages/identity/roles/create_role_not_unique_role_name.json | POST   | identity | /identity/roles | 400        | 62          |

    #add another validations


  Scenario: Deleting role

    When Role with name "Role name 1" for application id "1" is deleted
    Then Response code is "204"
    And Body is empty
    And Role with same id doesn't exist for application id "1"


  Scenario: Checking error code for deleting role
    When Nonexistent role id is deleted
    Then Response code is "204"


  Scenario Outline: Updating role
    When Role with name "<roleName>" for application id "1" is updated with data
      | applicationId   | roleName           | roleDescription   |
      | <applicationId> | <updated_roleName> | <roleDescription> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated role with name "<updated_roleName>" for application id "1" has data
      | applicationId   | roleName           | roleDescription   |
      | <applicationId> | <updated_roleName> | <roleDescription> |

    Examples:
      | applicationId | roleName    | updated_roleName  | roleDescription                |
      |               | Role name 1 | Updated role name |                                |
      |               | Role name 1 | Updated role name | Updated optional description   |
      |               | Role name 1 | Role name 1       | Updated optional description 1 |


  #TODO update with error fields, bad values, missing fields, not unique fields

  Scenario: Updating role with outdated etag
    When Role with name "Role name 1" for application id "1" is updated with data if updated before
      | applicationId | roleName          | roleDescription |
      |               | Updated role name |                 |
    Then Response code is "412"
    And Custom code is "57"

  #error codes