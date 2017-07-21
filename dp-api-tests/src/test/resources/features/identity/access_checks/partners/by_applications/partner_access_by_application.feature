@Identity
Feature: Access to /identity/partners endpoint for internal commercial and external applications
  - The acces is denied in external application to all types of users
  - The access is granted to internal commercial application only if it has a commercial subscription

  Background:
    Given Database is cleaned and default entities are created
    And The following user is created for customer "06000000-0000-4444-8888-000000000001"
      | type     | username      | firstName | lastName | email                 | timezone      | languageCode | isActive |
      | customer | customerUser  | Customer  | User2    | cust1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    And Relation between user "customerUser" and property "08000000-0000-4444-8888-000000000001" exists


  Scenario Outline: Access to endpoints is granted to the customer user if the internal application has a commercial subscription with the user's customer
    And The following applications exist
      | name                   | id                                   | partnerId                            | isInternal | website                 |
      | InternalNoSubscription | 33300000-0000-4000-a000-000000000333 | 07000000-0000-4444-8888-000000000002 | true       | http://www.snapshot.com |
    Given Application permission table is populated for application "InternalNoSubscription"
    Given The following application versions exists
      | id                                   | isNonCommercial | apiManagerId | name                        | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | false           | 2            | internalWithSubscription    | certified | Active version description   | 03000000-0000-4444-8888-000000000000 |
      | 22200000-0000-4000-a000-000000000444 | false           | 3            | internalWithoutSubscription | certified | Active version description   | 33300000-0000-4000-a000-000000000333 |
    When List of <entity> is requested by user "customerUser" for application version "internalWithoutSubscription"
    Then Response code is "403"
    When List of <entity> is requested by user "customerUser" for application version "internalWithSubscription"
    Then Response code is "200"
    Examples:
    | entity                   |
    | partners                 |
    | applications             |
    | application versions     |
#    | api subscriptions        |
    | commercial subscriptions |




