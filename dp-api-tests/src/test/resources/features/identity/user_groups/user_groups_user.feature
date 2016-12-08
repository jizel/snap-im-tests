Feature: User groups user relationship feature

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName        | email          | salesforceId | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | UserGroupsCustomer | ug@tenants.biz | ug_sf_1      | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given Default Snapshot user is created for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353"
    Given The following users exist for customer "45a5f9e4-5351-4e41-9d20-fdb4609e9353" as primary "true"
      | userId                                | userType   | userName      | firstName | lastName | email                         | timezone      | culture |
      | 00029079-48f0-4f00-9bec-e2329a8bdaac  | snapshot   | snapshotUser1 | Snapshot  | User1    | snaphostUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following user groups exist
      | userGroupId                          | customerId                           | name        | isActive | description          |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_1 | false    | userGroupDescription |


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
#   | snaphostUser2| snapshot | FNU3      | LNU3     | snaphostUser2@snapshot.travel| Asia/Tokyo        | en-US   | 33329079-48f0-4f00-9bec-e2329a8bdaac  |

  Scenario: Snapshot type user - Creator of User Group won't automatically become
    When Relation between user group "userGroup_1" and user "snapshotUser1" is got
    Then Response code is 404
    And Custom code is 40402
#    Use correct msg when DP-1581 is fixed and it's clear what msg should be used
    And Body contains entity with attribute "message" value "Current msg is wrong - DP-1581"