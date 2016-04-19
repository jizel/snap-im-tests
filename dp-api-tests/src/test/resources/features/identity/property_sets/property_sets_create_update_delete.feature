Feature: Property sets create update delete

  #TODO add etag things to get/update/create
  #TODO check adding propery set for customer id not existent

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |

    Given All users are removed for property_sets for customer with code "c1t" with names: ps1_name
    Given All properties are removed from property_sets for customer with code "c1t" with names: ps1_name
    Given All property sets are deleted for customers with codes: c1t
      #FIX ClassCastException - 500
    Given The following property sets exist for customer with code "c1t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |

    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |

  @Smoke
  Scenario: Creating property set for customer with code "c1t"
    When Property set is created for customer with code "c1t"
      | propertySetName  | propertySetDescription | propertySetType |
      | ps1_created_name | ps1_description        | branch          |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "ps1_created_name"
    And Body contains entity with attribute "property_set_type_id" value "branch"
    And "Location" header is set and contains the same property set
    And Etag header is present

  @Smoke
  Scenario: Deleting Property set
    When Property set with name "ps1_name" for customer with code "c1t" is deleted
    Then Response code is "204"
    And Body is empty
    And Property set with same id doesn't exist

  Scenario: Checking error code for deleting property
    When Nonexistent property set id is deleted
    Then Response code is "204"

  Scenario Outline: Updating property set
  Property sets for customer "c1t" were deleted in background, so we don't need to clean here.

    When Property set with name "<propertySetName>" for customer with code "c1t" is updated with data
      | propertySetName           | propertySetDescription   | propertySetType   |
      | <updated_propertySetName> | <propertySetDescription> | <propertySetType> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated property set with name "<updated_propertySetName>" for customer with code "c1t" has data
      | propertySetName           | propertySetDescription   | propertySetType   |
      | <updated_propertySetName> | <propertySetDescription> | <propertySetType> |

    Examples:
      | propertySetName | updated_propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_updated             | ps1_description        | branch          |

  Scenario: Updating property set with outdated etag
    When Property set with name "<propertySetName>" for customer with code "c1t" is updated with data if updated before
      | propertySetName | propertySetDescription | propertySetType |
      | ps3_name        | ps_desc                | branch          |
    Then Response code is "412"
    And Custom code is "57"

    #TODO error codes