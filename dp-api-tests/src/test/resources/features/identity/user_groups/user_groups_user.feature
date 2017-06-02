Feature: User groups user relationship feature

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name               | email          | salesforceId | vatId      | isDemo         | phone         | website                    | timezone      |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | UserGroupsCustomer | ug@tenants.biz | ug_sf_1      | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "true"
      | id                                    | type       | username      | firstName | lastName | email                         | timezone      | languageCode |
      | 00029079-48f0-4f00-9bec-e2329a8bdaac  | snapshot   | snapshotUser1 | Snapshot  | User1    | snaphostUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following user groups exist
      | id                                   | customerId                           | name        | isActive | description          |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_1 | false    | userGroupDescription |


  Scenario Outline: Users are added to User Group (valid)
      Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "true"
        | type       | username   | firstName   | lastName  | email   | timezone   | languageCode   |
        | <type> | <username> | <firstName> | <lastName>| <email> | <timezone> | <languageCode> |
      Given User "<username>" is added to userGroup "userGroup_1"
      Then Response code is "201"
      And Body contains entity with attribute "is_active" value "true"
      Examples:
    | username     | type     | firstName | lastName | email                        | timezone          | languageCode |
    | userPartner1 | partner  | FNU1      | LNU1     | userPartner1@snapshot.travel | Europe/Prague     | cs-CZ   |
    | userGuest    | guest    | FNU2      | LNU2     | userGuest@snapshot.travel    | America/New_York  | en-US   |
    | snaphostUser2| snapshot | FNU3      | LNU3     | snaphostUser2@snapshot.travel| Asia/Tokyo        | en-US   |

  Scenario: User is added as to User Group as inactive
    Given User "snapshotUser1" is added to userGroup "userGroup_1" as isActive "false"
    Then Response code is "201"
    And Body contains entity with attribute "is_active" value "false"

  @Smoke
  Scenario: Getting existing relationship between user group and user
    Given User "snapshotUser1" is added to userGroup "userGroup_1"
    When Relation between user group "userGroup_1" and user "snapshotUser1" is got
    Then Response code is 200
    And Content type is "application/json"
    And Body contains entity with attribute "user_id" value "00029079-48f0-4f00-9bec-e2329a8bdaac"
    And Body contains entity with attribute "is_active" value "true"

  Scenario: Getting nonexisting relationship between user group and user
    When Relation between user group "userGroup_1" and user "snapshotUser1" is got
    Then Response code is 404
    And Custom code is 40402

  Scenario Outline: Relationship creation between user group and user - invalid
    When Relation between user group "userGroup_1" and user with id "<user_id>" is created with isActive "<is_active>"
    Then Response code is "<error_response>"
    And Custom code is "<code>"
    Examples:
      | user_id                              | is_active | error_response | code   | #note                     |
      | NotValidFormat                       | /null     | 422            | 42201 | # user_id not in UUID     |
      | 30f983ea-7a69-4e50-a369-d1278f1a0c40 | /null     | 422            | 42202 | # notExisting user_id     |
      |                                      | /null     | 422            | 42201 | # user_id cannot be empty |
      | /null                                | /null     | 422            | 42201 | # user_id cannot be empty |


  Scenario: Remove user from UserGroup - valid
    Given User "snapshotUser1" is added to userGroup "userGroup_1" as isActive "false"
    Then Response code is "201"
    When User "snapshotUser1" is removed from userGroup "userGroup_1"
    Then Response code is 204
    And Body is empty
    When Relation between user group "userGroup_1" and user "snapshotUser1" is got
    Then Response code is 404
    And Custom code is 40402

  Scenario: Delete nonexistent relationship between User and UserGroup
    When User "snapshotUser1" is removed from userGroup "userGroup_1"
    Then Response code is 404
    And Custom code is 40402

  Scenario: (De)Activate relationship userGroup-user
    Given User "snapshotUser1" is added to userGroup "userGroup_1" as isActive "false"
    When Relation between user group "userGroup_1" and user "snapshotUser1" is activated
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and user "snapshotUser1" is active
    When Relation between user group "userGroup_1" and user "snapshotUser1" is deactivated
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and user "snapshotUser1" is not active

    Scenario: Add one User to multiple User Groups
      Given The following user groups exist
        | id                                   | name        | isActive | description          | customerId                           |
        | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_2 | false    | userGroupDescription | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 |
      Given User "snapshotUser1" is added to userGroup "userGroup_1" as isActive "true"
      Then Response code is "201"
      Given User "snapshotUser1" is added to userGroup "userGroup_2" as isActive "true"
      Then Response code is "201"
      When Relation between user group "userGroup_1" and user "snapshotUser1" is got
      Then Response code is 200
      When Relation between user group "userGroup_2" and user "snapshotUser1" is got
      Then Response code is 200

