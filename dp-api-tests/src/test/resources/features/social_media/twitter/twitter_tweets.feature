Feature: Twitter tweets
  Testing of API for Twitter Tweets with mock data in db - testing property id is "99000099-9999-4999-a999-999999999999"

  Scenario Outline: Getting a list of Twitter Tweets
    When List of twitter items "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is 200
    And Content type is "application/json"
    And All Twitter Tweets data are owned by "twitter"
    And There are <count> Twitter posts returned

    Examples:
      | url                       | limit | cursor | count | property                             |
      | /analytics/twitter/tweets | /null | /null  | 50    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | /null | 0      | 50    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | /null | 1      | 50    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | 0     | /null  | 0     | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | 1     | 0      | 1     | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | 0     | 0      | 0     | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | 51    | 0      | 51    | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | 0     | 1      | 0     | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Twitter tweets are sorted ascendingly by their attributes
    When List of twitter items "<url>" for property id "<property>" is got with limit "/null" and cursor "/null" and sort by "<sort>" descendingly
    Then Response code is 200
    And Content type is "application/json"
    And All Twitter Tweets data are owned by "twitter"
    And There are <count> Twitter posts returned
    And Records are sorted descendingly by "<sort>" of type "<type>"

    Examples:
      | url                       | count | property                             | sort        | type   |
      | /analytics/twitter/tweets | 50    | 99000099-9999-4999-a999-999999999999 | content     | string |
      | /analytics/twitter/tweets | 50    | 99000099-9999-4999-a999-999999999999 | impressions | double |
      | /analytics/twitter/tweets | 50    | 99000099-9999-4999-a999-999999999999 | engagement  | double |
      | /analytics/twitter/tweets | 50    | 99000099-9999-4999-a999-999999999999 | retweets    | double |
      | /analytics/twitter/tweets | 50    | 99000099-9999-4999-a999-999999999999 | favorites   | double |

  Scenario Outline: Twitter tweets are sorted ascendingly by their attributes
    When List of twitter items "<url>" for property id "<property>" is got with limit "/null" and cursor "/null" and sort by "<sort>" ascendingly
    Then Response code is 200
    And Content type is "application/json"
    And All Twitter Tweets data are owned by "twitter"
    And There are <count> Twitter posts returned
    And Records are sorted ascendingly by "<sort>" of type "<type>"

    Examples:
      | url                       | count | property                             | sort        | type   |
      | /analytics/twitter/tweets | 50    | 99000099-9999-4999-a999-999999999999 | content     | string |
      | /analytics/twitter/tweets | 50    | 99000099-9999-4999-a999-999999999999 | impressions | double |
      | /analytics/twitter/tweets | 50    | 99000099-9999-4999-a999-999999999999 | engagement  | double |
      | /analytics/twitter/tweets | 50    | 99000099-9999-4999-a999-999999999999 | retweets    | double |
      | /analytics/twitter/tweets | 50    | 99000099-9999-4999-a999-999999999999 | favorites   | double |

  Scenario Outline: Checking error codes for getting list of items
    When List of twitter items "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is 400
    And Custom code is "63"

    Examples:
      | url                       | limit | cursor | property                             |
      | /analytics/twitter/tweets | /null | -1     | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets |       | -1     | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | 10    | -1     | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | text  | 0      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | text  | /null  | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | text  |        | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/tweets | 10    | text   | 99000099-9999-4999-a999-999999999999 |
