@Identity
Feature: Property sets get

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 49ae92d9-2d80-47d9-994b-77f5f598336a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "49ae92d9-2d80-47d9-994b-77f5f598336a" as primary "false"
      | id                                   | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "49ae92d9-2d80-47d9-994b-77f5f598336a" and user "default1"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
      | ps2_name        | ps2_description        | brand           |
      | ps3_name        | ps3_description        | brand           |


  Scenario: Checking error code for getting property set
    When Nonexistent property set id is got
    Then Response code is "404"
    And Custom code is "40402"


  Scenario Outline: Getting property sets
    Given The following property sets exist for customer with id "49ae92d9-2d80-47d9-994b-77f5f598336a" and user "default1"
      | name            | description            | type            |
      | list_ps1_name   | list_ps1_description   | brand           |
      | list_ps2_name   | list_ps2_description   | brand           |
      | list_ps3_name   | list_ps3_description   | brand           |
      | list_ps4_name   | list_ps4_description   | brand           |
      | list_ps5_name   | list_ps5_description   | brand           |
      | list_ps6_name   | list_ps6_description   | brand           |
      | list_ps7_name   | list_ps7_description   | brand           |
      | list_ps8_name   | list_ps8_description   | brand           |
      | list_ps9_name   | list_ps9_description   | brand           |
      | list_ps10_name  | list_ps10_description  | brand           |
      | list_ps11_name  | list_ps11_description  | brand           |
      | list_ps12_name  | list_ps12_description  | brand           |
      | list_ps13_name  | list_ps13_description  | brand           |
      | list_ps14_name  | list_ps14_description  | brand           |
      | list_ps15_name  | list_ps15_description  | brand           |
      | list_ps16_name  | list_ps16_description  | brand           |
      | list_ps17_name  | list_ps17_description  | brand           |
      | list_ps18_name  | list_ps18_description  | brand           |
      | list_ps19_name  | list_ps19_description  | brand           |
      | list_ps20_name  | list_ps20_description  | brand           |
      | list_ps21_name  | list_ps21_description  | brand           |
      | list_ps22_name  | list_ps22_description  | brand           |
      | list_ps23_name  | list_ps23_description  | brand           |
      | list_ps24_name  | list_ps24_description  | brand           |
      | list_ps25_name  | list_ps25_description  | brand           |
      | list_ps26_name  | list_ps26_description  | brand           |
      | list_ps27_name  | list_ps27_description  | brand           |
      | list_ps28_name  | list_ps28_description  | brand           |
      | list_ps29_name  | list_ps29_description  | brand           |
      | list_ps30_name  | list_ps30_description  | brand           |
      | list_ps31_name  | list_ps31_description  | brand           |
      | list_ps32_name  | list_ps32_description  | brand           |
      | list_ps33_name  | list_ps33_description  | brand           |
      | list_ps34_name  | list_ps34_description  | brand           |
      | list_ps35_name  | list_ps35_description  | brand           |
      | list_ps36_name  | list_ps36_description  | brand           |
      | list_ps37_name  | list_ps37_description  | brand           |
      | list_ps38_name  | list_ps38_description  | brand           |
      | list_ps39_name  | list_ps39_description  | brand           |
      | list_ps40_name  | list_ps40_description  | brand           |
      | list_ps41_name  | list_ps41_description  | brand           |
      | list_ps42_name  | list_ps42_description  | brand           |
      | list_ps43_name  | list_ps43_description  | brand           |
      | list_ps44_name  | list_ps44_description  | brand           |
      | list_ps45_name  | list_ps45_description  | brand           |
      | list_ps46_name  | list_ps46_description  | brand           |
      | list_ps47_name  | list_ps47_description  | brand           |
      | list_ps48_name  | list_ps48_description  | brand           |
      | list_ps49_name  | list_ps49_description  | brand           |
      | list_ps50_name  | list_ps50_description  | brand           |
      | list_ps51_name  | list_ps51_description  | brand           |
      | list_ps52_name  | list_ps52_description  | brand           |
      | list_ps53_name  | list_ps53_description  | brand           |
      | list_ps54_name  | list_ps54_description  | brand           |
      | list_ps55_name  | list_ps55_description  | brand           |
      | list_ps56_name  | list_ps56_description  | brand           |
      | list_ps57_name  | list_ps57_description  | brand           |
      | list_ps58_name  | list_ps58_description  | brand           |
      | list_ps59_name  | list_ps59_description  | brand           |
    When List of property sets is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> property sets returned
    And Link header is '<link_header>'
    And Total count is "<total>"
    Examples:
      | limit | cursor | returned | total | link_header                                                                                                       |
      | /null |        | 50       | 62    | </identity/property_sets?limit=50&cursor=50>; rel="next"                                                          |
      | /null | /null  | 50       | 62    | </identity/property_sets?limit=50&cursor=50>; rel="next"                                                          |
      |       |        | 50       | 62    | </identity/property_sets?limit=50&cursor=50>; rel="next"                                                          |
      |       | /null  | 50       | 62    | </identity/property_sets?limit=50&cursor=50>; rel="next"                                                          |
      | 15    |        | 15       | 62    | </identity/property_sets?limit=15&cursor=15>; rel="next"                                                          |
      |       | 1      | 50       | 62    | </identity/property_sets?limit=50&cursor=0>; rel="prev", </identity/property_sets?limit=50&cursor=51>; rel="next" |
      | 20    | 0      | 20       | 62    | </identity/property_sets?limit=20&cursor=20>; rel="next"                                                          |
      | 10    | 0      | 10       | 62    | </identity/property_sets?limit=10&cursor=10>; rel="next"                                                          |
      | 5     | 10     | 5        | 62    | </identity/property_sets?limit=5&cursor=5>; rel="prev", </identity/property_sets?limit=5&cursor=15>; rel="next"   |


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


