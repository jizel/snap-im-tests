Feature: analytics

  Scenario Outline: Get collective analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains 5 values for all metrics

    Examples: 
      | url                                | granularity |
      | /social_media/analytics/           | day         |
      | /social_media/analytics/facebook/  | day         |
      | /social_media/analytics/twitter/   | week        |
      | /social_media/analytics/instagram/ | month       |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                              | granularity |
      | /social_media/analytics/reach                    | day         |
      | /social_media/analytics/engagement               | day         |
      | /social_media/analytics/followers                | day         |
      | /social_media/analytics/facebook/posts           | day         |
      | /social_media/analytics/facebook/number_of_posts | day         |
      | /social_media/analytics/facebook/engagement      | day         |
      | /social_media/analytics/facebook/likes           | day         |
      | /social_media/analytics/facebook/unlikes         | day         |
      | /social_media/analytics/twitter/tweets           | week        |
      | /social_media/analytics/twitter/engagement       | week        |
      | /social_media/analytics/twitter/followers        | week        |
      | /social_media/analytics/twitter/impressions      | week        |
      | /social_media/analytics/twitter/retweets         | week        |
      | /social_media/analytics/twitter/retweets_reach   | week        |
      | /social_media/analytics/twitter/mentions         | month       |
      | /social_media/analytics/twitter/mentions_reach   | month       |
      | /social_media/analytics/instagram/pictures       | month       |
      | /social_media/analytics/instagram/engagement     | month       |
      | /social_media/analytics/instagram/followers      | month       |
      | /social_media/analytics/instagram/mentions       | month       |
      | /social_media/analytics/instagram/tags           | month       |

  Scenario: Getting non-existent analytics data
    When Getting "/social_media/analytics/not_present" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario: Getting mismatched metrics analytics data
    When Getting "/social_media/analytics/facebook/tweets" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                              | error_code | custom_code |
      | /social_media/analytics                          | 400        | 52          |
      | /social_media/analytics/reach                    | 400        | 52          |
      | /social_media/analytics/engagement               | 400        | 52          |
      | /social_media/analytics/followers                | 400        | 52          |
      | /social_media/analytics/facebook                 | 400        | 52          |
      | /social_media/analytics/facebook/posts           | 400        | 52          |
      | /social_media/analytics/facebook/number_of_posts | 400        | 52          |
      | /social_media/analytics/facebook/engagement      | 400        | 52          |
      | /social_media/analytics/facebook/likes           | 400        | 52          |
      | /social_media/analytics/facebook/unlikes         | 400        | 52          |
      | /social_media/analytics/twitter                  | 400        | 52          |
      | /social_media/analytics/twitter/tweets           | 400        | 52          |
      | /social_media/analytics/twitter/engagement       | 400        | 52          |
      | /social_media/analytics/twitter/followers        | 400        | 52          |
      | /social_media/analytics/twitter/impressions      | 400        | 52          |
      | /social_media/analytics/twitter/retweets         | 400        | 52          |
      | /social_media/analytics/twitter/retweets_reach   | 400        | 52          |
      | /social_media/analytics/twitter/mentions         | 400        | 52          |
      | /social_media/analytics/twitter/mentions_reach   | 400        | 52          |
      | /social_media/analytics/instagram                | 400        | 52          |
      | /social_media/analytics/instagram/pictures       | 400        | 52          |
      | /social_media/analytics/instagram/engagement     | 400        | 52          |
      | /social_media/analytics/instagram/followers      | 400        | 52          |
      | /social_media/analytics/instagram/mentions       | 400        | 52          |
      | /social_media/analytics/instagram/tags           | 400        | 52          |

  Scenario Outline: Getting a list of items
    When List of "<url>" is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are at most <count> items returned

    Examples: 
      | url                                    | limit | cursor | count |
      | /social_media/analytics/twitter/tweets |       |        | 50    |
      | /social_media/analytics/twitter/tweets | 15    |        | 15    |
      | /social_media/analytics/twitter/tweets |       | 1      | 50    |
      | /social_media/analytics/twitter/tweets | 20    | 0      | 20    |
      | /social_media/analytics/twitter/tweets | 10    | 0      | 10    |
      | /social_media/analytics/twitter/tweets | 5     | 5      | 5     |
      | /social_media/analytics/facebook/posts |       |        | 50    |
      | /social_media/analytics/facebook/posts | 15    |        | 15    |
      | /social_media/analytics/facebook/posts |       | 1      | 50    |
      | /social_media/analytics/facebook/posts | 20    | 0      | 20    |
      | /social_media/analytics/facebook/posts | 10    | 0      | 10    |
      | /social_media/analytics/facebook/posts | 5     | 5      | 5     |

  Scenario: Get Instagram tags analytics data from API for an invalid granularity
    When Getting "/social_media/analytics/instagram/tags" data with "invalid" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "63"

  Scenario Outline: Checking error codes for getting list of items
    When List of "<url>" is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                    | limit | cursor | response_code | custom_code |
      | /social_media/analytics/twitter/tweets |       | -1     | 400           | 63          |
      | /social_media/analytics/twitter/tweets |       | text   | 400           | 63          |
      | /social_media/analytics/twitter/tweets | -1    |        | 400           | 63          |
      | /social_media/analytics/twitter/tweets | text  |        | 400           | 63          |
      | /social_media/analytics/facebook/posts | 10    | -1     | 400           | 63          |
      | /social_media/analytics/facebook/posts | text  | 0      | 400           | 63          |
      | /social_media/analytics/facebook/posts | 10    | text   | 400           | 63          |

  Scenario Outline: Get analytics data from API with missing parameters
    When Getting "<url>" data with "<granularity>" granularity for "property" since "<start_date>" until "<end_date>"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                           | granularity | start_date | end_date   |
      | /social_media/analytics       |             | 2015-09-01 | 2015-09-01 |
      | /social_media/analytics/reach | day         |            | 2015-09-01 |
      | /social_media/analytics/reach | day         | 2015-09-01 |            |
      | /social_media/analytics/reach | day         |            |            |
      | /social_media/analytics/reach |             |            |            |

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
      | url                           | granularity | start_date        | end_date       | expected_granularity | expected_since    | expected_until |
      | /social_media/analytics/reach |             |                   |                | day                  | today             | today          |
      | /social_media/analytics/reach |             |                   |                | day                  | today - 31 days   | today          |
      | /social_media/analytics/reach |             |                   |                | day                  | today             | today          |
      | /social_media/analytics/reach |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     |
      | /social_media/analytics/reach | day         |                   | today          | day                  | today - 31 days   | today          |
      | /social_media/analytics/reach | day         | today             |                | day                  | today             | today          |
      | /social_media/analytics/reach | week        |                   | today          | week                 | today - 13 weeks  | today          |
      | /social_media/analytics/reach | week        | today             |                | week                 | today             | today          |
      | /social_media/analytics/reach | month       |                   | today          | month                | today - 6 months  | today          |
      | /social_media/analytics/reach | month       | today             |                | month                | today             | today          |
      | /social_media/analytics/reach | day         | today - 100 days  | today          | day                  | today - 90 days   | today          |
      | /social_media/analytics/reach | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          |
      | /social_media/analytics/reach | month       | today - 40 months | today          | month                | today - 36 months | today          |
      | /social_media/analytics/reach | day         | today + 2 days    | today + 3 days | day                  | today             | today          |
