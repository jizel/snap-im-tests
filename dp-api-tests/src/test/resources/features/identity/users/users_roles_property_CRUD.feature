@Identity
Feature: Users property roles CRUD

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | Id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1234fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 2234fd9a-a05d-42d8-8e84-42e904ace123 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1234fd9a-a05d-42d8-8e84-42e904ace123" as primary "true"
      | Id                                   | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 33e9ddbe-c8f6-44e7-a536-27a0be3e90c3 | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | f5e630d0-dfe2-4466-b7ea-491265a329d2 | snapshot | default2 | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |


    Given The following properties exist with random address and billing address for user "33e9ddbe-c8f6-44e7-a536-27a0be3e90c3"
      | Id                                   | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 842529dd-481f-430d-b6b6-686fbb687cab | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1234fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Relation between user "default1" and property with code "p1_code" exists
    Given Switch for user property role tests
    Given The following roles exist
      | roleId                               | roleName    | description            | Id                                   |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | user_role_1 | optional description 1 | 11111111-0000-4000-a000-111111111111 |
      | b318fd9a-a05d-42d8-8e84-42e904ace123 | user_role_2 | optional description 2 | 11111111-0000-4000-a000-111111111111 |


  @Smoke
  Scenario Outline: Assigning role to user property
    When Role with id "<role_id>" for user name "<user_name>" and property code "<property_code>" is added
    Then Response code is "201"
    Examples:
      | role_id                              | user_name | property_code |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | default1  | p1_code       |
      | b318fd9a-a05d-42d8-8e84-42e904ace123 | default1  | p1_code       |


  Scenario Outline: Assigning not existing role
    When Role with id "<role_id>" for user name "<user_name>" and property code "<property_code>" is added
    Then Response code is "422"
    And Custom code is 42202
    Examples:
      | role_id                              | user_name | property_code |
      | 1111fd9a-a05d-42d8-8e84-42e904ace123 | default1  | p1_code       |


  Scenario Outline: Assigning customer type of role to user property
    Given Switch for user customer role tests
    Given The following roles exist
      | roleId                               | roleName        | description            | Id                                   |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | user_role_wrong | optional description 1 | 11111111-0000-4000-a000-111111111111 |
    When Role with id "<role_id>" for user name "<user_name>" and property code "<property_code>" is added
    Then Response code is "422"
    And Custom code is 42202
    Examples:
      | role_id                              | user_name | property_code |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | default1  | p1_code       |


  Scenario Outline: Assigning property set type of role to user property
    Given Switch for user property set role tests
    Given The following roles exist
      | roleId                               | roleName        | description            | Id                                   |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | user_role_wrong | optional description 1 | 11111111-0000-4000-a000-111111111111 |
    When Role with id "<role_id>" for user name "<user_name>" and property code "<property_code>" is added
    Then Response code is "422"
    And Custom code is 42202
    Examples:
      | role_id                              | user_name | property_code |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | default1  | p1_code       |


  #todo issue dp-1294
  Scenario Outline: Assigning role to not existing property
    When Role with id "<role_id>" for user name "<user_name>" and property id "<property_id>" is added
    Then Response code is "404"
    And Custom code is 40402
    Examples:
      | role_id                              | user_name | property_id                          |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | default1  | a111fd9a-a11d-11d8-8e84-42e111ace123 |


    #todo issue dp-1294
  Scenario Outline: Assigning role to not existing user
    When Role with id "<role_id>" for not existing user id and property code "<property_code>" is added
    Then Response code is "404"
    And Custom code is 40402
    Examples:
      | role_id                              | property_code |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | p1_code       |


  Scenario Outline: Removing role from user property
    When Role with id "<role_id>" for user name "<user_name>" and property code "<property_code>" is added
    When Role with id "<role_id>" for user name "<user_name>" and property code "<property_code>" is deleted
    Then Response code is "204"
    When Role with id "<role_id>" for user name "<user_name>" and property code "<property_code>" does not exist
    Examples:
      | role_id                              | property_code | user_name |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | p1_code       | default1  |


  Scenario Outline: Checking error code for removing not existing role from user customer
    When Role with id "<role_id>" for user name "<user_name>" and property code "<property_code>" is deleted
    Then Response code is "204"
    Examples:
      | role_id                              | property_code | user_name |
      | 1111fd9a-a11d-11d8-8e84-11e904ace123 | p1_code       | default1  |


  Scenario Outline: Filtering list of roles for user customer relationship
    Given Switch for user property role tests
    Given The following roles exist
      | roleName           | description            | Id                                   |
      | user_filter_role_1 | optional description 1 | 11111111-0000-4000-a000-111111111111 |
      | user_filter_role_2 | optional description 2 | 11111111-0000-4000-a000-111111111111 |
      | user_filter_role_3 | optional description 3 | 11111111-0000-4000-a000-111111111111 |
      | user_filter_role_4 | optional description 4 | 11111111-0000-4000-a000-111111111111 |
      | user_filter_role_5 | optional description 5 | 11111111-0000-4000-a000-111111111111 |
      | user_filter_role_6 | optional description 6 | 11111111-0000-4000-a000-111111111111 |
    Given Role with name "user_filter_role_1" for user name "default1" and property code "p1_code" is added
    Given Role with name "user_filter_role_2" for user name "default1" and property code "p1_code" is added
    Given Role with name "user_filter_role_3" for user name "default1" and property code "p1_code" is added
    Given Role with name "user_filter_role_4" for user name "default1" and property code "p1_code" is added
    Given Role with name "user_filter_role_5" for user name "default1" and property code "p1_code" is added
    Given Role with name "user_filter_role_6" for user name "default1" and property code "p1_code" is added
    When List of roles for user with username "default1" and property code "p1_code" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> user roles returned
    And There are user roles with following role names returned in order: <expected_usernames>
    And Total count is "<total>"
    Examples:
      | limit | cursor | returned | total | filter                     | sort  | sort_desc | expected_usernames                                                                                 |
      | 5     | 0      | 5        | 6     | name=='user_filter_role_*' | name  |           | user_filter_role_1, user_filter_role_2, user_filter_role_3, user_filter_role_4, user_filter_role_5 |
      | 5     | 0      | 5        | 6     | name=='user_filter_role_*' |       | name      | user_filter_role_6, user_filter_role_5, user_filter_role_4, user_filter_role_3, user_filter_role_2 |
      | 5     | 2      | 4        | 6     | name=='user_filter_role_*' | name  |           | user_filter_role_3, user_filter_role_4, user_filter_role_5, user_filter_role_6                     |
      | 5     | 2      | 4        | 6     | name=='user_filter_role_*' |       | name      | user_filter_role_4, user_filter_role_3, user_filter_role_2, user_filter_role_1                     |
      | /null | /null  | 1        | 1     | name=='user_filter_role_6' | /null | /null     | user_filter_role_6                                                                                 |

  Scenario Outline: Send POST request with empty body to all user-property endpoints
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                                                            |
      | identity/users/55529079-48f0-4f00-9bec-e2329a8bdaac/properties/842529dd-481f-430d-b6b6-686fbb687cab                                            |
      | identity/users/55529079-48f0-4f00-9bec-e2329a8bdaac/properties/842529dd-481f-430d-b6b6-686fbb687cab/roles                                      |
      | identity/users/55529079-48f0-4f00-9bec-e2329a8bdaac/properties/842529dd-481f-430d-b6b6-686fbb687cab/roles/a318fd9a-a05d-42d8-8e84-42e904ace123 |
