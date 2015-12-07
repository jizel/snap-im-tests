Feature: authorization

  #A background 'given' step will be added for verificaion that a user exists after the issue with recreating users is fixed
  Background: 
    Given The password of user with id "03a8947b-f892-4248-9df9-579cc3f4b6df" is "somepassword22"

  Scenario: Get access token for a specific existing user
    When Getting token for user "u_k_f_T8A_8_OD8_N3kO__1E049_Hq_a_zXnP_5Mgw_wGN0Eh_JP06_qIaGS_D9m" with password "somepassword22"
    Then Content type is "application/json"
    And Response code is "200"
    And Body contains entity with attribute "access_token"
    And Body contains entity with attribute "token_type" value "bearer"
    And Body contains entity with attribute "expires_in" value "86400"
    #Scope is set to 'all' for now, possibly to be changed in a future feature
    And Body contains entity with attribute "scope" value "all"

  Scenario: Get access token for a non-existent username, password combination
    When Getting token for user "list_default_14" with password "invalid_password"
    Then Content type is "application/json"
    And Response code is "403"

  Scenario: Getting configuration data with a valid access token
    When Getting configuration data for "/configuration" with a new token for user "u_k_f_T8A_8_OD8_N3kO__1E049_Hq_a_zXnP_5Mgw_wGN0Eh_JP06_qIaGS_D9m" with password "somepassword22"
    Then Content type is "application/json"
    And Response code is "200"

  Scenario: Getting configuration data with an invalid access token
    When Getting configuration data for "/configuration" with token "an_invalid_token"
    Then Response code is "403"

  Scenario Outline: Getting identity data with a valid access token
    When Getting identity data for "<url>" with a new token for user "u_k_f_T8A_8_OD8_N3kO__1E049_Hq_a_zXnP_5Mgw_wGN0Eh_JP06_qIaGS_D9m" with password "somepassword22"
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
