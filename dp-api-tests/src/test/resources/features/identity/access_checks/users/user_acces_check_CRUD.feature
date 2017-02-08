@Identity
Feature: Users can create, update and delete only users of the same customer

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
    Given The following users exist for customer "12300000-0000-4000-a000-000000000001" as primary "false"
      | userId                               | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaab | customer | user1OfC2  | Customer  | User1C2  | usr1@snapshot.com    | Europe/Prague | cs-CZ   | true     |

  # DP-1766
  @Bug
  Scenario: User can create users only within his customer
    When User "user1OfC1" creates user as primary "false" for customer with id "12300000-0000-4000-a000-000000000000"
      | userType | userName  | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user2OfC1 | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Then Response code is "201"
    When User "user1OfC1" creates user as primary "false" for customer with id "12300000-0000-4000-a000-000000000001"
      | userType | userName  | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user2OfC2 | Customer  | User2C2  | usr2@snapshot.com    | Europe/Prague | cs-CZ   | true     |
    Then Response code is "403"

  Scenario: User can delete users only within his customer
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userId                               | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | 22329079-48f0-4f00-9bec-e2329a8bdaac | customer | user2OfC1  | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    When User "user1OfC1" deletes user "22329079-48f0-4f00-9bec-e2329a8bdaac"
    Then Response code is "204"
    When User "user1OfC1" deletes user "12329079-48f0-4f00-9bec-e2329a8bdaab"
    Then Response code is "404"
    Given Relation between user "user1OfC1" and customer with id "12300000-0000-4000-a000-000000000001" exists with isPrimary "false"
    When User "user1OfC1" deletes user "12329079-48f0-4f00-9bec-e2329a8bdaab"
    Then Response code is "204"

  Scenario: User can update only users within his customer
    When User with userName "User1OfC2" is updated with data by user with id "12329079-48f0-4f00-9bec-e2329a8bdaac"
      | userType | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | Vasya     | Pupkin   | vpupkin@snapshot.com | Europe/Prague | cs-CZ   | false    |
    Then Response code is "404"
    Given Relation between user "user1OfC1" and customer with id "12300000-0000-4000-a000-000000000001" exists with isPrimary "false"
    When User with userName "User1OfC2" is updated with data by user with id "12329079-48f0-4f00-9bec-e2329a8bdaac"
      | userType | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | Vasya     | Pupkin   | vpupkin@snapshot.com | Europe/Prague | cs-CZ   | false    |
    Then Response code is "204"
