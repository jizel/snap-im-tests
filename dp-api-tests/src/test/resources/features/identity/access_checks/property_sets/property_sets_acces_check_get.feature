@Identity
Feature: Property sets access check feature - GET
  - Checking when certain user should and should not have access to certain property sets
  - User should have access to a property set (instance) when
    - User has a relationship to the instance
    - User is a member of a UserGroup which has a relationship to the instance
    - User has a relationship to any PropertySet that has a successor (in the PropertySet parent->child mapping) which has relationship to this instance
    - User is a member of a UserGroup that has relationship to a PropertySet that has a a successor
  - User type Snapshot has access to all entities (other user types are equal)
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)`
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (properties)

  Background:
  Given Database is cleaned and default entities are created

  Given The following customers exist with random address
    | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
    | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
  Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
    | userId                               | userType | userName          | firstName | lastName | email                | timezone      | culture | isActive |
    | 0d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithPropSet   | Customer1 | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    | 1d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoPropSet | Customer2 | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithPropSet"
      | propertySetName | propertySetType | propertySetId                        |
      | ps1_name        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 |

    Scenario: User has direct relationship to property set
      When Property set "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "200"
      When Property set "ps1_name" is requested by user "userWithNoPropSet"
      Then Response code is "404"
      And Custom code is 40402

    Scenario: User belongs to User Group that has access to property set
      Given The following user groups exist
        | userGroupId                          | customerId                           | name        | isActive |
        | a8b40d08-de38-4246-bb69-ad39c31c025c | 1238fd9a-a05d-42d8-8e84-42e904ace123 | userGroup_1 | false    |
      When Relation between user group "userGroup_1" and property set "ps1_name" exists with isActive "true"
      When Property set "ps1_name" is requested by user "userWithNoPropSet"
      Then Response code is "404"
      When User "userWithNoPropSet" is added to userGroup "userGroup_1"
      When Property set "ps1_name" is requested by user "userWithNoPropSet"
      Then Response code is "200"


    Scenario: User has a relationship to any PropertySet that has a successor which has relationship to this instance
      Given The following customers exist with random address
        | customerId                           | companyName     | email          | salesforceId  | vatId       | isDemoCustomer | phone         | website                    | timezone      |
        | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Given company 2 | c2@tenants.biz | salesforceid_2 | CZ20000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      Given The following property sets exist for customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithnoPropSet"
        | propertySetName | propertySetType | parentId                             | propertySetId                        |
        | childPS1        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      Given The following property sets exist for customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithnoPropSet"
        | propertySetName | propertySetType | parentId                             |
        | childPS2        | brand           | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      When Property set "childPS2" is requested by user "userWithPropSet"
      Then Response code is "200"

    Scenario: User belongs to User Group that has a relation to a PropertySet that has a successor which has relationship to this instance
      Given The following user groups exist
        | userGroupId                          | customerId                           | name        | isActive |
        | a8b40d08-de38-4246-bb69-ad39c31c025c | 1238fd9a-a05d-42d8-8e84-42e904ace123 | userGroup_1 | false    |
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithPropSet"
        | propertySetName | propertySetType | parentId                             | propertySetId                        |
        | childPS1        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithPropSet"
        | propertySetName | propertySetType | parentId                             |
        | childPS2        | brand           | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      When Relation between user group "userGroup_1" and property set "ps1_name" exists with isActive "true"
      When Property set "childPS2" is requested by user "userWithNoPropSet"
      Then Response code is "404"
      When User "userWithNoPropSet" is added to userGroup "userGroup_1"
      When Property set "childPS2" is requested by user "userWithNoPropSet"
      Then Response code is "200"

    Scenario: User type Snapshot has access to all entities (other user types are equal)
      Given The following customers exist with random address
        | customerId                           | companyName     | email          | salesforceId  | vatId       | isDemoCustomer | phone         | website                    | timezone      |
        | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Given company 2 | c2@tenants.biz | salesforceid_2 | CZ20000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      Given The following users exist for customer "2348fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
        | userType | userName  | firstName | lastName | email                | timezone      | culture | isActive |
        | snapshot | snapshot1 | Snapshot1 | User1    | sna1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
        | guest    | guest1    | Guest1    | User1    | gue1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
        | partner  | partner1  | Partner1  | User1    | par1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
        | customer | customer1 | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      When Property set "ps1_name" is requested by user "snapshot1"
      Then Response code is "200"
      When Property set "ps1_name" is requested by user "guest1"
      Then Response code is "404"
      And Custom code is 40402
      When Property set "ps1_name" is requested by user "partner1"
      Then Response code is "404"
      And Custom code is 40402
      When Property set "ps1_name" is requested by user "customer1"
      Then Response code is "404"
      And Custom code is 40402

     Scenario Outline: Filtering property sets with access checks
       Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithPropSet"
         | propertySetName | propertySetType | propertySetId                        | propertySetDescription | isActive |
         | ps2_name        | brand           | 23400000-1111-4c57-91bd-30230d2c1bd0 | desc2                  | true     |
         | ps3_name        | brand           | 33400000-1111-4c57-91bd-30230d2c1bd0 | desc3                  | true     |
         | ps4_name        | GEOLOCATION     | 43400000-1111-4c57-91bd-30230d2c1bd0 | desc4                  | false    |
         | ps5_name        | HOTEL_TYPE      | 53400000-1111-4c57-91bd-30230d2c1bd0 | desc5                  | false    |
       When List of property sets is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithPropSet"
       Then Response code is "200"
       And There are <returned> property sets returned
       When List of property sets is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithNoPropSet"
       Then Response code is "200"
       And There are 0 properties returned
       Examples:
         | limit | cursor | filter                        | sort        | sort_desc         | returned    |
         | /null | 0      | name=='*'                     | /null       | name              | 5           |
         | /null | 0      | name=='ps5_name'              | /null       | /null             | 1           |
         | /null | 0      | is_active=='true'             | /null       | property_set_type | 2           |
         | /null | 0      | property_set_type=='brand'    | /null       | property_set_type | 3           |
         | /null | 0      | description=='desc*'          | description | /null             | 4           |
         | /null | 0      | property_set_id=='43400000-*' | /null       | /null             | 1           |


#      -----------------------------< Second level entities accessibility check >------------------------------------

#    property_sets/ps_id/property_sets

  Scenario: Second level entities - User should see only child property sets of property set he has access to
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithnoPropSet"
      | propertySetName | propertySetType | parentId                             | propertySetId                        |
      | childPS1        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
    When Child property sets of property set "ps1_name" are requested by user "userWithPropSet"
    Then Response code is "200"
    When Child property sets of property set "ps1_name" are requested by user "userWithNoPropSet"
#      Fails until DP-1330 fixed
    Then Response code is "404"

#    General negative scenarios

    Scenario Outline: User with no access rights to property sends GET request to all general second level endpoints
      When GET request is sent to "<url>" on module "identity" by user "userWithNoPropSet"
      #      Fails until DP-1330 fixed
      Then Response code is "404"
      And Custom code is "40402"
      Examples:
        | url                                                                       |
        | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/users         |
        | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/properties    |
        | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/property_sets |

     Scenario Outline: User with no access rights to property sends GET request with parameters
       When GET request is sent to "<url>" on module "identity" by user "userWithNoPropSet"
       #      Fails until DP-1330 fixed
       Then Response code is "404"
       And Custom code is "40402"
       Examples:
         | url                                                                                                                      |
         | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/users?sortDesc=user_id&cursor=0                              |
         | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/properties?limit=55&filter=property_code=='*'                |
         | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/property_sets?filter=property_id=='*'&sort='property_set_id' |


    Scenario Outline: Unauthorized request - GET request is send to all endpoints without X-Auth-UserId header
      When GET request is sent to "<url>" on module "identity" without X-Auth-UserId header
      Then Response code is "403"
      And Custom code is "40301"
      Examples:
        | url                                                                       |
        | identity/property_sets                                                    |
        | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/              |
        | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/users         |
        | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/properties    |
        | identity/property_sets/12300000-1111-4c57-91bd-30230d2c1bd0/property_sets |