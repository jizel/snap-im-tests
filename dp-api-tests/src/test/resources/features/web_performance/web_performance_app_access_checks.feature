Feature: Web Performance - External applications access checks
  - Application should have access to Web Performance endpoints only if it's is_internal attribute is set to true
  - Positive cases are tested in separate features. This feature should test that external applications don't have access

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


  Scenario Outline: External application requests social media endpoints with valid parameters
    When GET request is sent to "<url>" on module "<module>" for application version "External App Version"
    Then Response code is "403"
    And Custom code is 40301
    When GET request is sent to "<url>" on module "<module>" for application version "External App Version" with since "<since>", until "<until>", granularity "<granularity>" and property "99000099-9999-4999-a999-999999999999"
    Then Response code is "403"
    And Custom code is 40301
    When GET request is sent to "<url>" on module "<module>" for application version "Internal App Version" with since "<since>", until "<until>", granularity "<granularity>" and property "99000099-9999-4999-a999-999999999999"
    Then Response code is "200"
    Examples:
      | url                                                  | module          | since      | until      | granularity |
      | web_performance/analytics                            | web_performance | 2016-12-07 | 2016-12-07 | day         |
      | web_performance/analytics/conversion_rates           | web_performance | 2016-08-08 | 2016-12-07 | week        |
      | web_performance/analytics/conversion_rates/countries | web_performance | 2016-09-07 | 2017-03-07 | month       |
      | web_performance/analytics/referrals                  | web_performance | 2016-08-08 | 2016-12-07 | week        |
      | web_performance/analytics/referrals/channels         | web_performance | 2016-12-07 | 2016-12-07 | day         |
      | web_performance/analytics/revenue                    | web_performance | 2016-08-08 | 2016-12-07 | week        |
      | web_performance/analytics/top_values                 | web_performance | 2016-09-07 | 2017-03-07 | month       |
      | web_performance/analytics/visits                     | web_performance | 2016-08-08 | 2016-12-07 | week        |
      | web_performance/analytics/visits/countries           | web_performance | 2016-12-07 | 2016-12-07 | day         |
      | web_performance/analytics/visits_unique              | web_performance | 2016-08-08 | 2016-12-07 | week        |
      | web_performance/analytics/visits_unique/countries    | web_performance | 2016-09-07 | 2017-03-07 | month       |