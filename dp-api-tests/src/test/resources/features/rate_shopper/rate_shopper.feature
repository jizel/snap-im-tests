Feature: Rate shopper

# GET /rate_shopper/analytics/market

  Scenario Outline: Checking error codes for missing parameters
    When Getting BAR values for a given market for "<propertyId>" since "<since>" until "<until>"
    Then Response code is "400"
    And Content type is "application/json"
    And Custom code is "52"

    Examples:
      | propertyId                           | since | until |
      | /null                                | today | today |
      | 98000099-9999-4999-a999-999999999999 | /null | today |
      | 98000099-9999-4999-a999-999999999999 | today | /null |

  Scenario Outline: Checking correct currency parameter returned for market
    Given Database is cleaned
    And The following properties exist with random address and billing address
      | propertyId                           | salesforceId     | propertyName | propertyCode | website                    | email            | isDemoProperty | timezone      | ttiId |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n1  | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz  | true           | Europe/Prague | 0     |
      | 99000299-9999-4999-a999-999999999999 | salesforceid_n2  | pn2_name     | pn2_code     | http://www.snapshot.travel | pn2@tenants.biz  | true           | Europe/Prague | 2     |
      | 99001499-9999-4999-a999-999999999999 | salesforceid_n14 | pn14_name    | pn14_code    | http://www.snapshot.travel | pn14@tenants.biz | true           | Europe/Prague | 14    |

    When Getting BAR values for a given market for "<propertyId>" since "today" until "today"
    Then Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "currency" value "<expected_currency>"

    Examples:
      | propertyId                           | expected_currency |
      | 99000099-9999-4999-a999-999999999999 | CHF               |
      | 99000299-9999-4999-a999-999999999999 | GBP               |
      | 99001499-9999-4999-a999-999999999999 | EUR               |

  Scenario Outline: Check minimal, average, and maximal market values
    Given Database is cleaned
    And The following properties exist with random address and billing address
      | propertyId                           | salesforceId    | propertyName | propertyCode | website                    | email           | isDemoProperty | timezone      | ttiId |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 0     |

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
      | property                             | since | until            |
      | 99000099-9999-4999-a999-999999999999 | today | today + 1 day    |
      | 99000099-9999-4999-a999-999999999999 | today | today + 20 days  |
      | 99000099-9999-4999-a999-999999999999 | today | today + 100 days |


# GET /rate_shopper/analytics/property/{id}

  @issue DP-1262

  Scenario Outline: Checking error codes for analytics data
    When Sending an empty request to "<url>"
    Then Response code is "404"
    And Content type is "application/json"
    And Custom code is "152"

    Examples:
      | url                                      |
      | /rate_shopper/analytics/property/invalid |

  Scenario Outline: Checking correct currency parameter returned for property
    Given Database is cleaned
    And The following properties exist with random address and billing address
      | propertyId                           | salesforceId     | propertyName | propertyCode | website                    | email            | isDemoProperty | timezone      | ttiId |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n1  | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz  | true           | Europe/Prague | 0     |
      | 99000299-9999-4999-a999-999999999999 | salesforceid_n2  | pn2_name     | pn2_code     | http://www.snapshot.travel | pn2@tenants.biz  | true           | Europe/Prague | 2     |
      | 99001499-9999-4999-a999-999999999999 | salesforceid_n14 | pn14_name    | pn14_code    | http://www.snapshot.travel | pn14@tenants.biz | true           | Europe/Prague | 14    |

    When Getting rate data for "<propertyId>" since "today" until "today" fetched "/null"
    Then Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "currency" value "<expected_currency>"

    Examples:
      | propertyId                           | expected_currency |
      | 99000099-9999-4999-a999-999999999999 | CHF               |
      | 99000299-9999-4999-a999-999999999999 | GBP               |
      | 99001499-9999-4999-a999-999999999999 | EUR               |


  Scenario Outline: Get BAR values for a given property analytics data from API
    Given Database is cleaned
    And The following properties exist with random address and billing address
      | propertyId                           | salesforceId    | propertyName | propertyCode | website                    | email           | isDemoProperty | timezone      | ttiId |
      | 99000499-9999-4999-a999-999999999999 | salesforceid_n4 | pn4_name     | pn4_code     | http://www.snapshot.travel | pn4@tenants.biz | true           | Europe/Prague | 4     |

    When Getting rate data for "<property>" since "<since>" until "<until>" fetched "<fetch_datetime>"
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "since"
    And Body contains entity with attribute "until"
    And Body contains entity with attribute "fetch_datetime"
    And Body contains entity with attribute "currency"
    And Response contains <count> values

    Examples:
      | property                             | since | until             | count | fetch_datetime      |
      | 99000499-9999-4999-a999-999999999999 | today | today             | 1     | 2015-12-14T00:00:01 |
      | 99000499-9999-4999-a999-999999999999 | today | today + 6 days    | 7     | 2015-12-14T00:00:01 |
      | 99000499-9999-4999-a999-999999999999 | today | today + 10 months | 60    | 2015-12-14T00:00:01 |

  Scenario: Getting data for a non-existent property
    When Getting rate data for "non-existent" since "today" until "today" fetched "/null"
    Then Response code is "404"
    And Custom code is "152"
    And Content type is "application/json"

