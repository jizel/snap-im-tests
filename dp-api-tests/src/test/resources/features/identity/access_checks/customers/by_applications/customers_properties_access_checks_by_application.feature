@Identity
Feature: Customers-Properties Application access check feature - GET
  - Checking when certain application should and should not have access to certain customers' properties
  - Only Customers for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - Only Properties for which there is a CommercialSubscription linking to the ApplicationVersion (through Application) are accessible
  - 404 is returned for unauthorized users (403 when the X-Auth-AppId header is missing)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name                        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | CustomerWithSubscription    | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 00000000-0000-4000-8000-123000000abc | CustomerWithoutSubscription | c2@tenants.biz | salesforceid_2 | CZ10000002 | true           | +420987654321 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | type     | username      | firstName | lastName | email                | timezone      | languageCode | isActive |
      | customer | userWithCust1 | Customer  | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following partner exist
      | id                                   | name                   | email                   | website                    |
      | 11100000-0000-4000-a000-000000000111 | PartnerForSubscription | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | name                     | id                                   | partnerId                            | isInternal | website                    |
      | App With Subscription    | 22200000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
      | App Without Subscription | 00000000-0000-4000-a000-000000000222 | 11100000-0000-4000-a000-000000000111 | true       | http://www.snapshot.travel |
    Given The following application versions exists
      | id                                   | apiManagerId | name                    | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | 1            | versionWithSubscription | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    Given The following application versions exists
      | id                                   | isNonCommercial | apiManagerId | name                       | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000444 | false           | 1            | versionWithoutSubscription | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
      | 22200000-0000-4000-a000-000000000555 | true            | 3            | nonCommercialversion       | certified | Active version description   | 00000000-0000-4000-a000-000000000222 |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId   | name                          | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 33300000-0000-4000-a000-000000000111 | salesforceid_1 | property_with_subscription    | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
      | 33300000-0000-4000-a000-000000000222 | salesforceid_2 | property_without_subscription | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 44400000-0000-4000-a000-000000000444 | 12300000-0000-4000-a000-000000000000 | 33300000-0000-4000-a000-000000000111 | 22200000-0000-4000-a000-000000000222 |
    Given The following api subscriptions exist
      | id                                   | applicationVersionId                 | commercialSubscriptionId             |
      | 55500000-0000-4000-a000-000000000555 | 22200000-0000-4000-a000-000000000333 | 44400000-0000-4000-a000-000000000444 |
    Given Relation between user "userWithCust1" and property with code "p1_code" exists with is_active "true"
    Given Relation between user "userWithCust1" and property with code "p2_code" exists with is_active "true"


  Scenario: Second level entities - Application sees only properties it should for accessible customer sees
    Given Relation between property with code "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property with code "p1_code" from customer with id "12300000-0000-4000-a000-000000000000" is got by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "200"
    When List of all customer properties is got for customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "200"
    And Total count is "1"
    When Property with code "p1_code" from customer with id "12300000-0000-4000-a000-000000000000" is got by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    When List of all customer properties is got for customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    When Property with code "p1_code" from customer with id "12300000-0000-4000-a000-000000000000" is got by user "userWithCust1" for application version "nonCommercialversion"
    Then Response code is "200"
    When List of all customer properties is got for customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1" for application version "nonCommercialversion"
    Then Response code is "200"

  Scenario: Add user to property by application with and without access to the property
    When Property with code "p1_code" is added to customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1" for application version "versionWithoutSubscription"
    Then Response code is "403"
    When Property with code "p2_code" is added to customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "422"
    When Property with code "p1_code" is added to customer with id "00000000-0000-4000-8000-123000000abc" by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "404"
    When Property with code "p1_code" is added to customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1" for application version "versionWithSubscription"
    Then Response code is "201"
    And Body contains entity with attribute "property_id" value "33300000-0000-4000-a000-000000000111"
    When Property with code "p2_code" is added to customer with id "12300000-0000-4000-a000-000000000000" by user "userWithCust1" for application version "nonCommercialversion"
    Then Response code is "201"

  Scenario: Update customer property relationship by application with and without access to the customer and the property
    Given Relation between property with code "p1_code" and customer with id "12300000-0000-4000-a000-000000000000" exists with type "chain" from "2015-01-01" to "2050-12-31"
    When Property with code "p1_code" for customer with id "12300000-0000-4000-a000-000000000000" is updated by user "userWithCust1" for application version "versionWithoutSubscription" with
      | type  |
      | owner |
    Then Response code is "403"
    When Property with code "p1_code" for customer with id "12300000-0000-4000-a000-000000000000" is updated by user "userWithCust1" for application version "versionWithSubscription" with
      | type  |
      | owner |
    Then Response code is "204"
    When Property with code "p1_code" for customer with id "12300000-0000-4000-a000-000000000000" is updated by user "userWithCust1" for application version "nonCommercialversion" with
      | type       |
      | management |
    Then Response code is "204"