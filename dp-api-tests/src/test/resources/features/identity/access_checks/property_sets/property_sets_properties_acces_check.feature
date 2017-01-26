@Identity
Feature: Property sets Properties access check feature
  - Checking when certain user should and should not have access to certain property sets
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)`
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (properties)

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
  #      Creating default user just to be able to get property by code. Access checks are always steps 'by user'
    Given Default Snapshot user is created
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName          | firstName | lastName | email                | timezone      | culture | isActive |
      | 0d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithPropSet   | Customer1 | User1    | usr1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 1d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoPropSet | Customer2 | User2    | usr2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithPropSet"
      | propertySetName | propertySetType | propertySetId                        |
      | ps1_name        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 |


    Scenario: Second level entities - User sees only properties he should for property set he owns
      Given The following properties exist with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Given The following properties exist with random address and billing address for user "1d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      When Property with code "p1_code" is added to property set "ps1_name"
      When Property with code "p1_code" for property set "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "200"
      When List of all properties for property set with name "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "200"
      And Total count is "1"


    Scenario: Second level entities - User doesn't see properties for property set when he doesn't have access to the property and the property set all at once
      Given The following properties exist with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Given Relation between user with username "userWithNoPropSet" and property with code "p1_code" exists
      When Property with code "p1_code" for property set "ps1_name" is requested by user "userWithPropSet"
      Then Response code is "404"
      And Custom code is 40402
      When Property with code "p1_code" for property set "ps1_name" is requested by user "userWithNoPropSet"
      Then Response code is "404"
      And Custom code is 40402
      When List of all properties for property set with name "ps1_name" is requested by user "userWithNoPropSet"
      #      Fails until DP-1330 fixed
      Then Response code is "404"
      And Custom code is 40402


  Scenario: Adding property to property set by user with access to it
    Given The following properties exist with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    When Property with code "p1_code" is added to property set "ps1_name" by user "userWithPropSet"
    Then Response code is "201"

  Scenario: Adding property to property set by user without access to the property set
    Given The following properties exist with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    When Property with code "p1_code" is added to property set "ps1_name" by user "userWithNoPropSet"
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Adding property to property set by user without access to the property
    Given The following properties exist with random address and billing address for user "1d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    When Property with code "p1_code" is added to property set "ps1_name" by user "userWithNoPropSet"
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Removing property from property set by user with access
    Given The following properties exist with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Property with code "p1_code" is removed from property set "ps1_name" by user "userWithPropSet"
    Then Response code is "204"
    And Body is empty
    And Property with code "p1_code" isn't there for property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"

  Scenario: Removing property from property set by user without access to the property set
    Given The following properties exist with random address and billing address for user "1d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Property with code "p1_code" is removed from property set "ps1_name" by user "userWithNoPropSet"
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Removing property from property set by user without access to the property
    Given The following properties exist with random address and billing address for user "1d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Property with code "p1_code" is removed from property set "ps1_name" by user "userWithPropSet"
#    DP-1330
    Then Response code is "404"
    And Custom code is 40402