@Identity
Feature: Customers create update delete

  #TODO add etag things to get/update/create
  Background:
    Given Database is cleaned and default entities are created

  @Smoke
  Scenario: Creating/activating/deleting Customer
    When Customer is created with random address
      | id                                   | name                  | email          | salesforceId    | vatId      | isDemo         | phone         | website                    | timezone      | type  | hospitalityId                        |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Creation test company | s1@tenants.biz | SALESFORCEID001 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | HOTEL | 000000b2-3836-4207-a705-42bbec000000 |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "Creation test company"
    And Body contains entity with attribute "email" value "s1@tenants.biz"
    And Body contains entity with attribute "salesforce_id" value "SALESFORCEID001"
    And Body contains entity with attribute "phone" value "+420123456789"
    And Body contains entity with attribute "customer_code"
    And Body contains entity with attribute "is_active" value "true"
    And Body contains entity with attribute "type" value "hotel"
    And Body contains entity with attribute "hospitality_id" value "000000b2-3836-4207-a705-42bbec000000"
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is activated
    Then Response code is "204"
    And Body is empty
    And Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is active
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is inactivated
    Then Response code is "204"
    And Body is empty
    And Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is not active
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is "204"
    And Body is empty
    And Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" doesn't exist


  Scenario Outline: Checking error codes for creating customer
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"
    Examples:
      | json_input_file                                                        | method | module   | url                 | error_code | custom_code    |
      | /messages/identity/customers/create_customer_missing_company_name.json | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_missing_type.json         | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_email_value.json    | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_field_email.json    | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_vatid_value.json    | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_country_value.json  | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_phone_value.json    | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_website_value.json  | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_type.json           | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_invalid_UUID.json         | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_hospitality_id.json | POST   | identity | /identity/customers | 422        | 42201          |

