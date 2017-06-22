@Identity
  Feature: User Properties access check feature
    Users must have access only to user-property relations if they have access to both user and property

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-000000000001 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456780 | http://www.snapshot.com | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | type     | username   | firstName | lastName | email                | timezone      | languageCode | isActive |
      | customer | user1OfC1  | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | user2OfC1  | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000001" as primary "false"
      | type     | username   | firstName | lastName | email                | timezone      | languageCode | isActive |
      | customer | user1OfC2  | Customer  | User1C2  | usr1@snapshot.com    | Europe/Prague | cs-CZ   | true     |
      | customer | user2OfC2  | Customer  | User2C2  | usr2@snapshot.com    | Europe/Prague | cs-CZ   | true     |
    Given Switch for user property role tests
    Given The following properties exist with random address and billing address
      | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given Relation between user "user1OfC1" and property with code "p1_code" exists with is_active "true"

  Scenario: User can view only list of property-users of his own customer and property he can access
    Given The following properties exist with random address and billing address
      | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.com    | p2@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    # Wrong customer
    When User "user1OfC2" requests list of users for property "p1_code"
    Then Response code is "404"
    # Wrong property
    When User "user1OfC1" requests list of users for property "p2_code"
    Then Response code is "404"
    # Own customer and own property
    When User "user1OfC1" requests list of users for property "p1_code"
    Then Response code is "200"
    Given Relation between user "user1OfC1" and property with code "p1_code" is inactivated
    When User "user1OfC1" requests list of users for property "p1_code"
    Then Response code is "404"


  Scenario: User can add users to property only if he has access both to users and to property
    # Add wrong users to property
    When User "user1OfC1" adds user "user1OfC2" to property "p1_code"
    Then Response code is "422"
    # Add user to wrong property
    Given The following properties exist with random address and billing address
      | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.com    | p2@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    When User "user1OfC1" adds user "user2OfC1" to property "p2_code"
    Then Response code is "404"
    Given Relation between user "user1OfC1" and property with code "p2_code" exists with is_active "true"
    When User "user1OfC1" adds user "user2OfC1" to property "p2_code"
    Then Response code is "201"
