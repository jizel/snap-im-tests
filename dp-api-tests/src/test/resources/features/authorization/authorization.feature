#This needs to be completely reimplemented
@skipped
Feature: authorization

  Background: 
    Given The following users exist
      | userType | userName  | firstName | lastName  | email          | timezone | culture |
      | customer | testauth1 | Auth1     | AuthUser1 | auth1@mail.com | GMT      | bg      |
    Given The password of user "testauth1" is "somepassword22"

  Scenario: Get access token for a specific existing user
    When Getting token for user "testauth1" with password "somepassword22"
    Then Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "access_token"
    And Body contains entity with attribute "token_type" value "bearer"
    And Body contains entity with attribute "expires_in" value "86400"
    #Scope is set to 'all' for now, possibly to be changed in a future feature
    And Body contains entity with attribute "scope" value "all"

  Scenario: Get access token for a non-existent username, password combination
    When Getting token for user "testauth1" with password "invalid_password"
    Then Content type is "application/json"
    And Response code is "403"

  Scenario: Getting configuration data with a valid access token
    When Getting configuration data for "/configuration" with a new token for user "testauth1" with password "somepassword22"
    Then Content type is "application/json"
    And Response code is "200"

  Scenario: Getting configuration data with an invalid access token
    When Getting configuration data for "/configuration" with token "an_invalid_token"
    Then Response code is "403"

  Scenario Outline: Getting identity data with a valid access token
    When Getting identity data for "<url>" with a new token for user "testauth1" with password "somepassword22"
    Then Content type is "application/json"
    And Response code is "200"

    Examples: 
      | url                    |
      | identity/customers     |
      | identity/properties    |
      | identity/property_sets |
      | identity/users         |
      | identity/roles         |

  Scenario Outline: Getting identity data with an invalid access token
    When Getting identity data for "<url>" with token "an_invalid_token"
    Then Response code is "403"

    Examples: 
      | url                    |
      | identity/customers     |
      | identity/properties    |
      | identity/property_sets |
      | identity/users         |
      | identity/roles         |
