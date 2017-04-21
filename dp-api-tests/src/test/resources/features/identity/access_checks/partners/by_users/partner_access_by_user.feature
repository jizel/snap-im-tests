@Identity
Feature: Access to /identity/partners endpoint for internal non-commercial application
  - The access is granted to snapshot user.
  - The access is granted to partner user, only if he has a relationship with some partner
  - The access is granted to customer user, only if he has a relationship with some customer

  Background:
    Given Database is cleaned and default entities are created
    And The following user is created
      | userType | userName      | firstName | lastName | email                 | timezone      | culture | isActive |
      | partner  | partnerUser   | Partner   | User1    | part1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    And The following user is created for customer "11111111-0000-4000-a000-555555555555" with is_active "false"
      | userType | userName      | firstName | lastName | email                 | timezone      | culture | isActive |
      | customer | customerUser  | Customer  | User2    | cust1@snapshot.travel | Europe/Prague | cs-CZ   | true     |

  @smoke
  Scenario Outline: Get access to partners, applictions, app versions, commercial subscriptions by snapshot, customer, and partner user for non-commercial app
    When List of <entity> is requested by user "defaultSnapshotUser"
    Then Response code is "200"
    And There are 1 <entity> returned
    When List of <entity> is requested by user "customerUser"
    Then Response code is "403"
    Given Relation between user "customerUser" and customer "11111111-0000-4000-a000-555555555555" is activated
    When List of <entity> is requested by user "customerUser"
    Then Response code is "200"
    And There are 1 <entity> returned
    When List of <entity> is requested by user "partnerUser"
    Then Response code is "403"
    Given Relation between user "partnerUser" and partner "11111111-0000-4000-a000-222222222222" exists
    When List of <entity> is requested by user "partnerUser"
    Then Response code is "200"
    And There are 1 <entity> returned
    Examples:
    | entity                   |
    | partners                 |
    | applications             |
    | application versions     |
#    | api subscriptions        |
    | commercial subscriptions |
