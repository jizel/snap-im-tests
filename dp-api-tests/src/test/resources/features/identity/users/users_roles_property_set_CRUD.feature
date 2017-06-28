@Identity
Feature: Users property set roles CRUD

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone      |
      | 1234fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 2234fd9a-a05d-42d8-8e84-42e904ace123 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1234fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | id                                   | type     | username | firstName | lastName | email                | timezone      | languageCode |
      | 2048b11e-eff2-477c-b322-015bbd931e46 | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | e0aa919b-5b55-4f03-99ba-48c9f8fec42d | customer | default2 | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "1234fd9a-a05d-42d8-8e84-42e904ace123" and user "default1"
      | id                                   | name            | description            | type            |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 | ps1_name        | ps1_description        | brand           |
    Given Switch for user property set role tests
    Given The following roles exist
      | id                                   | roleName    | description            | applicationId                        |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | user_role_1 | optional description 1 | 11111111-0000-4000-a000-111111111111 |
      | b318fd9a-a05d-42d8-8e84-42e904ace123 | user_role_2 | optional description 2 | 11111111-0000-4000-a000-111111111111 |

  Scenario Outline: Assigning not existing role
    And Relation between user "default1" and property set "ps1_name" exists
    When I assign role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>"
    Then Response code is "422"
    And Custom code is 42202
    Examples:
      | role_id                              | user_name | property_set_name | customer_id                          |
      | 1111fd9a-a05d-42d8-8e84-42e904ace123 | default1  | ps1_name          | 1234fd9a-a05d-42d8-8e84-42e904ace123 |

  Scenario Outline: Assigning property type of role to user property set
    Given Switch for user property role tests
    Given The following roles exist
      | roleId                               | roleName        | description            | id                                   |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | user_role_wrong | optional description 1 | 11111111-0000-4000-a000-111111111111 |
    And Relation between user "default1" and property set "ps1_name" exists
    When I assign role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>"
    Then Response code is "422"
    Examples:
      | role_id                              | user_name | property_set_name | customer_id                          |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | default1  | ps1_name          | 1234fd9a-a05d-42d8-8e84-42e904ace123 |

  Scenario Outline: Assigning customer type of role to user property set
    Given Switch for user customer role tests
    And Relation between user "default1" and property set "ps1_name" exists
    And The following roles exist
      | id                                   | roleName        | description            | applicationId                        |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | user_role_wrong | optional description 1 | 11111111-0000-4000-a000-111111111111 |
    When I assign role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>"
    Then Response code is "422"
    And Custom code is 42202

    Examples:
      | role_id                              | user_name | property_set_name | customer_id |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | default1  | ps1_name          | 1234fd9a-a05d-42d8-8e84-42e904ace123           |

  Scenario Outline: Assigning role to not existing user - property set relation
    When I assign role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_id>"
    Then Response code is "404"
    And Custom code is 40402
    Examples:
      | role_id                              | user_name | property_set_id                      |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | default1  | a318fd9a-a05d-42d8-8e84-42e904ace123 |


  Scenario Outline: Removing role from user property set
    And Relation between user "default1" and property set "ps1_name" exists
    When Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" is added
    When Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" is deleted
    Then Response code is "204"
    And Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" does not exist

    Examples:
      | role_id                              | property_set_name | user_name | customer_id |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | ps1_name          | default1  | 1234fd9a-a05d-42d8-8e84-42e904ace123           |


  Scenario Outline: Checking error code for removing not existing role from user property set
    When Nonexistent role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" is deleted
    Then Response code is "404"

    Examples:
      | role_id                              | property_set_name | user_name | customer_id |
      | 1111fd9a-a11d-11d8-8e84-11e904ace123 | ps1_name          | default1  | 1234fd9a-a05d-42d8-8e84-42e904ace123           |

  Scenario Outline: Filtering list of roles for property set customer relationship
    Given Switch for user property set role tests
    And Relation between user "default1" and property set "ps1_name" exists
    And The following roles exist
      | id                                   | roleName           | description            |
      | 1796a238-39de-44fb-af67-c3c5e4c4d739 | user_filter_role_1 | optional description 1 |
      | 2796a238-39de-44fb-af67-c3c5e4c4d739 | user_filter_role_2 | optional description 2 |
      | 3796a238-39de-44fb-af67-c3c5e4c4d739 | user_filter_role_3 | optional description 3 |
      | 4796a238-39de-44fb-af67-c3c5e4c4d739 | user_filter_role_4 | optional description 4 |
      | 5796a238-39de-44fb-af67-c3c5e4c4d739 | user_filter_role_5 | optional description 5 |
      | 6796a238-39de-44fb-af67-c3c5e4c4d739 | user_filter_role_6 | optional description 6 |
    Given Role with name "user_filter_role_1" for user name "default1" and property set name "ps1_name" for customer id "1234fd9a-a05d-42d8-8e84-42e904ace123" is added
    Given Role with name "user_filter_role_2" for user name "default1" and property set name "ps1_name" for customer id "1234fd9a-a05d-42d8-8e84-42e904ace123" is added
    Given Role with name "user_filter_role_3" for user name "default1" and property set name "ps1_name" for customer id "1234fd9a-a05d-42d8-8e84-42e904ace123" is added
    Given Role with name "user_filter_role_4" for user name "default1" and property set name "ps1_name" for customer id "1234fd9a-a05d-42d8-8e84-42e904ace123" is added
    Given Role with name "user_filter_role_5" for user name "default1" and property set name "ps1_name" for customer id "1234fd9a-a05d-42d8-8e84-42e904ace123" is added
    Given Role with name "user_filter_role_6" for user name "default1" and property set name "ps1_name" for customer id "1234fd9a-a05d-42d8-8e84-42e904ace123" is added
    When List of roles for user with username "default1" and property set name "ps1_name" for customer id "1234fd9a-a05d-42d8-8e84-42e904ace123" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> user roles returned
    And Total count is "<total>"
    Examples:
      | limit | cursor | returned | total | filter                                        | sort      | sort_desc |
      | 5     | 0      | 1        | 1     | role_id==1796a238-39de-44fb-af67-c3c5e4c4d739 |           |           |
      | 5     | 0      | 1        | 1     | role_id==1796a238-39de-44fb-af67-c3c5e4c4d739 |           | role_id   |
      | 5     | 2      | 4        | 6     | /null                                         | role_id   |           |
      | 5     | 2      | 4        | 6     | /null                                         |           | role_id   |
      | 5     | 0      | 5        | 6     | /null                                         |           |           |
