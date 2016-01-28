Feature: customers_create_update_delete

  #TODO add etag things to get/update/create
  Background: 
    Given Database is cleaned
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given All users are removed for customers with codes: c1t, c2t

  Scenario: Creating Customer without parent with random address
    When Customer is created with random address
      | companyName           | email          | code | salesforceId           | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | Creation test company | s1@tenants.biz | s1t  | salesforceid_created_1 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Then Response code is "201"
    And Body contains entity with attribute "company_name" value "Creation test company"
    And Body contains entity with attribute "email" value "s1@tenants.biz"
    And Body contains entity with attribute "code" value "s1t"
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
    Then Response code is "204"

  Scenario Outline: Updating customer
    Given The following customers with codes don't exist
      | UPDATED_CODE_c1t |
    When Customer with code "c1t" is updated with data
      | companyName   | email   | code   | salesforceId   | vatId   | phone   | website   | notes   | timezone   |
      | <companyName> | <email> | <code> | <salesforceId> | <vatId> | <phone> | <website> | <notes> | <timezone> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated customer with code "<code>" has data
      | companyName   | email   | code   | salesforceId   | vatId   | phone   | website   | notes   | timezone   |
      | <companyName> | <email> | <code> | <salesforceId> | <vatId> | <phone> | <website> | <notes> | <timezone> |

    Examples: 
      | companyName | email | code             | salesforceId         | vatId      | phone         | website        | notes | timezone      |
      | /null       | /null | c1t              | salesforceid_updated | CZ10000001 | /null         | /null          | /null | Europe/Prague |
      | /null       | /null | c1t              | /null                | /null      | +420123456789 | http://test.cz | /null | /null         |
      | /null       | /null | UPDATED_CODE_c1t | /null                | /null      | /null         | /null          | /null | /null         |

  #TODO update cutomer with not matched etag/empty etag/missing etag
  # update with error fields, bad values, missing fields
  # update nonexistent field
  Scenario: Updating customer with outdated etag
    When Customer with code "c1t" is updated with data if updated before
      | companyName | email | code | salesforceId | vatId | phone | website        | notes |
      |             |       |      |              |       |       | http://test.cz |       |
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

  Scenario Outline: Validate that customer regions belong to the correct country
    When A customer from country "<country>" region "<region>" code "<code>" email "<email>" is created
    Then Content type is "application/json"
    And Response code is 201
    And Body contains entity with attribute "address.region" value "<region>"

    Examples: 
      | country | region         | code       | email           |
      | US      | Alabama        | custcode1  | mail1@mail.com  |
      | US      | Alaska         | custcode2  | mail2@mail.com  |
      | US      | Arizona        | custcode3  | mail3@mail.com  |
      | US      | Arkansas       | custcode4  | mail4@mail.com  |
      | US      | California     | custcode5  | mail5@mail.com  |
      | US      | Colorado       | custcode6  | mail6@mail.com  |
      | US      | Connecticut    | custcode7  | mail7@mail.com  |
      | US      | Delaware       | custcode8  | mail8@mail.com  |
      | US      | Florida        | custcode9  | mail9@mail.com  |
      | US      | Georgia        | custcode10 | mail10@mail.com |
      | US      | Hawaii         | custcode11 | mail11@mail.com |
      | US      | Idaho          | custcode12 | mail12@mail.com |
      | US      | Illinois       | custcode13 | mail13@mail.com |
      | US      | Indiana        | custcode14 | mail14@mail.com |
      | US      | Iowa           | custcode15 | mail15@mail.com |
      | US      | Kansas         | custcode16 | mail16@mail.com |
      | US      | Kentucky       | custcode17 | mail17@mail.com |
      | US      | Louisiana      | custcode18 | mail18@mail.com |
      | US      | Maine          | custcode19 | mail19@mail.com |
      | US      | Maryland       | custcode20 | mail20@mail.com |
      | US      | Massachusetts  | custcode21 | mail21@mail.com |
      | US      | Michigan       | custcode22 | mail22@mail.com |
      | US      | Minnesota      | custcode23 | mail23@mail.com |
      | US      | Mississippi    | custcode24 | mail24@mail.com |
      | US      | Missouri       | custcode25 | mail25@mail.com |
      | US      | Montana        | custcode26 | mail26@mail.com |
      | US      | Nebraska       | custcode27 | mail27@mail.com |
      | US      | Nevada         | custcode28 | mail28@mail.com |
      | US      | New Hampshire  | custcode29 | mail29@mail.com |
      | US      | New Jersey     | custcode30 | mail30@mail.com |
      | US      | New Mexico     | custcode31 | mail31@mail.com |
      | US      | New York       | custcode32 | mail32@mail.com |
      | US      | North Carolina | custcode33 | mail33@mail.com |
      | US      | North Dakota   | custcode34 | mail34@mail.com |
      | US      | Ohio           | custcode35 | mail35@mail.com |
      | US      | Oklahoma       | custcode36 | mail36@mail.com |
      | US      | Oregon         | custcode37 | mail37@mail.com |
      | US      | Pennsylvania   | custcode38 | mail38@mail.com |
      | US      | Rhode Island   | custcode39 | mail39@mail.com |
      | US      | South Carolina | custcode40 | mail40@mail.com |
      | US      | South Dakota   | custcode41 | mail41@mail.com |
      | US      | Tennessee      | custcode42 | mail42@mail.com |
      | US      | Texas          | custcode43 | mail43@mail.com |
      | US      | Utah           | custcode44 | mail44@mail.com |
      | US      | Vermont        | custcode45 | mail45@mail.com |
      | US      | Virginia       | custcode46 | mail46@mail.com |
      | US      | Washington     | custcode47 | mail47@mail.com |
      | US      | West Virginia  | custcode48 | mail48@mail.com |
      | US      | Wisconsin      | custcode49 | mail49@mail.com |
      | US      | Wyoming        | custcode50 | mail50@mail.com |

  Scenario Outline: Checking error codes for regions
    When A property from country "<country>" region "<region>" code "<code>" email "<email>" is created
    Then Content type is "application/json"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "message" value "Region with identifier <region> was not found."

    Examples: 
      | country | region    | code       | email           | response_code | custom_code |
      | DE      | invalid   | propcode8  | mail8@mail.com  | 400           | 63          |
      | BG      | invalid   | propcode9  | mail9@mail.com  | 400           | 63          |
      | US      | invalid   | propcode10 | mail10@mail.com | 400           | 63          |
      | CZ      | invalid   | propcode11 | mail11@mail.com | 400           | 63          |
      | AU      | invalid   | propcode12 | mail12@mail.com | 400           | 63          |
      | US      | bg_region | propcode13 | mail13@mail.com | 400           | 63          |
      | CZ      | us_region | propcode14 | mail14@mail.com | 400           | 63          |
      | AU      | bg_region | propcode15 | mail15@mail.com | 400           | 63          |
