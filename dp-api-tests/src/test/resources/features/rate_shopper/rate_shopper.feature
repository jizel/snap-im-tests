Feature: rate_shopper

  Scenario: Get BAR values for a given property analytics data from API
    When Getting rate data with for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 3 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get BAR values for a given market analytics data from API
    When Getting "/rate-shopper/analytics/market" data with "" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 3 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario Outline: Getting a list of items
    When List of "<url>" is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are at most <count> items returned

    Examples: 
      | url                                       | limit | cursor | count |
      | /rate_shopper/analytics/market/properties |       |        | 50    |
      | /rate_shopper/analytics/market/properties | 51    |        | 50    |
      | /rate_shopper/analytics/market/properties |       | 1      | 50    |
      | /rate_shopper/analytics/market/properties | 20    | 0      | 20    |
      | /rate_shopper/analytics/market/properties | 49    | 0      | 49    |
      | /rate_shopper/analytics/market/properties | 5     | 5      | 5     |
      | /rate_shopper/analytics/market/           |       |        | 50    |
      | /rate_shopper/analytics/market/           | 51    |        | 50    |
      | /rate_shopper/analytics/market/           |       | 1      | 50    |
      | /rate_shopper/analytics/market/           | 20    | 0      | 20    |
      | /rate_shopper/analytics/market/           | 49    | 0      | 49    |
      | /rate_shopper/analytics/market/           | 5     | 5      | 5     |

  Scenario: Getting non-existent analytics data
    When Getting "/rate_shopper/analytics/not_present" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario: Getting mismatched analytics data
    When Getting "/rate_shopper/analytics/property/properties" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                       | error_code | custom_code |
      | /rate_shopper/analytics/property          | 404        | 151         |
      | /rate_shopper/analytics/market            | 400        | 52          |
      | /rate_shopper/analytics/market/properties | 400        | 52          |

  Scenario Outline: Checking error codes for getting list of referrals broken down by referring site
    When List of "<url>" is got with limit "<limit>" and cursor "" and filter empty and sort empty
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                       | limit | response_code | custom_code |
      | /rate_shopper/analytics/market            | -1    | 400           | 63          |
      | /rate_shopper/analytics/market/properties | text  | 400           | 63          |

  Scenario: Check min, average, and max market values
    When Getting "/rate-shopper/analytics/market" data with "" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then "minimal" value is not more than "average" value
    And "average" value is not more than "maxumal" value

  Scenario Outline: Get analytics data from API with missing parameters
    When Getting "<url>" data with "" granularity for "property" since "<start_date>" until "<end_date>"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                             | start_date | end_date   |
      | /rate_shopper/analytics/market/ |            | 2015-09-01 |
      | /rate_shopper/analytics/market/ | 2015-09-01 |            |
      | /rate_shopper/analytics/market/ |            |            |

  Scenario Outline: Checking default parameter values
    Empty column in examples section means default value will be used for this parameter.
    if text is empty, returns null
    if text is date in ISO format (2015-01-01), it returns this date
    text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which will add or substract
    particular number of days/weeks/months from first part of expression

    When Getting "<url>" data with "" granularity for "property" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"

    #And Response contains 5 values #will be added to validate correct number of results according to dates and granularity
    Examples: 
      | url                               | start_date       | end_date       | expected_since  | expected_until |
      | /web_performance/analytics/visits |                  |                | today - 31 days | today          |
      | /web_performance/analytics/visits |                  | today          | today - 31 days | today          |
      | /web_performance/analytics/visits | today            |                | today           | today          |
      | /web_performance/analytics/visits | today - 100 days | today          | today - 90 days | today          |
      | /web_performance/analytics/visits | today + 2 days   | today + 3 days | today           | today          |
