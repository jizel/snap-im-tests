@Identity
@Properties
Feature: Properties property sets create update delete
  - User is able to RUD relationship between property and property set (Create not implemented for now)
  - This is a test suite for properties/property_id/property_sets relationship
  - Most test cases for this are covered under properties_property_sets_access_check.feature

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 01112111-cdaf-439a-8bef-3140f56c657e | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
      | id                                   | name            | description            | type            |
      | 00002111-cdaf-439a-8bef-3140f56c657e | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists

    Scenario: Update Property - Property Set relationship with empty body - DP-1679
      When  Relation between property with code "p1_code" and property set "ps1_name" is updated with empty body
      Then Response code is "204"

    Scenario: Property cannot be deleted when it belongs to any property set (and vice versa)
      When Property with code "p1_code" is deleted
      Then Response code is "409"
      And Custom code is 40915
      When Property set "ps1_name" is deleted
      Then Response code is "409"
      And Custom code is 40915
      When Relation between property with code "p1_code" and property set "ps1_name" is deleted
      When Property with code "p1_code" is deleted
      Then Response code is "204"