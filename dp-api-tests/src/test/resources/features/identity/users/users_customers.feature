@Identity
Feature: User customer relationship tests
#  - TODO implement tests for all endpoints (can be partially covered by access check tests)

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    Scenario: User-customer-relationship attribute of created user creates correct relation between entities (covers DP-1804)
      Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "false"
        | userType   | userName      | firstName | lastName | email                         | timezone      | culture |
        | customer   | customerUser1 | Customer  | User1    | customerUser1@snapshot.travel | Europe/Prague | cs-CZ   |
      When Relation between user "customerUser1" and customer "12300000-0000-4000-a000-000000000000" is requested by user "defaultSnapshotUser"
      Then Response code is "200"
      And Body contains entity with attribute "is_primary" value "false"
      And Body contains entity with attribute "is_active" value "true"
      And Body contains entity with attribute "customer_id" value "12300000-0000-4000-a000-000000000000"
      Given The following users exist for customer "12300000-0000-4000-a000-000000000000" as primary "true"
        | userType   | userName      | firstName | lastName | email                         | timezone      | culture |
        | customer   | customerUser2 | Customer  | User1    | customerUser2@snapshot.travel | Europe/Prague | cs-CZ   |
      When Relation between user "customerUser2" and customer "12300000-0000-4000-a000-000000000000" is requested by user "defaultSnapshotUser"
      Then Response code is "200"
      And Body contains entity with attribute "is_primary" value "true"
      And Body contains entity with attribute "is_active" value "true"
      And Body contains entity with attribute "customer_id" value "12300000-0000-4000-a000-000000000000"
