Feature: Overall analytics feature

  Scenario Outline: Get collective analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values for all metrics
    And Content type is "application/json"
    And Response code is "200"

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

  Scenario Outline: 
    When List of tweets is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are <limit> tweets returned

    Examples: 
      | limit | cursor |
      |       |        |
      | 15    |        |
      |       | 1      |
      | 20    | 0      |
      | 10    | 0      |
      | 5     | 5      |

  Scenario Outline: 
    When List of Facebook posts is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are <limit> Facebook posts returned

    Examples: 
      | limit | cursor |
      |       |        |
      | 15    |        |
      |       | 1      |
      | 20    | 0      |
      | 10    | 0      |
      | 5     | 5      |

  Scenario: Get Instagram tags analytics data from API for an invalid granularity
    When Getting "/social_media/analytics/instagram/tags" data with "invalid" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "63"

  Scenario Outline: Checking error codes for getting list of tweets
    When List of tweets is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | limit | cursor | response_code | custom_code |
      |       | -1     | 400           | 63          |
      |       | text   | 400           | 63          |
      | -1    |        | 400           | 63          |
      | text  |        | 400           | 63          |
      | 10    | -1     | 400           | 63          |
      | text  | 0      | 400           | 63          |
      | 10    | text   | 400           | 63          |

  Scenario: Get analytics data from API with missing granularity
    When Getting "/social_media/analytics/facebook/engagement" data with "" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get analytics data from API for a day with missing start date
    When Getting "/social_media/analytics/facebook/engagement" data with "day" granularity for "property" since "" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get analytics data from API for a day with missing end date
    When Getting "/social_media/analytics/facebook/engagement" data with "day" granularity for "property" since "2015-09-01" until ""
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"
