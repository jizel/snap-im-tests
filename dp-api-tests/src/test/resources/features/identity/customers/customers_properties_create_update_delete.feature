@skipped
Feature: customers_properties_create_update_delete

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

  Scenario: Adding property to customer with some type valid from date to date

    When Property with code "p2_code" is added to customer with code "c1t" with type "anchor" from "2015-01-01" to "2015-10-31"
    Then Response code is "201"
    And Body contains entity with attribute "property_name" value "p2_name"
    And Body contains entity with attribute "validFrom" value "2015-01-01"
    And Body contains entity with attribute "validTo" value "2015-10-31"
    And Body contains entity with attribute "type" value "anchor"
    And "Location" header is set and contains the same customerProperty
    And Etag header is present


  @skipped
  Scenario Outline: Checking error codes for creating customerProperty

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

    #add wrong dates, wrong type, not unique type, more anchor for one property, ...


  Scenario: Deleting property from customer with some type

    When Property with code "p1_code" is deleted from customer with code "c1t" with type "anchor"
    Then Response code is "204"
    And Body is empty
    And customerProperty with same id doesn't exist


  @skipped
  Scenario: Checking error code for deleting customer
    When Nonexistent customerProperty id is deleted
    Then Response code is "204"


  @skipped
  Scenario Outline: Updating customerProperty
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

  @skipped
  Scenario: Updating customerProperty with outdated etag
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
