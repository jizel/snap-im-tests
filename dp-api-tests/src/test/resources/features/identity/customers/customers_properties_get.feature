@Identity
Feature: Customers properties get

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 40ebf861-7549-46f1-a99f-249716c83b33 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
      | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
      | b13fde13-615a-48fd-a287-ba4a7314193b | Given company 3 | c3@tenants.biz | salesforceid_given_3 | CZ10000003 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "40ebf861-7549-46f1-a99f-249716c83b33" as primary "true"
      | userId                               | userType | userName     | firstName | lastName     | email                                | timezone      | culture |
      | a63edcc6-6830-457c-89b1-7801730bd0ae | snapshot | snapshotUser | Snapshot  | User         | snapshotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
      | b63edcc6-6830-457c-89b1-7801730bd0ae | customer | custProp1           | customer  | property     | customerProperty1@snapshot.travel    | Europe/Prague | cs-CZ   |
    Given Default Snapshot user is created
    Given The following properties exist with random address and billing address for user "a63edcc6-6830-457c-89b1-7801730bd0ae"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 4d266045-1cf1-4735-8ef9-216de1370f2e | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 40ebf861-7549-46f1-a99f-249716c83b33 |
      | f7aaed3d-f8ef-41a4-a630-99d07f0ee592 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 58dd58d4-a56e-4cf5-a3a6-068fe37fef40 |
      | 0a9a76ca-a8f8-445e-85d1-af857b0986e1 | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague | b13fde13-615a-48fd-a287-ba4a7314193b |

    Given Relation between property with code "p1_code" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p3_code" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p3_code" and customer with id "b13fde13-615a-48fd-a287-ba4a7314193b" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p1_code" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "p1_code" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with type "asset_management" from "2015-01-01" to "2015-12-31"

    Given The password of user "snapshotUser" is "Password01"
    Given Get token for user "snapshotUser" with password "Password01"

  @Smoke
  Scenario: Getting customerProperty
    When Property with code "p1_code" from customer with id "40ebf861-7549-46f1-a99f-249716c83b33" is got with type "CHAIN"
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "property_id" value "4d266045-1cf1-4735-8ef9-216de1370f2e"
    And Body contains entity with attribute "valid_from" value "2015-01-01"
    And Body contains entity with attribute "valid_to" value "2015-12-31"
    And Body contains entity with attribute "relationship_type" value "chain"


  # DP-1722
  Scenario: Getting customerProperty with etag
    When Property with code "p1_code" from customer with id "40ebf861-7549-46f1-a99f-249716c83b33" is got with type "chain" with etag
    Then Response code is "304"
    And Body is empty

  Scenario: Getting customerProperty with not current etag
  CustomerProperty is got, etag is saved to tmp, then customerProperty valid_to is updated to "2016-12-31" so etag should change and is got again with previous etag
    When Property with code "p1_code" from customer with id "40ebf861-7549-46f1-a99f-249716c83b33" is got with type "chain" for etag, updated and got with previous etag
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "property_id" value "4d266045-1cf1-4735-8ef9-216de1370f2e"
    And Body contains entity with attribute "valid_from" value "2015-01-01"
    And Body contains entity with attribute "valid_to" value "2200-01-01"
    And Body contains entity with attribute "relationship_type" value "chain"


  Scenario: Checking error code for getting customerProperty
    When Nonexistent customerPropety id is got for customer with id "40ebf861-7549-46f1-a99f-249716c83b33"
    Then Response code is "404"
    And Custom code is "40402"


  Scenario Outline: Getting list of customerProperties
    Given The following customers exist with random address
      | customerId                           | companyName               | email               | salesforceId              | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | ae3c902f-3386-4956-be84-cd969866c571 | List test Given company 1 | list_c1@tenants.biz | list_salesforceid_given_1 | CZ22000001 | true           | +111111111111 | http://www.snapshot.travel | Europe/Prague |
      | f9766fd3-483d-4507-a20d-929421d44c84 | List test Given company 2 | list_c2@tenants.biz | list_salesforceid_given_2 | CZ22000002 | true           | +111111111111 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "ae3c902f-3386-4956-be84-cd969866c571" as primary "true"
      | userId                               | userType | userName              | firstName | lastName     | email                    | timezone      | culture |
      | 6006cd1a-7d4a-42b1-84f1-93d3628a38e0 | snapshot | gettingListofCustProp | Default   | SnapshotUser | snapshot@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "6006cd1a-7d4a-42b1-84f1-93d3628a38e0"
      | salesforceId   | propertyName           | propertyCode           | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | list_prop_cust_p1_name | list_prop_cust_p1_code | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | ae3c902f-3386-4956-be84-cd969866c571 |
      | salesforceid_2 | list_prop_cust_p2_name | list_prop_cust_p2_code | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | ae3c902f-3386-4956-be84-cd969866c571 |
      | salesforceid_3 | list_prop_cust_p3_name | list_prop_cust_p3_code | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague | ae3c902f-3386-4956-be84-cd969866c571 |
      | salesforceid_4 | list_prop_cust_p4_name | list_prop_cust_p4_code | http://www.snapshot.travel | p4@tenants.biz | true           | Europe/Prague | ae3c902f-3386-4956-be84-cd969866c571 |
      | salesforceid_5 | list_prop_cust_p5_name | list_prop_cust_p5_code | http://www.snapshot.travel | p5@tenants.biz | true           | Europe/Prague | ae3c902f-3386-4956-be84-cd969866c571 |
      | salesforceid_6 | list_prop_cust_p6_name | list_prop_cust_p6_code | http://www.snapshot.travel | p6@tenants.biz | true           | Europe/Prague | ae3c902f-3386-4956-be84-cd969866c571 |
      | salesforceid_7 | list_prop_cust_p7_name | list_prop_cust_p7_code | http://www.snapshot.travel | p7@tenants.biz | true           | Europe/Prague | ae3c902f-3386-4956-be84-cd969866c571 |
      | salesforceid_8 | list_prop_cust_p8_name | list_prop_cust_p8_code | http://www.snapshot.travel | p8@tenants.biz | true           | Europe/Prague | ae3c902f-3386-4956-be84-cd969866c571 |
      | salesforceid_9 | list_prop_cust_p9_name | list_prop_cust_p9_code | http://www.snapshot.travel | p9@tenants.biz | true           | Europe/Prague | ae3c902f-3386-4956-be84-cd969866c571 |
    Given Relation between property with code "list_prop_cust_p1_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p9_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "chain" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p9_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "data_owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p9_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "owner" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p9_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "asset_management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p9_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "management" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p1_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p2_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p3_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p4_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p5_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p6_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p7_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p8_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "membership" from "2015-01-01" to "2015-12-31"
    Given Relation between property with code "list_prop_cust_p9_code" and customer with id "ae3c902f-3386-4956-be84-cd969866c571" exists with type "membership" from "2015-01-01" to "2015-12-31"
    When List of customerProperties is got for customer with id "ae3c902f-3386-4956-be84-cd969866c571" with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customerProperties returned
    Examples:
      | limit | cursor | returned |
      | /null |        | 50       |
      | /null | /null  | 50       |
      |       |        | 50       |
      |       | /null  | 50       |
      | 15    |        | 15       |
      |       | 1      | 50       |
      | 20    | 0      | 20       |
      | 10    | 0      | 10       |
      | 5     | 5      | 5        |

  @Bug
  Scenario Outline: Checking error codes for getting list of customerProperties
    When List of customerProperties is got for customer with id "40ebf861-7549-46f1-a99f-249716c83b33" with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"
    Examples:
      | limit | cursor | filter   | sort              | sort_desc         | response_code | custom_code |
      #limit and cursor
      | /null | -1     | /null    | /null             | /null             | 400           | 40002       |
      |       | -1     | /null    | /null             | /null             | 400           | 40002       |
      | /null | text   | /null    | /null             | /null             | 400           | 40002       |
      |       | text   | /null    | /null             | /null             | 400           | 40002       |
      | -1    |        | /null    | /null             | /null             | 400           | 40002       |
      | -1    | /null  | /null    | /null             | /null             | 400           | 40002       |
      | text  |        | /null    | /null             | /null             | 400           | 40002       |
      | text  | /null  | /null    | /null             | /null             | 400           | 40002       |
      | 10    | -1     | /null    | /null             | /null             | 400           | 40002       |
      | text  | 0      | /null    | /null             | /null             | 400           | 40002       |
      | 10    | text   | /null    | /null             | /null             | 400           | 40002       |
      #filtering and sorting
      | 10    | 0      | /null    | relationship_type | relationship_type | 400           | 40002       |
      | 10    | 0      | type==   | /null             | /null             | 400           | 40002       |
      | 10    | 0      | vat==CZ* | /null             | /null             | 400           | 40002       |

   #TODO: Tests, that you can use right keywords for sort/sort_desc ()

  Scenario Outline: Filtering list of customer properties
    Given The following customers exist with random address
      | customerId                           | companyName                      | email               | salesforceId                     | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 3964bc8b-082d-4d47-b300-9a7b26a3ce91 | Filter list test Given company 1 | list_c1@tenants.biz | filter_list_salesforceid_given_1 | CZ22000001 | true           | +111111111111 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "3964bc8b-082d-4d47-b300-9a7b26a3ce91" as primary "true"
      | userId                               | userType | userName      | firstName | lastName     | email                    | timezone      | culture |
      | 7006cd1a-7d4a-42b1-84f1-93d3628a38e0 | snapshot | FilteringList | Default   | SnapshotUser | snapshot@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "7006cd1a-7d4a-42b1-84f1-93d3628a38e0"
      | salesforceId   | propertyName                  | propertyCode                  | website                    | email           | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | filter_list_prop_cust_p1_name | filter_list_prop_cust_p1_code | http://www.snapshot.travel | fp1@tenants.biz | true           | Europe/Prague | 3964bc8b-082d-4d47-b300-9a7b26a3ce91 |
      | salesforceid_2 | filter_list_prop_cust_p2_name | filter_list_prop_cust_p2_code | http://www.snapshot.travel | fp2@tenants.biz | true           | Europe/Prague | 3964bc8b-082d-4d47-b300-9a7b26a3ce91 |
      | salesforceid_3 | filter_list_prop_cust_p3_name | filter_list_prop_cust_p3_code | http://www.snapshot.travel | fp3@tenants.biz | true           | Europe/Prague | 3964bc8b-082d-4d47-b300-9a7b26a3ce91 |
      | salesforceid_4 | filter_list_prop_cust_p4_name | filter_list_prop_cust_p4_code | http://www.snapshot.travel | fp4@tenants.biz | true           | Europe/Prague | 3964bc8b-082d-4d47-b300-9a7b26a3ce91 |
      | salesforceid_5 | filter_list_prop_cust_p5_name | filter_list_prop_cust_p5_code | http://www.snapshot.travel | fp5@tenants.biz | true           | Europe/Prague | 3964bc8b-082d-4d47-b300-9a7b26a3ce91 |
      | salesforceid_6 | filter_list_prop_cust_p6_name | filter_list_prop_cust_p6_code | http://www.snapshot.travel | fp6@tenants.biz | true           | Europe/Prague | 3964bc8b-082d-4d47-b300-9a7b26a3ce91 |
      | salesforceid_7 | filter_list_prop_cust_p7_name | filter_list_prop_cust_p7_code | http://www.snapshot.travel | fp7@tenants.biz | true           | Europe/Prague | 3964bc8b-082d-4d47-b300-9a7b26a3ce91 |
      | salesforceid_8 | filter_list_prop_cust_p8_name | filter_list_prop_cust_p8_code | http://www.snapshot.travel | fp8@tenants.biz | true           | Europe/Prague | 3964bc8b-082d-4d47-b300-9a7b26a3ce91 |
    Given All customerProperties are deleted from DB for customer id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" and property code "filter_list_prop_cust_p1_code"
    Given All customerProperties are deleted from DB for customer id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" and property code "filter_list_prop_cust_p2_code"
    Given All customerProperties are deleted from DB for customer id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" and property code "filter_list_prop_cust_p3_code"
    Given All customerProperties are deleted from DB for customer id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" and property code "filter_list_prop_cust_p4_code"
    Given All customerProperties are deleted from DB for customer id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" and property code "filter_list_prop_cust_p5_code"
    Given All customerProperties are deleted from DB for customer id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" and property code "filter_list_prop_cust_p6_code"
    Given All customerProperties are deleted from DB for customer id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" and property code "filter_list_prop_cust_p7_code"
    Given All customerProperties are deleted from DB for customer id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" and property code "filter_list_prop_cust_p8_code"
    Given Relation between property with code "filter_list_prop_cust_p1_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "chain" from "2015-01-01" to "2030-02-31"
    Given Relation between property with code "filter_list_prop_cust_p2_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "chain" from "2015-02-01" to "2030-03-31"
    Given Relation between property with code "filter_list_prop_cust_p3_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "chain" from "2015-03-01" to "2030-04-31"
    Given Relation between property with code "filter_list_prop_cust_p4_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "chain" from "2015-04-01" to "2030-05-31"
    Given Relation between property with code "filter_list_prop_cust_p5_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "chain" from "2015-05-01" to "2030-06-31"
    Given Relation between property with code "filter_list_prop_cust_p6_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "chain" from "2015-06-01" to "2030-07-31"
    Given Relation between property with code "filter_list_prop_cust_p7_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "chain" from "2015-07-01" to "2030-08-31"
    Given Relation between property with code "filter_list_prop_cust_p8_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "chain" from "2015-08-01" to "2030-09-31"
    Given Relation between property with code "filter_list_prop_cust_p1_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "data_owner" from "2015-09-01" to "2030-10-31"
    Given Relation between property with code "filter_list_prop_cust_p2_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "data_owner" from "2015-10-01" to "2030-11-31"
    Given Relation between property with code "filter_list_prop_cust_p3_code" and customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" exists with type "data_owner" from "2015-11-01" to "2030-12-31"
    When List of customerProperties is got for customer with id "3964bc8b-082d-4d47-b300-9a7b26a3ce91" with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> customerProperties returned
    And Total count is "<total>"
    Examples:
      | limit | cursor | returned | total  | filter                   | sort  | sort_desc |
      | 5     | 0      | 5        | 11     | /null                    | /null |           |
      | 5     | 2      | 5        | 11     | /null                    | /null |           |
      | 5     | 4      | 5        | 11     | /null                    | /null |           |
      | 10    | 0      | 10       | 11     | /null                    | /null |           |
      | 10    | 0      | 10       | 11     | /null                    | /null |           |
      | 5     | 2      | 4        | 6      | valid_from<2015-06-15    |       | valid_to  |
