@Identity
Feature: Property sets Properties access check feature
  - Checking when certain user should and should not have access to certain property sets
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)`
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName          | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithPropSet   | Customer1 | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | userWithNoPropSet | Customer2 | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithPropSet"
      | name            | type            | id                                   |
      | ps1_name        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 |


    Scenario: Second level entities - User sees only properties he should for property set he owns
      Given The following properties exist with random address and billing address for user "userWithPropSet"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Given The following properties exist with random address and billing address for user "userWithNoPropSet"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      When Property with code "p1_code" is added to property set "ps1_name" with is_active "false"
      When Property with code "p1_code" for property set "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "200"
      When Relation between property "p1_code" and property set "ps1_name" is activated
      When Property with code "p1_code" for property set "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "200"
      When List of all properties for property set with name "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "200"
      And Total count is "1"

    Scenario: Second level entities - User doesn't see properties for property set when he doesn't have access to the property and the property set all at once
      Given The following properties exist with random address and billing address for user "userWithNoPropSet"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"
      When Property with code "p1_code" for property set "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "404"
      And Custom code is 40402
      When List of all property sets is got for property with code "p1_code" by user "userWithNoPropSet"
      Then Total count is "0"
      When Property with code "p1_code" for property set "ps1_name" is requested by user "userWithNoPropSet"
      Then Response code is "404"
      And Custom code is 40402
      When List of all properties for property set with name "ps1_name" is requested by user "userWithNoPropSet"
      Then Response code is "404"
      And Custom code is 40402
      Given Property with code "p1_code" is added to property set "ps1_name" with is_active "true"
      When Property with code "p1_code" for property set "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "200"


  Scenario: Adding property to property set by user with access to it
    Given The following properties exist with random address and billing address for user "userWithPropSet"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"
    When Relation between user "userWithPropSet" and property set "ps1_name" is inactivated
    And Property with code "p1_code" is added to property set "ps1_name" by user "userWithPropSet"
    Then Response code is "404"
    When Relation between user "userWithPropSet" and property set "ps1_name" is activated
    And Property with code "p1_code" is added to property set "ps1_name" by user "userWithPropSet"
    Then Response code is "201"

  Scenario: Adding property to property set by user without access to the property set
    Given The following properties exist with random address and billing address for user "userWithPropSet"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"
    When Property with code "p1_code" is added to property set "ps1_name" by user "userWithNoPropSet"
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Adding property to property set by user without access to the property
    Given The following properties exist with random address and billing address for user "userWithNoPropSet"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"
    When Property with code "p1_code" is added to property set "ps1_name" by user "userWithNoPropSet"
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Removing property from property set by user with and without access
    Given The following properties exist with random address and billing address for user "userWithPropSet"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    # When property set has only one property
    # Then anyone who has access to property, has implicit access to property set.
    # And removing explicit access to the property set has no effect
    Given The following properties exist with random address and billing address for user "userWithNoPropSet"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p2_code"
    Given Relation between property with code "p2_code" and property set with name "ps1_name" exists
    And Relation between user "userWithPropSet" and property set "ps1_name" is inactivated
    When Property with code "p1_code" is removed from property set "ps1_name" by user "userWithPropSet"
    Then Response code is "404"
    And Relation between user "userWithPropSet" and property set "ps1_name" is activated
    When Property with code "p1_code" is removed from property set "ps1_name" by user "userWithPropSet"
    Then Response code is "204"
    And Body is empty
    And Property with code "p1_code" isn't there for property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
