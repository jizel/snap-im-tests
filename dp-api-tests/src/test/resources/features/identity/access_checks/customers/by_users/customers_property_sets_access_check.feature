@Identity
Feature: Customers property sets access check feature (second level endpoints)
  - Checking when certain user should and should not have access to certain customer property sets
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)
  - All rules apply also to second level entities in both ways (e.g. customers/c_id/properties, properties/p_id/customers) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned and default entities are created
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
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithCust1"
      | propertySetId                        | name            | type            |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 | prop_set1       | brand           |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd1 | prop_set2       | brand           |
    Given The following property sets exist for customer with id "00000000-0000-4000-8000-123000000abc" and user "userWithCust2"
      | propertySetId                        | name            | type            |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd2 | prop_set3       | brand           |
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
    Given API subscriptions exist for default application and customer with id "00000000-0000-4000-8000-123000000abc"


  Scenario: Second level entities - User sees only property sets he should for customer he sees
    When List of all property sets for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1"
    Then Response code is "200"
    And Total count is "2"
    When List of all property sets for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust2"
    Then Response code is "404"
    When List of all property sets for customer with id "00000000-0000-4000-8000-123000000abc" is requested by user "userWithCust2"
    Then Response code is "200"
    And Total count is "1"
    When Relation between user "userWithCust1" and property set "prop_set1" is deactivated
    When List of all property sets for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1"
    Then Response code is "200"
    And Total count is "1"
    When Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is deactivated
    When List of all property sets for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1"
    Then Response code is "404"

