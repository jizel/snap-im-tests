@Identity
Feature: Customers validation

  Background:
    Given Database is cleaned and default entities are created

    Given the location "identity/customers" for object "customer"
    Given unique identifier "customer_id" for object "customer"
    Given the following "customer" object definition
      | path                   | type   | required | correct                                                     | invalid   | longer      |
      #----------------------------------------------------------------------------------------------------------------------------------------------------------
      | /name                  | String | true     | \w{255}                                                     | /null     | \w{256}     |
      | /salesforce_id         | String | false    | DEFAULTSFID0001                                             | /null     | \w{101}     |
      | /vat_id                | String | true     | CZ[0-9]{9}                                                  | /null     | \w{101}     |
      | /website               | String | false    | http:\/\/[a-z0-9]{63}\.com                                  | \.{10}    | \w{1001}    |
      | /email                 | String | true     | (([a-z]\|\d){9}\.){4}(\w\|\d){10}\@(([a-z]\|\d){9}\.){4}com | \.{10}    | \w{101}     |
      | /phone                 | String | false    | +[0-9]{12}                                                  | \.{10}    | \w{101}     |
      | /is_demo_customer      | Bool   | true     | (true\|false)                                               | /null     |             |
      #| /is_active             | String | false    | (0\|1)                                                       | x         |          |
      | /notes                 | String | false    | \w{255}                                                     | /null     | \w{256}     |
      | /headquarters_timezone | String | true     | (America/New_York\|Europe/Prague)                           | UTC+01:00 |             |
      | /address/address_line1 | String | true     | \w{100}                                                     | /null     | \w{101}     |
      | /address/address_line2 | String | false    | \w{100}                                                     | /null     | \w{101}     |
      | /address/city          | String | true     | \w{50}                                                      | /null     | \w{51}      |
      | /address/zip_code      | String | true     | [a-zA-Z0-9]{10}                                             | /null     | \w{11}      |
      | /address/country       | String | true     | US                                                          | xx        | USA         |
      | /type                  | String | true     | hotel                                                       | xx        | CONSULTANCY |
      | /is_active             | Bool   | false    | (true\|false)                                               | \.{10}    |             |

    Given The following customers exist with random address
      | id                                   | name                 | email                   | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone      |
      | 79e1ac09-17d7-4c58-b8d3-c2b583bdbb0e | Validation company 1 | validation1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |

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
      | testedField            | responseCode |
      | /website               | 204          |
      | /email                 | 204          |
      | /phone                 | 204          |
      | /address/country       | 204          |
      | /headquarters_timezone | 204          |


  Scenario: Object filtering
    When create 50 "customer" objects
    Then filtering by top-level fields returns matching "customer" objects

  # --- error handling ---
  Scenario: Object creation - invalid values
    When create "customer" objects each with one invalid field value
    Then there are following responses
      | testedField            | responseCode | customCode |
      | /website               | 422          | 42201      |
      | /email                 | 422          | 42201      |
      | /phone                 | 422          | 42201      |
      | /headquarters_timezone | 422          | 42201      |
      | /address/country       | 422          | 42202      |

  Scenario: Object creation - missing values
    When create "customer" objects each with one missing field
    Then there are following responses
      | testedField            | responseCode | customCode |
      | /name                  | 422          | 42201      |
      | /email                 | 422          | 42201      |
      | /headquarters_timezone | 422          | 42201      |
      | /is_demo_customer      | 422          | 42201      |
      | /address/address_line1 | 422          | 42201      |
      | /address/city          | 422          | 42201      |
      | /address/zip_code      | 422          | 42201      |
      | /address/country       | 422          | 42201      |

  Scenario: Object update - invalid values
    When update "customer" objects each with one invalid field value
    Then there are following responses
      | testedField      | responseCode | customCode |
      | /website         | 422          | 42201      |
      | /email           | 422          | 42201      |
      | /phone           | 422          | 42201      |
      | /address/country | 422          | 42202      |

  Scenario Outline: Object update - customer - invalid values
    When Update customer with id "79e1ac09-17d7-4c58-b8d3-c2b583bdbb0e", field "<updated_field>", its value "<value>"
    Then Response code is 422
    And Custom code is "<custom_code>"
    Examples:
      | updated_field | custom_code | value            |
      | timezone      | 42201       | invalid_timezone |
      | timezone      | 42201       | UTC+01:00        |
      | phone         | 42201       | invalid_phone    |
      | phone         | 42201       | 123              |
      | email         | 42201       | invalid_email    |
      | email         | 42201       | @invalid_email   |
      | vatId         | 42201       | invalid_vatId    |
      | vatId         | 42201       | @\/*             |
      | website       | 42201       | invalid_web      |
      | website       | 42201       | www.snapshot.com |

  Scenario Outline: Object update - customer's address - invalid values
    When Customer with id "79e1ac09-17d7-4c58-b8d3-c2b583bdbb0e", update address with following data
      | country   | city   | zipCode   | region   | addressLine1   | addressLine2   |
      | <country> | <city> | <zipCode> | <region> | <addressLine1> | <addressLine2> |
    Then Response code is 422
    And Custom code is "<custom_code>"
    Examples:
      | country | city    | zipCode | region  | addressLine1 | addressLine2 | custom_code |
      | /null   | \w{256} | /null   | /null   | /null        | /null        | 42201       |
      | XX      | /null   | /null   | /null   | /null        | /null        | 42202       |
      | USA     | /null   | /null   | /null   | /null        | /null        | 42202       |
      | /null   | /null   | \w{101} | /null   | /null        | /null        | 42201       |
      | /null   | /null   | /null   | \w{101} | /null        | /null        | 42201       |
      | /null   | /null   | /null   | /null   | \w{501}      | /null        | 42201       |
      | /null   | /null   | /null   | /null   | /null        | \w{501}      | 42201       |


  Scenario Outline: Object update - customer's address - empty values
    When Customer with id "79e1ac09-17d7-4c58-b8d3-c2b583bdbb0e", update address with following data
      | country   | city   | zipCode   | region   | addressLine1   | addressLine2   |
      | <country> | <city> | <zipCode> | <region> | <addressLine1> | <addressLine2> |
    Then Response code is 422
    And Custom code is "<custom_code>"
    Examples:
      | country | city  | zipCode | region | addressLine1 | addressLine2 | custom_code |
      |         | /null | /null   | /null  | /null        | /null        | 42201       |
      | /null   |       | /null   | /null  | /null        | /null        | 42201       |
      | /null   | /null |         | /null  | /null        | /null        | 42201       |
      | /null   | /null | /null   | /null  |              | /null        | 42201       |


  Scenario Outline: Object update - customer's address - valid values
    When Customer with id "79e1ac09-17d7-4c58-b8d3-c2b583bdbb0e", update address with following data
      | country   | city   | zipCode   | region   | addressLine1   | addressLine2   |
      | <country> | <city> | <zipCode> | <region> | <addressLine1> | <addressLine2> |
    Then Response code is 204
    And Body is empty
    And Etag header is present
    Examples:
      | country | city      | zipCode | region | addressLine1         | addressLine2         |
      | CZ      | /null     | /null   | /null  | /null                | /null                |
      | US      | /null     | /null   | /null  | /null                | /null                |
      | EG      | /null     | /null   | /null  | /null                | /null                |
      | CN      | /null     | /null   | /null  | /null                | /null                |
      | /null   | Prague    | /null   | /null  | /null                | /null                |
      | /null   | NewYork   | /null   | /null  | /null                | /null                |
      | /null   | NewYork11 | /null   | /null  | /null                | /null                |
      | /null   | 上海市       | /null   | /null  | /null                | /null                |
      | /null   | 上海市2      | /null   | /null  | /null                | /null                |
      | /null   | /null     | 60200   | /null  | /null                | /null                |
      | /null   | /null     | 123456  | /null  | /null                | /null                |
      | /null   | /null     | /null   |        | /null                | /null                |
      | /null   | /null     | /null   | /null  | 1                    | /null                |
      | /null   | /null     | /null   | /null  | AddressLineNumberOne | /null                |
      | /null   | /null     | /null   | /null  | 无锡市 99/1A-BC         | /null                |
      | /null   | /null     | /null   | /null  | /null                |                      |
      | /null   | /null     | /null   | /null  | /null                | AddressLineNumberTwo |
      | /null   | /null     | /null   | /null  | /null                | 2                    |
      | /null   | /null     | /null   | /null  | /null                | /null                |

  Scenario: Object update - US customer's region
    When Customer with id "79e1ac09-17d7-4c58-b8d3-c2b583bdbb0e", update address with following data
      | country | city  | zipCode | region | addressLine1 | addressLine2 |
      | US      | /null | /null   | Texas  | /null        | /null        |
    Then Response code is 204
    And Body is empty
    And Etag header is present

  Scenario Outline: Object update - customer - valid values
    When Update customer with id "79e1ac09-17d7-4c58-b8d3-c2b583bdbb0e", field "<updated_field>", its value "<value>"
    Then Response code is 204
    And  Body is empty
    Examples:
      | updated_field | value                 |
      | timezone      | Pacific/Fiji          |
      | timezone      | GMT                   |
      | phone         | +42098765432          |
      | email         | valid@snapshot.travel |
      | vatId         | CZ98765432            |
      | website       | http://snapshot.com   |
