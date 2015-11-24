Feature: authorization

  Background: 
    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone  | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | UTC+01:00 | cz      |

  Scenario: Getting configuration data with a valid access token
    When Getting configuration data for "/configuration" with token "b5be28b34e903d74d37ff108e83d51f8"
    Then Content type is "application/json"
    And Response code is "200"

  Scenario: Getting configuration data with an invalid access token
    When Getting configuration data for "/configuration" with token "an_invalid_token"
    Then Response code is "403"
    And Custom code is "3"

  Scenario Outline: Getting identity data with a valid access token
    When Getting identity data for "<url>" with token "1209fbb10f984de591c8126465e9b603"
    Then Content type is "application/json"
    And Response code is "200"
      | url                    |
      | identity/customers     |
      | identity/properties    |
      | identity/property_sets |
      | identity/users         |
      | identity/roles         |

  Scenario Outline: Getting identity data with an invalid access token
    When Getting identity data for "<url>" with token "an_invalid_token"
    Then Response code is "403"
    And Custom code is "3"
      | url                    |
      | identity/customers     |
      | identity/properties    |
      | identity/property_sets |
      | identity/users         |
      | identity/roles         |

  Scenario: Get access token for a specific existing user
    When Getting token for user with id "2af224ab-2295-4f27-bcbe-12315dc0c14c" username "default1" and password "valid_passwod"
    Then Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "access_token"
    And Body contains entity with attribute "token_type" value "bearer"
    And Body contains entity with attribute "expires_in" value "1800"

  Scenario: Get access token for a non-existent username, password combination
    When Getting token for user with id "2af224ab-2295-4f27-bcbe-12315dc0c14c" username "default1" and password "invalid_passwod"
    Then Content type is "application/json"
    And Response code is "403"
    And Custom code is "3"
