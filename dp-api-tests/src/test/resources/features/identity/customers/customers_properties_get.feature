Feature: customers_properties_get

  Background:
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel |


    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone  |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | UTC+01:00 |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | UTC+01:00 |

    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "asset_management" from "2015-01-01" to "2015-12-31"


  Scenario: Getting customerProperty
    When Property with code "p1_code" from customer with code "c1t" is got with type "anchor"
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "property_name" value "p1_name"
    And Body contains entity with attribute "validFrom" value "2015-01-01"
    And Body contains entity with attribute "validTo" value "2015-10-31"
    And Body contains entity with attribute "type" value "anchor"

  Scenario: Getting customerProperty with etag
    When Property with code "p1_code" from customer with code "c1t" is got with type "anchor" with etag
    Then Response code is "304"
    And Body is empty

  Scenario: Getting customerProperty with not current etag
  CustomerProperty is got, etag is saved to tmp, then customerProperty valid_to is updated to "2016-12-31" so etag should change and is got again with previous etag

    When Property with code "p1_code" from customer with code "c1t" is got with type "anchor" for etag, updated and got with previous etag
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "property_name" value "p1_name"
    And Body contains entity with attribute "validFrom" value "2015-01-01"
    And Body contains entity with attribute "validTo" value "2016-12-31"
    And Body contains entity with attribute "type" value "anchor"

  Scenario: Checking error code for getting customerProperty
    When Nonexistent customerPropety id is got for customer with code "c1t"
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Getting list of customerProperties
  not working now

    Given The following customers exist with random address
      | companyName               | email               | code     | salesforceId              | vatId      | isDemoCustomer | phone         | website                    |
      | List test Given company 1 | list_c1@tenants.biz | list_c1t | list_salesforceid_given_1 | CZ22000001 | true           | +111111111111 | http://www.snapshot.travel |
      | List test Given company 2 | list_c2@tenants.biz | list_c2t | list_salesforceid_given_2 | CZ22000002 | true           | +111111111111 | http://www.snapshot.travel |

    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName           | propertyCode           | website                    | email          | isDemoProperty | timezone  |
      | salesforceid_1 | list_prop_cust_p1_name | list_prop_cust_p1_code | http://www.snapshot.travel | p1@tenants.biz | true           | UTC+01:00 |
      | salesforceid_2 | list_prop_cust_p2_name | list_prop_cust_p2_code | http://www.snapshot.travel | p2@tenants.biz | true           | UTC+01:00 |
      | salesforceid_3 | list_prop_cust_p3_name | list_prop_cust_p3_code | http://www.snapshot.travel | p3@tenants.biz | true           | UTC+01:00 |
      | salesforceid_4 | list_prop_cust_p4_name | list_prop_cust_p4_code | http://www.snapshot.travel | p4@tenants.biz | true           | UTC+01:00 |
      | salesforceid_5 | list_prop_cust_p5_name | list_prop_cust_p5_code | http://www.snapshot.travel | p5@tenants.biz | true           | UTC+01:00 |
      | salesforceid_6 | list_prop_cust_p6_name | list_prop_cust_p6_code | http://www.snapshot.travel | p6@tenants.biz | true           | UTC+01:00 |
      | salesforceid_7 | list_prop_cust_p7_name | list_prop_cust_p7_code | http://www.snapshot.travel | p7@tenants.biz | true           | UTC+01:00 |
      | salesforceid_8 | list_prop_cust_p8_name | list_prop_cust_p8_code | http://www.snapshot.travel | p8@tenants.biz | true           | UTC+01:00 |

    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p1_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p2_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p3_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p4_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p5_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p6_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p7_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p8_code"

    Given Relation between property with code "list_prop_cust_p1_code" and customer with code "list_c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with code "list_c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with code "list_c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with code "list_c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with code "list_c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with code "list_c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with code "list_c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with code "list_c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with code "list_c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with code "list_c1t" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with code "list_c1t" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with code "list_c1t" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with code "list_c1t" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with code "list_c1t" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with code "list_c1t" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with code "list_c1t" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with code "list_c1t" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with code "list_c1t" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with code "list_c1t" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with code "list_c1t" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with code "list_c1t" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with code "list_c1t" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with code "list_c1t" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with code "list_c1t" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with code "list_c1t" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with code "list_c1t" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with code "list_c1t" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with code "list_c1t" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with code "list_c1t" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with code "list_c1t" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with code "list_c1t" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with code "list_c1t" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with code "list_c1t" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with code "list_c1t" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with code "list_c1t" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with code "list_c1t" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with code "list_c1t" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with code "list_c1t" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with code "list_c1t" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with code "list_c1t" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with code "list_c1t" exists with type "chains" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with code "list_c1t" exists with type "chains" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with code "list_c1t" exists with type "chains" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with code "list_c1t" exists with type "chains" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with code "list_c1t" exists with type "chains" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with code "list_c1t" exists with type "chains" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with code "list_c1t" exists with type "chains" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with code "list_c1t" exists with type "chains" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with code "list_c1t" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with code "list_c1t" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with code "list_c1t" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with code "list_c1t" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with code "list_c1t" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with code "list_c1t" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with code "list_c1t" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with code "list_c1t" exists with type "membership" from "2015-01-01" to "2015-12-31"

    When List of customerProperties is got for customer with code "list_c1t" with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customerProperties returned

    Examples:
      | limit | cursor | returned |
      | /null |        | 50       |
      | /null | /null  | 50       |
      |       |        | 50       |
      |       | /null  | 50       |
      | 15    |        | 15       |
      |       | 1      | 50       |
      | 20    | 0      | 20       |
      | 10    | 0      | 10       |
      | 5     | 5      | 5        |


  Scenario Outline: Checking error codes for getting list of customerProperties
    When List of customerProperties is got for customer with code "c1t" with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit | cursor | filter   | sort  | sort_desc | response_code | custom_code |
      #limit and cursor
      | /null | -1     | /null    | /null | /null     | 400           | 63          |
      |       | -1     | /null    | /null | /null     | 400           | 63          |
      | /null | text   | /null    | /null | /null     | 400           | 63          |
      |       | text   | /null    | /null | /null     | 400           | 63          |
      | -1    |        | /null    | /null | /null     | 400           | 63          |
      | -1    | /null  | /null    | /null | /null     | 400           | 63          |
      | text  |        | /null    | /null | /null     | 400           | 63          |
      | text  | /null  | /null    | /null | /null     | 400           | 63          |
      | 10    | -1     | /null    | /null | /null     | 400           | 63          |
      | text  | 0      | /null    | /null | /null     | 400           | 63          |
      | 10    | text   | /null    | /null | /null     | 400           | 63          |
      #filtering and sorting
      | 10    | 0      | /null    | type  | type      | 400           | 64          |
      | 10    | 0      | type==   | /null | /null     | 400           | 63          |
      | 10    | 0      | vat==CZ* | /null | /null     | 400           | 63          |

  Scenario Outline: Filtering list of customer properties
    Given The following customers exist with random address
      | companyName                      | email               | code            | salesforceId                     | vatId      | isDemoCustomer | phone         | website                    |
      | Filter list test Given company 1 | list_c1@tenants.biz | filter_list_c1t | filter_list_salesforceid_given_1 | CZ22000001 | true           | +111111111111 | http://www.snapshot.travel |


    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName                  | propertyCode                  | website                    | email           | isDemoProperty | timezone  |
      | salesforceid_1 | filter_list_prop_cust_p1_name | filter_list_prop_cust_p1_code | http://www.snapshot.travel | fp1@tenants.biz | true           | UTC+01:00 |
      | salesforceid_2 | filter_list_prop_cust_p2_name | filter_list_prop_cust_p2_code | http://www.snapshot.travel | fp2@tenants.biz | true           | UTC+01:00 |
      | salesforceid_3 | filter_list_prop_cust_p3_name | filter_list_prop_cust_p3_code | http://www.snapshot.travel | fp3@tenants.biz | true           | UTC+01:00 |
      | salesforceid_4 | filter_list_prop_cust_p4_name | filter_list_prop_cust_p4_code | http://www.snapshot.travel | fp4@tenants.biz | true           | UTC+01:00 |
      | salesforceid_5 | filter_list_prop_cust_p5_name | filter_list_prop_cust_p5_code | http://www.snapshot.travel | fp5@tenants.biz | true           | UTC+01:00 |
      | salesforceid_6 | filter_list_prop_cust_p6_name | filter_list_prop_cust_p6_code | http://www.snapshot.travel | fp6@tenants.biz | true           | UTC+01:00 |
      | salesforceid_7 | filter_list_prop_cust_p7_name | filter_list_prop_cust_p7_code | http://www.snapshot.travel | fp7@tenants.biz | true           | UTC+01:00 |
      | salesforceid_8 | filter_list_prop_cust_p8_name | filter_list_prop_cust_p8_code | http://www.snapshot.travel | fp8@tenants.biz | true           | UTC+01:00 |

    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p1_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p2_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p3_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p4_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p5_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p6_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p7_code"
    Given All customerProperties are deleted from DB for customer code "list_c1t" and property code "list_prop_cust_p8_code"

    Given Relation between property with code "list_prop_cust_p1_code" and customer with code "list_c1t" exists with type "anchor" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with code "list_c1t" exists with type "anchor" from "2015-02-01" to "2030-03-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with code "list_c1t" exists with type "anchor" from "2015-03-01" to "2030-04-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with code "list_c1t" exists with type "anchor" from "2015-04-01" to "2030-05-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with code "list_c1t" exists with type "anchor" from "2015-05-01" to "2030-06-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with code "list_c1t" exists with type "anchor" from "2015-06-01" to "2030-07-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with code "list_c1t" exists with type "anchor" from "2015-07-01" to "2030-08-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with code "list_c1t" exists with type "anchor" from "2015-08-01" to "2030-09-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with code "list_c1t" exists with type "anchor" from "2015-09-01" to "2030-10-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with code "list_c1t" exists with type "data_owner" from "2015-10-01" to "2030-11-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with code "list_c1t" exists with type "data_owner" from "2015-11-01" to "2030-12-31"

    When List of customerProperties is got for customer with code "c1t" with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customerProperties returned
    #And There are customers with following codes returned in order: <string>

    Examples:
      | limit | cursor | returned | filter                | sort | sort_desc | expected_codes                                             |
      | 10    | 0      | 9        | type==anchor          | type |           | Filter_c1t, Filter_c2t, Filter_c3t, Filter_c4t, Filter_c5t |
      | 5     | 2      | 4        | valid_from<2015-06-15 |      | valid_to  | Filter_c5t, Filter_c4t, Filter_c3t, Filter_c2t, Filter_c1t |
  #add all fields

    #TODO add test for wrong parameters in url

