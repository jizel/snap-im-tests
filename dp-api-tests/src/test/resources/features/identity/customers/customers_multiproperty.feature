@Identity
Feature: Customers multiproperty

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone          |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |

     #everything - all properties available
     #limited - some properties not available
     #snapshot - user that has everything accessible
     #random - no access to properties
    Given The following users exist for customer "a792d2b2-3836-4207-a705-42bbecf3d881" as primary "false"
      | id                                   | type     | username   | firstName | lastName | email                  | timezone      | languageCode |
      | a63edcc6-6830-457c-89b1-7801730bd0ae | customer | everything | Default1  | User1    | def1@snapshot.travel   | Europe/Prague | cs-CZ   |
      | ce5d9f8a-9623-4154-aba4-109e64cdc149 | customer | limited    | Default2  | User2    | def2@snapshot.travel   | Europe/Prague | cs-CZ   |
      | 3d262796-1ba1-442c-92fb-a95f6a598ffc | snapshot | snapshot   | snapshot  | root     | def3@snapshot.travel   | Europe/Prague | cs-CZ   |
      | 20d70d2e-e299-4860-b102-4e886fb99ac1 | customer | random     | random    | random   | random@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "a792d2b2-3836-4207-a705-42bbecf3d881"
      | name            | description            | type            |
      | ps1_name        | ps1_description        | brand           |
      | ps2_name        | ps2_description        | brand           |
    Given The following properties exist with random address and billing address for user "3d262796-1ba1-442c-92fb-a95f6a598ffc"
      | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |
      | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |
      | salesforceid_4 | p4_name      | p4_code      | http://www.snapshot.travel | p4@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |
      | salesforceid_5 | p5_name      | p5_code      | http://www.snapshot.travel | p5@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |
      | salesforceid_6 | p6_name      | p6_code      | http://www.snapshot.travel | p6@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |

    Given Relation between property with code "p1_code" and customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" exists with type "owner" from "2016-01-01" to "2016-02-28"
    Given Relation between property with code "p2_code" and customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" exists with type "owner" from "2016-01-01" to "2016-02-28"
    Given Relation between property with code "p3_code" and customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" exists with type "owner" from "2016-01-01" to "2016-02-28"
    Given Relation between property with code "p4_code" and customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" exists with type "owner" from "2016-01-01" to "2016-02-28"
    Given Relation between property with code "p5_code" and customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" exists with type "owner" from "2016-01-01" to "2016-02-28"
    Given Relation between property with code "p6_code" and customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" exists with type "owner" from "2016-01-01" to "2016-02-28"


  Scenario Outline: Accessing customer's property_sets with everything user with each property in just one ps
  - Property set 1 and property set 2 properties are disjuncted, everything user has access just for properties from ps1
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p2_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p3_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p4_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p5_code" and property set with name "ps2_name" exists
    Given Relation between property with code "p6_code" and property set with name "ps2_name" exists
      #user everything should get properties from ps1
      #limited should get only property set 2
      #random user has no access to properties
    Given Relation between user "everything" and property with code "p1_code" exists
    Given Relation between user "everything" and property with code "p2_code" exists
    Given Relation between user "everything" and property with code "p3_code" exists
    Given Relation between user "everything" and property with code "p4_code" exists
    Given Relation between user "limited" and property with code "p5_code" exists
    Given Relation between user "limited" and property with code "p6_code" exists
    When List of property sets for customer "a792d2b2-3836-4207-a705-42bbecf3d881" is got by user "<username>" with limit "100" and cursor "0" and filter "/null" and sort "<order>" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <count> customer property sets returned
    And There are property sets with following names returned in order: <names>

    Examples:
      | username   | count | names              | order |
      | snapshot   | 2     | ps1_name, ps2_name | name  |
      | everything | 1     | ps1_name           | /null |
      | everything | 1     | ps1_name           | name  |
      | limited    | 1     | ps2_name           | name  |
      | random     | 0     |                    | /null |


  Scenario Outline: Accessing customer's property_sets with everything user with one property in both sets
  There are same properties in property set 1 and 2, everything user has access to all properties, both property sets should be returned
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p2_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p3_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p4_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p1_code" and property set with name "ps2_name" exists
    Given Relation between property with code "p2_code" and property set with name "ps2_name" exists
    #user everything should get both property sets
    #user limited should get only one property set
    #user random should get 0 property sets
    Given Relation between user "everything" and property with code "p1_code" exists
    Given Relation between user "everything" and property with code "p2_code" exists
    Given Relation between user "everything" and property with code "p3_code" exists
    Given Relation between user "everything" and property with code "p4_code" exists
    Given Relation between user "limited" and property with code "p1_code" exists
    Given Relation between user "limited" and property with code "p2_code" exists
    When List of property sets for customer "a792d2b2-3836-4207-a705-42bbecf3d881" is got by user "<username>" with limit "100" and cursor "0" and filter "/null" and sort "<order>" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <count> customer property sets returned
    And There are property sets with following names returned in order: <names>

    Examples:
      | username   | count | names              | order |
      | snapshot   | 2     | ps1_name, ps2_name | name  |
      | everything | 2     |                    | /null |
      | everything | 2     | ps1_name, ps2_name | name  |
      | limited    | 1     | ps2_name           | name  |
      | random     | 0     |                    | /null |