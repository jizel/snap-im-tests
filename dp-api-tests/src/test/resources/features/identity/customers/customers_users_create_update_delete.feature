Feature: customers_users_create_update_delete

  Background:
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel |


    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone  | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | UTC+01:00 | cz      |
      | customer | default2 | Default2  | User2    | def2@snapshot.travel | UTC+01:00 | cz      |
      | customer | default3 | Default3  | User3    | def2@snapshot.travel | UTC+01:00 | cz      |

    Given All users are removed for customers with codes: c1t, c2t

    Given Relation between user with username "default1" and customer with code "c1t" exists with isPrimary "true"
    Given Relation between user with username "default2" and customer with code "c1t" exists with isPrimary "false"


  Scenario: Adding user to customer with isPrimary set

    When User with username "default3" is added to customer with code "c2t" with isPrimary "true"
    Then Response code is "204"

  #validate just one primary user, notexistent user, already present user
  #validate different type of users


  Scenario: Removing user from customer
#failing because of not working filtering for customer users
    When User with username "default2" is removed from customer with code "c1t"
    Then Response code is "204"
    And Body is empty
    And User with username "default2" isn't there for customer with code "c1t"


  Scenario: Checking error code for removing user from customer
    When Nonexistent user is removed from customer with code "c1t"
    Then Response code is "204"


  Scenario Outline: Filtering list of users for customer
    Given The following users exist
      | userType | userName            | firstName        | lastName      | email                           | phone        | timezone  | culture |
      | customer | filter_cu_default_1 | FilterCUDefault1 | FilterCUUser1 | filter_cu_user1@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | filter_cu_default_2 | FilterCUDefault2 | FilterCUUser2 | filter_cu_user2@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | guest    | filter_cu_default_3 | FilterCUDefault3 | FilterCUUser3 | filter_cu_user3@snapshot.travel | +42010111213 | UTC+02:00 | cz      |
      | customer | filter_cu_default_4 | FilterCUDefault4 | FilterCUUser4 | filter_cu_user4@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | partner  | filter_cu_default_5 | FilterCUDefault5 | FilterCUUser5 | filter_cu_user5@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | filter_cu_default_6 | FilterCUDefault6 | FilterCUUser6 | filter_cu_user6@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | other_cu_default_7  | FilterCUDefault7 | FilterCUUser7 | filter_cu_user7@snapshot.travel | +42010111217 | UTC+01:00 | cz      |
      | customer | other_cu_default_8  | FilterCUDefault8 | FilterCUUser8 | filter_cu_user8@snapshot.travel | +42010111213 | UTC+01:00 | sk      |
      | partner  | other_cu_default_9  | FilterCUDefault9 | FilterCUUser9 | filter_cu_user9@snapshot.travel | +42010111213 | UTC+01:00 | sk      |

    Given Relation between user with username "filter_cu_default_1" and customer with code "c1t" exists with isPrimary "true"
    Given Relation between user with username "filter_cu_default_2" and customer with code "c1t" exists with isPrimary "false"
    Given Relation between user with username "filter_cu_default_3" and customer with code "c1t" exists with isPrimary "false"
    Given Relation between user with username "filter_cu_default_4" and customer with code "c1t" exists with isPrimary "false"
    Given Relation between user with username "filter_cu_default_5" and customer with code "c1t" exists with isPrimary "false"
    Given Relation between user with username "filter_cu_default_6" and customer with code "c1t" exists with isPrimary "false"
    Given Relation between user with username "other_cu_default_7" and customer with code "c1t" exists with isPrimary "false"
    Given Relation between user with username "other_cu_default_8" and customer with code "c1t" exists with isPrimary "true"
    Given Relation between user with username "other_cu_default_9" and customer with code "c1t" exists with isPrimary "false"

    When List of users for customer with code "c1t" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customerUsers returned
    And There are customer users with following usernames returned in order: <expected_usernames>

    Examples:
      | limit | cursor | returned | filter                                       | sort      | sort_desc | expected_usernames                                                                                      |
      | 5     | 0      | 5        | user_name=='filter_cu_default*'              | user_name |           | filter_cu_default_1, filter_cu_default_2, filter_cu_default_3, filter_cu_default_4, filter_cu_default_5 |
      | 5     | 0      | 5        | user_name=='filter_cu_default*'              |           | user_name | filter_cu_default_6, filter_cu_default_5, filter_cu_default_4, filter_cu_default_3, filter_cu_default_2 |
      | 5     | 2      | 4        | user_name=='filter_cu_default*'              | user_name |           | filter_cu_default_3, filter_cu_default_4, filter_cu_default_5, filter_cu_default_6                      |
      | 5     | 2      | 4        | user_name=='filter_cu_default*'              |           | user_name | filter_cu_default_4, filter_cu_default_3, filter_cu_default_2, filter_cu_default_1                      |
      | /null | /null  | 1        | user_name==filter_cu_default_6               | /null     | /null     | filter_cu_default_6                                                                                     |
      | /null | /null  | 2        | user_name==other_default_* and is_primary==1 | user_name | /null     | filter_cu_default_1, other_cu_default_8                                                                 |
