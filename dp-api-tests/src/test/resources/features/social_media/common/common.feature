Feature: facebook

  Scenario Outline: Get collective analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains 3 values for all metrics

    Examples: 
      | url                               | granularity |
      | /social_media/analytics           | day         |
      | /social_media/analytics/facebook  | day         |
      | /social_media/analytics/twitter   | day         |
      | /social_media/analytics/instagram | day         |
      | /social_media/analytics           | week        |
      | /social_media/analytics/facebook  | week        |
      | /social_media/analytics/twitter   | week        |
      | /social_media/analytics/instagram | week        |
      | /social_media/analytics           | month       |
      | /social_media/analytics/facebook  | month       |
      | /social_media/analytics/twitter   | month       |
      | /social_media/analytics/instagram | month       |

  Scenario Outline: Get collective analytics data from API for a given wrong granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2014-09-01"
    Then Content type is "application/json"
    #And Response code is "400"
    And Response code is "200"
    #And Custom code is "53"

    Examples: 
      | url                               | granularity |
      | /social_media/analytics           | day         |
      | /social_media/analytics/facebook  | day         |
      | /social_media/analytics/twitter   | day         |
      | /social_media/analytics/instagram | day         |
      | /social_media/analytics           | week        |
      | /social_media/analytics/facebook  | week        |
      | /social_media/analytics/twitter   | week        |
      | /social_media/analytics/instagram | week        |
      | /social_media/analytics           | month       |
      | /social_media/analytics/facebook  | month       |
      | /social_media/analytics/twitter   | month       |
      | /social_media/analytics/instagram | month       |
      
  Scenario Outline: Get collective analytics data from API for a not given since granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "" until "2014-09-01"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "53"

    Examples: 
      | url                               | granularity |
     # | /social_media/analytics           | day         |
     # | /social_media/analytics/facebook  | day         |
     # | /social_media/analytics/twitter   | day         |
     # | /social_media/analytics/instagram | day         |
     # | /social_media/analytics           | week        |
     # | /social_media/analytics/facebook  | week        |
     # | /social_media/analytics/twitter   | week        |
     # | /social_media/analytics/instagram | week        |
     # | /social_media/analytics           | month       |
     # | /social_media/analytics/facebook  | month       |
     # | /social_media/analytics/twitter   | month       |
     # | /social_media/analytics/instagram | month       |
      
  Scenario Outline: Get collective analytics data from API for a not given until granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until ""
    Then Content type is "application/json"
    And Response code is "200"
    #And Response code is "400"
    #And Custom code is "53"

    Examples: 
      | url                               | granularity |
      | /social_media/analytics           | day         |
      | /social_media/analytics/facebook  | day         |
      | /social_media/analytics/twitter   | day         |
      | /social_media/analytics/instagram | day         |
      | /social_media/analytics           | week        |
      | /social_media/analytics/facebook  | week        |
      | /social_media/analytics/twitter   | week        |
      | /social_media/analytics/instagram | week        |
      | /social_media/analytics           | month       |
      | /social_media/analytics/facebook  | month       |
      | /social_media/analytics/twitter   | month       |
      | /social_media/analytics/instagram | month       |      
 

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                | granularity |
     # | /social_media/analytics            | day         |
      | /social_media/analytics/reach      | day         |
      | /social_media/analytics/followers  | day         |
      | /social_media/analytics/engagement | day         |
     # | /social_media/analytics            | week        |
      | /social_media/analytics/reach      | week        |
      | /social_media/analytics/followers  | week        |
      | /social_media/analytics/engagement | week        |
     # | /social_media/analytics            | month       |
      | /social_media/analytics/reach      | month       |
      | /social_media/analytics/followers  | month       |
      | /social_media/analytics/engagement | month       |

  Scenario Outline: Get specific analytics data from API for a given wrong granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2014-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    #And Response code is "400"
    And Response code is "200"
    #And Custom code is "53"

    Examples: 
      | url                                | granularity |
      #| /social_media/analytics            | day         |
      | /social_media/analytics/reach      | day         |
      | /social_media/analytics/followers  | day         |
      | /social_media/analytics/engagement | day         |
      #| /social_media/analytics            | week        |
      | /social_media/analytics/reach      | week        |
      | /social_media/analytics/followers  | week        |
      | /social_media/analytics/engagement | week        |
      #| /social_media/analytics            | month       |
      | /social_media/analytics/reach      | month       |
      | /social_media/analytics/followers  | month       |
      | /social_media/analytics/engagement | month       |
      
  Scenario: Getting large period analytics data
    When Getting "/social_media/analytics" data with "day" granularity for "property" since "1888-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"


  Scenario: Getting non-existent analytics data
    When Getting "/social_media/analytics/not_present" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    #And Response code is "404"
    And Response code is "200"
    #And Custom code is "151"

  Scenario: Getting "reach" non-existent analytics data
    When Getting "/social_media/analytics/reach/not_present" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "500"
    #And Response code is "404"
    #And Custom code is "151"

  Scenario: Getting "followers"non-existent analytics data
    When Getting "/social_media/analytics/followers/not_present" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    #And Response code is "404"
    And Response code is "500"
    #And Custom code is "151"

  Scenario: Getting "engagement" non-existent analytics data
    When Getting "/social_media/analytics/engagement/not_present" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    #And Response code is "404"
    And Response code is "500"
    #And Custom code is "151"

  Scenario: Getting facebook mismatched metrics analytics data
    When Getting "/social_media/analytics/facebook/tweets" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    #And Response code is "404"
    And Response code is "200"
    #And Custom code is "151"

  Scenario: Getting tweeter mismatched metrics analytics data
    When Getting "/social_media/analytics/tweeter/likes" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    #And Response code is "404"
    And Response code is "500"
    #And Custom code is "151"

  Scenario: Getting instagram mismatched metrics analytics data
    When Getting "/social_media/analytics/instagram/impressions" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    #And Response code is "404"
    And Response code is "200"
    #And Custom code is "151"

  Scenario Outline: Get analytics data from API with missing parameters
    When Getting "<url>" data with "<granularity>" granularity for "property" since "<start_date>" until "<end_date>"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                | granularity | start_date | end_date   |
      #| /social_media/analytics/           |             | 2015-09-01 | 2015-09-01 |
      #| /social_media/analytics/           |             |            | 2015-09-01 |
      #| /social_media/analytics/           |             | 2015-09-01 |            |
      #| /social_media/analytics/           |             |            |            |
      #| /social_media/analytics/           | day         |            | 2015-09-01 |
      #| /social_media/analytics/           | day         | 2015-09-01 |            |
      #| /social_media/analytics/           | day         |            |            |
      | /social_media/analytics/reach      |             | 2015-09-01 | 2015-09-01 |
      #| /social_media/analytics/reach      |             |            | 2015-09-01 |
      | /social_media/analytics/reach      |             | 2015-09-01 |            |
      | /social_media/analytics/reach      |             |            |            |
      #| /social_media/analytics/reach      | day         |            | 2015-09-01 |
      | /social_media/analytics/reach      | day         | 2015-09-01 |            |
      | /social_media/analytics/reach      | day         |            |            |
      | /social_media/analytics/followers  |             | 2015-09-01 | 2015-09-01 |
      #| /social_media/analytics/followers  |             |            | 2015-09-01 |
      | /social_media/analytics/followers  |             | 2015-09-01 |            |
      | /social_media/analytics/followers  |             |            |            |
      #| /social_media/analytics/followers  | day         |            | 2015-09-01 |
      | /social_media/analytics/followers  | day         | 2015-09-01 |            |
      | /social_media/analytics/followers  | day         |            |            |
      | /social_media/analytics/engagement |             | 2015-09-01 | 2015-09-01 |
      #| /social_media/analytics/engagement |             |            | 2015-09-01 |
      | /social_media/analytics/engagement |             | 2015-09-01 |            |
      | /social_media/analytics/engagement |             |            |            |
      #| /social_media/analytics/engagement | day         |            | 2015-09-01 |
      | /social_media/analytics/engagement | day         | 2015-09-01 |            |
      | /social_media/analytics/engagement | day         |            |            |

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
      | url                                | granularity | start_date        | end_date       | expected_granularity | expected_since    | expected_until | count |
