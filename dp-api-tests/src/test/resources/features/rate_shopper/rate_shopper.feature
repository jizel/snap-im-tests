Feature: rate_shopper

  Scenario Outline: Checking error codes for analytics data
    When Sending an empty request to "<url>"
    Then Response code is "<error_code>"
    And Content type is "application/json"
    And Custom code is "<custom_code>"

    Examples:
      | url                                        | error_code | custom_code |
      | /rate_shopper/analytics/property/invalid   | 404        | 152         |
      | /rate_shopper/analytics/market/            | 404        | 152         |
      | /rate_shopper/analytics/market/properties/ | 404        | 152         |

# GET /rate_shopper/analytics/market

  Scenario Outline: Checking correct currency parameter returned for market
    When Getting BAR values for a given market for "<property>" since "/null" until "/null"
    Then Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "currency" value "<expected_currency>"

    Examples:
      | property | expected_currency  |
      | 10010003 | EUR                |
      | 10030030 | GBP                |
      | 10023363 | CHF                |


  Scenario Outline: Check minimal, average, and maximal market values
    When Getting BAR values for a given market for "<property>" since "<since>" until "<until>"
    Then Response code is "200"
    And Content type is "application/json"
    And "minimal" values are not more than "average" values
    And "average" values are not more than "maximal" values
    And Body contains entity with attribute "since"
    And Body contains entity with attribute "until"
    And Body contains entity with attribute "fetch_datetime"
    And Body contains entity with attribute "currency"

    Examples: 
      | property | since      | until      |
      | 10010003 | 2015-12-14 | 2015-12-14 |
      | 10010003 | 2015-12-10 | 2015-12-10 |
      | 10010003 | 2015-12-14 | 2015-12-30 |
      | 10010003 | 2015-12-14 | 2015-12-20 |
      | 10010003 | 2015-12-10 | 2015-12-20 |
      | 10010003 | 2015-12-14 | 2015-12-30 |
      | 10010003 | 2015-11-08 | 2015-12-07 |
      | 10010003 | 2015-02-08 | 2015-03-30 |
      | 10010003 | 2013-02-08 | 2016-12-31 |
      | 10010004 | 2015-12-14 | 2015-12-14 |
      | 10010004 | 2015-12-10 | 2015-12-10 |
      | 10010004 | 2015-12-14 | 2015-12-30 |
      | 10010004 | 2015-12-14 | 2015-12-20 |
      | 10010004 | 2015-12-10 | 2015-12-20 |
      | 10010004 | 2015-12-14 | 2015-12-30 |
      | 10010004 | 2015-11-08 | 2015-12-07 |
      | 10010004 | 2015-02-08 | 2015-03-30 |
      | 10010004 | 2013-02-08 | 2016-12-31 |
      | 10010007 | 2015-12-14 | 2015-12-14 |
      | 10010007 | 2015-12-10 | 2015-12-10 |
      | 10010007 | 2015-12-14 | 2015-12-30 |
      | 10010007 | 2015-12-14 | 2015-12-20 |
      | 10010007 | 2015-12-10 | 2015-12-20 |
      | 10010007 | 2015-12-14 | 2015-12-30 |
      | 10010007 | 2015-11-08 | 2015-12-07 |
      | 10010007 | 2015-02-08 | 2015-03-30 |
      | 10010007 | 2013-02-08 | 2016-12-31 |

