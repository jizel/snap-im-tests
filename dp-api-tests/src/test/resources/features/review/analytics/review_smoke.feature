Feature: Review
  Testing of api for review module alias trip_advisor with mock data in db - testing property id is "99000099-9999-4999-a999-999999999999"
  data in db are mostly increasing some of the data also includes nulls

  NOTE: every monday will some tests fails because date calculation
  #todo granularity (date values) new rules when finished add tests

  Background:
    Given Database is cleaned and default entities are created
    Given The following users exist for customer "11111111-0000-4000-a000-555555555555" as primary "true"
      | id                                   | userType | userName  | firstName | lastName | email                     | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | customer | snapUser1 | Snapshot  | User1    | snapUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 99000199-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 11111111-0000-4000-a000-555555555555 |
    Given Relation between user "snapUser1" and property with code "p1_code" exists

  @Smoke
  Scenario Outline: Get specific analytics data from API for a given granularity of more complex endpoints - smoke
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of review analytics

    Examples:
      | url        | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |


  @Smoke
  Scenario Outline: Checking data correctness for analytics - smoke
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property_id>" since "<since>" until "<until>"
    And Response code is "200"
    And Content type is "application/json"
    Then Review file "<json_input_file>" is equals to previous response for analytics

    Examples:
      | json_input_file                     | url         | property_id                          | granularity | since      | until      |
      | /analytics/analytics_for_day.json   | /analytics/ | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |

  @Smoke
  Scenario: Get amount of analytics data from API for a given granularity of more complex endpoints
    When Get trip advisor travellers "travellers" data with "day" granularity for "99000199-9999-4999-a999-999999999999" since "today" until "today"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "day"
    And Response since is "today" for granularity "day"
    And Response until is "today" for granularity "day"
    And Response contains 1 number of analytics for travelers

  @Smoke
  Scenario Outline: Checking data correctness for all travellers analytics - smoke
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property_id>" since "<since>" until "<until>"
    Then Review travellers file "<json_input_file>" is equals to previous response
    And Response code is "200"
    And Content type is "application/json"

    Examples:
      | json_input_file           | url          | property_id                          | granularity | since      | until      |
      | /analytics_for_day.json   | /travellers/ | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
