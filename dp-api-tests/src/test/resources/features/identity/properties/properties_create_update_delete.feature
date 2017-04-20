@Identity
@Properties
Feature: Properties create update delete

  # TODO add etag things to get/update/create
  # TODO: check error codes for creating/updating property
  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName | firstName | lastName | email                | timezone      | culture |
      | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property is created with random address and billing address for user "default1"
      | id                                   | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

  @Smoke
  Scenario: Creating property without parent with random address (with capital UUID letters - DP-1974)
    When The following property is created with random address and billing address for user "default1"
      | id                                   | salesforceId    | name         | propertyCode | website                    | email           | isDemoProperty | timezone      | anchorCustomerId                     |
      | 000E833E-50B8-4854-A233-289F00bC4A09 | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "201"
    And Body contains property with attribute "property_code" value "pn1_code"
    And Body contains property with attribute "name" value "pn1_name"
    And Body contains property with attribute "email" value "pn1@tenants.biz"
    And Body contains property with attribute "property_id" value "000e833e-50b8-4854-a233-289f00bc4a09"

  @Smoke
  Scenario: Updating property
    When Property with code "p1_code" is updated with data
      | name         | website                  | email            | isDemoProperty | description  |
      | updated_name | https://www.upddated.com | updated@email.cz | false          | updated_desc |
    Then Response code is "204"
    When Property with code "p1_code" is requested
    Then Response code is "200"
    Then Body contains entity with attribute "name" value "updated_name"
    Then Body contains entity with attribute "website" value "https://www.upddated.com"
    Then Body contains entity with attribute "email" value "updated@email.cz"
    Then Body contains entity with attribute "is_demo_property" value "false"
    Then Body contains entity with attribute "description" value "updated_desc"

  @Smoke
  Scenario: Deleting Property
    When Property with code "p1_code" is deleted
    Then Response code is "204"
    And Body is empty
    And Property with same id doesn't exist

  Scenario: Checking error code for deleting property
    When Nonexistent property id is deleted
    Then Response code is "404"

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

  Scenario: Timezone parameter is mandatory (DP-1696)
    When The following property is created with random address and billing address for user "default1"
      | salesforceId    | name         | propertyCode | website                    | email           | isDemoProperty | anchorCustomerId                     |
      | salesforceid_n1 | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz | true           | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "422"
    And Custom code is 42201

  #GET /identity/properties/{id}/customers
  Scenario Outline: Filtering list of customers for property
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 2238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 3239fd9a-a05d-42d8-8e84-42e904ace123 | Given company 3 | c3@tenants.biz | salesforceid_given_3 | CZ10000003 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 4239fd9a-a05d-42d8-8e84-42e904ace123 | Given company 4 | c4@tenants.biz | salesforceid_given_4 | CZ10000004 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 5239fd9a-a05d-42d8-8e84-42e904ace123 | Given company 5 | c5@tenants.biz | salesforceid_given_5 | CZ10000005 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 6239fd9a-a05d-42d8-8e84-42e904ace123 | Given company 6 | c6@tenants.biz | salesforceid_given_6 | CZ10000006 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-28"
    Given Relation between property with code "p1_code" and customer with id "2238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-28"
    Given Relation between property with code "p1_code" and customer with id "3239fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-28"
    Given Relation between property with code "p1_code" and customer with id "4239fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-28"
    Given Relation between property with code "p1_code" and customer with id "5239fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-28"
    Given Relation between property with code "p1_code" and customer with id "6239fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2030-02-28"

    Given Customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is activated
    Given Customer with id "2238fd9a-a05d-42d8-8e84-42e904ace123" is activated
    Given Customer with id "3239fd9a-a05d-42d8-8e84-42e904ace123" is activated
    Given Customer with id "4239fd9a-a05d-42d8-8e84-42e904ace123" is activated
    When List of customers for property with code "p1_code" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"

    Then Response code is "200"
    And Content type is "application/json"
    And There are customers with following ids returned in order: <expected_ids>
    And Total count is "<total>"
