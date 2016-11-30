@Identity
Feature: Users property set roles CRUD

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1234fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 2234fd9a-a05d-42d8-8e84-42e904ace123 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1234fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 2048b11e-eff2-477c-b322-015bbd931e46 | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | e0aa919b-5b55-4f03-99ba-48c9f8fec42d | snapshot | default2 | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "1234fd9a-a05d-42d8-8e84-42e904ace123" and user "2048b11e-eff2-477c-b322-015bbd931e46"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | brand           |
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace124 |
    Given Switch for user property set role tests
    Given The following roles exist
      | roleId                               | roleName    | description            | applicationId                        |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | user_role_1 | optional description 1 | a318fd9a-a05d-42d8-8e84-42e904ace124 |
      | b318fd9a-a05d-42d8-8e84-42e904ace123 | user_role_2 | optional description 2 | a318fd9a-a05d-42d8-8e84-42e904ace124 |


  @Smoke
  Scenario Outline: Assigning role to user property set
    When Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" is added
    Then Response code is "201"
    Examples:
      | role_id                              | user_name | property_set_name | customer_id |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | default1  | ps1_name          | 1234fd9a-a05d-42d8-8e84-42e904ace123           |
      | b318fd9a-a05d-42d8-8e84-42e904ace123 | default1  | ps1_name          | 1234fd9a-a05d-42d8-8e84-42e904ace123           |

  Scenario Outline: Assigning not existing role
    When Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" is added
    Then Response code is "422"
    And Custom code is 42202
    Examples:
      | role_id                              | user_name | property_set_name | customer_id |
      | 1111fd9a-a05d-42d8-8e84-42e904ace123 | default1  | ps1_name          | 1234fd9a-a05d-42d8-8e84-42e904ace123           |

  Scenario Outline: Assigning property type of role to user property set
    Given Switch for user property role tests
    Given The following roles exist
      | roleId                               | roleName        | description            | applicationId                        |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | user_role_wrong | optional description 1 | a318fd9a-a05d-42d8-8e84-42e904ace124 |
    When Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" is added
    Then Response code is "422"
    And Custom code is 42202
    Examples:
      | role_id                              | user_name | property_set_name | customer_id |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | default1  | ps1_name          | 1234fd9a-a05d-42d8-8e84-42e904ace123           |

  Scenario Outline: Assigning customer type of role to user property set
    Given Switch for user customer role tests
    Given The following roles exist
      | roleId                               | roleName        | description            | applicationId                        |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | user_role_wrong | optional description 1 | a318fd9a-a05d-42d8-8e84-42e904ace124 |
    When Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" is added
    Then Response code is "422"
    And Custom code is 42202

    Examples:
      | role_id                              | user_name | property_set_name | customer_id |
      | a111fd9a-a05d-42d8-8e84-42e904ace123 | default1  | ps1_name          | 1234fd9a-a05d-42d8-8e84-42e904ace123           |

  #todo issue dp-1294
  Scenario Outline: Assigning role to not existing property set
    When Role with id "<role_id>" for user name "<user_name>" and property set id "<property_set_id>" is added
    Then Response code is "404"
    And Custom code is 40402
    Examples:
      | role_id                              | user_name | property_set_id                      |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | default1  | 1111fd9a-a05d-42d8-8e84-42e904ace123 |

    #todo issue dp-1294
  Scenario Outline: Assigning role to not existing user
    When Role with id "<role_id>" for not existing user id and property set name "<property_set_name>" for customer id "<customer_id>" is added
    Then Response code is "404"
    And Custom code is 40402
    Examples:
      | role_id                              | property_set_name | customer_id |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | ps1_name          | 1234fd9a-a05d-42d8-8e84-42e904ace123           |


  Scenario Outline: Removing role from user property set
    When Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" is added
    When Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" is deleted
    Then Response code is "204"
    When Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" does not exist

    Examples:
      | role_id                              | property_set_name | user_name | customer_id |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | ps1_name          | default1  | 1234fd9a-a05d-42d8-8e84-42e904ace123           |


  Scenario Outline: Checking error code for removing not existing role from user customer
    When Role with id "<role_id>" for user name "<user_name>" and property set name "<property_set_name>" for customer "<customer_id>" is deleted
    Then Response code is "204"

    Examples:
      | role_id                              | property_set_name | user_name | customer_id |
      | 1111fd9a-a11d-11d8-8e84-11e904ace123 | ps1_name          | default1  | 1234fd9a-a05d-42d8-8e84-42e904ace123           |

  Scenario Outline: Filtering list of roles for user customer relationship
    Given Switch for user property set role tests
    Given The following roles exist
      | roleName           | description            | applicationId                        |
      | user_filter_role_1 | optional description 1 | a318fd9a-a05d-42d8-8e84-42e904ace124 |
      | user_filter_role_2 | optional description 2 | a318fd9a-a05d-42d8-8e84-42e904ace124 |
      | user_filter_role_3 | optional description 3 | a318fd9a-a05d-42d8-8e84-42e904ace124 |
      | user_filter_role_4 | optional description 4 | a318fd9a-a05d-42d8-8e84-42e904ace124 |
      | user_filter_role_5 | optional description 5 | a318fd9a-a05d-42d8-8e84-42e904ace124 |
      | user_filter_role_6 | optional description 6 | a318fd9a-a05d-42d8-8e84-42e904ace124 |

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
    And There are user roles with following role names returned in order: <expected_usernames>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                     | sort  | sort_desc | expected_usernames                                                                                 |
      | 5     | 0      | 5        | 6     | name=='user_filter_role_*' | name  |           | user_filter_role_1, user_filter_role_2, user_filter_role_3, user_filter_role_4, user_filter_role_5 |
      | 5     | 0      | 5        | 6     | name=='user_filter_role_*' |       | name      | user_filter_role_6, user_filter_role_5, user_filter_role_4, user_filter_role_3, user_filter_role_2 |
      | 5     | 2      | 4        | 6     | name=='user_filter_role_*' | name  |           | user_filter_role_3, user_filter_role_4, user_filter_role_5, user_filter_role_6                     |
      | 5     | 2      | 4        | 6     | name=='user_filter_role_*' |       | name      | user_filter_role_4, user_filter_role_3, user_filter_role_2, user_filter_role_1                     |
      | /null | /null  | 1        | 1     | name=='user_filter_role_6' | /null | /null     | user_filter_role_6                                                                                 |



