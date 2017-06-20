Feature: Review application access checks
  - Application should have access to review endpoints only if it's is_internal attribute is set to true


  Background:
    Given Database is cleaned and default entities are created
    Given The following applications exist
      | id                                   | isInternal | name            | description          | website                    | partnerId                            |
      | 00000000-a05d-42d8-8e84-111111111111 | false      | External App    | External Application | http://www.snapshot.travel | 11111111-0000-4000-a000-222222222222 |
      | 00000000-a05d-42d8-8e84-111111100000 | true       | Internal App    | Internal Application | http://www.snapshot.travel | 11111111-0000-4000-a000-222222222222 |
    Given The following commercial subscriptions exist
      | customerId                           | propertyId                           | applicationId                        |
      | 11111111-0000-4000-a000-555555555555 | 11111111-0000-4000-a000-666666666666 | 00000000-a05d-42d8-8e84-111111111111 |
      | 11111111-0000-4000-a000-555555555555 | 11111111-0000-4000-a000-666666666666 | 00000000-a05d-42d8-8e84-111111100000 |
    Given The following application versions exists
      | id                                   | apiManagerId | name                 | status   | description            | applicationId                        |
      | 00000000-a05d-42d8-8e84-222222222222 | 123          | External App Version | inactive | Versions description 1 | 00000000-a05d-42d8-8e84-111111111111 |
      | 00000000-a05d-42d8-8e84-333333333333 | 321          | Internal App Version | inactive | Versions description 2 | 00000000-a05d-42d8-8e84-111111100000 |


  Scenario Outline: External application requests general review endpoints
    When GET request is sent to "<url>" on module "<module>" for application version "External App Version"
    Then Response code is "403"
    And Custom code is 40301
    When GET request is sent to "<url>" on module "<module>" for application version "External App Version" with since "<since>", until "<until>", granularity "<granularity>" and property "99000099-9999-4999-a999-999999999999"
    Then Response code is "403"
    And Custom code is 40301
    When GET request is sent to "<url>" on module "<module>" for application version "Internal App Version" with since "<since>", until "<until>", granularity "<granularity>" and property "99000099-9999-4999-a999-999999999999"
    Then Response code is "200"
    Examples:
      | url                                                | module | since      | until      | granularity |
      | /review/analytics/aspects_of_business              | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/number_of_reviews                | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/overall_bubble_rating            | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/popularity_index_rank            | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/rating_score                     | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/travellers                       | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/travellers/aspects_of_business   | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/travellers/number_of_reviews     | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/travellers/overall_bubble_rating | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/location                                   | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/locations                                  | review | 2016-12-07 | 2016-12-07 | day         |


  Scenario Outline: External application requests customers, properties and property sets review endpoints
    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "true"
      | id                                   | type     | username     | firstName | lastName | email                | timezone      | languageCode |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snapshotUser | Snapshot  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 99000199-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | 99000299-9999-4999-a999-999999999999 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | 99000399-9999-4999-a999-999999999999 | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "snapshotUser"
      | id                                   | name            | description            | type            |
      | 88000399-9999-4999-a999-000000000000 | ps1_name        | ps1_description        | brand           |
    Given Relation between property with code "p1_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p2_code" and property set with name "ps1_name" exists
    Given Relation between property with code "p3_code" and property set with name "ps1_name" exists
    Given Relation between user "snapshotUser" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with isPrimary "true"
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p2_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p3_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2016-12-31"
    Given Relation between user "snapshotUser" and property with code "p1_code" exists
    Given Relation between user "snapshotUser" and property with code "p2_code" exists
    Given Relation between user "snapshotUser" and property with code "p3_code" exists
    When GET request is sent to "<url>" on module "<module>" for application version "External App Version"
    Then Response code is "403"
    And Custom code is 40301
    When GET request is sent to "<url>" on module "<module>" for application version "External App Version" with since "<since>", until "<until>", granularity "<granularity>" and property "99000099-9999-4999-a999-999999999999"
    Then Response code is "403"
    And Custom code is 40301
    When GET request is sent to "<url>" on module "<module>" for application version "Internal App Version" with since "<since>", until "<until>", granularity "<granularity>" and property "99000099-9999-4999-a999-999999999999"
    Then Response code is "200"
    Examples:
      | url                                                                                  | module       | since      | until      | granularity |
      | /review/analytics/customer/1238fd9a-a05d-42d8-8e84-42e904ace123/aspects_of_business       | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/customer/1238fd9a-a05d-42d8-8e84-42e904ace123/number_of_reviews         | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/customer/1238fd9a-a05d-42d8-8e84-42e904ace123/overall_bubble_rating     | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/customer/1238fd9a-a05d-42d8-8e84-42e904ace123/popularity_index_rank     | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/property/99000199-9999-4999-a999-999999999999/aspects_of_business       | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/property/99000199-9999-4999-a999-999999999999/number_of_reviews         | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/property/99000199-9999-4999-a999-999999999999/overall_bubble_rating     | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/property/99000199-9999-4999-a999-999999999999/popularity_index_rank     | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/property_set/88000399-9999-4999-a999-000000000000/aspects_of_business   | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/property_set/88000399-9999-4999-a999-000000000000/number_of_reviews     | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/property_set/88000399-9999-4999-a999-000000000000/overall_bubble_rating | review | 2016-12-07 | 2016-12-07 | day         |
      | /review/analytics/property_set/88000399-9999-4999-a999-000000000000/popularity_index_rank | review | 2016-12-07 | 2016-12-07 | day         |