#    TODO: error codes for updating customer
  Scenario Outline: Create foreign customers
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is 201
    And Content type is "application/json"
    And Body contains entity with attribute "name" value "<name>"
    Examples:
      | json_input_file                                                              | method | module   | url                 | name |
      | /messages/identity/customers/positive/create_customer_arabic_symbols.json    | POST   | identity | /identity/customers | ݑݒݓ  |
      | /messages/identity/customers/positive/create_customer_malayalam_symbols.json | POST   | identity | /identity/customers | ണതഥ  |
      | /messages/identity/customers/positive/create_customer_greek_symbols.json     | POST   | identity | /identity/customers | ᴦᴧᴨ  |
      | /messages/identity/customers/positive/create_customer_katakana_symbols.json  | POST   | identity | /identity/customers | ラリル  |
      | /messages/identity/customers/positive/create_customer_ancient_symbols.json   | POST   | identity | /identity/customers | 㐱㐲㐳  |
      | /messages/identity/customers/positive/create_customer_chinese_symbols.json   | POST   | identity | /identity/customers | 笅笆笇  |


  #TODO update with error fields, bad values, missing fields
  #TODO update nonexistent field
  Scenario Outline: Updating customer
    Given The following customers exist with random address
      | id                                   | name          | email          | salesforceId   | vatId       | phone         | website           | timezone      | type   | isDemo         | hospitalityId                        |
      | 910cfc16-4597-479f-ae18-250b0a94752e | Some_Company  | cust1@email.cz | salesforceid   | CZ987654320 | +420123456789 | http://google.com | Europe/Prague | HOTEL  | true           | 111111b2-3836-4207-a705-42bbec111111 |
    When Customer "910cfc16-4597-479f-ae18-250b0a94752e" is updated with data
      | name          | email   | vatId   | phone   | website   | notes   | timezone   | hospitalityId   |
      | <name>        | <email> | <vatId> | <phone> | <website> | <notes> | <timezone> | <hospitalityId> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Check updated customer "910cfc16-4597-479f-ae18-250b0a94752e" has data
      | name          | email   | vatId   | phone   | website   | notes   | timezone   | hospitalityId   |
      | <name>        | <email> | <vatId> | <phone> | <website> | <notes> | <timezone> | <hospitalityId> |
    Examples:
      | name               | email             | vatId       | phone         | website              | notes         | timezone      | hospitalityId                        |
      | updatedCompanyName | /null             | /null       | /null         | /null                | /null         | /null         | /null                                |
      | /null              | updated@email.com | /null       | /null         | /null                | /null         | /null         | /null                                |
      | /null              | /null             | CZ987654321 | /null         | /null                | /null         | /null         | /null                                |
      | /null              | /null             | /null       | +420987654321 | /null                | /null         | /null         | /null                                |
      | /null              | /null             | /null       | /null         | http://update.com    | /null         | /null         | /null                                |
      | /null              | /null             | /null       | /null         | /null                | updatedNotes  | /null         | /null                                |
      | /null              | /null             | /null       | /null         | /null                | /null         | Pacific/Fiji  | /null                                |
      | severalUpdates     | /null             | /null       | +420111222333 | http://several.cz    | several_notes | Europe/Prague | /null                                |
      | allUpdates         | all@all.com       | CZ999888777 | +420444555666 | http://allUpdated.cz | all_notes     | Asia/Tokyo    | 000000b2-3836-4207-a705-42bbec000000 |


  #TODO update cutomer with not matched etag/empty etag/missing etag
  Scenario: Updating customer with outdated etag
    When Customer with id "06000000-0000-4444-8888-000000000001" is updated with outdated etag
    Then Response code is "412"
    And Custom code is "41202"

  Scenario: Headquarters timezone parameter is not optional (DP-1695)
    When Customer is created with random address
      | name                  | email          | salesforceId    | vatId      | isDemo         | phone         | website                    | type    |
      | Creation test company | s1@tenants.biz | SALESFORCEID001 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel | HOTEL   |
    Then Response code is "422"
    And Custom code is 42201

  Scenario Outline: Checking error codes for regions
    When A customer with following country "<country>", region "<region>", vatId "<vatId>" is created
    Then Content type is "application/json"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "message" value "Reference does not exist. The entity Region with ID <region> cannot be found."

    Examples:
      | country | region           | vatId    | response_code | custom_code |
      | DE      | invalid          | 12345678 | 422           | 42202       |
      | BG      | invalid          | 12345678 | 422           | 42202       |
      | US      | invalid          | 12345678 | 422           | 42202       |
      | CZ      | invalid          | 12345678 | 422           | 42202       |
      | AU      | invalid          | 12345678 | 422           | 42202       |
      | US      | bg_region        | 12345678 | 422           | 42202       |
      | CZ      | us_region        | 12345678 | 422           | 42202       |
      | AU      | bg_region        | 12345678 | 422           | 42202       |
      | AU      | VictoriaRegion   | 12345678 | 422           | 42202       |
      | AU      | TheGreatTasmania | 12345678 | 422           | 42202       |
      | CA      | Yukon region     | 12345678 | 422           | 42202       |
      | CA      | TheGreatYukon    | 12345678 | 422           | 42202       |

  Scenario Outline: Checking error codes for regions with invalid VatId
    When A customer with following country "<country>", region "<region>", vatId "<vatId>" is created
    Then Content type is "application/json"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    Examples:
      | country | region   | vatId      | response_code | custom_code |
      | AU      | Tasmania | notValidId | 422           | 42201       |
      | AU      | Tasmania | 1234       | 422           | 42201       |
      | CA      | Yukon    | notValid   | 422           | 42201      |
      | CA      | Yukon    | CZ1234578  | 422           | 42201       |


  @Bug
  Scenario: Creating customer with same name as previously deleted one - DP-1380
    Given The following customers exist with random address
      | id                                   | name        | email              | vatId      | isDemo         | phone         | website                    | timezone      |
      | 0002d2b2-3836-4207-a705-42bbecf3d881 | Company 1   | c1@snapshot.travel | CZ11100001 | true           | +420321456789 | http://www.snapshot.travel | Europe/Prague |
    Then Response code is 201
    When Customer with id "0002d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is 204
    Given The following customers exist with random address
      | id                                   | name        | email              | vatId      | isDemo         | phone         | website                    | timezone      |
      | 1112d2b2-3836-4207-a705-42bbecf3d881 | Company 1   | c1@snapshot.travel | CZ11100001 | true           | +420321456789 | http://www.snapshot.travel | Europe/Prague |
    Then Response code is 201
    And Body contains entity with attribute "customer_id" value "1112d2b2-3836-4207-a705-42bbecf3d881"
    And Body contains entity with attribute "name" value "Company 1"

  Scenario Outline: Send POST request with empty body to all customers endpoints
    Given The following users exist for customer "06000000-0000-4444-8888-000000000001"
      | id                                    | type       | username      | firstName | lastName | email                         | timezone      | languageCode |
      | 00029079-48f0-4f00-9bec-e2329a8bdaac  | customer   | customerUser1 | customer  | User1    | customeruser1@snapshot.travel | Europe/Prague | cs-CZ   |
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                |
      | identity/customers/                                                                                |
      | identity/customers/06000000-0000-4444-8888-000000000001                                            |
      | identity/customers/06000000-0000-4444-8888-000000000001/properties                                 |
      | identity/customers/06000000-0000-4444-8888-000000000001/users                                      |
      | identity/customers/06000000-0000-4444-8888-000000000001/users/00029079-48f0-4f00-9bec-e2329a8bdaac |

  Scenario: Customer ID must be unique when creating customer - DP-1661
    Given The following customers exist with random address
      | id                                   | name              | email              | vatId      | isDemo         | timezone      |
      | 00011222-3836-4207-a705-42bbecf3d881 | Original Customer | oc@snapshot.travel | CZ10000001 | true           | Europe/Prague |
    When Customer is created with random address
      | id                                   | name         | email                   | salesforceId    | vatId      | isDemo         | timezone      | type  |
      | 00011222-3836-4207-a705-42bbecf3d881 | New Customer | newcust@snapshot.travel | SALESFORCEID001 | CZ20000002 | true           | Europe/Prague | HOTEL |
    Then Response code is "409"
    And Custom code is 40902

  Scenario: CustomerId and parentId must be different - DP-1528
    Given The following customers exist with random address
      | id                                   | name            | email          | vatId      | isDemo         | phone         | website                    | timezone      | isActive |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Given company 0 | c0@tenants.biz | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     |
    When Customer is created with random address
      | id                                   | parentId                             | name                      | email          | salesforceId    | vatId      | isDemo         | phone         | website                    | timezone      | isActive | type  |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | a792d2b2-3836-4207-a705-42bbecf3d881 | Already existing custoemr | c1@tenants.biz | SALESFORCEID001 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     | HOTEL |
