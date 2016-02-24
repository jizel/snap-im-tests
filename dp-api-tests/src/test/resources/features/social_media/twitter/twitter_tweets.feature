Feature: twitter_tweets
  Testing of API for Twitter Tweets with mock data in db - testing property id is "99000099-9999-4999-a999-999999999999"

  Scenario Outline: Getting a list of Twitter Tweets
    When List of twitter items "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And All Twitter Tweets data are owned by "<data_owner>"
    And There are <count> posts returned

    Examples:
      | url                       | limit | cursor | count | property                             | response_code | content_type     | data_owner |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/tweets | /null | 0      | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/tweets | /null | 1      | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/tweets | 0     | /null  | 0     | 99000099-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/tweets | 1     | 0      | 1     | 99000099-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/tweets | 0     | 0      | 0     | 99000099-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/tweets | 51    | 0      | 51    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | twitter    |
      | /analytics/twitter/tweets | 0     | 1      | 0     | 99000099-9999-4999-a999-999999999999 | 200           | application/json | twitter    |

  Scenario Outline: Twitter tweets are sorted ascendingly by their attributes
    When List of twitter items "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>" and sort by "<sort>" descendingly
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And There are <count> posts returned
    And Records are sorted descendingly by "<sort>" of type "<type>"

    Examples:
      | url                       | limit | cursor | count | property                             | response_code | content_type     | sort        | type   |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | content     | string |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | impressions | double |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | engagement  | double |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | retweets    | double |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | favorites   | double |

  Scenario Outline: Twitter tweets are sorted ascendingly by their attributes
    When List of twitter items "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>" and sort by "<sort>" ascendingly
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And There are <count> posts returned
    And Records are sorted ascendingly by "<sort>" of type "<type>"

    Examples:
      | url                       | limit | cursor | count | property                             | response_code | content_type     | sort        | type   |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | content     | string |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | impressions | double |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | engagement  | double |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | retweets    | double |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 | 200           | application/json | favorites   | double |

  Scenario Outline: Checking error codes for getting list of items
    When List of twitter items "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples:
      | url                       | limit | cursor | response_code | custom_code | property                             |
      | /analytics/twitter/tweets | /null | -1     | 400           | 63          | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets |       | -1     | 400           | 63          | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | 10    | -1     | 400           | 63          | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | text  | 0      | 400           | 63          | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | text  | /null  | 400           | 63          | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | text  |        | 400           | 63          | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | 10    | text   | 400           | 63          | 99000099-9999-4999-a999-999999999999 |