# GET /rate_shopper/analytics/market/properties

  Scenario Outline: Checking error codes for missing parameters /market/properties
    When List of properties for market of "<propertyId>" is got with limit "/null" and cursor "/null"
    Then Response code is "<ReturnCode>"
    And Content type is "application/json"
    And Custom code is "<CustomCode>"

    Examples:
      | propertyId                           | CustomCode | ReturnCode |
      | /null                                | 52         | 400        |
      | 98000099-9999-4999-a999-999999999999 | 152        | 404        |

  Scenario Outline: Getting a list of items
    Given Database is cleaned
    And The following properties exist with random address and billing address
      | propertyId                           | salesforceId    | propertyName | propertyCode | website                    | email           | isDemoProperty | timezone      | ttiId |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n0 | pn0_name     | pn0_code     | http://www.snapshot.travel | pn0@tenants.biz | true           | Europe/Prague | 0     |
      | 99002499-9999-4999-a999-999999999999 | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 24    |
      | 99005299-9999-4999-a999-999999999999 | salesforceid_n2 | pn2_name     | pn2_code     | http://www.snapshot.travel | pn2@tenants.biz | true           | Europe/Prague | 52    |
      | 99011299-9999-4999-a999-999999999999 | salesforceid_n3 | pn3_name     | pn3_code     | http://www.snapshot.travel | pn3@tenants.biz | true           | Europe/Prague | 112   |
      | 99031899-9999-4999-a999-999999999999 | salesforceid_n4 | pn4_name     | pn4_code     | http://www.snapshot.travel | pn4@tenants.biz | true           | Europe/Prague | 318   |
      | 99032399-9999-4999-a999-999999999999 | salesforceid_n5 | pn5_name     | pn5_code     | http://www.snapshot.travel | pn5@tenants.biz | true           | Europe/Prague | 323   |
      | 99038399-9999-4999-a999-999999999999 | salesforceid_n6 | pn6_name     | pn6_code     | http://www.snapshot.travel | pn6@tenants.biz | true           | Europe/Prague | 383   |

    When List of properties for market of "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are at most <count> items returned
    And Body contains entity with attribute "fetch_datetime"

    Examples:
      | property                             | limit | cursor | count |
      | 99000099-9999-4999-a999-999999999999 |       |        | 50    |
      | 99000099-9999-4999-a999-999999999999 | 51    |        | 50    |
      | 99000099-9999-4999-a999-999999999999 |       | 1      | 50    |
      | 99000099-9999-4999-a999-999999999999 | 20    | 0      | 20    |
      | 99000099-9999-4999-a999-999999999999 | 49    | 0      | 49    |
      | 99000099-9999-4999-a999-999999999999 | 5     | 5      | 5     |

  Scenario Outline: Checking error codes for getting list of properties in one market
    Given Database is cleaned
    And The following properties exist with random address and billing address
      | propertyId                           | salesforceId    | propertyName | propertyCode | website                    | email           | isDemoProperty | timezone      | ttiId |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 0     |

    When List of properties for market of "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | property                             | limit       | cursor | response_code | custom_code |
      | 99000099-9999-4999-a999-999999999999 | /null       | -1     | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 |             | -1     | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 | /null       | text   | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 |             | text   | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 | -1          |        | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 | -1          | /null  | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 | 201         | /null  | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 | 21474836470 | /null  | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 | text        |        | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 | text        | /null  | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 | 10          | -1     | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 | text        | 0      | 400           | 63          |
      | 99000099-9999-4999-a999-999999999999 | 10          | text   | 400           | 63          |

  Scenario Outline: Given property in future or without fetchDatetime are calculated real time
    Given Database is cleaned
    And The following properties exist with random address and billing address
      | propertyId                           | salesforceId    | propertyName | propertyCode | website                    | email           | isDemoProperty | timezone      | ttiId |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n0 | pn0_name     | pn0_code     | http://www.snapshot.travel | pn0@tenants.biz | true           | Europe/Prague | 000   |
      | 99002099-9999-4999-a999-999999999999 | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 020   |
      | 99005299-9999-4999-a999-999999999999 | salesforceid_n2 | pn2_name     | pn2_code     | http://www.snapshot.travel | pn2@tenants.biz | true           | Europe/Prague | 052   |
      | 99005899-9999-4999-a999-999999999999 | salesforceid_n3 | pn3_name     | pn3_code     | http://www.snapshot.travel | pn3@tenants.biz | true           | Europe/Prague | 058   |
      | 99011099-9999-4999-a999-999999999999 | salesforceid_n4 | pn4_name     | pn4_code     | http://www.snapshot.travel | pn4@tenants.biz | true           | Europe/Prague | 110   |
      | 99015899-9999-4999-a999-999999999999 | salesforceid_n5 | pn5_name     | pn5_code     | http://www.snapshot.travel | pn5@tenants.biz | true           | Europe/Prague | 158   |
      | 99021299-9999-4999-a999-999999999999 | salesforceid_n6 | pn6_name     | pn6_code     | http://www.snapshot.travel | pn6@tenants.biz | true           | Europe/Prague | 212   |

    When List of properties for market of "<property>" is got with limit "/null" and cursor "/null" fetched "<fetch_datetime>"
    Then Response contains <count> properties
    And Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "fetch_datetime"

    #fetch is always done 1st and 15th in month
    #fetch for not existing datetime or null is recalculated on the fly
    Examples:
      | property                             | count | fetch_datetime      |
      | 99000099-9999-4999-a999-999999999999 | 6     | 2016-02-20T00:00:00 |
      | 99000099-9999-4999-a999-999999999999 | 6     | /null               |


