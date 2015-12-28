Feature: users_roles_create_update_delete

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |

    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |

    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone      | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | default2 | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | default3 | Default3  | User3    | def3@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following roles exist
      | applicationId | roleName        | roleDescription        |
      | 111           | user_role_rel_1 | optional description 1 |
      | 111           | user_role_rel_2 | optional description 2 |
      | 111           | user_role_rel_3 | optional description 3 |

    Given All users are removed for customers with codes: c1t, c2t

    Given Relation between role with name "user_role_rel_1" for application id "111" and user with username "default1" exists with relationship_type "customer" and entity with code "c1t"
    Given Relation between role with name "user_role_rel_1" for application id "111" and user with username "default1" exists with relationship_type "property" and entity with code "p1_code"


  Scenario Outline: Adding role to user

    When Role with name "<role_name>" for application id "<application_id>" is added to user with username "<username>" with relationship_type "<relationship_type>" and entity with code "<entity_code>"
    Then Response code is "204"

    Examples:
      | role_name       | application_id | username | relationship_type | entity_code |
      | user_role_rel_2 | 111            | default1 | customer          | c1t         |
      | user_role_rel_2 | 111            | default1 | property          | p1_code     |

  #validate just one primary user, notexistent user, already present user
  #validate different type of users


  Scenario Outline: Removing role from user
#failing because of not working filtering for customer users
    When Role with name "<role_name>" for application id "<application_id>" is removed from user with username "<username>" with relationship_type "<relationship_type>" and entity with code "<entity_code>"
    Then Response code is "204"
    And Body is empty
    And Role with name "<role_name>" for application id "<application_id>" is not there for user with username "<username>" with relationship_type "<relationship_type>" and entity with code "<entity_code>"

    Examples:
      | role_name       | application_id | username | relationship_type | entity_code |
      | user_role_rel_1 | 111            | default1 | customer          | c1t         |
      | user_role_rel_1 | 111            | default1 | property          | p1_code     |

  Scenario Outline: Checking error code for removing user from customer
    When Nonexistent role is removed from user with username "<username>" with relationship_type "<relationship_type>" and entity with code "<entity_code>"
    Then Response code is "204"

    Examples:
      | username | relationship_type | entity_code |
      | default1 | customer          | c1t         |
      | default1 | property          | p1_code     |


  Scenario Outline: Filtering list of roles for user for relationship_type and entity
    Given The following users exist
      | userType | userName                   | firstName | lastName | email                      | phone        | timezone      | culture |
      | customer | filter_user_roles_rel_name | Uwe       | Filter   | filter_uwe@snapshot.travel | +42010111213 | Europe/Prague | cs-CZ   |

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

    Given Relation between role with name "filter_user_role_rel_1" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "property" and entity with code "p1_code"
    Given Relation between role with name "filter_user_role_rel_2" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "property" and entity with code "p1_code"
    Given Relation between role with name "filter_user_role_rel_3" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "property" and entity with code "p1_code"
    Given Relation between role with name "filter_user_role_rel_4" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "property" and entity with code "p1_code"
    Given Relation between role with name "filter_user_role_rel_5" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "property" and entity with code "p1_code"
    Given Relation between role with name "filter_user_role_rel_6" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "property" and entity with code "p1_code"
    Given Relation between role with name "other_user_role_rel_2" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "property" and entity with code "p1_code"
    Given Relation between role with name "other_user_role_rel_3" for application id "111" and user with username "filter_user_roles_rel_name" exists with relationship_type "property" and entity with code "p1_code"

    When List of roles for user with username "filter_user_roles_rel_name" with relationship_type "<relationship_type>" and entity with code "<entity_code>" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> user roles returned
    And There are user roles with following role names returned in order: <expected_usernames>
    And Total count is "<total>"

    Examples:
      | relationship_type | entity_code | limit | cursor | returned |total | filter                             | sort      | sort_desc | expected_usernames                                                                                                          |
      | customer          | c1t         | 5     | 0      | 5        |6     | role_name=='filter_user_role_rel*' | role_name |           | filter_user_role_rel_1, filter_user_role_rel_2, filter_user_role_rel_3, filter_user_role_rel_4, filter_user_role_rel_5 |
      | customer          | c1t         | 5     | 0      | 5        |6     | role_name=='filter_user_role_rel*' |           | role_name | filter_user_role_rel_6, filter_user_role_rel_5, filter_user_role_rel_4, filter_user_role_rel_3, filter_user_role_rel_2 |
      | customer          | c1t         | 5     | 2      | 4        |6     | role_name=='filter_user_role_rel*' | role_name |           | filter_user_role_rel_3, filter_user_role_rel_4, filter_user_role_rel_5, filter_user_role_rel_6                          |
      | customer          | c1t         | 5     | 2      | 4        |6     | role_name=='filter_user_role_rel*' |           | role_name | filter_user_role_rel_4, filter_user_role_rel_3, filter_user_role_rel_2, filter_user_role_rel_1                          |
      | customer          | c1t         | /null | /null  | 1        |1     | role_name==filter_user_role_rel_6  | /null     | /null     | filter_user_role_rel_6                                                                                                     |
    #TODO error codes - if bad relationship_type is used, if wrong id is used,