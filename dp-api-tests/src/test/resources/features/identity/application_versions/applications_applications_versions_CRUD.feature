@Identity
Feature: Applications versions create update delete
  - CRUD tests for endpoints applications/application_id/application_version and applications/application_id/application_version/version_id

  Background:
    Given Database is cleaned and default entities are created

  @Smoke
  Scenario: Create/delete applications versions
    When Application version is created for application with id "03000000-0000-4444-8888-000000000000"
      | id                                   | apiManagerId | name        | status   | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 1   | inactive | Versions description 1 |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "Version 1"
    And Body contains entity with attribute "status" value "inactive"
    And Body contains entity with attribute "api_key" value "123"
    And Body contains entity with attribute "description" value "Versions description 1"
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" is deleted
    Then Response code is "204"
    And Body is empty
    And Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" does not exist

  Scenario Outline: Checking error codes for creating application versions
    Given The following application versions exists
      | id                                   | apiManagerId | name        | status   | description            | applicationId                        |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 1   | inactive | Versions description 1 | 03000000-0000-4444-8888-000000000000 |
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                                                             | method | module   | url                            | error_code | custom_code |
      | /messages/identity/applications/create_version_missing_version_name.json    | POST   | identity | /identity/application_versions | 422        | 42201       |
      | /messages/identity/applications/create_version_missing_api_manager_id.json  | POST   | identity | /identity/application_versions | 422        | 42201       |
      | /messages/identity/applications/create_version_missing_status.json          | POST   | identity | /identity/application_versions | 422        | 42201       |
      | /messages/identity/applications/create_version_not_unique_version_name.json | POST   | identity | /identity/application_versions | 422        | 42201       |
      | /messages/identity/applications/create_version_not_unique_version_id.json   | POST   | identity | /identity/application_versions | 422        | 42201       |
      | /messages/identity/applications/create_version_missing_application_id.json  | POST   | identity | /identity/application_versions | 422        | 42201       |

  Scenario: Checking error code for deleting application version
    When Nonexistent application version id is deleted
    Then Response code is "404"

  Scenario Outline: Updating application
    Given The following application versions exists
      | id                                   | apiManagerId | name        | status   | description            | applicationId                        |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 1   | inactive | Versions description 1 | 03000000-0000-4444-8888-000000000000 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "03000000-0000-4444-8888-000000000000" is updated with data
      | apiManagerId           | name                  | status           | description           |
      | <updated_apiManagerId> | <updated_versionName> | <updated_status> | <updated_description> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "03000000-0000-4444-8888-000000000000" has data
      | apiManagerId           | name                  | status           | description           |
      | <updated_apiManagerId> | <updated_versionName> | <updated_status> | <updated_description> |

    Examples:
      | updated_apiManagerId | updated_versionName  | updated_status | updated_description |
      | 123-XYZ              | Updated version name | certified      | Updated description |
      | 99999                | New name             | certified      | Updated description |

  Scenario: Updating application version with outdated etag
    Given The following application versions exists
      | id                                   | apiManagerId | name        | status   | description            | applicationId                        |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 1   | inactive | Versions description 1 | 03000000-0000-4444-8888-000000000000 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" is updated with data with invalid etag
      | apiManagerId | name                 | status    | description         |
      | 123-XYZ      | Updated version name | certified | Updated description |
    Then Response code is "412"
    And Custom code is "41202"

  Scenario: Duplicate creation of application version returns correct response - DP-1661
    When Application version is created for application with id "03000000-0000-4444-8888-000000000000"
      | id                                   | apiManagerId | name        | status   | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | Versions description 1 |
    Then Response code is 201
    When Application version is created for application with id "03000000-0000-4444-8888-000000000000"
      | id                                   | apiManagerId | name        | status   | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Same ID App | inactive | Versions description 1 |
    Then Response code is 409
    And Custom code is 40902
