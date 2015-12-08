Feature: users_roles_create_update_delete

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel |


    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone  | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | UTC+01:00 | cz      |
      | customer | default2 | Default2  | User2    | def2@snapshot.travel | UTC+01:00 | cz      |
      | customer | default3 | Default3  | User3    | def2@snapshot.travel | UTC+01:00 | cz      |

    Given The following roles exist
      | applicationId | roleName        | roleDescription        |
      | 111           | user_role_rel_1 | optional description 1 |
      | 111           | user_role_rel_2 | optional description 2 |
      | 111           | user_role_rel_3 | optional description 3 |

    Given All users are removed for customers with codes: c1t, c2t

    Given Relation between role with name "user_role_rel_1" for application id "111" and user with username "default1" exists with relationship_type "customer" and entity with code "c1t"


  Scenario: Adding role to user

    When Role with name "user_role_rel_2" for application id "111" is added to user with username "default1" with relationship_type "customer" and entity with code "c1t"
    Then Response code is "204"

  #validate just one primary user, notexistent user, already present user
  #validate different type of users


  Scenario: Removing role from user
#failing because of not working filtering for customer users
    When Role with name "user_role_rel_1" for application id "111" is removed from user with username "default2" with relationship_type "customer" and entity with code "c1t"
    Then Response code is "204"
    And Body is empty
    And Role with name "user_role_rel_1" for application id "111" is not there for user with username "default2" with relationship_type "customer" and entity with code "c1t"


  Scenario: Checking error code for removing user from customer
    When Nonexistent role is removed from user with username "default2" with relationship_type "customer" and entity with code "c1t"
    Then Response code is "204"


  Scenario Outline: Filtering list of roles for user for relationship_type and entity
    Given The following users exist
      | userType | userName                   | firstName | lastName | email                      | phone        | timezone  | culture |
      | customer | filter_user_roles_rel_name | Uwe       | Filter   | filter_uwe@snapshot.travel | +42010111213 | UTC+01:00 | cz      |

    Given The following roles exist
      | applicationId | roleName               | roleDescription        |
      | 111           | filter_user_role_rel_1 | optional description 1 |
      | 111           | filter_user_role_rel_2 | optional description 2 |
      | 111           | filter_user_role_rel_3 | optional description 3 |
      | 111           | filter_user_role_rel_4 | optional description 4 |
      | 111           | filter_user_role_rel_5 | optional description 5 |
      | 111           | filter_user_role_rel_6 | optional description 6 |
      | 111           | other_user_role_rel_2  | optional description 2 |
      | 111           | other_user_role_rel_3  | optional description 3 |


    Given Relation between role with name "filter_user_role_rel_1" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "customer" and entity with code "c1t"
    Given Relation between role with name "filter_user_role_rel_2" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "customer" and entity with code "c1t"
    Given Relation between role with name "filter_user_role_rel_3" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "customer" and entity with code "c1t"
    Given Relation between role with name "filter_user_role_rel_4" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "customer" and entity with code "c1t"
    Given Relation between role with name "filter_user_role_rel_5" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "customer" and entity with code "c1t"
    Given Relation between role with name "filter_user_role_rel_6" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "customer" and entity with code "c1t"
    Given Relation between role with name "other_user_role_rel_2" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "customer" and entity with code "c1t"
    Given Relation between role with name "other_user_role_rel_3" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "customer" and entity with code "c1t"


    When List of roles for user with username "filter_user_roles_rel_name" with relationship_type "customer" and entity with code "c1t" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> user roles returned
    And There are user roles with following role names returned in order: <expected_usernames>

    Examples:
      | limit | cursor | returned | filter                             | sort      | sort_desc | expected_usernames                                                                                                          |
      | 5     | 0      | 5        | role_name=='filter_user_role_rel*' | role_name |           | filter_user_roles_rel_1, filter_user_roles_rel_2, filter_user_roles_rel_3, filter_user_roles_rel_4, filter_user_roles_rel_5 |
      | 5     | 0      | 5        | role_name=='filter_user_role_rel*' |           | role_name | filter_user_roles_rel_6, filter_user_roles_rel_5, filter_user_roles_rel_4, filter_user_roles_rel_3, filter_user_roles_rel_2 |
      | 5     | 2      | 4        | role_name=='filter_user_role_rel*' | role_name |           | filter_user_roles_rel_3, filter_user_roles_rel_4, filter_user_roles_rel_5, filter_user_roles_rel_6                          |
      | 5     | 2      | 4        | role_name=='filter_user_role_rel*' |           | role_name | filter_user_roles_rel_4, filter_user_roles_rel_3, filter_user_roles_rel_2, filter_user_roles_rel_1                          |
      | /null | /null  | 1        | role_name==filter_user_role_rel_6  | /null     | /null     | filter_user_roles_rel_6                                                                                                     |

    #TODO error codes - if bad relationship_type is used, if wrong id is used,