#    DP-1769
  @skipped
  Scenario Outline: Creator of User Group does not automatically become it's member but he can access it - DP-1769
    Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "true"
      | type       | username   | firstName   | lastName  | email   | timezone   | languageCode   | id       |
      | <type>     | <username> | <firstName> | <lastName>| <email> | <timezone> | <languageCode> | <id>     |
    Given The following user group is created by user "<username>"
      | id                                   | customerId                           | name        | isActive | description          |
      | 12340d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_2 | false    | userGroupDescription |
    Then Response code is "201"
    When Relation between user group "userGroup_2" and user "<username>" is got
    Then Response code is 404
    When User group "userGroup_2" is requested by user "<username>"
    Then Response code is "200"
    When List of user groups is got with limit "/null" and cursor "/null" and filter "/null" and sort "/null" and sort_desc "/null" by user "<username>"
    Then Response code is "200"
    And There are "1" user groups returned
    Examples:
      | username      | type     | firstName | lastName | email                         | timezone          | languageCode | id                                    |
      | userPartner1  | partner  | FNU1      | LNU1     | userPartner1@snapshot.travel  | Europe/Prague     | cs-CZ   | 11129079-48f0-4f00-9bec-e2329a8bdaac  |
      | userGuest     | guest    | FNU2      | LNU2     | userGuest@snapshot.travel     | America/New_York  | en-US   | 22229079-48f0-4f00-9bec-e2329a8bdaac  |
      | snapshotUser2 | snapshot | FNU3      | LNU3     | snaphostUser2@snapshot.travel | Asia/Tokyo        | en-US   | 33329079-48f0-4f00-9bec-e2329a8bdaac  |
      | userCustomer1 | customer | FNU4      | LNU4     | userCustomer1@snapshot.travel | Europe/Prague     | cs-CZ   | 44429079-48f0-4f00-9bec-e2329a8bdaac  |

#  DP-1769
  @skipped
  Scenario Outline: Creator of User Group does not automatically become it's member but he can see all of it's members - DP-1769
    Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "true"
      | type       | username | firstName | lastName | email                   | timezone          | languageCode |
      | guest      | member1  | FNU5      | LNU5     | member1@snapshot.travel | America/New_York  | en-US   |
      | customer   | member2  | FNU6      | LNU6     | member2@snapshot.travel | America/New_York  | en-US   |
    Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "true"
      | type       | username   | firstName   | lastName  | email   | timezone   | languageCode   | id   |
      | <type> | <username> | <firstName> | <lastName>| <email> | <timezone> | <languageCode> | <Id> |
    Given The following user group is created by user "<username>"
      | id                                   | customerId                           | name        | isActive | description          |
      | 12340d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_2 | false    | userGroupDescription |
    Then Response code is "201"
    Given User "member1" is added to userGroup "userGroup_2"
    Given User "member2" is added to userGroup "userGroup_2"
    When List of all users for user group "userGroup_2" is requested by user "<username>"
    Then Response code is 200
    And Total count is "2"
    When Relation between user group "userGroup_2" and user "member1" is requested by user "<username>"
    Then Response code is 200
    Examples:
      | username      | type     | firstName | lastName | email                         | timezone          | languageCode | id                                    |
      | userPartner1  | partner  | FNU1      | LNU1     | userPartner1@snapshot.travel  | Europe/Prague     | cs-CZ   | 11129079-48f0-4f00-9bec-e2329a8bdaac  |
      | userGuest     | guest    | FNU2      | LNU2     | userGuest@snapshot.travel     | America/New_York  | en-US   | 22229079-48f0-4f00-9bec-e2329a8bdaac  |
      | snapshotUser2 | snapshot | FNU3      | LNU3     | snaphostUser2@snapshot.travel | Asia/Tokyo        | en-US   | 33329079-48f0-4f00-9bec-e2329a8bdaac  |
      | userCustomer1 | customer | FNU4      | LNU4     | userCustomer1@snapshot.travel | Europe/Prague     | cs-CZ   | 44429079-48f0-4f00-9bec-e2329a8bdaac  |


  Scenario Outline: Send POST request with empty body to all user groups endpoints
    Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "false"
      | id                                   | type     | username  | firstName | lastName | email                | timezone      | languageCode |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snapUser1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given User "snapUser1" is added to userGroup "userGroup_1"
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                  |
      | identity/user_groups/a8b40d08-de38-4246-bb69-ad39c31c025c/users/5d829079-48f0-4f00-9bec-e2329a8bdaac |

  Scenario: Duplicate adding of User to User Group returns correct response - DP-1661
    Given User "snapshotUser1" is added to userGroup "userGroup_1" as isActive "true"
    Then Response code is "201"
    Given User "snapshotUser1" is added to userGroup "userGroup_1" as isActive "true"
    Then Response code is "409"
    And Custom code is 40902

  Scenario: User cannot be deleted until User Group is (and vice versa)
    When User "snapshotUser1" is removed from customer with id "45a5f9e4-5351-4e41-9d20-fdb4609e9353"
    Then Response code is 204
    Given User "snapshotUser1" is added to userGroup "userGroup_1"
    #    Prerequisites
    Then Response code is 201
    When User "snapshotUser1" is deleted
    Then Response code is 409
    And Custom code is 40915
    When User group "userGroup_1" is deleted
    Then Response code is 409
    And Custom code is 40915
    When User "snapshotUser1" is removed from userGroup "userGroup_1"
    Then Response code is "204"
    When User "snapshotUser1" is deleted
    Then Response code is 204
    When User group "userGroup_1" is deleted
    Then Response code is 204