# DP-1722
      | 5     | 0      | 5        | 8      | relationship_type==chain | /null |           |
  #add all fields

    #TODO add test for wrong parameters in url

  Scenario Outline: Getting customers of a property
    When List of customers for property with code "<code>" is got with limit "/null" and cursor "/null" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And All customers are customers of property with code "<code>"
    And All customers are active

    Examples:
      | code    |
      | p1_code |
      | p2_code |
      | p3_code |

  Scenario: Relation between user and customer does not exist
    Given The password of user "custProp1" is "Password01"
    Given Get token for user "custProp1" with password "Password01"
    When Relation between user "b63edcc6-6830-457c-89b1-7801730bd0ae" and customer "40ebf861-7549-46f1-a99f-249716c83b33" is deleted
    When List of customerProperties is got for customer with id "40ebf861-7549-46f1-a99f-249716c83b33" with limit "" and cursor "" and filter "" and sort "" and sort_desc ""
    Then Response code is 404

  Scenario: No relation between user and property, 0 properties visible for user
    Given Relation between user "custProp1" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "true"
    Given The password of user "custProp1" is "Password01"
    Given Get token for user "custProp1" with password "Password01"
    When List of customerProperties is got for customer with id "40ebf861-7549-46f1-a99f-249716c83b33" with limit "" and cursor "" and filter "" and sort "" and sort_desc ""
    Then Response code is 200
    And Total count is "0"

  Scenario: Relation between user and property, 3 property visible for user
    Given Relation between user "custProp1" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "true"
    Given Relation between user "custProp1" and property with code "p1_code" exists
    Given The password of user "custProp1" is "Password01"
    Given Get token for user "custProp1" with password "Password01"
    When List of customerProperties is got for customer with id "40ebf861-7549-46f1-a99f-249716c83b33" with limit "" and cursor "" and filter "" and sort "" and sort_desc ""
    Then Response code is 200
    And Total count is "3"

  Scenario: Relation between user and all properties, 4 properties visible for user
    Given Relation between user "custProp1" and customer with id "40ebf861-7549-46f1-a99f-249716c83b33" exists with isPrimary "true"
    Given Relation between user "custProp1" and property with code "p1_code" exists
    Given Relation between user "custProp1" and property with code "p2_code" exists
    Given Relation between user "custProp1" and property with code "p3_code" exists
    Given The password of user "custProp1" is "Password01"
    Given Get token for user "custProp1" with password "Password01"
    When List of customerProperties is got for customer with id "40ebf861-7549-46f1-a99f-249716c83b33" with limit "" and cursor "" and filter "" and sort "" and sort_desc ""
    Then Response code is 200
    And Total count is "4"