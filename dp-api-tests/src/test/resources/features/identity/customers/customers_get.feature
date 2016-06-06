Feature: Customers get

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 87ae86b7-f5b5-4288-a59e-6bbf9fca4096 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

  @Smoke
  Scenario: Getting customer
    When Customer with customerId "87ae86b7-f5b5-4288-a59e-6bbf9fca4096" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "customer_code"
    And Body contains entity with attribute "name" value "Given company 1"
    And Body contains entity with attribute "email" value "c1@tenants.biz"
    And Body contains entity with attribute "vat_id" value "CZ10000001"

  Scenario: Getting customer with etag
    When Customer with customerId "87ae86b7-f5b5-4288-a59e-6bbf9fca4096" is got with etag
    Then Response code is "304"
    And Body is empty

  Scenario: Getting customer with not current etag
  Customer is got, etag is saved to tmp, then customer vat_id is updated to "CZnotvalidvatid" so etag should change and is got again with previous etag

    When Customer with customerId "87ae86b7-f5b5-4288-a59e-6bbf9fca4096" is got for etag, updated and got with previous etag
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "customer_code"
    And Body contains entity with attribute "name" value "Given company 1"
    And Body contains entity with attribute "email" value "c1@tenants.biz"
    And Body contains entity with attribute "vat_id" value "CZ99999999"

  Scenario: Checking error code for getting customer
    When Customer with customerId "NotExistent" is got
    Then Response code is "404"
    And Custom code is "40402"


  Scenario Outline: Getting list of customers
    Given The following customers exist with random address
      | companyName                | email                | salesforceId               | vatId      | isDemoCustomer | phone         | website                    | timezone     |
      | List test Given company 1  | list_c1@tenants.biz  | list_salesforceid_given_1  | CZ22000001 | true           | +111111111111 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 2  | list_c2@tenants.biz  | list_salesforceid_given_2  | CZ22000002 | true           | +111111111111 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 3  | list_c3@tenants.biz  | list_salesforceid_given_3  | CZ22000003 | true           | +111111111111 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 4  | list_c4@tenants.biz  | list_salesforceid_given_4  | CZ22000004 | true           | +111111111111 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 5  | list_c5@tenants.biz  | list_salesforceid_given_5  | CZ22000005 | true           | +111111111111 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 6  | list_c6@tenants.biz  | list_salesforceid_given_6  | CZ22000006 | true           | +111111111111 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 7  | list_c7@tenants.biz  | list_salesforceid_given_7  | CZ22000007 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 8  | list_c8@tenants.biz  | list_salesforceid_given_8  | CZ22000008 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 9  | list_c9@tenants.biz  | list_salesforceid_given_9  | CZ22000009 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 10 | list_c10@tenants.biz | list_salesforceid_given_10 | CZ22000010 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 11 | list_c11@tenants.biz | list_salesforceid_given_11 | CZ22000011 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 12 | list_c12@tenants.biz | list_salesforceid_given_12 | CZ22000012 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 13 | list_c13@tenants.biz | list_salesforceid_given_13 | CZ22000013 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 14 | list_c14@tenants.biz | list_salesforceid_given_14 | CZ22000014 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 15 | list_c15@tenants.biz | list_salesforceid_given_15 | CZ22000015 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 16 | list_c16@tenants.biz | list_salesforceid_given_16 | CZ22000016 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 17 | list_c17@tenants.biz | list_salesforceid_given_17 | CZ22000017 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 18 | list_c18@tenants.biz | list_salesforceid_given_18 | CZ22000018 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 19 | list_c19@tenants.biz | list_salesforceid_given_19 | CZ22000019 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 20 | list_c20@tenants.biz | list_salesforceid_given_20 | CZ22000020 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 21 | list_c21@tenants.biz | list_salesforceid_given_21 | CZ22000021 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 22 | list_c22@tenants.biz | list_salesforceid_given_22 | CZ22000022 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 23 | list_c23@tenants.biz | list_salesforceid_given_23 | CZ22000023 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 24 | list_c24@tenants.biz | list_salesforceid_given_24 | CZ22000024 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 25 | list_c25@tenants.biz | list_salesforceid_given_25 | CZ22000025 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 26 | list_c26@tenants.biz | list_salesforceid_given_26 | CZ22000026 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 27 | list_c27@tenants.biz | list_salesforceid_given_27 | CZ22000027 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 28 | list_c28@tenants.biz | list_salesforceid_given_28 | CZ22000028 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 29 | list_c29@tenants.biz | list_salesforceid_given_29 | CZ22000029 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 30 | list_c30@tenants.biz | list_salesforceid_given_30 | CZ22000030 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 31 | list_c31@tenants.biz | list_salesforceid_given_31 | CZ22000031 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 32 | list_c32@tenants.biz | list_salesforceid_given_32 | CZ22000032 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 33 | list_c33@tenants.biz | list_salesforceid_given_33 | CZ22000033 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 34 | list_c34@tenants.biz | list_salesforceid_given_34 | CZ22000034 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 35 | list_c35@tenants.biz | list_salesforceid_given_35 | CZ22000035 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 36 | list_c36@tenants.biz | list_salesforceid_given_36 | CZ22000036 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 37 | list_c37@tenants.biz | list_salesforceid_given_37 | CZ22000037 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 38 | list_c38@tenants.biz | list_salesforceid_given_38 | CZ22000038 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 39 | list_c39@tenants.biz | list_salesforceid_given_39 | CZ22000039 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 40 | list_c40@tenants.biz | list_salesforceid_given_40 | CZ22000040 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 41 | list_c41@tenants.biz | list_salesforceid_given_41 | CZ22000041 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 42 | list_c42@tenants.biz | list_salesforceid_given_42 | CZ22000042 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 43 | list_c43@tenants.biz | list_salesforceid_given_43 | CZ22000043 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 44 | list_c44@tenants.biz | list_salesforceid_given_44 | CZ22000044 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 45 | list_c45@tenants.biz | list_salesforceid_given_45 | CZ22000045 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 46 | list_c46@tenants.biz | list_salesforceid_given_46 | CZ22000046 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 47 | list_c47@tenants.biz | list_salesforceid_given_47 | CZ22000047 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 48 | list_c48@tenants.biz | list_salesforceid_given_48 | CZ22000048 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 49 | list_c49@tenants.biz | list_salesforceid_given_49 | CZ22000049 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 50 | list_c50@tenants.biz | list_salesforceid_given_50 | CZ22000051 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 51 | list_c51@tenants.biz | list_salesforceid_given_51 | CZ22000052 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 52 | list_c52@tenants.biz | list_salesforceid_given_52 | CZ22000053 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 53 | list_c53@tenants.biz | list_salesforceid_given_53 | CZ22000054 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 54 | list_c54@tenants.biz | list_salesforceid_given_54 | CZ22000055 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 55 | list_c55@tenants.biz | list_salesforceid_given_55 | CZ22000056 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 56 | list_c56@tenants.biz | list_salesforceid_given_56 | CZ22000057 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 57 | list_c57@tenants.biz | list_salesforceid_given_57 | CZ22000058 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |
      | List test Given company 58 | list_c58@tenants.biz | list_salesforceid_given_58 | CZ22000059 | true           | +420123456789 | http://www.snapshot.travel | Europe/Sofia |


    When List of customers is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Total count is "<total>"
    And There are <returned> customers returned
    And Link header is '<link_header>'

    Examples:
      | limit | cursor | returned | total | link_header                                                                                               |
      | /null |        | 50       | 59    | </identity/customers?limit=50&cursor=50>; rel="next"                                                      |
      | /null | /null  | 50       | 59    | </identity/customers?limit=50&cursor=50>; rel="next"                                                      |
      |       |        | 50       | 59    | </identity/customers?limit=50&cursor=50>; rel="next"                                                      |
      |       | /null  | 50       | 59    | </identity/customers?limit=50&cursor=50>; rel="next"                                                      |
      | 15    |        | 15       | 59    | </identity/customers?limit=15&cursor=15>; rel="next"                                                      |
      |       | 1      | 50       | 59    | </identity/customers?limit=50&cursor=51>; rel="next", </identity/customers?limit=50&cursor=0>; rel="prev" |
      | 20    | 0      | 20       | 59    | </identity/customers?limit=20&cursor=20>; rel="next"                                                      |
      | 10    | 0      | 10       | 59    | </identity/customers?limit=10&cursor=10>; rel="next"                                                      |
      | 5     | 10     | 5        | 59    | </identity/customers?limit=5&cursor=15>; rel="next", </identity/customers?limit=5&cursor=5>; rel="prev"   |

    #TODO test filter, sort with different values

  Scenario Outline: Checking error codes for getting list of customers
    When List of customers is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter   | sort        | sort_desc   | response_code | custom_code |
      #limit and cursor
      | /null       | -1     | /null    | /null       | /null       | 400           | 63          |
      |             | -1     | /null    | /null       | /null       | 400           | 63          |
      | /null       | text   | /null    | /null       | /null       | 400           | 63          |
      |             | text   | /null    | /null       | /null       | 400           | 63          |
      | -1          |        | /null    | /null       | /null       | 400           | 63          |
      | -1          | /null  | /null    | /null       | /null       | 400           | 63          |
      | 201         | /null  | /null    | /null       | /null       | 400           | 63          |
      | 21474836470 | /null  | /null    | /null       | /null       | 400           | 63          |
      | text        |        | /null    | /null       | /null       | 400           | 63          |
      | text        | /null  | /null    | /null       | /null       | 400           | 63          |
      | 10          | -1     | /null    | /null       | /null       | 400           | 63          |
      | text        | 0      | /null    | /null       | /null       | 400           | 63          |
      | 10          | text   | /null    | /null       | /null       | 400           | 63          |

      #filtering and sorting
      | 10          | 0      | /null    | name        | name        | 400           | 64          |
      | 10          | 0      | /null    | /null       | nonexistent | 400           | 63          |
      | 10          | 0      | /null    | nonexistent | /null       | 400           | 63          |
      | 10          | 0      | code==   | /null       | /null       | 400           | 63          |
      | 10          | 0      | vat==CZ* | /null       | /null       | 400           | 63          |

  Scenario Outline: Filtering list of customers
    Given The following customers exist with random address
      | companyName                           | email                 | salesforceId                | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | Filter test Given company 1           | Filter_c1@tenants.biz | Filter_salesforceid_given_1 | CZ12345671 | true           | +111111111111 | http://www.snapshot.travel | Europe/Berlin |
      | Filter test Given company 2           | Filter_c2@tenants.biz | Filter_salesforceid_given_2 | CZ12345672 | true           | +111111111111 | http://www.snapshot.travel | Europe/Berlin |
      | Filter test Given company 3           | Filter_c3@tenants.biz | Filter_salesforceid_given_3 | CZ12345673 | true           | +111111111111 | http://www.snapshot.travel | Europe/Berlin |
      | Filter test Given company 4           | Filter_c4@tenants.biz | Filter_salesforceid_given_4 | CZ12345674 | true           | +111111111111 | http://www.snapshot.travel | Europe/Berlin |
      | Filter test Given company 5           | Filter_c5@tenants.biz | Filter_salesforceid_given_5 | CZ12345675 | true           | +111111111111 | http://www.snapshot.travel | Europe/Berlin |
      | Filter different test Given company 6 | Filter_c6@tenants.biz | Filter_salesforceid_given_6 | CZ12345676 | true           | +22222222     | http://www.snapshot.cz     | Europe/Berlin |
      | Filter different test Given company 7 | Filter_c7@tenants.biz | Filter_salesforceid_given_7 | CZ12345677 | false          | +22222222     | http://www.snapshot.travel | Europe/Berlin |

    When List of customers is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customers returned
    And There are customers with following emails returned in order: "<expected_emails>"
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                                     | sort  | sort_desc | expected_emails                                       |
      | 5     | 0      | 5        | 5     | name=='Filter test*'                       | email |           | Filter_c1, Filter_c2, Filter_c3, Filter_c4, Filter_c5 |
      | 5     | 0      | 5        | 5     | name=='Filter test*'                       |       | email     | Filter_c5, Filter_c4, Filter_c3, Filter_c2, Filter_c1 |
      | 5     | 2      | 3        | 5     | name=='Filter test*'                       | email |           | Filter_c3, Filter_c4, Filter_c5                       |
      | 5     | 2      | 3        | 5     | name=='Filter test*'                       |       | email     | Filter_c3, Filter_c2, Filter_c1                       |
      | 5     | 3      | 2        | 5     | name=='Filter test*'                       | email |           | Filter_c4, Filter_c5                                  |
      | /null | /null  | 1        | 1     | email=='Filter_c7*'                        | /null | /null     | Filter_c7                                             |
      | /null | /null  | 2        | 2     | email==Filter_c* and phone==+22222222      | email | /null     | Filter_c6, Filter_c7                                  |
      | /null | /null  | 1        | 1     | email==Filter_c1@tenants.biz               | /null | /null     | Filter_c1                                             |
      | /null | /null  | 1        | 1     | salesforce_id==Filter_salesforceid_given_2 | /null | /null     | Filter_c2                                             |
      | /null | /null  | 1        | 1     | vat_id==CZ*73                              | /null | /null     | Filter_c3                                             |
      | /null | /null  | 1        | 1     | is_demo_customer==0                        | /null | /null     | Filter_c7                                             |
      | /null | /null  | 1        | 1     | website==http://www.snapshot.cz            | /null | /null     | Filter_c6                                             |
  #add all fields

    #TODO add test for wrong parameters in url

