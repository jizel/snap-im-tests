# TODO: DP-2014 - time based collection pagination is disabled, the test fails when it is enabled
Feature: Review travelers limit
  Testing of api for review modul alias trip_advisor with mock data in db and large time interval - testing property id is "99000199-9999-4999-a999-999999999999"
  data in db are mostly increasing some of the data also includes nullsT

  NOTE: every monday will some tests fails because date calculation
  #todo - granularity (date values) new rules when finished add tests

  Scenario Outline: Get amount of specific analytics data from API for a given granularity for travelers overall bubble rating
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of analytics for travelers overall bubble rating

    Examples:
      | url                               | granularity | count | since      | until      | real_since | real_until | property                             |
      | /travellers/overall_bubble_rating | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/overall_bubble_rating | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/overall_bubble_rating | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get amount specific analytics data from API for a given granularity for aspect of business
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of analytics for travelers for aspect of business

    Examples:
      | url                             | granularity | count | since      | until      | real_since | real_until | property                             |
      | /travellers/aspects_of_business | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get amount of specific analytics data from API for a given granularity for travelers number of reviews
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of analytics for travelers number of reviews

    Examples:
      | url                            | granularity | count | since      | until      | real_since | real_until | property                             |
      |  /travellers/number_of_reviews | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |
      |  /travellers/number_of_reviews | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |
      |  /travellers/number_of_reviews | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |

  @Smoke
  Scenario Outline: Get amount of analytics data from API for a given granularity of more complex endpoints
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of analytics for travelers

    Examples:
      | url           | granularity | count | since      | until      | real_since | real_until | property                             |
      |  /travellers/ | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |
      |  /travellers/ | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |
      |  /travellers/ | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000199-9999-4999-a999-999999999999 |


