@Identity
  Feature: User Property sets access check feature
    - Users must have access only to user-propertyset relations if they have access to both user and propertyset

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
      | customer | user2OfC1  | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | user3OfC1  | Customer  | User3C1  | usr3@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000001" as primary "false"
      | userId                               | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaab | customer | user1OfC2  | Customer  | User1C2  | usr1@snapshot.com    | Europe/Prague | cs-CZ   | true     |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "user1OfC1"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
    #    Must be here - DP-1846
    Given Relation between user "user1OfC1" and property with code "defaultPropertyCode" exists with is_active "true"
    Given Relation between user "user3OfC1" and default property exists

  Scenario: User can view only list of propertyset-users of his own customer and property set
    When User "user1OfC2" requests list of users for property set "ps1_name"
    Then Response code is "404"
    When User "user1OfC1" requests list of users for property set "ps1_name"
    Then Response code is "200"
    When Relation between property set "ps1_name" and user "user1OfC1" is inactivated
    When User "user1OfC1" requests list of users for property set "ps1_name"
    Then Response code is "404"

  Scenario: User can add users to property sets only when he has access to both users and propertyset
    # Add wrong user
    When User "user1OfC2" is added to property set with name "ps1_name" by user "user1OfC1"
    Then Response code is "422"
    # Add to wrong property set
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "user2OfC1"
      | name            | description            | type            |
      | ps2_name        | ps2_description        | brand           |
    When User "user3OfC1" is added to property set with name "ps2_name" by user "user1OfC1"
    Then Response code is "404"
    # Add correct user to the property set with which you have inactive relation
    Given User "user1OfC1" is added to property set "ps2_name" with is_active "false"
    When User "user3OfC1" is added to property set with name "ps2_name" by user "user1OfC1"
    Then Response code is "404"
    # Positive scenario
    Given Relation between user "user1OfC1" and property set "ps2_name" is activated
    When User "user3OfC1" is added to property set with name "ps2_name" by user "user1OfC1"
    Then Response code is "201"
