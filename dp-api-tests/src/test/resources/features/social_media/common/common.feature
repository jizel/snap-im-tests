Feature: common

  Scenario Outline: Get collective analytics data from API for all metrics
    When Get social media "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains <count> values for all metrics

    Examples: 
      | url        | granularity | since      | until      | count |
      | /analytics | day         | 2015-12-07 | 2015-12-07 | 1     |
      | /analytics | day         | 2015-12-03 | 2015-12-13 | 11    |
      | /analytics | day         | 2015-12-07 | 2015-12-23 | 17    |
      | /analytics | week        | 2015-12-07 | 2015-12-13 | 1     |
      | /analytics | week        | 2015-12-03 | 2015-12-13 | 1     |
      | /analytics | week        | 2015-12-07 | 2015-12-23 | 2     |
      | /analytics | month       | 2015-11-01 | 2015-11-30 | 1     |
      | /analytics | month       | 2015-02-01 | 2015-03-23 | 1     |
      | /analytics | month       | 2013-02-01 | 2016-12-31 | 12    |

  Scenario Outline: Get analytics data from API for specific metrics
    When Get social media "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<since>" until "<until>"
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
      | /analytics/reach      | day         | 2015-06-07 | 2015-12-07 | 90    |
      | /analytics/followers  | week        | 2015-12-07 | 2015-12-13 | 1     |
      | /analytics/followers  | week        | 2015-12-03 | 2015-12-13 | 1     |
      | /analytics/engagement | week        | 2015-12-03 | 2015-12-13 | 1     |
      | /analytics/engagement | week        | 2015-12-07 | 2015-12-23 | 2     |
      | /analytics/reach      | week        | 2015-12-07 | 2015-12-23 | 2     |
      | /analytics/reach      | week        | 2015-01-07 | 2015-12-23 | 26    |
      | /analytics/reach      | week        | 2015-01-07 | 2015-12-23 | 26    |
      | /analytics/followers  | month       | 2015-11-01 | 2015-11-30 | 1     |
      | /analytics/engagement | month       | 2015-02-01 | 2015-03-23 | 1     |
      | /analytics/reach      | month       | 2013-02-01 | 2016-12-31 | 12    |

  Scenario: Getting analytics data for a large time period
    When Get social media "/analytics" data with "day" granularity for "99999999-9999-4999-a999-999999999999" since "1888-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"

  Scenario: Getting non-existent analytics data
    When Get social media "/analytics/not_present" data with "day" granularity for "99999999-9999-4999-a999-999999999999" since "2015-09-01" until "2015-09-01"
    Then Response code is "404"
    And Body is empty


  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Body is empty

    Examples: 
      | url                   | error_code | custom_code |
      | /analytics/           | 404        | 52          |
      | /analytics/followers  | 404        | 52          |
      | /analytics/engagement | 404        | 52          |
      | /analytics/reach      | 404        | 52          |

  Scenario Outline: Get analytics data from API with missing parameters
    When Get social media "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                   | granularity | start_date | end_date   |
      | /analytics/followers  |             | 2015-12-03 | 2015-12-03 |
      | /analytics/engagement | day         |            | 2015-12-03 |
      | /analytics/reach      | day         | 2015-12-03 |            |
      | /analytics/           | day         |            |            |
      | /analytics/           |             |            |            |

  Scenario Outline: Checking default parameter values for all metrics
    When Get social media "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"

    Examples: 
      | url        | granularity | start_date     | end_date          | expected_granularity | expected_since   | expected_until |
      | /analytics |             |                |                   | day                  | today - 31 days  | today          |
      | /analytics |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03       | 2015-12-03     |
      | /analytics | day         |                | today             | day                  | today - 31 days  | today          |
      | /analytics | day         | today          |                   | day                  | today            | today          |
      | /analytics | week        |                | today             | week                 | today - 13 weeks | today          |
      | /analytics | week        | today          |                   | week                 | today            | today          |
      | /analytics | month       |                | today             | month                | today - 6 months | today          |
      | /analytics | month       | today          |                   | month                | today            | today          |
      | /analytics | day         | today          | today - 100 days  | day                  | today - 31 days  | today          |
      | /analytics | week        | today          | today - 30 weeks  | week                 | today - 13 weeks | today          |
      | /analytics | month       | today          | today - 40 months | month                | today - 6 months | today          |
      | /analytics | day         | today + 2 days | today + 3 days    | day                  | today            | today          |

  Scenario Outline: Checking default parameter values for specific metrics
    When Get social media "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"

    Examples: 
      | url                   | granularity | start_date     | end_date          | expected_granularity | expected_since   | expected_until |
      | /analytics/followers  |             |                |                   | day                  | today - 31 days  | today          |
      | /analytics/followers  |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03       | 2015-12-03     |
      | /analytics/followers  | day         |                | today             | day                  | today - 31 days  | today          |
      | /analytics/followers  | day         | today          |                   | day                  | today            | today          |
      | /analytics/followers  | week        |                | today             | week                 | today - 13 weeks | today          |
      | /analytics/followers  | week        | today          |                   | week                 | today            | today          |
      | /analytics/followers  | month       |                | today             | month                | today - 6 months | today          |
      | /analytics/followers  | month       | today          |                   | month                | today            | today          |
      | /analytics/followers  | day         | today          | today - 100 days  | day                  | today - 31 days  | today          |
      | /analytics/followers  | week        | today          | today - 30 weeks  | week                 | today - 13 weeks | today          |
      | /analytics/followers  | month       | today          | today - 40 months | month                | today - 6 months | today          |
      | /analytics/followers  | day         | today + 2 days | today + 3 days    | day                  | today            | today          |
      | /analytics/engagement |             |                |                   | day                  | today - 31 days  | today          |
      | /analytics/engagement |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03       | 2015-12-03     |
      | /analytics/engagement | day         |                | today             | day                  | today - 31 days  | today          |
      | /analytics/engagement | day         | today          |                   | day                  | today            | today          |
      | /analytics/engagement | week        |                | today             | week                 | today - 13 weeks | today          |
      | /analytics/engagement | week        | today          |                   | week                 | today            | today          |
      | /analytics/engagement | month       |                | today             | month                | today - 6 months | today          |
      | /analytics/engagement | month       | today          |                   | month                | today            | today          |
      | /analytics/engagement | day         | today          | today - 100 days  | day                  | today - 31 days  | today          |
      | /analytics/engagement | week        | today          | today - 30 weeks  | week                 | today - 13 weeks | today          |
      | /analytics/engagement | month       | today          | today - 40 months | month                | today - 6 months | today          |
      | /analytics/engagement | day         | today + 2 days | today + 3 days    | day                  | today            | today          |
      | /analytics/reach      |             |                |                   | day                  | today - 31 days  | today          |
      | /analytics/reach      |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03       | 2015-12-03     |
      | /analytics/reach      | day         |                | today             | day                  | today - 31 days  | today          |
      | /analytics/reach      | day         | today          |                   | day                  | today            | today          |
      | /analytics/reach      | week        |                | today             | week                 | today - 13 weeks | today          |
      | /analytics/reach      | week        | today          |                   | week                 | today            | today          |
      | /analytics/reach      | month       |                | today             | month                | today - 6 months | today          |
      | /analytics/reach      | month       | today          |                   | month                | today            | today          |
      | /analytics/reach      | day         | today          | today - 100 days  | day                  | today - 31 days  | today          |
      | /analytics/reach      | week        | today          | today - 30 weeks  | week                 | today - 13 weeks | today          |
      | /analytics/reach      | month       | today          | today - 40 months | month                | today - 6 months | today          |
      | /analytics/reach      | day         | today + 2 days | today + 3 days    | day                  | today            | today          |

  Scenario Outline: Verify overall analytics values
    When Verifying sum of "<metric>" from Facebook, Twitter, and Instagram with "<granularity>" granularity for property "99999999-9999-4999-a999-999999999999", since "<since>", until "<until>"
    Then Content type is "application/json"
    And Response code is "200"

    Examples: 
      | metric     | granularity | since      | until      |
      | followers  | day         | 2015-12-12 | 2015-12-12 |
      | engagement | week        | 2015-12-01 | 2015-12-12 |
      | reach      | month       | 2015-11-01 | 2015-12-12 |