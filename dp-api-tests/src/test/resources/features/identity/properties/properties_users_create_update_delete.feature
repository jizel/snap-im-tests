Feature: properties_users_create_update_delete

  Background:
    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone  |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | UTC+01:00 |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | UTC+01:00 |

    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone  | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | UTC+01:00 | cz      |
      | customer | default2 | Default2  | User2    | def2@snapshot.travel | UTC+01:00 | cz      |
      | customer | default3 | Default3  | User3    | def2@snapshot.travel | UTC+01:00 | cz      |

    Given All users are removed for properties with codes: p1_code

    Given Relation between user with username "default1" and property with code "p1_code" exists
    Given Relation between user with username "default2" and property with code "p1_code" exists


  Scenario: Adding user to property

    When User with username "default3" is added to property with code "p2_code"
    Then Response code is "201"
    And Etag header is present

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

