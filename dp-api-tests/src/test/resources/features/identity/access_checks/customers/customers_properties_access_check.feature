@Identity
Feature: Customers properties access check feature (second level endpoints)
  - Checking when certain user should and should not have access to certain customers
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)
  - All rules apply also to second level entities in both ways (e.g. customers/c_id/properties, properties/p_id/customers) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned
    Given Default Snapshot user is created
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 00000000-0000-4000-8000-123000000abc | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithCust1 | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "00000000-0000-4000-8000-123000000abc" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithCust2 | Customer  | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property is created with random address and billing address for user "12329079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |


  Scenario: Second level entities - User sees only properties he should for customer he sees
    Given Relation between property with code "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property with code "p1_code" from customer with id "12300000-0000-4000-a000-000000000000" is got by user "userWithCust1"
    Then Response code is "200"
    When List of all customer properties is got for customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1"
    Then Response code is "200"
    And Total count is "1"

  Scenario: Second level entities - User sees only properties he should for customer he sees - negative
    Given Relation between property with code "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property with code "p1_code" from customer with id "12300000-0000-4000-a000-000000000000" is got by user "userWithCust2"
    Then Response code is "404"
    When List of all customer properties is got for customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust2"
#  DP-1677
    Then Response code is "404"

     Scenario: Add user to property by user who can access the property
    When Property with code "p1_code" is added to customer with id "12300000-0000-4000-a000-000000000000" with type "chain" from "2015-01-01" to "2015-10-31" by user "userWithCust1"
    Then Response code is "201"
    And Body contains entity with attribute "property_id" value "999e833e-50e8-4854-a233-289f00b54a09"


  Scenario: Try to add property to customer by user who cannot access the property (but has access to the customer)
    Given The following property is created with random address and billing address for user "32129079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 888e833e-50e8-4854-a233-289f00b54a09 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 00000000-0000-4000-8000-123000000abc |
    When Property with code "p2_code" is added to customer with id "12300000-0000-4000-a000-000000000000" with type "chain" from "2015-01-01" to "2050-10-31" by user "userWithCust1"
    Then Response code is "422"
    And Custom code is 42202

    Scenario: Update customer property relationship by user who has access to the customer and the property
      Given Relation between property with code "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" exists with type "chain" from "2015-01-01" to "2050-12-31"
      When Property with code "p1_code" for customer with id "12300000-0000-4000-a000-000000000000" is updated by user "userWithCust1" with
      | type  | validFrom    |
      | owner | Jun 30, 2016 |
      Then Response code is "204"

  Scenario: Update customer property relationship by user without access the property
    Given The following property is created with random address and billing address for user "32129079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 888e833e-50e8-4854-a233-289f00b54a09 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 00000000-0000-4000-8000-123000000abc |
    Given Relation between property with code "p2_code" and customer with id "12300000-0000-4000-a000-000000000000" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property with code "p2_code" for customer with id "12300000-0000-4000-a000-000000000000" is updated by user "userWithCust1" with
      | type  | validFrom    |
      | owner | Jun 30, 2016 |
    Then Response code is "404"
    And Custom code is 40402

    Scenario: Update customer property relationship by user without access the customer
    Given The following property is created with random address and billing address for user "32129079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 888e833e-50e8-4854-a233-289f00b54a09 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 00000000-0000-4000-8000-123000000abc |
    Given Relation between property with code "p2_code" and customer with id "12300000-0000-4000-a000-000000000000" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property with code "p2_code" for customer with id "12300000-0000-4000-a000-000000000000" is updated by user "userWithCust2" with
      | type  | validFrom    |
      | owner | Jun 30, 2016 |
    Then Response code is "404"
    And Custom code is 40402

