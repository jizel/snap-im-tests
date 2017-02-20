@Identity
Feature: Property sets Users access check feature
  - Checking when certain user should and should not have access to certain property sets
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)`
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned
    Given Default Snapshot user is created
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName          | firstName | lastName | email                | timezone      | culture | isActive |
      | 0d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithPropSet   | Customer1 | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 1d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoPropSet | Customer2 | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithPropSet"
      | propertySetName | propertySetType | propertySetId                        |
      | ps1_name        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 |


    Scenario: Second level entities - User sees only users of the same property set he owns
      When List of all users for property set "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "200"
      And Total count is "1"
      Given Relation between user "userWithNoPropSet" and property set with name "ps1_name" exists
      When List of all users for property set "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "200"
      And Total count is "2"
      When User "userWithNoPropSet" for property set "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "200"
      And Body contains entity with attribute "user_id" value "1d829079-48f0-4f00-9bec-e2329a8bdaac"

    Scenario: Second level entities - User should not see users when he doesn't have access to the property set
      When List of all users for property set "ps1_name" is requested by user "userWithNoPropSet"
      #      Fails until DP-1677 fixed
      Then Response code is "404"
      When User "userWithPropSet" for property set "ps1_name" is requested by user "userWithNoPropSet"
      Then Response code is "404"
      And Custom code is 40402


  Scenario: Add user to property set by user who can access the property set
    When User "userWithNoPropSet" is added to property set with name "ps1_name" by user "userWithPropSet"
    Then Response code is "201"

  Scenario: Try to add user to property by user who cannot access the property
    When User "userWithNoPropSet" is added to property set with name "ps1_name" by user "userWithNoPropSet"
    Then Response code is "404"
    
  Scenario: Try to add user from different customer to property set
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Customer 2  | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    Given The following users exist for customer "2348fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName            | firstName | lastName | email                    | timezone      | culture | isActive |
      | 34529079-48f0-4f00-9bec-e2329a8bdaac | customer | userFromCustomer2   | Customer1 | User1    | usercus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    When User "userFromCustomer2" is added to property set with name "ps1_name" by user "userWithPropSet"
    Then Response code is "422"
    And Custom code is 42202

  Scenario: Updating Property Set-User relationship by user with access to the property set
    Given Check is active attribute is "false" for relation between user "userWithPropSet" and property set "ps1_name"
    When IsActive for relation between user "userWithPropSet" and property set "ps1_name" is set to "true" by user "userWithPropSet"
    Then Response code is "204"
    And Check is active attribute is "true" for relation between user "userWithPropSet" and property set "ps1_name"

  Scenario: Updating Property Set-User relationship by user without access to the property set
    Given Check is active attribute is "false" for relation between user "userWithPropSet" and property set "ps1_name"
    When IsActive for relation between user "userWithPropSet" and property set "ps1_name" is set to "true" by user "userWithNoPropSet"
#      Fails until DP-1330 fixed
    Then Response code is "404"
    And Check is active attribute is "false" for relation between user "userWithPropSet" and property set "ps1_name"

  Scenario: Delete user from property set by user who has access to it
    When User "userWithPropSet" is removed from property set "ps1_name" by user "userWithPropSet"
    Then Response code is "204"
    And Body is empty
    And User with "userWithPropSet" isn't there for property set "ps1_name"

  Scenario: Delete user from property set by user who does not have access to it
    When User "userWithPropSet" is removed from property set "ps1_name" by user "userWithNoPropSet"
    Then Response code is "404"

