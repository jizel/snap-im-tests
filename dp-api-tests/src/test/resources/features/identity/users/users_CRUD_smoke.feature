@Identity
@Smoke
Feature: Users customer roles CRUD

  Background:
    Given Database is cleaned and default entities are created
    Given The following users exist for customer "11111111-0000-4000-a000-555555555555"
      | type     | username | firstName | lastName | email                | timezone      | languageCode |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Relation between user "default1" and default property exists

  Scenario: Assigning role to user customer
    Given Switch for user customer role tests
    Given The following roles exist
      | id                                   | roleName    | description            | applicationId                        |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | user_role_1 | optional description 1 | 11111111-0000-4000-a000-111111111111 |
    When User "defaultSnapshotUser" assigns role "a318fd9a-a05d-42d8-8e84-42e904ace123" to relation between user "default1" and customer "11111111-0000-4000-a000-555555555555"
    Then Response code is "201"


  Scenario: Assigning role to user property
    Given Switch for user property role tests
    Given The following properties exist with random address and billing address for user "default1"
     | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
     | 721a284b-9dc4-48e6-8353-6ec55b89e291 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 11111111-0000-4000-a000-555555555555 |
    Given The following roles exist
      | id                                   | roleName    | description            | applicationId                                   |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | user_role_1 | optional description 1 | 11111111-0000-4000-a000-111111111111 |
    When User "defaultSnapshotUser" assigns role "a318fd9a-a05d-42d8-8e84-42e904ace123" to relation between user "default1" and property "721a284b-9dc4-48e6-8353-6ec55b89e291"
    Then Response code is "201"

  Scenario: Assigning role to user property set
    Given Switch for user property set role tests
    Given The following property sets exist for customer with id "11111111-0000-4000-a000-555555555555" and user "default1"
      | id                                   | name            | description            | type            |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 | ps1_name        | ps1_description        | brand           |
    Given The following roles exist
      | id                                   | roleName    | description            | applicationId                                   |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | user_role_1 | optional description 1 | 11111111-0000-4000-a000-111111111111 |
    When User "defaultSnapshotUser" assigns role "a318fd9a-a05d-42d8-8e84-42e904ace123" to relation between user "default1" and property set "ps1_name"
    Then Response code is "201"

  Scenario: Get, update, delete user
    When User "default1" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "user_id"
    And Body contains entity with attribute "user_type" value "customer"
    And Body contains entity with attribute "user_name" value "default1"
    And Body contains entity with attribute "first_name" value "Default1"
    And Body contains entity with attribute "last_name" value "User1"
    And Body contains entity with attribute "email" value "def1@snapshot.travel"
    And Body contains entity with attribute "timezone" value "Europe/Prague"
    And Body contains entity with attribute "culture" value "cs-CZ"
    When User "default1" is inactivated
    Then Response code is "204"
    And Body is empty
    And User "default1" is not active
    Given Relation between user "default1" and customer "11111111-0000-4000-a000-555555555555" is deleted
    When User "default1" is deleted
    Then Response code is "409"