#    Expected names are incorrect - fix
    Examples:
      | limit | cursor | total | filter                   | sort          | sort_desc     | expected_ids                 |
      | 5     | 0      | 2     | customer_id=='*238fd9a*' | customer_id   |               |  1238fd9a-a05d-42d8-8e84-42e904ace123, 2238fd9a-a05d-42d8-8e84-42e904ace123 |
      | 5     | 0      | 2     | customer_id=='*238fd9a*' |               | valid_from    |  1238fd9a-a05d-42d8-8e84-42e904ace123, 2238fd9a-a05d-42d8-8e84-42e904ace123 |
      | 5     | 2      | 2     | customer_id=='*238fd9a*' | customer_id   |               |  1238fd9a-a05d-42d8-8e84-42e904ace123               |
      | 5     | 2      | 2     | customer_id=='*238fd9a*' |               | valid_from    |  1238fd9a-a05d-42d8-8e84-42e904ace123               |
      | /null | /null  | 1     | customer_id=='3239fd9a*' | /null         | /null         |  3239fd9a-a05d-42d8-8e84-42e904ace123                |

  Scenario Outline: Checking error codes for getting list of customers from properties
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "chain" from "2015-01-01" to "2030-02-28"
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
    When A property for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" from country "<country>" region "<region>" code "<code>" email "<email>" is created by user "11111111-0000-4000-a000-000000000000"
    Then Content type is "application/json"
    And Response code is 201
    And Body contains entity with attribute "address.region" value "<region>"

    Examples:
      | country | region                            | code       | email           |
      | US      | Alaska                            | propcode2  | mail2@mail.com  |
    # Australia regions
      | AU      | Ashmore and Cartier Islands       | propcode51 | mail51@mail.com |
    # Canada regions
      | CA      | Ontario                           | propcode66 | mail66@mail.com |

  Scenario Outline: Checking error codes for regions
    When A property for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" from country "<country>" region "<region>" code "<code>" email "<email>" is created by user "11111111-0000-4000-a000-000000000000"
    Then Content type is "application/json"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "message" value "Reference does not exist. The entity Region with ID <region> cannot be found."

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

  Scenario: Creating property with same name as previously deleted one - DP-1380
    Given The following property is created with random address and billing address for user "default1"
      | salesforceId | name    | propertyCode | website                    | email              | isDemoProperty | timezone      | anchorCustomerId                     |
      | sl_id_1      | p2_name | p2_code      | http://www.snapshot.travel | p1@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is 201
    When Property with code "p2_code" is deleted
    Then Response code is 204
    Given The following property is created with random address and billing address for user "default1"
      | salesforceId | name    | propertyCode | website                    | email              | isDemoProperty | timezone      | anchorCustomerId                     |
      | sl_id_1      | p2_name | p2_code      | http://www.snapshot.travel | p1@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is 201
    And Body contains entity with attribute "property_code" value "p2_code"
    And Body contains entity with attribute "name" value "p2_name"

  Scenario Outline: Send POST request with empty body to all properties endpoints
    When The following property set is created for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
      | id                                   | name            | description            | type            |
      | c729e3b0-69bf-4c57-91bd-30230d2c1bd0 | ps1_name        | ps1_description        | brand           |
    When Property with code "p1_code" is added to property set "ps1_name"
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                        |
      | identity/properties/                                                                                       |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09                                                   |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/users/                                            |
      | identity/properties/999e833e-50e8-4854-a233-289f00b54a09/property_sets/c729e3b0-69bf-4c57-91bd-30230d2c1bd0|

    Scenario: Creating duplicate property returns correct error  - DP-1661
      When The following property is created with random address and billing address for user "default1"
        | id                                   | name         | propertyCode | website                    | email                | isDemoProperty | timezone      | anchorCustomerId                     |
        | 00011223-50e8-4854-a233-289f00b54a09 | original     | orig_code    | http://www.snapshot.travel | orig@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Response code is "201"
      When The following property is created with random address and billing address for user "default1"
        | id                                   | name         | propertyCode | website                    | email                | isDemoProperty | timezone      | anchorCustomerId                     |
        | 00011223-50e8-4854-a233-289f00b54a09 | original     | orig_code    | http://www.snapshot.travel | orig@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Response code is "409"
      And Custom code is 40902

