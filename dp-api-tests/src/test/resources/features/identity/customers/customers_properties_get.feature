@skipped
Feature: customers_properties_get

  Background:
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel |


    Given The following properties exist with random address and billing address
      | salesforceId          | propertyName  | propertyCode  | website                       | email             | vatId         | isDemoProperty    | timezone  |
      | salesforceid_1        | p1_name       | p1_code       | http://www.snapshot.travel    | p1@tenants.biz    | CZ20000001    | true              | UTC+01:00 |
      | salesforceid_2        | p2_name       | p2_code       | http://www.snapshot.travel    | p2@tenants.biz    | CZ20000002    | true              | UTC+01:00 |

    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "asset_management" from "2015-01-01" to "2015-12-31"


  Scenario: Getting customerProperty
    When Property with code "p1_code" from customer with code "c1t" is got with type "anchor"
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains customerProperty type with "property_name" value "p1_name"
    And Body contains customerProperty type with "validFrom" value "2015-01-01"
    And Body contains customerProperty type with "validTo" value "2015-10-31"
    And Body contains customerProperty type with "type" value "anchor"

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
    And Body contains customerProperty type with "property_name" value "p1_name"
    And Body contains customerProperty type with "validFrom" value "2015-01-01"
    And Body contains customerProperty type with "validTo" value "2016-12-31"
    And Body contains customerProperty type with "type" value "anchor"

  Scenario: Checking error code for getting customerProperty
    When Nonexistent customerPropety id is got for customer with code "c1t"
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Getting list of customerProperties
    Given The following customers exist with random address
      | companyName                | email                | code      | salesforceId               | vatId      | isDemoCustomer | phone         | website                    |
      | List test Given company 1  | list_c1@tenants.biz  | list_c1t  | list_salesforceid_given_1  | CZ22000001 | true           | +111111111111 | http://www.snapshot.travel |
      | List test Given company 2  | list_c2@tenants.biz  | list_c2t  | list_salesforceid_given_2  | CZ22000002 | true           | +111111111111 | http://www.snapshot.travel |
      | List test Given company 3  | list_c3@tenants.biz  | list_c3t  | list_salesforceid_given_3  | CZ22000003 | true           | +111111111111 | http://www.snapshot.travel |
      | List test Given company 4  | list_c4@tenants.biz  | list_c4t  | list_salesforceid_given_4  | CZ22000004 | true           | +111111111111 | http://www.snapshot.travel |
      | List test Given company 5  | list_c5@tenants.biz  | list_c5t  | list_salesforceid_given_5  | CZ22000005 | true           | +111111111111 | http://www.snapshot.travel |
      | List test Given company 6  | list_c6@tenants.biz  | list_c6t  | list_salesforceid_given_6  | CZ22000006 | true           | +111111111111 | http://www.snapshot.travel |


    When List of customerProperties is got for customer with code "c1t" with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
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
      | limit | cursor | filter   | sort         | sort_desc    | response_code | custom_code |
      #limit and cursor
      | /null | -1     | /null    | /null        | /null        | 400           | 63          |
      |       | -1     | /null    | /null        | /null        | 400           | 63          |
      | /null | text   | /null    | /null        | /null        | 400           | 63          |
      |       | text   | /null    | /null        | /null        | 400           | 63          |
      | -1    |        | /null    | /null        | /null        | 400           | 63          |
      | -1    | /null  | /null    | /null        | /null        | 400           | 63          |
      | text  |        | /null    | /null        | /null        | 400           | 63          |
      | text  | /null  | /null    | /null        | /null        | 400           | 63          |
      | 10    | -1     | /null    | /null        | /null        | 400           | 63          |
      | text  | 0      | /null    | /null        | /null        | 400           | 63          |
      | 10    | text   | /null    | /null        | /null        | 400           | 63          |
      #filtering and sorting
      | 10    | 0      | /null    | company_name | company_name | 400           | 64          |
      #| 10    | 0      | /null    | company_name |              | 400           | 63          |
      #| 10    | 0      | /null    |              | company_name | 400           | 63          |
      #| 10    | 0      | /null    | /null        |              | 400           | 63          |
      #| 10    | 0      | /null    |              | /null        | 400           | 63          |
      #| 10    | 0      | /null    |              |              | 400           | 63          |
      | 10    | 0      | code==   | /null        | /null        | 400           | 63          |
      | 10    | 0      | vat==CZ* | /null        | /null        | 400           | 63          |

  Scenario Outline: Filtering list of customer properties
    Given The following customers exist with random address
      | companyName                           | email                 | code       | salesforceId                | vatId      | isDemoCustomer | phone         | website                    |
      | Filter test Given company 1           | Filter_c1@tenants.biz | Filter_c1t | Filter_salesforceid_given_1 | ATU2200001 | true           | +111111111111 | http://www.snapshot.travel |
      | Filter test Given company 2           | Filter_c2@tenants.biz | Filter_c2t | Filter_salesforceid_given_2 | ATU2200002 | true           | +111111111111 | http://www.snapshot.travel |
      | Filter test Given company 3           | Filter_c3@tenants.biz | Filter_c3t | Filter_salesforceid_given_3 | ATU2200003 | true           | +111111111111 | http://www.snapshot.travel |
      | Filter test Given company 4           | Filter_c4@tenants.biz | Filter_c4t | Filter_salesforceid_given_4 | ATU2200004 | true           | +111111111111 | http://www.snapshot.travel |
      | Filter test Given company 5           | Filter_c5@tenants.biz | Filter_c5t | Filter_salesforceid_given_5 | ATU2200005 | true           | +111111111111 | http://www.snapshot.travel |
      | Filter different test Given company 6 | Filter_c6@tenants.biz | Filter_c6t | Filter_salesforceid_given_6 | ATU2200006 | true           | +22222222     | http://www.snapshot.cz     |
      | Filter different test Given company 7 | Filter_c7@tenants.biz | Filter_c7t | Filter_salesforceid_given_7 | ATU2200007 | false          | +22222222     | http://www.snapshot.travel |

    When List of customerProperties is got for customer with code "c1t" with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customerProperties returned
    And There are customerProperties with following property names returned in order: <expected_codes>

    Examples:
      | limit | cursor | returned | filter                                     | sort  | sort_desc | expected_codes                                             |
      | 5     | 0      | 5        | company_name=='Filter test*'               | code  |           | Filter_c1t, Filter_c2t, Filter_c3t, Filter_c4t, Filter_c5t |
      | 5     | 0      | 5        | company_name=='Filter test*'               |       | code      | Filter_c5t, Filter_c4t, Filter_c3t, Filter_c2t, Filter_c1t |
      | 5     | 2      | 3        | company_name=='Filter test*'               | code  |           | Filter_c3t, Filter_c4t, Filter_c5t                         |
      | 5     | 2      | 3        | company_name=='Filter test*'               |       | code      | Filter_c3t, Filter_c2t, Filter_c1t                         |
      | 5     | 3      | 2        | company_name=='Filter test*'               | code  |           | Filter_c4t, Filter_c5t                                     |
      | /null | /null  | 1        | code==Filter_c7t                           | /null | /null     | Filter_c7t                                                 |
      | /null | /null  | 2        | code==Filter_c* and phone==+22222222       | code  | /null     | Filter_c6t, Filter_c7t                                     |
      | /null | /null  | 1        | email==Filter_c1@tenants.biz               | /null | /null     | Filter_c1t                                                 |
      | /null | /null  | 1        | salesforce_id==Filter_salesforceid_given_2 | /null | /null     | Filter_c2t                                                 |
      | /null | /null  | 1        | vat_id==ATU22*003                          | /null | /null     | Filter_c3t                                                 |
      | /null | /null  | 1        | is_demo_customer==0                        | /null | /null     | Filter_c7t                                                 |
      | /null | /null  | 1        | website==http://www.snapshot.cz            | /null | /null     | Filter_c6t                                                 |
  #add all fields

    #TODO add test for wrong parameters in url

