@Identity
Feature: Property sets create update delete

  #TODO add etag things to get/update/create
  #TODO check adding propery set for customer id not existent

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 49ae92d9-2d80-47d9-994b-77f5f598336a | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "49ae92d9-2d80-47d9-994b-77f5f598336a" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "49ae92d9-2d80-47d9-994b-77f5f598336a" and user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 49ae92d9-2d80-47d9-994b-77f5f598336a |

  @Smoke
  Scenario: Creating property set for customer with code "c1t"
    When The following property set is created for customer with id "49ae92d9-2d80-47d9-994b-77f5f598336a" and user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertySetName  | propertySetDescription | propertySetType |
      | ps1_created_name | ps1_description        | branch          |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "ps1_created_name"
    And Body contains entity with attribute "property_set_type" value "branch"
    And "Location" header is set and contains the same property set
    And Etag header is present

  @Smoke
  Scenario: Deleting Property set
    When Property set with name "ps1_name" for customer with code "c1t" is deleted
    Then Response code is "204"
    And Body is empty
    And Property set with same id doesn't exist

  Scenario: Checking error code for deleting property
    When Nonexistent property set id is deleted
    Then Response code is "204"

  Scenario Outline: Updating property set
  Property sets for customer "c1t" were deleted in background, so we don't need to clean here.
    When Property set with name "<propertySetName>" for customer "49ae92d9-2d80-47d9-994b-77f5f598336a" is updated with following data
      | propertySetName           | propertySetDescription   | propertySetType   |
      | <updated_propertySetName> | <propertySetDescription> | <propertySetType> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated property set with name "<updated_propertySetName>" for customer "49ae92d9-2d80-47d9-994b-77f5f598336a" has following data
      | propertySetName           | propertySetDescription   | propertySetType   |
      | <updated_propertySetName> | <propertySetDescription> | <propertySetType> |
    Examples:
      | propertySetName | updated_propertySetName | propertySetDescription  | propertySetType |
      | ps1_name        | ps1_updated             | ps1_updated_description | branch          |


    #TODO error codes