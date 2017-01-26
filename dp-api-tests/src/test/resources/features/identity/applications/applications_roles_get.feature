@Identity
Feature: Applications roles get

  Background:
    Given Database is cleaned
#    TODO: Do application tests for all supported role types
    Given Switch for user customer role tests
    Given Default partner is created
    Given Default application is created

  @Smoke
  Scenario: Getting applications roles
    Given The following roles exist
      | applicationId                        | roleName    | description        |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_1 | Test description 1 |
    When Applications roles for application id "11111111-0000-4000-a000-111111111111" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "role_id"
    And Body contains entity with attribute "role_name"

  Scenario Outline: Getting list of applications roles
    Given The following roles exist
      | applicationId                        | roleName     | description         |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_1  | Test description 1  |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_2  | Test description 2  |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_3  | Test description 3  |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_4  | Test description 4  |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_5  | Test description 5  |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_6  | Test description 6  |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_7  | Test description 7  |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_8  | Test description 8  |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_9  | Test description 9  |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_10 | Test description 10 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_11 | Test description 11 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_12 | Test description 12 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_13 | Test description 13 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_14 | Test description 14 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_15 | Test description 15 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_16 | Test description 16 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_17 | Test description 17 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_18 | Test description 18 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_19 | Test description 19 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_20 | Test description 20 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_21 | Test description 21 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_22 | Test description 22 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_23 | Test description 23 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_24 | Test description 24 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_25 | Test description 25 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_26 | Test description 26 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_27 | Test description 27 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_28 | Test description 28 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_29 | Test description 29 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_30 | Test description 30 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_31 | Test description 31 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_32 | Test description 32 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_33 | Test description 33 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_34 | Test description 34 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_35 | Test description 35 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_36 | Test description 36 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_37 | Test description 37 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_38 | Test description 38 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_39 | Test description 39 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_40 | Test description 40 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_41 | Test description 41 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_42 | Test description 42 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_43 | Test description 43 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_44 | Test description 44 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_45 | Test description 45 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_46 | Test description 46 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_47 | Test description 47 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_48 | Test description 48 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_49 | Test description 49 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_50 | Test description 50 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_51 | Test description 51 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_52 | Test description 52 |
    When List of applications roles is got for application with id "a318fd9a-a05d-42d8-8e84-42e904ace111" and limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> applications roles returned
    And Link header is '<link_header>'
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | link_header                                                                                                                                                                                           |
      | /null |        | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=50&cursor=50>; rel="next"                                                                                                    |
      | /null | /null  | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=50&cursor=50>; rel="next"                                                                                                    |
      |       |        | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=50&cursor=50>; rel="next"                                                                                                    |
      |       | /null  | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=50&cursor=50>; rel="next"                                                                                                    |
      | 15    |        | 15       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=15&cursor=15>; rel="next"                                                                                                    |
      |       | 1      | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=50&cursor=51>; rel="next", </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=50&cursor=0>; rel="prev" |
      | 20    | 0      | 20       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=20&cursor=20>; rel="next"                                                                                                    |
      | 10    | 0      | 10       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=10&cursor=10>; rel="next"                                                                                                    |
      | 5     | 10     | 5        | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=5&cursor=15>; rel="next", </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace111/roles?limit=5&cursor=5>; rel="prev"   |

  Scenario Outline: Filtering list of applications roles
    Given Switch for user customer role tests
    Given The following roles exist
      | applicationId                        | roleName     | description         |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_11 | Test description 11 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_12 | Test description 12 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_13 | Test description 13 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_21 | Test description 21 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_22 | Test description 22 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_23 | Test description 23 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_31 | Test description 31 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_32 | Test description 32 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_33 | Test description 33 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_34 | Test description 34 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_35 | Test description 35 |
      | a318fd9a-a05d-42d8-8e84-42e904ace111 | Test_Role_36 | Test description 36 |
    When List of applications roles is got for application with id "a318fd9a-a05d-42d8-8e84-42e904ace111" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> applications roles returned
    And There are applications roles with following names returned in order: <expected_names>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                  | sort      | sort_desc | expected_names                                                       |
      | 5     | 0      | 5        | 12    | name=='Test_Role*'      | name      | /null     | Test_Role_11, Test_Role_12, Test_Role_13, Test_Role_21, Test_Role_22 |
      | 5     | 0      | 5        | 12    | name=='Test_Role*'      | /null     | name      | Test_Role_36, Test_Role_35, Test_Role_34, Test_Role_33, Test_Role_32 |
      | 5     | 9      | 3        | 12    | name=='Test_Role*'      | name      | /null     | Test_Role_34, Test_Role_35, Test_Role_36                             |
      | 5     | 9      | 3        | 12    | name=='Test_Role*'      | /null     | name      | Test_Role_13, Test_Role_12, Test_Role_11                             |
      | 5     | 10     | 2        | 12    | name=='Test_Role*'      | name      | /null     | Test_Role_35, Test_Role_36                                           |
      | /null | /null  | 1        | 1     | name=='*Role_34'        | /null     | /null     | Test_Role_34                                                         |

  Scenario Outline: Checking error codes for getting list of applications roles
    When List of applications roles is got for application with id "a318fd9a-a05d-42d8-8e84-42e904ace111" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter | sort        | sort_desc   | response_code | custom_code |
      | /null       | -1     | /null  | /null       | /null       | 400           | 40002          |
      |             | -1     | /null  | /null       | /null       | 400           | 40002          |
      | /null       | text   | /null  | /null       | /null       | 400           | 40002          |
      |             | text   | /null  | /null       | /null       | 400           | 40002          |
      | -1          |        | /null  | /null       | /null       | 400           | 40002          |
      | -1          | /null  | /null  | /null       | /null       | 400           | 40002          |
      | 201         | /null  | /null  | /null       | /null       | 400           | 40002          |
      | 21474836470 | /null  | /null  | /null       | /null       | 400           | 40002          |
      | text        |        | /null  | /null       | /null       | 400           | 40002          |
      | text        | /null  | /null  | /null       | /null       | 400           | 40002          |
      | 10          | -1     | /null  | /null       | /null       | 400           | 40002          |
      | text        | 0      | /null  | /null       | /null       | 400           | 40002          |
      | 10          | text   | /null  | /null       | /null       | 400           | 40002          |
      | 10          | 0      | /null  | name        | name        | 400           | 40002          |
      | 10          | 0      | /null  | /null       | nonexistent | 400           | 40002          |
      | 10          | 0      | /null  | nonexistent | /null       | 400           | 40002          |
      | 10          | 0      | code== | /null       | /null       | 400           | 40002          |
