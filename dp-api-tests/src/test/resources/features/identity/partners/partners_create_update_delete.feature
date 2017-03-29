Feature: Partners create update delete

  Background:
    Given Database is cleaned and default entities are created


  Scenario: Create partner
    When The following partner is created
      | name           | email              | website                    | vatId      | notes        |
      | Company name 1 | p1@snapshot.travel | http://www.snapshot.travel | CZ10000001 | Test notes 1 |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "Company name 1"
    And Body contains entity with attribute "email" value "p1@snapshot.travel"
    And Body contains entity with attribute "website" value "http://www.snapshot.travel"
    And Body contains entity with attribute "vat_id" value "CZ10000001"
    And Body contains entity with attribute "notes" value "Test notes 1"
    And Body contains entity with attribute "is_active" value "true"
    When The following partner is created
      | name            | email              |
      | Minimal Partner | p2@snapshot.travel |
    Then Response code is "201"

  Scenario Outline: Checking error codes for creating partners
    Given The following partner exist
      | name           | email          | website                    | vatId      | notes        |
      | Company name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 |
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples:
      | json_input_file                                                         | method | module   | url                | error_code | custom_code |
      | /messages/identity/partners/create_partner_missing_company_name.json    | POST   | identity | /identity/partners | 422        | 42201       |
      | /messages/identity/partners/create_partner_missing_salesforce_id.json   | POST   | identity | /identity/partners | 422        | 42201       |
      | /messages/identity/partners/create_partner_missing_phone.json           | POST   | identity | /identity/partners | 422        | 42201       |
      | /messages/identity/partners/create_partner_missing_email.json           | POST   | identity | /identity/partners | 422        | 42201       |
      | /messages/identity/partners/create_partner_missing_website.json         | POST   | identity | /identity/partners | 422        | 42201       |
      | /messages/identity/partners/create_partner_missing_status.json          | POST   | identity | /identity/partners | 422        | 42201       |
      | /messages/identity/partners/create_partner_not_unique_company_name.json | POST   | identity | /identity/partners | 422        | 42201       |
      | /messages/identity/partners/create_partner_not_unique_email.json        | POST   | identity | /identity/partners | 422        | 42201       |
      | /messages/identity/partners/create_partner_incorrect_website_value.json | POST   | identity | /identity/partners | 422        | 42201       |
      | /messages/identity/partners/create_partner_incorrect_email_value.json   | POST   | identity | /identity/partners | 422        | 42201       |
      | /messages/identity/partners/create_partner_incorrect_phone_value.json   | POST   | identity | /identity/partners | 422        | 42201       |

  Scenario: Deleting partner
    Given The following partner exist
      | name           | email          | website                    | vatId      | notes        |
      | Company name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 |
    When Partner with name "Company name 1" is deleted
    Then Response code is "204"
    And Body is empty
    And Partner with same id does not exist

  Scenario: Checking error code for deleting partner
    When Nonexistent partner id is deleted
    Then Response code is "404"


  Scenario: Activate partner
    Given The following partner exist
      | name           | email          | website                    | vatId      | notes        | Id                                   |
      | Company name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    When Partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is activated
    Then Response code is "204"
    And Body is empty
    And Partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is active

  Scenario: Inactivate partner
    Given The following partner exist
      | name           | email          | website                    | vatId      | notes        | Id                                   |
      | Company name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    When Partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is inactivated
    Then Response code is "204"
    And Body is empty
    And Partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is not active

  Scenario Outline: Updating partner
    Given The following partner exist
      | name           | email          | website                    | vatId      | notes        | Id                                   |
      | Company name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    When Partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is updated with data
      | name   | email           | website           | vatId           | notes           |
      | <name> | <updated_email> | <updated_website> | <updated_VatId> | <updated_notes> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" has data
      | name   | email           | website           | vatId           | notes           |
      | <name> | <updated_email> | <updated_website> | <updated_VatId> | <updated_notes> |

    Examples:
      | name           | updated_email   | updated_website   | updated_VatId | updated_notes            |
      | Partner test   | test123@abc.com | /null             | /null         | Updated test description |
      | Partner test 1 | /null           | http://google.com | CZ12345678    | Last notes update        |

  Scenario: Create duplicate partner throws correct error - DP-1661
    Given The following partner exist
      | Id                                   | name           | email          | website                    | vatId      | notes        |
      | abc00011-a05d-42d8-8e84-42e904ace123 | Company name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 |
    When The following partner is created
      | Id                                   | name           | email          | website                    | vatId      | notes        |
      | abc00011-a05d-42d8-8e84-42e904ace123 | Company name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 |
    Then Response code is "409"
    And Custom code is 40902



