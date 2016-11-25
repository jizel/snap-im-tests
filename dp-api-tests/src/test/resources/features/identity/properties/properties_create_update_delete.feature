@Identity
Feature: Properties create update delete

  #TODO add etag things to get/update/create
  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Default Snapshot user is created for customer "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

  @Smoke
  Scenario: Creating property without parent with random address
    When The following property is created with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId    | propertyName | propertyCode | website                    | email           | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "201"
    And Body contains property with attribute "property_code" value "pn1_code"
    And Body contains property with attribute "name" value "pn1_name"
    And Body contains property with attribute "email" value "pn1@tenants.biz"

  @Smoke
  Scenario: Deleting Property
    When Property with code "p1_code" is deleted
    Then Response code is "204"
    And Body is empty
    And Property with same id doesn't exist

  Scenario: Checking error code for deleting property
    When Nonexistent property id is deleted
    Then Response code is "412"

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
    And Custom code is "40402"

  Scenario: Deactivating non existing properties
    When Property with non existing property id "11111111-1111-1111-1111-111111111111" is activated
    Then Response code is "404"
    And Custom code is "40402"

  #GET /identity/properties/{id}/customers
  Scenario Outline: Filtering list of customers for property
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 2238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 3238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 3 | c3@tenants.biz | salesforceid_given_3 | CZ10000003 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 4238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 4 | c4@tenants.biz | salesforceid_given_4 | CZ10000004 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 5238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 5 | c5@tenants.biz | salesforceid_given_5 | CZ10000005 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 6238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 6 | c6@tenants.biz | salesforceid_given_6 | CZ10000006 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with id "2238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with id "3238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with id "4238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with id "5238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "p1_code" and customer with id "6238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-31"

    Given Customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is activated
    Given Customer with id "2238fd9a-a05d-42d8-8e84-42e904ace123" is activated
    Given Customer with id "3238fd9a-a05d-42d8-8e84-42e904ace123" is activated
    Given Customer with id "4238fd9a-a05d-42d8-8e84-42e904ace123" is activated
    When List of customers for property with code "p1_code" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"

    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customers returned
    And There are customers with following codes returned in order: <expected_names>
    #And Total count is "<total>"

