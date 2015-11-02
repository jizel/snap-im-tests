Feature: roles_get

  Background:
    Given The following roles exist
      | applicationId | roleName    | roleDescription        |
      | 1             | Role name 1 | optional description 1 |
      | 1             | Role name 2 | optional description 2 |
      | 1             | Role name 3 | optional description 3 |


  Scenario: Getting role
    When Role with name "Role name 1" for application id "1" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present

    # existence
    And Body contains role with attribute "role_id"
    And Body contains role with attribute "role_name"
    And Body contains role with attribute "application_id"

    # exact value
    And Body contains role with attribute "application_id" value "1"
    And Body contains role with attribute "role_description" value "optional description 1"
    And Body contains role with attribute "role_name" value "Role name 1"

    # non-existence
    And Body does not contain role with attribute "non_existent"


  Scenario: Getting role with etag
    When Role with name "Role name 1" for application id "1" is got with etag
    Then Response code is "304"
    And Body is empty


  Scenario: Getting role with expired etag
    #   1. property exists
    #   2. etag value is stored
    #   3. vat_id update changes etag
    #   4. previously stored (expired) tag is tested 

    When Role with name "Role name 1" for application id "1" is got for etag, forced new etag through update
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present

    # exact value
    And Body contains role with attribute "application_id" value "1"
    And Body contains role with attribute "role_description" value "optional description 1"
    And Body contains role with attribute "role_name" value "Role name 1"


  Scenario: Checking error code for nonexistent role
    When Nonexistent role id got
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Getting list of properties
    Given The following roles exist
      | applicationId | roleName    | roleDescription        |
      | 1             | List role name 1 | optional description 1 |
      | 1             | List role name 2 | optional description 2 |



    When List of roles exists with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> roles returned

    Examples:
    | description   | limit | cursor | returned |
    | default limit |       |        | 50       |
    | limit at 15   | 15    |        | 15       |
    | offset by 1   |       | 1      | 50       |
    | limit by 20   | 20    | 0      | 20       |
    | limit by 10   | 10    | 0      | 10       |
    | l:5 o:5       | 5     | 5      | 5        |


  Scenario Outline: Checking error codes for lists of roles
    When List of roles exists with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
    | description           | limit | cursor | response_code | custom_code |
    | negative cursor       |       | -1     | 400           | 63          |
    | cursor NaN            |       | text   | 400           | 63          |
    | negative limit        | -1    |        | 400           | 63          |
    | limit NaN             | text  |        | 400           | 63          |
    | limit ok, neg. cursor | 10    | -1     | 400           | 63          |
    | limit neg., cursor ok | -1    | 10     | 400           | 63          |
    | limit NaN, curson ok  | text  | 0      | 400           | 63          |
    | limit ok, cursor NaN  | 10    | text   | 400           | 63          |

  # negative values, strings, empty
  # wrong parameters (variables: parameter name, parameter value),