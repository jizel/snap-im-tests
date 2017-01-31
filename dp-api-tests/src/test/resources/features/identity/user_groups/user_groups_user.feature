Feature: User groups user relationship feature

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName        | email          | salesforceId | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | UserGroupsCustomer | ug@tenants.biz | ug_sf_1      | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given Default Snapshot user is created
    Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "true"
      | userId                                | userType   | userName      | firstName | lastName | email                         | timezone      | culture |
      | 00029079-48f0-4f00-9bec-e2329a8bdaac  | snapshot   | snapshotUser1 | Snapshot  | User1    | snaphostUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following user groups exist
      | userGroupId                          | customerId                           | name        | isActive | description          |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_1 | false    | userGroupDescription |


  Scenario Outline: Users are added to User Group (valid)
      Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "true"
        | userType   | userName   | firstName   | lastName  | email   | timezone   | culture   |
        | <userType> | <userName> | <firstName> | <lastName>| <email> | <timezone> | <culture> |
      Given User "<userName>" is added to userGroup "userGroup_1"
      Then Response code is "201"
      And Body contains entity with attribute "is_active" value "true"
      Examples:
    | userName     | userType | firstName | lastName | email                        | timezone          | culture |
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
      | user_id                              | is_active | error_response | code  | #note                     |
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
    Then Response code is 412
    And Body contains entity with attribute "message" value "Precondition failed: ETag not present."

  Scenario: Activate relationship userGroup-user
    Given User "snapshotUser1" is added to userGroup "userGroup_1" as isActive "false"
    When Relation between user group "userGroup_1" and user "snapshotUser1" is activated
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and user "snapshotUser1" is active

  Scenario: Deactivate relationship userGroup-property
    Given User "snapshotUser1" is added to userGroup "userGroup_1" as isActive "true"
    When Relation between user group "userGroup_1" and user "snapshotUser1" is deactivated
    Then Response code is 204
    And Body is empty
    And Relation between user group "userGroup_1" and user "snapshotUser1" is not active

    Scenario: Add one User to multiple User Groups
      Given The following user groups exist
        | customerId                           | name        | isActive | description          |
        | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_2 | false    | userGroupDescription |
      Given User "snapshotUser1" is added to userGroup "userGroup_1" as isActive "true"
      Then Response code is "201"
      Given User "snapshotUser1" is added to userGroup "userGroup_2" as isActive "true"
      Then Response code is "201"
      When Relation between user group "userGroup_1" and user "snapshotUser1" is got
      Then Response code is 200
      When Relation between user group "userGroup_2" and user "snapshotUser1" is got
      Then Response code is 200

  Scenario Outline: Creator of User Group automatically becomes it's member
    Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "true"
      | userType   | userName   | firstName   | lastName  | email   | timezone   | culture   | userId   |
      | <userType> | <userName> | <firstName> | <lastName>| <email> | <timezone> | <culture> | <userId> |
    Given The following user group is created by user "<userName>"
      | userGroupId                          | customerId                           | name        | isActive | description          |
      | 12340d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_2 | false    | userGroupDescription |
    Then Response code is "201"
    When Relation between user group "userGroup_2" and user "<userName>" is got
    Then Response code is 200
    And Body contains entity with attribute "user_id" value "<userId>"
    And Body contains entity with attribute "is_active" value "true"
    Examples:
      | userName     | userType | firstName | lastName | email                        | timezone          | culture | userId                                |
      | userGuest    | guest    | FNU2      | LNU2     | userGuest@snapshot.travel    | America/New_York  | en-US   | 22229079-48f0-4f00-9bec-e2329a8bdaac  |
      | userPartner1 | partner  | FNU1      | LNU1     | userPartner1@snapshot.travel | Europe/Prague     | cs-CZ   | 11129079-48f0-4f00-9bec-e2329a8bdaac  |
      | snaphostUser2| snapshot | FNU3      | LNU3     | snaphostUser2@snapshot.travel| Asia/Tokyo        | en-US   | 33329079-48f0-4f00-9bec-e2329a8bdaac  |

  Scenario: Snapshot type user - Creator of User Group won't automatically become
    When Relation between user group "userGroup_1" and user "snapshotUser1" is got
    Then Response code is 404
    And Custom code is 40402
#    Use correct msg when DP-1581 is fixed and it's clear what msg should be used
    And Body contains entity with attribute "message" value "Current msg is wrong - DP-1581"

  Scenario Outline: Send POST request with empty body to all user groups endpoints
    Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "false"
      | userId                               | userType | userName  | firstName | lastName | email                | timezone      | culture |
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