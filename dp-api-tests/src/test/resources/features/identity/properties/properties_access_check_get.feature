@Identity
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
  Given Database is cleaned
  Given The following customers exist with random address
    | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
    | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
  #      Creating default user just to be able to get property by code. Access checks are always steps 'by user'
  Given Default Snapshot user is created
  Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
    | userId                               | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
    | 0d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithProp   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    | 1d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoProp | Customer2 | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
  Given The following property is created with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
    | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
    | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |


    Scenario: User has direct relationship to property
      When Property with code "p1_code" is requested by user "userWithProp"
      Then Response code is "200"
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      And Custom code is 40402

    Scenario: User belongs to User Group that has access to property
      Given The following user groups exist
        | userGroupId                          | customerId                           | name        | isActive |
        | a8b40d08-de38-4246-bb69-ad39c31c025c | 1238fd9a-a05d-42d8-8e84-42e904ace123 | userGroup_1 | false    |
      When Relation between user group "userGroup_1" and property "999e833e-50e8-4854-a233-289f00b54a09" exists with isActive "true"
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When User "userWithNoProp" is added to userGroup "userGroup_1"
      And Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "200"

    Scenario: User has a relationship to any PropertySet that has a relationship to this instance
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp"
        | propertySetName | propertySetType |
        | ps1_name        | brand           |
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When Property with code "p1_code" is added to property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
      And Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "200"

    Scenario: User has a relationship to any PropertySet that has a successor that has a successor which has relationship to this instance
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp"
        | propertySetName | propertySetType | propertySetId                        |
        | parentPS        | brand           | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
        | propertySetName | propertySetType | parentId                             | propertySetId                        |
        | childPS1        | brand           | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
        | propertySetName | propertySetType | parentId                             |
        | childPS2        | brand           | d119e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When Property with code "p1_code" is added to property set with name "childPS2" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "200"

    Scenario: User belongs to User Group that has a relation to a PropertySet that has a relation to the property
      Given The following user groups exist
        | userGroupId                          | customerId                           | name        | isActive |
        | a8b40d08-de38-4246-bb69-ad39c31c025c | 1238fd9a-a05d-42d8-8e84-42e904ace123 | userGroup_1 | false    |
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
        | propertySetId                        | propertySetName | propertySetType |
        | fb141231-4d8c-4d75-9433-5d01cc665556 | ps1_name        | brand           |
      When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and property set "fb141231-4d8c-4d75-9433-5d01cc665556" exists with isActive "true"
      And Property with code "p1_code" is added to property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
      When Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When User "userWithNoProp" is added to userGroup "userGroup_1"
      And Property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "200"


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
       Given The following properties exist with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
         | salesforceId   | propertyName | propertyCode | email              | website                    | isDemoProperty | timezone      | anchorCustomerId                     | ttiId |
         | salesforceid_2 | p2_name      | p2_code      | p2@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 1234  |
         | salesforceid_3 | p3_name      | p3_code      | p3@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 4231  |
         | salesforceid_4 | p4_name      | p4_code      | p4@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 5678  |
         | salesforceid_5 | p5_name      | p5_code      | p5@snapshot.travel | http://www.snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 8765  |
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
         | /null | 0      | is_active=='false'                                         | /null          | salesforce_id       | 5           |
         | /null | 0      | salesforce_id=='salesforceid_2'                            | /null          | /null               | 1           |
         | /null | 0      | anchor_customer_id=='*23'                                  | /null          | anchor_customer_id  | 5           |
         | /null | 0      | anchor_customer_id=='2228fd9a-a05d-42d8-8e84-42e904ace222' | /null          | /null               | 0           |
         | /null | 0      | tti_id=='*23*'                                             | /null          | /null               | 2           |
         | /null | 0      | is_demo_property=='true'                                   | email          | /null               | 5           |
         | /null | 0      | email=='*@snapshot.travel'                                 | property_id   | /null                | 4           |
#       Fails because of DP-1639, uncomment when fixed
#         | /null | 0      | property_id=='999e833e-*'                                 | salesforce_id  | /null               | 1           |


#      -----------------------------< Second level entities accessibility check >------------------------------------

#    properties/p_id/property_sets

    Scenario: Second level entities - User sees only property sets he should for property he owns
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
        | propertySetName | propertySetType |
        | prop_set1       | brand           |
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp"
        | propertySetName | propertySetType |
        | prop_set2       | brand           |
      When Property with code "p1_code" is added to property set with name "prop_set1" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
      When Property set with name "prop_set1" for property with code "p1_code" is requested by user "userWithProp"
      Then Response code is "200"
      When Property set with name "prop_set2" for property with code "p1_code" is requested by user "userWithProp"
      Then Response code is "404"
      When List of all property sets is got for property with code "p1_code" by user "userWithProp"
      Then Response code is "200"
      And Total count is "1"


    Scenario: Second level entities - User doesn't see property sets for property when he doesn't have access to the property and the property set all at once
      Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp"
        | propertySetName | propertySetType | propertySetId                        |
        | prop_set1       | brand           | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 |
      When Property set with name "prop_set1" for property with code "p1_code" is requested by user "userWithNoProp"
      Then Response code is "404"
      When Property set with name "prop_set1" for property with code "p1_code" is requested by user "userWithProp"
      Then Response code is "404"
      When List of all property sets is got for property with code "p1_code" by user "userWithProp"
      Then Response code is "200"
      And Total count is "0"


#    properties/p_id/customers

    Scenario: Second level entities - User sees only customers of the same property he owns
      Given The following customers exist with random address
        | customerId                           | companyName     | email              | salesforceId | vatId      | isDemoCustomer | timezone      |
        | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Given company 2 | c2@snapshot.travel | sfid_2       | CZ20000001 | true           | Europe/Prague |
        | 4568fd9a-a05d-42d8-8e84-42e904ace123 | Given company 3 | c3@snapshot.travel | sfid_3       | CZ30000001 | true           | Europe/Prague |
      Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
      Given Relation between property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
      When List of all customers for property with code "p1_code" is got by user "userWithProp"
      Then Response code is "200"
#      Bug?
      And Total count is "1"
      When List of all customers for property with code "p1_code" is got by user "userWithNoProp"
      Then Response code is "404"


#    properties/p_id/users

    Scenario: Second level entities - User sees only users of the same property he owns
      When List of all users for property with code "p1_code" is got by user "userWithProp"
      Then Response code is "200"
      And Total count is "1"
      Given Relation between user with username "userWithNoProp" and property with code "p1_code" exists
      When List of all users for property with code "p1_code" is got by user "userWithProp"
      Then Response code is "200"
      And Total count is "2"

#    General negative scenarios

    Scenario Outline: User with no access rights to property sends GET request to all general second level endpoints
      When GET request is sent to "<url>" on module "identity" by user "userWithNoProp"
      Then Response code is "404"
      And Custom code is "40402"
      Examples:
        | url                                                                               |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/api_subscriptions        |
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
         | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/api_subscriptions?sort=application_version_id&filter=is_active=='true' |
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
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/api_subscriptions        |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/commercial_subscriptions |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/users                    |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/customers                |
        | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/property_sets            |