@Identity
  Feature: User Customers access check feature
    Users must have access only to user-customer relations of their customer

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-000000000001 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456780 | http://www.snapshot.com | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000001"
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | Id                                   | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | user1OfC1  | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | user2OfC1  | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000001" as primary "false"
      | Id                                   | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaab | customer | user1OfC2  | Customer  | User1C2  | usr1@snapshot.com    | Europe/Prague | cs-CZ   | true     |
     #    Must be here - DP-1846
    Given Relation between user "user1OfC1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user2OfC1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user1OfC2" and property with code "defaultPropertyCode" exists with is_active "true"

  # DP-1884
  @skipped
  Scenario: User can view customer-user relations of only his customer
    When Relation between user "user2OfC1" and customer "12300000-0000-4000-a000-000000000000" is requested by user "user1OfC1"
    Then Response code is "200"
    When Relation between user "user1OfC2" and customer "12300000-0000-4000-a000-000000000001" is requested by user "user1OfC1"
    Then Response code is "404"
    When Relation between customer "12300000-0000-4000-a000-000000000001" and user "user1OfC2" is requested by user "user1OfC1"
    Then Response code is "404"
    Given Relation between user "user2OfC1" and customer with id "12300000-0000-4000-a000-000000000001" exists with isPrimary "false"
    When User "user1OfC1" requests list of customer for user "user2OfC1"
    Then There are 1 userCustomers returned
    Given Relation between user "user1OfC1" and customer with id "12300000-0000-4000-a000-000000000000" is deactivated
    When User "user1OfC1" requests list of customer for user "user2OfC1"
    Then Response code is "404"
    Given Relation between user "user1OfC1" and customer with id "12300000-0000-4000-a000-000000000000" is activated
    Given Relation between user "user2OfC1" and customer with id "12300000-0000-4000-a000-000000000000" is deactivated
    When User "user1OfC1" requests list of customer for user "user2OfC1"
    Then Response code is "404"

  # DP-1781
  @skipped
  Scenario: User can add/remove users to customer only when he has access to both users and customer
    # Add wrong user to customer
    When User "user1OfC1" adds user "user1OfC2" to customer "12300000-0000-4000-a000-000000000000"
    Then Response code is "422"
    # Add correct user to wrong customer
    When User "user1OfC1" adds user "user2OfC1" to customer "12300000-0000-4000-a000-000000000001"
    Then Response code is "404"
    # Add/remove correct user to customer with which relation is inactive
    Given Relation between user "User1OfC1" and customer with id "12300000-0000-4000-a000-000000000001" exists with isPrimary "false" with is_active "false"
    When User "user1OfC1" adds user "user2OfC1" to customer "12300000-0000-4000-a000-000000000001"
    Then Response code is "404"
    When User "user1OfC1" removes user "user1OfC2" from customer "12300000-0000-4000-a000-000000000001"
    Then Response code is "404"
    # Positive scenario
    Given Relation between user "User1OfC1" and customer with id "12300000-0000-4000-a000-000000000001" is activated
    When User "user1OfC1" adds user "user2OfC1" to customer "12300000-0000-4000-a000-000000000001"
    Then Response code is "201"
    When User "user1OfC1" removes user "user1OfC2" from customer "12300000-0000-4000-a000-000000000001"
    Then Response code is "204"




