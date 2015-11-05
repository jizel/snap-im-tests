Feature: common_object_scenarios

  Background:
    Given the following "property" object definition
    | name               | type      | required | correct                                                            | invalid         | longer           |
#     | property_id        | ID        | true     |                                                                 |                 |               |
    | property_code      | String    | true     | (\w\|\d\|[:punct:]){50}                                            | /null           | \w{256}          |
    | property_name      | String    | true     | (\w\|\d\|[:punct:]){255}                                           | /null           | \w{256}          |
    | salesforce_id      | String    | false    | (\w\|\d\|[:punct:]){100}                                           | /null           | \w{101}          |
    | website            | String    | false    | http:\/\/[a-z0-9]{244}\.com                                        | \.{10}          | \w{1001}         |
    | email              | String    | true     | (([a-z]\|\d){9}\.){4}(\w\|\d){10}\@(([a-z]\|\d){9}\.){4}[a-z]{9}   | \.{10}          | \w{101}          |
    | vat_id             | String    | false    | CZ[a-zA-Z0-9]{12}                                                  | xx123           | \w{101}          |
    | timezone           | String    | true     | UTC(+\|-)[01][0-9]:[0-5][0-9]                                      | UTC+1:00        | UTC+001:00       |
    | is_demo_property   | Bool      | true     | (true\|false)                                                      | /null           |                  |
    | address            | Ref       | true     | address                                                            | address         |                  |
    
    Given the following "address" object definition
    | name              | type      | required | correct                        | invalid         | longer           |
    | address_line1     | String    | true     | [a-zA-Z0-9]{12}                | /null           | [a-zA-Z0-9]{13}  |
    | address_line2     | String    | false    | [a-zA-Z0-9]{12}                | /null           | [a-zA-Z0-9]{13}  |
    | city              | String    | true     | [a-zA-Z0-9]{12}                | /null           | [a-zA-Z0-9]{13}  |
    | zip_code          | String    | true     | [a-zA-Z0-9]{12}                | /null           | [a-zA-Z0-9]{13}  |
    | country           | String    | true     | US                             | xx              | [a-zA-Z0-9]{13}  |

    Given the location "identity/properties" for object "property"

  Scenario: Object creation - correct values
    When create "property" object with correct field values
    Then Response code is "201"

  Scenario: Object creation - incorrect values
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
