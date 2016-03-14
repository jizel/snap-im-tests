Feature: web_performance

  Scenario Outline: Get web_performance analytics data from API for a given wrong granularity
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Custom code is "<custom_code>"

    Examples:
      | url                         | granularity | property                             | since      | until      | content_type     | response_code | custom_code |
      | /analytics                  | ddd         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/visits           | ddd         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/visits_unique    | www         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/revenue          | yyy         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/conversion_rates | ttt         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |

  Scenario Outline: Validate that metrics have valid value in the db
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And The metric count is <count>

    Examples:
      | url                         | granularity | count | since      | until      | property                             | content_type     | response_code |
      | /analytics/visits           | day         | 6738  | 2015-11-03 | 2015-11-03 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | day         | 7912  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/revenue          | day         | 7373  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/conversion_rates | day         | 321   | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits           | week        | 7548  | 2015-12-03 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | week        | 8112  | 2015-12-03 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/revenue          | week        | 7631  | 2015-12-03 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/conversion_rates | week        | 193   | 2015-12-03 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits           | month       | 7337  | 2015-10-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | month       | 7838  | 2015-10-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/revenue          | month       | 7337  | 2015-10-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/conversion_rates | month       | 186   | 2015-10-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> values
    And Response since is "<expected_since>" for granularity "<granularity>"
    And Response until is "<expected_until>" for granularity "<granularity>"

    # TODO commented lines deal with counts which are chaging according to date when these tests are executed, I have not found a way how to make "count" value dynamically changed as well

    Examples:
      | url                         | granularity | count | since      | until      | expected_since | expected_until | property                             | response_code | data_owner       |
      | /analytics/visits           | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07     | 2015-12-07     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits_unique    | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07     | 2015-12-07     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/revenue          | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07     | 2015-12-07     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/conversion_rates | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07     | 2015-12-07     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits           | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits_unique    | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/revenue          | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/conversion_rates | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits           | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits_unique    | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/revenue          | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/conversion_rates | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
