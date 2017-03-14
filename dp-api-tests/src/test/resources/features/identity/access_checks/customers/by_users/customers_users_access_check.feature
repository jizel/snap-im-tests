@Identity
Feature: Customers users access check feature (second level endpoints)
  - Checking when certain user should and should not have access to certain customers
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)
  - All rules apply also to second level entities in both ways (e.g. customers/c_id/properties, properties/p_id/customers) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 00000000-0000-4000-8000-123000000abc | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithCust1 | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "00000000-0000-4000-8000-123000000abc" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithCust2 | Customer  | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
    Given API subscriptions exist for default application and customer with id "00000000-0000-4000-8000-123000000abc"
    Given Relation between user "userWithCust1" and default property exists
    Given Relation between user "userWithCust2" and default property exists

#      DP-1840
  @skipped
  Scenario: Second level entities - User sees only users of the same customer
    When List of all users for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1"
    Then Response code is "200"
    And Total count is "1"
    When List of all users for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust2"
    Then Response code is "404"
    Given Relation between user "userWithCust2" and customer with id "12300000-0000-4000-a000-000000000000" exists with isPrimary "false"
    When List of all users for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust2"
    Then Response code is "200"
    And Total count is "2"
    When Relation between user "userWithCust2" and customer with id "12300000-0000-4000-a000-000000000000" is deactivated
    When List of all users for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust2"
    Then Response code is "404"


    Scenario: Add user to customer by user without access to the customer
      When User "userWithCust2" is added to customer with id "12300000-0000-4000-a000-000000000000" with isPrimary "true" by user "userWithCust2"
      Then Response code is "404"
      And Custom code is 40402
      When Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is deactivated
      When User "userWithCust2" is added to customer with id "12300000-0000-4000-a000-000000000000" with isPrimary "true" by user "userWithCust1"
      Then Response code is "404"
      And Custom code is 40402

    Scenario: Updating User Customer relationship by user who can access it
      When Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is updated with isPrimary "false" by user "userWithCust1"
      Then Response code is "204"
      And Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is not primary

    Scenario: Updating User Customer relationship by user who cannot access the customer
      When Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is updated with isPrimary "false" by user "userWithCust2"
      Then Response code is "404"
      And Custom code is 40402
      When Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is deactivated
      When Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is updated with isPrimary "true" by user "userWithCust1"
      Then Response code is "404"

    Scenario: Deleting User Customer relationship by user who can access it
      When User "userWithCust1" is removed from customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1"
      Then Response code is "204"
      And Body is empty
      And User "userWithCust1" isn't there for customer with id "12300000-0000-4000-a000-000000000000"

    Scenario: Deleting User Customer relationship by user who cannot access the customer
      When User "userWithCust1" is removed from customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust2"
      Then Response code is "404"
      And Custom code is 40402
      When Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is deactivated
      When User "userWithCust1" is removed from customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1"
      Then Response code is "404"