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
