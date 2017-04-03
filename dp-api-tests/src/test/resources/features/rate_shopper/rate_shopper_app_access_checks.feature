Feature: Web Performance - External applications access checks
  - Application should have acccess to Web Performance endpoints only if it's is_internal attribute is set to true
  - Positive cases are tested in separate features. This feature should test that external applications don't have access

  Background:
    Given Database is cleaned and default entities are created
    Given The following applications exist
      | id                                   | isInternal | applicationName | description          | website                    | partnerId                            |
      | 00000000-a05d-42d8-8e84-111111111111 | false      | External App    | External Application | http://www.snapshot.travel | 11111111-0000-4000-a000-222222222222 |
      | 00000000-a05d-42d8-8e84-111111100000 | true       | Internal App    | Internal Application | http://www.snapshot.travel | 11111111-0000-4000-a000-222222222222 |
    Given The following commercial subscriptions exist
      | customerId                           | propertyId                           | applicationId                        |
      | 11111111-0000-4000-a000-555555555555 | 11111111-0000-4000-a000-666666666666 | 00000000-a05d-42d8-8e84-111111111111 |
      | 11111111-0000-4000-a000-555555555555 | 11111111-0000-4000-a000-666666666666 | 00000000-a05d-42d8-8e84-111111100000 |
    Given The following application versions exists
      | id                                   | apiManagerId | versionName          | status   | description            | applicationId                        |
      | 00000000-a05d-42d8-8e84-222222222222 | 123          | External App Version | inactive | Versions description 1 | 00000000-a05d-42d8-8e84-111111111111 |
      | 00000000-a05d-42d8-8e84-333333333333 | 321          | Internal App Version | inactive | Versions description 2 | 00000000-a05d-42d8-8e84-111111100000 |
    Given The following properties exist with random address and billing address
      | id                                   | salesforceId     | name         | propertyCode | website                    | email            | isDemoProperty | timezone      | ttiId | anchorCustomerId                     |
      | 99000099-9999-4999-a999-999999999999 | salesforceid_n1  | pn1_name     | pn1_code     | http://www.snapshot.travel | pn1@tenants.biz  | true           | Europe/Prague | 0     | 11111111-0000-4000-a000-555555555555 |


  Scenario Outline: External application requests social media endpoints with valid parameters
    When GET request is sent to "<url>" on module "<module>" for application version "External App Version"
    Then Response code is "404"
    When GET request is sent to "<url>" on module "<module>" for application version "External App Version" with since "<since>", until "<until>", granularity "<granularity>" and property "99000099-9999-4999-a999-999999999999" in path
    Then Response code is "404"
    When GET request is sent to "<url>" on module "<module>" for application version "Internal App Version" with since "<since>", until "<until>", granularity "<granularity>" and property "99000099-9999-4999-a999-999999999999" in path
    Then Response code is "200"
    Examples:
      | url                                                                   | module       | since      | until      | granularity |
      | /rate_shopper/analytics/market                                        | rate_shopper | 2016-12-07 | 2016-12-07 | day         |
#      DP-1955
#      | /rate_shopper/analytics/market/properties                            | rate_shopper | 2016-12-07 | 2016-12-07 | day         |
      | /rate_shopper/analytics/property/99000099-9999-4999-a999-999999999999 | rate_shopper | 2016-12-07 | 2016-12-07 | day         |