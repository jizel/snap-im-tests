@Identity
Feature: Commercial subscription get

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | id                                   | type     | username | firstName | lastName | email                | timezone      | languageCode |
      | 2a2f76f3-3537-4d5a-971a-7a36f61095bd | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "2a2f76f3-3537-4d5a-971a-7a36f61095bd"
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 742529dd-481f-430d-b6b6-686fbb687cab | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following commercial subscriptions exist
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728eb | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab | 03000000-0000-4444-8888-000000000000 |


  Scenario: Checking error code for nonexistent commercial subscription
    When Nonexistent commercial subscription id is got
    Then Response code is "404"
    And Custom code is "40402"
