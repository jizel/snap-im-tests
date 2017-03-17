@Identity
Feature: Customers properties create update delete

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | Id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 40ebf861-7549-46f1-a99f-249716c83b33 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
      | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
      | b13fde13-615a-48fd-a287-ba4a7314193b | Given company 3 | c3@tenants.biz | salesforceid_given_3 | CZ10000003 | true           | +420123456789 | http://www.snapshot.travel | Europe/Berlin |
    Given The following users exist for customer "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" as primary "true"
      | Id                                   | userType | userName            | firstName | lastName     | email                         | timezone      | culture |
      | a63edcc6-6830-457c-89b1-7801730bd0ae | snapshot | Snapshotuser        | Snapshot  | SnapshotUser | snapshotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "a63edcc6-6830-457c-89b1-7801730bd0ae"
      | Id                                   | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 9e678710-a1f6-47da-9468-0694466541df | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 40ebf861-7549-46f1-a99f-249716c83b33 |
      | 621bd8a4-0b73-40b2-ab5e-cbe88dac9e4e | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 |
      | fc9e3171-e018-44fa-84f4-d74b86c538e9 | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague | b13fde13-615a-48fd-a287-ba4a7314193b |

    Given Relation between property with code "p1_code" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p1_code" and customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p1_code" and customer with id "b13fde13-615a-48fd-a287-ba4a7314193b" exists with type "asset_management" from "2015-01-01" to "2015-12-31"

    Given The password of user "defaultSnapshotuser" is "Password01"
    Given Get token for user "defaultSnapshotuser" with password "Password01"

  @Smoke
  Scenario: Adding property to customer with some type valid from date to date
    Given All customerProperties are deleted from DB for customer id "40ebf861-7549-46f1-a99f-249716c83b33" and property code "p2_code"
    When Property with code "p2_code" is added to customer with id "40ebf861-7549-46f1-a99f-249716c83b33" with type "chain" from "2015-01-01" to "2015-10-31"
    Then Response code is "201"
    And Body contains entity with attribute "property_id" value "621bd8a4-0b73-40b2-ab5e-cbe88dac9e4e"
    And Body contains entity with attribute "valid_from" value "2015-01-01"
    And Body contains entity with attribute "valid_to" value "2015-10-31"
    And Body contains entity with attribute "relationship_type" value "chain"

  Scenario Outline: Checking error codes for creating customerProperty
    When Property with code "<property_code>" is added to customer with id "<customer_id>" with type "<type>" from "<valid_from>" to "<valid_to>" with error "true"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"
    Examples:
      |                      | property_code | customer_id                          | type        | valid_from | valid_to   | error_code | custom_code |
      | missing date         | p2_code       | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | chain       | /null      | 2100-01-01 | 422        | 42201       |
      | missing date         | p2_code       | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | chain       | /null      | /null      | 422        | 42201       |
      | missing date         | p2_code       | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | chain       | 2015-01-01 | /null      | 422        | 42201       |
      | missing date         | p2_code       | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | chain       |            |            | 422        | 42201       |
      | from after to date   | p2_code       | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | chain       | 2015-01-01 | 2014-12-31 | 422        | 42201       |
      | wrong date format    | p2_code       | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | chain       | 2015-01-   | 2100-01-01 | 400        | 40001       |
      | wrong date format    | p2_code       | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | chain       | 2015-01-01 | asdfasdf   | 400        | 40001       |
      | wrong date           | p2_code       | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | chain       | 2015-01-01 | 2018-02-30 | 400        | 40001       |
      | wrong type           | p2_code       | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | nonexistent | 2015-01-01 | 2100-01-01 | 422        | 42201       |
      | duplicate entry      | p1_code       | 40ebf861-7549-46f1-a99f-249716c83b33 | chain       | 2015-01-01 | 2100-01-01 | 409        | 40907       |
      | notexistent property | nonexistent   | 40ebf861-7549-46f1-a99f-249716c83b33 | chain       | 2015-01-01 | 2100-01-01 | 422        | 42202       |


    #add wrong dates, wrong type, not unique type, more anchor for one property, ...


  #TODO update cutomer with not matched etag/empty etag/missing etag
  # update with error fields, bad values, missing fields
  # update nonexistent field

  @Smoke
  Scenario: Updating customerProperty with etag
    Given All customerProperties are deleted from DB for customer id "40ebf861-7549-46f1-a99f-249716c83b33" and property code "p2_code"
    Given Relation between property with code "p2_code" and customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" exists with type "chain" from "2015-01-01" to "2015-12-31"
    When Property with code "p2_code" for customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" is updating field "valid_from" to value "2014-01-01"
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Field "valid_from" has value "2014-01-01" for property with code "p2_code" for customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" with type "chain"

  Scenario: Updating customerProperty with outdated etag
    Given All customerProperties are deleted from DB for customer id "40ebf861-7549-46f1-a99f-249716c83b33" and property code "p2_code"
    Given Relation between property with code "p2_code" and customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" exists with type "chain" from "2015-01-01" to "2015-12-31"
    When Property with code "p2_code" for customer with id "58dd58d4-a56e-4cf5-a3a6-068fe37fef40" is updating field "valid_from" to value "2015-01-01" with invalid etag
    Then Response code is "412"
    And Custom code is "41202"

  @Smoke
  Scenario: Delete customer should remove all related property relation
    Given Relation between property with code "p3_code" and customer with id "b13fde13-615a-48fd-a287-ba4a7314193b" exists with type "chain" from "2016-01-01" to "2016-01-15"
    Given Customer with id "b13fde13-615a-48fd-a287-ba4a7314193b" is deleted
    Then Property "p3_code" is not assigned to customer "b13fde13-615a-48fd-a287-ba4a7314193b"

  Scenario Outline: Updating customerProperty error codes
    Given All customerProperties are deleted from DB for customer id "40ebf861-7549-46f1-a99f-249716c83b33" and property code "p2_code"
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
