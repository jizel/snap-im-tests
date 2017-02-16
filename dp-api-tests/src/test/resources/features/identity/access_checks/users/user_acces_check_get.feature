@Identity
Feature: User access check feature - GET
  - Checking when certain user should and should not have access to other users
  - User should have access only to users which belong to the same customer

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
      | 32129079-48f0-4f00-9bec-e2329a8bdaab | customer | user2OfC2  | Customer  | User2C2  | usr2@snapshot.com    | Europe/Prague | cs-CZ   | true     |

  # DP-1765
  @Bug
  Scenario: User has access only to users of the same customer
    When List of users is got with limit "5" and cursor "0" and filter "/null" and sort "/null" and sort_desc "/null" by user "12329079-48f0-4f00-9bec-e2329a8bdaac"
    Then Response code is "200"
    And There are "2" users returned
    Given Relation between user "user1OfC1" and customer with id "12300000-0000-4000-a000-000000000001" exists with isPrimary "false"
    When List of users is got with limit "5" and cursor "0" and filter "/null" and sort "/null" and sort_desc "/null" by user "12329079-48f0-4f00-9bec-e2329a8bdaac"
    Then Response code is "200"
    And There are "4" users returned
    Given Relation between user "user1OfC1" and customer "12300000-0000-4000-a000-000000000001" is deleted
    When List of users is got with limit "5" and cursor "0" and filter "/null" and sort "/null" and sort_desc "/null" by user "12329079-48f0-4f00-9bec-e2329a8bdaac"
    Then Response code is "200"
    And There are "2" users returned

  Scenario: User has no access to users of another customer even when they share access to the same property
    Given The following properties exist with random address and billing address for user "12329079-48f0-4f00-9bec-e2329a8bdaac"
      |propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      |999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    And Relation between user "user1OfC2" and property with code "p1_code" exists
    When List of users is got with limit "5" and cursor "0" and filter "/null" and sort "/null" and sort_desc "/null" by user "12329079-48f0-4f00-9bec-e2329a8bdaac"
    Then Response code is "200"
    And There are "2" users returned

  Scenario: User has no access to users of another customer even when they share access to the same property set
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "user1OfC1"
      | propertySetName | propertySetType | propertySetId                        |
      | ps1_name        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 |
    And Relation between user "user1OfC2" and property set with name "ps1_name" exists
    When List of users is got with limit "5" and cursor "0" and filter "/null" and sort "/null" and sort_desc "/null" by user "12329079-48f0-4f00-9bec-e2329a8bdaac"
    Then Response code is "200"
    And There are "2" users returned
