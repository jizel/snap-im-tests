Feature: Applications UserProperty roles get

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1234fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users is created for customer "1234fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName | firstName | lastName | email                | timezone      | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Default partner is created
    Given Default application is created
    Given Switch for user property role tests
    Given The following roles exist
      | roleId                               | roleName    | description            | applicationId                        |
      | 972567b9-3dc2-477e-aa02-6e09465d22ae | user_role_1 | optional description 1 | 11111111-0000-4000-a000-111111111111 |

  @Smoke
  Scenario: Getting UserProperty role
    When Role with name "user_role_1" for application id "11111111-0000-4000-a000-111111111111" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "application_id" value "11111111-0000-4000-a000-111111111111"
    And Body contains entity with attribute "name" value "user_role_1"
    And Body contains entity with attribute "description" value "optional description 1"
    And Body contains entity with attribute "role_id" value "972567b9-3dc2-477e-aa02-6e09465d22ae"

  Scenario Outline: Getting list of applications UserProperty roles
    Given The following roles exist
      | applicationId                        | roleName     | description         |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_1  | Test description 1  |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_2  | Test description 2  |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_3  | Test description 3  |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_4  | Test description 4  |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_5  | Test description 5  |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_6  | Test description 6  |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_7  | Test description 7  |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_8  | Test description 8  |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_9  | Test description 9  |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_10 | Test description 10 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_11 | Test description 11 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_12 | Test description 12 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_13 | Test description 13 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_14 | Test description 14 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_15 | Test description 15 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_16 | Test description 16 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_17 | Test description 17 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_18 | Test description 18 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_19 | Test description 19 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_20 | Test description 20 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_21 | Test description 21 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_22 | Test description 22 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_23 | Test description 23 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_24 | Test description 24 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_25 | Test description 25 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_26 | Test description 26 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_27 | Test description 27 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_28 | Test description 28 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_29 | Test description 29 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_30 | Test description 30 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_31 | Test description 31 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_32 | Test description 32 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_33 | Test description 33 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_34 | Test description 34 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_35 | Test description 35 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_36 | Test description 36 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_37 | Test description 37 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_38 | Test description 38 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_39 | Test description 39 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_40 | Test description 40 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_41 | Test description 41 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_42 | Test description 42 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_43 | Test description 43 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_44 | Test description 44 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_45 | Test description 45 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_46 | Test description 46 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_47 | Test description 47 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_48 | Test description 48 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_49 | Test description 49 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_50 | Test description 50 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_51 | Test description 51 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_52 | Test description 52 |
    When List of applications roles is got for application with id "11111111-0000-4000-a000-111111111111" and limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> applications roles returned
    And Link header is '<link_header>'
    And Total count is "<total>"
    Examples:
      | limit | cursor | returned | total | link_header                                                                                                                                                                                           |
      | /null |        | 50       | 53    | </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=50&cursor=50>; rel="next"                                                                                                    |
      | /null | /null  | 50       | 53    | </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=50&cursor=50>; rel="next"                                                                                                    |
      |       |        | 50       | 53    | </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=50&cursor=50>; rel="next"                                                                                                    |
      |       | /null  | 50       | 53    | </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=50&cursor=50>; rel="next"                                                                                                    |
      | 15    |        | 15       | 53    | </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=15&cursor=15>; rel="next"                                                                                                    |
      |       | 1      | 50       | 53    | </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=50&cursor=51>; rel="next", </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=50&cursor=0>; rel="prev" |
      | 20    | 0      | 20       | 53    | </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=20&cursor=20>; rel="next"                                                                                                    |
      | 10    | 0      | 10       | 53    | </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=10&cursor=10>; rel="next"                                                                                                    |
      | 5     | 10     | 5        | 53    | </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=5&cursor=15>; rel="next", </identity/applications/11111111-0000-4000-a000-111111111111/roles?limit=5&cursor=5>; rel="prev"   |

  Scenario Outline: Filtering list of applications UserProperty roles
    Given The following roles exist
      | applicationId                        | roleName     | description         |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_11 | Test description 11 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_12 | Test description 12 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_13 | Test description 13 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_21 | Test description 21 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_22 | Test description 22 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_23 | Test description 23 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_31 | Test description 31 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_32 | Test description 32 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_33 | Test description 33 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_34 | Test description 34 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_35 | Test description 35 |
      | 11111111-0000-4000-a000-111111111111 | Test_Role_36 | Test description 36 |
    When List of applications roles is got for application with id "11111111-0000-4000-a000-111111111111" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> applications roles returned
    And There are applications roles with following names returned in order: <expected_names>
    And Total count is "<total>"
    Examples:
      | limit | cursor | returned | total | filter             | sort  | sort_desc | expected_names                                                       |
      | 5     | 0      | 5        | 12    | name=='Test_Role*' | name  | /null     | Test_Role_11, Test_Role_12, Test_Role_13, Test_Role_21, Test_Role_22 |
      | 5     | 0      | 5        | 12    | name=='Test_Role*' | /null | name      | Test_Role_36, Test_Role_35, Test_Role_34, Test_Role_33, Test_Role_32 |
      | 5     | 9      | 3        | 12    | name=='Test_Role*' | name  | /null     | Test_Role_34, Test_Role_35, Test_Role_36                             |
      | 5     | 9      | 3        | 12    | name=='Test_Role*' | /null | name      | Test_Role_13, Test_Role_12, Test_Role_11                             |
      | 5     | 10     | 2        | 12    | name=='Test_Role*' | name  | /null     | Test_Role_35, Test_Role_36                                           |
      | /null | /null  | 1        | 1     | name=='*Role_34'   | /null | /null     | Test_Role_34                                                         |

  Scenario Outline: Checking error codes for getting list of applications UserProperty roles
    When List of applications roles is got for application with id "11111111-0000-4000-a000-111111111111" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
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
      | 10          | 0      | /null  | role_name   | role_name   | 400           | 40002          |
      | 10          | 0      | /null  | /null       | nonexistent | 400           | 40002          |
      | 10          | 0      | /null  | nonexistent | /null       | 400           | 40002          |
      | 10          | 0      | code== | /null       | /null       | 400           | 40002          |
