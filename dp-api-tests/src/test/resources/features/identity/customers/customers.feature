Feature: Customers

  Background:
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | commercialSubscriptionId  | vatId      |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | subscription_id_given_001 | CZ10000001 |
#      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | subscription_id_given_002 | CZ10000002 |
#      | Given company 3 | c3@tenants.biz | c3t  | salesforceid_given_3 | subscription_id_given_003 | CZ10000003 |


  #TODO - rework - newly created object is in response
  Scenario: Creating Customer without parent with random address

    When Customer is created with random address
      | companyName           | email          | code | salesforceId           | commercialSubscriptionId    | vatId      |
      | Creation test company | s1@tenants.biz | s1t  | salesforceid_created_1 | subscription_id_created_001 | CZ00000001 |
    Then Response code is "201"
    And Body is empty
    And "Location" header is set and contains the same customer


  Scenario Outline: Checking error codes for creating customer
    When File "<json_input_file>" is used for "<method>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                                    | method | error_code | custom_code |
      | create_customer_missing_company_name.json          | POST   | 400        | 53          |
      | create_customer_not_unique_company_name_email.json | POST   | 405        | 51          |
      | create_customer_wrong_email_value.json             | POST   | 405        | 51          |
      | create_customer_wrong_field_email.json             | POST   | 405        | 51          |


  Scenario: Deleting Customer

    When Customer with code "c1t" is deleted
    Then Response code is "204"
    And Body is empty
    And Customer with same id doesn't exist


  Scenario: Checking error code for deletting customer
    When Nonexistent customer id is deleted
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Updating customer
    When Customer with code "c1t" is updated with data
      | companyName   | email   | code   | salesforceId   | commercialSubscriptionId   | vatId   | phone   | website   | sourceCustomer   | notes   |
      | <companyName> | <email> | <code> | <salesforceId> | <commercialSubscriptionId> | <vatId> | <phone> | <website> | <sourceCustomer> | <notes> |
    Then Response code is "204"
    And Body is empty
    And Updated customer with code "c1t" has data
      | companyName   | email   | code   | salesforceId   | commercialSubscriptionId   | vatId   | phone   | website   | sourceCustomer   | notes   |
      | <companyName> | <email> | <code> | <salesforceId> | <commercialSubscriptionId> | <vatId> | <phone> | <website> | <sourceCustomer> | <notes> |

    Examples:
      | companyName | email | code | salesforceId         | commercialSubscriptionId | vatId      | code | phone | website | sourceCustomer | notes |
      |             |       |      | salesforceid_updated | updated_subscription_id  | CZ10000001 |      |       |         |                |       |

  #TODO update customer, activate, inactivate

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
    And Returned customer has company "Given company 1"
    And Returned customer has email "c1@tenants.biz"

  Scenario: Checking error code for getting customer
    When Nonexistent customer id is got
    Then Response code is "404"
    And Custom code is "152"


    @skipped
  Scenario Outline: Getting list of customers
    Given The following customers exist with random address
      | companyName                | email                | code      | salesforceId               | commercialSubscriptionId       | vatId      |
      | List test Given company 1  | list_c1@tenants.biz  | list_c1t  | list_salesforceid_given_1  | list_subscription_id_given_001 | CZ22000001 |
      | List test Given company 2  | list_c2@tenants.biz  | list_c2t  | list_salesforceid_given_2  | list_subscription_id_given_002 | CZ22000002 |
      | List test Given company 3  | list_c3@tenants.biz  | list_c3t  | list_salesforceid_given_3  | list_subscription_id_given_003 | CZ22000003 |
      | List test Given company 4  | list_c4@tenants.biz  | list_c4t  | list_salesforceid_given_4  | list_subscription_id_given_004 | CZ22000004 |
      | List test Given company 5  | list_c5@tenants.biz  | list_c5t  | list_salesforceid_given_5  | list_subscription_id_given_005 | CZ22000005 |
      | List test Given company 6  | list_c6@tenants.biz  | list_c6t  | list_salesforceid_given_6  | list_subscription_id_given_006 | CZ22000006 |
      | List test Given company 7  | list_c7@tenants.biz  | list_c7t  | list_salesforceid_given_7  | list_subscription_id_given_007 | CZ22000007 |
      | List test Given company 8  | list_c8@tenants.biz  | list_c8t  | list_salesforceid_given_8  | list_subscription_id_given_008 | CZ22000008 |
      | List test Given company 9  | list_c9@tenants.biz  | list_c9t  | list_salesforceid_given_9  | list_subscription_id_given_009 | CZ22000009 |
      | List test Given company 10 | list_c10@tenants.biz | list_c10t | list_salesforceid_given_10 | list_subscription_id_given_010 | CZ22000010 |
      | List test Given company 11 | list_c11@tenants.biz | list_c11t | list_salesforceid_given_11 | list_subscription_id_given_011 | CZ22000011 |
      | List test Given company 12 | list_c12@tenants.biz | list_c12t | list_salesforceid_given_12 | list_subscription_id_given_012 | CZ22000012 |
      | List test Given company 13 | list_c13@tenants.biz | list_c13t | list_salesforceid_given_13 | list_subscription_id_given_013 | CZ22000013 |
      | List test Given company 14 | list_c14@tenants.biz | list_c14t | list_salesforceid_given_14 | list_subscription_id_given_014 | CZ22000014 |
      | List test Given company 15 | list_c15@tenants.biz | list_c15t | list_salesforceid_given_15 | list_subscription_id_given_015 | CZ22000015 |
      | List test Given company 16 | list_c16@tenants.biz | list_c16t | list_salesforceid_given_16 | list_subscription_id_given_016 | CZ22000016 |
      | List test Given company 17 | list_c17@tenants.biz | list_c17t | list_salesforceid_given_17 | list_subscription_id_given_017 | CZ22000017 |
      | List test Given company 18 | list_c18@tenants.biz | list_c18t | list_salesforceid_given_18 | list_subscription_id_given_018 | CZ22000018 |
      | List test Given company 19 | list_c19@tenants.biz | list_c19t | list_salesforceid_given_19 | list_subscription_id_given_019 | CZ22000019 |
      | List test Given company 20 | list_c20@tenants.biz | list_c20t | list_salesforceid_given_20 | list_subscription_id_given_020 | CZ22000020 |
      | List test Given company 21 | list_c21@tenants.biz | list_c21t | list_salesforceid_given_21 | list_subscription_id_given_021 | CZ22000021 |

    When List of customers is got with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are <limit> customers returned

    Examples:
      | limit | cursor |
      |       |        |
      | 15    |        |
      |       | 1      |
      | 20    | 0      |
      | 10    | 0      |
      | 5     | 5      |

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