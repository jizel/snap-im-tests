Feature: Authorization and authentication

  @skipped
  Scenario Outline:No access token is used for service

    When "<service>" is called without token using "<method>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | service            | method | error_code | custom_code |
      | identity/users     | GET    | 200        | 4           |
      | identity/customers | GET    | 200        | 4           |
      | identity/roles     | GET    | 200        | 4           |

