@Identity
Feature: Commercial subscription get

  Background:
    Given Database is cleaned
    Given Default partner is created
    Given Default application is created
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 2a2f76f3-3537-4d5a-971a-7a36f61095bd | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "2a2f76f3-3537-4d5a-971a-7a36f61095bd"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 742529dd-481f-430d-b6b6-686fbb687cab | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728eb | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab | 11111111-0000-4000-a000-111111111111 |


  @Smoke
  Scenario: Getting commercial subscription
    When Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "commercial_subscription_id" value "8e238f8e-2c9c-4e32-9a63-40474a9728eb"
    And Body contains entity with attribute "application_id" value "11111111-0000-4000-a000-111111111111"
    And Body contains entity with attribute "customer_id" value "1238fd9a-a05d-42d8-8e84-42e904ace123"
    And Body contains entity with attribute "property_id" value "742529dd-481f-430d-b6b6-686fbb687cab"


  Scenario: Getting commercial subscription with etag
    When Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is got with etag
    Then Response code is "304"
    And Body is empty


  Scenario: Checking error code for nonexistent commercial subscription
    When Nonexistent commercial subscription id is got
    Then Response code is "404"
    And Custom code is "40402"


  Scenario Outline: Getting list of commercial subscription
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | applicationId                        | customerId                           | propertyId                           |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728e1 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728e2 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728e3 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728e4 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728e5 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728e6 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728e7 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728e8 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728e9 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2c9c-4e32-9a63-40474a972810 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |

    When List of commercial subscriptions is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Total count is "11"
    And There are <returned> commercial subscriptions returned
    Examples:
      | limit | cursor | returned |
      | /null |        | 11       |
      | /null | /null  | 11       |
      |       |        | 11       |
      |       | /null  | 11       |
      | 10    |        | 10       |
      |       | 1      | 10       |
      |       | 20     | 0        |
      | 10    | 0      | 10       |
      | 5     | 0      | 5        |
      | 5     | 10     | 1        |


  Scenario Outline: Checking error codes for getting list of commercial subscriptions
    When List of commercial subscriptions is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"
    Examples:
      | limit       | cursor | filter | sort        | sort_desc   | response_code | custom_code |
      | /null       | -1     | /null  | /null       | /null       | 400           | 40002       |
      |             | -1     | /null  | /null       | /null       | 400           | 40002       |
      | /null       | text   | /null  | /null       | /null       | 400           | 40002       |
      |             | text   | /null  | /null       | /null       | 400           | 40002       |
      | -1          |        | /null  | /null       | /null       | 400           | 40002       |
      | -1          | /null  | /null  | /null       | /null       | 400           | 40002       |
      | 201         | /null  | /null  | /null       | /null       | 400           | 40002       |
      | 21474836470 | /null  | /null  | /null       | /null       | 400           | 40002       |
      | text        |        | /null  | /null       | /null       | 400           | 40002       |
      | text        | /null  | /null  | /null       | /null       | 400           | 40002       |
      | 10          | -1     | /null  | /null       | /null       | 400           | 40002       |
      | text        | 0      | /null  | /null       | /null       | 400           | 40002       |
      | 10          | text   | /null  | /null       | /null       | 400           | 40002       |
      | 10          | 0      | /null  | property_id | property_id | 400           | 40002       |
      | 10          | 0      | /null  | /null       | nonexistent | 400           | 40002       |
      | 10          | 0      | /null  | nonexistent | /null       | 400           | 40002       |
      | 10          | 0      | code== | /null       | /null       | 400           | 40002       |


  Scenario Outline: Filtering list of commercial subscriptions
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | applicationId                        | customerId                           | propertyId                           |
      | 8e238f8e-2a9c-4e32-9a61-40474a9728e1 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2a9c-4e32-9a62-40474a9728e2 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-2a9c-4e32-9a63-40474a9728e3 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-8c9c-4e32-9a63-40474a9728e4 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-8c9c-4e32-9a63-40474a9728e5 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-8c9c-4e32-9a63-40474a9728e6 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-3b9c-4e32-9a63-40474a9728e7 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-3b9c-4e32-9a63-40474a9728e8 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
      | 8e238f8e-3b9c-4e32-9a63-40474a9728e9 | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab |
    When List of commercial subscriptions is got with limit "/null" and cursor "/null" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> commercial subscriptions returned
    And There are commercial subscriptions with following referenceIDs returned in order: "<order>"
    Examples:
      | filter                                                                    | sort                       | sort_desc                  | returned | order                                                                                                            |
      | commercial_subscription_id=='8e238f8e*'                                   | /null                      | /null                      | 10       |                                                                                                                  |
      | commercial_subscription_id=='*9'                                          | /null                      | /null                      | 1        |                                                                                                                  |
      | commercial_subscription_id=='*2a9c*'                                      | commercial_subscription_id | /null                      | 3        | 8e238f8e-2a9c-4e32-9a61-40474a9728e1, 8e238f8e-2a9c-4e32-9a62-40474a9728e2, 8e238f8e-2a9c-4e32-9a63-40474a9728e3 |
      | commercial_subscription_id=='*2a9c*'                                      | /null                      | /null                      | 3        |                                                                                                                  |
      | commercial_subscription_id=='*2a9c*' or commercial_subscription_id=='*8'  | /null                      | /null                      | 4        |                                                                                                                  |
      | commercial_subscription_id=='*2a9c*' and commercial_subscription_id=='*1' | /null                      | /null                      | 1        |                                                                                                                  |
      | application_id=='a318fd9a*'                                               | /null                      | /null                      | 10       |                                                                                                                  |
      | is_active=='1'                                                            | /null                      | /null                      | 0        |                                                                                                                  |
      | customer_id=='123*'                                                       | /null                      | /null                      | 10       |                                                                                                                  |
      | property_id=='*cab'                                                       | /null                      | /null                      | 10       |                                                                                                                  |
      | property_id=='*cab' and commercial_subscription_id=='*1'                  | /null                      | /null                      | 1        |                                                                                                                  |
      | /null                                                                     |                            | commercial_subscription_id | 10       |                                                                                                                  |
      | /null                                                                     |                            | is_active                  | 10       |                                                                                                                  |
      | /null                                                                     |                            | customer_id                | 10       |                                                                                                                  |
      | /null                                                                     |                            | application_id             | 10       |                                                                                                                  |
      | /null                                                                     |                            | property_id                | 10       |                                                                                                                  |
      | /null                                                                     | commercial_subscription_id |                            | 10       |                                                                                                                  |
      | /null                                                                     | is_active                  |                            | 10       |                                                                                                                  |
      | /null                                                                     | customer_id                |                            | 10       |                                                                                                                  |
      | /null                                                                     | application_id             |                            | 10       |                                                                                                                  |
      | /null                                                                     | property_id                |                            | 10       |                                                                                                                  |
      | commercial_subscription_id=='*2a9c*'                                      | is_active                  | /null                      | 3        | 8e238f8e-2a9c-4e32-9a61-40474a9728e1, 8e238f8e-2a9c-4e32-9a62-40474a9728e2, 8e238f8e-2a9c-4e32-9a63-40474a9728e3 |
      | commercial_subscription_id=='*2a9c*'                                      | customer_id                | /null                      | 3        | 8e238f8e-2a9c-4e32-9a61-40474a9728e1, 8e238f8e-2a9c-4e32-9a62-40474a9728e2, 8e238f8e-2a9c-4e32-9a63-40474a9728e3 |
      | commercial_subscription_id=='*2a9c*'                                      | application_id             | /null                      | 3        | 8e238f8e-2a9c-4e32-9a61-40474a9728e1, 8e238f8e-2a9c-4e32-9a62-40474a9728e2, 8e238f8e-2a9c-4e32-9a63-40474a9728e3 |
      | commercial_subscription_id=='*2a9c*'                                      | property_id                | /null                      | 3        | 8e238f8e-2a9c-4e32-9a61-40474a9728e1, 8e238f8e-2a9c-4e32-9a62-40474a9728e2, 8e238f8e-2a9c-4e32-9a63-40474a9728e3 |
      | commercial_subscription_id=='*8c9c*'                                      | /null                      | commercial_subscription_id | 3        | 8e238f8e-8c9c-4e32-9a63-40474a9728e6, 8e238f8e-8c9c-4e32-9a63-40474a9728e5, 8e238f8e-8c9c-4e32-9a63-40474a9728e4 |
      | commercial_subscription_id=='*8c9c*'                                      | /null                      | is_active                  | 3        | 8e238f8e-8c9c-4e32-9a63-40474a9728e4, 8e238f8e-8c9c-4e32-9a63-40474a9728e5, 8e238f8e-8c9c-4e32-9a63-40474a9728e6 |
      | commercial_subscription_id=='*8c9c*'                                      | /null                      | customer_id                | 3        | 8e238f8e-8c9c-4e32-9a63-40474a9728e4, 8e238f8e-8c9c-4e32-9a63-40474a9728e5, 8e238f8e-8c9c-4e32-9a63-40474a9728e6 |
      | commercial_subscription_id=='*8c9c*'                                      | /null                      | application_id             | 3        | 8e238f8e-8c9c-4e32-9a63-40474a9728e4, 8e238f8e-8c9c-4e32-9a63-40474a9728e5, 8e238f8e-8c9c-4e32-9a63-40474a9728e6 |
      | commercial_subscription_id=='*8c9c*'                                      | /null                      | property_id                | 3        | 8e238f8e-8c9c-4e32-9a63-40474a9728e4, 8e238f8e-8c9c-4e32-9a63-40474a9728e5, 8e238f8e-8c9c-4e32-9a63-40474a9728e6 |
