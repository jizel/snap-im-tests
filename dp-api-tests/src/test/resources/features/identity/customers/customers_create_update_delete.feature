Feature: customers_create_update_delete

  #TODO add etag things to get/update/create

  Background:
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |

  Scenario: Creating Customer without parent with random address

    When Customer is created with random address
      | companyName           | email          | code | salesforceId           | vatId      | isDemoCustomer | phone         | website                    |
      | Creation test company | s1@tenants.biz | s1t  | salesforceid_created_1 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel |
    Then Response code is "201"
    And Body contains customer type with "company_name" value "Creation test company"
    And Body contains customer type with "email" value "s1@tenants.biz"
    And Body contains customer type with "code" value "s1t"
    And "Location" header is set and contains the same customer


  Scenario Outline: Checking error codes for creating customer

    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                                                                 | method | module   | url                 | error_code | custom_code |
      | /messages/identity/customers/create_customer_missing_company_name.json          | POST   | identity | /identity/customers | 400        | 53          |
      | /messages/identity/customers/create_customer_not_unique_company_name_email.json | POST   | identity | /identity/customers | 400        | 62          |
      | /messages/identity/customers/create_customer_not_unique_code_email.json         | POST   | identity | /identity/customers | 400        | 62          |
      | /messages/identity/customers/create_customer_wrong_email_value.json             | POST   | identity | /identity/customers | 400        | 59          |
      | /messages/identity/customers/create_customer_wrong_field_email.json             | POST   | identity | /identity/customers | 400        | 56          |
      | /messages/identity/customers/create_customer_wrong_vatid_value.json             | POST   | identity | /identity/customers | 400        | 59          |
      | /messages/identity/customers/create_customer_wrong_country_value.json           | POST   | identity | /identity/customers | 400        | 59          |
      | /messages/identity/customers/create_customer_wrong_phone_value.json             | POST   | identity | /identity/customers | 400        | 59          |
      | /messages/identity/customers/create_customer_wrong_website_value.json           | POST   | identity | /identity/customers | 400        | 59          |

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
  # update nonexistent field

  Scenario: Updating customer with outdated etag
    When Customer with code "c1t" is updated with data if updated before
      | companyName | email | code | salesforceId | vatId | phone | website        | sourceCustomer | notes |
      |             |       |      |              |       |       | http://test.cz |                |       |
    Then Response code is "412"
    And Custom code is "57"

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