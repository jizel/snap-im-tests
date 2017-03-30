@Identity
Feature: Customers-Users Application access check feature - GET
  - Checking when certain application should and should not have access to certain customers' users
  - Only Customers for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - Only Properties for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName                 | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 00000000-0000-4000-8000-123000000abc | CustomerWithoutSubscription | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithCust1 | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following users exist for customer "00000000-0000-4000-8000-123000000abc" as primary "false"
      | userType | userName      | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithCust2 | Customer  | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | Id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName          | Id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName             | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName                | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000444 | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following commercial subscriptions exist
      | Id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
      | 44400000-0000-4000-a000-000000000555 | 00000000-0000-4000-8000-123000000abc | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | Id                                   | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
      | 55500000-0000-4000-a000-000000000666 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000555 |
    Given Relation between user "userWithCust1" and property "11111111-0000-4000-a000-666666666666" exists with is_active "true"
    Given Relation between user "userWithCust2" and property "11111111-0000-4000-a000-666666666666" exists with is_active "true"
    Given Relation between user "userWithCust1" and customer with id "00000000-0000-4000-8000-123000000abc" exists

  Scenario: Application sees only users for customer it has subscribed to
    When List of all users for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "200"
    And Total count is "1"
    When List of all users for customer with id "12300000-0000-4000-a000-000000000000" is requested by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "403"

  Scenario: Add user to customer by application with and without access to the customer
    When User "userWithCust2" is added to customer with id "12300000-0000-4000-a000-000000000000" with isPrimary "true" by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    When User "userWithCust2" is added to customer with id "12300000-0000-4000-a000-000000000000" with isPrimary "true" by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "201"

  Scenario: Updating User Customer relationship by application with and without access
    When Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is updated with isPrimary "false" by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    When Relation between user "userWithCust1" and customer with id "12300000-0000-4000-a000-000000000000" is updated with isPrimary "false" by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "204"

  Scenario: Deleting User Customer relationship by application with and without access
    When User "userWithCust1" is removed from customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    When User "userWithCust1" is removed from customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "204"