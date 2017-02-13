@Identity
Feature: Commercial subscription create update delete

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 2a2f76f3-3537-4d5a-971a-7a36f61095bd | partner | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Default Snapshot user is created
    Given Default partner is created
    Given Default application is created

    Given The following properties exist with random address and billing address for user "2a2f76f3-3537-4d5a-971a-7a36f61095bd"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 742529dd-481f-430d-b6b6-686fbb687cab | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728eb | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab | 11111111-0000-4000-a000-111111111111 |


  # --------------------- CREATE ---------------------
  @Smoke
  Scenario: Create commercial subscription
    Given Commercial subscription is created
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728ec | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab | 11111111-0000-4000-a000-111111111111 |
    Then Response code is "201"
    And Body contains entity with attribute "commercial_subscription_id" value "8e238f8e-2c9c-4e32-9a63-40474a9728ec"
    And Body contains entity with attribute "customer_id" value "1238fd9a-a05d-42d8-8e84-42e904ace123"
    And Body contains entity with attribute "property_id" value "742529dd-481f-430d-b6b6-686fbb687cab"
    And Body contains entity with attribute "application_id" value "11111111-0000-4000-a000-111111111111"
    And Body contains entity with attribute "is_active" value "false"


  Scenario Outline: Checking error codes for creating commercial subscription
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"
    Examples:
      | json_input_file                                                                                 | method | module   | url                                | error_code | custom_code |
      | /messages/identity/commercial_subscriptions/create_commSubscription_missing_application_id.json | POST   | identity | /identity/commercial_subscriptions | 422        | 42201       |
      | /messages/identity/commercial_subscriptions/create_commSubscription_missing_customer_id.json    | POST   | identity | /identity/commercial_subscriptions | 422        | 42201       |
      | /messages/identity/commercial_subscriptions/create_commSubscription_missing_property_id.json    | POST   | identity | /identity/commercial_subscriptions | 422        | 42201       |


  # --------------------- DELETE ---------------------
  @Smoke
  Scenario: Deleting commercial subscriptions
    When Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is deleted
    Then Response code is "204"
    And Body is empty
    And Commercial subscription with same id does not exist


  Scenario: Checking error code for deleting commercial subscription
    When Nonexistent commercial subscription id is deleted
    Then Response code is "412"
    And Custom code is "41202"


  # --------------------- UPDATE ---------------------
  @Smoke
  Scenario: Update commercial subscription - activate
    When Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is activated
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is activate


  Scenario: Update commercial subscription - inactivate
    Given Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is activated
    When Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is deactivated
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is not activate


  Scenario Outline: Send POST request with empty body to all commercial subcriptions endpoints
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                    |
      | identity/commercial_subscriptions/                                     |
      | identity/commercial_subscriptions/8e238f8e-2c9c-4e32-9a63-40474a9728eb |
