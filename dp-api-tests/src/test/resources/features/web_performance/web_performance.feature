Feature: web_performance


  Background:
    Given Database is cleaned and default entities are created


  Scenario Outline: Get web_performance analytics data from API for a given wrong granularity
    When Get web_performance "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "2015-12-03" until "2015-12-03"
    Then Response code is "400"
    And Content type is "application/json"
    And Custom code is "40002"

    Examples:
      | url                         | granularity |
      | /analytics                  | ddd         |
      | /analytics/visits           | ddd         |
      | /analytics/visits_unique    | www         |
      | /analytics/revenue          | yyy         |
      | /analytics/conversion_rates | ttt         |

  Scenario Outline: Validate that metrics have valid value in the db
    When Get web_performance "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Response code is "200"
    And Content type is "application/json"
    And Value number "1" of value type "<type>" has value "<count>" and is incomplete "<incomplete>"

    Examples:
      | url                         | granularity | count | since      | until      | type                  | incomplete |
      | /analytics/visits           | day         | 6738  | 2015-11-03 | 2015-11-03 | SingleStatsDto        | false      |
      | /analytics/visits_unique    | day         | 7912  | 2015-12-03 | 2015-12-03 | SingleStatsDto        | false      |
      | /analytics/revenue          | day         | 7373  | 2015-12-03 | 2015-12-03 | PeriodAverageStatsDto | false      |
      | /analytics/conversion_rates | day         | 321   | 2015-12-03 | 2015-12-03 | PeriodAverageStatsDto | false      |
      | /analytics/visits           | week        | 7439  | 2015-12-03 | 2015-12-14 | SingleStatsDto        | false      |
      | /analytics/visits_unique    | week        | 7947  | 2015-12-03 | 2015-12-14 | SingleStatsDto        | false      |
      | /analytics/revenue          | week        | 7453  | 2015-12-03 | 2015-12-14 | PeriodAverageStatsDto | false      |
      | /analytics/conversion_rates | week        | 277   | 2015-12-03 | 2015-12-14 | PeriodAverageStatsDto | false      |
      | /analytics/visits           | month       | 6701  | 2015-10-03 | 2015-12-03 | SingleStatsDto        | false      |
      | /analytics/visits_unique    | month       | 7182  | 2015-10-03 | 2015-12-03 | SingleStatsDto        | false      |
      | /analytics/revenue          | month       | 6677  | 2015-10-03 | 2015-12-03 | PeriodAverageStatsDto | false      |
      | /analytics/conversion_rates | month       | 246   | 2015-10-03 | 2015-12-03 | PeriodAverageStatsDto | true       |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get web_performance "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Response code is "200"
    And Data is owned by "Google Analytics"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> values

    Examples:
      | url                         | granularity | count | since      | until      |
      | /analytics/visits           | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /analytics/visits_unique    | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /analytics/revenue          | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /analytics/conversion_rates | day         | 1     | 2015-12-07 | 2015-12-07 |
      | /analytics/visits           | day         | 11    | 2015-11-03 | 2015-11-13 |
      | /analytics/visits_unique    | day         | 11    | 2015-11-03 | 2015-11-13 |
      | /analytics/revenue          | day         | 11    | 2015-11-03 | 2015-11-13 |
      | /analytics/conversion_rates | day         | 11    | 2015-11-03 | 2015-11-13 |
      | /analytics/visits           | day         | 17    | 2015-11-07 | 2015-11-23 |
      | /analytics/visits_unique    | day         | 17    | 2015-11-07 | 2015-11-23 |
      | /analytics/revenue          | day         | 17    | 2015-11-07 | 2015-11-23 |
      | /analytics/conversion_rates | day         | 17    | 2015-11-07 | 2015-11-23 |
      | /analytics/visits           | day         | 379   | 2015-01-01 | 2016-01-14 |
      | /analytics/visits_unique    | day         | 379   | 2015-01-01 | 2016-01-14 |
      | /analytics/revenue          | day         | 379   | 2015-01-01 | 2016-01-14 |
      | /analytics/conversion_rates | day         | 379   | 2015-01-01 | 2016-01-14 |
      | /analytics/visits           | week        | 3     | 2015-11-01 | 2015-11-13 |
      | /analytics/visits_unique    | week        | 3     | 2015-11-01 | 2015-11-13 |
      | /analytics/revenue          | week        | 3     | 2015-11-01 | 2015-11-13 |
      | /analytics/conversion_rates | week        | 3     | 2015-11-01 | 2015-11-13 |
      | /analytics/visits           | week        | 4     | 2015-11-07 | 2015-11-23 |
      | /analytics/visits_unique    | week        | 4     | 2015-11-07 | 2015-11-23 |
      | /analytics/revenue          | week        | 4     | 2015-11-07 | 2015-11-23 |
      | /analytics/conversion_rates | week        | 4     | 2015-11-07 | 2015-11-23 |
      | /analytics/visits           | week        | 3     | 2015-11-01 | 2015-11-13 |
      | /analytics/visits_unique    | week        | 3     | 2015-11-01 | 2015-11-13 |
      | /analytics/revenue          | week        | 3     | 2015-11-01 | 2015-11-13 |
      | /analytics/conversion_rates | week        | 3     | 2015-11-01 | 2015-11-13 |
      | /analytics/visits           | week        | 55    | 2015-01-01 | 2016-01-14 |
      | /analytics/visits_unique    | week        | 55    | 2015-01-01 | 2016-01-14 |
      | /analytics/revenue          | week        | 107   | 2014-01-01 | 2016-01-14 |
      | /analytics/conversion_rates | week        | 107   | 2014-01-01 | 2016-01-14 |
      | /analytics/visits           | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /analytics/visits_unique    | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /analytics/revenue          | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /analytics/conversion_rates | month       | 1     | 2015-11-01 | 2015-11-30 |
      | /analytics/visits           | month       | 2     | 2015-10-01 | 2015-11-23 |
      | /analytics/visits_unique    | month       | 2     | 2015-10-01 | 2015-11-23 |
      | /analytics/revenue          | month       | 2     | 2015-10-01 | 2015-11-23 |
      | /analytics/conversion_rates | month       | 2     | 2015-10-01 | 2015-11-23 |
      | /analytics/visits           | month       | 4     | 2015-10-01 | 2016-01-14 |
      | /analytics/visits_unique    | month       | 4     | 2015-10-01 | 2016-01-14 |
      | /analytics/revenue          | month       | 4     | 2015-10-01 | 2016-01-14 |
      | /analytics/conversion_rates | month       | 4     | 2015-10-05 | 2016-01-14 |
      | /analytics/conversion_rates | month       | 5     | 2015-09-28 | 2016-01-14 |

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
      | /analytics/visits                     | 400           | 40002       | application/json |
      | /analytics/visits_unique              | 400           | 40002       | application/json |
      | /analytics/revenue                    | 400           | 40002       | application/json |
      | /analytics/conversion_rates           | 400           | 40002       | application/json |
      | /analytics/visits/countries           | 400           | 40002       | application/json |
      | /analytics/visits_unique/countries    | 400           | 40002       | application/json |
      | /analytics/conversion_rates/countries | 400           | 40002       | application/json |
      | /analytics/referrals                  | 400           | 40002       | application/json |

  Scenario Outline: Checking non-valid since and until parameters
    When Get web_performance "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "message" value "There is a problem with some parameters. See details."
    Examples:
      | url                         | granularity | since | until             | response_code | custom_code |
      | /analytics/visits           | day         | today | today - 100 days  | 400           | 40002       |
      | /analytics/visits           | week        | today | today - 30 weeks  | 400           | 40002       |
      | /analytics/visits           | month       | today | today - 40 months | 400           | 40002       |
      # this returns 200, case when both dates are in future is not treated yet
      #| /analytics/visits           | day         | today + 2 days | today + 3 days    | 400           | 40002       |
      | /analytics/visits_unique    | day         | today | today - 100 days  | 400           | 40002       |
      | /analytics/visits_unique    | week        | today | today - 30 weeks  | 400           | 40002       |
      | /analytics/visits_unique    | month       | today | today - 40 months | 400           | 40002       |
      # this returns 200, case when both dates are in future is not treated yet
      #| /analytics/visits_unique    | day         | today + 2 days | today + 3 days    | 200           | 40002       |
      | /analytics/revenue          | day         | today | today - 100 days  | 400           | 40002       |
      | /analytics/revenue          | week        | today | today - 30 weeks  | 400           | 40002       |
      | /analytics/revenue          | month       | today | today - 40 months | 400           | 40002       |
      # this returns 200, case when both dates are in future is not treated yet
      #| /analytics/revenue          | day         | today + 2 days | today + 3 days    | 400           | 40002       |
      | /analytics/conversion_rates | day         | today | today - 100 days  | 400           | 40002       |
      | /analytics/conversion_rates | week        | today | today - 30 weeks  | 400           | 40002       |
      | /analytics/conversion_rates | month       | today | today - 40 months | 400           | 40002       |
      # this returns 200, case when both dates are in future is not treated yet
      #| /analytics/conversion_rates | day         | today + 2 days | today + 3 days    | 400           | 40002       |

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
      | /analytics/visits           | week        | 2016-03-01     | 2016-03-10 | 2     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits           | week        | 2016-02-25     | 2016-03-10 | 3     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits           | week        | 2016-02-18     | 2016-03-10 | 4     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | month       | 2016-03-01     | 2016-03-01 | 1     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | month       | 2016-02-01     | 2016-03-01 | 2     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | month       | 2016-01-01     | 2016-03-01 | 3     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/visits_unique    | month       | 2016-02-10     | 2016-03-10 | 2     | 99000099-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Verifying sorting
    When Get web performance referrals with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<since>" until "<until>" sorted by "<metric>" "<direction>"
    Then Response code is "200"
    And Content type is "application/json"
    And Values are sorted by "<metric>" in "<direction>"

    Examples:
      | since           | until      | granularity | metric        | direction  |
      | today - 80 days | today      | day         | visits        | ascending  |
      | today - 80 days | today      | week        | revenue       | ascending  |
      | today - 80 days | today      | month       | visits_unique | ascending  |
      | today - 80 days | today      | day         | visits        | descending |
      | today - 80 days | today      | week        | revenue       | descending |
      | today - 80 days | today      | month       | visits_unique | descending |
      | 2016-01-01      | today      | day         | visits        | ascending  |
      | 2016-01-01      | today      | week        | revenue       | ascending  |
      | 2016-01-01      | today      | month       | visits_unique | ascending  |
      | 2016-01-01      | today      | day         | visits        | descending |
      | 2016-01-01      | today      | week        | revenue       | descending |
      | 2016-01-01      | today      | month       | visits_unique | descending |
      | 2016-01-01      | 2016-01-10 | day         | visits        | ascending  |
      | 2016-01-01      | 2016-01-10 | week        | revenue       | ascending  |
      | 2016-01-01      | 2016-01-10 | month       | visits_unique | ascending  |
      | 2016-01-01      | 2016-01-10 | day         | visits        | descending |
      | 2016-01-01      | 2016-01-10 | week        | revenue       | descending |
      | 2016-01-01      | 2016-01-10 | month       | visits_unique | descending |

  Scenario Outline: Checking error codes for sorting
    When Get web performance referrals with "day" granularity for "99000099-9999-4999-a999-999999999999" since "today - 80 days" until "today" sorted by "<metric>" "<direction>"
    Then Content type is "application/json"
    And Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | metric  | direction  | response_code | custom_code |
      | invalid | ascending  | 400           | 40002       |
      | invalid | descending | 400           | 40002       |

  Scenario Outline: Checking ISO country codes
    When Get web_performance "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Response code is "200"
    And All web performance analytics country codes in "<json_argument>" are in ISO format

    Examples:
      | url        | granularity | since            | until | json_argument                 |
      | /analytics | day         | today - 3 months | today | top_values.country_conversion |
      | /analytics | day         | today - 30 days  | today | top_values.country_visits     |

  Scenario Outline: Testing holes in Fact_web_performance tables
    When Get web_performance "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Response code is "200"
    And Content type is "application/json"
    And Response contains <count> values
    And Value number "<value_number>" of value type "<value_type>" has value "<value>" and is incomplete "<incomplete>"

    # there are holes in 2016-02-02 and 2016-02-07

    Examples:
      | url               | count | granularity | since      | until      | value_number | value_type     | value | incomplete |
      | /analytics/visits | 14    | day         | 2016-02-01 | 2016-02-14 | 2            | singleStatsDto | /null | true       |
      | /analytics/visits | 14    | day         | 2016-02-01 | 2016-02-14 | 7            | singleStatsDto | /null | true       |
      | /analytics/visits | 2     | week        | 2016-02-01 | 2016-02-14 | 1            | singleStatsDto | 8852  | true       |
      | /analytics/visits | 2     | week        | 2016-02-01 | 2016-02-14 | 2            | singleStatsDto | 9086  | false      |

  # TODO: DP-2014 - time based collection pagination is disabled, the test fails when it is enabled
