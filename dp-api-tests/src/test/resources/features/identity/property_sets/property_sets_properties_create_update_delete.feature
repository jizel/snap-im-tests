Feature: property_sets_properties_create_update_delete

  Background:
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel |

    Given All properties are removed from property_sets for customer with code "c1t" with names: ps1_name, ps2_name

    Given The following property sets exist for customer with code "c1t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
      | ps2_name        | ps2_description        | branch          |

    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone  |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | UTC+01:00 |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | UTC+01:00 |

    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with code "c1t" exists


  Scenario: Adding property to property set

    When Property with code "p2_name" is added to property set with name "ps1_name" for customer with code "c2t"
    Then Response code is "201"
    And Etag header is present

  #TODO validate just one primary property, notexistent property, already present property
  #validate different type of propertys


  Scenario: Removing property from property set
#failing because of not working filtering for property propertys
    When Property with code "p2_name" is removed from property set with name "ps1_name" for customer with code "c1t"
    Then Response code is "204"
    And Body is empty
    And Property with code "p2_name" isn't there for property set with name "ps1_name" for customer with code "c1t"


  Scenario: Checking error code for removing property from property set
    When Nonexistent property is removed from property set with name "ps1_name" for customer with code "c1t"
    Then Response code is "204"


  Scenario Outline: Filtering list of properties for property set
    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName          | propertyCode          | website                    | email                    | isDemoProperty | timezone  |
      | salesforceid_1 | filtering_prop_name_1 | filtering_prop_code_1 | http://www.snapshot.travel | filtering_p1@tenants.biz | true           | UTC+01:00 |
      | salesforceid_2 | filtering_prop_name_2 | filtering_prop_code_2 | http://www.snapshot.travel | filtering_p2@tenants.biz | true           | UTC+01:00 |
      | salesforceid_3 | filtering_prop_name_3 | filtering_prop_code_3 | http://www.snapshot.travel | filtering_p3@tenants.biz | true           | UTC+01:00 |
      | salesforceid_4 | filtering_prop_name_4 | filtering_prop_code_4 | http://www.snapshot.travel | filtering_p4@tenants.biz | true           | UTC+01:00 |
      | salesforceid_5 | filtering_prop_name_5 | filtering_prop_code_5 | http://www.snapshot.travel | filtering_p5@tenants.biz | true           | UTC+01:00 |
      | salesforceid_6 | filtering_prop_name_6 | filtering_prop_code_6 | http://www.snapshot.travel | filtering_p6@tenants.biz | true           | UTC+01:00 |

    Given Relation between property with code "filtering_prop_code_1" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "filtering_prop_code_2" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "filtering_prop_code_3" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "filtering_prop_code_4" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "filtering_prop_code_5" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "filtering_prop_code_6" and property set with name "ps1_name" for customer with code "c1t" exists

    When List of properties for property set with name "ps1_name" for customer with code "c1t" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> users returned
    And There are properties with following names returned in order: <expected_names>

    Examples:
      | limit | cursor | returned | filter                                | sort          | sort_desc     | expected_names                                                                                                    |
      | 5     | 0      | 5        | property_name=='filtering_prop_name*' | property_name |               | filtering_prop_name_1, filtering_prop_name_2, filtering_prop_name_3, filtering_prop_name_4, filtering_prop_name_5 |
      | 5     | 0      | 5        | property_name=='filtering_prop_name*' |               | property_name | filtering_prop_name_6, filtering_prop_name_5, filtering_prop_name_4, filtering_prop_name_3, filtering_prop_name_2 |
      | 5     | 2      | 4        | property_name=='filtering_prop_name*' | property_name |               | filtering_prop_name_3, filtering_prop_name_4, filtering_prop_name_5, filtering_prop_name_6                        |
      | 5     | 2      | 4        | property_name=='filtering_prop_name*' |               | property_name | filtering_prop_name_4, filtering_prop_name_3, filtering_prop_name_2, filtering_prop_name_1                        |
      | /null | /null  | 1        | property_name==filtering_prop_name_6  | /null         | /null         | filtering_prop_name_6                                                                                             |