#  DP-2193
#      | /null | /null  | 1        | 1     | role_id==*6*                                  | /null     | /null     |


  Scenario Outline: Send POST request with empty body to all user-property set endpoints
    Given Relation between user "default1" and property set "ps1_name" exists
    And Role with id "a318fd9a-a05d-42d8-8e84-42e904ace123" for user name "default1" and property set name "ps1_name" is added
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                                                               |
      | identity/users/2048b11e-eff2-477c-b322-015bbd931e46/property_sets/c729e3b0-69bf-4c57-91bd-30230d2c1bd0                                            |
      | identity/users/2048b11e-eff2-477c-b322-015bbd931e46/property_sets/c729e3b0-69bf-4c57-91bd-30230d2c1bd0/roles                                      |
      | identity/users/2048b11e-eff2-477c-b322-015bbd931e46/property_sets/c729e3b0-69bf-4c57-91bd-30230d2c1bd0/roles/a318fd9a-a05d-42d8-8e84-42e904ace123 |

  Scenario: Role cannot be deleted until User is (and vice versa)
    When Role with id "a318fd9a-a05d-42d8-8e84-42e904ace123" for user name "default1" and property set name "ps1_name" for customer "1234fd9a-a05d-42d8-8e84-42e904ace123" is added
    When Role with name "user_role_1" is deleted
    Then Response code is "409"
    And Custom code is 40915
    When User "default1" is deleted
    Then Response code is "409"
    And Custom code is 40915
    When Role with id "a318fd9a-a05d-42d8-8e84-42e904ace123" for user name "default1" and property set name "ps1_name" for customer "1234fd9a-a05d-42d8-8e84-42e904ace123" is deleted
    Given User "default1" is removed from customer with id "1234fd9a-a05d-42d8-8e84-42e904ace123"
    Given All users are removed for property_sets with names: ps1_name
    When Role with name "user_role_1" is deleted
    Then Response code is "204"
    When User "default1" is deleted
    Then Response code is "204"