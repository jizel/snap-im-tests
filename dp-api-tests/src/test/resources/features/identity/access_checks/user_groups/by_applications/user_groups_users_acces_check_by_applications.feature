@Identity
Feature: User Groups - Users Application access check feature
  - Checking when certain application should and should not have access to certain Users of User Groups
  - Only Users of User Groups for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | customerId                           | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName             | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithUserGroup    | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | userWithoutUserGroup | Customer  | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | partnerId                            | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName          | applicationId                        | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | versionId                            | apiManagerId | versionName             | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    Given The following application versions exists
      | versionId                            | apiManagerId | versionName                | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
    Given The following user groups exist
      | customerId                           | name        | isActive | description          |
      | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"
    Given Relation between user "userWithUserGroup" and property with code "defaultPropertyCode" exists
    Given Relation between user "userWithoutUserGroup" and property with code "defaultPropertyCode" exists

  Scenario: Application sees only users of user group it has subscribed to
    When Relation between user group "userGroup_1" and user "userWithUserGroup" is requested by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When Relation between user group "userGroup_1" and user "userWithUserGroup" is requested by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 200
    When List of all users for user group "userGroup_1" is requested by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When List of all users for user group "userGroup_1" is requested by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 200
    And Total count is "1"

  Scenario: Add user to user group by application with and without access
    When User "userWithoutUserGroup" is added to userGroup "userGroup_1" with isActive "true" by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is "404"
    And Custom code is 40402
    When User "userWithoutUserGroup" is added to userGroup "userGroup_1" with isActive "true" by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is "201"
    Given The following customers exist with random address
      | customerId                           | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000111 | CustomerWithoutSubscription | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000111" as primary "false"
      | userType | userName                | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithoutSubscription | Customer  | User3    | cus3@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    When User "userWithoutSubscription" is added to userGroup "userGroup_1" with isActive "true" by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is "422"
    And Custom code is 42202

  Scenario: Updating User Group-User relationship by user who can access it
    When Is Active for relation between user group "userGroup_1" and user "userWithUserGroup" is set to "false" by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When Is Active for relation between user group "userGroup_1" and user "userWithUserGroup" is set to "false" by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 204

  Scenario: Deleting User User Group relationship by user who can access it
    When User "userWithUserGroup" is removed from userGroup "userGroup_1" by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When User "userWithUserGroup" is removed from userGroup "userGroup_1" by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 204