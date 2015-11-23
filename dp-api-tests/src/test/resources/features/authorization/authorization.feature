Feature: authorization

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

# Not applicable for now

#  Scenario: Get access token for a specific user
#    Given User "user1" with password "password1" exists
#    When Getting token with username "user1" and password "password1"
#    Then Content type is "application/json"
#    And Response code is "200"
#    And "access_token" is got
#    And "token_type" is "bearer"
#    And "expires_in" is "1800"

#  Scenario: Get access token for a non-existent username, password combination
#    Given User "user2" with password "password2" does not exist
#    When Getting token with username "user2" and password "password2"
#    Then Content type is "application/json"
#    And Response code is "403"
#    And Custom code is "3"
