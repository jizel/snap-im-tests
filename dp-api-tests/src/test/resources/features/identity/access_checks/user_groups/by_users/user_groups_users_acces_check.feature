@Identity
Feature: User Groups Users access check feature
  - Checking when certain user should and should not have access to certain User Group
  - User can update/delete specific user group only if he has a relationship
  - Check positive scenarios
  - In negative tests, ETAG needs to be obtained by different user first
  - User type Snapshot has access to all entities (other user types are equal)
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)
  - All rules apply also to second level entities in both ways (e.g. user_groups/ug_id/properties, properties/ug_id/user_groups) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
    Given The following user groups exist
      | Id                                   | customerId                           | name        | isActive | description          |
      | 12345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | Id                                   | userType | userName            | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithUserGroup   | Customer  | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoUserGroup | Customer  | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"


    Scenario: User sees only users of the same user group
      When Relation between user group "userGroup_1" and user "userWithUserGroup" is requested by user "userWithUserGroup"
      Then Response code is 200
      When List of all users for user group "userGroup_1" is requested by user "userWithUserGroup"
      Then Response code is 200
      And Total count is "1"
      Given User "userWithNoUserGroup" is added to userGroup "userGroup_1"
      When List of all users for user group "userGroup_1" is requested by user "userWithNoUserGroup"
      Then Response code is 200
      And Total count is "2"

     Scenario: User does not see user group-users relations when he cannot access the user group
       When Relation between user group "userGroup_1" and user "userWithNoUserGroup" is requested by user "userWithNoUserGroup"
       Then Response code is 404
       And Custom code is 40402
       When List of all users for user group "userGroup_1" is requested by user "userWithNoUserGroup"
       Then Response code is 404
       And Custom code is 40402

     Scenario: User does not see user group-users relations when he cannot access the user
       When Relation between user group "userGroup_1" and user "userWithNoUserGroup" is requested by user "userWithUserGroup"
       Then Response code is 404
       And Custom code is 40402

       #   Might need refactoring when user access checks are fully implemented! - DP-1548
    Scenario: Add user to user group by user who has access to the user group
      Given User "userWithNoUserGroup" is added to userGroup "userGroup_1" with isActive "true" by user "userWithUserGroup"
      Then Response code is "201"
      When Relation between user group "userGroup_1" and user "userWithNoUserGroup" is requested by user "userWithNoUserGroup"
      Then Response code is 200

    Scenario: Add user to user group by user without access to the user group
      Given User "userWithNoUserGroup" is added to userGroup "userGroup_1" with isActive "true" by user "userWithNoUserGroup"
      Then Response code is "404"
      And Custom code is 40402

    Scenario: Updating User Group-User relationship by user who can access it
      When Is Active for relation between user group "userGroup_1" and user "userWithUserGroup" is set to "false" by user "userWithUserGroup"
      Then Response code is 204
      And Body is empty
      And Relation between user group "userGroup_1" and user "userWithUserGroup" is not active

    Scenario: Updating User Group-User relationship by user who cannot access the user group
      When Is Active for relation between user group "userGroup_1" and user "userWithNoUserGroup" is set to "false" by user "userWithNoUserGroup"
      Then Response code is 404
      And Custom code is 40402

    Scenario: Deleting User User Group relationship by user who can access it
      When Relation between user group "userGroup_1" and user "userWithUserGroup" is requested by user "userWithUserGroup"
      Then Response code is "200"
      When User "userWithUserGroup" is removed from userGroup "userGroup_1" by user "userWithUserGroup"
      Then Response code is 204
      When Relation between user group "userGroup_1" and user "userWithUserGroup" is requested by user "userWithUserGroup"
      Then Response code is 404

    Scenario: Deleting User User Group relationship by user who cannot access the user group
      When Relation between user group "userGroup_1" and user "userWithNoUserGroup" is requested by user "userWithNoUserGroup"
      Then Response code is 404