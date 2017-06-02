@SocialMedia
Feature: Social media - External applications access checks
  - Application should have acccess to social media endpoints only if it's is_internal attribute is set to true
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
      | url                                             | module       | since      | until      | granularity |
      | social_media/analytics                          | social_media | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/followers                | social_media | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/engagement               | social_media | 2016-12-12 | 2017-01-12 | week        |
      | social_media/analytics/reach                    | social_media | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/facebook                 | facebook     | 2016-12-07 | 2017-03-15 | month       |
      | social_media/analytics/facebook/posts           | facebook     | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/facebook/number_of_posts | facebook     | 2016-12-07 | 2017-03-15 | month       |
      | social_media/analytics/facebook/engagement      | facebook     | 2016-12-12 | 2017-01-12 | week        |
      | social_media/analytics/facebook/likes           | facebook     | 2016-12-07 | 2017-03-15 | month       |
      | social_media/analytics/facebook/unlikes         | facebook     | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/facebook/reach           | facebook     | 2016-12-12 | 2017-01-12 | week        |
      | social_media/analytics/facebook/followers       | facebook     | 2016-12-07 | 2017-03-15 | month       |
      | social_media/analytics/instagram                | instagram    | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/instagram/pictures       | instagram    | 2016-12-07 | 2017-03-15 | month       |
      | social_media/analytics/instagram/engagement     | instagram    | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/instagram/followers      | instagram    | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/instagram/tags           | instagram    | 2016-12-12 | 2017-01-12 | week        |
      | social_media/analytics/instagram/reach          | instagram    | 2016-12-07 | 2017-03-15 | month       |
      | social_media/analytics/instagram/likes          | instagram    | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/instagram/comments       | instagram    | 2016-12-07 | 2017-03-15 | month       |
      | social_media/analytics/twitter                  | twitter      | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/twitter/tweets           | twitter      | 2016-12-12 | 2017-01-12 | week        |
      | social_media/analytics/twitter/number_of_tweets | twitter      | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/twitter/engagement       | twitter      | 2016-12-12 | 2017-01-12 | week        |
      | social_media/analytics/twitter/followers        | twitter      | 2016-12-07 | 2016-12-07 | day         |
      | social_media/analytics/twitter/impressions      | twitter      | 2016-12-07 | 2017-03-15 | month       |