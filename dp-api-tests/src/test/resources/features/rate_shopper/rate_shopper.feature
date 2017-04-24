Feature: Rate shopper

# GET /rate_shopper/analytics/market

  Background:
    Given Database is cleaned and default entities are created


  Scenario Outline: Checking error codes for missing parameters
    When Getting BAR values for a given market for "<propertyId>" since "<since>" until "<until>"
    Then Response code is "400"
    And Content type is "application/json"
    And Custom code is "40002"
    Examples:
      | propertyId                           | since | until |
      | /null                                | today | today |
      | 98000099-9999-4999-a999-999999999999 | /null | today |
      | 98000099-9999-4999-a999-999999999999 | today | /null |

  Scenario Outline: Checking correct currency parameter returned for market
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId     | name         | propertyCode | website                    | email            | isDemoProperty | timezone      | ttiId | anchorCustomerId                     |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n1  | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz  | true           | Europe/Prague | 0     | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99000299-9999-4999-a999-999999999999 | salesforceid_n2  | pn2_name     | pn2_code     | http://www.snapshot.travel | pn2@tenants.biz  | true           | Europe/Prague | 2     | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99001499-9999-4999-a999-999999999999 | salesforceid_n14 | pn14_name    | pn14_code    | http://www.snapshot.travel | pn14@tenants.biz | true           | Europe/Prague | 14    | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
    When Getting BAR values for a given market for "<propertyId>" since "today" until "today"
    Then Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "currency" value "<expected_currency>"
    Examples:
      | propertyId                           | expected_currency |
      | 99000099-9999-4999-a999-999999999999 | CHF               |
      | 99000299-9999-4999-a999-999999999999 | GBP               |
      | 99001499-9999-4999-a999-999999999999 | EUR               |

#  This test keeps failing on my computer with empty values, altough on others it works. Needs to be reteted
  Scenario Outline: Check minimal, average, and maximal market values
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    And The following properties exist with random address and billing address for user "11111111-0000-4000-a000-000000000000"
      | id                                   | salesforceId    | name         | propertyCode | website                    | email           | isDemoProperty | timezone      | ttiId | anchorCustomerId                     |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 3     | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
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

  Scenario Outline: Checking error codes for analitics/property
    When Getting rate data for "<propertyId>" since "today" until "today" fetched "/null"
    Then Response code is "<responseCode>"
    And Custom code is "<customCode>"
    Examples:
      | propertyId                           | responseCode | customCode |
      | invalid                              | 400          | 40002      |
      | 98000099-9999-4999-a999-999999999999 | 404          | 40402      |

  Scenario Outline: Checking correct currency parameter returned for property
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId     | name         | propertyCode | website                    | email            | isDemoProperty | timezone      | ttiId | anchorCustomerId                     |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n1  | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz  | true           | Europe/Prague | 0     | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99000299-9999-4999-a999-999999999999 | salesforceid_n2  | pn2_name     | pn2_code     | http://www.snapshot.travel | pn2@tenants.biz  | true           | Europe/Prague | 2     | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99001499-9999-4999-a999-999999999999 | salesforceid_n14 | pn14_name    | pn14_code    | http://www.snapshot.travel | pn14@tenants.biz | true           | Europe/Prague | 14    | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
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
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    And The following properties exist with random address and billing address
      | id                                   | salesforceId    | name         | propertyCode | website                    | email           | isDemoProperty | timezone      | ttiId | anchorCustomerId                     |
      | 99000499-9999-4999-a999-999999999999 | salesforceid_n4 | pn4_name     | pn4_code     | http://www.snapshot.travel | pn4@tenants.biz | true           | Europe/Prague | 4     | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
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

#  DP-1982
  @skipped
  Scenario: Getting data for a non-existent property
    When Getting rate data for "00000000-0000-4000-a000-000000000000" since "today" until "today" fetched "/null"
    Then Response code is "404"
    And Custom code is "40402"
    And Content type is "application/json"
    When Getting rate data for "invalidUUID" since "today" until "today" fetched "/null"
    Then Response code is "400"
    And Custom code is "40002"

# GET /rate_shopper/analytics/market/properties

  Scenario Outline: Checking error codes for missing parameters /market/properties
    When List of properties for market of "<propertyId>" is got with limit "/null" and cursor "/null"
    Then Response code is "<ReturnCode>"
    And Content type is "application/json"
    And Custom code is "<CustomCode>"
    Examples:
      | propertyId                           | CustomCode | ReturnCode |
      | /null                                | 40002      | 400        |
      | 98000099-9999-4999-a999-999999999999 | 40402      | 404        |

