@Identity
Feature: Applications create update delete

  Background:
    Given Database is cleaned and default entities are created

  @Smoke
  Scenario: Create get delete application
    When Application is created
      | applicationName            | description               | website                    | partnerId                            | id                                   | isInternal |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | 11111111-0000-4000-a000-222222222222 | abc8fd9a-a05d-42d8-8e84-42e904ace123 | false      |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "Application test company 1"
    And Body contains entity with attribute "website" value "http://www.snapshot.travel"
    And Body contains entity with attribute "application_id" value "abc8fd9a-a05d-42d8-8e84-42e904ace123"
    And Body contains entity with attribute "is_active" value "true"
    And Body contains entity with attribute "is_internal" value "false"
    When Application with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is requested
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "application_id"
    And Body contains entity with attribute "name" value "Application test company 1"
    And Body contains entity with attribute "description" value "Application description 1"
    And Body contains entity with attribute "website" value "http://www.snapshot.travel"
    When Application with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is deleted
    Then Response code is "204"
    And Body is empty
    And Application with same id does not exist

  Scenario Outline: Checking error codes for creating applications
    Given Application is created
      | applicationName            | description               | website                    | partnerId                            |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | 11111111-0000-4000-a000-222222222222 |
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                                                                     | method | module   | url                    | error_code | custom_code |
      | /messages/identity/applications/create_application_missing_application_name.json    | POST   | identity | /identity/applications | 422        | 42201       |
      | /messages/identity/applications/create_application_not_unique_application_name.json | POST   | identity | /identity/applications | 422        | 42201       |
      | /messages/identity/applications/create_application_not_unique_application_id.json   | POST   | identity | /identity/applications | 422        | 42201       |
      | /messages/identity/applications/create_application_wrong_website_value.json         | POST   | identity | /identity/applications | 422        | 42201       |

  Scenario: Checking error code for deleting application
    When Nonexistent application id is deleted
    Then Response code is "404"

  Scenario Outline: Updating application
    Given The following applications exist
      | applicationName            | description               | website                    | partnerId                            |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | 11111111-0000-4000-a000-222222222222 |
    When Application "Application test company 1" is updated with data
      | applicationName           | description           | website           |
      | <updated_applicationName> | <updated_description> | <updated_website> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated application "<updated_applicationName>" has data
      | applicationName           | description           | website           |
      | <updated_applicationName> | <updated_description> | <updated_website> |

    Examples:
      | updated_applicationName | updated_description | updated_website             |
      | Application test        |                     | http://www.snap.shot.travel |
      | Application test 1      | New description     | http://www.snapshot.travel  |

  Scenario: Updating application with outdated etag
    Given The following applications exist
      | id                                   | applicationName            | description               | website                    | partnerId                            |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Application test company 1 | Application description 1 | http://www.snapshot.travel | 11111111-0000-4000-a000-222222222222 |
    When Application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is updated with invalid etag
      | applicationName  | description | website                    |
      | Updated App Name |             | http://www.snapshot.travel |
    Then Response code is "412"
    And Custom code is "41202"

  Scenario Outline: Send POST request with empty body to all applications endpoints
    When The following applications exist
      | id                                   | applicationName            | description               | website                    | partnerId                            |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Application test company 1 | Application description 1 | http://www.snapshot.travel | 11111111-0000-4000-a000-222222222222 |
    Given Application version is created for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123"
      | id                                   | apiManagerId | versionName | status   | description            |
      | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 1   | inactive | Versions description 1 |
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                                                    |
      | identity/applications/                                                                                                                 |
      | identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123                                                                             |
      | identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions                                                        |


  Scenario: Application ID and name is unique when creating role - DP-1661
    Given The following applications exist
      | applicationName | description          | website                    | id                                   | partnerId                            | isInternal |
      | App Name 1      | Original application | http://www.snapshot.travel | 00011222-a05d-42d8-8e84-42e904ace123 | 11111111-0000-4000-a000-222222222222 | false      |
    When Application is created
      | applicationName | description            | website                    | id                                   | partnerId                            | isInternal |
      | App Name 2      | Different name same ID | http://www.snapshot.travel | 00011222-a05d-42d8-8e84-42e904ace123 | 11111111-0000-4000-a000-222222222222 | false      |
    Then Response code is "409"
    And Custom code is 40902
    When Application is created
      | applicationName | description            | website                    | Id                                   | isInternal | partnerId                            |
      | App Name 1      | Different ID same name | http://www.snapshot.travel | 11111111-0000-4000-a000-222222222222 | false      | 11111111-0000-4000-a000-222222222222 |
    Then Response code is "409"
    And Custom code is 40912

  Scenario: Trying to create application with non-existing partner id - DP-1732
      When Application is created
        | applicationName | description            | website                    | id                                   | partnerId                            | isInternal |
        | App Name 2      | Different name same ID | http://www.snapshot.travel | 00011222-a05d-42d8-8e84-42e904ace123 | 00011123-0000-4000-a000-222222222222 | false      |
      Then Response code is "422"
      And Custom code is 42202
