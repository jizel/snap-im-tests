Feature: property_sets_get

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |

    Given All users are removed for property_sets for customer with code "c1t" with names: ps1_name
    Given All property sets are deleted for customers with codes: c1t, c2t

    Given The following property sets exist for customer with code "c1t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
      | ps2_name        | ps2_description        | branch          |
      | ps3_name        | ps3_description        | branch          |


  Scenario Outline: Getting property sets
    Given The following property sets exist for customer with code "c2t"
      | propertySetName | propertySetDescription | propertySetType |
      | list_ps1_name   | list_ps1_description   | branch          |
      | list_ps2_name   | list_ps2_description   | branch          |
      | list_ps3_name   | list_ps3_description   | branch          |
      | list_ps4_name   | list_ps4_description   | branch          |
      | list_ps5_name   | list_ps5_description   | branch          |
      | list_ps6_name   | list_ps6_description   | branch          |
      | list_ps7_name   | list_ps7_description   | branch          |
      | list_ps8_name   | list_ps8_description   | branch          |
      | list_ps9_name   | list_ps9_description   | branch          |
      | list_ps10_name  | list_ps10_description  | branch          |
      | list_ps11_name  | list_ps11_description  | branch          |
      | list_ps12_name  | list_ps12_description  | branch          |
      | list_ps13_name  | list_ps13_description  | branch          |
      | list_ps14_name  | list_ps14_description  | branch          |
      | list_ps15_name  | list_ps15_description  | branch          |
      | list_ps16_name  | list_ps16_description  | branch          |
      | list_ps17_name  | list_ps17_description  | branch          |
      | list_ps18_name  | list_ps18_description  | branch          |
      | list_ps19_name  | list_ps19_description  | branch          |
      | list_ps20_name  | list_ps20_description  | branch          |
      | list_ps21_name  | list_ps21_description  | branch          |
      | list_ps22_name  | list_ps22_description  | branch          |
      | list_ps23_name  | list_ps23_description  | branch          |
      | list_ps24_name  | list_ps24_description  | branch          |
      | list_ps25_name  | list_ps25_description  | branch          |
      | list_ps26_name  | list_ps26_description  | branch          |
      | list_ps27_name  | list_ps27_description  | branch          |
      | list_ps28_name  | list_ps28_description  | branch          |
      | list_ps29_name  | list_ps29_description  | branch          |
      | list_ps30_name  | list_ps30_description  | branch          |
      | list_ps31_name  | list_ps31_description  | branch          |
      | list_ps32_name  | list_ps32_description  | branch          |
      | list_ps33_name  | list_ps33_description  | branch          |
      | list_ps34_name  | list_ps34_description  | branch          |
      | list_ps35_name  | list_ps35_description  | branch          |
      | list_ps36_name  | list_ps36_description  | branch          |
      | list_ps37_name  | list_ps37_description  | branch          |
      | list_ps38_name  | list_ps38_description  | branch          |
      | list_ps39_name  | list_ps39_description  | branch          |
      | list_ps40_name  | list_ps40_description  | branch          |
      | list_ps41_name  | list_ps41_description  | branch          |
      | list_ps42_name  | list_ps42_description  | branch          |
      | list_ps43_name  | list_ps43_description  | branch          |
      | list_ps44_name  | list_ps44_description  | branch          |
      | list_ps45_name  | list_ps45_description  | branch          |
      | list_ps46_name  | list_ps46_description  | branch          |
      | list_ps47_name  | list_ps47_description  | branch          |
      | list_ps48_name  | list_ps48_description  | branch          |
      | list_ps49_name  | list_ps49_description  | branch          |
      | list_ps50_name  | list_ps50_description  | branch          |
      | list_ps51_name  | list_ps51_description  | branch          |
      | list_ps52_name  | list_ps52_description  | branch          |
      | list_ps53_name  | list_ps53_description  | branch          |
      | list_ps54_name  | list_ps54_description  | branch          |
      | list_ps55_name  | list_ps55_description  | branch          |
      | list_ps56_name  | list_ps56_description  | branch          |
      | list_ps57_name  | list_ps57_description  | branch          |
      | list_ps58_name  | list_ps58_description  | branch          |
      | list_ps59_name  | list_ps59_description  | branch          |


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
      |       | 1      | 50       | 62    | </identity/property_sets?limit=50&cursor=51>; rel="next", </identity/property_sets?limit=50&cursor=0>; rel="prev" |
      | 20    | 0      | 20       | 62    | </identity/property_sets?limit=20&cursor=20>; rel="next"                                                          |
      | 10    | 0      | 10       | 62    | </identity/property_sets?limit=10&cursor=10>; rel="next"                                                          |
      | 5     | 10     | 5        | 62    | </identity/property_sets?limit=5&cursor=15>; rel="next", </identity/property_sets?limit=5&cursor=5>; rel="prev"   |


  Scenario Outline: Checking error codes for getting list of property sets
    When List of property sets is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit | cursor | filter         | sort            | sort_desc       | response_code | custom_code |
      #limit and cursor
      | /null | -1     | /null          | /null           | /null           | 400           | 63          |
      |       | -1     | /null          | /null           | /null           | 400           | 63          |
      | /null | text   | /null          | /null           | /null           | 400           | 63          |
      |       | text   | /null          | /null           | /null           | 400           | 63          |
      | -1    |        | /null          | /null           | /null           | 400           | 63          |
      | -1    | /null  | /null          | /null           | /null           | 400           | 63          |
      | text  |        | /null          | /null           | /null           | 400           | 63          |
      | text  | /null  | /null          | /null           | /null           | 400           | 63          |
      | 10    | -1     | /null          | /null           | /null           | 400           | 63          |
      | text  | 0      | /null          | /null           | /null           | 400           | 63          |
      | 10    | text   | /null          | /null           | /null           | 400           | 63          |
      #filtering and sorting
      | 10    | 0      | /null          | property_set_id | property_set_id | 400           | 64          |
      | 10    | 0      | /null          | wrong           | /null           | 400           | 63          |
      | 10    | 0      | /null          | /null           | wrong           | 400           | 63          |
      | 10    | 0      | customer_id==  | /null           | /null           | 400           | 63          |
      | 10    | 0      | parent==blabla | /null           | /null           | 400           | 63          |

  Scenario Outline: Filtering list of property sets
    Given The following property sets exist for customer with code "c1t"
      | propertySetName      | propertySetDescription | propertySetType |
      | list_ps1_name        | list_ps1_description   | branch          |
      | list_ps2_name        | list_ps2_description   | branch          |
      | list_ps3_name        | list_ps3_description   | branch          |
      | list_ps4_name        | list_ps4_description   | chain           |
      | list_ps5_name        | list_ps5_description   | chain           |
      | second_list_ps6_name | list_ps6_description   | chain           |
      | second_list_ps7_name | list_ps7_description   | branch          |
      | second_list_ps8_name | list_ps8_description   | branch          |


    When List of property sets is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> property sets returned
    And There are property sets with following names returned in order: <expected_codes>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                                                 | sort              | sort_desc         | expected_codes                                                            |
      | 5     | 0      | 5        | 5     | property_set_name=='list_*'                            | property_set_name |                   | list_ps1_name, list_ps2_name, list_ps3_name, list_ps4_name, list_ps5_name |
      | 5     | 0      | 5        | 5     | property_set_name=='list_*'                            |                   | property_set_name | list_ps5_name, list_ps4_name, list_ps3_name, list_ps2_name, list_ps1_name |
      | 5     | 2      | 3        | 5     | property_set_name=='list_*'                            | property_set_name |                   | list_ps3_name, list_ps4_name, list_ps5_name                               |
      | 5     | 2      | 3        | 5     | property_set_name=='list_*'                            |                   | property_set_name | list_ps3_name, list_ps2_name, list_ps1_name                               |
      | /null | /null  | 1        | 1     | property_set_name==list_ps4_name                       | /null             | /null             | list_ps4_name                                                             |
      | /null | /null  | 2        | 2     | property_set_name==list_* and property_set_type==chain | property_set_name | /null             | list_ps4_name, list_ps5_name                                              |
      | /null | /null  | 1        | 1     | property_set_description==list_ps8_des*                | /null             | /null             | second_list_ps8_name                                                      |


