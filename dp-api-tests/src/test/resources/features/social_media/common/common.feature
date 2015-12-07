Feature: facebook

  Scenario Outline: Get collective analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "200"||"400"
    And Response contains "<int>"  values for all metrics

    Examples: 
      | url                               | granularity |	int |
      #| /social_media/analytics           | day         |	1	|
      | /social_media/analytics/facebook  | day         |	1	|
      | /social_media/analytics/twitter   | day         |	1	|
      | /social_media/analytics/instagram | day         |	1	|
      | /social_media/analytics           | week        |	1	|
      | /social_media/analytics/facebook  | week        |	1	|
      | /social_media/analytics/twitter   | week        |	1	|
      | /social_media/analytics/instagram | week        |	1	|
      | /social_media/analytics           | month       |	1	|
      | /social_media/analytics/facebook  | month       |	1	|
      | /social_media/analytics/twitter   | month       |	1	|
      | /social_media/analytics/instagram | month       |	1	|

  Scenario Outline: Get collective analytics data from API for a given wrong granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2014-09-01"
    Then Content type is "application/json"
    #And Response code is "400"
    And Response code is "200"
    #And Custom code is "53"
    Examples: 
      | url                               | granularity |
      #| /social_media/analytics           | day         |
      #| /social_media/analytics/facebook  | day         |
      #| /social_media/analytics/twitter   | day         |
      #| /social_media/analytics/instagram | day         |
      #| /social_media/analytics           | week        |
      #| /social_media/analytics/facebook  | week        |
      #| /social_media/analytics/twitter   | week        |
      #| /social_media/analytics/instagram | week        |
      #| /social_media/analytics           | month       |
      #| /social_media/analytics/facebook  | month       |
      #| /social_media/analytics/twitter   | month       |
      #| /social_media/analytics/instagram | month       |

  Scenario Outline: Get collective analytics data from API for a not given since granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "" until "2014-09-01"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "53"

    Examples: 
 	 | url								 | 
	 | /social_media/analytics           | 
  	 | /social_media/analytics/facebook  | 
 	 | /social_media/analytics/twitter   | 
 	 | /social_media/analytics/instagram | 
  	 | /social_media/analytics           | 
 	 | /social_media/analytics/facebook  |
  	 | /social_media/analytics/twitter   | 
  	 | /social_media/analytics/instagram | 
  	 | /social_media/analytics           | 
  	 | /social_media/analytics/facebook  | 
  	 | /social_media/analytics/twitter   | 
  	 | /social_media/analytics/instagram |
  
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
      | /social_media/analytics            | day         |
      | /social_media/analytics/reach      | day         |
      | /social_media/analytics/followers  | day         |
      | /social_media/analytics/engagement | day         |
      | /social_media/analytics            | week        |
      | /social_media/analytics/reach      | week        |
      | /social_media/analytics/followers  | week        |
      | /social_media/analytics/engagement | week        |
      | /social_media/analytics            | month       |
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
      | /social_media/analytics            | day         |
      | /social_media/analytics/reach      | day         |
      | /social_media/analytics/followers  | day         |
      | /social_media/analytics/engagement | day         |
      | /social_media/analytics            | week        |
      | /social_media/analytics/reach      | week        |
      | /social_media/analytics/followers  | week        |
      | /social_media/analytics/engagement | week        |
      | /social_media/analytics            | month       |
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
    #And Response code is "500"
    And Response code is "404"
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
      | url | granularity | start_date | end_date | expected_granularity | expected_since | expected_until | count |
