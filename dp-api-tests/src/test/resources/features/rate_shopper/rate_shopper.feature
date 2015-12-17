Feature: rate_shopper
  Tests are meant to be run after the midnight fetch (1:00)

  Scenario Outline: Get BAR values for a given property analytics data from API
    When Getting rate data for "<property>" since "<since>" until "<until>" fetched "<fetch_datetime>"
    Then Response contains <count> values
    And Content type is "application/json"
    And Response code is "200"

    Examples: 
      | property | since      | until      | count | fetch_datetime      |
      | 10010004 | 2015-12-14 | 2015-12-14 | 1     | 2015-12-14T10:15:30 |
      | 10010004 | 2015-12-14 | 2015-12-20 | 7     | 2015-12-14T10:15:30 |
      | 10010004 | 2015-12-14 | 2016-12-31 | 61    | 2015-12-14T10:15:30 |

  Scenario Outline: Check minimal, average, and maximal market values
    When Getting BAR values for a given market for "<property>" since "<since>" until "<until>"
    Then "minimal" value is not more than "average" value
    And "average" value is not more than "maximal" value

    Examples: 
      | property | since      | until      |
      | 10010003 | 2015-12-14 | 2015-12-14 |
      | 10010003 | 2015-12-10 | 2015-12-20 |
      | 10010003 | 2015-12-14 | 2015-12-30 |
      | 10010003 | 2015-12-14 | 2015-12-20 |
      | 10010003 | 2015-12-10 | 2015-12-20 |
      | 10010003 | 2015-12-14 | 2015-12-30 |
      | 10010003 | 2015-11-08 | 2015-12-07 |
      | 10010003 | 2015-02-08 | 2015-03-30 |
      | 10010003 | 2013-02-08 | 2016-12-31 |

  Scenario Outline: Getting a list of items
    When List of "<url>" for property id "10010003" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are at most <count> items returned

    Examples: 
      | url                                       | limit | cursor | count |
      | /rate_shopper/analytics/market/properties |       |        | 50    |
      | /rate_shopper/analytics/market/properties | 51    |        | 50    |
      | /rate_shopper/analytics/market/properties |       | 1      | 50    |
      | /rate_shopper/analytics/market/properties | 20    | 0      | 20    |
      | /rate_shopper/analytics/market/properties | 49    | 0      | 49    |
      | /rate_shopper/analytics/market/properties | 5     | 5      | 5     |

  Scenario: Getting non-existent analytics data
    When Getting rate data for "invalid" since "2015-12-07" until "2015-12-07" fetched ""
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario: Getting data for a non-existent property
    When Getting rate data for "non-existent" since "2015-09-01" until "2015-09-01" fetched ""
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "151"

  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                       | error_code | custom_code |
      | /rate_shopper/analytics/property          | 404        | 151         |
      | /rate_shopper/analytics/market            | 400        | 52          |
      | /rate_shopper/analytics/market/properties | 400        | 52          |

  Scenario Outline: Checking default parameter values
    When Getting rate data for "<property>" since "<since>" until "<until>" fetched "<fetch_datetime>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Body contains entity with attribute "fetch_datetime" value "<expected_fetch_datetime>"

    #   argument ranges:
    #	fetch_datetime  ( ; last fetch]
    #            since  [fetch_datetime ; fetch_datetime + 60 days]
    #            until  [since ; fetch_datetime + 60 days]
    #
    #	defaults:
    #	fetch_datetime - last fetch  (now - 12h ; now)
    #            since - last fetch date
    #            until - last fetch date + 60 days
    #
    # 	first fetch - 2015-12-11T16:11
    Examples: 
      | property | since          | until          | fetch_datetime      | expected_since | expected_until  | expected_fetch_datetime |
      #defaults:
      #default since
      | 10010003 |                | 2015-12-15     | 2015-12-14T10:00:01 | 2015-12-14     | 2015-12-15      | 2015-12-14T10:00:01     |
      | 10010003 |                | 2015-12-12     | 2015-12-14T10:00:01 | 2015-12-14     | 2016-02-12      | 2015-12-14T10:00:01     |
      #default until
      | 10010003 | 2015-12-15     |                | 2015-12-14T10:00:01 | 2015-12-15     | 2016-02-12      | 2015-12-14T10:00:01     |
      #default fetch datetime, copy-paste to get the last fetch
      | 10010003 | 2015-12-15     | 2015-12-15     |                     | today          | today + 60 days | last fetch              |
      | 10010003 | today + 2 days | today + 3 days |                     | today + 2 days | today + 3 days  | last fetch              |
      #default since, until
      | 10010003 |                |                | 2015-12-14T10:00:01 | 2015-12-07     | 2016-02-05      | 2015-12-14T10:00:01     |
      #default since, fetch datetime
      | 10010003 |                | 2015-12-15     |                     | today          | today + 60 days | last fetch              |
      | 10010003 |                | today + 2 days |                     | today          | today + 2 days  | last fetch              |
      #default until, fetch datetime
      | 10010003 | 2015-12-15     |                |                     | today          | today + 60 days | last fetch              |
      | 10010003 | today + 3 days |                |                     | today + 3 days | today + 60 days | last fetch              |
      #default since, until, fetch datetime
      | 10010003 |                |                |                     | today          | today + 60 days | last fetch              |
      #ranges:
      #fetch datetime
      #requesting fetch_datetime 2003 should return data from the first fetch - 2015-12-11T16:11
      | 10010003 | 2015-12-15     | 2015-12-15     | 2003-12-07T10:00:01 | 2015-12-11     | 2016-02-09      | 2015-12-11T16:11        |
      | 10010003 | 2015-12-15     | 2015-12-15     | 2099-12-07T10:00:01 | today          | today + 60 days | last fetch              |
      #since
      | 10010003 | 2015-12-10     | 2015-12-15     | 2015-12-14T10:00:01 | 2015-12-14     | 2015-12-15      | 2015-12-14T10:00:01     |
      | 10010003 | 2099-12-14     | 2015-12-22     | 2015-12-14T10:00:01 | 2015-12-14     | 2015-12-22      | 2015-12-14T10:00:01     |
      #until
      | 10010003 | 2015-12-16     | 2015-12-15     | 2015-12-14T10:00:01 | 2015-12-16     | 2016-02-12      | 2015-12-14T10:00:01     |
      | 10010003 | 2015-12-16     | 2099-12-15     | 2015-12-14T10:00:01 | 2015-12-16     | 2016-02-12      | 2015-12-14T10:00:01     |