#      | /analytics/visits           | day         | 311   | 2015-01-01 | 2016-01-14 | today - 12 months | 2016-01-14     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
#      | /analytics/visits_unique    | day         | 311   | 2015-01-01 | 2016-01-14 | today - 12 months | 2016-01-14     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
#      | /analytics/revenue          | day         | 311   | 2015-01-01 | 2016-01-14 | today - 12 months | 2016-01-14     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
#      | /analytics/conversion_rates | day         | 311   | 2015-01-01 | 2016-01-14 | today - 12 months | 2016-01-14     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits           | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits_unique    | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/revenue          | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/conversion_rates | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits           | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-11-09     | 2015-11-22     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits_unique    | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-11-09     | 2015-11-22     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/revenue          | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-11-09     | 2015-11-22     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/conversion_rates | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-11-09     | 2015-11-22     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
#      | /analytics/visits           | week        | 43    | 2015-01-01 | 2016-01-14 | today - 12 months | 2016-01-10     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
#      | /analytics/visits_unique    | week        | 43    | 2015-01-01 | 2016-01-14 | today - 12 months | 2016-01-10     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
#      | /analytics/revenue          | week        | 43    | 2014-01-01 | 2016-01-14 | today - 12 months | 2016-01-10     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
#      | /analytics/conversion_rates | week        | 43    | 2014-01-01 | 2016-01-14 | today - 12 months | 2016-01-10     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits           | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits_unique    | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/revenue          | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/conversion_rates | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits           | month       | 1     | 2015-10-01 | 2015-11-23 | 2015-10-01     | 2015-10-31     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits_unique    | month       | 1     | 2015-10-01 | 2015-11-23 | 2015-10-01     | 2015-10-31     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/revenue          | month       | 1     | 2015-10-01 | 2015-11-23 | 2015-10-01     | 2015-10-31     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/conversion_rates | month       | 1     | 2015-10-01 | 2015-11-23 | 2015-10-01     | 2015-10-31     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits           | month       | 3     | 2015-10-01 | 2016-01-14 | 2015-10-01     | 2015-12-31     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits_unique    | month       | 3     | 2015-10-01 | 2016-01-14 | 2015-10-01     | 2015-12-31     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/revenue          | month       | 3     | 2015-10-01 | 2016-01-14 | 2015-10-01     | 2015-12-31     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/conversion_rates | month       | 2     | 2015-10-05 | 2016-01-14 | 2015-11-01     | 2015-12-31     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/conversion_rates | month       | 3     | 2015-09-28 | 2016-01-14 | 2015-10-01     | 2015-12-31     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |

  Scenario Outline: Get specific analytics data from API for a given week granularity
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Response contains <count> values

    Examples:
      | url                         | granularity | count | since      | until      | expected_since | expected_until | property                             | response_code | data_owner       |
      | /analytics/visits           | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/visits_unique    | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/revenue          | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |
      | /analytics/conversion_rates | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 99000099-9999-4999-a999-999999999999 | 200           | Google Analytics |

  Scenario Outline: Getting non-existent analytics data
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>

    Examples:
      | url                           | granularity | property                             | since      | until      | response_code |
      | /analytics/visits/not_present | day         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | 404           |

  Scenario Outline: Checking error codes for analytics data
    When Get web_performance "<url>" with missing property header
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Custom code is "<custom_code>"

    Examples:
      | url                                   | response_code | custom_code | content_type     |
      | /analytics/visits                     | 400           | 52          | application/json |
      | /analytics/visits_unique              | 400           | 52          | application/json |
      | /analytics/revenue                    | 400           | 52          | application/json |
      | /analytics/conversion_rates           | 400           | 52          | application/json |
      | /analytics/visits/countries           | 400           | 52          | application/json |
      | /analytics/visits_unique/countries    | 400           | 52          | application/json |
      | /analytics/conversion_rates/countries | 400           | 52          | application/json |
      | /analytics/referrals                  | 400           | 52          | application/json |

