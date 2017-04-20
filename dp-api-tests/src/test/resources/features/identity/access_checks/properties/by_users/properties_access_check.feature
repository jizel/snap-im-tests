@Identity
@Properties
Feature: Properties access check feature - GET
  - Checking when certain user should and should not have access to certain properties
  - User should have access to a property (instance) when
    - User has a relationship to the instance
    - User is a member of a UserGroup which has a relationship to the instance
    - User has a relationship to any PropertySet that has a relationship to this instance
    - User has a relationship to any PropertySet that has a successor (in the PropertySet parent->child mapping) which has relationship to this instance
    - User is a member of a UserGroup that has relationship to a PropertySet that has a relationship to this instance
  - User type Snapshot has access to all entities (other user types are equal)
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)`
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (property_sets)

  Background:
  Given Database is cleaned and default entities are created
  Given The following customers exist with random address
    | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
    | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
  Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
  Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
    | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
    | customer | userWithProp   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    | customer | userWithNoProp | Customer2 | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
  Given The following property is created with random address and billing address for user "userWithProp"
    | id                                   | salesforceId   | name | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
    | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
  Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"

    Scenario: Inactive user-property relation does not grant user access to property
      Given Relation between user "userWithNoProp" and property with code "p1_code" exists with is_active "false"
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When Relation between user "userWithNoProp" and property with code "p1_code" is activated
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "200"

    Scenario: User has direct relationship to property
      When Property with code "p1_code" is requested by user "userWithProp"
      Then Response code is "200"
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      And Custom code is 40402

    Scenario: User belongs to User Group that has access to property
      Given The following user groups exist
        | id                                   | customerId                           | name        | isActive |
        | a8b40d08-de38-4246-bb69-ad39c31c025c | 1238fd9a-a05d-42d8-8e84-42e904ace123 | userGroup_1 | false    |
      When Relation between user group "userGroup_1" and property with code "p1_code" exists with isActive "false"
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When User "userWithNoProp" is added to userGroup "userGroup_1"
      And Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When Relation between user group "userGroup_1" and property "p1_code" is activated
      And Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "200"


    Scenario: User has a relationship to any PropertySet that has a relationship to this instance
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp"
        | name            | type            |
        | ps1_name        | brand           |
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When Property with code "p1_code" is added to property set "ps1_name" with is_active "false"
      And Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When Relation between property "p1_code" and property set "ps1_name" is activated
      And Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "200"


    Scenario: User has a relationship to any PropertySet that has a successor which has relationship to this instance
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp"
        | name            | type            | id                                   |
        | parentPS        | brand           | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
        | name            | type            | parentId                             | id                                   |
        | childPS1        | brand           | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
        | name            | type            | parentId                             |
        | childPS2        | brand           | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When Property with code "p1_code" is added to property set "childPS2" with is_active "false"
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When Relation between property "p1_code" and property set "childPS2" is activated
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "200"

    Scenario: User belongs to User Group that has a relation to a PropertySet that has a relation to the property
      Given The following user groups exist
        | customerId                           | name        | isActive |
        | 1238fd9a-a05d-42d8-8e84-42e904ace123 | userGroup_1 | false    |
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
        | name            | type            |
        | ps1_name        | brand           |
      When Relation between user group "userGroup_1" and property set "ps1_name" exists with isActive "true"
      And Property with code "p1_code" is added to property set "ps1_name"
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When User "userWithNoProp" is added to userGroup "userGroup_1"
      And Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "200"
      When Relation between user group "userGroup_1" and property set "ps1_name" is inactivated
      And Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"


    Scenario: User type Snapshot has access to all entities (other user types are equal)
      Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
        | userType | userName  | firstName | lastName | email                | timezone      | culture | isActive |
        | snapshot | snapshot1 | Snapshot1 | User1    | sna1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
        | guest    | guest1    | Guest1    | User1    | gue1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
        | partner  | partner1  | Partner1  | User1    | par1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      When Property with code "p1_code" is requested by user "snapshot1"
      Then Response code is "200"
      When Property with code "p1_code" is requested by user "guest1"
      Then Response code is "404"
      And Custom code is 40402
      When Property with code "p1_code" is requested by user "partner1"
      Then Response code is "404"
      And Custom code is 40402

     Scenario Outline: Filtering properties with access checks
       Given The following properties exist with random address and billing address for user "userWithProp"
         | salesforceId   | name | propertyCode | email              | website                    | isDemoProperty | timezone      | anchorCustomerId                     | ttiId |
         | salesforceid_2 | p2_name      | p2_code      | p2@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 1234  |
         | salesforceid_3 | p3_name      | p3_code      | p3@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 4231  |
         | salesforceid_4 | p4_name      | p4_code      | p4@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 5678  |
         | salesforceid_5 | p5_name      | p5_code      | p5@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 8765  |
       Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p2_code"
       Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p3_code"
       Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p4_code"
       Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p5_code"
       When List of properties is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithProp"
       Then Response code is "200"
       And There are <returned> properties returned
       When List of properties is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>" by user "userWithNoProp"
       Then Response code is "200"
       And There are 0 properties returned
       Examples:
         | limit | cursor | filter                                                     | sort           | sort_desc           | returned    |
         | /null | 0      | name=='*'                                                  | /null          | website             | 5           |
         | /null | 0      | name=='p5_name'                                            | /null          | /null               | 1           |
         | /null | 0      | website=='*www.*'                                          | /null          | /null               | 5           |
         | /null | 0      | property_code=='*_code'                                    | property_code  | /null               | 5           |
         | /null | 0      | property_code=='p3_code'                                   | /null          | /null               | 1           |
         | /null | 0      | is_active=='true'                                          | /null          | salesforce_id       | 5           |
         | /null | 0      | anchor_customer_id=='*23'                                  | /null          | anchor_customer_id  | 5           |
         | /null | 0      | anchor_customer_id=='2228fd9a-a05d-42d8-8e84-42e904ace222' | /null          | /null               | 0           |
         | /null | 0      | tti_id=='*23*'                                             | /null          | /null               | 2           |
         | /null | 0      | is_demo_property=='true'                                   | email          | /null               | 5           |
         | /null | 0      | email=='*@snapshot.travel'                                 | property_id   | /null                | 4           |
         | /null | 0      | property_id=='999e833e-*'                                  | salesforce_id  | /null               | 1           |


#      -----------------------------< Second level entities General negative scenarios >------------------------------------

    Scenario Outline: User with no access rights to property sends GET request to all general second level endpoints
      When GET request is sent to "<url>" on module "identity" by user "userWithNoProp"
      Then Response code is "404"
      And Custom code is "40402"
      Examples:
        | url                                                                               |
#        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/api_subscriptions        |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/commercial_subscriptions |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/users                    |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/customers                |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/property_sets            |

     Scenario Outline: User with no access rights to property sends GET request with parameters
       When GET request is sent to "<url>" on module "identity" by user "userWithNoProp"
       Then Response code is "404"
       And Custom code is "40402"
       Examples:
         | url                                                                                                                             |
#         | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/api_subscriptions?sort=application_version_id&filter=is_active=='true' |
         | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/commercial_subscriptions?filter=is_active=='false'&sort=customer_id    |
         | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/users?sortDesc=user_id&cursor=0                                        |
         | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/customers?limit=55&filter=company_name=='*'                            |
         | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/property_sets?filter=property_id=='*'&sort='property_set_id'           |

    Scenario Outline: Unauthorized request - GET request is send to all endpoints without X-Auth-UserId header
      When GET request is sent to "<url>" on module "identity" without X-Auth-UserId header
      Then Response code is "403"
      And Custom code is "40301"
      Examples:
        | url                                                                               |
        | identity/properties                                                               |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/                         |
#        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/api_subscriptions        |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/commercial_subscriptions |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/users                    |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/customers                |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/property_sets            |
