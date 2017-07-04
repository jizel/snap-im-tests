@Identity
Feature: User Groups access check feature - GET
  - Checking when certain user should and should not have access to certain User Group
  - User should have access to a User Group (instance) when
    - User has a relationship to the instance
  - User type Snapshot has access to all entities (other user types are equal)
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)
  - All rules apply also to second level entities in both ways (e.g. user_groups/ug_id/properties, properties/ug_id/user_groups) - reversed endpoints should be covered in other features (properties)

  Background:
  Given Database is cleaned and default entities are created
  Given The following customers exist with random address
    | id                                   | name        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
    | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
  Given The following user groups exist
    | id                                   | customerId                           | name        | isActive | description          |
    | 12345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_1 | false    | userGroupDescription |
  Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
    | id                                   | type     | username            | firstName | lastName | email                | timezone      | languageCode | isActive |
    | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithUserGroup   | Customer  | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoUserGroup | Customer  | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given User "userWithUserGroup" is added to userGroup "userGroup_1"


    Scenario: User has direct relationship to User Group
      When User group "userGroup_1" is requested by user "userWithUserGroup"
      Then Response code is "200"
      When Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
      And User group "userGroup_1" is requested by user "userWithUserGroup"
      Then Response code is "404"
      When User group "userGroup_1" is requested by user "userWithNoUserGroup"
      Then Response code is "404"
      And Custom code is 40402

    Scenario: User type Snapshot has access to all entities (other user types are equal)
      Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
        | type     | username  | firstName | lastName | email                | timezone      | languageCode | isActive |
        | snapshot | snapshot1 | Snapshot1 | User1    | sna1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
        | guest    | guest1    | Guest1    | User1    | gue1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
        | partner  | partner1  | Partner1  | User1    | par1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      When User group "userGroup_1" is requested by user "snapshot1"
      Then Response code is "200"
      When User group "userGroup_1" is requested by user "guest1"
      Then Response code is "404"
      And Custom code is 40402
      When User group "userGroup_1" is requested by user "partner1"
      Then Response code is "404"
      And Custom code is 40402
      Given Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
      When User group "userGroup_1" is requested by user "snapshot1"
      Then Response code is "200"
      When User group "userGroup_1" is requested by user "guest1"
      Then Response code is "404"
      And Custom code is 40402
      When User group "userGroup_1" is requested by user "partner1"
      Then Response code is "404"
      And Custom code is 40402

     Scenario Outline: Filtering user groups with access checks
       Given The following user groups exist
         | id                                   | customerId                           | name        | isActive | description           |
         | 22345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_2 | false    | userGroupDescription2 |
         | 32345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_3 | false    | userGroupDescription3 |
         | 42345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_4 | true     | userGroupDescription4 |
         | 52345000-1111-4000-a000-000000000000 | 12300000-0000-4000-a000-000000000000 | userGroup_5 | true     | userGroupDescription5 |
       Given User "userWithUserGroup" is added to userGroup "userGroup_2"
       Given User "userWithUserGroup" is added to userGroup "userGroup_5"
       When List of user groups is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithUserGroup"
       Then Response code is "200"
       And There are "<returned>" user groups returned
       Given Relation between user group "userGroup_1" and user "userWithUserGroup" is deactivated
       Given Relation between user group "userGroup_2" and user "userWithUserGroup" is deactivated
       Given Relation between user group "userGroup_5" and user "userWithUserGroup" is deactivated
       When List of user groups is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithUserGroup"
       Then Response code is "200"
       And There are "0" user groups returned
       Examples:
         | limit | cursor | filter                          | sort           | sort_desc           | returned    |
         | /null | 0      | name=='*'                       | /null          | is_active           | 3           |
         | /null | 0      | name=='Company 5'               | /null          | name                | 0           |
         | /null | 0      | is_active=='false'              | name           | /null               | 2           |
         | /null | 0      | description=='*'                | is_active      | /null               | 3           |

#      -----------------------------< Second level entities accessibility check - General negative scenarios >------------------------------------
 
#    DP-1677
    @skipped
    Scenario Outline: User with no access rights to property sends GET request with parameters
       Given The following property is created with random address and billing address for user "12329079-48f0-4f00-9bec-e2329a8bdaac"
         | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
         | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
       Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "false"
       Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithUserGroup"
         | id                                   | name            | type            |
         | 888e833e-50e8-4854-a233-289f00b54a09 | prop_set1       | brand           |
       Given Relation between user group "userGroup_1" and property set "prop_set1" exists with isActive "true"
       When GET request is sent to "<url>" on module "identity" by user "userWithNoUserGroup"
       Then Response code is "404"
       And Custom code is "40402"
       Examples:
         | url                                                                                                                    |
         | identity/user_groups/12345000-1111-4000-a000-000000000000/users?sortDesc=user_id&cursor=0                              |
         | identity/user_groups/12345000-1111-4000-a000-000000000000/properties?limit=55&filter=property_code=='*'                |
         | identity/user_groups/12345000-1111-4000-a000-000000000000/property_sets?filter=property_id=='*'&sort='property_set_id' |
         | identity/user_groups/12345000-1111-4000-a000-000000000000/roles?limit=10&filter=role_id=='123*'                        |
         | identity/user_groups/12345000-1111-4000-a000-000000000000/properties/999e833e-50e8-4854-a233-289f00b54a09/roles?filter=property_code=='*'&sort=is_active |
         | identity/user_groups/12345000-1111-4000-a000-000000000000/property_sets/888e833e-50e8-4854-a233-289f00b54a09/roles?filter=name=='hh*'&sort_desc=is_active |


    Scenario Outline: Unauthorized request - GET request is send to all endpoints without X-Auth-UserId header
      Given The following property is created with random address and billing address for user "12329079-48f0-4f00-9bec-e2329a8bdaac"
        | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
        | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
      Given Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "false"
      Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithUserGroup"
        | id                                   | name            | type            |
        | 888e833e-50e8-4854-a233-289f00b54a09 | prop_set1       | brand           |
      Given Relation between user group "userGroup_1" and property set "prop_set1" exists with isActive "false"
      When GET request is sent to "<url>" on module "identity" without X-Auth-UserId header
      Then Response code is "403"
      And Custom code is "40301"
      Given IsActive for relation between user group "userGroup_1" and property with code "p1_code" is set to "true" by user "userWithUserGroup"
      And IsActive relation between user group "userGroup_1" and property set "prop_set1" is set to "true" by user "userWithUserGroup"
      When GET request is sent to "<url>" on module "identity" without X-Auth-UserId header
      Then Response code is "403"
      And Custom code is "40301"
      Examples:
        | url                                                                                                                |
        | identity/customers                                                                                                 |
        | identity/user_groups/12345000-1111-4000-a000-000000000000/                                                         |
        | identity/user_groups/12345000-1111-4000-a000-000000000000/users                                                    |
        | identity/user_groups/12345000-1111-4000-a000-000000000000/properties                                               |
        | identity/user_groups/12345000-1111-4000-a000-000000000000/property_sets                                            |
        | identity/user_groups/12345000-1111-4000-a000-000000000000/roles                                                    |
        | identity/user_groups/12345000-1111-4000-a000-000000000000/properties/999e833e-50e8-4854-a233-289f00b54a09/roles    |
        | identity/user_groups/12345000-1111-4000-a000-000000000000/property_sets/888e833e-50e8-4854-a233-289f00b54a09/roles |
