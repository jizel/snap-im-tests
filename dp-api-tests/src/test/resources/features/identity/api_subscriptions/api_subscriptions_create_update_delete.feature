@Identity
Feature: Api subscription create update delete

  Background:
    Given Database is cleaned
    Given The following applications exist
      | applicationId                        | applicationName            | description               | website                    |
      | 6f552105-0bae-4410-b4bb-bee31567d4fa | Application test company 1 | Application description 1 | http://www.snapshot.travel |
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | description            |
      | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 1   | inactive | Versions description 1 |
      | c595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 2   | inactive | Versions description 2 |
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 742529dd-481f-430d-b6b6-686fbb687cab | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728eb | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab | 6f552105-0bae-4410-b4bb-bee31567d4fa |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | commercialSubscriptionId             |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 8e238f8e-2c9c-4e32-9a63-40474a9728eb |


  @Smoke
  Scenario: Create api subscription with valid data
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | description            |
      | e318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 4   | inactive | Versions description 1 |
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 2238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "2238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 6d829079-48f0-4f00-9bec-e2329a8bdaac | customer | default2 | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "6d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 842529dd-481f-430d-b6b6-686fbb687cab | salesforceid_1 | p2_name      | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 2238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728eb | 2238fd9a-a05d-42d8-8e84-42e904ace123 | 842529dd-481f-430d-b6b6-686fbb687cab | 6f552105-0bae-4410-b4bb-bee31567d4fa |
    Given The following api subscriptions is created
      | apiSubscriptionId                    | applicationVersionId                 | commercialSubscriptionId             |
      | 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | e318fd9a-a05d-42d8-8e84-22e904ace111 | 8e238f8e-2c9c-4e32-9a63-40474a9728eb |
    Then Response code is 201
    And Etag header is present
    And Body contains entity with attribute "api_subscription_id" value "6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f"
    And Body contains entity with attribute "application_version_id " value "e318fd9a-a05d-42d8-8e84-22e904ace111"
    And Body contains entity with attribute "commercial_subscription_id" value "8e238f8e-2c9c-4e32-9a63-40474a9728eb"


  Scenario: Trying to create application subscription with the same versionID
    Given Api subscription with id "5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f" is activated
    When Trying to create second api subscription with the same versionID
    Then Response code is 400
    And Custom code is 62


  @Smoke
  Scenario: Activate api subscription
    When Api subscription with id "5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f" is activated
    Then Response code is 204
    And Body is empty
    And Api subscription with id "5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f" is active


  Scenario: Deactivate api subscription
    When Api subscription with id "5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f" is activated
    When Api subscription with id "5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f" is deactivated
    Then Response code is 204
    And Body is empty
    And Api subscription with id "5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f" is not active


  @Smoke
  Scenario: Delete api subscription
    When Api subscription with id "5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f" is deleted
    Then Response code is 204
    And  Body is empty
    And Api subscription with id "287b49db-673c-44e5-ab40-345ce5e89c37" is not among all api subscriptions


  Scenario: Delete non-existing api subscription
    When Api subscription with id "non-existing-api-subscription" is deleted
    Then Response code is 204
    And  Body is empty


  Scenario Outline: Create api subscription with invalid data
    Given The following api subscriptions is created
      | apiSubscriptionId   | applicationVersionId   | commercialSubscriptionId   |
      | <apiSubscriptionId> | <applicationVersionId> | <commercialSubscriptionId> |
    Then Response code is <responseCode>
    And Custom code is <customCode>
    Examples:
      | apiSubscriptionId                    | applicationVersionId                 | commercialSubscriptionId             | responseCode | customCode | error_note                                                                                           |
      | something                            | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | 400          | 63         | # The value is invalid. Param 'api_subscription_id' is not universally unique identifier (UUID)      |
      | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | something                            | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | 400          | 63         | # The value is invalid. Param 'application_version_id' is not universally unique identifier (UUID)   |
      | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | something                            | 400          | 63         | # The value is invalid. Param 'commercialSubscriptionId' is not universally unique identifier (UUID) |
      | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 |                                      | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | 400          | 63         | # The body parameter 'application_version_id' cannot be empty.                                       |
      | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 |                                      | 400          | 63         | # The body parameter 'commercialSubscriptionId' cannot be empty.                                     |
      | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | 1d491c7d-4c70-4be7-ab47-73f36701bcf4 | 422          | 42202      | # Version with identifier 1d491c7d-4c70-4be7-ab47-73f36701bcf4 was not found.                        |

  Scenario Outline: Send POST request with empty body to all api subcriptions endpoints
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                            |
      | identity/api_subscriptions/                                     |
      | identity/api_subscriptions/5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
