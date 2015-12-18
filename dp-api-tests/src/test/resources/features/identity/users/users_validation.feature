Feature: users_validation

  Background:
    Given Database is cleaned
    Given the location "identity/users" for object "user"
    Given unique identifier "user_id" for object "user"
    Given the following "user" object definition
      | path           | type    | required | correct                                                          | invalid      | longer     |
      #---------------------------------------------------------------------------------------------------------------------------------------------------------
      | /first_name    | String  | true     | \w{255}                                                          | /null        | \w{256}    |
      | /last_name     | String  | true     | \w{255}                                                          | /null        | \w{256}    |
      | /user_type     | String  | true     | (snapshot\|customer\|partner\|guest)                             | nonexisstent | \w{256}    |
      | /user_name     | String  | true     | \w{64}                                                           | /null        | \w{65}     |
      | /salesforce_id | String  | false    | \w{32}                                                           | /null        | \w{33}     |
      | /email         | String  | true     | (([a-z]\|\d){9}\.){4}(\w\|\d){10}\@(([a-z]\|\d){9}\.){4}[a-z]{9} | \.{10}       | \w{101}    |
      | /phone         | String  | false    | +[0-9]{12}                                                       | \.{10}       | \w{101}    |
      | /timezone      | String  | true     | (America/New_York\|Europe/Prague\|GMT)                           | UTC+1:00     | UTC+001:00 |
      | /culture       | String  | true     | (cs-CZ\|sk-SK)                                                   | xx           |            |
      | /comment       | String  | false    | \w{255}                                                          | /null        | \w{256}    |
      | /is_active     | Integer | false    | (1\|0)                                                           | 2            |            |

  # --- happy path ---

  Scenario: Object creation - correct values
    When create "user" object with correct field values
    Then Response code is "201"
    And location header is set and points to the same object
    And returned "user" object matches

  Scenario: Object update - correct values
    When update "user" object with correct field values
    Then Response code is "204"
    And returned "user" object matches

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
    When create 50 "user" objects
    Then filtering by top-level fields returns matching "user" objects

  # --- error handling ---
  Scenario: Object creation - invalid values
    When create "user" objects each with one invalid field value
    Then there are following responses
      | testedField | responseCode | customCode |
      | /phone      | 400          | 59         |
      | /email      | 400          | 59         |
      | /timezone   | 400          | 59         |
      | /culture    | 400          | 59         |
      | /user_type  | 400          | 59         |

  Scenario: Object creation - missing values
    When create "user" objects each with one missing field
    Then there are following responses
      | testedField | responseCode | customCode |
      | /first_name | 400          | 53         |
      | /last_name  | 400          | 53         |
      | /user_type  | 400          | 53         |
      | /user_name  | 400          | 53         |
      | /email      | 400          | 53         |
      | /timezone   | 400          | 53         |
      | /culture    | 400          | 53         |

  Scenario: Object update - invalid values
    When update "user" objects each with one invalid field value
    Then there are following responses
      | testedField | responseCode | customCode |
      | /phone      | 400          | 59         |
      | /email      | 400          | 59         |
      | /timezone   | 400          | 59         |
      | /culture    | 400          | 59         |
      | /user_type  | 400          | 59         |

#   TODO when field lengths are stabilized
#
#   Scenario: Object creation - long values
