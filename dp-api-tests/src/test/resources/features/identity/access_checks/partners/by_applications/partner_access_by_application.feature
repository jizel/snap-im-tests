@Identity
Feature: Access to /identity/partners endpoint for internal commercial and external applications
  - The acces is denied in external application to all types of users
  - The access is granted to internal commercial application only if it has a commercial subscription

  Background:
    Given Database is cleaned and default entities are created
    And The following user is created
      | userType | userName      | firstName | lastName | email                 | timezone      | culture | isActive |
      | partner  | partnerUser   | Partner   | User1    | part1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    And The following user is created for customer "11111111-0000-4000-a000-555555555555"
      | userType | userName      | firstName | lastName | email                 | timezone      | culture | isActive |
      | customer | customerUser  | Customer  | User2    | cust1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    And The following applications exist
      | applicationName     | id                                   | partnerId                            | isInternal | website                 |
      | ExternalApplication | 22200000-0000-4000-a000-000000000222 | 11111111-0000-4000-a000-222222222222 | false      | http://www.snapshot.com |
    And The following application versions exists
      | id                                   | isNonCommercial | apiManagerId | versionName                 | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000555 | false           | 2            | externalWithSubscription    | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
      | 22200000-0000-4000-a000-000000000666 | true            | 3            | externalNonCommercial       | certified | Active version description   | 22200000-0000-4000-a000-000000000222 |
    And The following commercial subscriptions exist
      | customerId                           | propertyId                           | applicationId                        |
      | 11111111-0000-4000-a000-555555555555 | 11111111-0000-4000-a000-666666666666 | 22200000-0000-4000-a000-000000000222 |
    And Relation between user "partnerUser" and partner "11111111-0000-4000-a000-222222222222" exists
    And Relation between user "customerUser" and property "11111111-0000-4000-a000-666666666666" exists

  Scenario Outline: Access to endpoints is denied to external app
    When List of <entity> is requested by user "defaultSnapshotUser" for application version "externalWithSubscription"
    Then Response code is "403"
    When List of <entity> is requested by user "partnerUser" for application version "externalWithSubscription"
    Then Response code is "403"
    When List of <entity> is requested by user "customerUser" for application version "externalWithSubscription"
    Then Response code is "403"
    When List of <entity> is requested by user "defaultSnapshotUser" for application version "externalNonCommercial"
    Then Response code is "403"
    When List of <entity> is requested by user "partnerUser" for application version "externalNonCommercial"
    Then Response code is "403"
    When List of <entity> is requested by user "customerUser" for application version "externalNonCommercial"
    Then Response code is "403"
    Examples:
      | entity                   |
      | partners                 |
      | applications             |
      | application versions     |
#      | api subscriptions        |
      | commercial subscriptions |

  Scenario Outline: Access to endpoints is granted to the customer user if the internal application has a commercial subscription with the user's customer
    And The following applications exist
      | applicationName        | id                                   | partnerId                            | isInternal | website                 |
      | InternalNoSubscription | 33300000-0000-4000-a000-000000000333 | 11111111-0000-4000-a000-222222222222 | true       | http://www.snapshot.com |
    Given The following application versions exists
      | id                                   | isNonCommercial | apiManagerId | versionName                 | status    | description                  | applicationId                        |
      | 22200000-0000-4000-a000-000000000333 | false           | 2            | internalWithSubscription    | certified | Active version description   | 11111111-0000-4000-a000-111111111111 |
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




