Feature: General configuration

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
      | json_data                                                           | method | error_code | custom_code |
      | { "identifier":"", "description":"identifier is empty"}             | POST   | 400        | 53          |
      | { "description":"identifier is missing"}                            | POST   | 400        | 53          |
      | { "identifier": "conf_id_1", "description":"identifier is missing"} | POST   | 400        | 62          |

  Scenario: Deleting Configuration Type
    When Configuration type with identifier "conf_id_1" is deleted
    Then Response code is "204"
    And Body is empty
    And Configuration type with identifier "conf_id_1" doesn't exist

  Scenario: Deleting nonexistent configuration type
    When Nonexistent configuration type id is deleted
    Then Response code is "404"
    And Custom code is "152"

  Scenario: Delete configuration type with missing id parameter
    When Configuration type with identifier "" is deleted
    Then Response code is "400"
    And Custom code is "52"

  #error states
  #id is missing
  #x-application is missing
  Scenario: Updating configuration type description
    When Configuration type description is updated for identifier "conf_id_1" with description "New description"
    Then Response code is "204"
    And Body is empty
    And Configuration type with identifier "conf_id_1" has description "New description"

  Scenario: Updating description of nonexisting configuration type
    Given Configuration type with identifier "nonexisting_id" doesn't exist
    When Configuration type description is updated for identifier "nonexisting_id" with description "New description"
    Then Response code is "404"
    And Custom code is "152"

  Scenario: Updating description with missing body parameter
    When Configuration type description is updated for identifier "conf_id_2" with missing description
    Then Response code is "400"
    And Custom Code is "53"

  Scenario: Updating description with missing id parameter
    When Configuration type description is updated for identifier "" with description "New description"
    Then Response code is "400"
    And Custom code is "52"

  #error states
  #empty body, wrong id, wrong application id
  Scenario: Getting configuration type
    When Configuration type with with identifier "with_items_conf_id_1"  is got
    Then Response code is "200"
    And Content type is "application/json"
    And There are "2" configurations returned

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
    When List of configuration types is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are "<limit>" configuration types returned

    Examples: 
      | limit | cursor |
      |       |        |
      | 15    |        |
      |       | 1      |
      | 20    | 0      |
      | 10    | 0      |
      | 5     | 5      |

  #given hodne hodnot, aby se dalo testovat
  #test limit, cursor, filter, sort with different values
  Scenario Outline: Checking error codes for getting list of configuration types
    When List of configuration types is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | limit | cursor | response_code | custom_code |
      |       | -1     | 400           | 63          |
      |       | text   | 400           | 63          |
      | -1    |        | 400           | 63          |
      | text  |        | 400           | 63          |
      | 10    | -1     | 400           | 63          |
      | text  | 0      | 400           | 63          |
      | 10    | text   | 400           | 63          |

  Scenario Outline: add configuration key:value
    When Configuration is created for configuration type "conf_id_1"
      | key   | value   |
      | <key> | <value> |
    Then Response code is "201"
    And Body contains configuration
      | key   | value   |
      | <key> | <value> |
    And "Location" header is set and contains configuration with key "<key>"

    Examples: 
      | key        | value                                        |
      | test_key_1 | "text value"                                 |
      | test_key_2 | 11                                           |
      | test_key_3 | {"property_1": "value_1", "property_2": 45 } |

  Scenario Outline: Checking error codes for adding configuration key:value
    Given The following configurations exist for configuration type identifier "conf_id_1"
      | key              | value        |
      | given_test_key_1 | "text value" |
    When Configuration is created for configuration type "conf_id_1"
      | key   | value   |
      | <key> | <value> |
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | key         | value         | response_code | custom_code |
      |             | 11            | 400           | 61          |
      | given_key_1 | "text value2" | 400           | 62          |
      | test_key_1  |               | 400           | 61          |

  #errors
  #missing application
  #no key, empty key, wrong value
  #create already created
  Scenario: delete configuration key:value
    Given The following configurations exist for configuration type identifier "conf_id_1"
      | key              | value        |
      | given_test_key_1 | "text value" |
    When Configuration with identifier "given_test_key_1" is deleted from identifier "conf_id_1"
    Then Response code is "204"
    And Body is empty
    And Configuration with key "given_test_key_1" doesn't exist for configuration type "conf_id_1"

  #errors
  #wrong key
  Scenario Outline: update configuration value for key
    Given The following configurations exist for configuration type identifier "conf_id_1"
      | key   | value       |
      | <key> | <old_value> |
    When Configuration with from identifier "conf_id_1" is updated
      | key   | value       |
      | <key> | <new_value> |
    Then Response code is "204"
    And Body is empty
    And Configuration from identifier "conf_id_1" has following
      | key   | value       |
      | <key> | <new_value> |

    Examples: 
      | key              | old_value    | new_value   |
      | given_test_key_1 | "text value" | "new value" |

  #| test_key_2 | 11                                           |             |
  #| test_key_3 | {"property_1": "value_1", "property_2": 45 } |             |
  #wrong key
  #empty body - missing value
  Scenario: get configuration value for key
    Given The following configurations exist for configuration type identifier "conf_id_1"
      | key              | value        |
      | given_test_key_1 | "text value" |
    When Configuration with key "given_test_key_1"  is got from configuration type "conf_id_1"
    Then Response code is "200"
    And Content type is "application/json"
    And Returned configuration value is "text value"

  Scenario Outline: Checking errors for getting configuration value for key
    When Configuration with key "<key>" is got from configuration type "<identifier>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | identifier  | key          | response_code | custom_code |
      | "conf_id_1" | "wrong_key"  | 404           | 151         |
      | "wrong_id"  | "test_key_1" | 404           | 152         |

  # errors
  #wrong key
  Scenario Outline: get all configuration key:values from configuration type
    Given The following configurations exist for configuration type identifier "conf_id_1"
      | key                    | value           |
      | list_given_test_key_1  | "text value 1"  |
      | list_given_test_key_2  | "text value 2"  |
      | list_given_test_key_3  | "text value 3"  |
      | list_given_test_key_4  | "text value 4"  |
      | list_given_test_key_5  | "text value 5"  |
      | list_given_test_key_6  | "text value 6"  |
      | list_given_test_key_7  | "text value 7"  |
      | list_given_test_key_8  | "text value 8"  |
      | list_given_test_key_9  | "text value 9"  |
      | list_given_test_key_10 | "text value 10" |
      | list_given_test_key_11 | "text value 11" |
      | list_given_test_key_12 | "text value 12" |
      | list_given_test_key_13 | "text value 13" |
      | list_given_test_key_14 | "text value 14" |
      | list_given_test_key_15 | "text value 15" |
      | list_given_test_key_16 | "text value 16" |
      | list_given_test_key_17 | "text value 17" |
      | list_given_test_key_18 | "text value 18" |
      | list_given_test_key_19 | "text value 19" |
      | list_given_test_key_20 | "text value 20" |
      | list_given_test_key_21 | "text value 21" |
      | list_given_test_key_22 | "text value 22" |
    When List of configurations is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty for configuration type "conf_id_1"
    Then Response code is "200"
    And Content type is "application/json"
    And There are "<limit>" configurations returned

    Examples: 
      | limit | cursor |
      |       |        |
      | 15    |        |
      |       | 1      |
      | 20    | 0      |
      | 10    | 0      |
      | 5     | 5      |

  Scenario Outline: Checking error codes for getting list of configurations for configuration type "conf_id_1"
    When List of configurations is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty for configuration type "conf_id_1"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | limit | cursor | response_code | custom_code |
      |       | -1     | 400           | 63          |
      |       | text   | 400           | 63          |
      | -1    |        | 400           | 63          |
      | text  |        | 400           | 63          |
      | 10    | -1     | 400           | 63          |
      | text  | 0      | 400           | 63          |
      | 10    | text   | 400           | 63          |
