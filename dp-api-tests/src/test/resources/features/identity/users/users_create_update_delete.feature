Feature: Users create update delete

  Background:
    Given Database is cleaned
    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone      | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | default2 | Default2  | User1    | def2@snapshot.travel | Europe/Prague | cs-CZ   |

  Scenario Outline: Creating users
    When User is created
      | userType   | userName   | firstName   | lastName   | email   | timezone   | culture   |
      | <userType> | <userName> | <firstName> | <lastName> | <email> | <timezone> | <culture> |
    Then Response code is "201"
    And Body contains entity with attribute "user_type" value "<userType>"
    And Body contains entity with attribute "user_name" value "<userName>"
    And Body contains entity with attribute "first_name" value "<firstName>"
    And Body contains entity with attribute "last_name" value "<lastName>"
    And Body contains entity with attribute "email" value "<email>"
    And Body contains entity with attribute "timezone" value "<timezone>"
    And Body contains entity with attribute "culture" value "<culture>"
    And "Location" header is set and contains the same user
    And Etag header is present


    Examples:
      | userType | userName | firstName | lastName | email                           | timezone      | culture |
      | customer | snp      | Snap      | Shot     | snp@snapshot.travel             | Europe/Prague | cs-CZ   |
      | customer | snp1     | Snap1     | Shot1    | dummy_mail+32@gmail.com         | Europe/Prague | cs-CZ   |
      | customer | snp2     | Snap2     | Shot2    | dummy.test-mail@gmail.com       | Europe/Prague | cs-CZ   |
      | customer | snp3     | Snap3     | Shot3    | dummy_test-mail.32@gmail.com    | Europe/Prague | cs-CZ   |
      | customer | snp4     | Snap4     | Shot4    | dummy_test-mail.32+32@gmail.com | Europe/Prague | cs-CZ   |
      | customer | snp5     | Snap5     | Shot5    | dummy-mail+32@gmail.com         | Europe/Prague | cs-CZ   |
      | customer | snp6     | Snap6     | Shot6    | dummy-test_mail+32@gmail.com    | Europe/Prague | cs-CZ   |
      | customer | snp7     | Snap7     | Shot7    | dummy-test_mail@gmail.com       | Europe/Prague | cs-CZ   |
      | customer | snp8     | Snap8     | Shot8    | dummy_mail@gmail.com            | Europe/Prague | cs-CZ   |
      | customer | snp9     | Snap9     | Shot9    | dummy_test-mail@gmail.com       | Europe/Prague | cs-CZ   |
      | customer | snp10    | Snap10    | Shot10   | dummy-mail@gmail.com            | Europe/Prague | cs-CZ   |
      | customer | snp1     | Snap1     | Shot1    | vlastimil.kaluza+1@gmail.com    | Europe/Prague | cs-CZ   |
      | customer | snp3     | Snap3     | Shot3    | dummy.test_mail+32@gmail.com    | Europe/Prague | cs-CZ   |
      | customer | snp4     | Snap4     | Shot4    | dummy.mail+32@gmail.com         | Europe/Prague | cs-CZ   |
      | customer | snp7     | Snap7     | Shot7    | dummy-test.1_mail+32@gmail.com  | Europe/Prague | cs-CZ   |

  Scenario Outline: Creating users with wrong fields
    When User is created
      | userType   | userName   | firstName   | lastName   | email   | timezone   | culture   |
      | <userType> | <userName> | <firstName> | <lastName> | <email> | <timezone> | <culture> |
    Then Response code is "400"
    And Custom code is "<custom_code>"

    Examples:
      | userType | userName | firstName | lastName | email                  | timezone      | culture | custom_code |
      | customer | snp7     | Snap7     | Shot7    | snp@snapshot.          | Europe/Prague | cs-CZ   | 59          |
      | customer | snp      | Snap      | Shot     | snpsnapshot.travel     | Europe/Prague | cs-CZ   | 59          |
      | customer | snp      | Snap      | Shot     | @snpsnapshot.travel    | Europe/Prague | cs-CZ   | 59          |
      | customer | snp      | Snap      | Shot     | snp@snpsnapshot,travel | Europe/Prague | cs-CZ   | 59          |
      | customer | snp      | Snap      | Shot     | snp$snpsnapshot.travel | Europe/Prague | cs-CZ   | 59          |
      | customer | snp      | Snap      | Shot     | snp&snpsnapshot.travel | Europe/Prague | cs-CZ   | 59          |
      | customer | snp      | Snap      | Shot     | snp^snpsnapshot.travel | Europe/Prague | cs-CZ   | 59          |
      | customer |          | Snap      | Shot     | snp@snpsnapshot.travel | Europe/Prague | cs-CZ   | 61          |
      | customer | snp      |           | Shot     | snp@snpsnapshot.travel | Europe/Prague | cs-CZ   | 61          |
      | customer | snp      | Snap      |          | snp@snpsnapshot.travel | Europe/Prague | cs-CZ   | 61          |
      | customer | snp      | Snap      | Shot     |                        | Europe/Prague | cs-CZ   | 61          |
      | customer | snp      | Snap      | Shot     | snp@snpsnapshot.travel |               | cs-CZ   | 61          |
      | customer | snp      | Snap      | Shot     | snp@snpsnapshot.travel | Europe/Prague |         | 61          |

  Scenario Outline: Checking error codes for creating user with invalid json
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"
    Examples:
      | json_input_file                                             | method | module   | url             | error_code | custom_code |
      | /messages/identity/users/create_user_invalid_user_type.json | POST   | identity | /identity/users | 400        | 63          |

  @Smoke
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

  @Smoke
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
