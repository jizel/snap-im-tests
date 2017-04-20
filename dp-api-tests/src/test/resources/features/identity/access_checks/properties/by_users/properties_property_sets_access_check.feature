@Identity
@Properties
Feature: Properties-Property Sets access check feature
  - Checking when certain user should and should not have access to certain properties
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)`
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (property_sets)

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithProp   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | userWithNoProp | Customer2 | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property is created with random address and billing address for user "userWithProp"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"


  Scenario: Second level entities - User sees only property sets he should for property he owns
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp" with is_active "false"
      | name            | type            |
      | prop_set1       | brand           |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp" with is_active "false"
      | name            | type            |
      | prop_set2       | brand           |
    Given Relation between user "userWithProp" and property with code "p1_code" is inactivated
    When Relation between user "userWithProp" and property set "prop_set1" is inactivated
    When Property with code "p1_code" is added to property set "prop_set1"
    When Property set with name "prop_set1" for property with code "p1_code" is requested by user "userWithProp"
    Then Response code is "404"
    When Relation between user "userWithProp" and property set "prop_set1" is activated
    When Property set with name "prop_set1" for property with code "p1_code" is requested by user "userWithProp"
    Then Response code is "200"
    When Property set with name "prop_set2" for property with code "p1_code" is requested by user "userWithProp"
    Then Response code is "404"
    When List of all property sets is got for property with code "p1_code" by user "userWithProp"
    Then Response code is "200"
    And Total count is "1"


  Scenario: Update property - propertySet relationship by user has has access to them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists
    When  Relation between property with code "p1_code" and property set "ps1_name" is updated by user "userWithProp" with
      | isActive |
      | true     |
    Then Response code is "204"

  Scenario: Update property - propertySet relationship by user does not have access to the them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists
    When  Relation between property with code "p1_code" and property set "ps1_name" is updated by user "userWithNoProp" with
      | isActive |
      | true     |
    Then Response code is "404"

  Scenario: Delete property - PropertySet relationship by user who has access to them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists
    When Relation between property with code "p1_code" and property set "ps1_name" is deleted by user "userWithProp"
    Then Response code is "204"

  Scenario: Delete property - PropertySet relationship by user who does not have access to them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists
    When Relation between property with code "p1_code" and property set "ps1_name" is deleted by user "userWithNoProp"
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Delete property - PropertySet relationship by user who has access to the property but not to the property set
    Given The following property is created with random address and billing address
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.com    | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p2_code"
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p2_code" and property set with name "ps1_name" exists
    When Relation between property with code "p1_code" and property set "ps1_name" is deleted by user "userWithProp"
    Then Response code is "404"
    And Custom code is 40402