# GET /rate_shopper/analytics/property/{id}

  Scenario Outline: Checking correct currency parameter returned for property
    When Getting rate data for "<property>" since "/null" until "/null" fetched "/null"
    Then Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "currency" value "<expected_currency>"

    Examples:
      | property | expected_currency  |
      | 10010003 | EUR                |
      | 10030030 | GBP                |
      | 10023363 | CHF                |

  Scenario Outline: Get BAR values for a given property analytics data from API
    When Getting rate data for "<property>" since "<since>" until "<until>" fetched "<fetch_datetime>"
    Then Response contains <count> values
    And Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "since"
    And Body contains entity with attribute "until"
    And Body contains entity with attribute "fetch_datetime"
    And Body contains entity with attribute "currency"

    Examples:
      | property | since      | until      | count | fetch_datetime      |
      | 10010004 | 2015-12-14 | 2015-12-14 | 1     | 2015-12-14T00:00:01 |
      | 10010004 | 2015-12-14 | 2015-12-20 | 7     | 2015-12-14T00:00:01 |
      | 10010004 | 2015-12-14 | 2016-12-31 | 60    | 2015-12-14T00:00:01 |
      | 10010003 | 2015-12-14 | 2015-12-17 | 4     | 2015-12-14T00:00:01 |
      | 10010003 | 2015-12-14 | 2015-12-20 | 7     | 2015-12-14T00:00:01 |
      | 10010003 | 2015-12-14 | 2016-12-29 | 60    | 2015-12-14T00:00:01 |
      | 10010007 | 2015-12-14 | 2015-12-14 | 1     | 2015-12-14T00:00:01 |
      | 10010007 | 2015-12-09 | 2015-12-20 | 12    | 2015-12-14T00:00:01 |
      | 10010007 | 2015-12-14 | 2016-12-31 | 60    | 2015-12-14T00:00:01 |

  Scenario: Getting data for a non-existent property
    When Getting rate data for "non-existent" since "2015-09-01" until "2015-09-01" fetched ""
    Then Content type is "application/json"
    And Response code is "404"
    And Custom code is "152"

  Scenario Outline: Checking default parameter values
    When Getting rate data for "<property>" since "<since>" until "<until>" fetched "<fetch_datetime>"
    Then Content type is "application/json"
    And Response code is "200"
    And Response "since" for property "<property>" is "<expected_since>"
    And Response "until" for property "<property>" is "<expected_until>"

    Examples: 
      | property | since          | until          | fetch_datetime      | expected_since  | expected_until  |
      | 10010003 |                | 2015-12-30     | 2015-12-14T00:00:01 | today           | today + 59 days |
      | 10010003 | 2015-12-22     |                | 2015-12-14T00:00:01 | 2015-12-22      | 2016-02-19      |
      | 10010003 | 2013-12-15     | today + 3 days |                     | last_fetch_date | today + 3 days  |
      | 10010003 |                |                | 2015-12-14T00:00:01 | today           | today + 59 days |
      | 10010003 |                | 2015-12-15     |                     | today           | today + 59 days |
      | 10010003 |                | today + 2 days |                     | today           | today + 2 days  |
      | 10010003 | 2013-12-15     | today + 3 days |                     | last_fetch_date | today + 3 days  |
      | 10010003 | today + 3 days |                |                     | today + 3 days  | today + 59 days |
      | 10010003 |                |                |                     | today           | today + 59 days |
      | 10010003 | 2013-12-15     | today + 3 days | 2099-12-07T00:00:01 | last_fetch_date | today + 3 days  |
      | 10010003 | 2013-07-07     | 2015-12-15     | 2015-12-14T00:00:01 | last_fetch_date | 2015-12-15      |
      | 10010003 | 2099-12-14     | 2015-12-22     | 2015-12-14T00:00:01 | last_fetch_date | 2015-12-22      |
      | 10010003 | 2015-12-16     | 2015-12-15     | 2015-12-14T00:00:01 | 2015-12-16      | 2016-02-13      |
      | 10010003 | 2015-12-16     | 2099-12-15     | 2015-12-14T00:00:01 | 2015-12-16      | 2016-02-13      |
      | 10010004 |                | 2015-12-30     | 2015-12-14T00:00:01 | today           | today + 59 days |
      | 10010004 | 2015-12-22     |                | 2015-12-14T00:00:01 | 2015-12-22      | 2016-02-19      |
      | 10010004 | 2013-12-15     | today + 3 days |                     | last_fetch_date | today + 3 days  |
      | 10010004 |                |                | 2015-12-14T00:00:01 | today           | today + 59 days |
      | 10010004 |                | 2015-12-15     |                     | today           | today + 59 days |
      | 10010004 |                | today + 2 days |                     | today           | today + 2 days  |
      | 10010004 | 2013-12-15     | today + 3 days |                     | last_fetch_date | today + 3 days  |
      | 10010004 | today + 3 days |                |                     | today + 3 days  | today + 59 days |
      | 10010004 |                |                |                     | today           | today + 59 days |
      | 10010004 | 2013-12-15     | today + 3 days | 2099-12-07T00:00:01 | last_fetch_date | today + 3 days  |
      | 10010004 | 2013-07-07     | 2015-12-15     | 2015-12-14T00:00:01 | last_fetch_date | 2015-12-15      |
      | 10010004 | 2099-12-14     | 2015-12-22     | 2015-12-14T00:00:01 | last_fetch_date | 2015-12-22      |
      | 10010004 | 2015-12-16     | 2015-12-15     | 2015-12-14T00:00:01 | 2015-12-16      | 2016-02-13      |
      | 10010004 | 2015-12-16     | 2099-12-15     | 2015-12-14T00:00:01 | 2015-12-16      | 2016-02-13      |
      | 10010007 |                | 2015-12-30     | 2015-12-14T00:00:01 | today           | today + 59 days |
      | 10010007 | 2015-12-22     |                | 2015-12-14T00:00:01 | 2015-12-22      | 2016-02-19      |
      | 10010007 | 2013-12-15     | today + 3 days |                     | last_fetch_date | today + 3 days  |
      | 10010007 |                |                | 2015-12-14T00:00:01 | today           | today + 59 days |
      | 10010007 |                | 2015-12-15     |                     | today           | today + 59 days |
      | 10010007 |                | today + 2 days |                     | today           | today + 2 days  |
      | 10010007 | 2013-12-15     | today + 3 days |                     | last_fetch_date | today + 3 days  |
      | 10010007 | today + 3 days |                |                     | today + 3 days  | today + 59 days |
      | 10010007 |                |                |                     | today           | today + 59 days |
      | 10010007 | 2013-12-15     | today + 3 days | 2099-12-07T00:00:01 | last_fetch_date | today + 3 days  |
      | 10010007 | 2013-07-07     | 2015-12-15     | 2015-12-14T00:00:01 | last_fetch_date | 2015-12-15      |
      | 10010007 | 2099-12-14     | 2015-12-22     | 2015-12-14T00:00:01 | last_fetch_date | 2015-12-22      |
      | 10010007 | 2015-12-16     | 2015-12-15     | 2015-12-14T00:00:01 | 2015-12-16      | 2016-02-13      |
      | 10010007 | 2015-12-16     | 2099-12-15     | 2015-12-14T00:00:01 | 2015-12-16      | 2016-02-13      |

