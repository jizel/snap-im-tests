@Identity
Feature: User Groups Application access check feature
  - Checking when certain application should and should not have access to certain User Groups
  - Only User Groups for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName          | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithUserGroup | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | Id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName          | Id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName             | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName                | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | Id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | Id                                   | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
    Given The following user groups exist
      | Id                                   | name        | isActive | description          |
      | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"


    Scenario: Get user group by application with and without access
      When User group "userGroup_1" is requested by user "userWithUserGroup" for application version "versionWithoutSubscription"
      Then Response code is "404"
      And Custom code is 40402
      When User group "userGroup_1" is requested by user "userWithUserGroup" for application version "versionWithSubscription"
      Then Response code is "200"

#      Check tommorow, getAll access checks are fucked now
    Scenario Outline: Filtering user groups with application access checks
      Given The following user groups exist
        | userGroupId                          | Id                                   | name        | isActive | description           |
        | 22345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_2 | false    | userGroupDescription2 |
        | 32345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_3 | false    | userGroupDescription3 |
        | 42345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_4 | true     | userGroupDescription4 |
        | 52345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_5 | true     | userGroupDescription5 |
      Given User "userWithUserGroup" is added to userGroup "userGroup_2"
      Given User "userWithUserGroup" is added to userGroup "userGroup_3"
      When List of user groups is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithUserGroup" for application version "versionWithSubscription"
      Then Response code is "200"
      And There are "<returned>" user groups returned
      When List of user groups is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithUserGroup" for application version "versionWithoutSubscription"
      Then Response code is "200"
      And There are "0" user groups returned
      Examples:
        | limit | cursor | filter                          | sort           | sort_desc           | returned    |
        | /null | 0      | name=='*'                       | /null          | is_active           | 3           |
        | /null | 0      | name=='Company 5'               | /null          | name                | 0           |
        | /null | 0      | user_group_id=='22345000-*'     | /null          | picture             | 1           |
        | /null | 0      | is_active=='false'              | name           | /null               | 2           |
        | /null | 0      | description=='*'                | is_active      | /null               | 3           |
        | /null | 0      | customer_id=='123*'             | /null          | /null               | 3           |

    Scenario: Creating User Group is possible only with customer id with valid commercial subscription
      Given The following customers exist with random address
        | Id                                   | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
        | 23400000-0000-4000-a000-000000000000 | CustomerWithNoSubscription  | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      When The following user group is created by user "userWithUserGroup" for application version "versionWithoutSubscription"
        | Id                                   | name        | isActive | description          |
        | 12300000-0000-4000-a000-000000000000 | userGroup_2 | false    | userGroup2Description |
      Then Response code is 404
      And Custom code is 40402
      When The following user group is created by user "userWithUserGroup" for application version "versionWithSubscription"
        | Id                                   | name        | isActive | description          |
        | 23400000-0000-4000-a000-000000000000 | userGroup_2 | false    | userGroup2Description |
      Then Response code is 404
      And Custom code is 40402
      When The following user group is created by user "userWithUserGroup" for application version "versionWithSubscription"
        | Id                                   | name        | isActive | description          |
        | 12300000-0000-4000-a000-000000000000 | userGroup_2 | false    | userGroup2Description |
      Then Response code is 201

    Scenario: Application with and without access updates user group
      When User group "userGroup_1" is updated with following data by user "userWithUserGroup" for application version "versionWithoutSubscription"
        | name        | isActive   | description   |
        | updatedName | true       | udpatedDesc   |
      Then Response code is 404
      And Custom code is 40402
      When User group "userGroup_1" is updated with following data by user "userWithUserGroup" for application version "versionWithSubscription"
        | name        | isActive   | description   |
        | updatedName | true       | udpatedDesc   |
      Then Response code is 204

    Scenario: Delete user group by application with and without access
      When User group "userGroup_1" is deleted by user "userWithUserGroup" for application version "versionWithoutSubscription"
      Then Response code is 404
      And Custom code is 40402
      When User group "userGroup_1" is deleted by user "userWithUserGroup" for application version "versionWithSubscription"
  #    409 is good enough to check access checks work
      Then Response code is 409