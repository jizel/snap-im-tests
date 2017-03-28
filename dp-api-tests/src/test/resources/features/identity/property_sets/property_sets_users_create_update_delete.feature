@Identity
Feature: Property sets users create update delete

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | Id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | Id                                   | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default0 | Default0  | User0    | def0@snapshot.travel | Europe/Prague | cs-CZ   |
      | 6d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | 7d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default2 | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
      | 8d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default3 | Default3  | User3    | def3@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "default0"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
      | ps2_name        | ps2_description        | brand           |



  Scenario: Updating Property Set-User relationship
    When User "default0" is added to property set with name "ps1_name"
    Given Check is active attribute is "true" for relation between user "default0" and property set "ps1_name"
    When Relation between user "default0" and property set "ps1_name" is inactivated
    Then Response code is "204"
    And Check is active attribute is "false" for relation between user "default0" and property set "ps1_name"
    When Relation between user "default0" and property set "ps1_name" is activated
    Then Response code is "204"
    And Check is active attribute is "true" for relation between user "default0" and property set "ps1_name"

  Scenario: Removing invalid user from property set
    When Nonexistent user is removed from property set with name "ps1_name"
    Then Response code is "404"

  Scenario Outline: Filtering list of users for property set
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName             | firstName         | lastName       | email                            | phone        | timezone          | culture |
      | customer | filter_psu_default_1 | FilterPSUDefault1 | FilterPSUUser1 | filter_psu_user1@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | customer | filter_psu_default_2 | FilterPSUDefault2 | FilterPSUUser2 | filter_psu_user2@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | guest    | filter_psu_default_3 | FilterPSUDefault3 | FilterPSUUser3 | filter_psu_user3@snapshot.travel | +42010111213 | Europe/Bratislava | cs-CZ   |
      | customer | filter_psu_default_4 | FilterPSUDefault4 | FilterPSUUser4 | filter_psu_user4@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | partner  | filter_psu_default_5 | FilterPSUDefault5 | FilterPSUUser5 | filter_psu_user5@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | customer | filter_psu_default_6 | FilterPSUDefault6 | FilterPSUUser6 | filter_psu_user6@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
    Given Relation between user "filter_psu_default_1" and property set with name "ps1_name" exists
    Given Relation between user "filter_psu_default_2" and property set with name "ps1_name" exists
    Given Relation between user "filter_psu_default_3" and property set with name "ps1_name" exists
    Given Relation between user "filter_psu_default_4" and property set with name "ps1_name" exists
    Given Relation between user "filter_psu_default_5" and property set with name "ps1_name" exists
    Given Relation between user "filter_psu_default_6" and property set with name "ps1_name" exists
    When List of users for property set with name "ps1_name" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> users returned
    And There are property set users with following usernames returned in order: <expected_usernames>
    Examples:
      | limit | cursor | returned | filter           | sort      | sort_desc   | expected_usernames                                                                                            |
      | 5     | 0      | 5        | is_active==false | is_active |             | filter_psu_default_1, filter_psu_default_2, filter_psu_default_3, filter_psu_default_4, filter_psu_default_5  |
      | 5     | 0      | 5        | is_active==false |           | is_active   | filter_psu_default_6, filter_psu_default_5, filter_psu_default_4, filter_psu_default_3, filter_psu_default_2  |
      | 5     | 2      | 5        | is_active==false | is_active |             | filter_psu_default_3, filter_psu_default_4, filter_psu_default_5, filter_psu_default_6, default0              |
      | 5     | 2      | 5        | is_active==false |           | user_id     | filter_psu_default_5, filter_psu_default_4, filter_psu_default_3, filter_psu_default_2, filter_psu_default_1  |
      | 1     | 0      | 1        | is_active==false |           |             | filter_psu_default_6                                                                                          |
      | 2     | 0      | 1        | user_id=='*aac'  | user_id   |             | default0                                                                                                      |
      | 5     | 0      | 1        | user_id=='5d829*'| user_id   |             | default0                                                                                                      |

  Scenario Outline: Filtering list of users for property set - negative scenarios
    Given Relation between user "default0" and property set with name "ps1_name" exists
    Given Relation between user "default1" and property set with name "ps1_name" exists
    Given Relation between user "default2" and property set with name "ps1_name" exists
    Given Relation between user "default3" and property set with name "ps1_name" exists
    When List of users for property set with name "ps1_name" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "400"
    Examples:
      | limit | cursor | filter              | sort      | sort_desc   |
      | 5     | 0      | property_id==123    |           | is_active   |
      | 5     | 2      | property_set_id==xx | is_active |             |
      | 5     | 2      | name==J*            | user_id   |             |
      | 0     | 0      | user_id=='0*'       | /null     |             |
      | 5     | c      | /null               | /null     |             |