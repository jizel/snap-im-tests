Feature: properties_validation

  Background:
    Given the location "identity/properties" for object "property"
    Given unique identifier "property_id" for object "property"
    Given the following "property" object definition
    | name                   | type      | required | correct                                                            | invalid         | longer           |
    #----------------------------------------------------------------------------------------------------------------------------------------------------------
    | /property_code         | String    | true     | \w{50}                                                             | /null           | \w{256}          |
    | /property_name         | String    | true     | \w{255}                                                            | /null           | \w{256}          |
    | /salesforce_id         | String    | false    | \w{100}                                                            | /null           | \w{101}          |
    | /website               | String    | false    | http:\/\/[a-z0-9]{244}\.com                                        | \.{10}          | \w{1001}         |
    | /email                 | String    | true     | (([a-z]\|\d){9}\.){4}(\w\|\d){10}\@(([a-z]\|\d){9}\.){4}[a-z]{9}   | \.{10}          | \w{101}          |
    | /vat_id                | String    | false    | CZ[a-zA-Z0-9]{12}                                                  | xx123           | \w{101}          |
    | /timezone              | String    | true     | UTC(+\|-)[01][0-9]:[0-5][0-9]                                      | UTC+1:00        | UTC+001:00       |
    | /is_demo_property      | Bool      | true     | (true\|false)                                                      | /null           |                  |
    | /address/address_line1 | String    | true     | \w{100}                                                            | /null           | \w{101}          |
    | /address/address_line2 | String    | false    | \w{100}                                                            | /null           | \w{101}          |
    | /address/city          | String    | true     | \w{50}                                                             | /null           | \w{51}           |
    | /address/zip_code      | String    | true     | [a-zA-Z0-9]{10}                                                    | /null           | \w{11}           |
    | /address/country       | String    | true     | US                                                                 | xx              | USA              |

  # --- happy path ---

  Scenario: Object creation - correct values
    When create "property" object with correct field values
    Then Response code is "201"
    And location header is set and points to the same object

  Scenario: Object update - correct values
    When update "property" object with correct field values
    Then Response code is "201"
    And location header is set and points to the same object

  Scenario: Object update - correct values one by one
    When update "property" objects each with one correct field value
    Then there are following responses
    | testedField         | responseCode | customCode |
    | /website            | 400          | 59         |
    | /email              | 400          | 59         |
    | /vat_id             | 400          | 59         |
    | /timezone           | 400          | 59         |
    | /address/country    | 400          | 59         |

  @skipped
  Scenario: Object filtering
    When created 50 "property" objects
    Then filtering by top-level fields returns matching "property" objects

  # --- error handling ---

  Scenario: Object creation - invalid values
    When create "property" objects each with one invalid field value
    Then there are following responses
    | testedField         | responseCode | customCode |
    | /website            | 400          | 59         |
    | /email              | 400          | 59         |
    | /vat_id             | 400          | 59         |
    | /timezone           | 400          | 59         |
    | /address/country    | 400          | 59         |

  Scenario: Object creation - missing values
    When create "property" objects each with one missing field
    Then there are following responses
    | testedField              | responseCode | customCode |
    | /property_code           | 400          | 53         |
    | /property_name           | 400          | 53         |
    | /email                   | 400          | 53         |
    | /timezone                | 400          | 53         |
    | /is_demo_property        | 400          | 53         |
    | /address/address_line1   | 400          | 53         |
    | /address/city            | 400          | 53         |
    | /address/zip_code        | 400          | 53         |
    | /address/country         | 400          | 53         |

  Scenario: Object update - invalid values
    When update "property" objects each with one invalid field value
    Then there are following responses
    | testedField         | responseCode | customCode |
    | /website            | 400          | 59         |
    | /email              | 400          | 59         |
    | /vat_id             | 400          | 59         |
    | /timezone           | 400          | 59         |
    | /address/country    | 400          | 59         |

#   TODO when field lengths are stabilized
#   
#   Scenario: Object creation - long values
