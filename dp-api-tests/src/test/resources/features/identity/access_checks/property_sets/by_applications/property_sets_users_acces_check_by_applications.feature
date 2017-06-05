@Identity
Feature: Property sets- Users Application access check feature
  - Checking when certain application should and should not have access to certain property set
  - Only Users for Property sets for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name                        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 12300000-0000-4000-a000-000000000111 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | type     | username               | firstName | lastName | email                | timezone      | languageCode | isActive |
      | customer | userWithPropertySet    | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | userWithoutPropertySet | Customer  | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given Relation between user "userWithPropertySet" and property with code "defaultPropertyCode" exists
    Given Relation between user "userWithoutPropertySet" and property with code "defaultPropertyCode" exists
    Given The following partner exist
      | id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | name                     | id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | id                                   | apiManagerId | name                       | status    | description                | applicationId                                   |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription    | certified | Active version description | 22200000-0000-4000-a000-000000000222 |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description | 00000000-0000-4000-a000-000000000222 |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000000" and user "userWithPropertySet"
      | name            | type            | id                                   |
      | ps1_name        | brand           | 12300000-1111-4c57-91bd-30230d2c1bd0 |
    Given The following property sets exist for customer with id "12300000-0000-4000-a000-000000000111"
      | name            | type            | id                                   |
      | ps2_name        | brand           | 23400000-1111-4c57-91bd-30230d2c1bd0 |
    Given Relation between user "userWithPropertySet" and property set "ps2_name" exists
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | id                                   | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |

  Scenario: Application sees only users for property set it has subscription to
    When List of all users for property set "ps1_name" is requested by user "userWithPropertySet" for application version "versionWithSubscription"
    Then Response code is "200"
    And Total count is "1"
    When List of all users for property set "ps1_name" is requested by user "userWithPropertySet" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "userWithPropertySet" for property set "ps1_name" is requested by user "userWithPropertySet" for application version "versionWithSubscription"
    Then Response code is "200"
    When User "userWithPropertySet" for property set "ps1_name" is requested by user "userWithPropertySet" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301

  Scenario: Add user to property set by application with and without access
    When User "userWithoutPropertySet" is added to property set with name "ps1_name" by user "userWithPropertySet" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "userWithoutPropertySet" is added to property set with name "ps1_name" by user "userWithPropertySet" for application version "versionWithSubscription"
    Then Response code is "201"

  Scenario: Updating Property Set-User relationship by application with and without access
    When User "userWithoutPropertySet" is added to property set with name "ps1_name" with is_active "false"
    When IsActive for relation between user "userWithoutPropertySet" and property set "ps1_name" is set to "true" by user "userWithPropertySet" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When IsActive for relation between user "userWithoutPropertySet" and property set "ps1_name" is set to "true" by user "userWithPropertySet" for application version "versionWithSubscription"
    Then Response code is "204"
    And Check is active attribute is "true" for relation between user "userWithPropertySet" and property set "ps1_name"

  Scenario: Delete user from property set by application with and without access
    When User "userWithPropertySet" is removed from property set "ps1_name" by user "userWithPropertySet" for application version "versionWithoutSubscription"
    Then Response code is "403"
    And Custom code is 40301
    When User "userWithPropertySet" is removed from property set "ps1_name" by user "userWithPropertySet" for application version "versionWithSubscription"
    Then Response code is "204"
    And Body is empty
    And User with "userWithPropertySet" isn't there for property set "ps1_name"