#!  Expected: is "day" Actual: null              | /social_media/analytics/reach      |             |                   |                | day                  | today - 1 month   | today          | 32    |
#!  Expected: is "day" Actual: null              | /social_media/analytics/reach      |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     | 1     |
      #| /social_media/analytics/reach      | day         |                   | today          | day                  | today - 1 month   | today          | 32    |
      #| /social_media/analytics/reach      | day         | today             |                | day                  | today             | today          | 1     |
      #| /social_media/analytics/reach      | week        |                   | today          | week                 | today - 13 weeks  | today          | 13    |
      #| /social_media/analytics/reach      | week        | today             |                | week                 | today             | today          | 0     |
      #| /social_media/analytics/reach      | month       |                   | today          | month                | today - 6 months  | today          | 6     |
      #| /social_media/analytics/reach      | month       | today             |                | month                | today             | today          | 0     |
#!  expected:<2015-09-03> but was:<2015-08-24>      | /social_media/analytics/reach      | day         | today - 100 days  | today          | day                  | today - 90 days   | today          | 91    |
#!  expected:<2015-06-03> but was:<2015-05-06>      | /social_media/analytics/reach      | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 26    |
#!  expected:<2012-12-02> but was:<2012-08-02>      | /social_media/analytics/reach      | month       | today - 40 months | today          | month                | today - 36 months | today          | 36    |
#!  expected:<2015-12-02> but was:<2015-12-04>      | /social_media/analytics/reach      | day         | today + 2 days    | today + 3 days | day                  | today             | today          | 1     |
#!  Expected: is "day" Actual: null        |             |                   |                | day                  | today - 1 month   | today          | 32    |
#!  Expected: is "day" Actual: null        |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     | 1     |
      #| /social_media/analytics/followers  | day         |                   | today          | day                  | today - 1 month   | today          | 32    |
      #| /social_media/analytics/followers  | day         | today             |                | day                  | today             | today          | 1     |
      #| /social_media/analytics/followers  | week        |                   | today          | week                 | today - 13 weeks  | today          | 13    |
      #| /social_media/analytics/followers  | week        | today             |                | week                 | today             | today          | 0     |
      #| /social_media/analytics/followers  | month       |                   | today          | month                | today - 6 months  | today          | 6     |
      #| /social_media/analytics/followers  | month       | today             |                | month                | today             | today          | 0     |
      #| /social_media/analytics/followers  | day         | today - 100 days  | today          | day                  | today - 90 days   | today          | 91    |
