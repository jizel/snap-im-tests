@Identity
@Properties
Feature: Properties property sets get

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | id                                   | type     | username | firstName | lastName | email                | timezone      | languageCode |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 0b202111-cdaf-439a-8bef-3140f56c657e | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |



  Scenario Outline: Filtering list of properties property sets
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "default1"
      | name            | description            | type            |
      | list_ps1_name   | list_ps1_description   | brand           |
      | list_ps2_name   | list_ps2_description   | brand           |
      | list_ps3_name   | list_ps3_description   | brand           |
      | list_ps4_name   | list_ps4_description   | brand           |
      | list_ps5_name   | list_ps5_description   | brand           |
      | list_ps6_name   | list_ps6_description   | brand           |
      | list_ps7_name   | list_ps7_description   | brand           |
      | list_ps8_name   | list_ps8_description   | brand           |
      | list_ps9_name   | list_ps9_description   | brand           |
      | list_ps10_name  | list_ps10_description  | brand           |
      | list_ps11_name  | list_ps11_description  | brand           |
      | list_ps12_name  | list_ps12_description  | brand           |
    Given Relation between property with code "p1_code" and property set with name "list_ps1_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps2_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps3_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps4_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps5_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps6_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps7_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps8_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps9_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps10_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps11_name" exists
    Given Relation between property with code "p1_code" and property set with name "list_ps12_name" exists

    When List of property sets is got for property with id "0b202111-cdaf-439a-8bef-3140f56c657e" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> property property sets returned
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total |  filter          |   sort          |  sort_desc      |
      | 5     | 0      | 5        | 12    | is_active==true  | property_set_id |                 |
      | 5     | 0      | 5        | 12    | is_active==true  |                 | property_set_id |
      | 5     | 2      | 5        | 12    | is_active==true  | property_set_id |                 |
      | 5     | 2      | 0        | 0     | is_active==false |                 | property_set_id |
