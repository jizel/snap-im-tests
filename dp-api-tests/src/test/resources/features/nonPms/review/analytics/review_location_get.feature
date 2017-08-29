Feature: Review locations
  Testing of api for review locations with mock data in db - testing property id is "99000199-9999-4999-a999-999999999999"

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 99000199-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2055-12-31"


  # /locations/
  # ---------------------------------------------------------------------------------------------------------------------

  Scenario Outline: Getting a list of items
    When List of locations is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is 200
    And Content type is "application/json"
    And There are <count> posts returned

    Examples:
      | limit | cursor | count |
      |       |        | 50    |
      | /null |        | 50    |
      |       | /null  | 50    |
      | /null | /null  | 50    |
      | 51    |        | 51    |
      | 51    | /null  | 51    |
      |       | 1      | 50    |
      | /null | 1      | 50    |
      | 20    | 0      | 20    |
      | 60    | 0      | 60    |
      | 5     | 5      | 5     |

  Scenario Outline: Checking error codes for getting list of locations
    When List of locations is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "400"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter      | sort          | sort_desc     | custom_code |
      #limit and cursor
      |             | -1     | /null       | /null         | /null         | 40002       |
      |             | -1     | /null       | /null         | /null         | 40002       |
      |             | text   | /null       | /null         | /null         | 40002       |
      |             | text   | /null       | /null         | /null         | 40002       |
      | -1          |        | /null       | /null         | /null         | 40002       |
      | -1          |        | /null       | /null         | /null         | 40002       |
      | 201         |        | /null       | /null         | /null         | 40002       |
      | 21474836470 |        | /null       | /null         | /null         | 40002       |
      | text        |        | /null       | /null         | /null         | 40002       |
      | text        | /null  | /null       | /null         | /null         | 40002       |
      | 10          | -1     | /null       | /null         | /null         | 40002       |
      | text        | 0      | /null       | /null         | /null         | 40002       |
      | 10          | text   | /null       | /null         | /null         | 40002       |
      #filtering and sorting
      | 10          | 0      | /null       | location_name | location_name | 40002       |
      | 10          | 0      | /null       | /null         | nonexistent   | 40002       |
      | 10          | 0      | /null       | nonexistent   | /null         | 40002       |
      | 10          | 0      | not_here==  | /null         | /null         | 40002       |
      | 10          | 0      | random==CZ* | /null         | /null         | 40002       |

  # TODO DP-935 - X-Total-Count header is missing
  Scenario Outline: Filtering list of locations
    When List of locations is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are "<returned>" locations returned
    And There are locations with following name returned in order: "<expected_names>"

    Examples:
      | limit | cursor | returned | filter                                  | sort          | sort_desc     | expected_names                                  |
      | 5     | 0      | 5        | location_name=='town_99*'               | location_name |               | town 99, town 990, town 991, town 992, town 993 |
      | 5     | 0      | 5        | location_name=='town_99*'               |               | location_name | town 999, town 998, town 997, town 996, town 995 |
      | 5     | 2      | 5        | location_name=='town_99*'               | location_name |               | town 991, town 992, town 993, town 994, town 995|
      | 5     | 2      | 5        | location_name=='town_99*'               |               | location_name | town 997, town 996, town 995, town 994, town 993|
      | /null | /null  | 1        | location_name=='town 999'               |               |               | town 999                                        |
      | /null | /null  | 1        | location_id==999                        |               |               | town 999                                        |
      | /null | /null  | 1        | location_name==town_99*;location_id==99 |               |               | town 99                                         |
      | /null | /null  | 1        | location_id==99                         |               |               | town 99                                         |


  #/location/<property>
  #---------------------------------------------------------------------------------------------------------------------

  @Smoke
  Scenario Outline: Getting location id for correct property id
