@Identity
Feature: Customers access check feature - POST and DELETE
  - User can update/delete specific user group only if he has a relationship
  - Check positive scenarios
  - In negative tests, ETAG needs to be obtained by different user first
  - Various cases of when user should have access to a customer (instance) are covered in GET feature (user is a member of userGroup, child customers etc.)
  - 404 is returned for unauthorized users
  - All rules apply also to second level entities in both ways (e.g. user_groups/ug_id/properties, properties/ug_id/user_groups) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
    Given The following user groups exist
      | userGroupId                          | customerId                           | name        | isActive | description          |
      | 12345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userId                               | userType | userName            | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithUserGroup   | Customer  | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoUserGroup | Customer  | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"


  Scenario: User with access updates user group
    When User group "userGroup_1" is updated with following data by user "userWithUserGroup"
      | name        | isActive   | description   |
      | updatedName | true       | udpatedDesc   |
    Then Response code is 204
    When User group "updatedName" is requested by user "userWithUserGroup"
    Then Response code is "200"
    And Body contains entity with attribute "name" value "updatedName"
    And Body contains entity with attribute "description" value "udpatedDesc"
    And Body contains entity with attribute "is_active" value "true"

  Scenario: User without access tries to update user group, user loses access when relation is deactivated
    When User group "userGroup_1" is updated with following data by user "userWithNoUserGroup"
      | name        | isActive   | description   |
      | updatedName | true       | udpatedDesc   |
    Then Response code is "404"
    And Custom code is 40402
    When Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
    When User group "userGroup_1" is updated with following data by user "userWithUserGroup"
      | name        | isActive   | description   |
      | updatedName | true       | udpatedDesc   |
    Then Response code is 404

#  DP-1691
  @skipped
  Scenario: Delete user group by user with access
    When User group "userGroup_1" is deleted by user "userWithUserGroup"
    Then Response code is 204
    And Body is empty
    And User group with id "12345000-1111-4000-a000-000000000000" is no more exists

  Scenario: Delete user group by user without access, user loses access when relation is deactivated
    When User group "userGroup_1" is deleted by user "userWithNoUserGroup"
    Then Response code is 404
    When User group "userGroup_1" is requested by user "userWithUserGroup"
    Then Response code is "200"
    When Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
    When User group "userGroup_1" is deleted by user "userWithUserGroup"
    Then Response code is 404