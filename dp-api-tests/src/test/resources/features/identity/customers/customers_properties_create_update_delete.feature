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
  is failing because create passes just for the first time

    Given All customerProperties are deleted from DB for customer code "c1t" and property code "p2_code"

    When Property with code "p2_code" is added to customer with code "c1t" with type "anchor" from "2015-01-01" to "2015-10-31"
    Then Response code is "201"
    And Body contains entity with attribute "property_name" value "p2_name"
    And Body contains entity with attribute "valid_from" value "2015-01-01"
    And Body contains entity with attribute "valid_to" value "2015-10-31"
    And Body contains entity with attribute "type" value "anchor"
    And "Location" header is set and contains the same customerProperty
    And Etag header is present


  Scenario Outline: Checking error codes for creating customerProperty
    Given All customerProperties are deleted from DB for customer code "c1t" and property code "p2_code"

    When Property with code "<property_code>" is added to customer with code "<customer_code>" with type "<type>" from "<valid_from>" to "<valid_to>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      |                      | property_code | customer_code | type        | valid_from | valid_to   | error_code | custom_code |
      | missing date         | p2_code       | c2t           | anchor      | /null      |            | 400        | 53          |
      | missing date         | p2_code       | c2t           | anchor      | /null      | /null      | 400        | 53          |
      | missing date         | p2_code       | c2t           | anchor      |            | /null      | 400        | 53          |
      | missing date         | p2_code       | c2t           | anchor      |            |            | 400        | 53          |
      | from after to date   | p2_code       | c2t           | anchor      | 2015-01-01 | 2014-12-31 | 400        | 63          |
      | wrong date format    | p2_code       | c2t           | anchor      | 2015-01-   | 2100-01-01 | 400        | 59          |
      | wrong date format    | p2_code       | c2t           | anchor      | 2015-01-01 | asdfasdf   | 400        | 59          |
      | wrong type           | p2_code       | c2t           | nonexistent | 2015-01-01 | 2100-01-01 | 400        | 59          |
      | duplicate entry      | p1_code       | c1t           | anchor      | 2015-01-01 | 2100-01-01 | 400        | 62          |
      | notexistent property | nonexistent   | c1t           | anchor      | 2015-01-01 | 2100-01-01 | 400        | 63          |


    #add wrong dates, wrong type, not unique type, more anchor for one property, ...


  #TODO update cutomer with not matched etag/empty etag/missing etag
  # update with error fields, bad values, missing fields
  # update nonexistent field


  Scenario: Updating customerProperty with outdated etag

    Given All customerProperties are deleted from DB for customer code "c1t" and property code "p2_code"
    Given Relation between property with code "p2_code" and customer with code "c2t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    When Property with code "p2_code" for customer with code "c2t" with type "anchor" is updating field "valid_from" to value "2015-01-01" with invalid etag
    Then Response code is "412"
    And Custom code is "57"

  Scenario Outline: Updating customerProperty error codes

    Given All customerProperties are deleted from DB for customer code "c1t" and property code "p2_code"
    Given Relation between property with code "p2_code" and customer with code "c2t" exists with type "anchor" from "2015-01-01" to "2015-12-31"
    When Property with code "p2_code" for customer with code "c2t" with type "anchor" is updating field "<field>" to value "<value>"
    Then Response code is "<status_code>"
    And Custom code is "<custom_code>"

    Examples:
      | field      | value      | status_code | custom_code |
      | valid_from | 2016-01-01 | 400         | 63          |
      | valid_from | invalid    | 400         | 59          |
      | valid_to   | 2014-12-31 | 400         | 63          |
      | valid_to   | invalid    | 400         | 59          |
      | type       | invalid    | 400         | 59          |

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
