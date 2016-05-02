Feature: Properties create update delete

  #TODO add etag things to get/update/create
  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

  @Smoke
  Scenario: Creating property without parent with random address
    When Property is created with random address and billing address
      | salesforceId    | propertyName | propertyCode | website                    | email           | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "201"
    And Body contains property with attribute "property_code" value "pn1_code"
    And Body contains property with attribute "name" value "pn1_name"
    And Body contains property with attribute "email" value "pn1@tenants.biz"
    And "Location" header is set and contains the same property

  @Smoke
  Scenario: Deleting Property
    When Property with code "p1_code" is deleted
    Then Response code is "204"
    And Body is empty
    And Property with same id doesn't exist

  Scenario: Checking error code for deleting property
    When Nonexistent property id is deleted
    Then Response code is "204"

  @Smoke
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
      | companyName     | email          | code   | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | Given company 1 | c1@tenants.biz | c1test | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | Given company 2 | c2@tenants.biz | c2test | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | Given company 3 | c3@tenants.biz | c3test | salesforceid_given_3 | CZ10000003 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | Given company 4 | c4@tenants.biz | c4test | salesforceid_given_4 | CZ10000004 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | Given company 5 | c5@tenants.biz | c5test | salesforceid_given_5 | CZ10000005 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | Given company 6 | c6@tenants.biz | c6test | salesforceid_given_6 | CZ10000006 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    Given Relation between property with code "p1_code" and customer with code "c1test" exists with type "owner" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with code "c2test" exists with type "owner" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with code "c3test" exists with type "owner" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with code "c4test" exists with type "owner" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with code "c5test" exists with type "owner" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with code "c6test" exists with type "owner" from "2015-01-01" to "2030-02-31"

    Given Customer with code "c1test" is activated
    Given Customer with code "c2test" is activated
    Given Customer with code "c3test" is activated
    Given Customer with code "c4test" is activated
    When List of customers for property with code "p1_code" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"

    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customers returned
    And There are customers with following codes returned in order: <expected_names>
    #And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                  | sort          | sort_desc     | expected_names                 |
      | 5     | 0      | 4        | 6     | customer_code=='c*'     | customer_code |               | c1test, c2test, c3test, c4test |
      | 5     | 0      | 4        | 6     | customer_code=='c*'     |               | customer_code | c4test, c3test, c2test, c1test |
      | 5     | 2      | 2        | 6     | customer_code=='c*'     | customer_code |               | c3test, c4test                 |
      | 5     | 2      | 2        | 6     | customer_code=='c*'     |               | customer_code | c2test, c1test                 |
      | /null | /null  | 1        | 1     | customer_code==c3test   | /null         | /null         | c3test                         |
      | /null | /null  | 1        | 1     | name=='Given company 2' | /null         | /null         | c2test                         |


  Scenario Outline: Checking error codes for getting list of customers from properties
    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "chain" from "2015-01-01" to "2030-02-31"
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
      | 10          | 0      | /null    | company_name | company_name | 400           | 64          |
      | 10          | 0      | /null    | /null        | nonexistent  | 400           | 63          |
      | 10          | 0      | /null    | nonexistent  | /null        | 400           | 63          |
      | 10          | 0      | code==   | /null        | /null        | 400           | 63          |
      | 10          | 0      | vat==CZ* | /null        | /null        | 400           | 63          |

  Scenario Outline: Validate that property regions belong to the correct country
    When A property for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" from country "<country>" region "<region>" code "<code>" email "<email>" is created
    Then Content type is "application/json"
    And Response code is 201
    And Body contains entity with attribute "address.region" value "<region>"

    Examples:
      | country | region         | code       | email           |
      | US      | Alabama        | propcode1  | mail1@mail.com  |
      | US      | Alaska         | propcode2  | mail2@mail.com  |
      | US      | Arizona        | propcode3  | mail3@mail.com  |
      | US      | Arkansas       | propcode4  | mail4@mail.com  |
      | US      | California     | propcode5  | mail5@mail.com  |
      | US      | Colorado       | propcode6  | mail6@mail.com  |
      | US      | Connecticut    | propcode7  | mail7@mail.com  |
      | US      | Delaware       | propcode8  | mail8@mail.com  |
      | US      | Florida        | propcode9  | mail9@mail.com  |
      | US      | Georgia        | propcode10 | mail10@mail.com |
      | US      | Hawaii         | propcode11 | mail11@mail.com |
      | US      | Idaho          | propcode12 | mail12@mail.com |
      | US      | Illinois       | propcode13 | mail13@mail.com |
      | US      | Indiana        | propcode14 | mail14@mail.com |
      | US      | Iowa           | propcode15 | mail15@mail.com |
      | US      | Kansas         | propcode16 | mail16@mail.com |
      | US      | Kentucky       | propcode17 | mail17@mail.com |
      | US      | Louisiana      | propcode18 | mail18@mail.com |
      | US      | Maine          | propcode19 | mail19@mail.com |
      | US      | Maryland       | propcode20 | mail20@mail.com |
      | US      | Massachusetts  | propcode21 | mail21@mail.com |
      | US      | Michigan       | propcode22 | mail22@mail.com |
      | US      | Minnesota      | propcode23 | mail23@mail.com |
      | US      | Mississippi    | propcode24 | mail24@mail.com |
      | US      | Missouri       | propcode25 | mail25@mail.com |
      | US      | Montana        | propcode26 | mail26@mail.com |
      | US      | Nebraska       | propcode27 | mail27@mail.com |
      | US      | Nevada         | propcode28 | mail28@mail.com |
      | US      | New Hampshire  | propcode29 | mail29@mail.com |
      | US      | New Jersey     | propcode30 | mail30@mail.com |
      | US      | New Mexico     | propcode31 | mail31@mail.com |
      | US      | New York       | propcode32 | mail32@mail.com |
      | US      | North Carolina | propcode33 | mail33@mail.com |
      | US      | North Dakota   | propcode34 | mail34@mail.com |
      | US      | Ohio           | propcode35 | mail35@mail.com |
      | US      | Oklahoma       | propcode36 | mail36@mail.com |
      | US      | Oregon         | propcode37 | mail37@mail.com |
      | US      | Pennsylvania   | propcode38 | mail38@mail.com |
      | US      | Rhode Island   | propcode39 | mail39@mail.com |
      | US      | South Carolina | propcode40 | mail40@mail.com |
      | US      | South Dakota   | propcode41 | mail41@mail.com |
      | US      | Tennessee      | propcode42 | mail42@mail.com |
      | US      | Texas          | propcode43 | mail43@mail.com |
      | US      | Utah           | propcode44 | mail44@mail.com |
      | US      | Vermont        | propcode45 | mail45@mail.com |
      | US      | Virginia       | propcode46 | mail46@mail.com |
      | US      | Washington     | propcode47 | mail47@mail.com |
      | US      | West Virginia  | propcode48 | mail48@mail.com |
      | US      | Wisconsin      | propcode49 | mail49@mail.com |
      | US      | Wyoming        | propcode50 | mail50@mail.com |

  Scenario Outline: Checking error codes for regions
    When A property for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" from country "<country>" region "<region>" code "<code>" email "<email>" is created
    Then Content type is "application/json"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "message" value "Region with identifier <region> was not found."

    Examples:
      | country | region  | code       | email           | response_code | custom_code |
      | DE      | invalid | propcode8  | mail8@mail.com  | 400           | 63          |
      | BG      | invalid | propcode9  | mail9@mail.com  | 400           | 63          |
      | US      | invalid | propcode10 | mail10@mail.com | 400           | 63          |
      | CZ      | invalid | propcode11 | mail11@mail.com | 400           | 63          |
      | AU      | invalid | propcode12 | mail12@mail.com | 400           | 63          |
      | CZ      | Texas   | propcode14 | mail14@mail.com | 400           | 63          |
      | AU      | Ohio    | propcode15 | mail15@mail.com | 400           | 63          |