#    Property ids are set by load_fake_data script in table dp/tripadvisor_property
    When Get trip advisor "<url>" for "<property>"
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "location_id" value "<location_id>"
    And Body contains entity with attribute "location_name" value "<location_name>"
    Examples:
      | url       | property                             | location_id | location_name |
      | /location | 99000199-9999-4999-a999-999999999999 | 19          | town 19       |
      | /location | 99000099-9999-4999-a999-999999999999 | 20          | town 20       |
      | /location | 99000499-9999-4999-a999-999999999999 | 21          | town 21       |


  Scenario Outline: Getting error code for not existing property id from /location
    When Get trip advisor "<url>" for "<property>"
    Then Response code is "<response_code>"
    And Content type is "application/json"
    And Custom code is "<custom_code>"

    #todo DP-1117 - response message for property "null" has wrong message - check here after fix
    Examples:
      | url       | property                             | response_code | custom_code |
      | /location | 11111111-1111-4111-a111-111111111111 | 404           | 40402       |
      | /location | null                                 | 400           | 40002       |
      | /location | /null                                | 400           | 40002       |


  #/location/<location_id>/properties
  #---------------------------------------------------------------------------------------------------------------------
  Scenario Outline: Getting a list of properties for location id
    When List of location properties is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null" for location id "20"
    Then Response code is 200
    And Content type is "application/json"
    And There are <count> posts returned

    Examples:
      | limit | cursor | count |
      |       |        | 50    |
      | /null |        | 50    |
      |       | /null  | 50    |
      | /null | /null  | 50    |
      | 51    |        | 51    |
      | 51    | /null  | 51    |
      |       | 1      | 50    |
      | /null | 1      | 50    |
      | 20    | 0      | 20    |
      | 60    | 0      | 60    |
      | 5     | 5      | 5     |

  Scenario Outline: Checking error codes for getting list of properties for location id
    When List of location properties is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" for location id "20"
    Then Response code is "400"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter      | sort        | sort_desc   | custom_code |
      #limit and cursor
      | /null       | -1     | /null       | /null       | /null       | 40002       |
      |             | -1     | /null       | /null       | /null       | 40002       |
      | /null       | text   | /null       | /null       | /null       | 40002       |
      |             | text   | /null       | /null       | /null       | 40002       |
      | -1          |        | /null       | /null       | /null       | 40002       |
      | -1          | /null  | /null       | /null       | /null       | 40002       |
      | 201         | /null  | /null       | /null       | /null       | 40002       |
      | 21474836470 | /null  | /null       | /null       | /null       | 40002       |
      | text        |        | /null       | /null       | /null       | 40002       |
      | text        | /null  | /null       | /null       | /null       | 40002       |
      | 10          | -1     | /null       | /null       | /null       | 40002       |
      | text        | 0      | /null       | /null       | /null       | 40002       |
      | 10          | text   | /null       | /null       | /null       | 40002       |
      #filtering and sorting
      | 10          | 0      | /null       | property_id | property_id | 40002       |
      | 10          | 0      | /null       | /null       | nonexistent | 40002       |
      | 10          | 0      | /null       | nonexistent | /null       | 40002       |
      | 10          | 0      | not_here==  | /null       | /null       | 40002       |
      | 10          | 0      | random==CZ* | /null       | /null       | 40002       |

  Scenario Outline: Filtering list of location properties
    When List of location properties is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" for location id "20"
    Then Response code is "200"
    And Content type is "application/json"
    And There are "<returned>" location properties returned
    And There are properties for location id are returned in order: "<expected_ids>"
    #And Total count is "<total>" #header "X-Total-Count" not implemented

    Examples:
      | limit | cursor | returned | total | filter                                            | sort        | sort_desc   | expected_ids                                                               |
      | 5     | 0      | 2        | 6     | property_id==99000*                               | property_id |             | 99000099-9999-4999-a999-999999999999, 99000799-9999-4999-a999-999999999999 |
      | 5     | 0      | 2        | 6     | property_id==99000*                               |             | property_id | 99000799-9999-4999-a999-999999999999, 99000099-9999-4999-a999-999999999999 |
      | 5     | 1      | 1        | 6     | property_id==99002*                               | property_id |             | 99002599-9999-4999-a999-999999999999                                       |
      | 5     | 1      | 1        | 6     | property_id==99002*                               |             | property_id | 99002099-9999-4999-a999-999999999999                                       |
      | /null | /null  | 1        | 1     | property_id==99000099-9999-4999-a999-999999999999 |             |             | 99000099-9999-4999-a999-999999999999                                       |
