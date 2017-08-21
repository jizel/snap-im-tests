@Identity
Feature: Property sets get

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone          |
      | 49ae92d9-2d80-47d9-994b-77f5f598336a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "49ae92d9-2d80-47d9-994b-77f5f598336a" as primary "false"
      | id                                   | type     | username | firstName | lastName | email                | timezone      | languageCode |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "49ae92d9-2d80-47d9-994b-77f5f598336a" and user "default1"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
      | ps2_name        | ps2_description        | brand           |
      | ps3_name        | ps3_description        | brand           |


  Scenario Outline: Checking error codes for getting list of property sets
    When List of property sets is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"
    Examples:
      | limit | cursor | filter         | sort            | sort_desc       | response_code | custom_code |
      #limit and cursor
      | /null | -1     | /null          | /null           | /null           | 400           | 40002       |
      |       | -1     | /null          | /null           | /null           | 400           | 40002       |
      | /null | text   | /null          | /null           | /null           | 400           | 40002       |
      |       | text   | /null          | /null           | /null           | 400           | 40002       |
      | -1    |        | /null          | /null           | /null           | 400           | 40002       |
      | -1    | /null  | /null          | /null           | /null           | 400           | 40002       |
      | text  |        | /null          | /null           | /null           | 400           | 40002       |
      | text  | /null  | /null          | /null           | /null           | 400           | 40002       |
      | 10    | -1     | /null          | /null           | /null           | 400           | 40002       |
      | text  | 0      | /null          | /null           | /null           | 400           | 40002       |
      | 10    | text   | /null          | /null           | /null           | 400           | 40002       |
      #filtering and sorting
      | 10    | 0      | /null          | property_set_id | property_set_id | 400           | 40002       |
      | 10    | 0      | /null          | wrong           | /null           | 400           | 40002       |
      | 10    | 0      | /null          | /null           | wrong           | 400           | 40002       |
      | 10    | 0      | customer_id==  | /null           | /null           | 400           | 40002       |
      | 10    | 0      | parent==blabla | /null           | /null           | 400           | 40002       |

  Scenario Outline: Filtering list of property sets
    Given The following property sets exist for customer with id "49ae92d9-2d80-47d9-994b-77f5f598336a" and user "default1"
      | name                 | description            | type            |
      | list_ps1_name        | list_ps1_description   | brand           |
      | list_ps2_name        | list_ps2_description   | brand           |
      | list_ps3_name        | list_ps3_description   | brand           |
      | list_ps4_name        | list_ps4_description   | geolocation     |
      | list_ps5_name        | list_ps5_description   | geolocation     |
      | second_list_ps6_name | list_ps6_description   | geolocation     |
      | second_list_ps7_name | list_ps7_description   | brand           |
      | second_list_ps8_name | list_ps8_description   | brand           |


    When List of property sets is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> property sets returned
    And There are property sets with following names returned in order: <expected_codes>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                                          | sort  | sort_desc | expected_codes                                                            |
      | 5     | 0      | 5        | 5     | name=='list_*'                                  | name  |           | list_ps1_name, list_ps2_name, list_ps3_name, list_ps4_name, list_ps5_name |
      | 5     | 0      | 5        | 5     | name=='list_*'                                  |       | name      | list_ps5_name, list_ps4_name, list_ps3_name, list_ps2_name, list_ps1_name |
      | 5     | 2      | 3        | 5     | name=='list_*'                                  | name  |           | list_ps3_name, list_ps4_name, list_ps5_name                               |
      | 5     | 2      | 3        | 5     | name=='list_*'                                  |       | name      | list_ps3_name, list_ps2_name, list_ps1_name                               |
      | /null | /null  | 1        | 1     | name==list_ps4_name                             | /null | /null     | list_ps4_name                                                             |
      | /null | /null  | 2        | 2     | name==list_* and property_set_type==geolocation | name  | /null     | list_ps4_name, list_ps5_name                                              |
      | /null | /null  | 1        | 1     | description==list_ps8_des*                      | /null | /null     | second_list_ps8_name                                                      |


