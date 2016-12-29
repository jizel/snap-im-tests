@Identity
Feature: Properties access check feature - POST and DELETE
  - User can update/delete specific property and it's relationships only if he owns it
  - Check positive scenarios
  - In negative tests, ETAG needs to be obtained by different user first
  - Various cases of when user should have access to a property (instance) are covered in GET feature (user is a member of userGroup, property set etc.)
  - 404 is returned for unauthorized users
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (property_sets)

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
 #      Creating default user just to be able to get property by code. Access checks are always steps 'by user'
    Given Default Snapshot user is created for customer "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
      | 0d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithProp   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 1d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoProp | Customer2 | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property is created with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     | ttiId  |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 654123 |


  Scenario: User with access updates property
    When Property with code "p1_code" is updated with data by user "userWithProp"
      | salesforceId   | propertyName | propertyCode | website                  | email            | isDemoProperty |
      | updated_sf_id  | updated_name | updated_code | https://www.upddated.com | updated@email.cz | false          |
    Then Response code is "204"
    When Property with code "updated_code" is requested by user "userWithProp"
    Then Response code is "200"
    And Body contains entity with attribute "name" value "updated_name"
    And Body contains entity with attribute "salesforce_id" value "updated_sf_id"
    And Body contains entity with attribute "website" value "https://www.upddated.com"
    And Body contains entity with attribute "email" value "updated@email.cz"
    And Body contains entity with attribute "is_demo_property" value "false"

  Scenario: User without access tries to update property
    When Property with code "p1_code" is updated with data by user "userWithNoProp"
      | salesforceId   | propertyName | propertyCode | website                  | email            | isDemoProperty |
      | updated_sf_id  | updated_name | updated_code | https://www.upddated.com | updated@email.cz | false          |
    Then Response code is "404"
    When Property with code "p1_code" is requested by user "userWithProp"
    Then Response code is "200"

  Scenario: Deleting Property by user who owns it
    When Property with code "p1_code" is deleted by user "userWithProp"
    Then Response code is "204"
    And Body is empty
    And Property with same id doesn't exist

  Scenario: Deleting Property by user without access to it
    When Property with code "p1_code" is deleted by user "userWithNoProp"
    Then Response code is "404"
    When Property with code "p1_code" is requested by user "userWithProp"
    Then Response code is "200"

  Scenario: Anchor_customer_id of not accessible customer cannot be used when creating property
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    When The following property is created with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_2 | p2_name      | p2_code      | p2@tenants.biz | true           | Europe/Prague | 2348fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Anchor_customer_id of not accessible customer cannot be used when updating property
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    When Property with code "p1_code" is updated with data by user "userWithProp"
      | anchorCustomerId                     |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "404"
    And Custom code is 40402


  #    ----------------------------< Second level entities >----------------------------------

#    properties/p_id/users

  Scenario: Add user to property by user who can access the property
    When User "userWithNoProp" is added to property with code "p1_code" by user "userWithProp"
    Then Response code is "201"
    When Property with code "p1_code" is requested by user "userWithNoProp"
    Then Response code is "200"

  Scenario: Try to add user to property by user who cannot access the property
    When User "userWithNoProp" is added to property with code "p1_code" by user "userWithNoProp"
    Then Response code is "404"

#    Fails because DP-1548 is not finished yet
    Scenario: Try to add user from different customer to property
      Given The following customers exist with random address
        | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
        | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Customer 2  | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
      Given The following users exist for customer "2348fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
        | userId                               | userType | userName            | firstName | lastName | email                    | timezone      | culture | isActive |
        | 34529079-48f0-4f00-9bec-e2329a8bdaac | customer | userFromCustomer2   | Customer1 | User1    | usercus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      When User "userFromCustomer2" is added to property with code "p1_code" by user "userWithProp"
      Then Response code is "404"


#    Fails because of DP-1630 - remove this scenario completely if solution a) is picked in the issue
#    Scenario: Delete user from property by user who has access to it
#      Given Relation between user with username "userWithNoProp" and property with code "p1_code" exists
#      When User with username "userWithNoProp" is removed from property with code "p1_code" by user "userWithProp"
#      Then Response code is "204"
#      And Body is empty
#      And User with username "userWithNoProp" isn't there for property with code "p1_code"

#    Fails because of DP-1630 - remove this scenario completely if solution a) is picked in the issue
#  Scenario: Delete user from property by user who does not have access to it
#      When User with username "userWithNoProp" is removed from property with code "p1_code" by user "userWithNoProp"
#      Then Response code is "404"


#    properties/p_id/property_sets

#  Fails because of DP-1628 - wait until fixed
  Scenario: Update property - propertySet relationship by user has has access to them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When  Relation between property with code "p1_code" and property set "ps1_name" is updated by user "userWithProp" with
    | isActive |
    | true     |
    Then Response code is "201"

#  Fails because of DP-1628 - wait until fixed
  Scenario: Update property - propertySet relationship by user does not have access to the them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When  Relation between property with code "p1_code" and property set "ps1_name" is updated by user "userWithNoProp" with
    | isActive |
    | true     |
    Then Response code is "404"

  Scenario: Delete property - PropertySet relationship by user who has access to them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Relation between property with code "p1_code" and property set "ps1_name" is deleted by user "userWithProp"
    Then Response code is "204"

  Scenario: Delete property - PropertySet relationship by user who does not have access to them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Relation between property with code "p1_code" and property set "ps1_name" is deleted by user "userWithNoProp"
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Delete property - PropertySet relationship by user who has access to the property but not to the property set
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Relation between property with code "p1_code" and property set "ps1_name" is deleted by user "userWithProp"
    Then Response code is "404"
    And Custom code is 40402


#    properties/p_id/customers

  Scenario: Update Property-Customer relationship by user who has access to them
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property customer relationship for property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is updated by user "userWithProp" with
      | type  | validTo      |
      | owner | Jun 30, 2060 |
    Then Response code is "204"

  Scenario: Update Property-Customer relationship by user without access to the property
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property customer relationship for property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is updated by user "userWithNoProp" with
      | type  | validTo      |
      | owner | Jun 30, 2060 |
    Then Response code is "404"
    And Custom code is 40402

#    Bug?
  Scenario: Update Property-Customer relationship by user without access to the customer
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    Given Relation between property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property customer relationship for property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is updated by user "userWithProp" with
      | type  | validTo      |
      | owner | Jun 30, 2060 |
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Delete Property-Customer relationship by user who has access to them
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property customer relationship for property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is deleted by user "userWithProp"
    Then Response code is "204"

  Scenario: Delete Property-Customer relationship by user without access to the property
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property customer relationship for property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is deleted by user "userWithNoProp"
    Then Response code is "404"
    And Custom code is 40402

#    Bug?
  Scenario: Delete Property-Customer relationship by user without access to the customer
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    Given Relation between property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property customer relationship for property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is deleted by user "userWithProp"
    Then Response code is "404"
    And Custom code is 40402


#    properties/p_id/tti

  Scenario: Add tti to booking.com mapping to property with defined tti_id by use who has access to the property
    When Add ttiId to booking.com id "1234" mapping to property with code "p1_code" by user "userWithProp"
    Then Response code is "201"
    And Body contains entity with attribute "code" and integer value 1234

  Scenario: Add tti to booking.com mapping to property with defined tti_id by use who does not have access to the property
    When Add ttiId to booking.com id "1234" mapping to property with code "p1_code" by user "userWithNoProp"
    Then Response code is "404"