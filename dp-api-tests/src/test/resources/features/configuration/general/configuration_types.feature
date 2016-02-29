Feature: configuration_types

  #TODO add etag things to get/update/create

  Background:
    Given The following configuration types exist
      | identifier | description                               |
      | conf_id_1  | Description of configuration identifier 1 |
      | conf_id_2  | Description of configuration identifier 2 |
    Given The following configuration types exist with 2 random text items
      | identifier           | description                                          |
      | with_items_conf_id_1 | Description of configuration identifier 1 with items |
      | with_items_conf_id_2 | Description of configuration identifier 2 with items |

  Scenario: Creating Configuration Type
  POST /configuration/configurations

    When Configuration type is created
      | identifier        | description                                       |
      | created_conf_id_1 | Description of created configuration identifier 1 |
    Then Response code is "201"
    And Body contains configuration type with identifier "created_conf_id_1" and description "Description of created configuration identifier 1"
    And "Location" header is set and contains configuration type with identifier "created_conf_id_1"

  Scenario Outline: Checking error codes for creating configuration type
    When Data '<json_data>' is used for "<method>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_data                                                          | method | error_code | custom_code |
      | { "identifier":"", "description":"identifier is empty"}            | POST   | 400        | 61          |
      | { "description":"identifier is missing"}                           | POST   | 400        | 53          |
      | { "identifier": "conf_id_1", "description":"duplicate identifier"} | POST   | 400        | 62          |

  Scenario: Deleting Configuration Type
    When Configuration type with identifier "conf_id_1" is deleted
    Then Response code is "204"
    And Body is empty
    And Configuration type with identifier "conf_id_1" doesn't exist

  Scenario: Deleting nonexistent configuration type
    When Nonexistent configuration type id is deleted
    Then Response code is "204"

  #error states
  #id is missing
  #x-application is missing
  Scenario Outline: Updating configuration type description
    When Configuration type description is updated for identifier "conf_id_1" with description "<description>"
    Then Response code is "204"
    And Body is empty
    And Configuration type with identifier "conf_id_1" has description "<description>"

    Examples:
      | description         |
      | updated description |
      |                     |

  Scenario: Updating description of nonexisting configuration type
    Given Configuration type with identifier "nonexisting_id" doesn't exist
    When Configuration type description is updated for identifier "nonexisting_id" with description "New description"
    Then Response code is "404"
    And Custom code is "152"

  #error states
  #empty body, wrong id, wrong application id
  Scenario: Getting configuration type
    When Configuration type with with identifier "with_items_conf_id_1"  is got
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains configuration type with identifier "with_items_conf_id_1" and description "Description of configuration identifier 1 with items"


  Scenario: Getting configuration type with nonexisting id
    When Configuration type with with identifier "nonexisting_id"  is got
    Then Response code is "404"
    And Custom code is "152"

  # error states
  #wrong id, wrong/missing x-application
  Scenario Outline: Getting list of configuration types
    Given The following configuration types exist
      | identifier      | description                                                                    |
      | list_conf_id_1  | Description of configuration identifier 1 for listing all configuration types  |
      | list_conf_id_2  | Description of configuration identifier 2 for listing all configuration types  |
      | list_conf_id_3  | Description of configuration identifier 3 for listing all configuration types  |
      | list_conf_id_4  | Description of configuration identifier 4 for listing all configuration types  |
      | list_conf_id_5  | Description of configuration identifier 5 for listing all configuration types  |
      | list_conf_id_6  | Description of configuration identifier 6 for listing all configuration types  |
      | list_conf_id_7  | Description of configuration identifier 7 for listing all configuration types  |
      | list_conf_id_8  | Description of configuration identifier 8 for listing all configuration types  |
      | list_conf_id_9  | Description of configuration identifier 9 for listing all configuration types  |
      | list_conf_id_10 | Description of configuration identifier 10 for listing all configuration types |
      | list_conf_id_11 | Description of configuration identifier 11 for listing all configuration types |
      | list_conf_id_12 | Description of configuration identifier 12 for listing all configuration types |
      | list_conf_id_13 | Description of configuration identifier 13 for listing all configuration types |
      | list_conf_id_14 | Description of configuration identifier 14 for listing all configuration types |
      | list_conf_id_15 | Description of configuration identifier 15 for listing all configuration types |
      | list_conf_id_16 | Description of configuration identifier 16 for listing all configuration types |
      | list_conf_id_17 | Description of configuration identifier 17 for listing all configuration types |
      | list_conf_id_18 | Description of configuration identifier 18 for listing all configuration types |
      | list_conf_id_19 | Description of configuration identifier 19 for listing all configuration types |
      | list_conf_id_20 | Description of configuration identifier 20 for listing all configuration types |
      | list_conf_id_21 | Description of configuration identifier 21 for listing all configuration types |
      | list_conf_id_22 | Description of configuration identifier 22 for listing all configuration types |
      | list_conf_id_23 | Description of configuration identifier 23 for listing all configuration types |
      | list_conf_id_24 | Description of configuration identifier 24 for listing all configuration types |
      | list_conf_id_25 | Description of configuration identifier 25 for listing all configuration types |
      | list_conf_id_26 | Description of configuration identifier 26 for listing all configuration types |
      | list_conf_id_27 | Description of configuration identifier 27 for listing all configuration types |
      | list_conf_id_28 | Description of configuration identifier 28 for listing all configuration types |
      | list_conf_id_29 | Description of configuration identifier 29 for listing all configuration types |
      | list_conf_id_30 | Description of configuration identifier 30 for listing all configuration types |
      | list_conf_id_31 | Description of configuration identifier 31 for listing all configuration types |
      | list_conf_id_32 | Description of configuration identifier 32 for listing all configuration types |
      | list_conf_id_33 | Description of configuration identifier 33 for listing all configuration types |
      | list_conf_id_34 | Description of configuration identifier 34 for listing all configuration types |
      | list_conf_id_35 | Description of configuration identifier 35 for listing all configuration types |
      | list_conf_id_36 | Description of configuration identifier 36 for listing all configuration types |
      | list_conf_id_37 | Description of configuration identifier 37 for listing all configuration types |
      | list_conf_id_38 | Description of configuration identifier 38 for listing all configuration types |
      | list_conf_id_39 | Description of configuration identifier 39 for listing all configuration types |
      | list_conf_id_40 | Description of configuration identifier 40 for listing all configuration types |
      | list_conf_id_41 | Description of configuration identifier 41 for listing all configuration types |
      | list_conf_id_42 | Description of configuration identifier 42 for listing all configuration types |
      | list_conf_id_43 | Description of configuration identifier 43 for listing all configuration types |
      | list_conf_id_44 | Description of configuration identifier 44 for listing all configuration types |
      | list_conf_id_45 | Description of configuration identifier 45 for listing all configuration types |
      | list_conf_id_46 | Description of configuration identifier 46 for listing all configuration types |
      | list_conf_id_47 | Description of configuration identifier 47 for listing all configuration types |
      | list_conf_id_48 | Description of configuration identifier 48 for listing all configuration types |
      | list_conf_id_49 | Description of configuration identifier 49 for listing all configuration types |
      | list_conf_id_50 | Description of configuration identifier 50 for listing all configuration types |
      | list_conf_id_51 | Description of configuration identifier 51 for listing all configuration types |
      | list_conf_id_52 | Description of configuration identifier 52 for listing all configuration types |
      | list_conf_id_53 | Description of configuration identifier 53 for listing all configuration types |
      | list_conf_id_54 | Description of configuration identifier 54 for listing all configuration types |
      | list_conf_id_55 | Description of configuration identifier 55 for listing all configuration types |
      | list_conf_id_56 | Description of configuration identifier 56 for listing all configuration types |
      | list_conf_id_57 | Description of configuration identifier 57 for listing all configuration types |
      | list_conf_id_58 | Description of configuration identifier 58 for listing all configuration types |
      | list_conf_id_59 | Description of configuration identifier 59 for listing all configuration types |

    When List of configuration types is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> configuration types returned
    And Link header is '<link_header>'

    Examples:
      | limit | cursor | returned | link_header                                                                                      |
      | /null |        | 50       | </configurations?limit=50&cursor=50>; rel="next"                                                 |
      | /null | /null  | 50       | </configurations?limit=50&cursor=50>; rel="next"                                                 |
      |       |        | 50       | </configurations?limit=50&cursor=50>; rel="next"                                                 |
      |       | /null  | 50       | </configurations?limit=50&cursor=50>; rel="next"                                                 |
      | 15    |        | 15       | </configurations?limit=15&cursor=15>; rel="next"                                                 |
      |       | 1      | 50       | </configurations?limit=50&cursor=0>; rel="prev", </configurations?limit=50&cursor=51>; rel="next" |
      | 20    | 0      | 20       | </configurations?limit=20&cursor=20>; rel="next"                                                 |
      | 10    | 0      | 10       | </configurations?limit=10&cursor=10>; rel="next"                                                 |
      | 5     | 10     | 5        | </configurations?limit=5&cursor=5>; rel="prev", </configurations?limit=5&cursor=15>; rel="next"   |

  #given hodne hodnot, aby se dalo testovat
  #test limit, cursor, filter, sort with different values
  Scenario Outline: Checking error codes for getting list of configuration types
    When List of configuration types is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit | cursor | filter           | sort        | sort_desc   | response_code | custom_code |
      #limit and cursor
      | /null | -1     | /null            | /null       | /null       | 400           | 63          |
      |       | -1     | /null            | /null       | /null       | 400           | 63          |
      | /null | text   | /null            | /null       | /null       | 400           | 63          |
      |       | text   | /null            | /null       | /null       | 400           | 63          |
      | -1    |        | /null            | /null       | /null       | 400           | 63          |
      | -1    | /null  | /null            | /null       | /null       | 400           | 63          |
      | text  |        | /null            | /null       | /null       | 400           | 63          |
      | text  | /null  | /null            | /null       | /null       | 400           | 63          |
      | 10    | -1     | /null            | /null       | /null       | 400           | 63          |
      | text  | 0      | /null            | /null       | /null       | 400           | 63          |
      | 10    | text   | /null            | /null       | /null       | 400           | 63          |
      #filtering and sorting
      | 10    | 0      | /null            | identifier  | identifier  | 400           | 64          |
      | 10    | 0      | /null            | /null       | nonexistent | 400           | 63          |
      | 10    | 0      | /null            | nonexistent | /null       | 400           | 63          |
      | 10    | 0      | identifier==     | /null       | /null       | 400           | 63          |
      | 10    | 0      | nonexistent==CZ* | /null       | /null       | 400           | 63          |

  Scenario Outline: Filtering list of configurations types
    Given The following configurations exist for configuration type identifier "conf_id_2"
      | key          | value         | type   |
      | known_key_1  | text value 1  | string |
      | known_key_2  | text value 2  | string |
      | known_key_3  | text value 3  | string |
      | list_key_4   | text value 4  | string |
      | list_key_5   | same          | string |
      | list_key_6   | same          | string |
      | other_key_7  | text value 7  | string |
      | other_key_8  | text value 8  | string |
      | other_key_9  | text value 9  | string |
      | other_key_10 | special       | string |
      | other_key_11 | text value 11 | string |
      | other_key_12 | text value 12 | string |
      #| spec                   | 25             | integer |

    When List of configurations is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" for configuration type "conf_id_2"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> configurations returned
    And There are configurations with following keys returned in order: <expected_keys>

    Examples:
      | limit | cursor | returned | filter                          | sort      | sort_desc | expected_keys                                                     |
      | 5     | 0      | 5        | key=='other_key*'               | key       |           | other_key_7, other_key_8, other_key_9, other_key_10, other_key_11 |
      | 5     | 0      | 5        | key=='other_key*'               |           | key       | other_key_11, other_key_10, other_key_9, other_key_8, other_key_7 |
      | 5     | 2      | 3        | key=='other_key*'               | user_name |           | other_key_9, other_key_10, other_key_11                           |
      | 5     | 2      | 3        | key=='other_key*'               |           | user_name | other_key_9, other_key_8, other_key_7                             |
      | /null | /null  | 1        | key==known_key_3                | /null     | /null     | known_key_3                                                       |
      | /null | /null  | 2        | key==list_key_* and value==same | key       | /null     | list_key_5, list_key_6                                            |
      | /null | /null  | 1        | value==special                  | /null     | /null     | other_key_10                                                  |
