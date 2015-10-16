Feature: Overall analytics feature

  Scenario: Get all analytics data (reach, engagement, followers) from API for a day
    When Getting "/social_media/analytics" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values for all metrics
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get reach analytics data from API for a day
    When Getting "/social_media/analytics/reach" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get engagement analytics data from API for a day
    When Getting "/social_media/analytics/engagement" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get followers analytics data from API for a day
    When Getting "/social_media/analytics/followers" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get all Facebook analytics data (number of posts, engagement, likes and unlikes) from API for a day
    When Getting "/social_media/analytics/facebook" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values for all metrics
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Facebook number of posts analytics data from API for a day
    When Getting "/social_media/analytics/facebook/number_of_posts" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Facebook engagement analytics data from API for a day
    When Getting "/social_media/analytics/facebook/engagement" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Facebook likes analytics data from API for a day
    When Getting "/social_media/analytics/facebook/likes" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Facebook unlikes analytics data from API for a day
    When Getting "/social_media/analytics/facebook/unlikes" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Facebook posts analytics data from API for a day
    When Getting "/social_media/analytics/facebook/posts" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get all Twitter analytics data (tweets, engagement, followers, impressions, retweets, retweet reach, mentions and mentions reach) from API for a day
    When Getting "/social_media/analytics/twitter" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values for all metrics
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Twitter tweets analytics data from API for a day
    When Getting "/social_media/analytics/twitter/tweets" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Twitter number of tweets analytics data from API for a day
    When Getting "/social_media/analytics/twitter/number_of_tweets" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Twitter engagement analytics data from API for a day
    When Getting "/social_media/analytics/twitter/engagement" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Twitter followers analytics data from API for a day
    When Getting "/social_media/analytics/twitter/followers" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Twitter impressions analytics data from API for a day
    When Getting "/social_media/analytics/twitter/impressions" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Twitter retweets analytics data from API for a day
    When Getting "/social_media/analytics/twitter/retweets" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Twitter retweets reach analytics data from API for a day
    When Getting "/social_media/analytics/twitter/retweets_reach" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Twitter mentions analytics data from API for a day
    When Getting "/social_media/analytics/twitter/mentions" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Twitter mentions reach analytics data from API for a day
    When Getting "/social_media/analytics/twitter/mentions_reach" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get all Instagram analytics data (number of pictures, engagement, followers and tags) from API for a day
    When Getting "/social_media/analytics/instagram" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values for all metrics
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Instagram pictures analytics data from API for a day
    When Getting "/social_media/analytics/instagram/pictures" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Instagram engagement analytics data from API for a day
    When Getting "/social_media/analytics/instagram/engagement" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Instagram followers analytics data from API for a day
    When Getting "/social_media/analytics/instagram/followers" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Instagram mentions analytics data from API for a day
    When Getting "/social_media/analytics/instagram/mentions" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

  Scenario: Get Instagram tags analytics data from API for a day
    When Getting "/social_media/analytics/instagram/tags" data with "day" granularity for "property" since "2015-09-01" until "2015-09-01"
    Then Response contains 5 values
    And Content type is "application/json"
    And Response code is "200"

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
      | /social_media/analytics/instagram                | 400        | 52          |