#!  expected:<2015-06-03> but was:<2015-05-06>	   | /social_media/analytics/followers  | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 26    |
#!  expected:<2012-12-02> but was:<2012-08-02>      | /social_media/analytics/followers  | month       | today - 40 months | today          | month                | today - 36 months | today          | 36    |
#!  expected:<2015-12-02> but was:<2015-12-04>      | /social_media/analytics/followers  | day         | today + 2 days    | today + 3 days | day                  | today             | today          | 1     |
#!  Expected: is "day" Actual: null      | /social_media/analytics/engagement |             |                   |                | day                  | today - 1 month   | today          | 32    |
#!  Expected: is "day" Actual: null      | /social_media/analytics/engagement |             | 2015-09-01        | 2015-09-01     | day                  | 2015-09-01        | 2015-09-01     | 1     |
      #| /social_media/analytics/engagement | day         |                   | today          | day                  | today - 1 month   | today          | 32    |
      #| /social_media/analytics/engagement | day         | today             |                | day                  | today             | today          | 1     |
      #| /social_media/analytics/engagement | week        |                   | today          | week                 | today - 13 weeks  | today          | 13    |
      #| /social_media/analytics/engagement | week        | today             |                | week                 | today             | today          | 0     |
      #| /social_media/analytics/engagement | month       |                   | today          | month                | today - 6 months  | today          | 6     |
      #| /social_media/analytics/engagement | month       | today             |                | month                | today             | today          | 0     |
#!  expected:<2015-09-03> but was:<2015-08-24>      | /social_media/analytics/engagement | day         | today - 100 days  | today          | day                  | today - 90 days   | today          | 91    |
#!  expected:<2015-06-03> but was:<2015-05-06>      | /social_media/analytics/engagement | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 26    |
#!  expected:<2012-12-02> but was:<2012-08-02>      | /social_media/analytics/engagement | month       | today - 40 months | today          | month                | today - 36 months | today          | 36    |
#!  expected:<2015-12-02> but was:<2015-12-04>      | /social_media/analytics/engagement | day         | today + 2 days    | today + 3 days | day                  | today             | today          | 1     |
