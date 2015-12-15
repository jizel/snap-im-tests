Feature: common

  Scenario Outline: Get collective analytics data from API for all metrics
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains <count> values for all metrics

    Examples: 
      | url                     | granularity | since      | until      | count |
      | /social_media/analytics | day         | 2015-12-07 | 2015-12-07 | 1     |
      | /social_media/analytics | day         | 2015-12-03 | 2015-12-13 | 11    |
      | /social_media/analytics | day         | 2015-12-07 | 2015-12-23 | 17    |
      | /social_media/analytics | week        | 2015-12-07 | 2015-12-13 | 1     |
      | /social_media/analytics | week        | 2015-12-03 | 2015-12-13 | 1     |
      | /social_media/analytics | week        | 2015-12-07 | 2015-12-23 | 2     |
      | /social_media/analytics | month       | 2015-11-01 | 2015-11-30 | 1     |
      | /social_media/analytics | month       | 2015-02-01 | 2015-03-23 | 1     |
      | /social_media/analytics | month       | 2013-02-01 | 2016-12-31 | 36    |

  Scenario Outline: Get analytics data from API for specific metrics
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains <count> values

    Examples: 
      | url                                | granularity | since      | until      | count |
      | /social_media/analytics/followers  | day         | 2015-12-07 | 2015-12-07 | 1     |
      | /social_media/analytics/followers  | day         | 2015-12-03 | 2015-12-13 | 11    |
      | /social_media/analytics/engagement | day         | 2015-12-03 | 2015-12-13 | 11    |
      | /social_media/analytics/engagement | day         | 2015-12-07 | 2015-12-23 | 17    |
      | /social_media/analytics/reach      | day         | 2015-12-07 | 2015-12-23 | 17    |
      | /social_media/analytics/reach      | day         | 2015-06-07 | 2015-12-07 | 90    |
      | /social_media/analytics/followers  | week        | 2015-12-07 | 2015-12-13 | 1     |
      | /social_media/analytics/followers  | week        | 2015-12-03 | 2015-12-13 | 1     |
      | /social_media/analytics/engagement | week        | 2015-12-03 | 2015-12-13 | 1     |
      | /social_media/analytics/engagement | week        | 2015-12-07 | 2015-12-23 | 2     |
      | /social_media/analytics/reach      | week        | 2015-12-07 | 2015-12-23 | 2     |
      | /social_media/analytics/reach      | week        | 2015-01-07 | 2015-12-23 | 26    |
      | /social_media/analytics/reach      | week        | 2015-01-07 | 2015-12-23 | 26    |
      | /social_media/analytics/followers  | month       | 2015-11-01 | 2015-11-30 | 1     |
      | /social_media/analytics/engagement | month       | 2015-02-01 | 2015-03-23 | 1     |
      | /social_media/analytics/reach      | month       | 2013-02-01 | 2016-12-31 | 36    |

  Scenario: Getting analytics data for a large time period
    When Getting "/social_media/analytics" data with "day" granularity for "99999999-9999-4999-a999-999999999999" since "1888-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"

  Scenario: Getting non-existent analytics data
    When Getting "/social_media/analytics/not_present" data with "day" granularity for "99999999-9999-4999-a999-999999999999" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario: Getting facebook mismatched metrics analytics data
    When Getting "/social_media/analytics/facebook/tweets" data with "day" granularity for "99999999-9999-4999-a999-999999999999" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                | error_code | custom_code |
      | /social_media/analytics/           | 400        | 52          |
      | /social_media/analytics/followers  | 400        | 52          |
      | /social_media/analytics/engagement | 400        | 52          |
      | /social_media/analytics/reach      | 400        | 52          |

  Scenario Outline: Get analytics data from API with missing parameters
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                | granularity | start_date | end_date   |
      | /social_media/analytics/followers  |             | 2015-12-03 | 2015-12-03 |
      | /social_media/analytics/engagement | day         |            | 2015-12-03 |
      | /social_media/analytics/reach      | day         | 2015-12-03 |            |
      | /social_media/analytics/           | day         |            |            |
      | /social_media/analytics/           |             |            |            |

  Scenario Outline: Checking default parameter values for all metrics
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"

    Examples: 
      | url                     | granularity | start_date     | end_date          | expected_granularity | expected_since    | expected_until |
      | /social_media/analytics |             |                |                   | day                  | today - 1 month   | today          |
      | /social_media/analytics |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     |
      | /social_media/analytics | day         |                | today             | day                  | today - 1 month   | today          |
      | /social_media/analytics | day         | today          |                   | day                  | today             | today          |
      | /social_media/analytics | week        |                | today             | week                 | today - 13 weeks  | today          |
      | /social_media/analytics | week        | today          |                   | week                 | today             | today          |
      | /social_media/analytics | month       |                | today             | month                | today - 6 months  | today          |
      | /social_media/analytics | month       | today          |                   | month                | today             | today          |
      | /social_media/analytics | day         | today          | today - 100 days  | day                  | today - 90 days   | today          |
      | /social_media/analytics | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          |
      | /social_media/analytics | month       | today          | today - 40 months | month                | today - 36 months | today          |
      | /social_media/analytics | day         | today + 2 days | today + 3 days    | day                  | today             | today          |

  Scenario Outline: Checking default parameter values for specific metrics
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"

    Examples: 
      | url                                 | granularity | start_date     | end_date          | expected_granularity | expected_since    | expected_until |
      | /social_media/analytics/followers   |             |                |                   | day                  | today - 1 month   | today          |
      | /social_media/analytics/followers   |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     |
      | /social_media/analytics/followers   | day         |                | today             | day                  | today - 1 month   | today          |
      | /social_media/analytics/followers   | day         | today          |                   | day                  | today             | today          |
      | /social_media/analytics/followers   | week        |                | today             | week                 | today - 13 weeks  | today          |
      | /social_media/analytics/followers   | week        | today          |                   | week                 | today             | today          |
      | /social_media/analytics/followers   | month       |                | today             | month                | today - 6 months  | today          |
      | /social_media/analytics/followers   | month       | today          |                   | month                | today             | today          |
      | /social_media/analytics/followers   | day         | today          | today - 100 days  | day                  | today - 90 days   | today          |
      | /social_media/analytics/followers   | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          |
      | /social_media/analytics/followers   | month       | today          | today - 40 months | month                | today - 36 months | today          |
      | /social_media/analytics/followers   | day         | today + 2 days | today + 3 days    | day                  | today             | today          |
      | /social_media/analytics/engagement  |             |                |                   | day                  | today - 1 month   | today          |
      | /social_media/analytics/engagement  |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     |
      | /social_media/analytics/engagement  | day         |                | today             | day                  | today - 1 month   | today          |
      | /social_media/analytics/engagement  | day         | today          |                   | day                  | today             | today          |
      | /social_media/analytics/engagement  | week        |                | today             | week                 | today - 13 weeks  | today          |
      | /social_media/analytics/engagement  | week        | today          |                   | week                 | today             | today          |
      | /social_media/analytics/engagement  | month       |                | today             | month                | today - 6 months  | today          |
      | /social_media/analytics/engagement  | month       | today          |                   | month                | today             | today          |
      | /social_media/analytics/engagement  | day         | today          | today - 100 days  | day                  | today - 90 days   | today          |
      | /social_media/analytics/engagement  | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          |
      | /social_media/analytics/engagement  | month       | today          | today - 40 months | month                | today - 36 months | today          |
      | /social_media/analytics/engagement  | day         | today + 2 days | today + 3 days    | day                  | today             | today          |
      | /social_media/analytics/reach |             |                |                   | day                  | today - 1 month   | today          |
      | /social_media/analytics/reach |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     |
      | /social_media/analytics/reach | day         |                | today             | day                  | today - 1 month   | today          |
      | /social_media/analytics/reach | day         | today          |                   | day                  | today             | today          |
      | /social_media/analytics/reach | week        |                | today             | week                 | today - 13 weeks  | today          |
      | /social_media/analytics/reach | week        | today          |                   | week                 | today             | today          |
      | /social_media/analytics/reach | month       |                | today             | month                | today - 6 months  | today          |
      | /social_media/analytics/reach | month       | today          |                   | month                | today             | today          |
      | /social_media/analytics/reach | day         | today          | today - 100 days  | day                  | today - 90 days   | today          |
      | /social_media/analytics/reach | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          |
      | /social_media/analytics/reach | month       | today          | today - 40 months | month                | today - 36 months | today          |
      | /social_media/analytics/reach | day         | today + 2 days | today + 3 days    | day                  | today             | today          |

  Scenario Outline: Verify overall analytics values
    When Verifying sum of "<metric>" from Facebook, Twitter, and Instagram with "<granularity>" granularity for property "99999999-9999-4999-a999-999999999999", since "<since>", until "<until>"
    Then Content type is "application/json"
    And Response code is "200"

    Examples: 
      | metric     | granularity | since      | until      |
      | followers  | day         | 2015-12-12 | 2015-12-12 |
      | engagement | week        | 2015-12-01 | 2015-12-12 |
      | reach      | month       | 2015-11-01 | 2015-12-12 |