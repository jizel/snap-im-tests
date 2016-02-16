Feature: review_locaitons
  Testing of api for review locations with mock data in db - testing property id is "99000199-9999-4999-a999-999999999999"

  Scenario Outline: Getting a list of items
    When List of locations is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is <response_code>
    And Content type is "application/json"
    And There are <count> posts returned
    #when limit and cursor are null -> error, bug reported
    Examples:
    | limit | cursor | count | response_code |
    |       |        | 50    | 200           |
    | /null |        | 50    | 200           |
    |       | /null  | 50    | 200           |
    | /null | /null  | 50    | 200           |
    | 51    |        | 51    | 200           |
    | 51    | /null  | 51    | 200           |
    |       | 1      | 50    | 200           |
    | /null | 1      | 50    | 200           |
    | 20    | 0      | 20    | 200           |
    | 60    | 0      | 60    | 200           |
    | 5     | 5      | 5     | 200           |

  Scenario Outline: Checking error codes for getting list of locations
    When List of locations is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
    | limit       | cursor | filter      | sort          | sort_desc     | response_code | custom_code |
    #limit and cursor
    | /null       | -1     | /null       | /null         | /null         | 400           | 63          |
    |             | -1     | /null       | /null         | /null         | 400           | 63          |
    | /null       | text   | /null       | /null         | /null         | 400           | 63          |
    |             | text   | /null       | /null         | /null         | 400           | 63          |
    | -1          |        | /null       | /null         | /null         | 400           | 63          |
    | -1          | /null  | /null       | /null         | /null         | 400           | 63          |
    | 201         | /null  | /null       | /null         | /null         | 400           | 63          |
    | 21474836470 | /null  | /null       | /null         | /null         | 400           | 63          |
    | text        |        | /null       | /null         | /null         | 400           | 63          |
    | text        | /null  | /null       | /null         | /null         | 400           | 63          |
    | 10          | -1     | /null       | /null         | /null         | 400           | 63          |
    | text        | 0      | /null       | /null         | /null         | 400           | 63          |
    | 10          | text   | /null       | /null         | /null         | 400           | 63          |
    #filtering and sorting
    #not implemented ?
    | 10          | 0      | /null       | location_name | location_name | 400           | 64          |
    | 10          | 0      | /null       | /null         | nonexistent   | 400           | 63          |
    | 10          | 0      | /null       | nonexistent   | /null         | 400           | 63          |
    | 10          | 0      | not_here==  | /null         | /null         | 400           | 63          |
    | 10          | 0      | random==CZ* | /null         | /null         | 400           | 63          |

  #TODO BUG reported - check and retest
  Scenario Outline: Filtering list of locations
    When List of locations is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are "<returned>" locations returned
    And There are locations with following name returned in order: "<expected_names>"
    #And Total count is "<total>" #header "X-Total-Count" not implemented

    #delete /null values from sort/sort_desc empty parameter should be ignored
    #location_id cannot be used as filtering parameter (dominik hasn't answeared me yet)
    Examples:
    | limit | cursor | returned | total | filter                                        | sort          | sort_desc     | expected_names                                                                      |
    | 5     | 0      | 5        | 6     | location_name=='filter_town*'                 | location_name | /null         | filter_town1000, filter_town1001, filter_town1002, filter_town1003, filter_town1004 |
    | 5     | 0      | 5        | 6     | location_name=='filter_town*'                 | /null         | location_name | filter_town1005, filter_town1004, filter_town1003, filter_town1002, filter_town1001 |
    | 5     | 2      | 4        | 6     | location_name=='filter_town*'                 | location_name | /null         | filter_town1002, filter_town1003, filter_town1004, filter_town1005                  |
    | 5     | 2      | 4        | 6     | location_name=='filter_town*'                 | /null         | location_name | filter_town1003, filter_town1002, filter_town1001, filter_town1000                  |
    | /null | /null  | 1        | 1     | location_name==filter_town1005                | /null         | /null         | filter_town1005                                                                    |
    | /null | /null  | 1        | 1     | location_name==filter_town*,location_id==1005 | /null         | /null         | filter_town1005                                                                    |
    | /null | /null  | 1        | 1     | location_id==1001                             | /null         | /null         | filter_town1001                                                                    |


  #/location/<property>
  #---------------------------------------------------------------------------------------------------------------------
  Scenario Outline: Getting location id for correct property id
    When Get trip advisor "<url>" for "<property>"
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "location_id" value "<value>"

    Examples:
    | url       | property                             | value  |
    | /location | 99000199-9999-4999-a999-999999999999 | 590001 |


  Scenario Outline: Getting error code for not existing property id from /location
    When Get trip advisor "<url>" for "<property>"
    Then Response code is "404"
    And Content type is "application/json"
    And Custom code is "152"

    Examples:
    | url       | property                             |
    | /location | 11111111-1111-4111-a111-111111111111 |


  #/location/<location_id>/properties
  #---------------------------------------------------------------------------------------------------------------------
  Scenario Outline: Getting a list of properties for location id
    When List of location properties is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null" for location id "20"
    Then Response code is <response_code>
    And Content type is "application/json"
    And There are <count> posts returned
   #BUG when limit and cursor are null -> error, bug reported
    Examples:
      | limit | cursor | count | response_code |
      |       |        | 50    | 200           |
      | /null |        | 50    | 200           |
      |       | /null  | 50    | 200           |
      | /null | /null  | 50    | 200           |
      | 51    |        | 51    | 200           |
      | 51    | /null  | 51    | 200           |
      |       | 1      | 50    | 200           |
      | /null | 1      | 50    | 200           |
      | 20    | 0      | 20    | 200           |
      | 60    | 0      | 60    | 200           |
      | 5     | 5      | 5     | 200           |

  Scenario Outline: Checking error codes for getting list of properties for location id
    When List of location properties is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" for location id "20"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter      | sort        | sort_desc   | response_code | custom_code |
      #limit and cursor
      | /null       | -1     | /null       | /null       | /null       | 400           | 63          |
      |             | -1     | /null       | /null       | /null       | 400           | 63          |
      | /null       | text   | /null       | /null       | /null       | 400           | 63          |
      |             | text   | /null       | /null       | /null       | 400           | 63          |
      | -1          |        | /null       | /null       | /null       | 400           | 63          |
      | -1          | /null  | /null       | /null       | /null       | 400           | 63          |
      | 201         | /null  | /null       | /null       | /null       | 400           | 63          |
      | 21474836470 | /null  | /null       | /null       | /null       | 400           | 63          |
      | text        |        | /null       | /null       | /null       | 400           | 63          |
      | text        | /null  | /null       | /null       | /null       | 400           | 63          |
      | 10          | -1     | /null       | /null       | /null       | 400           | 63          |
      | text        | 0      | /null       | /null       | /null       | 400           | 63          |
      | 10          | text   | /null       | /null       | /null       | 400           | 63          |
      #filtering and sorting
      #BUG
      | 10          | 0      | /null       | property_id | property_id | 400           | 64          |
      | 10          | 0      | /null       | /null       | nonexistent | 400           | 63          |
      | 10          | 0      | /null       | nonexistent | /null       | 400           | 63          |
      | 10          | 0      | not_here==  | /null       | /null       | 400           | 63          |
      | 10          | 0      | random==CZ* | /null       | /null       | 400           | 63          |

  #TODO BUG reported - check and retest, correct return values, BUG for X-Total-Count heder is missing
  Scenario Outline: Filtering list of location properties
    When List of location properties is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" for location id "20"
    Then Response code is "200"
    And Content type is "application/json"
    And There are "<returned>" location properties returned
    And There are properties for location id are returned in order: "<expected_ids>"
    #And Total count is "<total>" #header "X-Total-Count" not implemented

    #delete /null values from sort/sort_desc empty parameter should be ignored (first 4 rows)
    Examples:
      | limit | cursor | returned | total | filter                                            | sort        | sort_desc   | expected_ids                                                               |
      | 5     | 0      | 2        | 6     | property_id==99000*                               | property_id | /null       | 99000099-9999-4999-a999-999999999999, 99000799-9999-4999-a999-999999999999 |
      | 5     | 0      | 2        | 6     | property_id==99000*                               | /null       | property_id | 99000799-9999-4999-a999-999999999999, 99000099-9999-4999-a999-999999999999 |
      | 5     | 1      | 1        | 6     | property_id==99002*                               | property_id | /null       | 99002599-9999-4999-a999-999999999999                                       |
      | 5     | 1      | 1        | 6     | property_id==99002*                               | /null       | property_id | 99002099-9999-4999-a999-999999999999                                       |
      | /null | /null  | 1        | 1     | property_id==99000099-9999-4999-a999-999999999999 | /null       | /null       | 99000099-9999-4999-a999-999999999999                                       |