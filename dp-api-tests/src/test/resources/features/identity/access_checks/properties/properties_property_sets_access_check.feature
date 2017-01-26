@Identity
Feature: Properties-Property Sets access check feature
  - Checking when certain user should and should not have access to certain properties
  - 404 is returned for unauthorized users (403 when the X-Auth-UserId header is missing)`
  - All rules apply also to second level entities in both ways (e.g. properties/p_id/property_sets, property_set/p_set_id/properties) - reversed endpoints should be covered in other features (property_sets)

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
  #      Creating default user just to be able to get property by code. Access checks are always steps 'by user'
    Given Default Snapshot user is created
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
      | 0d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithProp   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 1d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoProp | Customer2 | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property is created with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |


  Scenario: Second level entities - User sees only property sets he should for property he owns
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | propertySetName | propertySetType |
      | prop_set1       | brand           |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp"
      | propertySetName | propertySetType |
      | prop_set2       | brand           |
    When Property with code "p1_code" is added to property set "prop_set1"
    When Property set with name "prop_set1" for property with code "p1_code" is requested by user "userWithProp"
    Then Response code is "200"
    When Property set with name "prop_set2" for property with code "p1_code" is requested by user "userWithProp"
    Then Response code is "404"
    When List of all property sets is got for property with code "p1_code" by user "userWithProp"
    Then Response code is "200"
    And Total count is "1"


  Scenario: Update property - propertySet relationship by user has has access to them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When  Relation between property with code "p1_code" and property set "ps1_name" is updated by user "userWithProp" with
      | isActive |
      | true     |
    Then Response code is "204"

#  DP-1710
  Scenario: Update property - propertySet relationship by user does not have access to the them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When  Relation between property with code "p1_code" and property set "ps1_name" is updated by user "userWithNoProp" with
      | isActive |
      | true     |
    Then Response code is "404"

  Scenario: Delete property - PropertySet relationship by user who has access to them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Relation between property with code "p1_code" and property set "ps1_name" is deleted by user "userWithProp"
    Then Response code is "204"

  Scenario: Delete property - PropertySet relationship by user who does not have access to them
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithProp"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Relation between property with code "p1_code" and property set "ps1_name" is deleted by user "userWithNoProp"
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Delete property - PropertySet relationship by user who has access to the property but not to the property set
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithNoProp"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When Relation between property with code "p1_code" and property set "ps1_name" is deleted by user "userWithProp"
    Then Response code is "404"
    And Custom code is 40402
