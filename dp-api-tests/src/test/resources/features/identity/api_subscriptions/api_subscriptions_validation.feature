Feature: Api subscriptions validation

  Background:
    Given Database is cleaned
    Given The following applications exist
      | applicationId                        | applicationName            | description               | website                    |
      | 6f552105-0bae-4410-b4bb-bee31567d4fa | Application test company 1 | Application description 1 | http://www.snapshot.travel |
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 1   | inactive | 2016-02-01  | Versions description 1 |
      | c595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 2   | inactive | 2016-02-01  | Versions description 2 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | apiVersion       |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | validation_api_1 |

  Scenario Outline: Object update - api_subscription - invalid values
    When Update api subscription with id "<api_subscription>", field "<updated_field>", its value "<value>"
    Then Response code is 400
    And Custom code is "<custom_code>"
    Examples:
      | api_subscription                     | updated_field        | custom_code | value                          | errorNote                                                                                          |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | apiVersion           | 61          |                                | # The body parameter 'api_version' cannot be empty                                                 |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | applicationVersionId | 63          | invalid_application_version_id | # The value is invalid. Param 'application_version_id' is not universally unique identifier (UUID) |

  Scenario Outline: Object update - api_subscription - valid values
    When Update api subscription with id "<api_subscription>", field "<updated_field>", its value "<value>"
    Then Response code is 204
    And  Body is empty
    Examples:
      | api_subscription                     | updated_field        | value                                |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | isActive             | 4                                    |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | isActive             | 0                                    |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | isActive             | 1                                    |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | isActive             | -1                                   |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | isActive             | 99999999                             |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | apiVersion           | someStringValueToBeChanged           |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | applicationVersionId | c595fc9d-f5ca-45e7-a15d-c8a97108d884 |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | applicationVersionId | b595fc9d-f5ca-45e7-a15d-c8a97108d884 |
