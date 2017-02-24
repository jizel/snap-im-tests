@Identity
Feature: Properties validation

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given the location "identity/properties" for object "property"
    Given unique identifier "property_id" for object "property"
    Given the following "property" object definition
      | path                   | type   | required | correct                                                     | invalid  | longer     |
      #----------------------------------------------------------------------------------------------------------------------------------------------------------
      | /property_code         | String | true     | \w{50}                                                      | /null    | \w{256}    |
      | /name                  | String | true     | \w{255}                                                     | /null    | \w{256}    |
      | /salesforce_id         | String | false    | \w{100}                                                     | /null    | \w{101}    |
      | /anchor_customer_id    | String | true     | 1238fd9a-a05d-42d8-8e84-42e904ace123                        | /null    | \w{101}    |
      | /website               | String | false    | http:\/\/[a-z0-9]{63}\.com                                  | \.{10}   | \w{1001}   |
      | /email                 | String | true     | (([a-z]\|\d){9}\.){4}(\w\|\d){10}\@(([a-z]\|\d){9}\.){4}com | \.{10}   | \w{101}    |
      | /timezone              | String | true     | (America/New_York\|Europe/Prague)                           | UTC+1:00 | UTC+001:00 |
      | /is_demo_property      | Bool   | true     | (true\|false)                                               | /null    |            |
      | /is_active             | String | false    | (true\|false)                                                      | x        |            |
      | /address/address_line1 | String | true     | \w{100}                                                     | /null    | \w{101}    |
      | /address/address_line2 | String | false    | \w{100}                                                     | /null    | \w{101}    |
      | /address/city          | String | true     | \w{50}                                                      | /null    | \w{51}     |
      | /address/zip_code      | String | true     | [a-zA-Z0-9]{10}                                             | /null    | \w{11}     |
      | /address/country       | String | true     | US                                                          | xx       | USA        |

  # --- happy path ---

  @Smoke
  Scenario: Object creation - correct values
    When create "property" object with correct field values
    Then Response code is "201"
    And location header is set and points to the same object
    And returned "property" object matches

  @Smoke
  Scenario: Object update - correct values
    When update "property" object with correct field values
    Then Response code is "204"
    And returned "property" object matches

  Scenario: Object update - correct values one by one
    When update "property" objects each with one correct field value
    Then there are following responses
      | testedField      | responseCode |
      | /website         | 204          |
      | /email           | 204          |
      | /timezone        | 204          |
      | /address/country | 204          |

  Scenario: Object filtering
    When create 50 "property" objects
    Then filtering by top-level fields returns matching "property" objects

  # --- error handling ---
  Scenario: Object creation - invalid values
    When create "property" objects each with one invalid field value
    Then there are following responses
      | testedField      | responseCode | customCode |
      | /website         | 422          | 42201      |
      | /email           | 422          | 42201      |
      | /timezone        | 422          | 42201      |
      | /address/country | 422          | 42202      |
      | /is_active       | 422          | 42201      |

  Scenario: Object creation - missing values
    When create "property" objects each with one missing field
    Then there are following responses
      | testedField            | responseCode | customCode    |
      | /property_code         | 201          |               |
      | /name                  | 422          | 42201         |
      | /email                 | 422          | 42201         |
      | /timezone              | 422          | 42201         |
      | /is_demo_property      | 422          | 42201         |
      | /address/address_line1 | 422          | 42201         |
      | /address/city          | 422          | 42201         |
      | /address/zip_code      | 422          | 42201         |
      | /address/country       | 422          | 42201         |
      | /website               | 201          |               |
      | /is_active             | 201          |               |

  Scenario: Object update - invalid values
    When update "property" objects each with one invalid field value
    Then there are following responses
      | testedField      | responseCode | customCode |
      | /website         | 422          | 42201      |
      | /email           | 422          | 42201      |
      | /timezone        | 422          | 42201      |
      | /address/country | 422          | 42202      |

#   TODO when field lengths are stabilized
#
#   Scenario: Object creation - long values
