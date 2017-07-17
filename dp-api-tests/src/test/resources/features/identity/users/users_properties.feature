@Identity
Feature: User property relationship tests
#  - TODO implement tests for all endpoints (can be partially covered by access check tests)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    When The following user is created for customer "12300000-0000-4000-a000-000000000000" as primary "false"
      | id                                    | type       | username      | firstName | lastName | email                         | timezone      | languageCode |
      | 6d829079-48f0-4f00-9bec-e2329a8bdaac  | customer   | customerUser1 | Customer  | User1    | customeruser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 4d266045-1cf1-4735-8ef9-216de1370f2e | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 12300000-0000-4000-a000-000000000000 |
    Given Relation between user "customerUser1" and property with code "p1_code" exists with is_active "true"

  Scenario Outline: Empty update of (customer type) user - property relationship returns correct code - DP-1777
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                   |
      | identity/users/6d829079-48f0-4f00-9bec-e2329a8bdaac/properties/4d266045-1cf1-4735-8ef9-216de1370f2e   |