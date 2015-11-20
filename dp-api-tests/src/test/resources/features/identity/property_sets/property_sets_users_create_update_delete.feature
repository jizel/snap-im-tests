Feature: property_sets_users_create_update_delete

  Background:
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel |

    Given All users are removed for property_sets for customer with code "c1t" with names: ps1_name, ps2_name
    Given All property sets are deleted for customers with codes: c1t, c2t

    Given The following property sets exist for customer with code "c1t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
      | ps2_name        | ps2_description        | branch          |

    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone  | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | UTC+01:00 | cz      |
      | customer | default2 | Default2  | User2    | def2@snapshot.travel | UTC+01:00 | cz      |
      | customer | default3 | Default3  | User3    | def2@snapshot.travel | UTC+01:00 | cz      |

    Given Relation between user with username "default1" and property set with name "ps1_name" for customer with code "c1t" exists


  Scenario: Adding user to property set

    When User with username "default3" is added to property set with name "ps1_name" for customer with code "c1t"
    Then Response code is "204"

  #TODO validate just one primary user, notexistent user, already present user
  #validate different type of users


  Scenario: Removing user from property set
#failing because of not working filtering for customer users
    When User with username "default2" is removed from property set with name "ps1_name" for customer with code "c1t"
    Then Response code is "204"
    And Body is empty
    And User with username "default2" isn't there for property set with name "ps1_name" for customer with code "c1t"


  Scenario: Checking error code for removing user from property set
    When Nonexistent user is removed from property set with name "ps1_name" for customer with code "c1t"
    Then Response code is "204"

  Scenario Outline: Filtering list of users for property set
    Given The following users exist
      | userType | userName             | firstName         | lastName       | email                            | phone        | timezone  | culture |
      | customer | filter_psu_default_1 | FilterPSUDefault1 | FilterPSUUser1 | filter_psu_user1@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | filter_psu_default_2 | FilterPSUDefault2 | FilterPSUUser2 | filter_psu_user2@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | guest    | filter_psu_default_3 | FilterPSUDefault3 | FilterPSUUser3 | filter_psu_user3@snapshot.travel | +42010111213 | UTC+02:00 | cz      |
      | customer | filter_psu_default_4 | FilterPSUDefault4 | FilterPSUUser4 | filter_psu_user4@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | partner  | filter_psu_default_5 | FilterPSUDefault5 | FilterPSUUser5 | filter_psu_user5@snapshot.travel | +42010111213 | UTC+01:00 | cz      |
      | customer | filter_psu_default_6 | FilterPSUDefault6 | FilterPSUUser6 | filter_psu_user6@snapshot.travel | +42010111213 | UTC+01:00 | cz      |

    Given Relation between user with username "filter_psu_default_1" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between user with username "filter_psu_default_2" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between user with username "filter_psu_default_3" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between user with username "filter_psu_default_4" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between user with username "filter_psu_default_5" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between user with username "filter_psu_default_6" and property set with name "ps1_name" for customer with code "c1t" exists

    When List of users for property set with name "ps1_name" for customer with code "c1t" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
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