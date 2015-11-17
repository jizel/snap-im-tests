Feature: authorization

  Scenario: Get access token for a specific user
    Given User "user1" with password "password1" exists
    When Posting to "add_url_here" username "user1" and password "password1"
    Then Content type is "application/json"
    And Response code is "200"
    And "access_token" is not null
    And "token_type" is "bearer"
    And "expires_in" is "1800"

  Scenario: Get access token for a non-existent username, password combination
    Given User "user2" with password "password2" does not exist
    When Posting to "add_url_here" username "user2" and password "password2"
    Then Content type is "application/json"
    And Response code is "" #need to add response code
    And Custom code is ""   #need to add custom code
