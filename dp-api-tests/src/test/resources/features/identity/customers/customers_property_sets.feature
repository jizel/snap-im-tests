@Identity
Feature: Customers property sets

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 55e2cf39-ffb6-4bb8-ad3f-66306c2be124 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
      | 0fdc1123-b242-46a7-8377-f95210df8c66 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    Given The following users exist for customer "55e2cf39-ffb6-4bb8-ad3f-66306c2be124" as primary "false"
      | userId                               | userType | userName            | firstName | lastName     | email                                | timezone      | culture |
      | ae912431-b6aa-4d78-a6d9-f8620ccd9d0b | snapshot | defaultSnapshotuser | Default   | SnapshotUser | defaultSnapshotUser1@snapshot.travel | Europe/Prague | cs-CZ   |

    #Get token for snapshot user and set it to session (?access_token={token})
    Given The password of user "defaultSnapshotuser" is "Password01"
    Given Get token for user "defaultSnapshotuser" with password "Password01"


  Scenario Outline: getting list of property sets for customer "c1t" on customers side
#failing because of not functional filtering
    Given The following property sets exist for customer with id "55e2cf39-ffb6-4bb8-ad3f-66306c2be124" and user "ae912431-b6aa-4d78-a6d9-f8620ccd9d0b"
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
    When List of property sets for customer "55e2cf39-ffb6-4bb8-ad3f-66306c2be124" is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customer property sets returned
    And Total count is "<total>"
    Examples:
      | limit | cursor | returned | total |
      | /null |        | 50       | 59    |
      | /null | /null  | 50       | 59    |
      |       |        | 50       | 59    |
      |       | /null  | 50       | 59    |
      | 15    |        | 15       | 59    |
      |       | 1      | 50       | 59    |
      | 20    | 0      | 20       | 59    |
      | 10    | 0      | 10       | 59    |
      | 5     | 5      | 5        | 59    |

  Scenario Outline: Checking error codes for getting list of property sets
    When List of property sets for customer "c1t" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
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
    Given The following property sets exist for customer with c1t"
      | propertySetName      | propertySetDescription | propertySetType |
      | list_ps1_name        | list_ps1_description   | branch          |
      | list_ps2_name        | list_ps2_description   | branch          |
      | list_ps3_name        | list_ps3_description   | branch          |
      | list_ps4_name        | list_ps4_description   | chain           |
      | list_ps5_name        | list_ps5_description   | chain           |
      | second_list_ps6_name | list_ps6_description   | chain           |
      | second_list_ps7_name | list_ps7_description   | branch          |
      | second_list_ps8_name | list_ps8_description   | branch          |


    When List of property sets for customer "c1t" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customer property sets returned
    And There are property sets with following names returned in order: <expected_codes>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter              | sort  | sort_desc | expected_codes                                                            |
      | 5     | 0      | 5        | 5     | name=='list_*'      | name  |           | list_ps1_name, list_ps2_name, list_ps3_name, list_ps4_name, list_ps5_name |
      | 5     | 0      | 5        | 5     | name=='list_*'      |       | name      | list_ps5_name, list_ps4_name, list_ps3_name, list_ps2_name, list_ps1_name |
      | 5     | 2      | 3        | 5     | name=='list_*'      | name  |           | list_ps3_name, list_ps4_name, list_ps5_name                               |
      | 5     | 2      | 3        | 5     | name=='list_*'      |       | name      | list_ps3_name, list_ps2_name, list_ps1_name                               |
      | /null | /null  | 1        | 1     | name==list_ps4_name | /null | /null     | list_ps4_name                                                             |
