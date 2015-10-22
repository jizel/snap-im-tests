Feature: Users
  #TODO update for users

  Background:
    Given The following users exist
      | roleId | applicationId | roleName   | description       |
      | role_1 | application_1 | first_role | sample descpriotn |































  @skipped
  Scenario: Creating user

    When User is created
      | tenantId | companyName | email          | code |
      | 11       | Company 1   | c1@tenants.biz | c1t  |
    Then User with "id" exists
    And Content type is "application/json"
    And Response code is "201"

  @skipped
  Scenario Outline: Updating User
    When Role with "<id>" is updated with "<updated_fields>" by "<new_values>"
    Then Role with "<id>" exists with "<updated_fields>" by "<new_values>"
    And Response code is "204"
    And Content type is "application/json"

    Examples:
      | id    | updated_fields                | new_values          |
      | 10001 | ["role_id", "application_id"] | ["10005", "100005"] |

  @skipped
  Scenario Outline: Checking error codes for creating user
    When File <json_input_file> is used for "<method>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                           | method | error_code | custom_code |
      | identity/users/user_missing_username.json | POST   | 405        | 51          |