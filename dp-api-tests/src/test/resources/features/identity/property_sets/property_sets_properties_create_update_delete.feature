Feature: Property sets properties create update delete

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
      | ps2_name        | ps2_description        | branch          |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |


  @Smoke
  Scenario: Adding property to property set
    When Property with code "p2_code" is added to property set with name "ps1_name" for customer with code "c1t"
    Then Response code is "204"


  @Smoke
  Scenario: Removing property from property set
    When Property with code "p2_code" is removed from property set with name "ps1_name" for customer with code "c1t"
    Then Response code is "204"
    And Body is empty
    And Property with code "p2_code" isn't there for property set with name "ps1_name" for customer with code "c1t"

  @Smoke
  Scenario: Removing propertySet with valid properties
    Given Relation between property with code "p1_code" and property set with name "ps2_name" for customer with code "c1t" exists
    Given Relation between property with code "p2_code" and property set with name "ps2_name" for customer with code "c1t" exists
    When Property set with name "toDelete" for customer with code "c1t" is deleted
    Then Response code is "204"
    And Body is empty
    And Property set with same id doesn't exist


  Scenario: Checking error code for removing property from property set
    When Nonexistent property is removed from property set with name "ps1_name" for customer with code "c1t"
    Then Response code is "204"


  Scenario Outline: Filtering list of properties for property set
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName          | propertyCode          | website                    | email                    | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | filtering_prop_name_1 | filtering_prop_code_1 | http://www.snapshot.travel | filtering_p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_2 | filtering_prop_name_2 | filtering_prop_code_2 | http://www.snapshot.travel | filtering_p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_3 | filtering_prop_name_3 | filtering_prop_code_3 | http://www.snapshot.travel | filtering_p3@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_4 | filtering_prop_name_4 | filtering_prop_code_4 | http://www.snapshot.travel | filtering_p4@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_5 | filtering_prop_name_5 | filtering_prop_code_5 | http://www.snapshot.travel | filtering_p5@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | salesforceid_6 | filtering_prop_name_6 | filtering_prop_code_6 | http://www.snapshot.travel | filtering_p6@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Relation between property with code "filtering_prop_code_1" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "filtering_prop_code_2" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "filtering_prop_code_3" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "filtering_prop_code_4" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "filtering_prop_code_5" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "filtering_prop_code_6" and property set with name "ps1_name" for customer with code "c1t" exists
    When List of properties for property set with name "ps1_name" for customer with code "c1t" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> property set properties  returned
    And There are properties with following names returned in order: <expected_names>
    Examples:
      | limit | cursor | returned | filter                       | sort  | sort_desc | expected_names                                                                                                    |
      | 5     | 0      | 5        | name=='filtering_prop_name*' | name  |           | filtering_prop_name_1, filtering_prop_name_2, filtering_prop_name_3, filtering_prop_name_4, filtering_prop_name_5 |
      | 5     | 0      | 5        | name=='filtering_prop_name*' |       | name      | filtering_prop_name_6, filtering_prop_name_5, filtering_prop_name_4, filtering_prop_name_3, filtering_prop_name_2 |
      | 5     | 2      | 4        | name=='filtering_prop_name*' | name  |           | filtering_prop_name_3, filtering_prop_name_4, filtering_prop_name_5, filtering_prop_name_6                        |
      | 5     | 2      | 4        | name=='filtering_prop_name*' |       | name      | filtering_prop_name_4, filtering_prop_name_3, filtering_prop_name_2, filtering_prop_name_1                        |
      | /null | /null  | 1        | name==filtering_prop_name_6  | /null | /null     | filtering_prop_name_6                                                                                             |