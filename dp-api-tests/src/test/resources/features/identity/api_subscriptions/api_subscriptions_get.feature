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


  @Smoke
  Scenario: Getting api subscription
    When Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is got
    Then Response code is 200
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "application_version_id" value "a318fd9a-a05d-42d8-8e84-22e904ace111"
    And Body contains entity with attribute "api_version" value "b1111d9a-a05d-42d8-8e84-42e904ace999"
    And Body contains entity with attribute "api_subscription_id" value "187b49db-673c-44e5-ab40-345ce5e89c37"


  Scenario: Getting customer api subscription with etag
    When Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is got with etag
    Then Response code is 304
    And Body is empty


  Scenario: Checking error code for getting nonExisting api subscription
    When Api subscription with id "nonExistingApi" is got
    Then Response code is 404
    And Content type is "application/json"
    And Custom code is 152

  Scenario Outline: Getting list of api subscriptions
    Examples:
      |  |


  Scenario Outline: Checking error codes for getting list of api subscriptions
    Examples:
      |  |

  Scenario Outline: Filtering list of api subscriptions
    Examples:
      |  |