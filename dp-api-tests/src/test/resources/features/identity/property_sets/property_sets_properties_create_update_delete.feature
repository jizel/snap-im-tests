@Identity
Feature: Property sets properties create update delete

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | id                                   | type     | username | firstName | lastName | email                | timezone      | languageCode |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "default1"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
      | ps2_name        | ps2_description        | brand           |
      | toDelete        | ps3_description        | brand           |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |


  Scenario: Checking error code for removing property from property set
    When Nonexistent property is removed from property set "ps1_name"
    Then Response code is "404"


  Scenario Outline: Filtering list of properties for property set
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | name                  | code                  | website                    | email                    | isDemo         | timezone      | anchorCustomerId                     |
      | salesforceid_1 | filtering_prop_name_1 | filtering_prop_code_1 | http://www.snapshot.travel | filtering_p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_2 | filtering_prop_name_2 | filtering_prop_code_2 | http://www.snapshot.travel | filtering_p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_3 | filtering_prop_name_3 | filtering_prop_code_3 | http://www.snapshot.travel | filtering_p3@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Relation between property with code "filtering_prop_code_1" and property set with name "ps1_name" exists
    Given Relation between property with code "filtering_prop_code_2" and property set with name "ps1_name" exists
    Given Relation between property with code "filtering_prop_code_3" and property set with name "ps1_name" exists
    When List of properties for property set with name "ps1_name" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> property set properties returned
    And There are properties with following names returned in order: <expected_names>
    Examples:
      | limit | cursor | returned | filter           | sort          | sort_desc       | expected_names                                                      |
      | 1     | 0      | 1        | is_active==true  | property_id   |                 | filtering_prop_name_1                                               |
      | 2     | 2      | 1        | is_active==true  |               |                 | filtering_prop_name_2                                               |
      | 3     | 2      | 1        | is_active==true  |               |                 | filtering_prop_name_3                        |
      | 5     | 0      | 3        | is_active==true  |               |                 | filtering_prop_name_1, filtering_prop_name_2, filtering_prop_name_3 |
