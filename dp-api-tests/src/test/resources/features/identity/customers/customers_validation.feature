Feature: customers_validation

  Background:
    Given Database is cleaned
    Given the location "identity/customers" for object "customer"
    Given unique identifier "customer_id" for object "customer"
    Given the following "customer" object definition
      | path                   | type   | required | correct                                                      | invalid   | longer   |
      #----------------------------------------------------------------------------------------------------------------------------------------------------------
      | /code                  | String | true     | \w{50}                                                       | /null     | \w{256}  |
      | /company_name          | String | true     | \w{255}                                                      | /null     | \w{256}  |
      | /salesforce_id         | String | false    | \w{100}                                                      | /null     | \w{101}  |
      | /vat_id                | String | true     | CZ[0-9]{9}                                                   | /null     | \w{101}  |
      | /website               | String | false    | http:\/\/[a-z0-9]{63}\.com                                   | \.{10}    | \w{1001} |
      | /email                 | String | true     | (([a-z]\|\d){9}\.){4}(\w\|\d){10}\@(([a-z]\|\d){9}\.){4}com | \.{10}    | \w{101}  |
      | /phone                 | String | false    | +[0-9]{12}                                                   | \.{10}    | \w{101}  |
      | /is_demo_customer      | Bool   | true     | (true\|false)                                                | /null     |          |
      #| /is_active             | String | false    | (0\|1)                                                       | x         |          |
      | /notes                 | String | false    | \w{255}                                                      | /null     | \w{256}  |
      | /timezone              | String | true     | (America/New_York\|Europe/Prague)                            | UTC+01:00 |          |
      | /address/address_line1 | String | true     | \w{100}                                                      | /null     | \w{101}  |
      | /address/address_line2 | String | false    | \w{100}                                                      | /null     | \w{101}  |
      | /address/city          | String | true     | \w{50}                                                       | /null     | \w{51}   |
      | /address/zip_code      | String | true     | [a-zA-Z0-9]{10}                                              | /null     | \w{11}   |
      | /address/country       | String | true     | US                                                           | xx        | USA      |

  # --- happy path ---

  Scenario: Object creation - correct values
    When create "customer" object with correct field values
    Then Response code is "201"
    And location header is set and points to the same object
    And returned "customer" object matches

  Scenario: Object update - correct values
    When update "customer" object with correct field values
    Then Response code is "204"
    And returned "customer" object matches

  Scenario: Object update - correct values one by one
    When update "customer" objects each with one correct field value
    Then there are following responses
      | testedField      | responseCode |
      | /website         | 204          |
      | /email           | 204          |
      | /phone           | 204          |
      | /address/country | 204          |
      | /timezone        | 204          |


  Scenario: Object filtering
    When create 50 "customer" objects
    Then filtering by top-level fields returns matching "customer" objects

  # --- error handling ---
  Scenario: Object creation - invalid values
    When create "customer" objects each with one invalid field value
    Then there are following responses
      | testedField      | responseCode | customCode |
      | /website         | 400          | 59         |
      | /email           | 400          | 59         |
      | /phone           | 400          | 59         |
      | /timezone        | 400          | 59         |
      | /address/country | 400          | 59         |

  Scenario: Object creation - missing values
    When create "customer" objects each with one missing field
    Then there are following responses
      | testedField            | responseCode | customCode |
      | /code                  | 400          | 53         |
      | /company_name          | 400          | 53         |
      | /email                 | 400          | 53         |
      | /timezone              | 400          | 53         |
      | /is_demo_customer      | 400          | 53         |
      | /address/address_line1 | 400          | 53         |
      | /address/city          | 400          | 53         |
      | /address/zip_code      | 400          | 53         |
      | /address/country       | 400          | 53         |

  Scenario: Object update - invalid values
    When update "customer" objects each with one invalid field value
    Then there are following responses
      | testedField      | responseCode | customCode |
      | /website         | 400          | 59         |
      | /email           | 400          | 59         |
      | /phone           | 400          | 59         |
      | /address/country | 400          | 59         |

#   TODO when field lengths are stabilized
#
#   Scenario: Object creation - long values
