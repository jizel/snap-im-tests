Feature: Applications versions create update delete

  Background:
    Given Database is cleaned

  @Smoke
  Scenario: Create applications versions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    When Application versions are created for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123"
      | apiManagerId | versionName | status   | description            |
      | 123          | Version 1   | inactive | Versions description 1 |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "Version 1"
    And Body contains entity with attribute "status" value "inactive"
    And Body contains entity with attribute "api_manager_id" value "123"
    And Body contains entity with attribute "description" value "Versions description 1"

  Scenario Outline: Checking error codes for creating application versions
    Given Application is created
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 1   | inactive | Versions description 1 |
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                                                             | method | module   | url                                                                              | error_code | custom_code |
      | /messages/identity/applications/create_version_missing_version_name.json    | POST   | identity | /identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions | 400        | 53          |
      | /messages/identity/applications/create_version_missing_api_manager_id.json  | POST   | identity | /identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions | 400        | 53          |
      | /messages/identity/applications/create_version_missing_status.json          | POST   | identity | /identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions | 400        | 53          |
      | /messages/identity/applications/create_version_not_unique_version_name.json | POST   | identity | /identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions | 400        | 62          |
      | /messages/identity/applications/create_version_not_unique_version_id.json   | POST   | identity | /identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions | 400        | 62          |

  @Smoke
  Scenario: Deleting application version
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 1   | inactive | Versions description 1 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is deleted
    Then Response code is "204"
    And Body is empty
    And Application version with same id for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" does not exist

  Scenario: Checking error code for deleting application version
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    When Nonexistent application version for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is deleted
    Then Response code is "204"

  Scenario Outline: Updating application
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 1   | inactive | Versions description 1 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is updated with data
      | apiManagerId           | versionName           | status           | description           |
      | <updated_apiManagerId> | <updated_versionName> | <updated_status> | <updated_description> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" has data
      | apiManagerId           | versionName           | status           | description           |
      | <updated_apiManagerId> | <updated_versionName> | <updated_status> | <updated_description> |

    Examples:
      | updated_apiManagerId | updated_versionName  | updated_status | updated_description |
      | 123-XYZ              | Updated version name | certified      | Updated description |
      | 99999                | New name             | certified      | Updated description |

  Scenario: Updating application version with outdated etag
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 1   | inactive | Versions description 1 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is updated with data with invalid etag
      | apiManagerId | versionName          | status    | description         |
      | 123-XYZ      | Updated version name | certified | Updated description |
    Then Response code is "412"
    And Custom code is "57"
