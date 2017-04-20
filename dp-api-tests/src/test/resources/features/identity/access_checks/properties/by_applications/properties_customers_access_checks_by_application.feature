@Identity
@Properties
Feature: Properties-Customers Application access check feature
  - Checking when certain application should and should not have access to certain customers of some property
  - Only Customers of Properties for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | user1    | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName          | id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
    | id                                   | apiManagerId | versionName             | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    Given The following application versions exists
      | id                                   | apiManagerId | versionName                | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId   | name                          | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 33300000-0000-4000-a000-000000000111 | salesforceid_1 | property_with_subscription    | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
      | 33300000-0000-4000-a000-000000000222 | salesforceid_2 | property_without_subscription | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 33300000-0000-4000-a000-000000000111 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | id                                   | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
    Given Relation between user "user1" and property with code "p1_code" exists with is_active "true"
    Given Relation between user "user1" and property with code "p2_code" exists with is_active "true"
    Given Relation between property with code "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" exists

    Scenario: Only customers of property with valid commercial subscription are visible
      When List of all customers for property with code "p1_code" is got by user "user1" for application version "versionWithSubscription"
      Then Response code is "200"
      And Total count is "1"
      When List of all customers for property with code "p1_code" is got by user "user1" for application version "versionWithoutSubscription"
      Then Response code is "403"
      When List of all customers for property with code "p2_code" is got by user "user1" for application version "versionWithSubscription"
      Then Response code is "404"
      And Custom code is 40402
      When Relation between property "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" is requested by user "user1" for application version "versionWithSubscription"
      Then Response code is "200"
      When Relation between property "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" is requested by user "user1" for application version "versionWithoutSubscription"
      Then Response code is "403"
      And Custom code is 40301

  Scenario: Update Property-Customer relationship by application with and without access
    When Property customer relationship for property with code "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" is updated by user "user1" for application version "versionWithoutSubscription" with
      | type  |
      | owner |
    Then Response code is "403"
    And Custom code is 40301
    When Property customer relationship for property with code "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" is updated by user "user1" for application version "versionWithSubscription" with
      | type  |
      | owner |
    Then Response code is "204"

  Scenario: Delete Property-Customer relationship by application with and without access
    When Property customer relationship for property with code "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" is deleted by user "user1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    When Property customer relationship for property with code "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" is deleted by user "user1" for application version "versionWithSubscription"
    Then Response code is "204"