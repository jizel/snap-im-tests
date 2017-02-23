@Identity
Feature: Roles create update delete user property set

  Background:
    Given Database is cleaned and default entities are created
    Given Switch for user property set role tests



    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        | partnerId                           | isInternal |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |11111111-0000-4000-a000-222222222222 | false      |
      | Application test company 2 | Application description 2 | http://www.snapshot.travel | b318fd9a-a05d-42d8-8e84-42e904ace123 |11111111-0000-4000-a000-222222222222 | false      |
    Given The following roles exist
      | applicationId                        | roleName    | description            |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 1 | optional description 1 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 2 | optional description 2 |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 3 | optional description 3 |


  @Smoke
  Scenario: Creating role
    When Role is created
      | applicationId                        | roleName            | description            |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Created role name 1 | optional description 1 |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "Created role name 1"
    And Body contains entity with attribute "description" value "optional description 1"
    And "Location" header is set and contains the same role
    And Etag header is present


  Scenario Outline: Checking error codes for creating role
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"
    Examples:
      | json_input_file                                                    | method | module   | url                           | error_code | custom_code |
      | /messages/identity/roles/create_role_missing_application_id.json   | POST   | identity | /identity/user_customer_roles | 422        | 42201       |
      | /messages/identity/roles/create_role_missing_role_name.json        | POST   | identity | /identity/user_customer_roles | 422        | 42201       |
      | /messages/identity/roles/create_role_not_existing_application.json | POST   | identity | /identity/user_customer_roles | 422        | 42201       |
      | /messages/identity/roles/create_role_not_recognized_field.json     | POST   | identity | /identity/user_customer_roles | 422        | 42201       |
      | /messages/identity/roles/create_role_not_unique_role_name.json     | POST   | identity | /identity/user_customer_roles | 422        | 42201       |
      | /messages/identity/roles/create_role_not_valid_json.json           | POST   | identity | /identity/user_customer_roles | 400        | 40001       |


  @Smoke
  Scenario: Deleting role
    When Role with name "Role name 1" for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" is deleted
    Then Response code is "204"
    And Body is empty
    And Role with same id doesn't exist for application id "a318fd9a-a05d-42d8-8e84-42e904ace123"


  Scenario: Checking error code for deleting role
    When Nonexistent role id is deleted
    Then Response code is "404"

#  DP-1793
  Scenario Outline: Updating role
    When Role with name "<roleName>" for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" is updated with data
      | applicationId   | roleName           | description       |
      | <applicationId> | <updated_roleName> | <description> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated role with name "<updated_roleName>" has data
      | applicationId   | roleName           | description   |
      | <applicationId> | <updated_roleName> | <description> |
    Examples:
      | applicationId                        | roleName    | updated_roleName  | description                    |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 1 | Updated role name |                                |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 1 | Updated role name | Updated optional description   |
      | b318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 1 | Role name 1       | Updated optional description 1 |


  Scenario: Updating role with outdated etag
    When Role with name "Role name 1" is updated with data if updated before
      | roleName          | description |
      | Updated role name |             |
    Then Response code is "412"
    And Custom code is "41202"


  Scenario Outline: Updating with invalid data
    When Role with name "<roleName>" for application id "<applicationId>" is updated with data
      | applicationId           | roleName           | description           |
      | <updated_applicationId> | <updated_roleName> | <updated_description> |
    Then Response code is "<responseCode>"
    And Custom code is "<customCode>"
    Examples:
      | applicationId                        | updated_applicationId                | roleName    | updated_roleName | updated_description | responseCode | customCode |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | a000111a-a05d-42d8-8e84-42e904ace123 | Role name 3 | Role name 5      |                     | 422          | 42202      |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | nonUUID                              | Role name 3 | Role name 5      |                     | 422          | 42201      |
      | a318fd9a-a05d-42d8-8e84-42e904ace123 | a318fd9a-a05d-42d8-8e84-42e904ace123 | Role name 3 |                  | empty name          | 422          | 42201      |

  Scenario Outline: Send POST request with empty body to all user customer roles endpoints
    Given The following roles exist
      | roleId                               | applicationId                        | roleName    | description            |
      | 7e0982a4-cab3-47fb-b3ff-3951fa10967c | a318fd9a-a05d-42d8-8e84-42e904ace123 | testPOST1   | optional description 1 |
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                               |
      | identity/user_property_set_roles                                      |
      | identity/user_property_set_roles/7e0982a4-cab3-47fb-b3ff-3951fa10967c |

  Scenario: Role ID and name is unique when creating role - DP-1661
    When Role is created
      | roleId                               | roleName         | description            | applicationId                        |
      | 33344455-3dc2-477e-aa02-6e09465d22ae | user_cust_role   | optional description 2 | 11111111-0000-4000-a000-111111111111 |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "user_cust_role"
    When Role is created
      | roleId                               | roleName         | description            | applicationId                        |
      | 33344455-3dc2-477e-aa02-6e09465d22ae | user_cust_role2  | Same ID different name | 11111111-0000-4000-a000-111111111111 |
    Then Response code is "409"
    And Custom code is 40902
    When Role is created
      | roleName         | description            | applicationId                        |
      | user_cust_role   | Same name different ID | 11111111-0000-4000-a000-111111111111 |
    Then Response code is "409"
    And Custom code is 40907

  Scenario: Role ID is unique for all applications, role name just for one
    Given Role is created
      | roleId                               | roleName         | description            | applicationId                        |
      | 33344455-3dc2-477e-aa02-6e09465d22ae | user_cust_role   | optional description 2 | 11111111-0000-4000-a000-111111111111 |
    Given The following applications exist
      | applicationName  | description               | website                    | applicationId                        | partnerId                           | isInternal |
      | OtherApplication | Application description 1 | http://www.snapshot.travel | 1118fd9a-a05d-42d8-8e84-42e904ace123 |11111111-0000-4000-a000-222222222222 | false      |
    When Role is created
      | roleId                               | roleName         | description            | applicationId                        |
      | 33344455-3dc2-477e-aa02-6e09465d22ae | user_cust_role   | Same ID different app  | 1118fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "409"
    When Role is created
      | roleId                               | roleName         | description             | applicationId                        |
      | 00044455-3dc2-477e-aa02-6e09465d22ae | user_cust_role   | Same name different app | 1118fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "201"