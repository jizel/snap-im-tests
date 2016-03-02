Feature: facebook
  Testing of api for facebook with mock data in db - testing property id is "99999999-9999-4999-a999-999999999999"
  data in db are increasing for all metrics, inserted to db according to following pattern:
  starting from d=2014-01-01 as i=0, i++
  INSERT INTO `DP_SOCIAL_MEDIA`.`FactFacebookPageStats` (`dim_property_id`, `dim_date_id`, `impressions`, `engagements`, `followers`, `number_of_posts`, `reach`, `likes`, `unlikes`, `collected_time_stamp`, `inserted_time_stamp` )
  VALUES ( VALUES (999999, ${d.format("yyyyMMdd")},  ${i*3},  ${i},  ${i*10},  ${i+100}, ${i*5},  ${i*2}, ${i},  CURRENT_TIMESTAMP,   '${d.format("yyyy-MM-dd HH:mm:ss")}' );

  Scenario Outline: Get facebook analytics data from API for a given wrong granularity
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is 400
    And Custom code is "63"

    Examples:
      | url                                 | granularity | property                              | since       | until       |
      | /analytics/facebook/                | ddd         | 99999999-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/                | ddd         | 99999999-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12     |
      | /analytics/facebook/                | ddd         | 99999999-9999-4999-a999-999999999999  | 2015-12     | 2015-12-03  |
      | /analytics/facebook/                | ddd         | 99999999-9999-4999-a999-99999999999#  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/number_of_posts | www         | 99999999-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/engagement      | yyy         | 99999999-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/likes           | MONTHs      | 99999999-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/unlikes         | we3EK       | 99999999-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/reach           | D@YS        | 99999999-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/followers       | M0nth       | 99999999-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/reach           | week        | ?99999999-9999-4999-a999-999999999999 | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/reach           | week        | 99999999-9999-4999-a999-999999999999  | 2015-12-03  | -2015-12-03 |
      | /analytics/facebook/reach           | week        | 99999999-9999-4999-a999-999999999999  | -2015-12-03 | 2015-12-03  |

  Scenario Outline: Validate that metrics have valid value in the db
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is 200
    And The metric count is <count>

    Examples:
      | url                                 | granularity | count | since      | until      | property                             |
      | /analytics/facebook/number_of_posts | day         | 801   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | day         | 701   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | day         | 1402  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | day         | 701   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | day         | 3505  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | day         | 7010  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | week        | 811   | 2015-12-05 | 2015-12-14 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | 711   | 2015-12-06 | 2015-12-14 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | week        | 1422  | 2015-12-06 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | week        | 711   | 2015-12-07 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | week        | 3555  | 2015-12-07 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | week        | 7110  | 2015-12-07 | 2015-12-13 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | month       | 798   | 2015-10-30 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | month       | 698   | 2015-10-31 | 2015-12-01 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | 1396  | 2015-11-01 | 2015-12-01 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | 698   | 2015-11-01 | 2015-11-30 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | month       | 3490  | 2015-10-30 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | month       | 6980  | 2015-10-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 |

  Scenario Outline: Get specific analytics data from API for a given granularity and check it`s count
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "facebook"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> values

    Examples:
      | url                                 | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics/facebook/number_of_posts | day         | 1     | today             | today | today             | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | week        | 3     | today - 27 days   | today | today - 27 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | week        | 51    | today - 363 days  | today | today - 363 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | month       | 11    | today - 40 months | today | today - 12 months | today      | 99999999-9999-4999-a999-999999999999 |

      | /analytics/facebook/engagement      | day         | 1     | today             | today | today             | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | 3     | today - 27 days   | today | today - 27 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | 51    | today - 363 days  | today | today - 363 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | month       | 11    | today - 40 months | today | today - 12 months | today      | 99999999-9999-4999-a999-999999999999 |

      | /analytics/facebook/likes           | day         | 1     | today             | today | today             | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | week        | 3     | today - 27 days   | today | today - 27 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | week        | 51    | today - 363 days  | today | today - 363 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | 11    | today - 40 months | today | today - 12 months | today      | 99999999-9999-4999-a999-999999999999 |

      | /analytics/facebook/unlikes         | day         | 1     | today             | today | today             | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | week        | 3     | today - 27 days   | today | today - 27 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | week        | 51    | today - 363 days  | today | today - 363 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | 11    | today - 40 months | today | today - 12 months | today      | 99999999-9999-4999-a999-999999999999 |

      | /analytics/facebook/reach           | day         | 1     | today             | today | today             | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | week        | 3     | today - 27 days   | today | today - 27 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | week        | 51    | today - 363 days  | today | today - 363 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | month       | 11    | today - 40 months | today | today - 12 months | today      | 99999999-9999-4999-a999-999999999999 |

      | /analytics/facebook/followers       | day         | 1     | today             | today | today             | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | week        | 3     | today - 27 days   | today | today - 27 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | week        | 51    | today - 363 days  | today | today - 363 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | month       | 11    | today - 40 months | today | today - 12 months | today      | 99999999-9999-4999-a999-999999999999 |


  Scenario Outline: Get specific analytics data from API for a given granularity and check it`s count for /facebook/
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> values of facebook data

    Examples:
      | url                  | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics/facebook/ | day         | 1     | today             | today | today             | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | week        | 3     | today - 27 days   | today | today - 27 days   | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | week        | 51    | today - 363 days  | today | today - 363 days  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | month       | 11    | today - 40 months | today | today - 12 months | today      | 99999999-9999-4999-a999-999999999999 |

  Scenario Outline: Getting non-existent analytics data
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Body is empty

    Examples:
      | url                             | granularity | property                             | since      | until      | response_code |
      | /analytics/facebook/not_present | day         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | 404           |

  Scenario Outline: Getting mismatched metrics analytics data
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Body is empty

    Examples:
      | url                        | granularity | property                             | since      | until      | response_code |
      | /analytics/facebook/tweets | day         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | 404           |

  Scenario Outline: Checking error codes for analytics data
    When Get facebook "<url>" with missing property header
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is "52"

    Examples:
      | url                                 |
      | /analytics/facebook                 |
      | /analytics/facebook/number_of_posts |
      | /analytics/facebook/engagement      |
      | /analytics/facebook/likes           |
      | /analytics/facebook/unlikes         |
      | /analytics/facebook/reach           |
      | /analytics/facebook/followers       |

  Scenario Outline: Get analytics data from API for correct granularity
    When Get facebook "<url>" data with "<granularity>" granularity for "99999999-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<expected_granularity>"

    Examples:
      | url                                 | granularity | expected_granularity | since           | until      |
      | /analytics/facebook                 |             | day                  | 2015-12-03      | 2015-12-03 |
      | /analytics/facebook/number_of_posts | week        | week                 | 2015-12-03      |            |
      | /analytics/facebook/engagement      | month       | month                |                 | today      |
      | /analytics/facebook/likes           | day         | day                  | today - 1 month |            |


  Scenario Outline: Get analytics data from API from 1800s
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<start_date>" until "<end_date>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains 0 values

    Examples:
      | url                                 | granularity | start_date | end_date   | property                             |
      | /analytics/facebook/reach           | week        | 1888-11-01 | 1889-01-01 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | 1888-11-01 | 1889-01-01 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | day         | 1888-11-01 | 1889-01-01 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | 1888-11-01 | 1889-01-01 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | 1888-11-01 | 1889-01-01 | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | day         | 1888-11-01 | 1889-01-01 | 99999999-9999-4999-a999-999999999999 |

  Scenario Outline: Get analytics data from API from 1800s
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<start_date>" until "<end_date>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains 0 values in data enclosure

    Examples:
      | url                  | granularity | start_date | end_date   | property                             |
      | /analytics/facebook/ | day         | 1888-11-01 | 1889-01-01 | 99999999-9999-4999-a999-999999999999 |


  Scenario Outline: Get analytics data with wrong time interval
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains 0 values

    Examples:
      | url                                 | granularity | since | until            | property                             |
      | /analytics/facebook/reach           | week        | today | today - 3 months | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | today | today - 3 months | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | day         | today | today - 3 months | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | today | today - 3 months | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | today | today - 3 months | 99999999-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | day         | today | today - 3 months | 99999999-9999-4999-a999-999999999999 |

  Scenario Outline: Get analytics data with wrong time interval
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains 0 values in data enclosure

    Examples:
      | url                  | granularity | since | until            | property                             |
      | /analytics/facebook/ | day         | today | today - 3 months | 99999999-9999-4999-a999-999999999999 |


