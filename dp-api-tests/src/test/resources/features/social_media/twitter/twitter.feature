Feature: instagram

  Scenario Outline: Instagram get collective analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains 3 values for all metrics

    Examples: 
      | url                                | granularity |
      | /social_media/analytics/instagram/ | day         |
      | /social_media/analytics/instagram/ | month       |
      | /social_media/analytics/instagram/ | year        |

  Scenario Outline: Instagram get specific analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                          | granularity |
      #  | /social_media/analytics/instagram            | day         |
      | /social_media/analytics/instagram/pictures   | day         |
      | /social_media/analytics/instagram/engagement | day         |
      | /social_media/analytics/instagram/followers  | day         |
      | /social_media/analytics/instagram/mentions   | day       |
      | /social_media/analytics/instagram/tags       | day         |
            #  | /social_media/analytics/instagram            | week       |
      | /social_media/analytics/instagram/pictures   | week        |
      | /social_media/analytics/instagram/engagement | week        |
      | /social_media/analytics/instagram/followers  | week        |
      | /social_media/analytics/instagram/mentions   | week        |
      | /social_media/analytics/instagram/tags       | week        |
      #  | /social_media/analytics/instagram            | month       |
      | /social_media/analytics/instagram/pictures   | month       |
      | /social_media/analytics/instagram/engagement | month       |
      | /social_media/analytics/instagram/followers  | month       |
      | /social_media/analytics/instagram/mentions   | month       |
      | /social_media/analytics/instagram/tags       | month       |
      #   | /social_media/analytics/instagram            | year        |
      | /social_media/analytics/instagram/pictures   | year        |
      | /social_media/analytics/instagram/engagement | year        |
      | /social_media/analytics/instagram/followers  | year        |
      | /social_media/analytics/instagram/mentions   | year        |
      | /social_media/analytics/instagram/tags       | year        |

  Scenario Outline: Instagram get specific analytics data from API for a given wrong granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2014-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "400"
    And Custom code is "63"

    Examples: 
      | url                                          | granularity |
      #  | /social_media/analytics/instagram            | day         |
      | /social_media/analytics/instagram/pictures   | day         |
      | /social_media/analytics/instagram/engagement | day         |
      | /social_media/analytics/instagram/followers  | day         |
      | /social_media/analytics/instagram/mentions   | day       |
      | /social_media/analytics/instagram/tags       | day         |
      #  | /social_media/analytics/instagram            | week       |
      | /social_media/analytics/instagram/pictures   | week        |
      | /social_media/analytics/instagram/engagement | week        |
      | /social_media/analytics/instagram/followers  | week        |
      | /social_media/analytics/instagram/mentions   | week        |
      | /social_media/analytics/instagram/tags       | week        |
      #  | /social_media/analytics/instagram            | month       |
      | /social_media/analytics/instagram/pictures   | month       |
      | /social_media/analytics/instagram/engagement | month       |
      | /social_media/analytics/instagram/followers  | month       |
      | /social_media/analytics/instagram/mentions   | month       |
      | /social_media/analytics/instagram/tags       | month       |
      #   | /social_media/analytics/instagram            | year        |
      | /social_media/analytics/instagram/pictures   | year        |
      | /social_media/analytics/instagram/engagement | year        |
      | /social_media/analytics/instagram/followers  | year        |
      | /social_media/analytics/instagram/mentions   | year        |
      | /social_media/analytics/instagram/tags       | year        |

  Scenario Outline: Instagram checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                          | error_code | custom_code |
      | /social_media/analytics/instagram            | 400        | 52          |
      | /social_media/analytics/instagram/pictures   | 400        | 52          |
      | /social_media/analytics/instagram/engagement | 400        | 52          |
      | /social_media/analytics/instagram/followers  | 400        | 52          |
      | /social_media/analytics/instagram/mentions   | 400        | 52          |
      | /social_media/analytics/instagram/tags       | 400        | 52          |

  Scenario Outline: Instagram getting a list of items
    When List of "<url>" is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are at most <count> items returned

    Examples: 
      | url                                          | limit | cursor | count |
      | /social_media/analytics/instagram/followers  |       |        | 50    |
      | /social_media/analytics/instagram/followers  | 15    |        | 15    |
      | /social_media/analytics/instagram/followers  |       | 1      | 50    |
      | /social_media/analytics/instagram/followers  | 20    | 0      | 20    |
      | /social_media/analytics/instagram/followers  | 49    | 0      | 49    |
      | /social_media/analytics/instagram/followers  | 5     | 5      | 5     |
      | /social_media/analytics/instagram/followers  |       |        | 50    |
      | /social_media/analytics/instagram/followers  | 51    |        | 50    |
      | /social_media/analytics/instagram/followers  |       | 1      | 50    |
      | /social_media/analytics/instagram/followers  | 20    | 0      | 20    |
      | /social_media/analytics/instagram/followers  | 60    | 0      | 50    |
      | /social_media/analytics/instagram/followers  | 5     | 5      | 5     |
      | /social_media/analytics/instagram/pictures   |       |        | 50    |
      | /social_media/analytics/instagram/pictures   | 15    |        | 15    |
      | /social_media/analytics/instagram/pictures   |       | 1      | 50    |
      | /social_media/analytics/instagram/pictures   | 20    | 0      | 20    |
      | /social_media/analytics/instagram/pictures   | 49    | 0      | 49    |
      | /social_media/analytics/instagram/pictures   | 5     | 5      | 5     |
      | /social_media/analytics/instagram/pictures   |       |        | 50    |
      | /social_media/analytics/instagram/pictures   | 51    |        | 50    |
      | /social_media/analytics/instagram/pictures   |       | 1      | 50    |
      | /social_media/analytics/instagram/pictures   | 20    | 0      | 20    |
      | /social_media/analytics/instagram/pictures   | 60    | 0      | 50    |
      | /social_media/analytics/instagram/pictures   | 5     | 5      | 5     |
      | /social_media/analytics/instagram/engagement |       |        | 50    |
      | /social_media/analytics/instagram/engagement | 15    |        | 15    |
      | /social_media/analytics/instagram/engagement |       | 1      | 50    |
      | /social_media/analytics/instagram/engagement | 20    | 0      | 20    |
      | /social_media/analytics/instagram/engagement | 49    | 0      | 49    |
      | /social_media/analytics/instagram/engagement | 5     | 5      | 5     |
      | /social_media/analytics/instagram/engagement |       |        | 50    |
      | /social_media/analytics/instagram/engagement | 51    |        | 50    |
      | /social_media/analytics/instagram/engagement |       | 1      | 50    |
      | /social_media/analytics/instagram/engagement | 20    | 0      | 20    |
      | /social_media/analytics/instagram/engagement | 60    | 0      | 50    |
      | /social_media/analytics/instagram/engagement | 5     | 5      | 5     |
      | /social_media/analytics/instagram/mentions   |       |        | 50    |
      | /social_media/analytics/instagram/mentions   | 15    |        | 15    |
      | /social_media/analytics/instagram/mentions   |       | 1      | 50    |
      | /social_media/analytics/instagram/mentions   | 20    | 0      | 20    |
      | /social_media/analytics/instagram/mentions   | 49    | 0      | 49    |
      | /social_media/analytics/instagram/mentions   | 5     | 5      | 5     |
      | /social_media/analytics/instagram/mentions   |       |        | 50    |
      | /social_media/analytics/instagram/mentions   | 51    |        | 50    |
      | /social_media/analytics/instagram/mentions   |       | 1      | 50    |
      | /social_media/analytics/instagram/mentions   | 20    | 0      | 20    |
      | /social_media/analytics/instagram/mentions   | 60    | 0      | 50    |
      | /social_media/analytics/instagram/mentions   | 5     | 5      | 5     |
      | /social_media/analytics/instagram/followers  |       |        | 50    |
      | /social_media/analytics/instagram/tags       | 15    |        | 15    |
      | /social_media/analytics/instagram/tags       |       | 1      | 50    |
      | /social_media/analytics/instagram/tags       | 20    | 0      | 20    |
      | /social_media/analytics/instagram/tags       | 49    | 0      | 49    |
      | /social_media/analytics/instagram/tags       | 5     | 5      | 5     |
      | /social_media/analytics/instagram/tags       |       |        | 50    |
      | /social_media/analytics/instagram/tags       | 51    |        | 50    |
      | /social_media/analytics/instagram/tags       |       | 1      | 50    |
      | /social_media/analytics/instagram/tags       | 20    | 0      | 20    |
      | /social_media/analytics/instagram/tags       | 60    | 0      | 50    |
      | /social_media/analytics/instagram/tags       | 5     | 5      | 5     |

  Scenario: Get Instagram tags analytics data from API for an invalid granularity
    When Getting "/social_media/analytics/instagram/tags" data with "invalid" granularity for "property" since "2015-09-01" until "2015-08-01"
    Then Content type is "application/json"
    And Response code is "400"
    #And Response code is "200" - returned
    And Custom code is "63"
    #null is returned when code 200 is set
    
  Scenario Outline: Instagram checking error codes for getting list of items
    When List of "<url>" is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                         | limit | cursor | response_code | custom_code |
      | /social_media/analytics/instagram/followers |       | -1     | 400           | 63          |
      | /social_media/analytics/instagram/followers |       | text   | 400           | 63          |
      | /social_media/analytics/instagram/followers | -1    |        | 400           | 63          |
      | /social_media/analytics/instagram/followers | text  |        | 400           | 63          |
      | /social_media/analytics/instagram/followers | 10    | -1     | 400           | 63          |
      | /social_media/analytics/instagram/followers | text  | 0      | 400           | 63          |
      | /social_media/analytics/instagram/followers | 10    | text   | 400           | 63          |

  Scenario Outline: Instagram get analytics data from API with missing parameters
    When Getting "<url>" data with "<granularity>" granularity for "property" since "<start_date>" until "<end_date>"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                          | granularity | start_date | end_date   |
      | /social_media/analytics/instagram/followers  |             | 2015-09-01 | 2015-09-01 |
      # | /social_media/analytics/instagram/followers  | day         |            | 2015-09-02 |
      # | /social_media/analytics/instagram/followers  | day         | 2015-09-03 |            |
      # | /social_media/analytics/instagram/followers  | day         |            |            |
      # | /social_media/analytics/instagram/followers  |             |            |            |
      | /social_media/analytics/instagram/pictures   |             | 2015-09-01 | 2015-09-01 |
      # | /social_media/analytics/instagram/pictures   | day         |            | 2015-09-02 |
      #| /social_media/analytics/instagram/pictures   | day         | 2015-09-03 |            |
      | /social_media/analytics/instagram/pictures   | day         |            |            |
      | /social_media/analytics/instagram/pictures   |             |            |            |
      | /social_media/analytics/instagram/engagement |             | 2015-09-01 | 2015-09-01 |
      # | /social_media/analytics/instagram/engagement | day         |            | 2015-09-02 |
      #| /social_media/analytics/instagram/engagement | day         | 2015-09-03 |            |
      | /social_media/analytics/instagram/engagement | day         |            |            |
      | /social_media/analytics/instagram/engagement |             |            |            |
      | /social_media/analytics/instagram/mentions   |             | 2015-09-01 | 2015-09-01 |
      #| /social_media/analytics/instagram/mentions   | day         |            | 2015-09-02 |
      #| /social_media/analytics/instagram/mentions   | day         | 2015-09-03 |            |
      | /social_media/analytics/instagram/mentions   | day         |            |            |
      | /social_media/analytics/instagram/mentions   |             |            |            |
      | /social_media/analytics/instagram/followers  |             | 2015-09-01 | 2015-09-01 |
      # | /social_media/analytics/instagram/tags       | day         |            | 2015-09-02 |
      # | /social_media/analytics/instagram/tags       | day         | 2015-09-03 |            |
      | /social_media/analytics/instagram/tags       | day         |            |            |

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
    And Response contains no more than <count> values

    Examples: 
      | url                                         | granularity | start_date | end_date | expected_granularity | expected_since  | expected_until | count |
      | /social_media/analytics/instagram/followers |             |            |          | day                  | today - 1 month | today          | 32    |

  #   | /social_media/analytics/instagram/followers |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     | 1     |
  #   | /social_media/analytics/instagram/followers | day         |                   | today          | day                  | today - 1 month   | today          | 32    |
  #   | /social_media/analytics/instagram/followers | day         | today             |                | day                  | today             | today          | 1     |
  #   | /social_media/analytics/instagram/followers | week        |                   | today          | week                 | today - 13 weeks  | today          | 13    |
  #   | /social_media/analytics/instagram/followers | week        | today             |                | week                 | today             | today          | 0     |
  #   | /social_media/analytics/instagram/followers | month       |                   | today          | month                | today - 6 months  | today          | 6     |
  #   | /social_media/analytics/instagram/followers | month       | today             |                | month                | today             | today          | 0     |
  #   | /social_media/analytics/instagram/followers | day         | today - 100 days  | today          | day                  | today - 90 days   | today          | 91    |
  #   | /social_media/analytics/instagram/followers | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 26    |
  #   | /social_media/analytics/instagram/followers | month       | today - 40 months | today          | month                | today - 36 months | today          | 36    |
  # | /social_media/analytics/instagram/followers | day         | today + 2 days    | today + 3 days | day                  | today             | today          | 1     |
  
  Scenario Outline: Checking number of values in response for various granularities
    When Getting "<url>" data with "<granularity>" granularity for "property" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains no more than <count> values

    Examples: 
      | url                                          | granularity | since           | <until> | count |
      #  | /social_media/analytics/instagram            | day         | today           | today   | 1     |
      | /social_media/analytics/instagram/pictures   | day         | today - 1 day   | today   | 5     |
      | /social_media/analytics/instagram/engagement | day         | today - 6 days  | today   | 7     |
      | /social_media/analytics/instagram/followers  | day         | today - 7 days  | today   | 8     |
      | /social_media/analytics/instagram/mentions   | day         | today - 8 days  | today   | 9     |
      | /social_media/analytics/instagram/tags       | day         | today - 29 days | today   | 30    |