#       Already existing customerId
    Then Response code is "409"
    And Custom code is 40902
    When Customer is created with random address
      | id                                   | parentId                             | name         | email                   | salesforceId    | vatId      | isDemo         | timezone      | type  |
      | 00011222-3836-4207-a705-42bbecf3d881 | 00011222-3836-4207-a705-42bbecf3d881 | New Customer | newcust@snapshot.travel | SALESFORCEID001 | CZ20000002 | true           | Europe/Prague | HOTEL |
#       Nonexistent customerId
    Then Response code is "422"
    And Custom code is 42202
    Then Customer with id "00011222-3836-4207-a705-42bbecf3d881" doesn't exist

  Scenario: Parent-child relationship should not contain loops - DP-1395
    Given The following customers exist with random address
      | id                                   | name            | email          | vatId      | isDemo         | phone         | website                    | timezone      | isActive |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Given company 0 | c0@tenants.biz | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     |
    Given The following customers exist with random address
      | id                                   | parentId                             | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone      | isActive |
      | 00011222-3836-4207-a705-42bbecf3d881 | a792d2b2-3836-4207-a705-42bbecf3d881 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     |
      | 10011222-3836-4207-a705-42bbecf3d881 | 00011222-3836-4207-a705-42bbecf3d881 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     |
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is updated with data
      | parentId                             |
      | 10011222-3836-4207-a705-42bbecf3d881 |
    Then Response code is "409"
    Then Custom code is 40911

  Scenario Outline: Checking customer types - DP-1879
    When Customer is created with random address
      | type   | name             | email                        | salesforceId    | vatId      | isDemo         | phone         | website                    | timezone      |
      | <type> | TypeTestCustomer | typecustomer@snapshot.travle | SALESFORCEID001 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Then Response code is "201"
    And Body contains entity with attribute "type" value "<type>"
    Examples:
    | type                            |
    | hotel                           |
    | consultancy                     |
    | educational_institution         |
    | hospitality_technology_provider |
    | other                           |

  Scenario: Customer with parent-child relationships cannot be deleted
    Given The following customers exist with random address
      | id                                   | name            | email          | vatId      | isDemo         | phone         | website                    | timezone      | isActive |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Given company 0 | c0@tenants.biz | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     |
    Given The following customers exist with random address
      | id                                   | parentId                             | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone      | isActive |
      | 00011222-3836-4207-a705-42bbecf3d881 | a792d2b2-3836-4207-a705-42bbecf3d881 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     |
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is "409"
    And Custom code is 40915
    When Customer with id "00011222-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is "204"
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is "204"

