Feature: Partners
  #TODO update for partners

  Background:
    Given Basepath is "/identity/partners"
    Given The following partners from files exist
      | filename                           |
      | identity/partners/partner_001.json |
      | identity/partners/partner_002.json |
      | identity/partners/partner_003.json |


  @skipped
  Scenario: Creating partner

    When User is created
      | tenantId | companyName | email          | code |
      | 11       | Company 1   | c1@tenants.biz | c1t  |
    Then User with "id" exists
    And Content type is "application/json"
    And Response code is "201"

  @skipped
  Scenario Outline: Updating partner
    When Partner with "<id>" is updated with "<updated_fields>" by "<new_values>"
    Then Partner with "<id>" exists with "<updated_fields>" by "<new_values>"
    And Response code is "204"
    And Content type is "application/json"

    Examples:
      | id    | updated_fields                | new_values          |
      | 10001 | ["role_id", "application_id"] | ["10005", "100005"] |

  @skipped
  Scenario Outline: Checking error codes for creating partner
    When File <json_input_file> is used for "<method>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                           | method | error_code | custom_code |
      | identity/partners/partner_missing_vatid.json | POST   | 405        | 51          |