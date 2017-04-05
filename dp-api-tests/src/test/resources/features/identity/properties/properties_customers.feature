@Identity
Feature: Properties - customers feature

  Background:
    Given Database is cleaned and default entities are created


  Scenario: Property cannot be deleted if it has relationship with existing customer
    Given The following customers exist with random address
    | id                                   | companyName   | email          | salesforceId    | vatId      | isDemoCustomer | phone         | website                    | timezone      | type  |
    | a792d2b2-3836-4207-a705-42bbecf3d881 | Deletion test | c1@tenants.biz | SALESFORCEID001 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | HOTEL |
    Given The following properties exist with random address and billing address
    | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
    | salesforceid_4 | p4_name      | p4_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |
    Given Relation between property with code "p4_code" and customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" exists
    When Property with code "p4_code" is deleted
    Then Response code is "409"
    And Custom code is 40915
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is "409"
    And Custom code is 40915
    Given Property customer relationship for property with code "p4_code" and customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    When Property with code "p4_code" is deleted
    Then Response code is "204"
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is "204"