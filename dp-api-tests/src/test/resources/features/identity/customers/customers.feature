Feature: Customers

  #TODO add etag things to get/update/create

  Background:
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           |
#      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 |CZ10000002 |
#      | Given company 3 | c3@tenants.biz | c3t  | salesforceid_given_3 |CZ10000003 |


  Scenario: Creating Customer without parent with random address

    When Customer is created with random address
      | companyName           | email          | code | salesforceId           | vatId      | isDemoCustomer |
      | Creation test company | s1@tenants.biz | s1t  | salesforceid_created_1 | CZ00000001 | true           |
    Then Response code is "201"
    And Body contains customer type with "company_name" value "Creation test company"
    And Body contains customer type with "email" value "s1@tenants.biz"
    And Body contains customer type with "code" value "s1t"
    And "Location" header is set and contains the same customer


  Scenario Outline: Checking error codes for creating customer
    When File "<json_input_file>" is used for "<method>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                                    | method | error_code | custom_code |
      | create_customer_missing_company_name.json          | POST   | 400        | 53          |
      | create_customer_not_unique_company_name_email.json | POST   | 400        | 62          |
      | create_customer_wrong_email_value.json             | POST   | 400        | 59          |
      | create_customer_wrong_field_email.json             | POST   | 400        | 56          |

    #add wrong web, null customer code,


  Scenario: Deleting Customer

    When Customer with code "c1t" is deleted
    Then Response code is "204"
    And Body is empty
    And Customer with same id doesn't exist


  Scenario: Checking error code for deleting customer
    When Nonexistent customer id is deleted
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Updating customer
    When Customer with code "c1t" is updated with data
      | companyName   | email   | code   | salesforceId   | vatId   | phone   | website   | sourceCustomer   | notes   |
      | <companyName> | <email> | <code> | <salesforceId> | <vatId> | <phone> | <website> | <sourceCustomer> | <notes> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated customer with code "<code>" has data
      | companyName   | email   | code   | salesforceId   | vatId   | phone   | website   | sourceCustomer   | notes   |
      | <companyName> | <email> | <code> | <salesforceId> | <vatId> | <phone> | <website> | <sourceCustomer> | <notes> |

    Examples:
      | companyName | email | code             | salesforceId         | vatId      | phone         | website        | sourceCustomer | notes |
      |             |       | c1t              | salesforceid_updated | CZ10000001 |               |                |                |       |
      |             |       | c1t              |                      |            | +420123456789 | http://test.cz |                |       |
      |             |       | UPDATED_CODE_c1t |                      |            |               |                |                |       |


  #TODO update cutomer with not matched etag/empty etag/missing etag
  # update with error fields, bad values, missing fields

  Scenario: Updating customer with outdated etag
    When Customer with code "c1t" is updated with data if updated before
      | companyName | email | code | salesforceId | vatId | phone | website        | sourceCustomer | notes |
      |             |       |      |              |       |       | http://test.cz |                |       |
    Then Response code is "412"
    And Custom code is "57"

  Scenario: Customer is activated
    When Customer with code "c1t" is activated
    Then Response code is "204"
    And Body is empty
    And Customer with code "c1t" is active

    #error codes

  Scenario: Customer is inactivated
    Given Customer with code "c1t" is activated
    When Customer with code "c1t" is inactivated
    Then Response code is "204"
    And Body is empty
    And Customer with code "c1t" is not active


    #error codes

  #Scenario Outline: Checking error codes for updating customer
    #When File "<json_input_file>" is used for "<method>"
    #Then Response code is "<error_code>"
    #And Custom code is "<custom_code>"

   # Examples:
   #   | json_input_file                           | method | error_code | custom_code |
   #   | create_customer_missing_company_name.json | POST   | 405        | 51          |
   #   | create_customer_wrong_field_name.json     | POST   | 405        | 51          |
   #   | create_customer_wrong_field_name.json     | POST   | 405        | 51          |
   #   | create_customer_faulty_address.json       | POST   | 405        | 51          |

  #field cannot be updated
  #mandatory atribute is empty
  #wrong field name
  #id doesn't exist
  #too long string in attribute
  #invalid value in attribute


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
    And Body contains customer type with "vat_id" value "CZnotvalidvatid"

  Scenario: Checking error code for getting customer
    When Nonexistent customer id is got
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Getting list of customers
    Given The following customers exist with random address
      | companyName                | email                | code      | salesforceId               | vatId      | isDemoCustomer |
      | List test Given company 1  | list_c1@tenants.biz  | list_c1t  | list_salesforceid_given_1  | CZ22000001 | true           |
      | List test Given company 2  | list_c2@tenants.biz  | list_c2t  | list_salesforceid_given_2  | CZ22000002 | true           |
      | List test Given company 3  | list_c3@tenants.biz  | list_c3t  | list_salesforceid_given_3  | CZ22000003 | true           |
      | List test Given company 4  | list_c4@tenants.biz  | list_c4t  | list_salesforceid_given_4  | CZ22000004 | true           |
      | List test Given company 5  | list_c5@tenants.biz  | list_c5t  | list_salesforceid_given_5  | CZ22000005 | true           |
      | List test Given company 6  | list_c6@tenants.biz  | list_c6t  | list_salesforceid_given_6  | CZ22000006 | true           |
      | List test Given company 7  | list_c7@tenants.biz  | list_c7t  | list_salesforceid_given_7  | CZ22000007 | true           |
      | List test Given company 8  | list_c8@tenants.biz  | list_c8t  | list_salesforceid_given_8  | CZ22000008 | true           |
      | List test Given company 9  | list_c9@tenants.biz  | list_c9t  | list_salesforceid_given_9  | CZ22000009 | true           |
      | List test Given company 10 | list_c10@tenants.biz | list_c10t | list_salesforceid_given_10 | CZ22000010 | true           |
      | List test Given company 11 | list_c11@tenants.biz | list_c11t | list_salesforceid_given_11 | CZ22000011 | true           |
      | List test Given company 12 | list_c12@tenants.biz | list_c12t | list_salesforceid_given_12 | CZ22000012 | true           |
      | List test Given company 13 | list_c13@tenants.biz | list_c13t | list_salesforceid_given_13 | CZ22000013 | true           |
      | List test Given company 14 | list_c14@tenants.biz | list_c14t | list_salesforceid_given_14 | CZ22000014 | true           |
      | List test Given company 15 | list_c15@tenants.biz | list_c15t | list_salesforceid_given_15 | CZ22000015 | true           |
      | List test Given company 16 | list_c16@tenants.biz | list_c16t | list_salesforceid_given_16 | CZ22000016 | true           |
      | List test Given company 17 | list_c17@tenants.biz | list_c17t | list_salesforceid_given_17 | CZ22000017 | true           |
      | List test Given company 18 | list_c18@tenants.biz | list_c18t | list_salesforceid_given_18 | CZ22000018 | true           |
      | List test Given company 19 | list_c19@tenants.biz | list_c19t | list_salesforceid_given_19 | CZ22000019 | true           |
      | List test Given company 20 | list_c20@tenants.biz | list_c20t | list_salesforceid_given_20 | CZ22000020 | true           |
      | List test Given company 21 | list_c21@tenants.biz | list_c21t | list_salesforceid_given_21 | CZ22000021 | true           |

    When List of customers is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customers returned

    Examples:
      | limit | cursor | returned |
      |       |        | 20       |
      | 15    |        | 15       |
      |       | 1      | 20       |
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