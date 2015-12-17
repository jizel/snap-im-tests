Feature: web_performance

  Scenario Outline: Get web_performance analytics data from API for a given wrong granularity
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                         | granularity | property                             | since      | until      | content_type     | response_code | custom_code |
      | /analytics/visits           | ddd         | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/visits_unique    | www         | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/revenue          | yyy         | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/conversion_rates | ttt         | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |

  Scenario Outline: Validate that metrics have valid value in the db
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And The metric count is <count>

    Examples: 
      | url                         | granularity | count | since      | until      | property                             | content_type     | response_code |
      | /analytics/visits           | day         | 10238     | 2015-11-03 | 2015-11-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits_unique    | day         | 12839     | 2015-12-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/revenue          | day         | 119745.664     | 2015-12-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/conversion_rates | day         | 1     | 2015-12-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits           | day         | 2     | 2015-12-03 | 2015-12-09 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits_unique    | day         | 2     | 2015-12-03 | 2015-12-09 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/revenue          | day         | 2     | 2015-12-03 | 2015-12-09 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/conversion_rates | day         | 2     | 2015-12-03 | 2015-12-09 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits           | day         | 3     | 2015-11-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits_unique    | day         | 3     | 2015-11-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/revenue          | day         | 3     | 2015-11-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/conversion_rates | day         | 3     | 2015-11-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    #And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body contains entity with attribute "since" value "<calculated_statistics_real_since>"
    And Body contains entity with attribute "until" value "<until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> values

    Examples: 
      | url                         | granularity | count | since      | until      | calculated_statistics_real_since | property                             | content_type     | response_code | data_owner |
      | /analytics/visits           | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits           | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits           | day         | 23    | 2015-11-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | day         | 23    | 2015-11-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | day         | 23    | 2015-11-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | day         | 23    | 2015-11-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits           | day         | 92    | 2015-06-07 | 2015-12-07 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | day         | 92    | 2015-06-07 | 2015-12-07 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | day         | 92    | 2015-06-07 | 2015-12-07 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | day         | 92    | 2015-06-07 | 2015-12-07 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits           | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits           | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits           | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits           | week        | 26    | 2015-01-07 | 2015-12-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | week        | 26    | 2015-01-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | week        | 26    | 2015-01-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | week        | 26    | 2015-01-07 | 2015-11-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits           | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits           | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits           | month       | 36    | 2013-02-01 | 2015-11-30 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/visits_unique    | month       | 36    | 2013-02-01 | 2015-11-30 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/revenue          | month       | 36    | 2013-02-01 | 2015-11-30 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |
      | /analytics/conversion_rates | month       | 36    | 2013-02-01 | 2015-11-30 | 2015-12-07                       | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | google     |

  Scenario Outline: Getting non-existent analytics data
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property<" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples: 
      | url                           | granularity | property                             | since      | until      | response_code | custom_code |
      | /analytics/visits/not_present | day         | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | 404           | 151         |

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

  Scenario Outline: Get analytics data from API with missing parameters
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response contains <count> values

    Examples: 
      | url                                   | granularity | since      | until      | count | property                             | response_code | content_type     |
      | /analytics/visits                     |             | 2015-12-03 | 2015-12-03 | 1     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique              | day         |            | 2015-12-03 | 31    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue                    | day         | 2015-12-03 |            | 31    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates           | day         |            |            | 31    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits/countries           |             |            |            | 31    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique/countries    | day         | 2015-12-03 |            | 31    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates/countries | day         |            |            | 31    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/referrals                  |             |            |            | 31    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |

  Scenario Outline: Get analytics data from API from 1800s
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Body contains entity with attribute "since" value "<since>"
    And Body contains entity with attribute "until" value "<until>"
	And Custom code is "<custom_code>"

    Examples: 
      | url                         | granularity | since      | until      | property                             | since      | until      | content_type     | response_code | custom_code |
      | /analytics                  | month       | 1888-09-01 | 1890-10-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           | 51          |
      | /analytics/visits           | month       | 1888-09-01 | 1890-10-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           | 51          |
      | /analytics/visits_unique    | month       | 1888-09-01 | 1890-10-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           | 51          |
      | /analytics/revenue          | month       | 1888-09-01 | 1890-10-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           | 51          |
      | /analytics/conversion_rates | month       | 1888-09-01 | 1890-10-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           | 51          |
      | /analytics                  | day         | 1888-09-01 | 1888-09-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           | 51          |
      | /analytics/visits           | day         | 1888-09-01 | 1888-09-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           | 51          |
      | /analytics/visits_unique    | day         | 1888-09-01 | 1888-09-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           | 51          |
      | /analytics/revenue          | day         | 1888-09-01 | 1888-09-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           | 51          |
      | /analytics/conversion_rates | day         | 1888-09-01 | 1888-09-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           | 51          |

  Scenario Outline: Checking default parameter values
    Empty column in examples section means default value will be used for this parameter.
    if text is empty, returns null
    if text is date in ISO format (2015-01-01), it returns this date
    text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which will add or substract
    particular number of days/weeks/months from first part of expression

    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Response contains correct number of values for granularity "<granularity>" between "<expected_since>" and "<expected_until>"

    Examples: 
      | url                         | granularity | since          | until             | expected_granularity | expected_since    | expected_until | property                             | response_code | content_type     |
      | /analytics/visits           |             |                |                   | day                  | today - 1 month   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | day         |                | today             | day                  | today - 1 month   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | day         | today          |                   | day                  | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | week        |                | today             | week                 | today - 13 weeks  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | week        | today          |                   | week                 | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | month       |                | today             | month                | today - 6 months  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | month       | today          |                   | month                | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | month       | today          | today - 40 months | month                | today - 36 months | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    |             |                |                   | day                  | today - 1 month   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    | day         |                | today             | day                  | today - 1 month   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    | day         | today          |                   | day                  | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    | week        |                | today             | week                 | today - 13 weeks  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    | week        | today          |                   | week                 | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    | month       |                | today             | month                | today - 6 months  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    | month       | today          |                   | month                | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    | month       | today          | today - 40 months | month                | today - 36 months | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique    | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          |             |                |                   | day                  | today - 1 month   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          | day         |                | today             | day                  | today - 1 month   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          | day         | today          |                   | day                  | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          | week        |                | today             | week                 | today - 13 weeks  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          | week        | today          |                   | week                 | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          | month       |                | today             | month                | today - 6 months  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          | month       | today          |                   | month                | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          | month       | today          | today - 40 months | month                | today - 36 months | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue          | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates |             |                |                   | day                  | today - 1 month   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates | day         |                | today             | day                  | today - 1 month   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates | day         | today          |                   | day                  | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates | week        |                | today             | week                 | today - 13 weeks  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates | week        | today          |                   | week                 | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates | month       |                | today             | month                | today - 6 months  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates | month       | today          |                   | month                | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates | month       | today          | today - 40 months | month                | today - 36 months | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |

  Scenario Outline: Checking number of values in response for various granularities
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response contains <count> values

    Examples: 
      | url                         | granularity | since          | until | count | property                             | content_type     | response_code |
      | /analytics/visits           | day         | today - 1 day  | today | 2     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits_unique    | day         | today - 6 days | today | 7     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/revenue          | day         | today - 7 days | today | 8     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/conversion_rates | day         | today - 8 days | today | 9     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
