@Identity
@Properties
Feature: Properties create update delete

  # TODO add etag things to get/update/create
  # TODO: check error codes for creating/updating property
  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | type     | username | firstName | lastName | email                | timezone      | languageCode |
      | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property is created with random address and billing address for user "default1"
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

  Scenario Outline: Validate that property regions belong to the correct country
    When A property for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" from country "<country>" region "<region>" code "<code>" email "<email>" is created by user "0b000000-0000-4444-8888-000000000000"
    Then Content type is "application/json"
    And Response code is 201
    And Body contains entity with attribute "address.region" value "<region>"

    Examples:
      | country | region                            | code       | email           |
      | US      | Alaska                            | propcode2  | mail2@mail.com  |
    # Australia regions
      | AU      | Ashmore and Cartier Islands       | propcode51 | mail51@mail.com |
    # Canada regions
      | CA      | Ontario                           | propcode66 | mail66@mail.com |

  Scenario Outline: Checking error codes for regions
    When A property for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" from country "<country>" region "<region>" code "<code>" email "<email>" is created by user "0b000000-0000-4444-8888-000000000000"
    Then Content type is "application/json"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "message" value "Reference does not exist. The entity Region with ID <region> cannot be found."

    Examples:
      | country | region           | code        | email           | response_code | custom_code |
      | DE      | invalid          | propcode8  | mail8@mail.com  | 422           | 42202       |
      | BG      | invalid          | propcode9  | mail9@mail.com  | 422           | 42202       |
      | US      | invalid          | propcode10 | mail10@mail.com | 422           | 42202       |
      | CZ      | invalid          | propcode11 | mail11@mail.com | 422           | 42202       |
      | AU      | invalid          | propcode12 | mail12@mail.com | 422           | 42202       |
      | CZ      | Texas            | propcode14 | mail14@mail.com | 422           | 42202       |
      | AU      | Ohio             | propcode15 | mail15@mail.com | 422           | 42202       |
      | US      | bg_region        | propcode13 | mail13@mail.com | 422           | 42202       |
      | CZ      | us_region        | propcode14 | mail14@mail.com | 422           | 42202       |
      | AU      | bg_region        | propcode15 | mail15@mail.com | 422           | 42202       |
      | AU      | VictoriaRegion   | propcode16 | mail16@mail.com | 422           | 42202       |
      | AU      | TheGreatTasmania | propcode17 | mail17@mail.com | 422           | 42202       |
      | CA      | Yukon region     | propcode18 | mail18@mail.com | 422           | 42202       |
      | CA      | TheGreatYukon    | propcode19 | mail19@mail.com | 422           | 42202       |

  Scenario: Creating property with same name as previously deleted one - DP-1380
    Given The following property is created with random address and billing address for user "default1"
      | salesforceId | name    | code         | website                    | email              | isDemo         | timezone      | customerId                           |
      | sl_id_1      | p2_name | p2_code      | http://www.snapshot.travel | p1@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is 201
    When Property with code "p2_code" is deleted
    Then Response code is 204
    Given The following property is created with random address and billing address for user "default1"
      | salesforceId | name    | code         | website                    | email              | isDemo         | timezone      | customerId                           |
      | sl_id_1      | p2_name | p2_code      | http://www.snapshot.travel | p1@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is 201
    And Body contains entity with attribute "property_code" value "p2_code"
    And Body contains entity with attribute "name" value "p2_name"

  Scenario Outline: Send POST request with empty body to all properties endpoints
    When The following property set is created for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
      | id                                   | name            | description            | type            |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 | ps1_name        | ps1_description        | brand           |
    When Property with code "p1_code" is added to property set "ps1_name"
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                        |
      | identity/properties/                                                                                       |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09                                                   |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/users/                                            |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/property_sets/c729e3b0-69bf-4c57-91bd-30230d2c1bd0|

    Scenario: Creating duplicate property returns correct error  - DP-1661
      When The following property is created with random address and billing address for user "default1"
        | id                                   | name         | code         | website                    | email                | isDemo         | timezone      | customerId                           |
        | 00011223-50e8-4854-a233-289f00b54a09 | original     | orig_code    | http://www.snapshot.travel | orig@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Response code is "201"
      When The following property is created with random address and billing address for user "default1"
        | id                                   | name         | code         | website                    | email                | isDemo         | timezone      | customerId                           |
        | 00011223-50e8-4854-a233-289f00b54a09 | original     | orig_code    | http://www.snapshot.travel | orig@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Response code is "409"
      And Custom code is 40902

