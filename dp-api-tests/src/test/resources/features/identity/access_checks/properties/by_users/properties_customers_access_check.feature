@Identity
Feature: Properties-Customers access check feature
  - Checking when certain user should and should not have access to certain properties
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)`
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (property_sets)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithProp   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | userWithNoProp | Customer2 | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property is created with random address and billing address for user "userWithProp"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"
    Given API subscriptions exist for default application and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"

  @skipped
  Scenario: Second level entities - User sees only customers of the same property he owns
    Given The following customers exist with random address
    | Id                                   | companyName     | email              | salesforceId | vatId      | isDemoCustomer | timezone      |
    | 4568fd9a-a05d-42d8-8e84-42e904ace123 | Given company 3 | c3@snapshot.travel | sfid_3       | CZ30000001 | true           | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "4568fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"
    Given Relation between property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with is_active "false"
    Given Relation between property with code "p1_code" and customer with id "4568fd9a-a05d-42d8-8e84-42e904ace123" exists with is_active "false"
    When List of all customers for property with code "p1_code" is got by user "userWithProp"
    Then Response code is "200"
    And Total count is "0"
    When Relation between property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is activated
    And List of all customers for property with code "p1_code" is got by user "userWithProp"
    Then Response code is "200"
    And Total count is "0"
    When List of all customers for property with code "p1_code" is got by user "userWithNoProp"
    Then Response code is "404"


  Scenario: Update Property-Customer relationship by user who has access to them
    Given Relation between property "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with is_active "false"
    When Property customer relationship for property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is updated by user "userWithProp" with
      | type  |
      | owner |
    Then Response code is "404"
    Given Relation between property "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Property customer relationship for property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is updated by user "userWithProp" with
      | type  |
      | owner |
    Then Response code is "204"

  Scenario: Update Property-Customer relationship by user without access to the customer
    Given Relation between property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Property customer relationship for property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is updated by user "userWithProp" with
      | type  |
      | owner |
    Then Response code is "404"
    And Custom code is 40402
    Given Relation between user "userWithProp" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with is_active "false"
    And Property customer relationship for property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is updated by user "userWithProp" with
      | type  |
      | owner |
    Then Response code is "404"
    And Custom code is 40402
    Given Relation between user "userWithProp" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is activated
    And Property customer relationship for property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is updated by user "userWithProp" with
      | type  |
      | owner |
    Then Response code is "204"

  Scenario: Delete Property-Customer relationship by user who has access to them
    Given Relation between property "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with is_active "false"
    When Relation between property "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithProp"
    Then Response code is "200"
    When Property customer relationship for property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is deleted by user "userWithProp"
    Then Response code is "204"
    Given Relation between user "userWithProp" and property with code "p1_code" is activated
    Given Relation between property "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Relation between property "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is requested by user "userWithProp"
    Then Response code is "200"
    When Property customer relationship for property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is deleted by user "userWithProp"
    Then Response code is "204"

  Scenario: Delete Property-Customer relationship by user without access to the property or customer
    Given Relation between property "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Property customer relationship for property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is deleted by user "userWithNoProp"
    Then Response code is "404"
    And Custom code is 40402
    Given Relation between property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property customer relationship for property with code "p1_code" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is deleted by user "userWithProp"
    Then Response code is "404"
    And Custom code is 40402