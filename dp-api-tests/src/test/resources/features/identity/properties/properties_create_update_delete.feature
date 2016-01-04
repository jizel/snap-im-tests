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
