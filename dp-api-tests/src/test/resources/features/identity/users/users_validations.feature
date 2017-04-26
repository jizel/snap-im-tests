@Identity
Feature: Users validations

  Background:
    Given Database is cleaned and default entities are created
    Given the location "identity/users" for object "user"
    Given unique identifier "user_id" for object "user"
    Given The following customers exist with random address
      | id                                   | companyName        | email                          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 55656571-a3be-4f8b-bc05-02c0797912a6 | UserCreateCustomer | userCreateCustomer@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given the following "user" object definition
      | path                        | type       | required | correct                                                     | invalid      | longer     |
      #---------------------------------------------------------------------------------------------------------------------------------------------------------
      | /first_name                 | String     | true     | \w{255}                                                     | /null        | \w{256}    |
      | /last_name                  | String     | true     | \w{255}                                                     | /null        | \w{256}    |
      | /user_type                  | String     | true     | (snapshot\|customer\|partner\|guest)                        | nonexisstent | \w{256}    |
      | /user_name                  | String     | true     | \w{150}                                                     | /null        | \w{151}    |
      | /email                      | String     | true     | (([a-z]\|\d){9}\.){4}(\w\|\d){10}\@(([a-z]\|\d){9}\.){4}com | \.{10}       | (([a-z]\|\d){9}\.){4}(\w\|\d){24}\@(([a-z]\|\d){191}\.)com |
      | /phone                      | String     | false    | +[0-9]{12}                                                  | \.{10}       | \w{101}    |
      | /timezone                   | String     | true     | (America/New_York\|Europe/Prague\|GMT)                      | UTC+1:00     | UTC+001:00 |
      | /culture                    | String     | true     | (cs-CZ\|sk-SK)                                              | xx           |            |
      | /comment                    | String     | false    | \w{255}                                                     | /null        | \w{256}    |
      | /user_customer_relationship | JSON       | true     | {"is_primary":false,"customer_id":"55656571-a3be-4f8b-bc05-02c0797912a6"}|  /null   |  \w{100}    |
      #| /is_active     | Integer | false    | (1\|0)                                                      | 2            |            |

  # --- happy path ---

  @Smoke
  Scenario: Object create, update - correct values
    When create "user" object with correct field values
    Then Response code is "201"
    And location header is set and points to the same object
    When update "user" object with correct field values
    Then Response code is "204"

  Scenario: Object update - correct values one by one
    When update "user" objects each with one correct field value
    Then there are following responses
      | testedField | responseCode |
      | /phone      | 204          |
      | /email      | 204          |
      | /timezone   | 204          |
      | /culture    | 204          |
      | /first_name | 204          |
      | /last_name  | 204          |

  Scenario: Object filtering
    When create 5 "user" objects
    Then filtering by top-level fields returns matching "user" objects

  # --- error handling ---
  Scenario: Object creation - invalid values
    When create "user" objects each with one invalid field value
    Then there are following responses
      | testedField | responseCode | customCode |
      | /phone      | 422          | 42201      |
      | /email      | 422          | 42201      |
      | /timezone   | 422          | 42201      |
      | /culture    | 422          | 42201      |
      | /user_type  | 422          | 42201      |

  Scenario: Object creation - missing values
    When create "user" objects each with one missing field
    Then there are following responses
      | testedField | responseCode | customCode |
      | /first_name | 422          | 42201      |
      | /last_name  | 422          | 42201      |
      | /user_type  | 422          | 42201      |
      | /user_name  | 422          | 42201      |
      | /email      | 422          | 42201      |
      | /timezone   | 422          | 42201      |
      | /culture    | 422          | 42201      |

  Scenario: Object update - invalid values
    When update "user" objects each with one invalid field value
    Then there are following responses
      | testedField | responseCode | customCode |
      | /phone      | 422          | 42201      |
      | /email      | 422          | 42201      |
      | /timezone   | 422          | 42201      |
      | /culture    | 422          | 42201      |
      | /user_type  | 422          | 42201      |

  Scenario: Object creation - longer values
    When create "user" objects each with one long field
    Then there are following responses
      | testedField | responseCode | customCode |
      | /first_name | 422          | 42201      |
      | /last_name  | 422          | 42201      |
      | /user_type  | 422          | 42201      |
      | /user_name  | 422          | 42201      |
      | /email      | 422          | 42201      |
      | /timezone   | 422          | 42201      |
      | /culture    | 422          | 42201      |

#   TODO when field lengths are stabilized
#
#   Scenario: Object creation - long values
