Feature: customers_multiproperty

  Background: Initial data to DB
    Given Database is cleaned
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |

     #everything - all properties available
     #limited - some properties not available
     #snapshot - user that has everything accessible
     #random - no access to properties
    Given The following users exist
      | userType | userName   | firstName | lastName | email                  | timezone      | culture |
      | customer | everything | Default1  | User1    | def1@snapshot.travel   | Europe/Prague | cs-CZ   |
      | customer | limited    | Default2  | User2    | def2@snapshot.travel   | Europe/Prague | cs-CZ   |
      | snapshot | snapshot   | snapshot  | root     | def3@snapshot.travel   | Europe/Prague | cs-CZ   |
      | customer | random     | random    | random   | random@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The password of user "everything" is "Password01"
    Given The password of user "limited" is "Password01"
    Given The password of user "snapshot" is "Password01"
    Given The password of user "random" is "Password01"

    Given Relation between user with username "everything" and customer with code "c1t" exists with isPrimary "true"
    Given Relation between user with username "limited" and customer with code "c1t" exists with isPrimary "true"
    Given Relation between user with username "random" and customer with code "c1t" exists with isPrimary "true"

    #Even though user "snapshot" do not have relation with customer he should have access to all property sets
    #Given Relation between user with username "snapshot" and customer with code "c1t" exists with isPrimary "true"

    Given The following property sets exist for customer with code "c1t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
      | ps2_name        | ps2_description        | branch          |

    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague |
      | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague |
      | salesforceid_4 | p4_name      | p4_code      | http://www.snapshot.travel | p4@tenants.biz | true           | Europe/Prague |
      | salesforceid_5 | p5_name      | p5_code      | http://www.snapshot.travel | p5@tenants.biz | true           | Europe/Prague |
      | salesforceid_6 | p6_name      | p6_code      | http://www.snapshot.travel | p6@tenants.biz | true           | Europe/Prague |

    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "anchor" from "2016-01-01" to "2016-02-28"
    Given Relation between property with code "p2_code" and customer with code "c1t" exists with type "anchor" from "2016-01-01" to "2016-02-28"
    Given Relation between property with code "p3_code" and customer with code "c1t" exists with type "anchor" from "2016-01-01" to "2016-02-28"
    Given Relation between property with code "p4_code" and customer with code "c1t" exists with type "anchor" from "2016-01-01" to "2016-02-28"
    Given Relation between property with code "p5_code" and customer with code "c1t" exists with type "anchor" from "2016-01-01" to "2016-02-28"
    Given Relation between property with code "p6_code" and customer with code "c1t" exists with type "anchor" from "2016-01-01" to "2016-02-28"


  Scenario Outline: Accessing customer's property_sets with everything user with each property in just one ps
  Property set 1 and property set 2 properties are disjuncted, everything user has access just for properties from ps1

    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p2_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p3_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p4_code" and property set with name "ps1_name" for customer with code "c1t" exists

    Given Relation between property with code "p5_code" and property set with name "ps2_name" for customer with code "c1t" exists
    Given Relation between property with code "p6_code" and property set with name "ps2_name" for customer with code "c1t" exists

      #user everything should get properties from ps1
      #limited should get only property set 2
      #random user has no access to properties
    Given Relation between user with username "everything" and property with code "p1_code" exists
    Given Relation between user with username "everything" and property with code "p2_code" exists
    Given Relation between user with username "everything" and property with code "p3_code" exists
    Given Relation between user with username "everything" and property with code "p4_code" exists

    Given Relation between user with username "limited" and property with code "p5_code" exists
    Given Relation between user with username "limited" and property with code "p6_code" exists

    Given Get token for user "<username>" with password "<password>"
    When List of property sets for customer "c1t" is got with limit "100" and cursor "0" and filter "/null" and sort "<order>" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <count> customer property sets returned
    And There are property sets with following names returned in order: <names>

    Examples:
      | username   | password   | count | names              | order             |
      | snapshot   | Password01 | 2     | ps1_name, ps2_name | property_set_name |
      | everything | Password01 | 1     | ps1_name           | /null             |
      | everything | Password01 | 1     | ps1_name           | property_set_name |
      | limited    | Password01 | 1     | ps2_name           | property_set_name |
      | random     | Password01 | 0     |                    | /null             |


  Scenario Outline: Accessing customer's property_sets with everything user with one property in both sets
  There are same properties in property set 1 and 2, everything user has access to all properties, both property sets should be returned

    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p2_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p3_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p4_code" and property set with name "ps1_name" for customer with code "c1t" exists

    Given Relation between property with code "p1_code" and property set with name "ps2_name" for customer with code "c1t" exists
    Given Relation between property with code "p2_code" and property set with name "ps2_name" for customer with code "c1t" exists

    #user everything should get both property sets
    #user limited should get only one property set
    #user random should get 0 property sets
    Given Relation between user with username "everything" and property with code "p1_code" exists
    Given Relation between user with username "everything" and property with code "p2_code" exists
    Given Relation between user with username "everything" and property with code "p3_code" exists
    Given Relation between user with username "everything" and property with code "p4_code" exists

    Given Relation between user with username "limited" and property with code "p1_code" exists
    Given Relation between user with username "limited" and property with code "p2_code" exists

    Given Get token for user "<username>" with password "<password>"

    When List of property sets for customer "c1t" is got with limit "100" and cursor "0" and filter "/null" and sort "<order>" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <count> customer property sets returned
    And There are property sets with following names returned in order: <names>

    Examples:
      | username   | password   | count | names              | order             |
      | snapshot   | Password01 | 2     | ps1_name, ps2_name | property_set_name |
      | everything | Password01 | 2     | ps1_name, ps2_name | /null             |
      | everything | Password01 | 2     | ps1_name, ps2_name | property_set_name |
      | limited    | Password01 | 1     | ps2_name           | property_set_name |
      | random     | Password01 | 0     |                    | /null             |