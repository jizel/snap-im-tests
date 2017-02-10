@Identity
  Feature: User Customers access check feature
    Users must have access only to user-customer relations of their customer

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-000000000001 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456780 | http://www.snapshot.com | Europe/Prague |
    Given Default Snapshot user is created
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userId                               | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | user1OfC1  | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | user2OfC1  | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000001" as primary "false"
      | userId                               | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaab | customer | user1OfC2  | Customer  | User1C2  | usr1@snapshot.com    | Europe/Prague | cs-CZ   | true     |

  Scenario: User can view customer-user relations of only his customer
    When Relation between user "user1OfC2" and customer "12300000-0000-4000-a000-000000000001" is requested by user "user1OfC1"
    Then Response code is "404"
    When Relation between customer "12300000-0000-4000-a000-000000000001" and user "user1OfC2" is requested by user "user1OfC1"
    Then Response code is "404"
    Given Relation between user "user2OfC1" and customer with id "12300000-0000-4000-a000-000000000001" exists with isPrimary "false"
    When User "user1OfC1" requests list of customer for user "user2OfC1"
    Then There are 1 userCustomers returned

  # DP-1781
  @Bug
  Scenario: User can view only list of customer-user roles of his own customer
    Given Relation between user "user2OfC1" and customer with id "12300000-0000-4000-a000-000000000001" exists with isPrimary "false"
    When User "user1OfC1" requests roles of user "user2OfC1" for customer "12300000-0000-4000-a000-000000000001"
    Then Response code is "404"
    When User "user1OfC1" requests roles of user "user2OfC1" for customer "12300000-0000-4000-a000-000000000000"
    Then Response code is "200"

