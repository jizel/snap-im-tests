@Identity
Feature: Property sets users create update delete

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default0 | Default0  | User0    | def0@snapshot.travel | Europe/Prague | cs-CZ   |
      | 6d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | 7d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default2 | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
      | 8d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default3 | Default3  | User3    | def3@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "default0"
    Given Default Snapshot user is created
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
      | ps2_name        | ps2_description        | brand           |

  @Smoke
  Scenario: Adding user to property set
    When User with username "default3" is added to property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Then Response code is "204"

  Scenario: Updating Property Set - User relationship
#    This is now blocked by DP-1612 - if not fixed used the reversed endpoint that works for this test
    Given Check is active attribute is "false" for relation between user "default0" and property set "ps1_name"
    When Relation between user "default0" and property set "ps1_name" is activated
    Then Response code is "204"
    And Check is active attribute is "true" for relation between user "default0" and property set "ps1_name"
    When Relation between user "default0" and property set "ps1_name" is inactivated
    Then Response code is "204"
    And Check is active attribute is "false" for relation between user "default0" and property set "ps1_name"


  @Smoke
  Scenario: Removing user from property set
    Given Relation between user with username "default2" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When User with username "default2" is removed from property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Then Response code is "204"
    And Body is empty
    And User with username "default2" isn't there for property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"


  Scenario: Checking error code for removing user from property set
    When Nonexistent user is removed from property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Then Response code is "204"

  Scenario Outline: Filtering list of users for property set
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName             | firstName         | lastName       | email                            | phone        | timezone          | culture |
      | customer | filter_psu_default_1 | FilterPSUDefault1 | FilterPSUUser1 | filter_psu_user1@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | customer | filter_psu_default_2 | FilterPSUDefault2 | FilterPSUUser2 | filter_psu_user2@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | guest    | filter_psu_default_3 | FilterPSUDefault3 | FilterPSUUser3 | filter_psu_user3@snapshot.travel | +42010111213 | Europe/Bratislava | cs-CZ   |
      | customer | filter_psu_default_4 | FilterPSUDefault4 | FilterPSUUser4 | filter_psu_user4@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | partner  | filter_psu_default_5 | FilterPSUDefault5 | FilterPSUUser5 | filter_psu_user5@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | customer | filter_psu_default_6 | FilterPSUDefault6 | FilterPSUUser6 | filter_psu_user6@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
    Given Relation between user with username "filter_psu_default_1" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    Given Relation between user with username "filter_psu_default_2" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    Given Relation between user with username "filter_psu_default_3" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    Given Relation between user with username "filter_psu_default_4" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    Given Relation between user with username "filter_psu_default_5" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    Given Relation between user with username "filter_psu_default_6" and property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists
    When List of users for property set with name "ps1_name" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> users returned
    And There are property set users with following usernames returned in order: <expected_usernames>
    Examples:
      | limit | cursor | returned | filter                           | sort      | sort_desc | expected_usernames                                                                                           |
      | 5     | 0      | 5        | user_name=='filter_psu_default*' | user_name |           | filter_psu_default_1, filter_psu_default_2, filter_psu_default_3, filter_psu_default_4, filter_psu_default_5 |
      | 5     | 0      | 5        | user_name=='filter_psu_default*' |           | user_name | filter_psu_default_6, filter_psu_default_5, filter_psu_default_4, filter_psu_default_3, filter_psu_default_2 |
      | 5     | 2      | 4        | user_name=='filter_psu_default*' | user_name |           | filter_psu_default_3, filter_psu_default_4, filter_psu_default_5, filter_psu_default_6                       |
      | 5     | 2      | 4        | user_name=='filter_psu_default*' |           | user_name | filter_psu_default_4, filter_psu_default_3, filter_psu_default_2, filter_psu_default_1                       |
      | /null | /null  | 1        | user_name==filter_psu_default_6  | /null     | /null     | filter_psu_default_6                                                                                         |