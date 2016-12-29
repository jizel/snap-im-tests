@Identity
Feature: Property sets create update delete

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 49ae92d9-2d80-47d9-994b-77f5f598336a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "49ae92d9-2d80-47d9-994b-77f5f598336a" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1  | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Default Snapshot user is created for customer "49ae92d9-2d80-47d9-994b-77f5f598336a"
    Given The following property sets exist for customer with id "49ae92d9-2d80-47d9-994b-77f5f598336a" and user "snaphotUser1"
      | propertySetId                        | propertySetName | propertySetDescription | propertySetType |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 | ps1_name        | ps1_description        | brand           |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 49ae92d9-2d80-47d9-994b-77f5f598336a |

  @Smoke
  Scenario Outline: Creating property set for customer with id "49ae92d9-2d80-47d9-994b-77f5f598336a"
    When The following property set is created for customer with id "49ae92d9-2d80-47d9-994b-77f5f598336a" and user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertySetName   | propertySetDescription   | propertySetType   |
      | <propertySetName> | <propertySetDescription> | <propertySetType> |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "<propertySetName>"
    And Body contains entity with attribute "property_set_type" value "<propertySetType>"
    And "Location" header is set and contains the same property set
    And Etag header is present
    Examples:
    | propertySetName | propertySetDescription | propertySetType |
    | ps1_name        | ps1_description        | brand           |
    | ps2_name        | ps2_description        | geolocation     |
    | ps3_name        | ps3_description        | hotel_type      |

  @Smoke
  Scenario: Deleting Property set
    When Property set with name "ps1_name" for customer with id "49ae92d9-2d80-47d9-994b-77f5f598336a" is deleted
    Then Response code is "204"
    And Body is empty
    And Property set with same id doesn't exist

  Scenario: Checking error code for deleting property
    When Nonexistent property set id is deleted
    Then Response code is "204"

  Scenario Outline: Updating property set
#  Property sets for customer "49ae92d9-2d80-47d9-994b-77f5f598336a" were deleted in background, so we don't need to clean here.
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
      | propertySetName | updated_propertySetName | propertySetDescription   | propertySetType |
      | ps1_name        | ps1_updated             | ps1_updated_description  | brand           |
      | ps1_name        | ps1_updated2            | ps1_updated_description  | geolocation     |
      | ps1_name        | ps2_updated2            | ps1_updated_description2 | hotel_type      |


  Scenario Outline: Checking error codes for creating property set
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "message" value "<message>"

    Examples:
      | json_input_file                                                                | method | module   | url                     | error_code | custom_code | message                                                                                                     |
      | /messages/identity/property_sets/create_property_set-wrong_type1.json          | POST   | identity | /identity/property_sets | 422        | 42201       | Semantic validation errors in the request body.                                                                   |
      | /messages/identity/property_sets/create_property_set-wrong_type2.json          | POST   | identity | /identity/property_sets | 422        | 42201       | Semantic validation errors in the request body.                                                                   |
      | /messages/identity/property_sets/create_property_set-missing_ps_type.json      | POST   | identity | /identity/property_sets | 422        | 42201       | Semantic validation errors in the request body.                                                                   |
      | /messages/identity/property_sets/create_property_set-missing_name.json         | POST   | identity | /identity/property_sets | 422        | 42201       | Semantic validation errors in the request body.                                                                   |
      | /messages/identity/property_sets/create_property_set-missing_customer_id.json  | POST   | identity | /identity/property_sets | 422        | 42201       | Semantic validation errors in the request body.                                                                   |
      | /messages/identity/property_sets/create_property_set-nonexistent_customer.json | POST   | identity | /identity/property_sets | 422        | 42202       | Reference does not exist. The entity Customer with ID 5d829079-48f0-4f00-9bec-e2329a8bdaac cannot be found.       |

  Scenario Outline: Send POST request with empty body to all property set endpoints
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                           |
      | identity/property_sets/                                                                                       |
      | identity/property_sets/c729e3b0-69bf-4c57-91bd-30230d2c1bd0                                                   |
      | identity/property_sets/c729e3b0-69bf-4c57-91bd-30230d2c1bd0/users/                                            |
#      Fail because of DP-1578
      | identity/property_sets/c729e3b0-69bf-4c57-91bd-30230d2c1bd0/users/5d829079-48f0-4f00-9bec-e2329a8bdaac        |
      | identity/property_sets/c729e3b0-69bf-4c57-91bd-30230d2c1bd0/properties/                                       |