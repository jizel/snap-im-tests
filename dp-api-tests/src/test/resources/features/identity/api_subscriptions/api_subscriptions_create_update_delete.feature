Feature: Api subscription create update delete

  Background:
    Given Database is cleaned
    Given The following applications exist
      | applicationId                        | applicationName            | description               | website                    |
      | 6f552105-0bae-4410-b4bb-bee31567d4fa | Application test company 1 | Application description 1 | http://www.snapshot.travel |
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | 2016-02-22  | Versions description 1 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | apiVersion |
      | 187b49db-673c-44e5-ab40-345ce5e89c37 | a318fd9a-a05d-42d8-8e84-22e904ace111 | apiVersion |


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
    And Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is active


  Scenario: Deactivate api subscription
    When Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is activated
    When Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is deactivated
    Then Response code is 204
    And Body is empty
    And Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is not active


  @Smoke
  Scenario: Delete api subscription
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | b318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 456 | inactive | 2016-02-22  | Versions description 1 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | apiVersion                           |
      | 287b49db-673c-44e5-ab40-345ce5e89c37 | b318fd9a-a05d-42d8-8e84-22e904ace111 | b1111d9a-a05d-42d8-8e84-42e904ace999 |
    When Api subscription with id "287b49db-673c-44e5-ab40-345ce5e89c37" is deleted
    Then Response code is 204
    And  Body is empty
    And Api subscription with id "287b49db-673c-44e5-ab40-345ce5e89c37" is not among all api subscriptions


  Scenario: Delete non-existing api subscription
    When Api subscription with id "non-existing-api-subscription" is deleted
    Then Response code is 204
    And  Body is empty


  Scenario Outline: Update api subscription with invalid data
    When Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is updated with following data
      | applicationVersionId | apiVersion |
      | <appVerId>           | <apiVer>   |
    Then Response code is 400
    And Custom code is <customCode>
    Examples:
      | appVerId                             | apiVer    | customCode | error_note (not used)                                                           |
      |                                      | something | 61         | # "The body parameter 'application_version_id' cannot be empty."                |
      | something                            |           | 61         | # "The body parameter 'api_version' cannot be empty."                           |
      | notValidId                           | something | 63         | # "Param 'application_version_id' is not universally unique identifier (UUID)"  |
      | 6f552105-0bae-4410-b4bb-bee31567d4fa | something | 63         | # "Version with identifier 6f552105-0bae-4410-b4bb-bee31567d4fa was not found." |


  Scenario Outline: Update api subscription with valid data
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | c318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 7   | inactive | 2016-02-22  | Versions description 1 |
      | d318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 8   | inactive | 2016-02-22  | Versions description 1 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | apiVersion |
      | 387b49db-673c-44e5-ab40-345ce5e89c37 | c318fd9a-a05d-42d8-8e84-22e904ace111 | apiVersion |
    When Api subscription with id "387b49db-673c-44e5-ab40-345ce5e89c37" is updated with following data
      | applicationVersionId | apiVersion |
      | <appVerId>           | <apiVer>   |
    Then Response code is 204
    And Body is empty
    And Etag header is present
    Examples:
      | appVerId                             | apiVer         | note                                         |
      | d318fd9a-a05d-42d8-8e84-22e904ace111 | apiVersion     | # applicationVersionID updated to second one |
      | d318fd9a-a05d-42d8-8e84-22e904ace111 | string_updated | # apiVersion string updated                  |
      | d318fd9a-a05d-42d8-8e84-22e904ace111 | string_updated | # no change, but still should successful     |


    # "Version with identifier 6f552105-0bae-4410-b4bb-bee31567d4fa already link with"
  Scenario: Update api subscription with to the same applicationVersionId that already is
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | b318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 456 | inactive | 2016-02-22  | Versions description 1 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | apiVersion                           |
      | 287b49db-673c-44e5-ab40-345ce5e89c37 | b318fd9a-a05d-42d8-8e84-22e904ace111 | b1111d9a-a05d-42d8-8e84-42e904ace999 |
    When Api subscription with id "287b49db-673c-44e5-ab40-345ce5e89c37" is updated with following data
      | applicationVersionId                 | apiVersion  |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | api_updated |
    Then Response code is 400
    And Custom code is 62


  Scenario Outline: Create api subscription with invalid data
    Given The following api subscriptions is created
      | apiSubscriptionId | applicationVersionId | apiVersion   |
      | <apiSubId>        | <appVersionId>       | <apiVersion> |
    Then Response code is <responseCode>
    And Custom code is <customCode>
    Examples:
      | apiSubId                             | appVersionId                         | apiVersion | responseCode | customCode | error_note                                                                                         |
      | something                            | something                            | something  | 400          | 63         | # The value is invalid. Param 'api_subscription_id' is not universally unique identifier (UUID)    |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | something                            | something  | 400          | 63         | # The value is invalid. Param 'application_version_id' is not universally unique identifier (UUID) |
      | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | something  | 400          | 63         | # Version with identifier 1d491c7d-4c70-4be7-ab47-73f36701bcf4 was not found.                      |
      | something                            |                                      | something  | 400          | 61         | # The body parameter 'application_version_id' cannot be empty.                                     |
      | h318fd9a-a05d-42d8-8e84-22e904ace111 | h318fd9a-a05d-42d8-8e84-22e904ace111 |            | 400          | 61         | # The body parameter 'api_version' cannot be empty.                                                |

  @Smoke
  Scenario: Create api subscription with valid data
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | e318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 4   | inactive | 2016-02-22  | Versions description 1 |
    Given The following api subscriptions is created
      | apiSubscriptionId                    | applicationVersionId                 | apiVersion |
      | e318fd9a-a05d-42d8-8e84-22e904ace111 | e318fd9a-a05d-42d8-8e84-22e904ace111 | something  |
    Then Response code is 201
    And Etag header is present
    And Body contains entity with attribute "api_subscription_id" value "e318fd9a-a05d-42d8-8e84-22e904ace111"
    And Body contains entity with attribute "application_version_id " value "e318fd9a-a05d-42d8-8e84-22e904ace111"
    And Body contains entity with attribute "api_version" value "something"