# Uncomment once since and until works ok.
#
#  Scenario Outline: Get analytics data from API from 1800s
#    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
#    Then Response code is <response_code>
#    And Content type is "<content_type>"
#    And Response since is "<expected_since>" for granularity "<granularity>"
#    And Response until is "<expected_until>" for granularity "<granularity>"
#
#    Examples:
#      | url                         | granularity | since      | until      | property                             | expected_since    | expected_until | content_type     | response_code |
#      | /analytics                  | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 | today - 12 months | 2015-12-03     | application/json | 200           |
#      | /analytics/visits           | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 | today - 12 months | 2015-12-03     | application/json | 200           |
#      | /analytics/visits_unique    | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 | today - 12 months | 2015-12-03     | application/json | 200           |
#      | /analytics/revenue          | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 | today - 12 months | 2015-12-03     | application/json | 200           |
#      | /analytics/conversion_rates | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 | today - 12 months | 2015-12-03     | application/json | 200           |
#      | /analytics                  | day         | 1888-09-01 | 1888-09-01 | 99000099-9999-4999-a999-999999999999 | today - 12 months | 2015-12-03     | application/json | 200           |
#      | /analytics/visits           | day         | 1888-09-01 | 1888-09-01 | 99000099-9999-4999-a999-999999999999 | today - 12 months | 2015-12-03     | application/json | 200           |
#      | /analytics/visits_unique    | day         | 1888-09-01 | 1888-09-01 | 99000099-9999-4999-a999-999999999999 | today - 12 months | 2015-12-03     | application/json | 200           |
#      | /analytics/revenue          | day         | 1888-09-01 | 1888-09-01 | 99000099-9999-4999-a999-999999999999 | today - 12 months | 2015-12-03     | application/json | 200           |
#      | /analytics/conversion_rates | day         | 1888-09-01 | 1888-09-01 | 99000099-9999-4999-a999-999999999999 | today - 12 months | 2015-12-03     | application/json | 200           |

  Scenario Outline: Checking default parameter values
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>" for granularity "<expected_granularity>"
    And Response until is "<expected_until>" for granularity "<expected_granularity>"

    # Single comments comment out requests which does not have since or until specified
    # Double comments represents invalid input data which return responses which does not make sense

    Examples:
      | url                         | granularity | since          | until             | expected_granularity | expected_since    | expected_until | property                             | response_code |
      | /analytics/visits           |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99000099-9999-4999-a999-999999999999 | 200           |
      #| /analytics/visits           | day         |                | today             | day                  | today - 30 days   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      #| /analytics/visits           | day         | today          |                   | day                  | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      #| /analytics/visits           | week        |                | today             | week                 | today - 14 weeks  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      #| /analytics/visits           | week        | today          |                   | week                 | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      #| /analytics/visits           | month       |                | today             | month                | today - 7 months  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      #| /analytics/visits           | month       | today          |                   | month                | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |

      ##| /analytics/visits           | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      ##| /analytics/visits           | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      ##| /analytics/visits           | month       | today          | today - 40 months | month                | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      ##| /analytics/visits           | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/visits_unique    |             |                |                   | day                  | today - 31 days   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      | /analytics/visits_unique    |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/visits_unique    | day         |                | today             | day                  | today - 1 month   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/visits_unique    | day         | today          |                   | day                  | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/visits_unique    | week        |                | today             | week                 | today - 13 weeks  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/visits_unique    | week        | today          |                   | week                 | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/visits_unique    | month       |                | today             | month                | today - 6 months  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/visits_unique    | month       | today          |                   | month                | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/visits_unique    | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/visits_unique    | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/visits_unique    | month       | today          | today - 40 months | month                | today - 36 months | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/visits_unique    | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/revenue          |             |                |                   | day                  | today - 1 month   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      | /analytics/revenue          |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/revenue          | day         |                | today             | day                  | today - 1 month   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/revenue          | day         | today          |                   | day                  | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/revenue          | week        |                | today             | week                 | today - 13 weeks  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/revenue          | week        | today          |                   | week                 | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/revenue          | month       |                | today             | month                | today - 6 months  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/revenue          | month       | today          |                   | month                | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/revenue          | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/revenue          | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/revenue          | month       | today          | today - 40 months | month                | today - 36 months | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/revenue          | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/conversion_rates |             |                |                   | day                  | today - 1 month   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
      | /analytics/conversion_rates |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/conversion_rates | day         |                | today             | day                  | today - 1 month   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/conversion_rates | day         | today          |                   | day                  | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/conversion_rates | week        |                | today             | week                 | today - 13 weeks  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/conversion_rates | week        | today          |                   | week                 | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/conversion_rates | month       |                | today             | month                | today - 6 months  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
