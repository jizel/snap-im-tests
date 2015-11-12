Feature: users_create_update_delete

  Background:
    Given The following users exist
      | userType | userName  | firstName  | lastName  | email                | timezone  | culture |
      | customer | default1  | Default1   | User1     | def1@snapshot.travel | UTC+01:00 | cz      |
      | customer | default2  | Default2   | User1     | def2@snapshot.travel | UTC+01:00 | cz      |

  Scenario: Creating user

    When User is created
      | userType | userName | firstName | lastName | email               | timezone  | culture |
      | customer | snp      | Snap      | Shot     | snp@snapshot.travel | UTC+01:00 | cz      |
    Then Response code is "201"
    And Body contains entity with attribute "user_type" value "customer"
    And Body contains entity with attribute "user_name" value "snp"
    And Body contains entity with attribute "first_name" value "Snap"
    And Body contains entity with attribute "last_name" value "Shot"
    And Body contains entity with attribute "email" value "snp@snapshot.travel"
    And Body contains entity with attribute "timezone" value "UTC+01:00"
    And Body contains entity with attribute "culture" value "cz"
    And Location header is set and contains the same user

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
      | userType | firstName | lastName | email                 | timezone  | culture | comment  |
      | partner  | FNUp1     | LNUp1    | EMUp1@snapshot.travel | UTC+02:00 | cz      | VIP user |
      | guest    | FNUp2     | LNUp2    | EMUp2@snapshot.travel | UTC-19:00 | gb      | /null    |

  Scenario: Updating user with outdated ETag
    When User with userName "default2" is updated with data if updated before
      | firstName   |
      | NOT_APPLIED |
    Then Response code is "412"
    And Custom code is "57"
