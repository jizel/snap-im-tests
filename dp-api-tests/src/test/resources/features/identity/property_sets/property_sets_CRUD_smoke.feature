@Identity
@Smoke
Feature: Property sets create update delete

  Background:
    Given Database is cleaned and default entities are created


  Scenario Outline: Create, delete property set
    When The following property set is created for customer with id "11111111-0000-4000-a000-555555555555"
      | name    | description    | type    |
      | <name>  | <description>  | <type>  |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "<name>"
    And Body contains entity with attribute "property_set_type" value "<type>"
    And Body contains entity with attribute "is_active" value "true"
    And Etag header is present
    # --------- DELETE --------------
    When Property set "<name>" is deleted
    Then Response code is "204"
    And Body is empty
    And Property set with same id doesn't exist
    Examples:
    | name            | description            | type            |
    | ps1_name        | ps1_description        | brand           |
    | ps2_name        | ps2_description        | geolocation     |
    | ps3_name        | ps3_description        | hotel_type      |

  Scenario: Add remove property to property set
    # --------------- ADD ----------------------
    Given The following property sets exist for customer "11111111-0000-4000-a000-555555555555" and user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | id                                   | name      | description     | type  |
      | ad0fe5c3-e46f-474a-b1d1-73e9393dbdc4 | ps1_name  | ps1_description | brand |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId    | name         | code         | website                    | email          | isDemo         | timezone      | anchorCustomerId                     |
      | 200749ac-a36e-416f-9f13-4d94a3db1267 | SALESFORCE12345 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 11111111-0000-4000-a000-555555555555 |
    When Property "200749ac-a36e-416f-9f13-4d94a3db1267" is added to property set "ad0fe5c3-e46f-474a-b1d1-73e9393dbdc4"
    Then Response code is "201"
    # ---------------- CASCADE DELETE ----------------------------
    When Property set "ad0fe5c3-e46f-474a-b1d1-73e9393dbdc4" is deleted
    Then Response code is "409"
    # ---------------- REMOVE RELATION --------------------------
    When Property "200749ac-a36e-416f-9f13-4d94a3db1267" is removed from property set "ad0fe5c3-e46f-474a-b1d1-73e9393dbdc4"
    Then Response code is "204"
    And Body is empty
    And Property "200749ac-a36e-416f-9f13-4d94a3db1267" isn't there for property set "ad0fe5c3-e46f-474a-b1d1-73e9393dbdc4" for customer "11111111-0000-4000-a000-555555555555"

  Scenario: Add remove user to property set
    # ----------------- ADD --------------------
    Given The following users exist for customer "11111111-0000-4000-a000-555555555555" as primary "false"
      | id                                   | type     | username | firstName | lastName | email                | timezone      | languageCode |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    When The following property sets exist for customer "11111111-0000-4000-a000-555555555555"
      | id                                   | name      | description     | type  |
      | ad0fe5c3-e46f-474a-b1d1-73e9393dbdc4 | ps1_name  | ps1_description | brand |
    When User "5d829079-48f0-4f00-9bec-e2329a8bdaac" is added to property set "ad0fe5c3-e46f-474a-b1d1-73e9393dbdc4"
    Then Response code is "201"
    # ----------------- REMOVE --------------------
    When User "5d829079-48f0-4f00-9bec-e2329a8bdaac" is removed from property set "ad0fe5c3-e46f-474a-b1d1-73e9393dbdc4"
    Then Response code is "204"
    And Body is empty
    And User "5d829079-48f0-4f00-9bec-e2329a8bdaac" isn't there for property set "ad0fe5c3-e46f-474a-b1d1-73e9393dbdc4"
