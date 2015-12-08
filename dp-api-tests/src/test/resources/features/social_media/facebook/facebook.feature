Feature: facebook

  Scenario Outline: Get facebook analytics data from API for a given wrong granularity
    When Getting "<url>" data with "<granularity>" granularity for "999999" since "2015-12-03" until "2015-12-03"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "151"

    Examples: 
      | url                                              | granularity |
      | /social_media/analytics/facebook/                | ddd         |
      | /social_media/analytics/facebook/number_of_posts | www         |
      | /social_media/analytics/facebook/engagement      | yyy         |
      | /social_media/analytics/facebook/likes           | ttt         |
      | /social_media/analytics/facebook/unlikes         | uuu         |
      | /social_media/analytics/facebook/reach           | 1234        |

 # Scenario Outline: Get specific analytics data from API for a given day granularity
 #   When Getting "<url>" data with "<granularity>" granularity for "999999" since "2015-12-03" until "2015-12-03"
 #   Then Response code is "200"
 #   And Content type is "application/json"
 #   And Response contains "<count>" values
 #   And Body contains entity with attribute "since" value "2015-12-03"
 #   And Body contains entity with attribute "until" value "2015-12-03"
 #   And Body contains entity with attribute "granularity" value "day"

 #   Examples: 
 #     | url                                              | granularity | count |
 #     | /social_media/analytics/facebook/number_of_posts | day         | 1     |
 #     | /social_media/analytics/facebook/engagement      | day         | 1     |
 #     | /social_media/analytics/facebook/likes           | day         | 1     |
 #     | /social_media/analytics/facebook/unlikes         | day         | 1     |
 #     | /social_media/analytics/facebook/reach           | day         | 1     |
 #     | /social_media/analytics/facebook/followers       | day         | 1     |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "999999" since "<since>" until "<until>"
    Then Response code is "404"
    And Content type is "application/json"
    And Response contains "<count>" values
    And Body contains entity with attribute "since" value "<since>"
    And Body contains entity with attribute "until" value "<until>"
    And Body contains entity with attribute "granularity" value "day"

    Examples: 
      | url                                              | granularity | count | since      | until      |	property	|
      | /social_media/analytics/facebook/number_of_posts | day         | 1     | 2015-12-01 | 2015-12-01 |	999999		|
      | /social_media/analytics/facebook/engagement      | day         | 1     | 2015-12-01 | 2015-12-01 |	999999		|
      | /social_media/analytics/facebook/likes           | day         | 1     | 2015-12-01 | 2015-12-01 |	999999		|
      | /social_media/analytics/facebook/unlikes         | day         | 1     | 2015-12-01 | 2015-12-01 |	999999		|
      | /social_media/analytics/facebook/reach           | day         | 1     | 2015-12-01 | 2015-12-01 |	999999		|
      | /social_media/analytics/facebook/followers       | day         | 1     | 2015-12-01 | 2015-12-01 |	999999		|
      | /social_media/analytics/facebook/number_of_posts | day         | 11    | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/engagement      | day         | 11    | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/likes           | day         | 11    | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/unlikes         | day         | 11    | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/reach           | day         | 11    | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/followers       | day         | 11    | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/number_of_posts | day         | 23    | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/engagement      | day         | 23    | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/likes           | day         | 23    | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/unlikes         | day         | 23    | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/reach           | day         | 23    | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/followers       | day         | 23    | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/number_of_posts | day         | 90    | 2015-06-07 | 2015-12-07 |	999999		|
      | /social_media/analytics/facebook/engagement      | day         | 90    | 2015-06-07 | 2015-12-07 |	999999		|
      | /social_media/analytics/facebook/likes           | day         | 90    | 2015-06-07 | 2015-12-07 |	999999		|
      | /social_media/analytics/facebook/unlikes         | day         | 90    | 2015-06-07 | 2015-12-07 |	999999		|
      | /social_media/analytics/facebook/reach           | day         | 90    | 2015-06-07 | 2015-12-07 |	999999		|
      | /social_media/analytics/facebook/followers       | day         | 90    | 2015-06-07 | 2015-12-07 |	999999		|
      | /social_media/analytics/facebook/number_of_posts | week        | 1     | 2015-12-07 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/engagement      | week        | 1     | 2015-12-07 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/likes           | week        | 1     | 2015-12-07 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/unlikes         | week        | 1     | 2015-12-07 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/reach           | week        | 1     | 2015-12-07 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/followers       | week        | 1     | 2015-12-07 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/number_of_posts | week        | 1     | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/engagement      | week        | 1     | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/likes           | week        | 1     | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/unlikes         | week        | 1     | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/reach           | week        | 1     | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/followers       | week        | 1     | 2015-12-03 | 2015-12-13 |	999999		|
      | /social_media/analytics/facebook/number_of_posts | week        | 2     | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/engagement      | week        | 2     | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/likes           | week        | 2     | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/unlikes         | week        | 2     | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/reach           | week        | 2     | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/followers       | week        | 2     | 2015-12-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/number_of_posts | week        | 26    | 2015-01-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/engagement      | week        | 26    | 2015-01-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/likes           | week        | 26    | 2015-01-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/unlikes         | week        | 26    | 2015-01-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/reach           | week        | 26    | 2015-01-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/followers       | week        | 26    | 2015-01-07 | 2015-12-23 |	999999		|
      | /social_media/analytics/facebook/number_of_posts | month       | 1     | 2015-11-01 | 2015-11-30 |	999999		|
      | /social_media/analytics/facebook/engagement      | month       | 1     | 2015-11-01 | 2015-11-30 |	999999		|
      | /social_media/analytics/facebook/likes           | month       | 1     | 2015-11-01 | 2015-11-30 |	999999		|
      | /social_media/analytics/facebook/unlikes         | month       | 1     | 2015-11-01 | 2015-11-30 |	999999		|
      | /social_media/analytics/facebook/reach           | month       | 1     | 2015-11-01 | 2015-11-30 |	999999		|
      | /social_media/analytics/facebook/followers       | month       | 1     | 2015-11-01 | 2015-11-30 |	999999		|
      | /social_media/analytics/facebook/number_of_posts | month       | 1     | 2015-02-01 | 2015-03-23 |	999999		|
      | /social_media/analytics/facebook/engagement      | month       | 1     | 2015-02-01 | 2015-03-23 |	999999		|
      | /social_media/analytics/facebook/likes           | month       | 1     | 2015-02-01 | 2015-03-23 |	999999		|
      | /social_media/analytics/facebook/unlikes         | month       | 1     | 2015-02-01 | 2015-03-23 |	999999		|
      | /social_media/analytics/facebook/reach           | month       | 1     | 2015-02-01 | 2015-03-23 |	999999		|
      | /social_media/analytics/facebook/followers       | month       | 1     | 2015-02-01 | 2015-03-23 |	999999		|
      | /social_media/analytics/facebook/number_of_posts | month       | 36    | 2013-02-01 | 2016-12-31 |	999999		|
      | /social_media/analytics/facebook/engagement      | month       | 36    | 2013-02-01 | 2016-12-31 |	999999		|
      | /social_media/analytics/facebook/likes           | month       | 36    | 2013-02-01 | 2016-12-31 |	999999		|
      | /social_media/analytics/facebook/unlikes         | month       | 36    | 2013-02-01 | 2016-12-31 |	999999		|
      | /social_media/analytics/facebook/reach           | month       | 36    | 2013-02-01 | 2016-12-31 |	999999		|
      | /social_media/analytics/facebook/followers       | month       | 36    | 2013-02-01 | 2016-12-31 |	999999		|

  Scenario: Getting non-existent analytics data
    When Getting "/social_media/analytics/facebook/not_present" data with "day" granularity for "999999" since "2015-12-03" until "2015-12-03"
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario: Getting mismatched metrics analytics data
    When Getting "/social_media/analytics/facebook/tweets" data with "day" granularity for "999999" since "2015-12-03" until "2015-12-03"
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                              | error_code | custom_code |
      | /social_media/analytics/facebook                 | 400        | 52          |
      | /social_media/analytics/facebook/number_of_posts | 400        | 52          |
      | /social_media/analytics/facebook/engagement      | 400        | 52          |
      | /social_media/analytics/facebook/likes           | 400        | 52          |
      | /social_media/analytics/facebook/unlikes         | 400        | 52          |
      | /social_media/analytics/facebook/reach           | 400        | 52          |
      | /social_media/analytics/facebook/followers       | 400        | 52          |

  #not yet implemented
  Scenario Outline: Getting a list of items
    When List of "<url>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <count> posts returned

    Examples: 
      | url                                    | limit | cursor | count |
      | /social_media/analytics/facebook/posts |       |        | 50    |
      | /social_media/analytics/facebook/posts | /null |        | 50    |
      | /social_media/analytics/facebook/posts |       | /null  | 50    |
      | /social_media/analytics/facebook/posts | /null | /null  | 50    |
      | /social_media/analytics/facebook/posts | 51    |        | 50    |
      | /social_media/analytics/facebook/posts | 51    | /null  | 50    |
      | /social_media/analytics/facebook/posts |       | 1      | 50    |
      | /social_media/analytics/facebook/posts | /null | 1      | 50    |
      | /social_media/analytics/facebook/posts | 20    | 0      | 20    |
      | /social_media/analytics/facebook/posts | 60    | 0      | 50    |
      | /social_media/analytics/facebook/posts | 5     | 5      | 5     |

  #just posts, but not yet implemented, other metrics are not pageable
  Scenario Outline: Checking error codes for getting list of items
    When List of "<url>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                    | limit | cursor | response_code | custom_code |
      | /social_media/analytics/facebook/posts | /null | -1     | 400           | 63          |
      | /social_media/analytics/facebook/posts |       | -1     | 400           | 63          |
      | /social_media/analytics/facebook/posts | 10    | -1     | 400           | 63          |
      | /social_media/analytics/facebook/posts | text  | 0      | 400           | 63          |
      | /social_media/analytics/facebook/posts | text  | /null  | 400           | 63          |
      | /social_media/analytics/facebook/posts | text  |        | 400           | 63          |
      | /social_media/analytics/facebook/posts | 10    | text   | 400           | 63          |

  Scenario Outline: Get analytics data from API with missing parameters
    When Getting "<url>" data with "<granularity>" granularity for "999999" since "<start_date>" until "<end_date>"
    Then Response code is "200"
    And Content type is "application/json"
    And Response contains <count> values
    And Data is owned by "facebook"

    Examples: 
      | url                                              | granularity | start_date | end_date   | count |
      | /social_media/analytics/facebook/number_of_posts |             | 2015-12-03 | 2015-12-03 | 1     |
      | /social_media/analytics/facebook/engagement      | day         |            | 2015-12-03 | 30    |
      | /social_media/analytics/facebook/likes           | day         | 2015-12-03 |            | 30    |
      | /social_media/analytics/facebook/unlikes         | day         |            |            | 30    |
      | /social_media/analytics/facebook/reach           |             |            |            | 30    |

  Scenario Outline: Get analytics data from API from 1800s
    When Getting "<url>" data with "<granularity>" granularity for "999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                                              | granularity | start_date | end_date   |
      | /social_media/analytics/facebook                 | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/facebook/number_of_posts | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/facebook/engagement      | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/facebook/likes           | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/facebook/unlikes         | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/facebook/reach           | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/facebook/followers       | month       | 1888-09-01 | 1890-10-01 |
      | /social_media/analytics/facebook                 | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/facebook/number_of_posts | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/facebook/engagement      | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/facebook/likes           | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/facebook/unlikes         | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/facebook/reach           | day         | 1888-09-01 | 1888-09-01 |
      | /social_media/analytics/facebook/followers       | day         | 1888-09-01 | 1888-09-01 |

  Scenario Outline: Checking default parameter values
    Empty column in examples section means default value will be used for this parameter.
    if text is empty, returns null
    if text is date in ISO format (2015-01-01), it returns this date
    text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which will add or substract
    particular number of days/weeks/months from first part of expression

    When Getting "<url>" data with "<granularity>" granularity for "999999" since "<start_date>" until "<end_date>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Response contains no more than <count> values

    Examples: 
      | url                                              | granularity | start_date     | end_date          | expected_granularity | expected_since    | expected_until | count |
      | /social_media/analytics/facebook/number_of_posts |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/number_of_posts |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/facebook/number_of_posts | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/number_of_posts | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/number_of_posts | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/facebook/number_of_posts | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/facebook/number_of_posts | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/facebook/number_of_posts | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/facebook/number_of_posts | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/facebook/number_of_posts | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/facebook/number_of_posts | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/facebook/number_of_posts | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/engagement      |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/engagement      |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/facebook/engagement      | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/engagement      | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/engagement      | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/facebook/engagement      | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/facebook/engagement      | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/facebook/engagement      | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/facebook/engagement      | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/facebook/engagement      | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/facebook/engagement      | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/facebook/engagement      | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/likes           |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/likes           |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/facebook/likes           | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/likes           | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/likes           | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/facebook/likes           | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/facebook/likes           | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/facebook/likes           | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/facebook/likes           | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/facebook/likes           | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/facebook/likes           | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/facebook/likes           | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/unlikes         |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/unlikes         |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/facebook/unlikes         | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/unlikes         | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/unlikes         | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/facebook/unlikes         | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/facebook/unlikes         | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/facebook/unlikes         | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/facebook/unlikes         | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/facebook/unlikes         | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/facebook/unlikes         | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/facebook/unlikes         | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/reach           |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/reach           |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/facebook/reach           | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/reach           | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/reach           | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/facebook/reach           | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/facebook/reach           | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/facebook/reach           | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/facebook/reach           | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/facebook/reach           | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/facebook/reach           | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/facebook/reach           | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/followers       |             |                |                   | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/followers       |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 1     |
      | /social_media/analytics/facebook/followers       | day         |                | today             | day                  | today - 1 month   | today          | 32    |
      | /social_media/analytics/facebook/followers       | day         | today          |                   | day                  | today             | today          | 1     |
      | /social_media/analytics/facebook/followers       | week        |                | today             | week                 | today - 13 weeks  | today          | 13    |
      | /social_media/analytics/facebook/followers       | week        | today          |                   | week                 | today             | today          | 0     |
      | /social_media/analytics/facebook/followers       | month       |                | today             | month                | today - 6 months  | today          | 6     |
      | /social_media/analytics/facebook/followers       | month       | today          |                   | month                | today             | today          | 0     |
      | /social_media/analytics/facebook/followers       | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 91    |
      | /social_media/analytics/facebook/followers       | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 26    |
      | /social_media/analytics/facebook/followers       | month       | today          | today - 40 months | month                | today - 36 months | today          | 36    |
      | /social_media/analytics/facebook/followers       | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 1     |

  # Scenario: Get data owners data for facebook
  #can be combined to other scnearios
  #  When Getting "/social_media/analytics/facebook/engagement" data with "day" granularity for "999999" since "2015-12-03" until "2015-12-03"
  #  Then Content type is "application/json"
  #  And Response code is "200"
  #  And Data is owned by "facebook"
  Scenario Outline: Checking number of values in response for various granularities
    When Getting "<url>" data with "<granularity>" granularity for "999999" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response contains no more than <count> values

    Examples: 
      | url                                              | granularity | since           | until | count |
      #this one is different - returns all metrics together, so validation of number of values needs to be different
      | /social_media/analytics/facebook/number_of_posts | day         | today - 1 day   | today | 2     |
      | /social_media/analytics/facebook/engagement      | day         | today - 6 days  | today | 7     |
      | /social_media/analytics/facebook/likes           | day         | today - 7 days  | today | 8     |
      | /social_media/analytics/facebook/unlikes         | day         | today - 8 days  | today | 9     |
      | /social_media/analytics/facebook/reach           | day         | today - 29 days | today | 30    |
      | /social_media/analytics/facebook/followers       | day         | today - 30 days | today | 31    |
      | /social_media/analytics/facebook/likes           | week        | today           | today | 0     |
