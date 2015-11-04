Feature: web_performance

  Scenario Outline: Get collective analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains 5 values for all metrics

    Examples: 
      | url                         | granularity |
      | /web_performance/analytics/ | day         |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                                   | granularity |
      | /web_performance/analytics/visits                     | day         |
      | /web_performance/analytics/visits/countries           | day         |
      | /web_performance/analytics/visits_unique              | week        |
      | /web_performance/analytics/visits_unique/countries    | week        |
      | /web_performance/analytics/revenue                    | week        |
      | /web_performance/analytics/conversion_rates           | month       |
      | /web_performance/analytics/convertion_rates/countries | month       |
      | /web_performance/analytics/referals                   | month       |

  Scenario: Getting non-existent analytics data
    When Getting "/web_performance/analytics/not_present" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario: Getting mismatched metrics analytics data
    When Getting "/web_performance/analytics/referals/countries" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                                   | error_code | custom_code |
      | /web_performance/analytics                            | 400        | 52          |
      | /web_performance/analytics/visits                     | 400        | 52          |
      | /web_performance/analytics/visits/countries           | 400        | 52          |
      | /web_performance/analytics/visits_unique              | 400        | 52          |
      | /web_performance/analytics/visits_unique/countries    | 400        | 52          |
      | /web_performance/analytics/revenue                    | 400        | 52          |
      | /web_performance/analytics/conversion_rates           | 400        | 52          |
      | /web_performance/analytics/convertion_rates/countries | 400        | 52          |
      | /web_performance/analytics/referals                   | 400        | 52          |

  Scenario Outline: Getting a list of items
    When List of "<url>" is got with limit "<limit>" and cursor "" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are at most <count> items returned

    Examples: 
      | url                                                   | limit | count |
      | /web_performance/analytics/visits/countries           | 5     | 5     |
      | /web_performance/analytics/visits/countries           | 15    | 15    |
      | /web_performance/analytics/visits/countries           |       | 50    |
      | /web_performance/analytics/visits_unique/countries    | 5     | 5     |
      | /web_performance/analytics/visits_unique/countries    | 15    | 15    |
      | /web_performance/analytics/visits_unique/countries    |       | 50    |
      | /web_performance/analytics/conversion_rates/countries | 5     | 5     |
      | /web_performance/analytics/convertion_rates/countries | 15    | 15    |
      | /web_performance/analytics/convertion_rates/countries |       | 50    |
      | /web_performance/analytics/referals                   | 5     | 5     |
      | /web_performance/analytics/referals                   | 15    | 15    |
      | /web_performance/analytics/referals                   |       | 50    |

  Scenario: Get analytics data from API for an invalid granularity
    When Getting "/web_performance/analytics/visits/countries" data with "invalid" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "63"

  Scenario Outline: Checking error codes for getting list of referals broken down by referring site
    When List of "<url>" is got with limit "<limit>" and cursor "" and filter empty and sort empty
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                 | limit | response_code | custom_code |
      | /web_performance/analytics/referals |       | 400           | 63          |
      | /web_performance/analytics/referals | -1    | 400           | 63          |
      | /web_performance/analytics/referals | text  | 400           | 63          |
      | /web_performance/analytics/referals | 10    | 400           | 63          |

  Scenario Outline: Get analytics data from API with missing parameters
    When Getting "<url>" data with "<granularity>" granularity for "property" since "<start_date>" until "<end_date>"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                         | granularity | start_date | end_date   |
      | /web_performance/analytics/visits/countries |             | 2015-09-01 | 2015-09-01 |
      | /web_performance/analytics/visits/countries | day         |            | 2015-09-01 |
      | /web_performance/analytics/visits/countries | day         | 2015-09-01 |            |
      | /web_performance/analytics/visits/countries | day         |            |            |
      | /web_performance/analytics/visits/countries |             |            |            |

  Scenario Outline: Checking default parameter values
    Empty column in examples section means default value will be used for this parameter.
    if text is empty, returns null
    if text is date in ISO format (2015-01-01), it returns this date
    text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which will add or substract
    particular number of days/weeks/months from first part of expression

    When Getting "<url>" data with "<granularity>" granularity for "property" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"

    #And Response contains 5 values #will be added to validate correct number of results according to dates and granularity
    Examples: 
      | url                               | granularity | start_date        | end_date       | expected_granularity | expected_since    | expected_until |
      | /web_performance/analytics/visits |             |                   |                | day                  | today             | today          |
      | /web_performance/analytics/visits |             |                   |                | day                  | today - 31 days   | today          |
      | /web_performance/analytics/visits |             |                   |                | day                  | today             | today          |
      | /web_performance/analytics/visits |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     |
      | /web_performance/analytics/visits | day         |                   | today          | day                  | today - 31 days   | today          |
      | /web_performance/analytics/visits | day         | today             |                | day                  | today             | today          |
      | /web_performance/analytics/visits | week        |                   | today          | week                 | today - 13 weeks  | today          |
      | /web_performance/analytics/visits | week        | today             |                | week                 | today             | today          |
      | /web_performance/analytics/visits | month       |                   | today          | month                | today - 6 months  | today          |
      | /web_performance/analytics/visits | month       | today             |                | month                | today             | today          |
      | /web_performance/analytics/visits | day         | today - 100 days  | today          | day                  | today - 90 days   | today          |
      | /web_performance/analytics/visits | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          |
      | /web_performance/analytics/visits | month       | today - 40 months | today          | month                | today - 36 months | today          |
      | /web_performance/analytics/visits | day         | today + 2 days    | today + 3 days | day                  | today             | today          |
