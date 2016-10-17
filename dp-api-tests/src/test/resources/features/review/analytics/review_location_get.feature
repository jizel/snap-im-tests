Feature: Review locaitons
  Testing of api for review locations with mock data in db - testing property id is "99000199-9999-4999-a999-999999999999"

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |

    Given The following properties exist with random address and billing address
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 99000199-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone      | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The password of user "default1" is "Password1"
    Given Relation between user with username "default1" and customer with code "c1t" exists with isPrimary "true"

    Given Get token for user "default1" with password "Password1"
    Given Set access token from session for customer steps defs
    Given Set access token for review steps defs

    Given Relation between user with username "default1" and property with code "p1_code" exists
    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"

# /locations/
#---------------------------------------------------------------------------------------------------------------------

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
      |             | -1     | /null       | /null         | /null         | 63          |
      |             | -1     | /null       | /null         | /null         | 63          |
      |             | text   | /null       | /null         | /null         | 63          |
      |             | text   | /null       | /null         | /null         | 63          |
      | -1          |        | /null       | /null         | /null         | 63          |
      | -1          |        | /null       | /null         | /null         | 63          |
      | 201         |        | /null       | /null         | /null         | 63          |
      | 21474836470 |        | /null       | /null         | /null         | 63          |
      | text        |        | /null       | /null         | /null         | 63          |
      | text        | /null  | /null       | /null         | /null         | 63          |
      | 10          | -1     | /null       | /null         | /null         | 63          |
      | text        | 0      | /null       | /null         | /null         | 63          |
      | 10          | text   | /null       | /null         | /null         | 63          |
      #filtering and sorting
      | 10          | 0      | /null       | location_name | location_name | 64          |
      | 10          | 0      | /null       | /null         | nonexistent   | 63          |
      | 10          | 0      | /null       | nonexistent   | /null         | 63          |
      | 10          | 0      | not_here==  | /null         | /null         | 63          |
      | 10          | 0      | random==CZ* | /null         | /null         | 63          |

  #TODO DP-935 - X-Total-Count header is missing
  Scenario Outline: Filtering list of locations
    When List of locations is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are "<returned>" locations returned
    And There are locations with following name returned in order: "<expected_names>"
  #And Total count is "<total>" #header "X-Total-Count" not implemented

    Examples:
      | limit | cursor | returned | total | filter                                        | sort          | sort_desc     | expected_names                                                                      |
      | 5     | 0      | 5        | 6     | location_name=='filter_town*'                 | location_name |               | filter_town1000, filter_town1001, filter_town1002, filter_town1003, filter_town1004 |
      | 5     | 0      | 5        | 6     | location_name=='filter_town*'                 |               | location_name | filter_town1005, filter_town1004, filter_town1003, filter_town1002, filter_town1001 |
      | 5     | 2      | 4        | 6     | location_name=='filter_town*'                 | location_name |               | filter_town1002, filter_town1003, filter_town1004, filter_town1005                  |
      | 5     | 2      | 4        | 6     | location_name=='filter_town*'                 |               | location_name | filter_town1003, filter_town1002, filter_town1001, filter_town1000                  |
      | /null | /null  | 1        | 1     | location_name==filter_town1005                |               |               | filter_town1005                                                                     |
      | /null | /null  | 1        | 1     | location_name==filter_town*;location_id==1005 |               |               | filter_town1005                                                                     |
      | /null | /null  | 1        | 1     | location_id==1001                             |               |               | filter_town1001                                                                     |


  #/location/<property>
  #---------------------------------------------------------------------------------------------------------------------

  @Smoke
  Scenario Outline: Getting location id for correct property id
    When Get trip advisor "<url>" for "<property>"
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "location_id" value "<location_id>"
    And Body contains entity with attribute "location_name" value "<location_name>"

    Examples:
      | url       | property                             | location_id | location_name |
      | /location | 99000199-9999-4999-a999-999999999999 | 19          | town19        |


  Scenario Outline: Getting error code for not existing property id from /location
    When Get trip advisor "<url>" for "<property>"
    Then Response code is "<response_code>"
    And Content type is "application/json"
    And Custom code is "<custom_code>"

    #todo DP-1117 - response message for property "null" has wrong message - check here after fix
    Examples:
      | url       | property                             | response_code | custom_code |
      | /location | 11111111-1111-4111-a111-111111111111 | 404           | 152         |
      | /location | null                                 | 400           | 63          |
      | /location | /null                                | 400           | 52          |


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
      | /null       | -1     | /null       | /null       | /null       | 63          |
      |             | -1     | /null       | /null       | /null       | 63          |
      | /null       | text   | /null       | /null       | /null       | 63          |
      |             | text   | /null       | /null       | /null       | 63          |
      | -1          |        | /null       | /null       | /null       | 63          |
      | -1          | /null  | /null       | /null       | /null       | 63          |
      | 201         | /null  | /null       | /null       | /null       | 63          |
      | 21474836470 | /null  | /null       | /null       | /null       | 63          |
      | text        |        | /null       | /null       | /null       | 63          |
      | text        | /null  | /null       | /null       | /null       | 63          |
      | 10          | -1     | /null       | /null       | /null       | 63          |
      | text        | 0      | /null       | /null       | /null       | 63          |
      | 10          | text   | /null       | /null       | /null       | 63          |
      #filtering and sorting
      | 10          | 0      | /null       | property_id | property_id | 64          |
      | 10          | 0      | /null       | /null       | nonexistent | 63          |
      | 10          | 0      | /null       | nonexistent | /null       | 63          |
      | 10          | 0      | not_here==  | /null       | /null       | 63          |
      | 10          | 0      | random==CZ* | /null       | /null       | 63          |

  #TODO DP-935 - X-Total-Count header is missing
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
