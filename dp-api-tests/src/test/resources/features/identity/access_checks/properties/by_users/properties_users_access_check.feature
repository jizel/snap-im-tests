@Identity
Feature: Properties-Users access check feature
  - Checking when certain user should and should not have access to certain properties
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)`
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (property_sets)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithProp   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | userWithNoProp | Customer2 | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following properties exist with random address and billing address for user "userWithProp"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"

  # DP-1840
  @skipped
  Scenario: Second level entities - User sees only users of the same property he owns
    When List of all users for property with code "p1_code" is got by user "userWithProp"
    Then Response code is "200"
    And Total count is "1"
    Given Relation between user "userWithNoProp" and property with code "p1_code" exists with is_active "false"
    When List of all users for property with code "p1_code" is got by user "userWithProp"
    Then Response code is "200"
    And Total count is "1"
    When List of all users for property with code "p1_code" is got by user "userWithNoProp"
    Then Response code is "404"
    Given Relation between user "userWithNoProp" and property with code "p1_code" is activated
    When List of all users for property with code "p1_code" is got by user "userWithProp"
    Then Response code is "200"
    And Total count is "2"
    When List of all users for property with code "p1_code" is got by user "userWithNoProp"
    Then Response code is "200"
    And Total count is "2"

  Scenario: Add/Remove user to property by user who can access the property
    When User "userWithNoProp" is added to property with code "p1_code" by user "userWithProp"
    # User will not be found, because in order to see the user you must share with him not only customer, but also a property/propertyset
    # The error would be 422 - Reference does not exist
    Then Response code is "422"
    When User "userWithNoProp" is added to property with code "p1_code" with is_active "false"
    Then Response code is "201"
    When Relation between property with code "p1_code" and user "userWithNoProp" is deleted by user "userWithProp"
    Then Response code is "404"
    Given Relation between user "userWithNoProp" and property "p1_code" is activated
    When Relation between property with code "p1_code" and user "userWithNoProp" is deleted by user "userWithProp"
    Then Response code is "204"
    And User "userWithNoProp" isn't there for property with code "p1_code"

  Scenario: Try to add user to property by user who cannot access the property
    When User "userWithNoProp" is added to property with code "p1_code" by user "userWithNoProp"
    Then Response code is "404"

  Scenario: Try to add user from different customer to property
    Given The following customers exist with random address
      | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Customer 2  | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    Given The following users exist for customer "2348fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName            | firstName | lastName | email                    | timezone      | culture | isActive |
      | customer | userFromCustomer2   | Customer1 | User1    | usercus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    When User "userFromCustomer2" is added to property with code "p1_code" by user "userWithProp"
    Then Response code is "422"
    And Custom code is 42202

  Scenario: Delete user from property by user who does not have access to it
    When User "userWithProp" is removed from property with code "p1_code" by user "userWithNoProp"
    Then Response code is "404"