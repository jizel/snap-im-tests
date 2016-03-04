Feature: Properties property sets get

  Background: 
    Given Database is cleaned
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone          |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Bratislava |
    Given The following properties exist with random address and billing address
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      |
      | 0b202111-cdaf-439a-8bef-3140f56c657e | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |

  Scenario: Getting properties property sets
      Given The following property sets exist for customer with code "c1t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
      Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with code "c1t" exists
    When Property set with name "ps1_name" for property with code "p1_code" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "property_set_id"
    And Body contains entity with attribute "property_set_name"

  Scenario Outline: Getting list of properties property sets
  Given The following property sets exist for customer with code "c1t"
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
      
   Given Relation between property with code "p1_code" and property set with name "list_ps1_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps2_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps3_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps4_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps5_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps6_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps7_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps8_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps9_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps10_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps11_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps12_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps13_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps14_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps15_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps16_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps17_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps18_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps19_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps20_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps21_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps22_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps23_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps24_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps25_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps26_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps27_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps28_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps29_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps30_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps31_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps32_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps33_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps34_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps35_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps36_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps37_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps38_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps39_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps40_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps41_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps42_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps43_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps44_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps45_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps46_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps47_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps48_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps49_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps50_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps51_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps52_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps53_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps54_name" for customer with code "c1t" exists
   
   When List of property sets is got for property with id "0b202111-cdaf-439a-8bef-3140f56c657e" and limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> property sets returned
    And Total count is "<total>"

    Examples: 
      | limit | cursor | returned | total |
      | /null |        | 50       | 54    |
      | /null | /null  | 50       | 54    |
      |       |        | 50       | 54    |
      |       | /null  | 50       | 54    |
      | 15    |        | 15       | 54    |
      |       | 1      | 50       | 54    |
      | 20    | 0      | 20       | 54    |
      | 10    | 0      | 10       | 54    |
      | 5     | 10     | 5        | 54    |

     Scenario Outline: Filtering list of properties property sets
  Given The following property sets exist for customer with code "c1t"
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
   Given Relation between property with code "p1_code" and property set with name "list_ps1_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps2_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps3_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps4_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps5_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps6_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps7_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps8_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps9_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps10_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps11_name" for customer with code "c1t" exists
   Given Relation between property with code "p1_code" and property set with name "list_ps12_name" for customer with code "c1t" exists
   
    When List of property sets is got for property with id "0b202111-cdaf-439a-8bef-3140f56c657e" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> property sets returned
    And There are property sets with following names returned in order: <expected_names>
    And Total count is "<total>"

    Examples: 
      | limit | cursor | returned | total | filter                                                 | sort              | sort_desc         | expected_names                                                               |
      | 5     | 0      | 5        | 12    | property_set_name=='list_*'                            | property_set_name |                   | list_ps10_name, list_ps11_name, list_ps12_name, list_ps1_name, list_ps2_name |
      | 5     | 0      | 5        | 12    | property_set_name=='list_*'                            |                   | property_set_name | list_ps9_name, list_ps8_name, list_ps7_name, list_ps6_name, list_ps5_name    |
      | 5     | 2      | 5        | 12    | property_set_name=='list_*'                            | property_set_name |                   | list_ps12_name, list_ps1_name, list_ps2_name, list_ps3_name, list_ps4_name   |
      | 5     | 2      | 5        | 12    | property_set_name=='list_*'                            |                   | property_set_name | list_ps7_name, list_ps6_name, list_ps5_name, list_ps4_name, list_ps3_name    |
      | /null | /null  | 1        | 1     | property_set_name==list_ps4_name                       | /null             | /null             | list_ps4_name                                                                |

  Scenario Outline: Checking error codes for getting list of property property sets
    When List of property sets is got for property with id "0b202111-cdaf-439a-8bef-3140f56c657e" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | limit       | cursor | filter | sort              | sort_desc         | response_code | custom_code |
      | /null       | -1     | /null  | /null             | /null             | 400           | 63          |
      |             | -1     | /null  | /null             | /null             | 400           | 63          |
      | /null       | text   | /null  | /null             | /null             | 400           | 63          |
      |             | text   | /null  | /null             | /null             | 400           | 63          |
      | -1          |        | /null  | /null             | /null             | 400           | 63          |
      | -1          | /null  | /null  | /null             | /null             | 400           | 63          |
      | 201         | /null  | /null  | /null             | /null             | 400           | 63          |
      | 21474836470 | /null  | /null  | /null             | /null             | 400           | 63          |
      | text        |        | /null  | /null             | /null             | 400           | 63          |
      | text        | /null  | /null  | /null             | /null             | 400           | 63          |
      | 10          | -1     | /null  | /null             | /null             | 400           | 63          |
      | text        | 0      | /null  | /null             | /null             | 400           | 63          |
      | 10          | text   | /null  | /null             | /null             | 400           | 63          |
      | 10          | 0      | /null  | property_set_name | property_set_name | 400           | 64          |
      | 10          | 0      | /null  | /null             | nonexistent       | 400           | 63          |
      | 10          | 0      | /null  | nonexistent       | /null             | 400           | 63          |
      | 10          | 0      | code== | /null             | /null             | 400           | 63          |
      
      