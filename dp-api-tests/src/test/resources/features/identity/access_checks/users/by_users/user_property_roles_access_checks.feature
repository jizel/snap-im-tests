@Identity
  Feature: User Properties access check feature
    Users must have access only to user-property relations if they have access to both user and property

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-000000000001 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420123456780 | http://www.snapshot.com | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000"
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000001"
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user1OfC1  | Customer  | User1C1  | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | user2OfC1  | Customer  | User2C1  | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000001" as primary "false"
      | userType | userName   | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user1OfC2  | Customer  | User1C2  | usr1@snapshot.com    | Europe/Prague | cs-CZ   | true     |
    Given Switch for user property role tests
    Given The following roles exist
      | id                                   | applicationId                        | roleName | description      |
      | 0d07159e-855a-4fc3-bcf2-a0cdbf54a44d | 11111111-0000-4000-a000-111111111111 | NewRole  | Some description |
    Given The following properties exist with random address and billing address
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000000" and property "p1_code"
    Given API subscriptions exist for default application and customer with id "12300000-0000-4000-a000-000000000001" and property "p1_code"
     #    Must be here - DP-1846
    Given Relation between user "user1OfC1" and property with code "p1_code" exists with is_active "true"
    Given Relation between user "user2OfC1" and property with code "p1_code" exists with is_active "true"

  Scenario: User can view only list of property-user roles of his own customer and property he can access
    When User "user1OfC2" requests roles of user "user2OfC1" for property "p1_code"
    Then Response code is "404"
    When User "user1OfC1" requests roles of user "user2OfC1" for property "p1_code"
    Then Response code is "200"
    Given Relation between user "user1OfC1" and property with code "p1_code" is inactivated
    When User "user1OfC1" requests roles of user "user2OfC1" for property "p1_code"
    Then Response code is "404"

  Scenario: User can assign and revoke roles to property-users only when he has access to both user and property
    When User "user1OfC2" assigns role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" to relation between user "user1OfC1" and property "p1_code"
    Then Response code is "404"
    When User "user1OfC1" assigns role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" to relation between user "user1OfC1" and property "p1_code"
    Then Response code is "201"
    When User "user2OfC1" assigns role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" to relation between user "user1OfC2" and property "p1_code"
    Then Response code is "404"
    Given Relation between user "user1OfC2" and property with code "p1_code" exists
    When User "user1OfC1" assigns role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" to relation between user "user1OfC2" and property "p1_code"
    Then Response code is "404"
    When User "user1OfC2" deletes role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" from relation between user "user1OfC1" and property "p1_code"
    Then Response code is "404"
    When User "user1OfC1" deletes role "0d07159e-855a-4fc3-bcf2-a0cdbf54a44d" from relation between user "user1OfC1" and property "p1_code"
    Then Response code is "204"