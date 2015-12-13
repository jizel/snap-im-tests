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