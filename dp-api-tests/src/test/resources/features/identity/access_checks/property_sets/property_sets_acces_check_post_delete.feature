@Identity
Feature: Property sets access check feature - POST and DELETE
  - User can update/delete specific property set and it's relationships only if he owns it
  - Check positive scenarios
  - In negative tests, ETAG needs to be obtained by different user first
  - Various cases of when user should have access to a property set (instance) are covered in GET feature (user is a member of userGroup, child property set etc.)
  - 404 is returned for unauthorized users

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


  Scenario: User with access updates property set
    When Property set "ps1_name" is updated with following data by user "userWithPropSet"
      | propertySetName | propertySetDescription | propertySetType   |
      | updated_name    | updated description    | geolocation       |
    Then Response code is "204"
    And Body is empty
    And Updated property set "updated_name" has following data
      | propertySetName | propertySetDescription | propertySetType   |
      | updated_name    | updated description    | geolocation       |

  Scenario: User without access tries to update property set
    When Property set "ps1_name" is updated with following data by user "userWithNoPropSet"
      | propertySetName | propertySetDescription | propertySetType   |
      | updated_name    | updated description    | geolocation       |
    Then Response code is "404"
    When Property set "ps1_name" is requested by user "userWithPropSet"
    Then Response code is "200"

#  DP-1683
  Scenario: Deleting Property set by user who can access it
    When Property set "ps1_name" is deleted by user "userWithPropSet"
    Then Response code is "204"
    And Body is empty
    And Property set with same id doesn't exist
#  DP-1683
  Scenario: Deleting Property set by user without access to it
    When Property set "ps1_name" is deleted by user "userWithNoPropSet"
    Then Response code is "404"
    When Property set "ps1_name" is requested by user "userWithPropSet"
    Then Response code is "200"