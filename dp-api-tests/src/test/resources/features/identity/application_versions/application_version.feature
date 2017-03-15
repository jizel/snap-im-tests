@Identity
Feature: Applications versions GET (directly) - DP-1784
  - GET tests for endpoint applications/application_version/version_id
  - This endpoint only works for GET, data must be created via "old" endpoints
  - Behavior might change slightly in the future, refactor tests properly

  Background:
    Given Database is cleaned and default entities are created


  @Smoke
  Scenario: Getting application versions
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName | status   | description            | applicationId                        |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | Versions description 1 | 11111111-0000-4000-a000-111111111111 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" is requested directly
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "application_version_id"
    And Body contains entity with attribute "name" value "Version 123"
    And Body contains entity with attribute "api_key" value "123"
    And Body contains entity with attribute "status" value "inactive"
    And Body contains entity with attribute "description" value "Versions description 1"

  Scenario: Checking error code for nonexistent application versions
    When Application version with id "nonexistent" is requested directly
    Then Response code is "404"
    And Custom code is "40402"

  Scenario: Only GET is allowed for application_versions endpoint
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName | status   | description            | applicationId                        |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | Versions description 1 | 11111111-0000-4000-a000-111111111111 |
    When Empty POST request is sent to "identity/application_versions/a318fd9a-a05d-42d8-8e84-22e904ace111" on module "identity"
    Then Response code is "405"
    And Custom code is "40501"
    When DELETE request is sent to "identity/application_versions/a318fd9a-a05d-42d8-8e84-22e904ace111" on module "identity"
    Then Response code is "405"
    And Custom code is "40501"