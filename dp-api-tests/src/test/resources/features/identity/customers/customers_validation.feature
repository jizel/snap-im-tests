Feature: Customers validation

  Background:
    Given Database is cleaned
    Given the location "identity/customers" for object "customer"
    Given unique identifier "customer_id" for object "customer"
    Given the following "customer" object definition
      | path                   | type   | required | correct                                                     | invalid   | longer   |
      #----------------------------------------------------------------------------------------------------------------------------------------------------------
      | /customer_code         | String | true     | \w{50}                                                      | /null     | \w{256}  |
      | /name                  | String | true     | \w{255}                                                     | /null     | \w{256}  |
      | /salesforce_id         | String | false    | \w{100}                                                     | /null     | \w{101}  |
      | /vat_id                | String | true     | CZ[0-9]{9}                                                  | /null     | \w{101}  |
      | /website               | String | false    | http:\/\/[a-z0-9]{63}\.com                                  | \.{10}    | \w{1001} |
      | /email                 | String | true     | (([a-z]\|\d){9}\.){4}(\w\|\d){10}\@(([a-z]\|\d){9}\.){4}com | \.{10}    | \w{101}  |
      | /phone                 | String | false    | +[0-9]{12}                                                  | \.{10}    | \w{101}  |
      | /is_demo_customer      | Bool   | true     | (true\|false)                                               | /null     |          |
      #| /is_active             | String | false    | (0\|1)                                                       | x         |          |
      | /notes                 | String | false    | \w{255}                                                     | /null     | \w{256}  |
      | /headquarters_timezone | String | true     | (America/New_York\|Europe/Prague)                           | UTC+01:00 |          |
      | /address/address_line1 | String | true     | \w{100}                                                     | /null     | \w{101}  |
      | /address/address_line2 | String | false    | \w{100}                                                     | /null     | \w{101}  |
      | /address/city          | String | true     | \w{50}                                                      | /null     | \w{51}   |
      | /address/zip_code      | String | true     | [a-zA-Z0-9]{10}                                             | /null     | \w{11}   |
      | /address/country       | String | true     | US                                                          | xx        | USA      |

    Given The following customers exist with random address
      | companyName          | email                   | code        | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | Validation company 1 | validation1@tenants.biz | validation1 | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |

  # --- happy path ---

  @Smoke
  Scenario: Object creation - correct values
    When create "customer" object with correct field values
    Then Response code is "201"
    And location header is set and points to the same object
    And returned "customer" object matches

  @Smoke
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
      | /address/country | 400          | 63         |

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
      | /address/country | 400          | 63         |

  Scenario Outline: Object update - customer - invalid values
    When Update customer with code "validation1", field "<updated_field>", its value "<value>"
    Then Response code is 400
    And Custom code is "<custom_code>"
    Examples:
      | updated_field | custom_code | value            |
      | timezone      | 59          | invalid_timezone |
      | timezone      | 59          | UTC+01:00        |
      | phone         | 59          | invalid_phone    |
      | phone         | 59          | 123              |
      | email         | 59          | invalid_email    |
      | email         | 59          | @invalid_email   |
      | vatId         | 59          | invalid_vatId    |
      | vatId         | 59          | @\/*             |
      | website       | 59          | invalid_web      |
      | website       | 59          | www.snapshot.com |

  Scenario Outline: Object update - customer's address - invalid values
    When Update customer with code "validation1", address field "<address_field>", its value "<value>"
    Then Response code is 400
    And Custom code is "<custom_code>"
    Examples:
      | address_field | value                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | custom_code |
      | country       | XX                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 63          |
      | country       |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | 61          |
      | city          |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | 61          |
      | city          | 1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901                                                                                                                                                                                                         | 63          |
      | zipCode       |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | 61          |
      | zipCode       | 12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901                                                                                                                                                                                                                                                                                                                                                                                                                 | 63          |
      | region        | 12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901                                                                                                                                                                                                                                                                                                                                                                                                                 | 63          |
      | addressLine2  | 123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901 | 63          |
      | addressLine1  |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | 61          |
      | addressLine1  | 123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901 | 63          |

  Scenario Outline: Object update - customer's address - valid values
    When Update customer with code "validation1", address field "<address_field>", its value "<value>"
    Then Response code is 204
    And Body is empty
    And Etag header is present
    Examples:
      | address_field | value                       |
      | country       | CZ                          |
      | country       | US                          |
      | country       | EG                          |
      | country       | CN                          |
      | city          | Prague                      |
      | city          | NewYork1                    |
      | city          | 上海市                         |
      | zipCode       | 60200                       |
      | zipCode       | ThisIsAnAwesomeValidZipCode |
      | region        |                             |
      | addressLine2  |                             |
      | addressLine2  | AddressLineNumberTwo        |
      | addressLine2  | 1                           |
      | addressLine1  | 1                           |
      | addressLine1  | AddressLineNumberOne        |
      | addressLine1  | 无锡市 99/1A-BC                |

  Scenario: Object update - US customer's region
    When Update customer with code "validation1", address field "country", its value "US"
    When Update customer with code "validation1", address field "region", its value "Texas"
    Then Response code is 204
    And Body is empty
    And Etag header is present

  Scenario Outline: Object update - customer - valid values
    When Update customer with code "validation1", field "<updated_field>", its value "<value>"
    Then Response code is 204
    And  Body is empty
    Examples:
      | updated_field         | value                 |
      | headquarters_timezone | Pacific/Fiji          |
      | headquarters_timezone | GMT                   |
      | phone                 | +42098765432          |
      | email                 | valid@snapshot.travel |
      | vatId                 | CZ98765432            |
      | website               | http://snapshot.com   |
