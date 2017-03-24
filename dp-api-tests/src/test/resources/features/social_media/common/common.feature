@SocialMedia
Feature: Social media - Commons

  Background:
    Given Database is cleaned and default entities are created


  Scenario Outline: Get collective analytics data from API for all metrics
    When Get social media "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains <count> values for all metrics

    Examples: 
      | url        | granularity | since      | until      | count |
      | /analytics | day         | 2015-12-07 | 2015-12-07 | 1     |
      | /analytics | day         | 2015-12-03 | 2015-12-13 | 11    |
      | /analytics | day         | 2015-12-07 | 2015-12-23 | 17    |
      | /analytics | week        | 2015-12-07 | 2015-12-13 | 1     |
      | /analytics | week        | 2015-12-03 | 2015-12-13 | 2     |
      | /analytics | week        | 2015-12-07 | 2015-12-23 | 3     |
      | /analytics | month       | 2015-11-01 | 2015-11-30 | 1     |
      | /analytics | month       | 2015-02-01 | 2015-03-23 | 2     |
      | /analytics | month       | 2015-02-01 | 2016-01-01 | 12    |

  Scenario Outline: Get analytics data from API for specific metrics
    When Get social media "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains <count> values

    Examples: 
      | url                   | granularity | since      | until      | count |
      | /analytics/followers  | day         | 2015-12-07 | 2015-12-07 | 1     |
      | /analytics/followers  | day         | 2015-12-03 | 2015-12-13 | 11    |
      | /analytics/engagement | day         | 2015-12-03 | 2015-12-13 | 11    |
      | /analytics/engagement | day         | 2015-12-07 | 2015-12-23 | 17    |
      | /analytics/reach      | day         | 2015-12-07 | 2015-12-23 | 17    |
      | /analytics/reach      | day         | 2015-06-07 | 2016-02-01 | 240   |
      | /analytics/followers  | week        | 2015-12-07 | 2015-12-13 | 1     |
      | /analytics/followers  | week        | 2015-12-03 | 2015-12-13 | 2     |
      | /analytics/engagement | week        | 2015-12-03 | 2015-12-13 | 2     |
      | /analytics/engagement | week        | 2015-12-07 | 2015-12-23 | 3     |
      | /analytics/reach      | week        | 2015-12-07 | 2015-12-23 | 3     |
      | /analytics/reach      | week        | 2015-01-07 | 2015-06-03 | 22    |
      | /analytics/reach      | week        | 2015-01-07 | 2015-12-23 | 51    |
      | /analytics/followers  | month       | 2015-11-01 | 2015-11-30 | 1     |
      | /analytics/engagement | month       | 2015-02-01 | 2015-03-23 | 2     |
      | /analytics/reach      | month       | 2015-02-01 | 2016-01-01 | 12    |

  Scenario: Getting analytics data for a large time period
    When Get social media "/analytics" data with "day" granularity for "99000099-9999-4999-a999-999999999999" since "1888-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"

  Scenario: Getting non-existent analytics data
    When Get social media "/analytics/not_present" data with "day" granularity for "99000099-9999-4999-a999-999999999999" since "2015-09-01" until "2015-09-01"
    Then Response code is "404"
    And Body is empty


  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is <custom_code>

    Examples: 
      | url                   | error_code | custom_code |
      | /analytics/           | 400        | 40002       |
      | /analytics/followers  | 400        | 40002       |
      | /analytics/engagement | 400        | 40002       |
      | /analytics/reach      | 400        | 40002       |

  Scenario Outline: Get analytics data from API with missing parameters
    When Get social media "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is 40002

    Examples: 
      | url                   | granularity | start_date | end_date   |
      | /analytics/followers  |             | 2015-12-03 | 2015-12-03 |
      | /analytics/engagement | day         |            | 2015-12-03 |
      | /analytics/reach      | day         | 2015-12-03 |            |
      | /analytics/           | day         |            |            |
      | /analytics/           |             |            |            |


#  Checking response code and content type here does not make sense (actually 4 get requests, not just one) and is covered elsewhere (Get analytics data from API for specific metrics)
  Scenario Outline: Verify overall analytics values
    When Verifying sum of "<metric>" from Facebook, Twitter, and Instagram with "<granularity>" granularity for property "99000099-9999-4999-a999-999999999999", since "<since>", until "<until>"

    Examples: 
      | metric     | granularity | since      | until      |
      | followers  | day         | 2015-12-12 | 2015-12-12 |
      | engagement | week        | 2015-12-01 | 2015-12-12 |
      | reach      | month       | 2015-11-01 | 2015-12-12 |