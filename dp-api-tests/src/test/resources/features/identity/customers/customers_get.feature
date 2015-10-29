Feature: customers_get

  #TODO add etag things to get/update/create

  Background:
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |
#      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 |CZ10000002 || +420123456789|http://www.snapshot.travel|
#      | Given company 3 | c3@tenants.biz | c3t  | salesforceid_given_3 |CZ10000003 || +420123456789|http://www.snapshot.travel|


  Scenario: Getting customer
    When Customer with code "c1t" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains customer type with "code" value "c1t"
    And Body contains customer type with "company_name" value "Given company 1"
    And Body contains customer type with "email" value "c1@tenants.biz"
    And Body contains customer type with "vat_id" value "CZ10000001"

  Scenario: Getting customer with etag
    When Customer with code "c1t" is got with etag
    Then Response code is "304"
    And Body is empty

  Scenario: Getting customer with not current etag
  Customer is got, etag is saved to tmp, then customer vat_id is updated to "CZnotvalidvatid" so etag should change and is got again with previous etag

    When Customer with code "c1t" is got for etag, updated and got with previous etag
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains customer type with "code" value "c1t"
    And Body contains customer type with "company_name" value "Given company 1"
    And Body contains customer type with "email" value "c1@tenants.biz"
    And Body contains customer type with "vat_id" value "CZ99999999"

  Scenario: Checking error code for getting customer
    When Nonexistent customer id is got
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Getting list of customers
    Given The following customers exist with random address
      | companyName                | email                | code      | salesforceId               | vatId      | isDemoCustomer | phone         | website                    |
      | List test Given company 1  | list_c1@tenants.biz  | list_c1t  | list_salesforceid_given_1  | CZ22000001 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 2  | list_c2@tenants.biz  | list_c2t  | list_salesforceid_given_2  | CZ22000002 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 3  | list_c3@tenants.biz  | list_c3t  | list_salesforceid_given_3  | CZ22000003 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 4  | list_c4@tenants.biz  | list_c4t  | list_salesforceid_given_4  | CZ22000004 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 5  | list_c5@tenants.biz  | list_c5t  | list_salesforceid_given_5  | CZ22000005 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 6  | list_c6@tenants.biz  | list_c6t  | list_salesforceid_given_6  | CZ22000006 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 7  | list_c7@tenants.biz  | list_c7t  | list_salesforceid_given_7  | CZ22000007 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 8  | list_c8@tenants.biz  | list_c8t  | list_salesforceid_given_8  | CZ22000008 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 9  | list_c9@tenants.biz  | list_c9t  | list_salesforceid_given_9  | CZ22000009 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 10 | list_c10@tenants.biz | list_c10t | list_salesforceid_given_10 | CZ22000010 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 11 | list_c11@tenants.biz | list_c11t | list_salesforceid_given_11 | CZ22000011 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 12 | list_c12@tenants.biz | list_c12t | list_salesforceid_given_12 | CZ22000012 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 13 | list_c13@tenants.biz | list_c13t | list_salesforceid_given_13 | CZ22000013 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 14 | list_c14@tenants.biz | list_c14t | list_salesforceid_given_14 | CZ22000014 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 15 | list_c15@tenants.biz | list_c15t | list_salesforceid_given_15 | CZ22000015 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 16 | list_c16@tenants.biz | list_c16t | list_salesforceid_given_16 | CZ22000016 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 17 | list_c17@tenants.biz | list_c17t | list_salesforceid_given_17 | CZ22000017 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 18 | list_c18@tenants.biz | list_c18t | list_salesforceid_given_18 | CZ22000018 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 19 | list_c19@tenants.biz | list_c19t | list_salesforceid_given_19 | CZ22000019 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 20 | list_c20@tenants.biz | list_c20t | list_salesforceid_given_20 | CZ22000020 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 21 | list_c21@tenants.biz | list_c21t | list_salesforceid_given_21 | CZ22000021 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 22 | list_c22@tenants.biz | list_c22t | list_salesforceid_given_22 | CZ22000022 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 23 | list_c23@tenants.biz | list_c23t | list_salesforceid_given_23 | CZ22000023 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 24 | list_c24@tenants.biz | list_c24t | list_salesforceid_given_24 | CZ22000024 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 25 | list_c25@tenants.biz | list_c25t | list_salesforceid_given_25 | CZ22000025 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 26 | list_c26@tenants.biz | list_c26t | list_salesforceid_given_26 | CZ22000026 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 27 | list_c27@tenants.biz | list_c27t | list_salesforceid_given_27 | CZ22000027 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 28 | list_c28@tenants.biz | list_c28t | list_salesforceid_given_28 | CZ22000028 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 29 | list_c29@tenants.biz | list_c29t | list_salesforceid_given_29 | CZ22000029 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 30 | list_c30@tenants.biz | list_c30t | list_salesforceid_given_30 | CZ22000030 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 31 | list_c31@tenants.biz | list_c31t | list_salesforceid_given_31 | CZ22000031 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 32 | list_c32@tenants.biz | list_c32t | list_salesforceid_given_32 | CZ22000032 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 33 | list_c33@tenants.biz | list_c33t | list_salesforceid_given_33 | CZ22000033 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 34 | list_c34@tenants.biz | list_c34t | list_salesforceid_given_34 | CZ22000034 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 35 | list_c35@tenants.biz | list_c35t | list_salesforceid_given_35 | CZ22000035 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 36 | list_c36@tenants.biz | list_c36t | list_salesforceid_given_36 | CZ22000036 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 37 | list_c37@tenants.biz | list_c37t | list_salesforceid_given_37 | CZ22000037 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 38 | list_c38@tenants.biz | list_c38t | list_salesforceid_given_38 | CZ22000038 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 39 | list_c39@tenants.biz | list_c39t | list_salesforceid_given_39 | CZ22000039 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 40 | list_c40@tenants.biz | list_c40t | list_salesforceid_given_40 | CZ22000040 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 41 | list_c41@tenants.biz | list_c41t | list_salesforceid_given_41 | CZ22000041 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 42 | list_c42@tenants.biz | list_c42t | list_salesforceid_given_42 | CZ22000042 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 43 | list_c43@tenants.biz | list_c43t | list_salesforceid_given_43 | CZ22000043 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 44 | list_c44@tenants.biz | list_c44t | list_salesforceid_given_44 | CZ22000044 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 45 | list_c45@tenants.biz | list_c45t | list_salesforceid_given_45 | CZ22000045 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 46 | list_c46@tenants.biz | list_c46t | list_salesforceid_given_46 | CZ22000046 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 47 | list_c47@tenants.biz | list_c47t | list_salesforceid_given_47 | CZ22000047 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 48 | list_c48@tenants.biz | list_c48t | list_salesforceid_given_48 | CZ22000048 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 49 | list_c49@tenants.biz | list_c49t | list_salesforceid_given_49 | CZ22000049 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 50 | list_c50@tenants.biz | list_c50t | list_salesforceid_given_50 | CZ22000051 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 51 | list_c51@tenants.biz | list_c51t | list_salesforceid_given_51 | CZ22000052 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 52 | list_c52@tenants.biz | list_c52t | list_salesforceid_given_52 | CZ22000053 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 53 | list_c53@tenants.biz | list_c53t | list_salesforceid_given_53 | CZ22000054 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 54 | list_c54@tenants.biz | list_c54t | list_salesforceid_given_54 | CZ22000055 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 55 | list_c55@tenants.biz | list_c55t | list_salesforceid_given_55 | CZ22000056 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 56 | list_c56@tenants.biz | list_c56t | list_salesforceid_given_56 | CZ22000057 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 57 | list_c57@tenants.biz | list_c57t | list_salesforceid_given_57 | CZ22000058 | true           | +420123456789 | http://www.snapshot.travel |
      | List test Given company 58 | list_c58@tenants.biz | list_c58t | list_salesforceid_given_58 | CZ22000059 | true           | +420123456789 | http://www.snapshot.travel |


    When List of customers is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customers returned

    Examples:
      | limit | cursor | returned |
      |       |        | 50       |
      | 15    |        | 15       |
      |       | 1      | 50       |
      | 20    | 0      | 20       |
      | 10    | 0      | 10       |
      | 5     | 5      | 5        |

  #given hodne hodnot, aby se dalo testovat
    #test limit, cursor, filter, sort with different values

  Scenario Outline: Checking error codes for getting list of customers
    When List of customers is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit | cursor | response_code | custom_code |
      |       | -1     | 400           | 63          |
      |       | text   | 400           | 63          |
      | -1    |        | 400           | 63          |
      | text  |        | 400           | 63          |
      | 10    | -1     | 400           | 63          |
      | text  | 0      | 400           | 63          |
      | 10    | text   | 400           | 63          |

  #negative values, strings, empty
  #wrong parameters (variables: parameter name, parameter value),