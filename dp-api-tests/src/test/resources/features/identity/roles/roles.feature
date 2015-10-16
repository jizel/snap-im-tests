Feature: Roles

  Background:
    Given The following roles exist
      | roleId | applicationId | roleName    | description          |
      | 10001  | 100001        | Role name 1 | optional description |
      | 10002  | 100002        | Role name 2 | optional description |
      | 10003  | 100003        | Role name 3 | optional description |
    And The following applications exist
      | applicationId | partnerId | applicationName | applicationDescription | isActive |
      |               |           |                 |                        |          |


  @skipped
  Scenario: Creating role

    When Role is created
      | tenantId | companyName | email          | code |
      | 11       | Company 1   | c1@tenants.biz | c1t  |
    Then Role with "id" exists
    And Content type is "application/json"
    And Response code is "201"

  @skipped
  Scenario Outline: Updating role
    When Role with "<id>" is updated with "<updated_fields>" by "<new_values>"
    Then Role with "<id>" exists with "<updated_fields>" by "<new_values>"
    And Response code is "204"
    And Content type is "application/json"

    Examples:
      | id    | updated_fields                | new_values          |
      | 10001 | ["role_id", "application_id"] | ["10005", "100005"] |

  @skipped
  Scenario Outline: Checking error codes for creating role
    When File <json_input_file> is used for "<method>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                            | method | error_code | custom_code |
      | identity/roles/role_missing_role_name.json | POST   | 405        | 51          |