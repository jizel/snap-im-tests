@Identity
Feature: Applications create update delete

  Background:
    Given Database is cleaned

  @Smoke
  Scenario: Create application
    When Application is created
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "Application test company 1"
    And Body contains entity with attribute "website" value "http://www.snapshot.travel"
    And Body contains entity with attribute "application_id" value "a318fd9a-a05d-42d8-8e84-42e904ace123"

  Scenario Outline: Checking error codes for creating applications
    Given Application is created
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                                                                     | method | module   | url                    | error_code | custom_code |
      | /messages/identity/applications/create_application_missing_application_name.json    | POST   | identity | /identity/applications | 422        | 42201       |
      | /messages/identity/applications/create_application_not_unique_application_name.json | POST   | identity | /identity/applications | 422        | 42201       |
      | /messages/identity/applications/create_application_not_unique_application_id.json   | POST   | identity | /identity/applications | 422        | 42201       |
      | /messages/identity/applications/create_application_wrong_website_value.json         | POST   | identity | /identity/applications | 422        | 42201       |

  @Smoke
  Scenario: Deleting application
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    When Application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is deleted
    Then Response code is "204"
    And Body is empty
    And Application with same id does not exist

  Scenario: Checking error code for deleting application
    When Nonexistent application id is deleted
    Then Response code is "204"

  Scenario Outline: Updating application
    Given Application is created
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    When Application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is updated with data
      | applicationName           | description           | website           |
      | <updated_applicationName> | <updated_description> | <updated_website> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" has data
      | applicationName           | description           | website           |
      | <updated_applicationName> | <updated_description> | <updated_website> |

    Examples:
      | updated_applicationName | updated_description | updated_website             |
      | Application test        |                     | http://www.snap.shot.travel |
      | Application test 1      | New description     | http://www.snapshot.travel  |

  Scenario: Updating application with outdated etag
    Given Application is created
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    When Application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is updated with data if updated before
      | applicationName  | description | website                    |
      | Updated App Name |             | http://www.snapshot.travel |
    Then Response code is "412"
    And Custom code is "41202"

  Scenario Outline: Send POST request with empty body to all applications endpoints
    When The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Application versions are created for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123"
      | versionId                            | apiManagerId | versionName | status   | description            |
      | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 1   | inactive | Versions description 1 |
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                                                    |
      | identity/applications/                                                                                                                 |
      | identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123                                                                             |
      | identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions                                                        |
      | identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions/b595fc9d-f5ca-45e7-a15d-c8a97108d884                   |
#      Fails because of DP-1583
      | identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions                                                    |
      | identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions/b595fc9d-f5ca-45e7-a15d-c8a97108d884/api_subscriptions |
