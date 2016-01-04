Feature: users_create_update_delete

  Background:
    Given Database is cleaned
    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone      | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | default2 | Default2  | User1    | def2@snapshot.travel | Europe/Prague | cs-CZ   |

  Scenario: Creating user

    When User is created
      | userType | userName | firstName | lastName | email               | timezone      | culture |
      | customer | snp      | Snap      | Shot     | snp@snapshot.travel | Europe/Prague | cs-CZ   |
    Then Response code is "201"
    And Body contains entity with attribute "user_type" value "customer"
    And Body contains entity with attribute "user_name" value "snp"
    And Body contains entity with attribute "first_name" value "Snap"
    And Body contains entity with attribute "last_name" value "Shot"
    And Body contains entity with attribute "email" value "snp@snapshot.travel"
    And Body contains entity with attribute "timezone" value "Europe/Prague"
    And Body contains entity with attribute "culture" value "cs-CZ"
    And "Location" header is set and contains the same user
    And Etag header is present

  Scenario: Deleting user

    When User with userName "default1" is deleted
    Then Response code is "204"
    And Body is empty
    And User with same id doesn't exist

  Scenario: Checking deleting user is idempotent
    When Nonexistent user is deleted
    Then Response code is "204"
    And Body is empty

  Scenario Outline: Updating user
    When User with userName "default2" is updated with data
      | userType   | firstName   | lastName   | email   | timezone   | culture   | comment   |
      | <userType> | <firstName> | <lastName> | <email> | <timezone> | <culture> | <comment> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated user with userName "default2" has data
      | userType   | firstName   | lastName   | email   | timezone   | culture   | comment   |
      | <userType> | <firstName> | <lastName> | <email> | <timezone> | <culture> | <comment> |

    Examples:
      | userType | firstName | lastName | email                 | timezone         | culture | comment  |
      | partner  | FNUp1     | LNUp1    | EMUp1@snapshot.travel | Europe/Prague    | cs-CZ   | VIP user |
      | guest    | FNUp2     | LNUp2    | EMUp2@snapshot.travel | America/New_York | en-US   | /null    |

  Scenario: Updating user with outdated ETag
    When User with userName "default2" is updated with data if updated before
      | firstName   |
      | NOT_APPLIED |
    Then Response code is "412"
    And Custom code is "57"

  Scenario: User is activated
    When User with id "default1" is activated
    Then Response code is "204"
    And Body is empty
    And User with id "default1" is active
    
  Scenario: User is inactivated
    Given User with id "default1" is activated
    When User with id "default1" is inactivated
    Then Response code is "204"
    And Body is empty
    And User with id "default1" is not active

  Scenario: Activating non existing user
    When User with not existing id "11111111-1111-1111-1111-111111111111" is inactivated
    Then Response code is "404"
    And Custom code is "152"

  Scenario: Deactivating non existing user
    When User with not existing id "11111111-1111-1111-1111-111111111111" is activated
    Then Response code is "404"
    And Custom code is "152"