#  DP-2043
  @skipped
  Scenario Outline: Get analytics data with granularity and large interval
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Data is owned by "Google Analytics"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> amount of values for global stats dto

    Examples:
      | url        | granularity | count | since      | until      | real_since | real_until | property                             |
      | /analytics | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |

  # TODO: DP-2014 - time based collection pagination is disabled, the test fails when it is enabled
  Scenario Outline: Get specific analytics data with granularity and large time interval
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Data is owned by "Google Analytics"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> values

    Examples:
      | url                         | granularity | count | since      | until      | real_since | real_until | property                             |
      | /analytics/conversion_rates | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/revenue          | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits           | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits_unique    | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |

      | /analytics/conversion_rates | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/revenue          | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits           | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits_unique    | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |

      | /analytics/conversion_rates | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/revenue          | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits           | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits_unique    | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |

  # TODO: DP-2014 - time based collection pagination is disabled, the test fails when it is enabled
  Scenario Outline: Get specific analytics data without granularity and with large time interval and pagination ignored
    When List of web performance "<url>" for property id "<property>" is got with limit "1" and cursor "0" and granularity "day" and since "<since>" and until "<until>"
    When Get web_performance "<url>" data with "day" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Data is owned by "Google Analytics"
    And Response contains <count> values

    Examples:
      | url                                   | count | since      | until      | property                             |
      | /analytics/conversion_rates/countries | 249   | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/referrals                  | 100   | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/referrals/channels         | 11    | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits/countries           | 249   | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/visits_unique/countries    | 249   | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
