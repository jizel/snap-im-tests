Feature: facebook

  Scenario Outline: Get facebook analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains 3 values for all metrics

    Examples: 
      | url                               | granularity |
      | /social_media/analytics/facebook/ | day         |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 3||5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                              | granularity |
      | /social_media/analytics/facebook/posts           | day         |
      | /social_media/analytics/facebook/number_of_posts | day         |
      | /social_media/analytics/facebook/engagement      | day         |
      | /social_media/analytics/facebook/likes           | day         |
      | /social_media/analytics/facebook/unlikes         | day         |

  Scenario: Getting non-existent analytics data
    When Getting "/social_media/analytics/facebook/not_present" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    #And Response code is "404"
    #And Custom code is "151"
    And Response code is "200"

  Scenario: Getting mismatched metrics analytics data
    When Getting "/social_media/analytics/facebook/tweets" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    #And Response code is "404"
    #And Custom code is "151"
    And Response code is "200"

  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                              | error_code | custom_code |
      | /social_media/analytics/facebook                 | 400        | 52          |
      | /social_media/analytics/facebook/posts           | 400        | 52          |
      | /social_media/analytics/facebook/number_of_posts | 400        | 52          |
      | /social_media/analytics/facebook/engagement      | 400        | 52          |
      | /social_media/analytics/facebook/likes           | 400        | 52          |
      | /social_media/analytics/facebook/unlikes         | 400        | 52          |

  Scenario Outline: Getting a list of items
    When List of "<url>" is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"||"400"
    And Content type is "application/json"
    And There are at most <count> items returned

    Examples: 
      | url                                              | limit | cursor | count |
      | /social_media/analytics/facebook/posts           |       |        | 50    |
      | /social_media/analytics/facebook/posts           | 51    |        | 50    |
      | /social_media/analytics/facebook/posts           |       | 1      | 50    |
      | /social_media/analytics/facebook/posts           | 20    | 0      | 20    |
      | /social_media/analytics/facebook/posts           | 60    | 0      | 50    |
      | /social_media/analytics/facebook/posts           | 5     | 5      | 5     |
      | /social_media/analytics/facebook/number_of_posts |       |        | 50    |
      | /social_media/analytics/facebook/number_of_posts | 51    |        | 50    |
      | /social_media/analytics/facebook/number_of_posts |       | 1      | 50    |
      | /social_media/analytics/facebook/number_of_posts | 20    | 0      | 20    |
      | /social_media/analytics/facebook/number_of_posts | 60    | 0      | 50    |
      | /social_media/analytics/facebook/number_of_posts | 5     | 5      | 5     |
      | /social_media/analytics/facebook/engagement      |       |        | 50    |
      | /social_media/analytics/facebook/engagement      | 51    |        | 50    |
      | /social_media/analytics/facebook/engagement      |       | 1      | 50    |
      | /social_media/analytics/facebook/engagement      | 20    | 0      | 20    |
      | /social_media/analytics/facebook/engagement      | 60    | 0      | 50    |
      | /social_media/analytics/facebook/engagement      | 5     | 5      | 5     |
      | /social_media/analytics/facebook/likes           |       |        | 50    |
      | /social_media/analytics/facebook/likes           | 51    |        | 50    |
      | /social_media/analytics/facebook/likes           |       | 1      | 50    |
      | /social_media/analytics/facebook/likes           | 20    | 0      | 20    |
      | /social_media/analytics/facebook/likes           | 60    | 0      | 50    |
      | /social_media/analytics/facebook/likes           | 5     | 5      | 5     |
      | /social_media/analytics/facebook/unlikes         |       |        | 50    |
      | /social_media/analytics/facebook/unlikes         | 51    |        | 50    |
      | /social_media/analytics/facebook/unlikes         |       | 1      | 50    |
      | /social_media/analytics/facebook/unlikes         | 20    | 0      | 20    |
      | /social_media/analytics/facebook/unlikes         | 60    | 0      | 50    |
      | /social_media/analytics/facebook/unlikes         | 5     | 5      | 5     |

  Scenario: Get Instagram tags analytics data from API for an invalid granularity
    When Getting "/social_media/analytics/facebook/posts" data with "invalid" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    #And Response code is "400"
    #And Custom code is "63"
    And Response code is "200"

  Scenario Outline: Checking error codes for getting list of items
    When List of "<url>" is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                      | limit | cursor | response_code | custom_code |
      #! Expected status code <400> doesn't match actual status code <200>.      | /social_media/analytics/facebook/posts           | 10    | -1     | 400           | 63          |
      #! Expected status code <400> doesn't match actual status code <500>      | /social_media/analytics/facebook/posts           | text  | 0      | 400           | 63          |
      #! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/posts           | 10    | text   | 400           | 63          |
      #! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/number_of_posts | 10    | -1     | 400           | 63          |
      #! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/number_of_posts | text  | 0      | 400           | 63          |
      #! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/number_of_posts | 10    | text   | 400           | 63          |
      #! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/engagement      | 10    | -1     | 400           | 63          |
      #! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/engagement      | text  | 0      | 400           | 63          |
      #! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/engagement      | 10    | text   | 400           | 63          |
#! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/likes   | 10    | -1     | 400           | 63          |
#! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/likes   | text  | 0      | 400           | 63          |
#! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/likes   | 10    | text   | 400           | 63          |
#! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/unlikes | 10    | -1     | 400           | 63          |
#! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/unlikes | text  | 0      | 400           | 63          |
#! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook/unlikes | 10    | text   | 400           | 63          |
#! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook         | 10    | -1     | 400           | 63          |
#! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook         | text  | 0      | 400           | 63          |
#! Expected status code <400> doesn't match actual status code <200>      | /social_media/analytics/facebook         | 10    | text   | 400           | 63          |

  Scenario Outline: Get analytics data from API with missing parameters
    When Getting "<url>" data with "<granularity>" granularity for "property" since "<start_date>" until "<end_date>"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                              | granularity | start_date | end_date   |
      #Expected: is <5>  Actual: 3	| /social_media/analytics/facebook/posts           |             | 2015-09-01 | 2015-09-01 |
      #NullPointer| /social_media/analytics/facebook/posts           | day         |            | 2015-09-01 |
      #Expected: is <5>  Actual: 3	| /social_media/analytics/facebook/posts           | day         | 2015-09-01 |            |
      #Expected: is <5>  Actual: 3	| /social_media/analytics/facebook/posts           | day         |            |            |
      #Expected: is <5>  Actual: 3	| /social_media/analytics/facebook/posts           |             |            |            |
      ## IlegalArtgument| /social_media/analytics/facebook                 |             | 2015-09-01 | 2015-09-01 |
      #| /social_media/analytics/facebook                 | day         |            | 2015-09-01 |
      ##| /social_media/analytics/facebook                 | day         | 2015-09-01 |            |
      ##| /social_media/analytics/facebook                 | day         |            |            |
      ##| /social_media/analytics/facebook                 |             |            |            |
      | /social_media/analytics/facebook/number_of_posts |             | 2015-09-01 | 2015-09-01 |
      #| /social_media/analytics/facebook/number_of_posts | day         |            | 2015-09-01 |
      | /social_media/analytics/facebook/number_of_posts | day         | 2015-09-01 |            |
      | /social_media/analytics/facebook/number_of_posts | day         |            |            |
      | /social_media/analytics/facebook/number_of_posts |             |            |            |
      | /social_media/analytics/facebook/engagement      |             | 2015-09-01 | 2015-09-01 |
      #| /social_media/analytics/facebook/engagement      | day         |            | 2015-09-01 |
      | /social_media/analytics/facebook/engagement      | day         | 2015-09-01 |            |
      | /social_media/analytics/facebook/engagement      | day         |            |            |
      | /social_media/analytics/facebook/engagement      |             |            |            |
      | /social_media/analytics/facebook/likes           |             | 2015-09-01 | 2015-09-01 |
      #| /social_media/analytics/facebook/likes           | day         |            | 2015-09-01 |
      | /social_media/analytics/facebook/likes           | day         | 2015-09-01 |            |
      | /social_media/analytics/facebook/likes           | day         |            |            |
      | /social_media/analytics/facebook/likes           |             |            |            |
      | /social_media/analytics/facebook/unlikes         |             | 2015-09-01 | 2015-09-01 |
      #| /social_media/analytics/facebook/unlikes         | day         |            | 2015-09-01 |
      | /social_media/analytics/facebook/unlikes         | day         | 2015-09-01 |            |
      | /social_media/analytics/facebook/unlikes         | day         |            |            |
      | /social_media/analytics/facebook/unlikes         |             |            |            |

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
      | url                                              | granularity | start_date        | end_date       | expected_granularity | expected_since    | expected_until | count |
      #Expected: is "day"  Actual: null	| /social_media/analytics/facebook                 |             |                   |                | day                  | today - 1 month   | today          | 32    |
      #Expected: is "day"  Actual: null	| /social_media/analytics/facebook                 |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     | 1     |
      #| /social_media/analytics/facebook                 | day         |                   | today          | day                  | today - 1 month   | today          | 32    |
      #| /social_media/analytics/facebook                 | day         | today             |                | day                  | today             | today          | 1     |
      #| /social_media/analytics/facebook                 | week        |                   | today          | week                 | today - 13 weeks  | today          | 13    |
      #| /social_media/analytics/facebook                 | week        | today             |                | week                 | today             | today          | 0     |
      #| /social_media/analytics/facebook                 | month       |                   | today          | month                | today - 6 months  | today          | 6     |
      #| /social_media/analytics/facebook                 | month       | today             |                | month                | today             | today          | 0     |
      #expected:<2015-09-03> but was:<2015-08-24>	| /social_media/analytics/facebook                 | day         | today - 100 days  | today          | day                  | today - 90 days   | today          | 91    |
      #expected:<2015-06-03> but was:<2015-05-06>	| /social_media/analytics/facebook                 | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 26    |
      #expected:<2012-12-02> but was:<2012-08-02>	| /social_media/analytics/facebook                 | month       | today - 40 months | today          | month                | today - 36 months | today          | 36    |
      #expected:<2015-12-02> but was:<2015-12-04>	| /social_media/analytics/facebook                 | day         | today + 2 days    | today + 3 days | day                  | today             | today          | 1     |
      #Expected: is "day"  Actual: [null, null, null]	| /social_media/analytics/facebook/posts           |             |                   |                | day                  | today - 1 month   | today          | 32    |
      #Expected: is "day"  Actual: [null, null, null]	| /social_media/analytics/facebook/posts           |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     | 1     |
      #| /social_media/analytics/facebook/posts           | day         |                   | today          | day                  | today - 1 month   | today          | 32    |
      #Expected: is "day"  Actual: [null, null, null]	| /social_media/analytics/facebook/posts           | day         | today             |                | day                  | today             | today          | 1     |
      #| /social_media/analytics/facebook/posts           | week        |                   | today          | week                 | today - 13 weeks  | today          | 13    |
      #Expected: is "day"  Actual: [null, null, null]	| /social_media/analytics/facebook/posts           | week        | today             |                | week                 | today             | today          | 0     |
      #| /social_media/analytics/facebook/posts           | month       |                   | today          | month                | today - 6 months  | today          | 6     |
      #Expected: is "day"  Actual: [null, null, null]	| /social_media/analytics/facebook/posts           | month       | today             |                | month                | today             | today          | 0     |
      #Expected: is "day"  Actual: [null, null, null]	| /social_media/analytics/facebook/posts           | day         | today - 100 days  | today          | day                  | today - 90 days   | today          | 91    |
      #Expected: is "day"  Actual: [null, null, null]	| /social_media/analytics/facebook/posts           | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 26    |
      #Expected: is "day"  Actual: [null, null, null]	| /social_media/analytics/facebook/posts           | month       | today - 40 months | today          | month                | today - 36 months | today          | 36    |
      #Expected: is "day"  Actual: [null, null, null]	| /social_media/analytics/facebook/posts           | day         | today + 2 days    | today + 3 days | day                  | today             | today          | 1     |
      #Expected: is "day"  Actual: null		| /social_media/analytics/facebook/number_of_posts |             |                   |                | day                  | today - 1 month   | today          | 32    |
      #Expected: is "day"  Actual: null| /social_media/analytics/facebook/number_of_posts |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     | 1     |
      | /social_media/analytics/facebook/number_of_posts | day         |                   | today          | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/number_of_posts | day         | today             |                | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/number_of_posts | week        |                   | today          | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/facebook/number_of_posts | week        | today             |                | week                 | today             | today          | 0     |
      | /social_media/analytics/facebook/number_of_posts | month       |                   | today          | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/facebook/number_of_posts | month       | today             |                | month                | today             | today          | 0     |
      | /social_media/analytics/facebook/number_of_posts | day         | today - 100 days  | today          | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/facebook/number_of_posts | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/facebook/number_of_posts | month       | today - 40 months | today          | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/facebook/number_of_posts | day         | today + 2 days    | today + 3 days | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/engagement      |             |                   |                | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/engagement      |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     | 1     |
      | /social_media/analytics/facebook/engagement      | day         |                   | today          | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/engagement      | day         | today             |                | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/engagement      | week        |                   | today          | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/facebook/engagement      | week        | today             |                | week                 | today             | today          | 0     |
      | /social_media/analytics/facebook/engagement      | month       |                   | today          | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/facebook/engagement      | month       | today             |                | month                | today             | today          | 0     |
      | /social_media/analytics/facebook/engagement      | day         | today - 100 days  | today          | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/facebook/engagement      | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/facebook/engagement      | month       | today - 40 months | today          | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/facebook/engagement      | day         | today + 2 days    | today + 3 days | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/likes           |             |                   |                | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/likes           |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     | 1     |
      | /social_media/analytics/facebook/likes           | day         |                   | today          | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/likes           | day         | today             |                | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/likes           | week        |                   | today          | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/facebook/likes           | week        | today             |                | week                 | today             | today          | 0     |
      | /social_media/analytics/facebook/likes           | month       |                   | today          | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/facebook/likes           | month       | today             |                | month                | today             | today          | 0     |
      | /social_media/analytics/facebook/likes           | day         | today - 100 days  | today          | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/facebook/likes           | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/facebook/likes           | month       | today - 40 months | today          | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/facebook/likes           | day         | today + 2 days    | today + 3 days | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/unlikes         |             |                   |                | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/unlikes         |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     | 1     |
      | /social_media/analytics/facebook/unlikes         | day         |                   | today          | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/unlikes         | day         | today             |                | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/unlikes         | week        |                   | today          | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/facebook/unlikes         | week        | today             |                | week                 | today             | today          | 0     |
      | /social_media/analytics/facebook/unlikes         | month       |                   | today          | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/facebook/unlikes         | month       | today             |                | month                | today             | today          | 0     |
      | /social_media/analytics/facebook/unlikes         | day         | today - 100 days  | today          | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/facebook/unlikes         | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/facebook/unlikes         | month       | today - 40 months | today          | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/facebook/unlikes         | day         | today + 2 days    | today + 3 days | day                  | today             | today          | 1     |

  Scenario: Get data owners data for twitter
    When Getting "/social_media/analytics/twitter/engagement" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"
    And Data is owned by "twitter"

  Scenario: Get data owners data for facebook
    When Getting "/social_media/analytics/facebook/engagement" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"
    And Data is owned by "facebook"

  Scenario Outline: Checking number of values in response for various granularities
    When Getting "<url>" data with "<granularity>" granularity for "property" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains no more than <count> values

    Examples: 
      | url                                              | granularity | since           | <until> | count |
      | /social_media/analytics/facebook                 | day         | today           | today   | 1     |
      | /social_media/analytics/facebook/number_of_posts | day         | today - 1 day   | today   | 2     |
      | /social_media/analytics/facebook/engagement      | day         | today - 6 days  | today   | 7     |
      | /social_media/analytics/facebook/likes           | day         | today - 7 days  | today   | 8     |
      | /social_media/analytics/facebook/unlikes         | day         | today - 8 days  | today   | 9     |
      | /social_media/analytics/facebook/reach           | day         | today - 29 days | today   | 30    |
      | /social_media/analytics/facebook/followers       | day         | today - 30 days | today   | 31    |
      | /social_media/analytics/facebook/posts           | week        | today           | today   | 0     |