#      | /analytics/conversion_rates | month       | today          |                   | month                | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/conversion_rates | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/conversion_rates | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/conversion_rates | month       | today          | today - 40 months | month                | today - 36 months | today          | 99000099-9999-4999-a999-999999999999 | 200           |
##      | /analytics/conversion_rates | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99000099-9999-4999-a999-999999999999 | 200           |

  Scenario Outline: Checking number of values in response for various granularities
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response contains <count> values

    Examples:
      | url                         | granularity | since          | until      | count | property                             | content_type     | response_code |
      | /analytics/visits           | day         | today - 1 day  | today      | 2     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | day         | today - 6 days | today      | 7     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/revenue          | day         | today - 7 days | today      | 8     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/conversion_rates | day         | today - 8 days | today      | 9     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits           | week        | 2016-03-01     | 2016-03-10 | 0     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits           | week        | 2016-02-25     | 2016-03-10 | 1     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits           | week        | 2016-02-18     | 2016-03-10 | 2     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | month       | 2016-03-01     | 2016-03-01 | 0     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | month       | 2016-02-01     | 2016-03-01 | 1     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | month       | 2016-01-01     | 2016-03-01 | 2     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | month       | 2016-02-10     | 2016-03-10 | 0     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Verifying sorting
    When Get web performance referrals with "<granularity>" granularity for "<property>" since "<since>" until "<until>" sorted by "<metric>" "<direction>"
    Then Response code is "200"
    And Content type is "application/json"
    And Values are sorted by "<metric>" in "<direction>"

    Examples:
      | property                             | since           | until      | granularity | metric        | direction  |
      | 99000099-9999-4999-a999-999999999999 | today - 80 days | today      | day         | visits        | ascending  |
      | 99000099-9999-4999-a999-999999999999 | today - 80 days | today      | week        | revenue       | ascending  |
      | 99000099-9999-4999-a999-999999999999 | today - 80 days | today      | month       | visits_unique | ascending  |
      | 99000099-9999-4999-a999-999999999999 | today - 80 days | today      | day         | visits        | descending |
      | 99000099-9999-4999-a999-999999999999 | today - 80 days | today      | week        | revenue       | descending |
      | 99000099-9999-4999-a999-999999999999 | today - 80 days | today      | month       | visits_unique | descending |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | today      | day         | visits        | ascending  |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | today      | week        | revenue       | ascending  |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | today      | month       | visits_unique | ascending  |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | today      | day         | visits        | descending |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | today      | week        | revenue       | descending |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | today      | month       | visits_unique | descending |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | 2016-01-10 | day         | visits        | ascending  |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | 2016-01-10 | week        | revenue       | ascending  |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | 2016-01-10 | month       | visits_unique | ascending  |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | 2016-01-10 | day         | visits        | descending |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | 2016-01-10 | week        | revenue       | descending |
      | 99000099-9999-4999-a999-999999999999 | 2016-01-01      | 2016-01-10 | month       | visits_unique | descending |

  Scenario Outline: Checking error codes for sorting
    When Get web performance referrals with "day" granularity for "99000099-9999-4999-a999-999999999999" since "today - 80 days" until "today" sorted by "<metric>" "<direction>"
    Then Content type is "application/json"
    And Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | metric  | direction  | response_code | custom_code |
      | invalid | ascending  | 400           | 63          |
      | invalid | descending | 400           | 63          |

  Scenario Outline: Checking ISO country codes
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And All web performance analytics country codes in "<json_argument>" are in ISO format

    Examples:
      | url        | granularity | since            | until | property                             | response_code | json_argument                 |
      | /analytics | day         | today - 3 months | today | 99000099-9999-4999-a999-999999999999 | 200           | top_values.country_conversion |
      | /analytics | day         | today - 30 days  | today | 99000099-9999-4999-a999-999999999999 | 200           | top_values.country_visits     |

  Scenario Outline: Getting a list of items
    When List of web performance "<url>" for property id "<property>" is got with limit "<limit>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response contains <count> values

    # Commented out examples does not work yet because limitting / cursor feature does not work at all

    Examples:
      | url                                   | limit | count | response_code | content_type     | property                             |
      | /analytics/visits/countries           |       | 25    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits/countries           | /null | 25    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits/countries           | 26    | 25    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
#      | /analytics/visits/countries           | 24    | 24    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits_unique/countries    |       | 25    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits_unique/countries    | /null | 25    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits_unique/countries    | 26    | 25    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
#      | /analytics/visits_unique/countries    | 24    | 24    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/conversion_rates/countries |       | 25    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/conversion_rates/countries | /null | 25    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/conversion_rates/countries | 26    | 25    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
#      | /analytics/conversion_rates/countries | 24    | 24    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/referrals                  |       | 26    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/referrals                  | /null | 26    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
      | /analytics/referrals                  | 26    | 26    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |
#      | /analytics/referrals                  | 24    | 24    | 200           | application/json | 99000099-9999-4999-a999-999999999999 |