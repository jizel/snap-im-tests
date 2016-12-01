@Identity
Feature: Users create update delete

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName        | email                          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 55656571-a3be-4f8b-bc05-02c0797912a6 | UserCreateCustomer | userCreateCustomer@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "55656571-a3be-4f8b-bc05-02c0797912a6" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                         | timezone      | culture |
      | 55529079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snapshotUser1 | Snapshot1 | User1    | snapshotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
      | 66629079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snapshotUser2 | Snapshot2 | User2    | snapshotUser2@snapshot.travel | Europe/Prague | cs-CZ   |

  Scenario Outline: Creating users
    When The following users is created for customer "55656571-a3be-4f8b-bc05-02c0797912a6" as primary "false"
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
    When The following users is created for customer "55656571-a3be-4f8b-bc05-02c0797912a6" as primary "false"
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
      | /messages/identity/users/create_user_invalid_user_type.json | POST   | identity | /identity/users | 400        | 40002       |

  @Smoke
  Scenario: Deleting user
    When User with userName "snapshotUser1" is deleted
    Then Response code is "204"
    And Body is empty
    And User with same id doesn't exist

  Scenario: Checking deleting user is idempotent
    When Nonexistent user is deleted
    Then Response code is "204"
    And Body is empty

  Scenario Outline: Updating user
    When User with userName "snapshotUser2" is updated with data
      | userType   | firstName   | lastName   | email   | timezone   | culture   | comment   |
      | <userType> | <firstName> | <lastName> | <email> | <timezone> | <culture> | <comment> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated user with userName "snapshotUser2" has data
      | userType   | firstName   | lastName   | email   | timezone   | culture   | comment   |
      | <userType> | <firstName> | <lastName> | <email> | <timezone> | <culture> | <comment> |

    Examples:
      | userType | firstName | lastName | email                 | timezone         | culture | comment  |
      | partner  | FNUp1     | LNUp1    | EMUp1@snapshot.travel | Europe/Prague    | cs-CZ   | VIP user |
      | guest    | FNUp2     | LNUp2    | EMUp2@snapshot.travel | America/New_York | en-US   | /null    |

  Scenario: Updating user with outdated ETag
    When User with userName "snapshotUser2" is updated with data if updated before
      | firstName   |
      | NOT_APPLIED |
    Then Response code is "412"
    And Custom code is "57"

#    TODO: Check error codes for user update

  @Smoke
  Scenario: User is activated
    When User with userName "snapshotUser1" is activated
    Then Response code is "204"
    And Body is empty
    And User with userName "snapshotUser1" is active

  Scenario: User is inactivated
    Given User with userName "snapshotUser1" is activated
    When User with userName "snapshotUser1" is inactivated
    Then Response code is "204"
    And Body is empty
    And User with userName "snapshotUser1" is not active

  @Bug
  Scenario: Creating user with same name as previously deleted user - DP-1380
    When The following users is created for customer "55656571-a3be-4f8b-bc05-02c0797912a6" as primary "false"
      | userId                                | userType   | userName      | firstName | lastName | email                         | timezone      | culture |
      | 00029079-48f0-4f00-9bec-e2329a8bdaac  | snapshot   | snaphostUser1 | Snapshot  | User1    | snaphostUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Then Response code is 201
    When User with userName "snaphostUser1" is deleted
    Then Response code is 204
    When The following users is created for customer "55656571-a3be-4f8b-bc05-02c0797912a6" as primary "false"
      | userId                                | userType   | userName      | firstName | lastName | email                         | timezone      | culture |
      | 6d829079-48f0-4f00-9bec-e2329a8bdaac  | snapshot   | snaphostUser1 | Snapshot  | User1    | snaphostUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Then Response code is 201
    And Body contains entity with attribute "user_id" value "6d829079-48f0-4f00-9bec-e2329a8bdaac"
    And Body contains entity with attribute "user_name" value "snaphostUser1"

  Scenario Outline: Send POST request with empty body to all user endpoints
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                   |
      | identity/users/                                                                                       |
      | identity/users/55529079-48f0-4f00-9bec-e2329a8bdaac                                                   |
#      Fails because of DP-1579
      | identity/users/55529079-48f0-4f00-9bec-e2329a8bdaac/password/                                         |
      | identity/users/55529079-48f0-4f00-9bec-e2329a8bdaac/partners/                                         |
#  Uncomment when partner tests (create partner) are implemented
#      | identity/users/55529079-48f0-4f00-9bec-e2329a8bdaac/partners/77729079-48f0-4f00-9bec-e2329a8bdaac     |
      | identity/users/55529079-48f0-4f00-9bec-e2329a8bdaac/customers/                                        |
      | identity/users/55529079-48f0-4f00-9bec-e2329a8bdaac/properties/                                       |
      | identity/users/55529079-48f0-4f00-9bec-e2329a8bdaac/property_sets/                                    |
