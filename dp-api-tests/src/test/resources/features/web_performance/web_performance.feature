Feature: web_performance

  Scenario Outline: Get web_performance analytics data from API for a given wrong granularity
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "2015-12-03" until "2015-12-03"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "63"

    Examples: 
      | url                                                   | granularity |
      | /web_performance/analytics/visits                     | ddd         |
      | /web_performance/analytics/visits_unique              | www         |
      | /web_performance/analytics/revenue                    | yyy         |
      | /web_performance/analytics/conversion_rates           | ttt         |
      | /web_performance/analytics/visits/countries           | uuu         |
      | /web_performance/analytics/visits_unique/countries    | month1      |
      | /web_performance/analytics/conversion_rates/countries | DAY         |
      | /web_performance/analytics/referrals                  | WEEK        |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Response code is "200"
    #And Data is owned by ""
    And Content type is "application/json"
    And Response contains "<count>" values
    And Body contains entity with attribute "since" value "<since>"
    And Body contains entity with attribute "until" value "<until>"
    And Body contains entity with attribute "granularity" value "<granularity>"

    Examples: 
      | url                                                   | granularity | count | since      | until      |
      | /web_performance/analytics/visits                     | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits_unique              | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/revenue                    | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/conversion_rates           | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits/countries           | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits_unique/countries    | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits                     | day         | 11    | 2015-11-03 | 2015-11-13 |
      | /web_performance/analytics/visits_unique              | day         | 11    | 2015-11-03 | 2015-11-13 |
      | /web_performance/analytics/revenue                    | day         | 11    | 2015-11-03 | 2015-11-13 |
      | /web_performance/analytics/conversion_rates           | day         | 11    | 2015-11-03 | 2015-11-13 |
      | /web_performance/analytics/visits/countries           | day         | 11    | 2015-11-03 | 2015-11-13 |
      | /web_performance/analytics/visits_unique/countries    | day         | 11    | 2015-12-03 | 2015-12-13 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits                     | day         | 23    | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/visits_unique              | day         | 23    | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/revenue                    | day         | 23    | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/conversion_rates           | day         | 23    | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/visits/countries           | day         | 23    | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/visits_unique/countries    | day         | 23    | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits                     | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /web_performance/analytics/visits_unique              | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /web_performance/analytics/revenue                    | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /web_performance/analytics/conversion_rates           | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /web_performance/analytics/visits/countries           | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /web_performance/analytics/visits_unique/countries    | day         | 92    | 2015-06-07 | 2015-12-07 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits                     | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/visits_unique              | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/revenue                    | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/conversion_rates           | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/visits/countries           | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/visits_unique/countries    | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits                     | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/visits_unique              | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/revenue                    | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/conversion_rates           | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/visits/countries           | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/visits_unique/countries    | week        | 1     | 2015-11-07 | 2015-11-13 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits                     | week        | 2     | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/visits_unique              | week        | 2     | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/revenue                    | week        | 2     | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/conversion_rates           | week        | 2     | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/visits/countries           | week        | 2     | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/visits_unique/countries    | week        | 2     | 2015-11-07 | 2015-11-23 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits                     | week        | 26    | 2015-01-07 | 2015-12-23 |
      | /web_performance/analytics/visits_unique              | week        | 26    | 2015-01-07 | 2015-11-23 |
      | /web_performance/analytics/revenue                    | week        | 26    | 2015-01-07 | 2015-11-23 |
      | /web_performance/analytics/conversion_rates           | week        | 26    | 2015-01-07 | 2015-11-23 |
      | /web_performance/analytics/visits/countries           | week        | 26    | 2015-01-07 | 2015-11-23 |
      | /web_performance/analytics/visits_unique/countries    | week        | 26    | 2015-01-07 | 2015-11-23 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits                     | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /web_performance/analytics/visits_unique              | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /web_performance/analytics/revenue                    | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /web_performance/analytics/conversion_rates           | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /web_performance/analytics/visits/countries           | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /web_performance/analytics/visits_unique/countries    | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits                     | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /web_performance/analytics/visits_unique              | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /web_performance/analytics/revenue                    | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /web_performance/analytics/conversion_rates           | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /web_performance/analytics/visits/countries           | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /web_performance/analytics/visits_unique/countries    | month       | 1     | 2015-02-01 | 2015-03-23 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/visits                     | month       | 36    | 2013-02-01 | 2015-11-30 |
      | /web_performance/analytics/visits_unique              | month       | 36    | 2013-02-01 | 2015-11-30 |
      | /web_performance/analytics/revenue                    | month       | 36    | 2013-02-01 | 2015-11-30 |
      | /web_performance/analytics/conversion_rates           | month       | 36    | 2013-02-01 | 2015-11-30 |
      | /web_performance/analytics/visits/countries           | month       | 36    | 2013-02-01 | 2015-11-30 |
      | /web_performance/analytics/visits_unique/countries    | month       | 36    | 2013-02-01 | 2015-11-30 |
      | /web_performance/analytics/conversion_rates/countries | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /web_performance/analytics/referrals                  | day         | 1     | 2015-12-07 | 2015-12-07 |

  Scenario: Getting non-existent analytics data
    When Getting "/web_performance/analytics/not_present" data with "day" granularity for "99999999-9999-4999-a999-999999999999" since "2015-12-03" until "2015-12-03"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "52"

  Scenario: Getting mismatched metrics analytics data
    When Getting "/web_performance/analytics/referrals/tweets" data with "day" granularity for "99999999-9999-4999-a999-999999999999" since "2015-12-03" until "2015-12-03"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "52"

  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                                   | error_code | custom_code |
      | /web_performance/analytics/visits                     | 400        | 52          |
      | /web_performance/analytics/visits_unique              | 400        | 52          |
      | /web_performance/analytics/revenue                    | 400        | 52          |
      | /web_performance/analytics/conversion_rates           | 400        | 52          |
      | /web_performance/analytics/visits/countries           | 400        | 52          |
      | /web_performance/analytics/visits_unique/countries    | 400        | 52          |
      | /web_performance/analytics/conversion_rates/countries | 400        | 52          |
      | /web_performance/analytics/referrals                  | 400        | 52          |

  Scenario Outline: Get analytics data from API with missing parameters
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Response code is "200"
    And Content type is "application/json"
    And Response contains <count> values

    Examples: 
      | url                                                   | granularity | start_date | end_date   | count |
      | /web_performance/analytics/visits                     |             | 2015-12-03 | 2015-12-03 | 1     |
      | /web_performance/analytics/visits_unique              | day         |            | 2015-12-03 | 31    |
      | /web_performance/analytics/revenue                    | day         | 2015-12-03 |            | 31    |
      | /web_performance/analytics/conversion_rates           | day         |            |            | 31    |
      | /web_performance/analytics/visits/countries           |             |            |            | 31    |
      | /web_performance/analytics/visits_unique/countries    | day         | 2015-12-03 |            | 31    |
      | /web_performance/analytics/conversion_rates/countries | day         |            |            | 31    |
      | /web_performance/analytics/referrals                  |             |            |            | 31    |

  Scenario Outline: Get analytics data from API from 1800s
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "since" value "<start_date>"
    And Body contains entity with attribute "until" value "<end_date>"

    Examples: 
      | url                                                   | granularity | start_date | end_date   |
      | /social_media/analytics/facebook                      | month       | 1888-09-01 | 1890-10-01 |
      | /web_performance/analytics/visits                     | month       | 1888-09-01 | 1890-10-01 |
      | /web_performance/analytics/visits_unique              | month       | 1888-09-01 | 1890-10-01 |
      | /web_performance/analytics/revenue                    | month       | 1888-09-01 | 1890-10-01 |
      | /web_performance/analytics/conversion_rates           | month       | 1888-09-01 | 1890-10-01 |
      | /web_performance/analytics/visits/countries           | month       | 1888-09-01 | 1890-10-01 |
      | /web_performance/analytics/visits_unique/countries    | month       | 1888-09-01 | 1890-10-01 |
      | /web_performance/analytics/conversion_rates/countries | month       | 1888-09-01 | 1890-10-01 |
      | /web_performance/analytics/referrals                  | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/facebook                      | day         | 1888-09-01 | 1888-09-01 |
      | /web_performance/analytics/visits                     | day         | 1888-09-01 | 1888-09-01 |
      | /web_performance/analytics/visits_unique              | day         | 1888-09-01 | 1888-09-01 |
      | /web_performance/analytics/revenue                    | day         | 1888-09-01 | 1888-09-01 |
      | /web_performance/analytics/conversion_rates           | day         | 1888-09-01 | 1888-09-01 |
      | /web_performance/analytics/visits/countries           | day         | 1888-09-01 | 1888-09-01 |
      | /web_performance/analytics/visits_unique/countries    | day         | 1888-09-01 | 1888-09-01 |
      | /web_performance/analytics/conversion_rates/countries | month       | 1888-09-01 | 1890-10-01 |
      | /web_performance/analytics/referrals                  | month       | 1888-09-01 | 1890-10-01 |

  Scenario Outline: Checking default parameter values
    Empty column in examples section means default value will be used for this parameter.
    if text is empty, returns null
    if text is date in ISO format (2015-01-01), it returns this date
    text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which will add or substract
    particular number of days/weeks/months from first part of expression

    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Response contains no more than <count> values

    Examples: 
      | url                                                   | granularity | start_date     | end_date          | expected_granularity | expected_since    | expected_until | count |
      | /web_performance/analytics/visits                     |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/visits                     |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /web_performance/analytics/visits                     | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/visits                     | day         | today          |                   | day                  | today             | today          | 1     |
      | /web_performance/analytics/visits                     | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /web_performance/analytics/visits                     | week        | today          |                   | week                 | today             | today          | 0     |
      | /web_performance/analytics/visits                     | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /web_performance/analytics/visits                     | month       | today          |                   | month                | today             | today          | 0     |
      | /web_performance/analytics/visits                     | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /web_performance/analytics/visits                     | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /web_performance/analytics/visits                     | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /web_performance/analytics/visits                     | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /web_performance/analytics/visits_unique              |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/visits_unique              |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /web_performance/analytics/visits_unique              | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/visits_unique              | day         | today          |                   | day                  | today             | today          | 1     |
      | /web_performance/analytics/visits_unique              | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /web_performance/analytics/visits_unique              | week        | today          |                   | week                 | today             | today          | 0     |
      | /web_performance/analytics/visits_unique              | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /web_performance/analytics/visits_unique              | month       | today          |                   | month                | today             | today          | 0     |
      | /web_performance/analytics/visits_unique              | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /web_performance/analytics/visits_unique              | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /web_performance/analytics/visits_unique              | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /web_performance/analytics/visits_unique              | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /web_performance/analytics/revenue                    |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/revenue                    |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /web_performance/analytics/revenue                    | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/revenue                    | day         | today          |                   | day                  | today             | today          | 1     |
      | /web_performance/analytics/revenue                    | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /web_performance/analytics/revenue                    | week        | today          |                   | week                 | today             | today          | 0     |
      | /web_performance/analytics/revenue                    | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /web_performance/analytics/revenue                    | month       | today          |                   | month                | today             | today          | 0     |
      | /web_performance/analytics/revenue                    | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /web_performance/analytics/revenue                    | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /web_performance/analytics/revenue                    | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /web_performance/analytics/revenue                    | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /web_performance/analytics/conversion_rates           |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/conversion_rates           |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /web_performance/analytics/conversion_rates           | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/conversion_rates           | day         | today          |                   | day                  | today             | today          | 1     |
      | /web_performance/analytics/conversion_rates           | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /web_performance/analytics/conversion_rates           | week        | today          |                   | week                 | today             | today          | 0     |
      | /web_performance/analytics/conversion_rates           | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /web_performance/analytics/conversion_rates           | month       | today          |                   | month                | today             | today          | 0     |
      | /web_performance/analytics/conversion_rates           | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /web_performance/analytics/conversion_rates           | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /web_performance/analytics/conversion_rates           | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /web_performance/analytics/conversion_rates           | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /web_performance/analytics/visits/countries           |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/visits/countries           |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /web_performance/analytics/visits/countries           | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/visits/countries           | day         | today          |                   | day                  | today             | today          | 1     |
      | /web_performance/analytics/visits/countries           | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /web_performance/analytics/visits/countries           | week        | today          |                   | week                 | today             | today          | 0     |
      | /web_performance/analytics/visits/countries           | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /web_performance/analytics/visits/countries           | month       | today          |                   | month                | today             | today          | 0     |
      | /web_performance/analytics/visits/countries           | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /web_performance/analytics/visits/countries           | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /web_performance/analytics/visits/countries           | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /web_performance/analytics/visits/countries           | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /web_performance/analytics/visits_unique/countries    |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/visits_unique/countries    |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /web_performance/analytics/visits_unique/countries    | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/visits_unique/countries    | day         | today          |                   | day                  | today             | today          | 1     |
      | /web_performance/analytics/visits_unique/countries    | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /web_performance/analytics/visits_unique/countries    | week        | today          |                   | week                 | today             | today          | 0     |
      | /web_performance/analytics/visits_unique/countries    | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /web_performance/analytics/visits_unique/countries    | month       | today          |                   | month                | today             | today          | 0     |
      | /web_performance/analytics/visits_unique/countries    | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /web_performance/analytics/visits_unique/countries    | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /web_performance/analytics/visits_unique/countries    | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /web_performance/analytics/visits_unique/countries    | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /web_performance/analytics/conversion_rates/countries |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/conversion_rates/countries |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /web_performance/analytics/conversion_rates/countries | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/conversion_rates/countries | day         | today          |                   | day                  | today             | today          | 1     |
      | /web_performance/analytics/conversion_rates/countries | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /web_performance/analytics/conversion_rates/countries | week        | today          |                   | week                 | today             | today          | 0     |
      | /web_performance/analytics/conversion_rates/countries | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /web_performance/analytics/conversion_rates/countries | month       | today          |                   | month                | today             | today          | 0     |
      | /web_performance/analytics/conversion_rates/countries | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /web_performance/analytics/conversion_rates/countries | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /web_performance/analytics/conversion_rates/countries | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /web_performance/analytics/conversion_rates/countries | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /web_performance/analytics/referrals                  |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/referrals                  |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /web_performance/analytics/referrals                  | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /web_performance/analytics/referrals                  | day         | today          |                   | day                  | today             | today          | 1     |
      | /web_performance/analytics/referrals                  | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /web_performance/analytics/referrals                  | week        | today          |                   | week                 | today             | today          | 0     |
      | /web_performance/analytics/referrals                  | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /web_performance/analytics/referrals                  | month       | today          |                   | month                | today             | today          | 0     |
      | /web_performance/analytics/referrals                  | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /web_performance/analytics/referrals                  | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /web_performance/analytics/referrals                  | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /web_performance/analytics/referrals                  | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |

  Scenario Outline: Checking number of values in response for various granularities
    When Getting "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains <count> values

    Examples: 
      | url                                                   | granularity | since           | until | count |
      #this one is different - returns all metrics together, so validation of number of values needs to be different
      | /web_performance/analytics/visits                     | day         | today - 1 day   | today | 2     |
      | /web_performance/analytics/visits_unique              | day         | today - 6 days  | today | 7     |
      | /web_performance/analytics/revenue                    | day         | today - 7 days  | today | 8     |
      | /web_performance/analytics/conversion_rates           | day         | today - 8 days  | today | 9     |
      | /web_performance/analytics/visits/countries           | day         | today - 29 days | today | 30    |
      | /web_performance/analytics/visits_unique/countries    | day         | today - 30 days | today | 31    |
      | /web_performance/analytics/conversion_rates/countries | week        | today           | today | 0     |
