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
      | url                         | granularity | count    | since      | until      | property                             | content_type     | response_code |
      | /analytics/visits           | day         | 2981795  | 2015-11-03 | 2015-11-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits_unique    | day         | 2789996  | 2015-12-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/revenue          | day         | 32174862 | 2015-12-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/conversion_rates | day         | 3        | 2015-12-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits           | week        | 3332155  | 2015-12-03 | 2015-12-14 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits_unique    | week        | 2860608  | 2015-12-03 | 2015-12-14 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/revenue          | week        | 32812324 | 2015-12-03 | 2015-12-14 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/conversion_rates | week        | 3        | 2015-12-03 | 2015-12-14 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits           | month       | 3218303  | 2015-10-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits_unique    | month       | 2767266  | 2015-10-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/revenue          | month       | 31938030 | 2015-10-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/conversion_rates | month       | 3        | 2015-10-03 | 2015-12-03 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Response contains <count> values
    And Body contains entity with attribute "since" value "<expected_since>"
    And Body contains entity with attribute "until" value "<expected_until>"
    And Body contains entity with attribute "granularity" value "<granularity>"

    Examples: 
      | url                         | granularity | count | since      | until      | expected_since | expected_until | property                             | content_type     | response_code | data_owner       |
      | /analytics/visits           | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07     | 2015-12-07     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07     | 2015-12-07     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07     | 2015-12-07     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07     | 2015-12-07     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits           | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits           | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits           | day         | 366   | 2014-01-01 | 2016-01-14 | 2015-01-14     | 2016-01-14     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | day         | 366   | 2014-01-01 | 2016-01-14 | 2015-01-14     | 2016-01-14     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | day         | 366   | 2014-01-01 | 2016-01-14 | 2015-01-14     | 2016-01-14     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | day         | 366   | 2014-01-01 | 2016-01-14 | 2015-01-14     | 2016-01-14     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits           | week        | 0     | 2015-11-07 | 2015-11-13 | 2015-11-07     | 2015-11-13     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | week        | 0     | 2015-11-07 | 2015-11-13 | 2015-11-07     | 2015-11-13     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | week        | 0     | 2015-11-07 | 2015-11-13 | 2015-11-07     | 2015-11-13     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | week        | 0     | 2015-11-07 | 2015-11-13 | 2015-11-07     | 2015-11-13     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits           | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | week        | 1     | 2015-11-01 | 2015-11-13 | 2015-11-02     | 2015-11-08     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits           | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-11-09     | 2015-11-22     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-11-09     | 2015-11-22     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-11-09     | 2015-11-22     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-11-09     | 2015-11-22     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits           | week        | 51    | 2014-01-01 | 2016-01-14 | 2015-01-19     | 2016-01-10     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | week        | 51    | 2014-01-01 | 2016-01-14 | 2015-01-19     | 2016-01-10     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | week        | 51    | 2014-01-01 | 2016-01-14 | 2015-01-19     | 2016-01-10     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | week        | 51    | 2014-01-01 | 2016-01-14 | 2015-01-19     | 2016-01-10     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits           | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits           | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits           | month       | 11    | 2014-01-01 | 2016-01-14 | 2015-02-01     | 2015-12-31     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/visits_unique    | month       | 11    | 2014-01-01 | 2016-01-14 | 2015-02-01     | 2015-12-31     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/revenue          | month       | 11    | 2014-01-01 | 2016-01-14 | 2015-02-01     | 2015-12-31     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |
      | /analytics/conversion_rates | month       | 11    | 2014-01-01 | 2016-01-14 | 2015-02-01     | 2015-12-31     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | Google Analytics |

  Scenario Outline: Getting non-existent analytics data
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property<" since "<since>" until "<until>"
    Then Response code is <response_code>

    Examples: 
      | url                           | granularity | property                             | since      | until      | response_code |
      | /analytics/visits/not_present | day         | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | 404           |

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
      | url                                   | granularity | since            | until      | count | property                             | response_code | content_type     |
      | /analytics/visits                     |             | 2015-12-03       | 2015-12-03 | 1     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique              | day         |                  | 2015-12-03 | 31    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/revenue                    | day         | today - 12 weeks |            | 85    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates           | day         |                  |            | 31    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits/countries           |             |                  |            | 50    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits_unique/countries    | day         | 2015-12-03       |            | 50    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/conversion_rates/countries | day         |                  |            | 50    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/referrals                  |             |                  |            | 50    | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |

  Scenario Outline: Get analytics data from API from 1800s
    When Get web_performance "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Body contains entity with attribute "since" value "<since>"
    And Body contains entity with attribute "until" value "<until>"

    Examples: 
      | url                         | granularity | since      | until      | property                             | since      | until      | content_type     | response_code |
      | /analytics                  | month       | 1888-09-01 | 1890-10-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           |
      | /analytics/visits           | month       | 1888-09-01 | 1890-10-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           |
      | /analytics/visits_unique    | month       | 1888-09-01 | 1890-10-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           |
      | /analytics/revenue          | month       | 1888-09-01 | 1890-10-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           |
      | /analytics/conversion_rates | month       | 1888-09-01 | 1890-10-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           |
      | /analytics                  | day         | 1888-09-01 | 1888-09-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           |
      | /analytics/visits           | day         | 1888-09-01 | 1888-09-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           |
      | /analytics/visits_unique    | day         | 1888-09-01 | 1888-09-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           |
      | /analytics/revenue          | day         | 1888-09-01 | 1888-09-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           |
      | /analytics/conversion_rates | day         | 1888-09-01 | 1888-09-01 | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 2015-12-03 | 2015-12-03 | application/json | 200           |

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

    #And Response contains correct number of values for granularity "<granularity>" between "<expected_since>" and "<expected_until>"
    Examples: 
      | url                         | granularity | since          | until             | expected_granularity | expected_since    | expected_until | property                             | response_code | content_type     |
      | /analytics/visits           |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | day         |                | today             | day                  | today - 1 month   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | day         | today          |                   | day                  | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | week        |                | today             | week                 | today - 13 weeks  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | week        | today          |                   | week                 | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | month       |                | today             | month                | today - 6 months  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | month       | today          |                   | month                | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
      | /analytics/visits           | month       | today          | today - 40 months | month                | today             | today          | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | 200           | application/json |
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
      | /analytics/visits           | day         | today - 1 day  | today | 0     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/visits_unique    | day         | today - 6 days | today | 2     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/revenue          | day         | today - 7 days | today | 3     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |
      | /analytics/conversion_rates | day         | today - 8 days | today | 4     | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           |

  Scenario Outline: Verifying sorting
    When Get web performance referrals with "day" granularity for "<property>" since "<since>" until "<until>" sorted by "<metric>" "<direction>"
    Then Response code is "200"
    And Content type is "application/json"
    And Values are sorted by "<metric>" in "<direction>"

    Examples: 
      | property                             | since           | until | metric        | direction  |
      | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | today - 80 days | today | visits        | ascending  |
      | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | today - 80 days | today | revenue       | ascending  |
      | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | today - 80 days | today | visits_unique | ascending  |
      | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | today - 80 days | today | visits        | descending |
      | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | today - 80 days | today | revenue       | descending |
      | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | today - 80 days | today | visits_unique | descending |

  Scenario Outline: Checking error codes for sorting
    When Get web performance referrals with "day" granularity for "54db88d7-0b3d-4c27-b877-087d9071f5b6" since "today - 80 days" until "today" sorted by "<metric>" "<direction>"
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
      | url        | granularity | since            | until | property                             | content_type     | response_code | json_argument                 |
      | /analytics | day         | today - 3 months | today | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | top_values.country_conversion |
      | /analytics | day         | today - 30 days  | today | 54db88d7-0b3d-4c27-b877-087d9071f5b6 | application/json | 200           | top_values.country_visits     |
