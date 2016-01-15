Feature: facebook_posts
  Testing of api for facebook posts with mock data in db - testing property id is "99999999-9999-4999-a999-999999999999"

  Scenario Outline: Getting a list of items
    When List of facebook items "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And There are <count> posts returned

    Examples: 
      | url                       | limit | cursor | count | property                             | response_code | content_type     |
      | /analytics/facebook/posts |       |        | 50    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook/posts | /null |        | 50    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook/posts |       | /null  | 50    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook/posts | /null | /null  | 50    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook/posts | 51    |        | 51    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook/posts | 51    | /null  | 51    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook/posts |       | 1      | 50    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook/posts | /null | 1      | 50    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook/posts | 20    | 0      | 20    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook/posts | 60    | 0      | 60    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook/posts | 5     | 5      | 5     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |

  Scenario Outline: Checking error codes for getting list of items
    When List of facebook items "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples: 
      | url                       | limit | cursor | response_code | custom_code | property                             |
      | /analytics/facebook/posts | /null | -1     | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts |       | -1     | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | 10    | -1     | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | text  | 0      | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | text  | /null  | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | text  |        | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/posts | 10    | text   | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
