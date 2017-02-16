@Identity
Feature: Properties-Users access check feature
  - Checking when certain user should and should not have access to certain properties
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)`
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (property_sets)

  Background:
    Given Database is cleaned
    Given Default Snapshot user is created
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
      | 0d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithProp   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 1d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoProp | Customer2 | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property is created with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

  Scenario: Second level entities - User sees only users of the same property he owns
    When List of all users for property with code "p1_code" is got by user "userWithProp"
    Then Response code is "200"
    And Total count is "1"
    Given Relation between user "userWithNoProp" and property with code "p1_code" exists
    When List of all users for property with code "p1_code" is got by user "userWithProp"
    Then Response code is "200"
    And Total count is "2"

  Scenario: Add user to property by user who can access the property
    When User "userWithNoProp" is added to property with code "p1_code" by user "userWithProp"
    Then Response code is "201"
    When Property with code "p1_code" is requested by user "userWithNoProp"
    Then Response code is "200"

  Scenario: Try to add user to property by user who cannot access the property
    When User "userWithNoProp" is added to property with code "p1_code" by user "userWithNoProp"
    Then Response code is "404"

  Scenario: Try to add user from different customer to property
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Customer 2  | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    Given The following users exist for customer "2348fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName            | firstName | lastName | email                    | timezone      | culture | isActive |
      | 34529079-48f0-4f00-9bec-e2329a8bdaac | customer | userFromCustomer2   | Customer1 | User1    | usercus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    When User "userFromCustomer2" is added to property with code "p1_code" by user "userWithProp"
    Then Response code is "422"
    And Custom code is 42202

    Scenario: Delete user from property by user who has access to it
      Given Relation between user "userWithNoProp" and property with code "p1_code" exists
      When User "userWithNoProp" is removed from property with code "p1_code" by user "userWithProp"
      Then Response code is "204"
      And Body is empty
      And User "userWithNoProp" isn't there for property with code "p1_code"

    Scenario: Delete user from property by user who does not have access to it
      When User "userWithProp" is removed from property with code "p1_code" by user "userWithNoProp"
      Then Response code is "404"