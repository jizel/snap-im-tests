Feature: properties_create_update_delete

  #TODO add etag things to get/update/create

  Background:
    Given Database is cleaned
    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |

  Scenario: Creating property without parent with random address

    When Property is created with random address and billing address
      | salesforceId    | propertyName | propertyCode | website                    | email           | isDemoProperty | timezone      |
      | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague |

    Then Response code is "201"
    And Body contains property with attribute "property_code" value "pn1_code"
    And Body contains property with attribute "property_name" value "pn1_name"
    And Body contains property with attribute "email" value "pn1@tenants.biz"
    And "Location" header is set and contains the same property


  Scenario: Deleting Property
    When Property with code "p1_code" is deleted
    Then Response code is "204"
    And Body is empty
    And Property with same id doesn't exist


  Scenario: Checking error code for deleting property
    When Nonexistent property id is deleted
    Then Response code is "204"

  Scenario: Property is activated
    When Property with code "p1_code" is activated
    Then Response code is "204"
    And Body is empty
    And Property with code "p1_code" is active

  Scenario: Property is inactivated
    Given Property with code "p1_code" is activated
    When Property with code "p1_code" is inactivated
    Then Response code is "204"
    And Body is empty
    And Property with code "p1_code" is not active

  Scenario: Activating non existing properties
    When Property with non existing property id "11111111-1111-1111-1111-111111111111" is inactivated
    Then Response code is "404"
    And Custom code is "152"

  Scenario: Deactivating non existing properties
    When Property with non existing property id "11111111-1111-1111-1111-111111111111" is activated
    Then Response code is "404"
    And Custom code is "152"

  #GET /identity/properties/{id}/customers
  Scenario Outline: Filtering list of customers for property
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | Given company 3 | c3@tenants.biz | c3t  | salesforceid_given_3 | CZ10000003 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | Given company 4 | c4@tenants.biz | c4t  | salesforceid_given_4 | CZ10000004 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | Given company 5 | c5@tenants.biz | c5t  | salesforceid_given_5 | CZ10000005 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | Given company 6 | c6@tenants.biz | c6t  | salesforceid_given_6 | CZ10000006 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with code "c2t" exists with type "anchor" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with code "c3t" exists with type "anchor" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with code "c4t" exists with type "anchor" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with code "c5t" exists with type "anchor" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with code "c6t" exists with type "anchor" from "2015-01-01" to "2030-02-31"

    Given Customer with code "c1t" is activated
    Given Customer with code "c2t" is activated
    Given Customer with code "c3t" is activated
    Given Customer with code "c4t" is activated

    When List of customers for property with code "p1_code" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customers returned
    And There are customers with following codes returned in order: <expected_names>
    #And Total count is "<total>"

    Examples:
      | limit | cursor | returned |total | filter                          | sort      | sort_desc | expected_names      |
      | 5     | 0      | 4        |6     | code=='c*'                      | code      |           | c1t, c2t, c3t, c4t  |
      | 5     | 0      | 4        |6     | code=='c*'                      |           | code      | c4t, c3t, c2t, c1t  |
      | 5     | 2      | 2        |6     | code=='c*'                      | code      |           | c3t, c4t            |
      | 5     | 2      | 2        |6     | code=='c*'                      |           | code      | c2t, c1t            |
      | /null | /null  | 1        |1     | code==c3t                       | /null     | /null     | c3t                 |
      | /null | /null  | 1        |1     | company_name=='Given company 2' | /null     | /null     | c2t                 |


  Scenario Outline: Checking error codes for getting list of customers from properties
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2030-02-31"
    Given Customer with code "c1t" is activated

    When List of customers for property with code "p1_code" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter   | sort         | sort_desc    | response_code | custom_code |
      #limit and cursor
      | /null       | -1     | /null    | /null        | /null        | 400           | 63          |
      |             | -1     | /null    | /null        | /null        | 400           | 63          |
      | /null       | text   | /null    | /null        | /null        | 400           | 63          |
      |             | text   | /null    | /null        | /null        | 400           | 63          |
      | -1          |        | /null    | /null        | /null        | 400           | 63          |
      | -1          | /null  | /null    | /null        | /null        | 400           | 63          |
      | 201         | /null  | /null    | /null        | /null        | 400           | 63          |
      | 21474836470 | /null  | /null    | /null        | /null        | 400           | 63          |
      | text        |        | /null    | /null        | /null        | 400           | 63          |
      | text        | /null  | /null    | /null        | /null        | 400           | 63          |
      | 10          | -1     | /null    | /null        | /null        | 400           | 63          |
      | text        | 0      | /null    | /null        | /null        | 400           | 63          |
      | 10          | text   | /null    | /null        | /null        | 400           | 63          |

      #filtering and sorting
      | 10    | 0      | /null    | company_name | company_name | 400           | 64          |
      | 10    | 0      | /null    | /null        | nonexistent  | 400           | 63          |
      | 10    | 0      | /null    | nonexistent  | /null        | 400           | 63          |
      | 10    | 0      | code==   | /null        | /null        | 400           | 63          |
      | 10    | 0      | vat==CZ* | /null        | /null        | 400           | 63          |

