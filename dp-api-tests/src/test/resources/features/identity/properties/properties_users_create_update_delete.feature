@Identity
Feature: Properties users create update delete

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | 6d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default2 | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
      | 7d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default3 | Default3  | User3    | def3@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following properties exist with random address and billing address for user "6d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Default Snapshot user is created

  @Smoke
  Scenario: Adding user to property
    When User with username "default3" is added to property with code "p2_code"
    Then Response code is "201"

  Scenario: Updating User - Property relationship
    Given Check is active attribute is "false" for relation between user "default2" and property with code "p2_code"
    When Set is active to "true" for relation between user "default2" and property with code "p2_code"
    Then Response code is "204"
    And Check is active attribute is "true" for relation between user "default2" and property with code "p2_code"
    When Set is active to "false" for relation between user "default2" and property with code "p2_code"
    Then Response code is "204"
    And Check is active attribute is "false" for relation between user "default2" and property with code "p2_code"

  #validate just one primary user, notexistent user, already present user
  #validate different type of users

  @Smoke
#    Fails because of DP-1630
  Scenario: Removing user from property
    When User with username "default2" is removed from property with code "p1_code"
    Then Response code is "204"
    And Body is empty
    And User with username "default2" isn't there for property with code "p1_code"


  Scenario: Checking error code for removing user from property
    When Nonexistent user is removed from property with code "p1_code"
    Then Response code is "204"

  Scenario Outline: Filtering list of users for property
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName            | firstName        | lastName      | email                           | phone        | timezone      | culture |
      | customer | filter_pu_default_1 | FilterPUDefault1 | FilterPUUser1 | filter_pu_user1@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | customer | filter_pu_default_2 | FilterPUDefault2 | FilterPUUser2 | filter_pu_user2@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | guest    | filter_pu_default_3 | FilterPUDefault3 | FilterPUUser3 | filter_pu_user3@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | customer | filter_pu_default_4 | FilterPUDefault4 | FilterPUUser4 | filter_pu_user4@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | partner  | filter_pu_default_5 | FilterPUDefault5 | FilterPUUser5 | filter_pu_user5@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | customer | filter_pu_default_6 | FilterPUDefault6 | FilterPUUser6 | filter_pu_user6@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | customer | other_cu_default_7  | FilterPUDefault7 | FilterPUUser7 | filter_pu_user7@snapshot.travel | +42010111217 | Europe/Prague | cs-CZ   |
      | customer | other_cu_default_8  | FilterPUDefault8 | FilterPUUser8 | filter_pu_user8@snapshot.travel | +42010111213 | Europe/Prague | sk-SK   |
      | snapshot | other_cu_default_9  | FilterPUDefault9 | FilterPUUser9 | filter_pu_user9@snapshot.travel | +42010111213 | Europe/Prague | sk-SK   |

    Given Relation between user with username "filter_pu_default_1" and property with code "p1_code" exists
    Given Relation between user with username "filter_pu_default_2" and property with code "p1_code" exists
    Given Relation between user with username "filter_pu_default_3" and property with code "p1_code" exists
    Given Relation between user with username "filter_pu_default_4" and property with code "p1_code" exists
    Given Relation between user with username "filter_pu_default_5" and property with code "p1_code" exists
    Given Relation between user with username "filter_pu_default_6" and property with code "p1_code" exists

    When List of users for property with code "p1_code" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> users returned
    And There are property users with following usernames returned in order: <expected_usernames>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                          | sort      | sort_desc | expected_usernames                                                                                      |
      | 5     | 0      | 5        | 6     | user_name=='filter_pu_default*' | user_name |           | filter_pu_default_1, filter_pu_default_2, filter_pu_default_3, filter_pu_default_4, filter_pu_default_5 |
      | 5     | 0      | 5        | 6     | user_name=='filter_pu_default*' |           | user_name | filter_pu_default_6, filter_pu_default_5, filter_pu_default_4, filter_pu_default_3, filter_pu_default_2 |
      | 5     | 2      | 4        | 6     | user_name=='filter_pu_default*' | user_name |           | filter_pu_default_3, filter_pu_default_4, filter_pu_default_5, filter_pu_default_6                      |
      | 5     | 2      | 4        | 6     | user_name=='filter_pu_default*' |           | user_name | filter_pu_default_4, filter_pu_default_3, filter_pu_default_2, filter_pu_default_1                      |
      | /null | /null  | 1        | 1     | user_name==filter_pu_default_6  | /null     | /null     | filter_pu_default_6                                                                                     |