@Identity
Feature: Customers properties create update delete

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone      |
      | 40ebf861-7549-46f1-a99f-249716c83b33 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
      | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
      | b13fde13-615a-48fd-a287-ba4a7314193b | Given company 3 | c3@tenants.biz | salesforceid_given_3 | CZ10000003 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    Given The following users exist for customer "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" as primary "true"
      | id                                   | type     | username            | firstName | lastName     | email                         | timezone      | languageCode |
      | a63edcc6-6830-457c-89b1-7801730bd0ae | snapshot | Snapshotuser        | Snapshot  | SnapshotUser | snapshotuser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "a63edcc6-6830-457c-89b1-7801730bd0ae"
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 9e678710-a1f6-47da-9468-0694466541df | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 40ebf861-7549-46f1-a99f-249716c83b33 |
      | 621bd8a4-0b73-40b2-ab5e-cbe88dac9e4e | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 |
      | fc9e3171-e018-44fa-84f4-d74b86c538e9 | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague | b13fde13-615a-48fd-a287-ba4a7314193b |

    Given Relation between property with code "p1_code" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p1_code" and customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p1_code" and customer with id "b13fde13-615a-48fd-a287-ba4a7314193b" exists with type "asset_management" from "2015-01-01" to "2015-12-31"


  #TODO update cutomer with not matched etag/empty etag/missing etag
  # update with error fields, bad values, missing fields
  # update nonexistent field

  @Smoke
  Scenario: Updating customerProperty with etag
    Given Relation between property with code "p2_code" and customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" exists with type "chain" from "2015-01-01" to "2015-12-31"
    When Property with code "p2_code" for customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" is updating field "valid_from" to value "2014-01-01"
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Field "valid_from" has value "2014-01-01" for property with code "p2_code" for customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" with type "chain"

  Scenario: Updating customerProperty with outdated etag
    Given Relation between property with code "p2_code" and customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" exists with type "chain" from "2015-01-01" to "2015-12-31"
    When Property with code "p2_code" for customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" is updating field "valid_from" to value "2015-01-01" with invalid etag
    Then Response code is "412"
    And Custom code is "41202"

  Scenario Outline: Updating customerProperty error codes
    Given Relation between property with code "p2_code" and customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" exists with type "chain" from "2015-01-01" to "2015-12-31"
    When Property with code "p2_code" for customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" is updating field "<field>" to value "<value>"
    Then Response code is "<status_code>"
    And Custom code is "<custom_code>"
    Examples:
      | field             | value      | status_code | custom_code |
      | valid_from        | 2016-01-01 | 422         | 42201       |
      | valid_from        | invalid    | 400         | 40001       |
      | valid_to          | 2014-12-31 | 422         | 42201       |
      | valid_to          | invalid    | 400         | 40001       |
      | relationship_type | invalid    | 422         | 42201       |

  Scenario: Duplicate adding of customer property throws correct error - DP-1661
    When Property with code "p2_code" is added to customer with id "40ebf861-7549-46f1-a99f-249716c83b33" with type "chain" from "2015-01-01" to "2015-10-31"
    Then Response code is "201"
    When Property with code "p2_code" is added to customer with id "40ebf861-7549-46f1-a99f-249716c83b33" with type "chain" from "2015-01-01" to "2015-10-31"
    Then Response code is "409"
    And Custom code is 40907

  Scenario: Customer cannot be deleted if he is anchor customer of some existing property
    Given The following customers exist with random address
      | id                                   | name          | email          | salesforceId    | vatId      | isDemo         | phone         | website                    | timezone      | type  |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Deletion test | c1@tenants.biz | SALESFORCEID001 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | HOTEL |
    Given The following properties exist with random address and billing address
      | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | salesforceid_4 | p4_name      | p4_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is "409"
    And Custom code is 40915
    Given Property with code "p4_code" is deleted
    Then Response code is "204"
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is "204"

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
