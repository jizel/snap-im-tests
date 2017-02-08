@Identity
Feature: Roles get user customer

  Background:
    Given Database is cleaned
    Given Switch for user customer role tests
    Given Default Snapshot user is created
    Given Default partner is created
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        | partnerId                           | isInternal |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |11111111-0000-4000-a000-222222222222 | false      |
      | Application test company 2 | Application description 2 | http://www.snapshot.travel | b318fd9a-a05d-42d8-8e84-42e904ace123 |11111111-0000-4000-a000-222222222222 | false      |

    Given The following roles exist
      | applicationId                        | roleName    | description            |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 1 | optional description 1 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 2 | optional description 2 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 3 | optional description 3 |

  @Smoke
  Scenario: Getting role
    When Role with name "Role name 1" for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "role_id"
    And Body contains entity with attribute "application_id" value "a318fd9a-a05d-42d8-8e84-42e904ace123"
    And Body contains entity with attribute "role_description" value "optional description 1"
    And Body contains entity with attribute "name" value "Role name 1"
    And Body doesn't contain entity with attribute "non_existent"


  Scenario: Getting role with etag
    When Role with name "Role name 1" for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" is got with etag
    Then Response code is "304"
    And Body is empty


  Scenario: Getting role with expired etag
    #   1. property exists
    #   2. etag value is stored
    #   3. vat_id update changes etag
    #   4. previously stored (expired) tag is tested

    When Role with name "Role name 1" for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" is got for etag, forced new etag through update
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "application_id" value "a318fd9a-a05d-42d8-8e84-42e904ace123"
    And Body contains entity with attribute "role_description" value "updated because of etag"
    And Body contains entity with attribute "name" value "Role name 1"


  Scenario: Checking error code for nonexistent role
    When Nonexistent role id got
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Getting list of roles
    Given The following roles exist
      | applicationId                        | roleName          | description             |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 1  | optional description 1  |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 2  | optional description 2  |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 3  | optional description 3  |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 4  | optional description 4  |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 5  | optional description 5  |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 6  | optional description 6  |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 7  | optional description 7  |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 8  | optional description 8  |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 9  | optional description 9  |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 10 | optional description 10 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 11 | optional description 11 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 12 | optional description 12 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 13 | optional description 13 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 14 | optional description 14 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 15 | optional description 15 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 16 | optional description 16 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 17 | optional description 17 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 18 | optional description 18 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 19 | optional description 19 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 20 | optional description 20 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 21 | optional description 21 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 22 | optional description 22 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 23 | optional description 23 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 24 | optional description 24 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 25 | optional description 25 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 26 | optional description 26 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 27 | optional description 27 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 28 | optional description 28 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 29 | optional description 29 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 30 | optional description 30 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 31 | optional description 31 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 32 | optional description 32 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 33 | optional description 33 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 34 | optional description 34 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 35 | optional description 35 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 36 | optional description 36 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 37 | optional description 37 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 38 | optional description 38 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 39 | optional description 39 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 40 | optional description 40 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 41 | optional description 41 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 42 | optional description 42 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 43 | optional description 43 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 44 | optional description 44 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 45 | optional description 45 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 46 | optional description 46 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 47 | optional description 47 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 48 | optional description 48 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 49 | optional description 49 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 50 | optional description 50 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 51 | optional description 51 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 52 | optional description 52 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 53 | optional description 53 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | List role name 54 | optional description 54 |

    When List of roles is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> roles returned
    And Link header is '<link_header>'
    And Total count is "<total>"

    Examples:
      | description   | limit | cursor | returned | total | link_header                                                                                                                   |
      | default limit | /null |        | 50       | 57    | </identity/user_customer_roles?limit=50&cursor=50>; rel="next"                                                                |
      | default limit |       | /null  | 50       | 57    | </identity/user_customer_roles?limit=50&cursor=50>; rel="next"                                                                |
      | default limit | /null | /null  | 50       | 57    | </identity/user_customer_roles?limit=50&cursor=50>; rel="next"                                                                |
      | default limit |       |        | 50       | 57    | </identity/user_customer_roles?limit=50&cursor=50>; rel="next"                                                                |
      | limit at 15   | 15    |        | 15       | 57    | </identity/user_customer_roles?limit=15&cursor=15>; rel="next"                                                                |
      | offset by 1   |       | 1      | 50       | 57    | </identity/user_customer_roles?limit=50&cursor=0>; rel="prev", </identity/user_customer_roles?limit=50&cursor=51>; rel="next" |
      | limit by 20   | 20    | 0      | 20       | 57    | </identity/user_customer_roles?limit=20&cursor=20>; rel="next"                                                                |
      | limit by 10   | 10    | 0      | 10       | 57    | </identity/user_customer_roles?limit=10&cursor=10>; rel="next"                                                                |
      | l:5 o:5       | 5     | 10     | 5        | 57    | </identity/user_customer_roles?limit=5&cursor=5>; rel="prev", </identity/user_customer_roles?limit=5&cursor=15>; rel="next"   |


  Scenario Outline: Checking error codes for lists of roles
    When List of roles is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | description                 | limit | cursor | filter      | sort   | sort_desc | response_code | custom_code |

      #limit and cursor
      | negative cursor, null limit | /null | -1     | /null       | /null  | /null     | 400           | 40002       |
      | --empty string limit        |       | -1     | /null       | /null  | /null     | 400           | 40002       |
      | cursor NaN, null limit      | /null | text   | /null       | /null  | /null     | 400           | 40002       |
      | --empty string limit        |       | text   | /null       | /null  | /null     | 400           | 40002       |
      | negative limit, cursor null | -1    |        | /null       | /null  | /null     | 400           | 40002       |
      | --cursor empty string       | -1    | /null  | /null       | /null  | /null     | 400           | 40002       |
      | NaN limit                   | text  |        | /null       | /null  | /null     | 400           | 40002       |
      | --"--                       | text  | /null  | /null       | /null  | /null     | 400           | 40002       |
      |                             | 10    | -1     | /null       | /null  | /null     | 400           | 40002       |
      |                             | text  | 0      | /null       | /null  | /null     | 400           | 40002       |
      |                             | 10    | text   | /null       | /null  | /null     | 400           | 40002       |

      #filtering and sorting
      | both sort and sort_desc     | 10    | 0      | /null       | name   | name      | 400           | 40002       |
      #|| 10    | 0      | /null    | company_name |              | 400           | 40002       |
      #|| 10    | 0      | /null    |              | company_name | 400           | 40002       |
      #|| 10    | 0      | /null    | /null        |              | 400           | 40002       |
      #|| 10    | 0      | /null    |              | /null        | 400           | 40002       |
      #|| 10    | 0      | /null    |              |              | 400           | 40002       |
      | invalid expression          | 10    | 0      | code==      | /null  | /null     | 400           | 40002       |
      | invalid field  in filter    | 10    | 0      | role_n==aa* | /null  | /null     | 400           | 40002       |
      | invalid field  in sort      | 10    | 0      | /null       | role_n | /null     | 400           | 40002       |
      | invalid field  in sort_desc | 10    | 0      | /null       | /null  | aaa       | 400           | 40002       |

  Scenario Outline: Filtering list of roles
    Given The following roles exist
      | applicationId                        | roleName           | description             |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Filter role name 1 | optional description 1  |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Filter role name 2 | optional description 2  |
      | b318fd9a-a05d-42d8-8e84-42e904ace123 | Filter role name 3 | different description 3 |
      | b318fd9a-a05d-42d8-8e84-42e904ace123 | Filter role name 4 | different description 4 |
      | b318fd9a-a05d-42d8-8e84-42e904ace123 | Filter role name 5 | optional description 5  |
      | b318fd9a-a05d-42d8-8e84-42e904ace123 | Filter role name 6 | optional description 3  |

    When List of roles is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> roles returned
    And There are roles with following names returned in order: <expected_names>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                                                                             | sort  | sort_desc | expected_names                                                                                     |
      | 5     | 0      | 5        | 6     | name=='Filter role name*'                                                          | name  |           | Filter role name 1, Filter role name 2, Filter role name 3, Filter role name 4, Filter role name 5 |
      | 5     | 0      | 5        | 6     | name=='Filter role name*'                                                          |       | name      | Filter role name 6, Filter role name 5, Filter role name 4, Filter role name 3, Filter role name 2 |
      | 5     | 2      | 4        | 6     | name=='Filter role name*'                                                          | name  |           | Filter role name 3, Filter role name 4, Filter role name 5, Filter role name 6                     |
      | 5     | 2      | 4        | 6     | name=='Filter role name*'                                                          |       | name      | Filter role name 4, Filter role name 3, Filter role name 2, Filter role name 1                     |
      | 5     | 4      | 2        | 6     | name=='Filter role name*'                                                          | name  |           | Filter role name 5, Filter role name 6                                                             |
      | /null | /null  | 1        | 1     | name=='Filter role name 6'                                                         | /null | /null     | Filter role name 6                                                                                 |
      | /null | /null  | 2        | 2     | name=='Filter role name*' and application_id==a318fd9a-a05d-42d8-8e84-42e904ace123 | name  | /null     | Filter role name 1, Filter role name 2                                                             |
      | /null | /null  | 4        | 4     | application_id==b318fd9a-a05d-42d8-8e84-42e904ace123                               | name  | /null     | Filter role name 3, Filter role name 4, Filter role name 5, Filter role name 6                     |
      | /null | /null  | 2        | 2     | description=='different*'                                                          | name  | /null     | Filter role name 3, Filter role name 4                                                             |
  #add all fields