#    Expected names are incorrect - fix
    Examples:
      | limit | cursor | returned | total | filter                   | sort          | sort_desc     | expected_names                 |
      | 5     | 0      | 4        | 6     | customer_id=='*238fd9a*' | customer_code |               | c3test, c4test                 |
      | 5     | 0      | 4        | 6     | customer_id=='*238fd9a*' |               | customer_code | c4test, c3test, c2test,        |
      | 5     | 2      | 2        | 6     | customer_id=='*238fd9a*' | customer_code |               | c3test, c4test                 |
      | 5     | 2      | 2        | 6     | customer_id=='*238fd9a*' |               | customer_code | c2test,                        |
      | /null | /null  | 1        | 1     | customer_id=='3238fd9a*' | /null         | /null         | c3test                         |
      | /null | /null  | 1        | 1     | name=='Given company 2'  | /null         | /null         | c2test                         |


  Scenario Outline: Checking error codes for getting list of customers from properties
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2030-02-31"
    Given Customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is activated

    When List of customers for property with code "p1_code" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter   | sort        | sort_desc   | response_code | custom_code |
      #limit and cursor
      | /null       | -1     | /null    | /null       | /null       | 400           | 40002          |
      |             | -1     | /null    | /null       | /null       | 400           | 40002          |
      | /null       | text   | /null    | /null       | /null       | 400           | 40002          |
      |             | text   | /null    | /null       | /null       | 400           | 40002          |
      | -1          |        | /null    | /null       | /null       | 400           | 40002          |
      | -1          | /null  | /null    | /null       | /null       | 400           | 40002          |
      | 201         | /null  | /null    | /null       | /null       | 400           | 40002          |
      | 21474836470 | /null  | /null    | /null       | /null       | 400           | 40002          |
      | text        |        | /null    | /null       | /null       | 400           | 40002          |
      | text        | /null  | /null    | /null       | /null       | 400           | 40002          |
      | 10          | -1     | /null    | /null       | /null       | 400           | 40002          |
      | text        | 0      | /null    | /null       | /null       | 400           | 40002          |
      | 10          | text   | /null    | /null       | /null       | 400           | 40002          |

      #filtering and sorting
      | 10          | 0      | /null    | name         | name         | 400           | 40002          |
      | 10          | 0      | /null    | /null        | nonexistent  | 400           | 40002          |
      | 10          | 0      | /null    | nonexistent  | /null        | 400           | 40002          |
      | 10          | 0      | code==   | /null        | /null        | 400           | 40002          |
      | 10          | 0      | vat==CZ* | /null        | /null        | 400           | 40002          |

  Scenario Outline: Validate that property regions belong to the correct country
    When A property for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" from country "<country>" region "<region>" code "<code>" email "<email>" is created with userId "5d829079-48f0-4f00-9bec-e2329a8bdaac"
    Then Content type is "application/json"
    And Response code is 201
    And Body contains entity with attribute "address.region" value "<region>"

    Examples:
      | country | region                            | code       | email           |
      | US      | Alabama                           | propcode1  | mail1@mail.com  |
      | US      | Alaska                            | propcode2  | mail2@mail.com  |
      | US      | Arizona                           | propcode3  | mail3@mail.com  |
      | US      | Arkansas                          | propcode4  | mail4@mail.com  |
      | US      | California                        | propcode5  | mail5@mail.com  |
      | US      | Colorado                          | propcode6  | mail6@mail.com  |
      | US      | Connecticut                       | propcode7  | mail7@mail.com  |
      | US      | Delaware                          | propcode8  | mail8@mail.com  |
      | US      | Florida                           | propcode9  | mail9@mail.com  |
      | US      | Georgia                           | propcode10 | mail10@mail.com |
      | US      | Hawaii                            | propcode11 | mail11@mail.com |
      | US      | Idaho                             | propcode12 | mail12@mail.com |
      | US      | Illinois                          | propcode13 | mail13@mail.com |
      | US      | Indiana                           | propcode14 | mail14@mail.com |
      | US      | Iowa                              | propcode15 | mail15@mail.com |
      | US      | Kansas                            | propcode16 | mail16@mail.com |
      | US      | Kentucky                          | propcode17 | mail17@mail.com |
      | US      | Louisiana                         | propcode18 | mail18@mail.com |
      | US      | Maine                             | propcode19 | mail19@mail.com |
      | US      | Maryland                          | propcode20 | mail20@mail.com |
      | US      | Massachusetts                     | propcode21 | mail21@mail.com |
      | US      | Michigan                          | propcode22 | mail22@mail.com |
      | US      | Minnesota                         | propcode23 | mail23@mail.com |
      | US      | Mississippi                       | propcode24 | mail24@mail.com |
      | US      | Missouri                          | propcode25 | mail25@mail.com |
      | US      | Montana                           | propcode26 | mail26@mail.com |
      | US      | Nebraska                          | propcode27 | mail27@mail.com |
      | US      | Nevada                            | propcode28 | mail28@mail.com |
      | US      | New Hampshire                     | propcode29 | mail29@mail.com |
      | US      | New Jersey                        | propcode30 | mail30@mail.com |
      | US      | New Mexico                        | propcode31 | mail31@mail.com |
      | US      | New York                          | propcode32 | mail32@mail.com |
      | US      | North Carolina                    | propcode33 | mail33@mail.com |
      | US      | North Dakota                      | propcode34 | mail34@mail.com |
      | US      | Ohio                              | propcode35 | mail35@mail.com |
      | US      | Oklahoma                          | propcode36 | mail36@mail.com |
      | US      | Oregon                            | propcode37 | mail37@mail.com |
      | US      | Pennsylvania                      | propcode38 | mail38@mail.com |
      | US      | Rhode Island                      | propcode39 | mail39@mail.com |
      | US      | South Carolina                    | propcode40 | mail40@mail.com |
      | US      | South Dakota                      | propcode41 | mail41@mail.com |
      | US      | Tennessee                         | propcode42 | mail42@mail.com |
      | US      | Texas                             | propcode43 | mail43@mail.com |
      | US      | Utah                              | propcode44 | mail44@mail.com |
      | US      | Vermont                           | propcode45 | mail45@mail.com |
      | US      | Virginia                          | propcode46 | mail46@mail.com |
      | US      | Washington                        | propcode47 | mail47@mail.com |
      | US      | West Virginia                     | propcode48 | mail48@mail.com |
      | US      | Wisconsin                         | propcode49 | mail49@mail.com |
      | US      | Wyoming                           | propcode50 | mail50@mail.com |
    # Australia regions
      | AU      | Ashmore and Cartier Islands       | propcode51 | mail51@mail.com |
      | AU      | Australian Antarctic Territory    | propcode52 | mail52@mail.com |
      | AU      | Australian Capital Territory      | propcode53 | mail53@mail.com |
      | AU      | Christmas Island                  | propcode54 | mail54@mail.com |
      | AU      | Cocos (Keeling) Islands           | propcode55 | mail55@mail.com |
      | AU      | Coral Sea Islands                 | propcode56 | mail56@mail.com |
      | AU      | Heard Island and McDonald Islands | propcode57 | mail57@mail.com |
      | AU      | Jervis Bay Territory              | propcode58 | mail58@mail.com |
      | AU      | New South Wales                   | propcode59 | mail59@mail.com |
      | AU      | Norfolk Island                    | propcode60 | mail60@mail.com |
      | AU      | Northern Territory                | propcode61 | mail61@mail.com |
      | AU      | Queensland                        | propcode62 | mail62@mail.com |
      | AU      | South Australia                   | propcode63 | mail63@mail.com |
      | AU      | Tasmania                          | propcode64 | mail64@mail.com |
      | AU      | Victoria                          | propcode65 | mail65@mail.com |
    # Canada regions
      | CA      | Ontario                           | propcode66 | mail66@mail.com |
      | CA      | Quebec                            | propcode67 | mail67@mail.com |
      | CA      | Nova Scotia                       | propcode68 | mail68@mail.com |
      | CA      | New Brunswick                     | propcode69 | mail69@mail.com |
      | CA      | Manitoba                          | propcode70 | mail70@mail.com |
      | CA      | British Columbia                  | propcode71 | mail71@mail.com |
      | CA      | Prince Edward Island              | propcode72 | mail72@mail.com |
      | CA      | Saskatchewan                      | propcode73 | mail73@mail.com |
      | CA      | Alberta                           | propcode74 | mail74@mail.com |
      | CA      | Newfoundland and Labrador         | propcode75 | mail75@mail.com |
      | CA      | Northwest Territories             | propcode76 | mail76@mail.com |
      | CA      | Yukon                             | propcode77 | mail77@mail.com |
      | CA      | Nunavut                           | propcode78 | mail78@mail.com |

  Scenario Outline: Checking error codes for regions
    When A property for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" from country "<country>" region "<region>" code "<code>" email "<email>" is created with userId "5d829079-48f0-4f00-9bec-e2329a8bdaac"
    Then Content type is "application/json"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "message" value "The associate Region with ID <region> was not found."

    Examples:
      | country | region           | code       | email           | response_code | custom_code |
      | DE      | invalid          | propcode8  | mail8@mail.com  | 422           | 42202       |
      | BG      | invalid          | propcode9  | mail9@mail.com  | 422           | 42202       |
      | US      | invalid          | propcode10 | mail10@mail.com | 422           | 42202       |
      | CZ      | invalid          | propcode11 | mail11@mail.com | 422           | 42202       |
      | AU      | invalid          | propcode12 | mail12@mail.com | 422           | 42202       |
      | CZ      | Texas            | propcode14 | mail14@mail.com | 422           | 42202       |
      | AU      | Ohio             | propcode15 | mail15@mail.com | 422           | 42202       |
      | US      | bg_region        | propcode13 | mail13@mail.com | 422           | 42202       |
      | CZ      | us_region        | propcode14 | mail14@mail.com | 422           | 42202       |
      | AU      | bg_region        | propcode15 | mail15@mail.com | 422           | 42202       |
      | AU      | VictoriaRegion   | propcode16 | mail16@mail.com | 422           | 42202       |
      | AU      | TheGreatTasmania | propcode17 | mail17@mail.com | 422           | 42202       |
      | CA      | Yukon region     | propcode18 | mail18@mail.com | 422           | 42202       |
      | CA      | TheGreatYukon    | propcode19 | mail19@mail.com | 422           | 42202       |
