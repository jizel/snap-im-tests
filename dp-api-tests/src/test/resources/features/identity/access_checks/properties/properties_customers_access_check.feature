@Identity
Feature: Properties-Customers access check feature
  - Checking when certain user should and should not have access to certain properties
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


  Scenario: Second level entities - User sees only customers of the same property he owns
    Given The following customers exist with random address
    | customerId                           | companyName     | email              | salesforceId | vatId      | isDemoCustomer | timezone      |
    | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Given company 2 | c2@snapshot.travel | sfid_2       | CZ20000001 | true           | Europe/Prague |
    | 4568fd9a-a05d-42d8-8e84-42e904ace123 | Given company 3 | c3@snapshot.travel | sfid_3       | CZ30000001 | true           | Europe/Prague |
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    Given Relation between property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When List of all customers for property with code "p1_code" is got by user "userWithProp"
    Then Response code is "200"
    And Total count is "1"
    When List of all customers for property with code "p1_code" is got by user "userWithNoProp"
#  DP-1677
    Then Response code is "404"


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

  Scenario: Update Property-Customer relationship by user without access to the customer
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    Given Relation between property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property customer relationship for property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is updated by user "userWithProp" with
      | type  |
      | owner |
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

  Scenario: Delete Property-Customer relationship by user without access to the customer
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    Given Relation between property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property customer relationship for property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is deleted by user "userWithProp"
    Then Response code is "404"
    And Custom code is 40402
