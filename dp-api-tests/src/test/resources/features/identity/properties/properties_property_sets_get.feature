@Identity
Feature: Properties property sets get

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 0b202111-cdaf-439a-8bef-3140f56c657e | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

  Scenario: Getting properties property sets
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "default1"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists
    When Property set with name "ps1_name" for property with code "p1_code" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "property_set_id"

  Scenario Outline: Getting list of properties property sets
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "default1"
      | propertySetName | propertySetDescription | propertySetType |
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

    Given Relation between property with code "p1_code" and property set with name "list_ps1_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps2_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps3_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps4_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps5_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps6_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps7_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps8_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps9_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps10_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps11_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps12_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps13_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps14_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps15_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps16_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps17_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps18_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps19_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps20_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps21_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps22_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps23_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps24_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps25_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps26_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps27_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps28_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps29_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps30_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps31_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps32_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps33_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps34_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps35_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps36_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps37_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps38_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps39_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps40_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps41_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps42_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps43_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps44_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps45_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps46_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps47_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps48_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps49_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps50_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps51_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps52_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps53_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps54_name" exists

    When List of property sets is got for property with id "0b202111-cdaf-439a-8bef-3140f56c657e" and limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Total count is "<total>"

    Examples:
      | limit | cursor | total |
      | /null |        | 54    |
      | /null | /null  | 54    |
      |       |        | 54    |
      |       | /null  | 54    |
      | 15    |        | 54    |
      |       | 1      | 54    |
      | 20    | 0      | 54    |
      | 10    | 0      | 54    |
      | 5     | 10     | 54    |

  Scenario Outline: Filtering list of properties property sets
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "default1"
      | propertySetName | propertySetDescription | propertySetType |
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
    Given Relation between property with code "p1_code" and property set with name "list_ps1_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps2_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps3_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps4_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps5_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps6_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps7_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps8_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps9_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps10_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps11_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps12_name" exists

    When List of property sets is got for property with id "0b202111-cdaf-439a-8bef-3140f56c657e" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> property set properties returned
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total |  filter           |   sort         |  sort_desc  |
      | 5     | 0      | 5        | 12    | is_active==false  | property_id  |               |
      | 5     | 0      | 5        | 12    | is_active==false  |              | property_id   |
      | 5     | 2      | 5        | 12    | is_active==false  | property_id  |               |
      | 5     | 2      | 0        | 0     | is_active==true   |              | property_id    |

  Scenario Outline: Checking error codes for getting list of property property sets
    When List of property sets is got for property with id "0b202111-cdaf-439a-8bef-3140f56c657e" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter | sort        | sort_desc   | response_code | custom_code |
      | /null       | -1     | /null  | /null       | /null       | 400           | 40002       |
      |             | -1     | /null  | /null       | /null       | 400           | 40002       |
      | /null       | text   | /null  | /null       | /null       | 400           | 40002       |
      |             | text   | /null  | /null       | /null       | 400           | 40002       |
      | -1          |        | /null  | /null       | /null       | 400           | 40002       |
      | -1          | /null  | /null  | /null       | /null       | 400           | 40002       |
      | 201         | /null  | /null  | /null       | /null       | 400           | 40002       |
      | 21474836470 | /null  | /null  | /null       | /null       | 400           | 40002       |
      | text        |        | /null  | /null       | /null       | 400           | 40002       |
      | text        | /null  | /null  | /null       | /null       | 400           | 40002       |
      | 10          | -1     | /null  | /null       | /null       | 400           | 40002       |
      | text        | 0      | /null  | /null       | /null       | 400           | 40002       |
      | 10          | text   | /null  | /null       | /null       | 400           | 40002       |
      | 10          | 0      | /null  | name        | name        | 400           | 40002       |
      | 10          | 0      | /null  | /null       | nonexistent | 400           | 40002       |
      | 10          | 0      | /null  | nonexistent | /null       | 400           | 40002       |
      | 10          | 0      | code== | /null       | /null       | 400           | 40002       |
      
      