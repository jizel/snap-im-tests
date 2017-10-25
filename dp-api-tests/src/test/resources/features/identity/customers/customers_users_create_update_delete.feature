@Identity
Feature: Customers users create update delete

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone      |
      | 40ebf861-7549-46f1-a99f-249716c83b33 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
      | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    Given The following users exist for customer "40ebf861-7549-46f1-a99f-249716c83b33" as primary "true"
      | type     | username  | firstName | lastName | email                | timezone      | languageCode |
      | snapshot | snapUser1 | Snapshot1 | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | snapshot | snapUser2 | Snapshot2 | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
      | snapshot | snapUser3 | Snapshot3 | User3    | def3@snapshot.travel | Europe/Prague | cs-CZ   |
    Given All users are removed for customers with ids: 40ebf861-7549-46f1-a99f-249716c83b33, 58dd58d4-a56e-4cf5-a3a6-068fe37fef40
    Given Relation between user "snapUser1" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "true"
    Given Relation between user "snapUser2" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "false"

  Scenario Outline: Filtering list of users for customer
    Given The following users exist for customer "40ebf861-7549-46f1-a99f-249716c83b33" as primary "true"
      | type     | username            | firstName        | lastName      | email                           | phone        | timezone      | languageCode |
      | customer | filter_cu_default_1 | FilterCUDefault1 | FilterCUUser1 | filter_cu_user1@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | customer | filter_cu_default_2 | FilterCUDefault2 | FilterCUUser2 | filter_cu_user2@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | guest    | filter_cu_default_3 | FilterCUDefault3 | FilterCUUser3 | filter_cu_user3@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | customer | filter_cu_default_4 | FilterCUDefault4 | FilterCUUser4 | filter_cu_user4@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | partner  | filter_cu_default_5 | FilterCUDefault5 | FilterCUUser5 | filter_cu_user5@snapshot.travel | +42010111213 | Europe/Berlin | cs-CZ   |
      | customer | filter_cu_default_6 | FilterCUDefault6 | FilterCUUser6 | filter_cu_user6@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |
      | customer | other_cu_default_7  | FilterCUDefault7 | FilterCUUser7 | filter_cu_user7@snapshot.travel | +42010111217 | Europe/Prague | cs-CZ   |
      | customer | other_cu_default_8  | FilterCUDefault8 | FilterCUUser8 | filter_cu_user8@snapshot.travel | +42010111213 | Europe/Prague | sk-SK   |
      | partner  | other_cu_default_9  | FilterCUDefault9 | FilterCUUser9 | filter_cu_user9@snapshot.travel | +42010111213 | Europe/Prague | sk-SK   |

    Given Relation between user "filter_cu_default_1" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "true"
    Given Relation between user "filter_cu_default_2" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "false"
    Given Relation between user "filter_cu_default_3" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "false"
    Given Relation between user "filter_cu_default_4" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "false"
    Given Relation between user "filter_cu_default_5" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "false"
    Given Relation between user "filter_cu_default_6" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "false"
    Given Relation between user "other_cu_default_7" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "false"
    Given Relation between user "other_cu_default_8" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "true"
    Given Relation between user "other_cu_default_9" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "false"

    When List of users for customer with id "40ebf861-7549-46f1-a99f-249716c83b33" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customerUsers returned
    And There are customer users with following usernames returned in order: <expected_usernames>
    And Total count is "<total>"

    Examples:

      | limit | cursor | returned | total | filter                         | sort      | sort_desc | expected_usernames                                                                                                                              |
      | /null | 0      | 8        | 8     | is_primary=='false'            | /null     |           | filter_cu_default_2, filter_cu_default_3, filter_cu_default_4, filter_cu_default_5, filter_cu_default_6, other_cu_default_7, other_cu_default_9 |
      | 5     | 0      | 5        | 8     | is_primary=='false'            | /null     |           | filter_cu_default_2, filter_cu_default_3, filter_cu_default_4, filter_cu_default_5, filter_cu_default_6, other_cu_default_7, other_cu_default_9 |
      | 5     | 0      | 3        | 3     | is_primary=='true'             |           | /null     | filter_cu_default_2, other_cu_default_8                                                                                                         |
      | 5     | 2      | 5        | 8     | is_primary=='false'            | /null     |           | filter_cu_default_3, filter_cu_default_4, filter_cu_default_5, filter_cu_default_6, other_cu_default_7, other_cu_default_9                      |
      | /null | /null  | 8        | 8     | is_primary=='false'            | /null     | /null     | filter_cu_default_1, other_cu_default_8                                                                                                         |
