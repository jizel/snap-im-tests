@Identity
Feature: User Groups - Properties Application access check feature
  - Checking when certain application should and should not have access to certain Properties of User Groups
  - Only Properties of User Groups for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | customerId                           | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName          | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithUserGroup | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
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
    Given The following properties exist with random address and billing address
      | propertyId                           | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 33300000-0000-4000-a000-000000000333 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
      | 33300000-0000-4000-a000-000000000000 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 33300000-0000-4000-a000-000000000333 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
    Given The following user groups exist
      | customerId                           | name        | isActive | description          |
      | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"
    Given Relation between user "userWithUserGroup" and property with code "p1_code" exists with is_active "true"

  Scenario: Application sees only user group-properties relations for user groups and properties it has subscribed to
    Given Relation between user group "userGroup_1" and property with code "p1_code" exists
    When Relation between user group "userGroup_1" and property with code "p1_code" is requested by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 200
    When Relation between user group "userGroup_1" and property with code "p1_code" is requested by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When Relation between user group "userGroup_1" and property with code "p2_code" is requested by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 404
    And Custom code is 40402
    When List of all properties for user group "userGroup_1" is requested by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 200
    And Total count is "1"
    When List of all properties for user group "userGroup_1" is requested by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402

  Scenario: Add user group to property relationship by application with and without access
    When Relation between user group "userGroup_1" and property with code "p1_code" is created by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is "404"
    And Custom code is 40402
    When Relation between user group "userGroup_1" and property with code "p2_code" is created by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is "422"
    And Custom code is 42202
    When Relation between user group "userGroup_1" and property with code "p1_code" is created by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is "201"

  Scenario: Update user group to property relationship  by application with and without access
    Given Relation between user group "userGroup_1" and property with code "p1_code" exists
    When IsActive for relation between user group "userGroup_1" and property with code "p1_code" is set to "false" by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When IsActive for relation between user group "userGroup_1" and property with code "p1_code" is set to "false" by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 204

  Scenario: Delete userGroup-property relationship by application with and without access
    Given Relation between user group "userGroup_1" and property with code "p1_code" exists
    When Relation between user group "userGroup_1" and property with code "p1_code" is deleted is deleted by user "userWithUserGroup" for application version "versionWithoutSubscription"
    Then Response code is 404
    And Custom code is 40402
    When Relation between user group "userGroup_1" and property with code "p1_code" is deleted is deleted by user "userWithUserGroup" for application version "versionWithSubscription"
    Then Response code is 204