# GET /rate_shopper/analytics/market/properties

  Scenario Outline: Getting a list of items
    When List of properties for market of "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are at most <count> items returned
    And Body contains entity with attribute "fetch_datetime"

    Examples:
      | property | limit | cursor | count |
      | 10010003 |       |        | 50    |
      | 10010003 | 51    |        | 50    |
      | 10010003 |       | 1      | 50    |
      | 10010003 | 20    | 0      | 20    |
      | 10010003 | 49    | 0      | 49    |
      | 10010003 | 5     | 5      | 5     |
      | 10010004 |       |        | 50    |
      | 10010004 | 51    |        | 50    |
      | 10010004 |       | 1      | 50    |
      | 10010004 | 20    | 0      | 20    |
      | 10010004 | 49    | 0      | 49    |
      | 10010004 | 5     | 5      | 5     |
      | 10010007 |       |        | 50    |
      | 10010007 | 51    |        | 50    |
      | 10010007 |       | 1      | 50    |
      | 10010007 | 20    | 0      | 20    |
      | 10010007 | 49    | 0      | 49    |
      | 10010007 | 5     | 5      | 5     |

  Scenario Outline: Checking error codes for getting list of properties in one market
    When List of properties for market of "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | property | limit       | cursor | response_code | custom_code |
      | 10010004 | /null       | -1     | 400           | 63          |
      | 10010004 |             | -1     | 400           | 63          |
      | 10010004 | /null       | text   | 400           | 63          |
      | 10010004 |             | text   | 400           | 63          |
      | 10010004 | -1          |        | 400           | 63          |
      | 10010004 | -1          | /null  | 400           | 63          |
      | 10010004 | 201         | /null  | 400           | 63          |
      | 10010004 | 21474836470 | /null  | 400           | 63          |
      | 10010004 | text        |        | 400           | 63          |
      | 10010004 | text        | /null  | 400           | 63          |
      | 10010004 | 10          | -1     | 400           | 63          |
      | 10010004 | text        | 0      | 400           | 63          |
      | 10010004 | 10          | text   | 400           | 63          |


  Scenario Outline: Given property in future or without fetchDatetime are calculated real time
    When List of properties for market of "<property>" is got with limit "/null" and cursor "/null" fetched "<fetch_datetime>"
    Then Response contains <count> properties
    And Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "fetch_datetime"
    And Body contains entity with attribute "currency"

    #fetch is always done 1st and 15th in month, last fetch time in DB was at 2016-02-01
    #fetch in future should return same return as request without fetch
    Examples:
      | property | count | fetch_datetime      |
      | 10010004 | 31    | 2016-02-20T00:00:00 |
      | 10010004 | 31    | null                |


