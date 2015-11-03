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
    And Body contains role with attribute "role_description" value "updated because of etag"
    And Body contains role with attribute "role_name" value "Role name 1"


  Scenario: Checking error code for nonexistent role
    When Nonexistent role id got
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Getting list of roles
    Given The following roles exist
      | applicationId | roleName          | roleDescription         |
      | 1             | List role name 1  | optional description 1  |
      | 1             | List role name 2  | optional description 2  |
      | 1             | List role name 3  | optional description 3  |
      | 1             | List role name 4  | optional description 4  |
      | 1             | List role name 5  | optional description 5  |
      | 1             | List role name 6  | optional description 6  |
      | 1             | List role name 7  | optional description 7  |
      | 1             | List role name 8  | optional description 8  |
      | 1             | List role name 9  | optional description 9  |
      | 1             | List role name 10 | optional description 10 |
      | 1             | List role name 11 | optional description 11 |
      | 1             | List role name 12 | optional description 12 |
      | 1             | List role name 13 | optional description 13 |
      | 1             | List role name 14 | optional description 14 |
      | 1             | List role name 15 | optional description 15 |
      | 1             | List role name 16 | optional description 16 |
      | 1             | List role name 17 | optional description 17 |
      | 1             | List role name 18 | optional description 18 |
      | 1             | List role name 19 | optional description 19 |
      | 1             | List role name 20 | optional description 20 |
      | 1             | List role name 21 | optional description 21 |
      | 1             | List role name 22 | optional description 22 |
      | 1             | List role name 23 | optional description 23 |
      | 1             | List role name 24 | optional description 24 |
      | 1             | List role name 25 | optional description 25 |
      | 1             | List role name 26 | optional description 26 |
      | 1             | List role name 27 | optional description 27 |
      | 1             | List role name 28 | optional description 28 |
      | 1             | List role name 29 | optional description 29 |
      | 1             | List role name 30 | optional description 30 |
      | 1             | List role name 31 | optional description 31 |
      | 1             | List role name 32 | optional description 32 |
      | 1             | List role name 33 | optional description 33 |
      | 1             | List role name 34 | optional description 34 |
      | 1             | List role name 35 | optional description 35 |
      | 1             | List role name 36 | optional description 36 |
      | 1             | List role name 37 | optional description 37 |
      | 1             | List role name 38 | optional description 38 |
      | 1             | List role name 39 | optional description 39 |
      | 1             | List role name 40 | optional description 40 |
      | 1             | List role name 41 | optional description 41 |
      | 1             | List role name 42 | optional description 42 |
      | 1             | List role name 43 | optional description 43 |
      | 1             | List role name 44 | optional description 44 |
      | 1             | List role name 45 | optional description 45 |
      | 1             | List role name 46 | optional description 46 |
      | 1             | List role name 47 | optional description 47 |
      | 1             | List role name 48 | optional description 48 |
      | 1             | List role name 49 | optional description 49 |
      | 1             | List role name 50 | optional description 50 |
      | 1             | List role name 51 | optional description 51 |
      | 1             | List role name 52 | optional description 52 |
      | 1             | List role name 53 | optional description 53 |
      | 1             | List role name 54 | optional description 54 |

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