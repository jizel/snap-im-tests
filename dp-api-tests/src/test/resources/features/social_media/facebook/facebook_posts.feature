Feature: facebook_posts
  Testing of api for facebook posts with mock data in db - testing property id is "99999999-9999-4999-a999-999999999999"

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
