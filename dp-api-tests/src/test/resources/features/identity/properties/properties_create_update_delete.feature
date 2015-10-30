Feature: properties_create_update_delete

  #TODO add etag things to get/update/create

  Background:
    Given The following properties exist with random address and billing address
    | salesforceId          | propertyName  | propertyCode  | website                       | email             | vatId         | isDemoProperty    | timezone  |
    | salesforceid_1        | p1_name       | p1_code       | http://www.snapshot.travel    | p1@tenants.biz    | CZ10000001    | true              | UTC+01:00 |

  Scenario: Creating property without parent with random address

    When Property is created with random address and billing address
    | salesforceId          | propertyName  | propertyCode  | website                       | email             | vatId         | isDemoProperty    | timezone  |
    | salesforceid_n1       | pn1_name      | pn1_code      | http://www.snapshot.travel    | pn1@tenants.biz    | CZ100000n1   | true              | UTC+01:00 |

    Then Response code is "201"
    And Body contains property with attribute "property_code" value "pn1_code"
    And Body contains property with attribute "property_name" value "pn1_name"
    And Body contains property with attribute "email" value "pn1@tenants.biz"
    And "Location" header is set and contains the same property


  Scenario Outline: Checking response codes when creating property

    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                                                                  | method | module   | url                  | response_code  | custom_code |
      
      # missing required fields
      | /messages/identity/properties/create_property_missing_property_name.json         | POST   | identity | /identity/properties | 400            | 53          |
      | /messages/identity/properties/create_property_missing_property_code.json         | POST   | identity | /identity/properties | 400            | 53          |
      | /messages/identity/properties/create_property_missing_email.json                 | POST   | identity | /identity/properties | 400            | 53          |
      | /messages/identity/properties/create_property_missing_is_demo_property.json      | POST   | identity | /identity/properties | 400            | 53          |
      | /messages/identity/properties/create_property_missing_timezone.json              | POST   | identity | /identity/properties | 400            | 53          |

      # invalid field values
      | /messages/identity/properties/create_property_invalid_website.json               | POST   | identity | /identity/properties | 400            | 59          |
      | /messages/identity/properties/create_property_long_website.json                  | POST   | identity | /identity/properties | 400            | 60          |
      | /messages/identity/properties/create_property_long_property_code.json            | POST   | identity | /identity/properties | 400            | 60          |
      | /messages/identity/properties/create_property_long_property_name.json            | POST   | identity | /identity/properties | 400            | 60          |
      | /messages/identity/properties/create_property_invalid_email.json                 | POST   | identity | /identity/properties | 400            | 59          |
      | /messages/identity/properties/create_property_long_email.json                    | POST   | identity | /identity/properties | 400            | 60          |
      | /messages/identity/properties/create_property_invalid_vat_id.json                | POST   | identity | /identity/properties | 400            | 59          |
      | /messages/identity/properties/create_property_long_vat_id.json                   | POST   | identity | /identity/properties | 400            | 60          |
      | /messages/identity/properties/create_property_invalid_is_demo_property.json      | POST   | identity | /identity/properties | 400            | 59          |

      # address required fields
      | /messages/identity/properties/create_property_missing_address_line_1.json        | POST   | identity | /identity/properties | 400            | 53          |
      | /messages/identity/properties/create_property_missing_city.json                  | POST   | identity | /identity/properties | 400            | 53          |
      | /messages/identity/properties/create_property_missing_zipcode.json               | POST   | identity | /identity/properties | 400            | 53          |

      # invalid address field values
      | /messages/identity/properties/create_property_long_address_line_1.json           | POST   | identity | /identity/properties | 400            | 60          |
      | /messages/identity/properties/create_property_long_address_line_2.json           | POST   | identity | /identity/properties | 400            | 60          |
      | /messages/identity/properties/create_property_long_city.json                     | POST   | identity | /identity/properties | 400            | 60          |
      | /messages/identity/properties/create_property_long_zipcode.json                  | POST   | identity | /identity/properties | 400            | 60          |
      | /messages/identity/properties/create_property_invalid_country.json               | POST   | identity | /identity/properties | 400            | 59          |
      | /messages/identity/properties/create_property_long_country.json                  | POST   | identity | /identity/properties | 400            | 60          |


  Scenario: Deleting Property
    When Property with code "p1_code" is deleted
    Then Response code is "204"
    And Body is empty
    And Property with same id doesn't exist


  Scenario: Checking error code for deleting property
    When Nonexistent property id is deleted
    Then Response code is "404"
    And Custom code is "152"


#   Scenario Outline: Updating customer
#     When Customer with code "c1t" is updated with data
#       | companyName   | email   | code   | salesforceId   | vatId   | phone   | website   | sourceCustomer   | notes   |
#       | <companyName> | <email> | <code> | <salesforceId> | <vatId> | <phone> | <website> | <sourceCustomer> | <notes> |
#     Then Response code is "204"
#     And Body is empty
#     And Etag header is present
#     And Updated customer with code "<code>" has data
#       | companyName   | email   | code   | salesforceId   | vatId   | phone   | website   | sourceCustomer   | notes   |
#       | <companyName> | <email> | <code> | <salesforceId> | <vatId> | <phone> | <website> | <sourceCustomer> | <notes> |
# 
#     Examples:
#       | companyName | email | code             | salesforceId         | vatId      | phone         | website        | sourceCustomer | notes |
#       |             |       | c1t              | salesforceid_updated | CZ10000001 |               |                |                |       |
#       |             |       | c1t              |                      |            | +420123456789 | http://test.cz |                |       |
#       |             |       | UPDATED_CODE_c1t |                      |            |               |                |                |       |
# 


#   #TODO update cutomer with not matched etag/empty etag/missing etag
#   # update with error fields, bad values, missing fields
# 
#   Scenario: Updating customer with outdated etag
#     When Customer with code "c1t" is updated with data if updated before
#       | companyName | email | code | salesforceId | vatId | phone | website        | sourceCustomer | notes |
#       |             |       |      |              |       |       | http://test.cz |                |       |
#     Then Response code is "412"
#     And Custom code is "57"
# 
#   #error codes
# 
#   #Scenario Outline: Checking error codes for updating customer
#     #When File "<json_input_file>" is used for "<method>"
#     #Then Response code is "<error_code>"
#     #And Custom code is "<custom_code>"
# 
#    # Examples:
#    #   | json_input_file                           | method | error_code | custom_code |
#    #   | create_customer_missing_company_name.json | POST   | 405        | 51          |
#    #   | create_customer_wrong_field_name.json     | POST   | 405        | 51          |
#    #   | create_customer_wrong_field_name.json     | POST   | 405        | 51          |
#    #   | create_customer_faulty_address.json       | POST   | 405        | 51          |
# 
#   #field cannot be updated
#   #mandatory atribute is empty
#   #wrong field name
#   #id doesn't exist
#   #too long string in attribute
#   #invalid value in attribute
# 
#   Scenario: Customer is activated
#     When Customer with code "c1t" is activated
#     Then Response code is "204"
#     And Body is empty
#     And Customer with code "c1t" is active
# 
#     #error codes
# 
#   Scenario: Customer is inactivated
#     Given Customer with code "c1t" is activated
#     When Customer with code "c1t" is inactivated
#     Then Response code is "204"
#     And Body is empty
#     And Customer with code "c1t" is not active