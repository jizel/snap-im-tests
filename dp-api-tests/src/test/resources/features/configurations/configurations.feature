@Configurations
Feature: Configurations create update delete

  #TODO add etag things to get/update/create

  Background:
    Given The following configuration types exist
      | identifier | description                               |
      | conf_id_1  | Description of configuration identifier 1 |
      | conf_id_2  | Description of configuration identifier 2 |

  Scenario Outline: add configuration key:value
    When Configuration is created for configuration type "conf_id_1"
      | key   | value   | type   |
      | <key> | <value> | <type> |
    Then Response code is "201"
    And Body contains configuration
      | key   | value   | type   |
      | <key> | <value> | <type> |
    And "Location" header is set and contains configuration with key "<key>"

    Examples:
      | key                 | value                                        | type     |
      | string_test_key1    | text value                                   | string   |
      | integer_test_key_2  | 11                                           | integer  |
      | long_test_key_2     | 114444                                       | long     |
      | double_test_key_2   | 12.34                                        | double   |
      | boolean_test_key_2  | true                                         | boolean  |
      | date_test_key_2     | 2015-01-01                                   | date     |
      | datetime_test_key_2 | 2015-01-01T10:10                             | datetime |
      | object_test_key_3   | {"property_1": "value_1", "property_2": 45 } | object   |

  Scenario Outline: Checking error codes for adding configuration key:value
    Given The following configurations exist for configuration type identifier "conf_id_1"
      | key              | value      | type   |
      | given_test_key_1 | text_value | STRING |
    When Configuration is created for configuration type "conf_id_1"
      | key   | value   | type   |
      | <key> | <value> | <type> |
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | key                 | value           | type     | response_code | custom_code |
      | /null               | 11              | integer  | 400           | 53          |
      |                     | 11              | integer  | 400           | 61          |
      | given_test_key_1    | text value2     | string   | 400           | 62          |
      | test_key_1          | /null           | string   | 400           | 53          |
      | test_key_2          |                 | string   | 400           | 61          |
      | test_key_3          | /null           | integer  | 400           | 53          |
      | test_key_4          | /null           | object   | 400           | 53          |
      | test_key_5          |                 | date     | 400           | 61          |
      | test_key_6          |                 | datetime | 400           | 61          |
      | test_date_key_1     | 2015-xxx-11     | date     | 400           | 63          |
      | test_datetime_key_1 | 2015-01-01Taaaa | datetime | 400           | 63          |

  #errors
  #missing application
  #no key, empty key, wrong value
  #create already created
  @Smoke
  Scenario: delete configuration key:value
    Given The following configurations exist for configuration type identifier "conf_id_1"
      | key              | value      | type   |
      | given_test_key_1 | text value | string |
    When Configuration with identifier "given_test_key_1" is deleted from identifier "conf_id_1"
    Then Response code is "204"
    And Body is empty
    And Configuration with key "given_test_key_1" doesn't exist for configuration type "conf_id_1"

  #TODO Etag
  Scenario Outline: update configuration value for key
    Given The following configurations exist for configuration type identifier "conf_id_1"
      | key   | value       | type       |
      | <key> | <old_value> | <old_type> |
    When Configuration with from identifier "conf_id_1" is updated
      | key   | value       | type       |
      | <key> | <new_value> | <new_type> |
    Then Response code is "204"
    And Body is empty
    And Configuration from identifier "conf_id_1" has following
      | key   | value       | type       |
      | <key> | <new_value> | <new_type> |

    Examples:
      | key              | old_value  | old_type | new_value | new_type |
      | given_test_key_1 | text value | string   | new value | string   |


  Scenario Outline: get configuration value for key
    Given The following configurations exist for configuration type identifier "conf_id_1"
      | key   | value   | type   |
      | <key> | <value> | <type> |
    When Configuration with key "<key>" is got from configuration type "conf_id_1"
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains configurationValue
      | key   | value   | type   |
      | <key> | <value> | <type> |

    Examples:
      | key                 | value                                        | type     |
      | string_test_key1    | text value                                   | string   |
      | integer_test_key_2  | 11                                           | integer  |
      | long_test_key_2     | 114444                                       | long     |
      | double_test_key_2   | 12.34                                        | double   |
      | boolean_test_key_2  | true                                         | boolean  |
      | object_test_key_3   | {"property_1": "value_1", "property_2": 45 } | object   |
      | date_test_key_2     | 2015-01-01                                   | date     |
      | datetime_test_key_2 | 2015-01-01T10:10                             | datetime |


  Scenario Outline: Checking errors for getting configuration value for key
    When Configuration with key "<key>" is got from configuration type "<identifier>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | identifier | key        | response_code | custom_code |
      | conf_id_1  | wrong_key  | 404           | 152         |
      | wrong_id   | test_key_1 | 404           | 152         |

  # errors
  #wrong key
  Scenario Outline: get all configuration key:values from configuration type
    Given The following configurations exist for configuration type identifier "conf_id_1"
      | key                    | value         | type   |
      | list_given_test_key_1  | text value 1  | string |
      | list_given_test_key_2  | text value 2  | string |
      | list_given_test_key_3  | text value 3  | string |
      | list_given_test_key_4  | text value 4  | string |
      | list_given_test_key_5  | text value 5  | string |
      | list_given_test_key_6  | text value 6  | string |
      | list_given_test_key_7  | text value 7  | string |
      | list_given_test_key_8  | text value 8  | string |
      | list_given_test_key_9  | text value 9  | string |
      | list_given_test_key_10 | text value 10 | string |
      | list_given_test_key_11 | text value 11 | string |
      | list_given_test_key_12 | text value 12 | string |
      | list_given_test_key_13 | text value 13 | string |
      | list_given_test_key_14 | text value 14 | string |
      | list_given_test_key_15 | text value 15 | string |
      | list_given_test_key_16 | text value 16 | string |
      | list_given_test_key_17 | text value 17 | string |
      | list_given_test_key_18 | text value 18 | string |
      | list_given_test_key_19 | text value 19 | string |
      | list_given_test_key_20 | text value 20 | string |
      | list_given_test_key_21 | text value 21 | string |
      | list_given_test_key_22 | text value 22 | string |
      | list_given_test_key_23 | text value 23 | string |
      | list_given_test_key_24 | text value 24 | string |
      | list_given_test_key_25 | text value 25 | string |
      | list_given_test_key_26 | text value 26 | string |
      | list_given_test_key_27 | text value 27 | string |
      | list_given_test_key_28 | text value 28 | string |
      | list_given_test_key_29 | text value 29 | string |
      | list_given_test_key_30 | text value 30 | string |
      | list_given_test_key_31 | text value 31 | string |
      | list_given_test_key_32 | text value 32 | string |
      | list_given_test_key_33 | text value 33 | string |
      | list_given_test_key_34 | text value 34 | string |
      | list_given_test_key_35 | text value 35 | string |
      | list_given_test_key_36 | text value 36 | string |
      | list_given_test_key_37 | text value 37 | string |
      | list_given_test_key_38 | text value 38 | string |
      | list_given_test_key_39 | text value 39 | string |
      | list_given_test_key_40 | text value 40 | string |
      | list_given_test_key_41 | text value 41 | string |
      | list_given_test_key_42 | text value 42 | string |
      | list_given_test_key_43 | text value 43 | string |
      | list_given_test_key_44 | text value 44 | string |
      | list_given_test_key_45 | text value 45 | string |
      | list_given_test_key_46 | text value 46 | string |
      | list_given_test_key_47 | text value 47 | string |
      | list_given_test_key_48 | text value 48 | string |
      | list_given_test_key_49 | text value 49 | string |
      | list_given_test_key_50 | text value 50 | string |
      | list_given_test_key_51 | text value 51 | string |
      | list_given_test_key_52 | text value 52 | string |
      | list_given_test_key_53 | text value 53 | string |
      | list_given_test_key_54 | text value 54 | string |
      | list_given_test_key_55 | text value 55 | string |
      | list_given_test_key_56 | text value 56 | string |
      | list_given_test_key_57 | text value 57 | string |
      | list_given_test_key_58 | text value 58 | string |
      | list_given_test_key_59 | text value 59 | string |

    When List of configurations is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null" for configuration type "conf_id_1"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> configurations returned
    And Link header is '<link_header>'

    Examples:
      | limit | cursor | returned | link_header                                                                                                                           |
      | /null |        | 50       | </configurations/conf_id_1/records?limit=50&cursor=50>; rel="next"                                                                    |
      | /null | /null  | 50       | </configurations/conf_id_1/records?limit=50&cursor=50>; rel="next"                                                                    |
      |       |        | 50       | </configurations/conf_id_1/records?limit=50&cursor=50>; rel="next"                                                                    |
      |       | /null  | 50       | </configurations/conf_id_1/records?limit=50&cursor=50>; rel="next"                                                                    |
      | 15    |        | 15       | </configurations/conf_id_1/records?limit=15&cursor=15>; rel="next"                                                                    |
      |       | 1      | 50       | </configurations/conf_id_1/records?limit=50&cursor=0>; rel="prev", </configurations/conf_id_1/records?limit=50&cursor=51>; rel="next" |
      | 20    | 0      | 20       | </configurations/conf_id_1/records?limit=20&cursor=20>; rel="next"                                                                    |
      | 10    | 0      | 10       | </configurations/conf_id_1/records?limit=10&cursor=10>; rel="next"                                                                    |
      | 5     | 10     | 5        | </configurations/conf_id_1/records?limit=5&cursor=5>; rel="prev", </configurations/conf_id_1/records?limit=5&cursor=15>; rel="next"   |

  Scenario Outline: Checking error codes for getting list of configurations for configuration type "conf_id_1"
    When List of configurations is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" for configuration type "conf_id_1"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit | cursor | filter          | sort        | sort_desc   | response_code | custom_code |
      #limit and cursor
      | /null | -1     | /null           | /null       | /null       | 400           | 63          |
      |       | -1     | /null           | /null       | /null       | 400           | 63          |
      | /null | text   | /null           | /null       | /null       | 400           | 63          |
      |       | text   | /null           | /null       | /null       | 400           | 63          |
      | -1    |        | /null           | /null       | /null       | 400           | 63          |
      | -1    | /null  | /null           | /null       | /null       | 400           | 63          |
      | text  |        | /null           | /null       | /null       | 400           | 63          |
      | text  | /null  | /null           | /null       | /null       | 400           | 63          |
      | 10    | -1     | /null           | /null       | /null       | 400           | 63          |
      | text  | 0      | /null           | /null       | /null       | 400           | 63          |
      | 10    | text   | /null           | /null       | /null       | 400           | 63          |
      #filtering and sorting
      | 10    | 0      | /null           | key         | key         | 400           | 64          |
      | 10    | 0      | /null           | /null       | nonexistent | 400           | 63          |
      | 10    | 0      | /null           | nonexistent | /null       | 400           | 63          |
      | 10    | 0      | key==           | /null       | /null       | 400           | 63          |
      | 10    | 0      | nonexistent==a* | /null       | /null       | 400           | 63          |


  Scenario Outline: Filtering list of configurations
    Given The following configuration types exist
      | identifier       | description                               |
      | filter_conf_id_1 | Description of configuration identifier 1 |
      | filter_conf_id_2 | Description of configuration identifier 2 |
      | id_1_filter      | Description of configuration identifier 3 |
      | id_2_filter      | spec                                      |

    When List of configuration types is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> configuration types returned
    And There are following configurations returned in order: <expected_identifiers>
    Examples:
      | limit | cursor | returned | filter                                   | sort       | sort_desc  | expected_identifiers               |
      | 1     | 0      | 1        | identifier=='filter_conf_id_*'           | identifier |            | filter_conf_id_1                   |
      | 5     | 0      | 2        | identifier=='filter_conf_id_*'           |            | identifier | filter_conf_id_2, filter_conf_id_1 |
      | 5     | 1      | 3        | identifier=='filter_conf_id_*'           | identifier |            | filter_conf_id_2                   |
      | 5     | 1      | 3        | identifier=='filter_conf_id_*'           |            | identifier | filter_conf_id_1                   |
      | /null | /null  | 1        | identifier==id_1_filter                  | /null      | /null      | id_1_filter                        |
      | /null | /null  | 1        | identifier=='id_*' and description==spec | identifier | /null      | id_2_filter                        |
      | /null | /null  | 1        | description==spec                        | /null      | /null      | id_2_filter                        |

    Scenario Outline: Send POST request with empty body to all configurations endpoints
      When Empty POST request is sent to "<url>" on module "configurations"
      Then Response code is "422"
      And Custom code is "42201"
      Examples:
      | url                              |
      | configurations/                  |
      | configurations/conf_id_1         |
      | configurations/conf_id_1/records |
