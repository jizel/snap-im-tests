@Identity
Feature: Users can create, update and delete only users of the same customer

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-000000000001 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456780 | http://www.snapshot.com | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000001"
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user1OfC1  | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000001" as primary "false"
      | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user1OfC2  | Customer  | User1C2  | usr1@snapshot.com    | Europe/Prague | cs-CZ   | true     |
#    Must be here - DP-1846
    Given Relation between user "user1OfC1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user1OfC2" and property with code "defaultPropertyCode" exists with is_active "true"

#  DP-1847
  @skipped
  Scenario: User can create users only within his customer
    When User "user1OfC1" creates user as primary "false" for customer with id "12300000-0000-4000-a000-000000000000"
      | userType | userName  | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user2OfC1 | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Then Response code is "201"
    When User "user1OfC1" creates user as primary "false" for customer with id "12300000-0000-4000-a000-000000000001"
      | userType | userName  | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user2OfC2 | Customer  | User2C2  | usr2@snapshot.com    | Europe/Prague | cs-CZ   | true     |
    Then Response code is "422"
    And Custom code is 42202
    Given Relation between user "user1OfC1" and customer with id "12300000-0000-4000-a000-000000000000" is inactivated
    When User "user1OfC1" creates user as primary "false" for customer with id "12300000-0000-4000-a000-000000000000"
      | userType | userName  | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user2OfC1 | Customer  | User3C1  | usr3@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Then Response code is "422"

#  DP-1848
  @skipped
  Scenario: User can delete users only within his customer
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userId                               | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | 22329079-48f0-4f00-9bec-e2329a8bdaac | customer | user2OfC1  | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    When User "user1OfC1" deletes user "user1OfC2"
    Then Response code is "404"
    Given Relation between user "user1OfC1" and customer with id "12300000-0000-4000-a000-000000000001" exists with isPrimary "false"
    When User "user1OfC1" deletes user "user1OfC2"
    Then Response code is "204"
    Given Relation between user "user1OfC1" and customer with id "12300000-0000-4000-a000-000000000000" is inactivated
    When User "user1OfC1" deletes user "user1OfC1"
    Then Response code is "404"

#    DP-1849 - test with uncommented "inactivate" step as well in case DP-1848 was fixed first
  @skipped
  Scenario: User can update only users within his customer
    When User "user1OfC2" is updated with data by user "user1OfC1"
      | userType | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | Vasya     | Pupkin   | vpupkin@snapshot.com | Europe/Prague | cs-CZ   | false    |
    Then Response code is "404"
    Given Relation between user "user1OfC1" and customer with id "12300000-0000-4000-a000-000000000001" exists with isPrimary "false"
    When User "User1OfC2" is updated with data by user "User1OfC1"
      | userType | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | Vasya     | Pupkin   | vpupkin@snapshot.com | Europe/Prague | cs-CZ   | false    |
    Then Response code is "204"
    Given Relation between user "user1OfC1" and customer with id "12300000-0000-4000-a000-000000000000" is inactivated
    When User "user1OfC1" is updated with data by user "user1OfC1"
      | userType | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | Vasya     | Pupkin   | vpupkin@snapshot.com | Europe/Prague | cs-CZ   | false    |
    Then Response code is "404"

  # DP-1759
  Scenario: User of type customer can not create snapshot user
    When User "User1OfC1" creates user with:
      | userType | userName | firstName | lastName | email                | timezone      | culture | isActive |
      | snapshot | vpupkin  | Vasya     | Pupkin   | vpupkin@snapshot.com | Europe/Prague | cs-CZ   | true     |
    Then Response code is "403"
    And Custom code is "40301"