#  DP-1955
  @skipped
  Scenario Outline: Getting a list of items
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    And The following properties exist with random address and billing address
      | id                                   | salesforceId    | name         | propertyCode | website                    | email           | isDemoProperty | timezone      | ttiId | anchorCustomerId                     |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n0 | pn0_name     | pn0_code     | http://www.snapshot.travel | pn0@tenants.biz | true           | Europe/Prague | 999     | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99002499-9999-4999-a999-999999999999 | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 24    | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99005299-9999-4999-a999-999999999999 | salesforceid_n2 | pn2_name     | pn2_code     | http://www.snapshot.travel | pn2@tenants.biz | true           | Europe/Prague | 52    | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99011299-9999-4999-a999-999999999999 | salesforceid_n3 | pn3_name     | pn3_code     | http://www.snapshot.travel | pn3@tenants.biz | true           | Europe/Prague | 112   | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99031899-9999-4999-a999-999999999999 | salesforceid_n4 | pn4_name     | pn4_code     | http://www.snapshot.travel | pn4@tenants.biz | true           | Europe/Prague | 318   | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99032399-9999-4999-a999-999999999999 | salesforceid_n5 | pn5_name     | pn5_code     | http://www.snapshot.travel | pn5@tenants.biz | true           | Europe/Prague | 323   | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99038399-9999-4999-a999-999999999999 | salesforceid_n6 | pn6_name     | pn6_code     | http://www.snapshot.travel | pn6@tenants.biz | true           | Europe/Prague | 383   | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
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
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    And The following properties exist with random address and billing address
      | id                                   | salesforceId    | name         | propertyCode | website                    | email           | isDemoProperty | timezone      | ttiId | anchorCustomerId                     |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 0     | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
    When List of properties for market of "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"
    Examples:
      | property                             | limit       | cursor | response_code | custom_code |
      | 99000099-9999-4999-a999-999999999999 | /null       | -1     | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 |             | -1     | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 | /null       | text   | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 |             | text   | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 | -1          |        | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 | -1          | /null  | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 | 201         | /null  | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 | 21474836470 | /null  | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 | text        |        | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 | text        | /null  | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 | 10          | -1     | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 | text        | 0      | 400           | 40002       |
      | 99000099-9999-4999-a999-999999999999 | 10          | text   | 400           | 40002       |

#  DP-1955
  @skipped
  Scenario Outline: Given property in future or without fetchDatetime are calculated real time
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    And The following properties exist with random address and billing address
      | id                                   | salesforceId    | name         | propertyCode | website                    | email           | isDemoProperty | timezone      | ttiId | anchorCustomerId                     |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n0 | pn0_name     | pn0_code     | http://www.snapshot.travel | pn0@tenants.biz | true           | Europe/Prague | 3     | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99002499-9999-4999-a999-999999999999 | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 11    | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99005299-9999-4999-a999-999999999999 | salesforceid_n2 | pn2_name     | pn2_code     | http://www.snapshot.travel | pn2@tenants.biz | true           | Europe/Prague | 25    | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99011299-9999-4999-a999-999999999999 | salesforceid_n3 | pn3_name     | pn3_code     | http://www.snapshot.travel | pn3@tenants.biz | true           | Europe/Prague | 170   | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99031899-9999-4999-a999-999999999999 | salesforceid_n4 | pn4_name     | pn4_code     | http://www.snapshot.travel | pn4@tenants.biz | true           | Europe/Prague | 186   | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99032399-9999-4999-a999-999999999999 | salesforceid_n5 | pn5_name     | pn5_code     | http://www.snapshot.travel | pn5@tenants.biz | true           | Europe/Prague | 199   | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 99038399-9999-4999-a999-999999999999 | salesforceid_n6 | pn6_name     | pn6_code     | http://www.snapshot.travel | pn6@tenants.biz | true           | Europe/Prague | 207   | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
    When List of properties for market of "<property>" is got with limit "/null" and cursor "/null" fetched "<fetch_datetime>"
    And Content type is "application/json"
    And Response code is "200"
    Then Response contains <count> properties
    And Body contains entity with attribute "fetch_datetime"
    #fetch is always done 1st and 15th in month
    #fetch for not existing datetime or null is recalculated on the fly
#    Property 99000099-9999-4999-a999-999999999999 has d_city=9 and stars = 3 in stg_d_hotel table. We need to select similar hotels (same city and no of stars) whose ids are present in stg_d_hotel_count table
    Examples:
      | property                             | count | fetch_datetime      |
      | 99000099-9999-4999-a999-999999999999 | 6     | 2016-02-20T00:00:00 |
      | 99000099-9999-4999-a999-999999999999 | 6     | /null               |

  Scenario Outline: Checking error codes for missing headers
    When GET request is sent to "<url>" on module "rate_shopper" without X-Auth-AppId header
    Then Response code is "403"
    And Custom code is "40301"
    When GET request is sent to "<url>" on module "rate_shopper" without X-Auth-UserId header
    Then Response code is "403"
    And Custom code is "40301"
    Examples:
      | url                                       |
      | /rate_shopper/analytics/market            |
      | /rate_shopper/analytics/market/properties |
      | /rate_shopper/analytics/property/anyId    |


