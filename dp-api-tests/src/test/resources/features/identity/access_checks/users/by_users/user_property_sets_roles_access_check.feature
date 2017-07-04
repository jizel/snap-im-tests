@Identity
  Feature: User Property sets access check feature
    - Users must have access only to user-propertyset relations if they have access to both user and propertyset

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-000000000001 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456780 | http://www.snapshot.com | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | id                                   | type     | username   | firstName | lastName | email                | timezone      | languageCode | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaac | customer | user1OfC1  | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 32129079-48f0-4f00-9bec-e2329a8bdaac | customer | user2OfC1  | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000001" as primary "false"
      | id                                   | type     | username   | firstName | lastName | email                | timezone      | languageCode | isActive |
      | 12329079-48f0-4f00-9bec-e2329a8bdaab | customer | user1OfC2  | Customer  | User1C2  | usr1@snapshot.com    | Europe/Prague | cs-CZ   | true     |
    Given Switch for user property set role tests
    Given The following roles exist
      | id                                   | applicationId                        | roleName | description      |
      | 0d07159e-855a-4fc3-bcf2-a0cdbf54a44d | 03000000-0000-4444-8888-000000000000 | NewRole  | Some description |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "user1OfC1"
      | id                                   | name            | description            | type            |
      | 00002111-cdaf-439a-8bef-3140f56c657e | ps1_name        | ps1_description        | brand           |
    #    Must be here - DP-1846
    Given Relation between user "user1OfC1" and property with code "defaultPropertyCode" exists with is_active "true"

  Scenario: User can view only list of propertyset-user roles of his own customer and property set
    Given Relation between user "user2OfC1" and property set "ps1_name" exists with is_active "true"
    When User "user1OfC2" requests roles of user "user2OfC1" for property set "ps1_name"
    Then Response code is "404"
    When User "user1OfC1" requests roles of user "user2OfC1" for property set "ps1_name"
    Then Response code is "200"
    When Relation between property set "ps1_name" and user "user1OfC1" is inactivated
    When User "user1OfC1" requests roles of user "user2OfC1" for property set "ps1_name"
    Then Response code is "404"

  Scenario: User can assign and revoke roles to propertyset-users only when he has access to both user and propertyset
    When User "user2OfC1" assigns role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" to relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    When Relation between property set "ps1_name" and user "user1OfC1" is inactivated
    When User "user1OfC1" assigns role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" to relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    When Relation between property set "ps1_name" and user "user1OfC1" is activated
    When User "user1OfC1" assigns role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" to relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "201"
    When User "user1OfC1" assigns role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" to relation between user "user2OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    Given User "user1OfC2" is added to property set with name "ps1_name"
    When User "user1OfC1" assigns role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" to relation between user "user1OfC2" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    When User "user1OfC2" deletes role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" from relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    When Relation between property set "ps1_name" and user "user1OfC1" is inactivated
    When User "user1OfC1" deletes role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" from relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "404"
    When Relation between property set "ps1_name" and user "user1OfC1" is activated
    When User "user1OfC1" deletes role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" from relation between user "user1OfC1" and property set "00002111-cdaf-439a-8bef-3140f56c657e"
    Then Response code is "204"
