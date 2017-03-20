@Identity
Feature: Customers properties get

  Background:
    Given Database is cleaned and default entities are created
    Given The following application versions exists
      | Id                                   | apiManagerId | versionName | status   | description            | applicationId                        |
      | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 1   | inactive | Versions description 1 | 11111111-0000-4000-a000-111111111111 |
      | c595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 2   | inactive | Versions description 2 | 11111111-0000-4000-a000-111111111111 |
    Given The following customers exist with random address
      | Id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | Id                                   | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | Id                                   | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 742529dd-481f-430d-b6b6-686fbb687cab | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following commercial subscriptions exist
      | Id                                   | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728eb | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab | 11111111-0000-4000-a000-111111111111 |
    Given The following api subscriptions exist
      | Id                                   | applicationVersionId                 | commercialSubscriptionId             |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 8e238f8e-2c9c-4e32-9a63-40474a9728eb |
      | 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | c595fc9d-f5ca-45e7-a15d-c8a97108d884 | 8e238f8e-2c9c-4e32-9a63-40474a9728eb |

#  DP-1775
  Scenario Outline: Getting customer's api subscriptions - positive scenarios
    When List of api subscriptions is got for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And Total count is "<total_count>"
    And There are "<count>" api subscriptions returned
    And There are api subscriptions with following IDs returned in order: "<order>"
    Examples:
      | limit | cursor | filter                                                             | sort                       | sort_desc                  | total_count | count | order                                                                      |
      | /null | /null  | /null                                                              | /null                      | /null                      | 2           | 2     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | 1     | /null  | /null                                                              | /null                      | /null                      | 2           | 1     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f                                       |
      | /null | 0      | /null                                                              | /null                      | /null                      | 2           | 2     |                                                                            |
      | /null | 1      | /null                                                              | /null                      | /null                      | 2           | 1     |                                                                            |
      | /null | 20     | /null                                                              | /null                      | /null                      | 2           | 0     |                                                                            |
      | /null | /null  | is_active=='true'                                                  | /null                      | /null                      | 0           | 0     |                                                                            |
      | /null | /null  | api_subscription_id=='5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f'        | /null                      | /null                      | 1           | 1     |                                                                            |
      | /null | /null  | api_subscription_id=='*810c*'                                      | /null                      | /null                      | 2           | 2     |                                                                            |
      | /null | /null  | application_version_id=='b595fc9d-f5ca-45e7-a15d-c8a97108d884'     | /null                      | /null                      | 1           | 1     |                                                                            |
      | /null | /null  | application_version_id=='*45e7*'                                   | /null                      | /null                      | 2           | 2     |                                                                            |
      | /null | /null  | commercial_subscription_id=='8e238f8e-2c9c-4e32-9a63-40474a9728eb' | /null                      | /null                      | 2           | 2     |                                                                            |
      | /null | /null  | commercial_subscription_id=='*4e32*'                               | /null                      | /null                      | 2           | 2     |                                                                            |
      | /null | /null  | commercial_subscription_id=='*4e32*'                               | is_active                  | /null                      | 2           | 2     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null | /null  | commercial_subscription_id=='*4e32*'                               | commercial_subscription_id | /null                      | 2           | 2     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null | /null  | commercial_subscription_id=='*4e32*'                               | api_subscription_id        | /null                      | 2           | 2     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null | /null  | commercial_subscription_id=='*4e32*'                               | application_version_id     | /null                      | 2           | 2     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null | /null  | commercial_subscription_id=='*4e32*'                               | /null                      | is_active                  | 2           | 2     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null | /null  | commercial_subscription_id=='*4e32*'                               | /null                      | commercial_subscription_id | 2           | 2     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null | /null  | commercial_subscription_id=='*4e32*'                               | /null                      | api_subscription_id        | 2           | 2     | 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null | /null  | commercial_subscription_id=='*4e32*'                               | /null                      | application_version_id     | 2           | 2     | 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null | /null  | commercial_subscription_id=='*4e32*' and is_active=='false'        | /null                      | /null                      | 2           | 2     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null | /null  | commercial_subscription_id=='*4e32*' and is_active=='true'         | /null                      | /null                      | 0           | 0     |                                                                            |
      | /null | /null  | commercial_subscription_id=='*4e32*' or is_active=='false'         | /null                      | /null                      | 2           | 2     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null | /null  | commercial_subscription_id=='*4e32*' or is_active=='true'          | /null                      | /null                      | 2           | 2     | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 6c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |


  Scenario Outline: Getting customer's api subscriptions - negative scenarios
    When List of api subscriptions is got for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"
    Examples:
      | limit | cursor | filter                  | sort      | sort_desc | error_code | custom_code |
      | 0     | /null  | /null                   | /null     | /null     | 400        | 40002       |
      | -1    | /null  | /null                   | /null     | /null     | 400        | 40002       |
      | karel | /null  | /null                   | /null     | /null     | 400        | 40002       |
      | /null | -1     | /null                   | /null     | /null     | 400        | 40002       |
      | /null | karel  | /null                   | /null     | /null     | 400        | 40002       |
      | /null | /null  | /null                   | is_active | is_active | 400        | 40002       |
      | /null | /null  | /null                   | @{}\€$    | /null     | 400        | 40002       |
      | /null | /null  | /null                   | /null     | @{}\€$    | 400        | 40002       |
      | /null | /null  | customer_id=='*'        | /null     | /null     | 400        | 40002       |
      | /null | /null  | anotherNonExistent=='*' | /null     | /null     | 400        | 40002       |
