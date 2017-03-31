Feature: User to Property set relationship feature
  - User can have a relationship to property set via users/user_id/property_sets endpoint
  - User can perform all crud operations

#  TODO - implement all basic positive and negative tests for this endpoint

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | id                                   | userType | userName      | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | userWithSet   | Default0  | User0    | def0@snapshot.travel | Europe/Prague | cs-CZ   |
      | 6d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | userWithNoSet | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "userWithSet"
      | id                                   | name            | description            | type            |
      | 10012345-48f0-4f00-9bec-e2329a8bdaac | ps1_name        | ps1_description        | brand           |

  Scenario: Duplicate creation of user-property set relationship returns correct error response - DP-1661
    When Property set "ps1_name" is added to user "userWithNoSet"
    Then Response code is "201"
    When Property set "ps1_name" is added to user "userWithNoSet"
    Then Response code is "409"
    And Custom code is 40902

  Scenario Outline: Empty update of (customer type) user - property set relationship returns correct code - DP-1771
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                    |
      | identity/users/5d829079-48f0-4f00-9bec-e2329a8bdaac/property_sets/10012345-48f0-4f00-9bec-e2329a8bdaac |