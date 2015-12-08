Feature: properties_users_create_update_delete

  Background:
    Given Database is cleaned
    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone  |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | UTC+01:00 |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | UTC+01:00 |

    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone  | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | UTC+01:00 | cz      |
      | customer | default2 | Default2  | User2    | def2@snapshot.travel | UTC+01:00 | cz      |
      | customer | default3 | Default3  | User3    | def2@snapshot.travel | UTC+01:00 | cz      |

    Given All users are removed for properties with codes: p1_code, p2_code

    Given Relation between user with username "default1" and property with code "p1_code" exists
    Given Relation between user with username "default2" and property with code "p1_code" exists


  Scenario: Adding user to property

    When User with username "default3" is added to property with code "p2_code"
    Then Response code is "204"

  #validate just one primary user, notexistent user, already present user
  #validate different type of users


  Scenario: Removing user from property
#failing because of not working filtering for property users
    When User with username "default2" is removed from property with code "p1_code"
    Then Response code is "204"
    And Body is empty
    And User with username "default2" isn't there for property with code "p1_code"


  Scenario: Checking error code for removing user from property
    When Nonexistent user is removed from property with code "p1_code"
    Then Response code is "204"

  Scenario Outline: Filtering list of users for property
    Given The following users exist
      | userType | userName            | firstName        | lastName      | email                           | phone        | timezone  | culture |
      | customer | filter_pu_default_1 | FilterPUDefault1 | FilterPUUser1 | filter_pu_user1@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | filter_pu_default_2 | FilterPUDefault2 | FilterPUUser2 | filter_pu_user2@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | guest    | filter_pu_default_3 | FilterPUDefault3 | FilterPUUser3 | filter_pu_user3@snapshot.travel | +42010111213 | UTC+02:00 | cz      |
      | customer | filter_pu_default_4 | FilterPUDefault4 | FilterPUUser4 | filter_pu_user4@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | partner  | filter_pu_default_5 | FilterPUDefault5 | FilterPUUser5 | filter_pu_user5@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | filter_pu_default_6 | FilterPUDefault6 | FilterPUUser6 | filter_pu_user6@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | other_cu_default_7  | FilterPUDefault7 | FilterPUUser7 | filter_pu_user7@snapshot.travel | +42010111217 | UTC+01:00 | cz      |
      | customer | other_cu_default_8  | FilterPUDefault8 | FilterPUUser8 | filter_pu_user8@snapshot.travel | +42010111213 | UTC+01:00 | sk      |
      | snapshot | other_cu_default_9  | FilterPUDefault9 | FilterPUUser9 | filter_pu_user9@snapshot.travel | +42010111213 | UTC+01:00 | sk      |

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

    Examples:
      | limit | cursor | returned | filter                                       | sort      | sort_desc | expected_usernames                                                                                      |
      | 5     | 0      | 5        | user_name=='filter_pu_default*'              | user_name |           | filter_pu_default_1, filter_pu_default_2, filter_pu_default_3, filter_pu_default_4, filter_pu_default_5 |
      | 5     | 0      | 5        | user_name=='filter_pu_default*'              |           | user_name | filter_pu_default_6, filter_pu_default_5, filter_pu_default_4, filter_pu_default_3, filter_pu_default_2 |
      | 5     | 2      | 4        | user_name=='filter_pu_default*'              | user_name |           | filter_pu_default_3, filter_pu_default_4, filter_pu_default_5, filter_pu_default_6                      |
      | 5     | 2      | 4        | user_name=='filter_pu_default*'              |           | user_name | filter_pu_default_4, filter_pu_default_3, filter_pu_default_2, filter_pu_default_1                      |
      | /null | /null  | 1        | user_name==filter_pu_default_6               | /null     | /null     | filter_pu_default_6                                                                                     |

