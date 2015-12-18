Feature: roles_get

  Background:
    Given Database is cleaned
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
    And Body contains entity with attribute "role_id"
    # exact value
    And Body contains entity with attribute "application_id" value "1"
    And Body contains entity with attribute "role_description" value "optional description 1"
    And Body contains entity with attribute "role_name" value "Role name 1"

    # non-existence
    And Body doesn't contain entity with attribute "non_existent"


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
    And Body contains entity with attribute "application_id" value "1"
    And Body contains entity with attribute "role_description" value "updated because of etag"
    And Body contains entity with attribute "role_name" value "Role name 1"


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

    When List of roles is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> roles returned
    And Link header is '<link_header>'
    And Total count is "<total>"

    Examples:
      | description   | limit | cursor | returned | total | link_header                                                                                       |
      | default limit | /null |        | 50       | 57    | </identity/roles?limit=50&cursor=50>; rel="next"                                                  |
      | default limit |       | /null  | 50       | 57    | </identity/roles?limit=50&cursor=50>; rel="next"                                                  |
      | default limit | /null | /null  | 50       | 57    | </identity/roles?limit=50&cursor=50>; rel="next"                                                  |
      | default limit |       |        | 50       | 57    | </identity/roles?limit=50&cursor=50>; rel="next"                                                  |
      | limit at 15   | 15    |        | 15       | 57    | </identity/roles?limit=15&cursor=15>; rel="next"                                                  |
      | offset by 1   |       | 1      | 50       | 57    | </identity/roles?limit=50&cursor=51>; rel="next", </identity/roles?limit=50&cursor=0>; rel="prev" |
      | limit by 20   | 20    | 0      | 20       | 57    | </identity/roles?limit=20&cursor=20>; rel="next"                                                  |
      | limit by 10   | 10    | 0      | 10       | 57    | </identity/roles?limit=10&cursor=10>; rel="next"                                                  |
      | l:5 o:5       | 5     | 10     | 5        | 57    | </identity/roles?limit=5&cursor=15>; rel="next", </identity/roles?limit=5&cursor=5>; rel="prev"   |


  Scenario Outline: Checking error codes for lists of roles
    When List of roles is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | description                 | limit | cursor | filter      | sort      | sort_desc | response_code | custom_code |
      #limit and cursor
      | negative cursor, null limit | /null | -1     | /null       | /null     | /null     | 400           | 63          |
      | --empty string limit        |       | -1     | /null       | /null     | /null     | 400           | 63          |
      | cursor NaN, null limit      | /null | text   | /null       | /null     | /null     | 400           | 63          |
      | --empty string limit        |       | text   | /null       | /null     | /null     | 400           | 63          |
      | negative limit, cursor null | -1    |        | /null       | /null     | /null     | 400           | 63          |
      | --cursor empty string       | -1    | /null  | /null       | /null     | /null     | 400           | 63          |
      | NaN limit                   | text  |        | /null       | /null     | /null     | 400           | 63          |
      | --"--                       | text  | /null  | /null       | /null     | /null     | 400           | 63          |
      |                             | 10    | -1     | /null       | /null     | /null     | 400           | 63          |
      |                             | text  | 0      | /null       | /null     | /null     | 400           | 63          |
      |                             | 10    | text   | /null       | /null     | /null     | 400           | 63          |
      #filtering and sorting
      | both sort and sort_desc     | 10    | 0      | /null       | role_name | role_name | 400           | 64          |
      #|| 10    | 0      | /null    | company_name |              | 400           | 63          |
      #|| 10    | 0      | /null    |              | company_name | 400           | 63          |
      #|| 10    | 0      | /null    | /null        |              | 400           | 63          |
      #|| 10    | 0      | /null    |              | /null        | 400           | 63          |
      #|| 10    | 0      | /null    |              |              | 400           | 63          |
      | invalid expression          | 10    | 0      | code==      | /null     | /null     | 400           | 63          |
      | invalid field  in filter    | 10    | 0      | role_n==aa* | /null     | /null     | 400           | 63          |
      | invalid field  in sort      | 10    | 0      | /null       | role_n    | /null     | 400           | 63          |
      | invalid field  in sort_desc | 10    | 0      | /null       | /null     | aaa       | 400           | 63          |

  Scenario Outline: Filtering list of roles
    Given The following roles exist
      | applicationId | roleName           | roleDescription         |
      | 1             | Filter role name 1 | optional description 1  |
      | 1             | Filter role name 2 | optional description 2  |
      | 2             | Filter role name 3 | different description 3 |
      | 2             | Filter role name 4 | different description 4 |
      | 2             | Filter role name 5 | optional description 5  |
      | 2             | Filter role name 6 | optional description 3  |

    When List of roles is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> roles returned
    And There are roles with following names returned in order: <expected_names>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                                               | sort      | sort_desc | expected_names                                                                                     |
      | 5     | 0      | 5        | 6     | role_name=='Filter role name*'                       | role_name |           | Filter role name 1, Filter role name 2, Filter role name 3, Filter role name 4, Filter role name 5 |
      | 5     | 0      | 5        | 6     | role_name=='Filter role name*'                       |           | role_name | Filter role name 6, Filter role name 5, Filter role name 4, Filter role name 3, Filter role name 2 |
      | 5     | 2      | 4        | 6     | role_name=='Filter role name*'                       | role_name |           | Filter role name 3, Filter role name 4, Filter role name 5, Filter role name 6                     |
      | 5     | 2      | 4        | 6     | role_name=='Filter role name*'                       |           | role_name | Filter role name 4, Filter role name 3, Filter role name 2, Filter role name 1                     |
      | 5     | 4      | 2        | 1     | role_name=='Filter role name*'                       | role_name |           | Filter role name 5, Filter role name 6                                                             |
      | /null | /null  | 1        | 1     | role_name=='Filter role name 6'                      | /null     | /null     | Filter role name 6                                                                                 |
      | /null | /null  | 2        | 2     | role_name=='Filter role name*' and application_id==1 | role_name | /null     | Filter role name 1, Filter role name 2                                                             |
      | /null | /null  | 4        | 4     | application_id==2                                    | role_name | /null     | Filter role name 3, Filter role name 4, Filter role name 5, Filter role name 6                     |
      | /null | /null  | 2        | 2     | role_description==different*                         | role_name | /null     | Filter role name 3, Filter role name 4                                                             |
  #add all fields

