Feature: Api subscription get

  Background:
    Given Database is cleaned
    Given The following applications exist
      | applicationId                        | applicationName            | description               | website                    |
      | 6f552105-0bae-4410-b4bb-bee31567d4fa | Application test company 1 | Application description 1 | http://www.snapshot.travel |
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | 2016-02-22  | Versions description 1 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | apiVersion                           |
      | 187b49db-673c-44e5-ab40-345ce5e89c37 | a318fd9a-a05d-42d8-8e84-22e904ace111 | b1111d9a-a05d-42d8-8e84-42e904ace999 |

  Scenario: Trying to create application subscription with the same versionID
    Given Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is activated
    When Trying to create second api subscription with the same versionID
    Then Response code is 400
    And Custom code is 62

  @Smoke
  Scenario: Activate api subscription
    When Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is activated
    Then Response code is 204
    And Body is empty
    And Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is among all api subscriptions

  Scenario: Deactivate api subscription
    When Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is activated
    When Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is deactivated
    Then Response code is 204
    And Body is empty
    And Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is not among all api subscriptions

  @Smoke
  Scenario: Delete api subscription

  Scenario: Delete non-existing api subscription

  Scenario: Update api subscription
    #several examples

  Scenario: Create api subscription
     #several examples
   #check error codes