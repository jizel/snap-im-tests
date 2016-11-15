@SocialMedia
Feature: facebook
  Testing of api for facebook with mock data in db - testing property id is "99000099-9999-4999-a999-999999999999"

  Scenario Outline: Get facebook analytics data from API for a given wrong granularity
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is 400
    And Custom code is "63"

    Examples:
      | url                                 | granularity | property                              | since       | until       |
      | /analytics/facebook/                | ddd         | 99000099-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/                | ddd         | 99000099-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12     |
      | /analytics/facebook/                | ddd         | 99000099-9999-4999-a999-999999999999  | 2015-12     | 2015-12-03  |
      | /analytics/facebook/                | ddd         | 99999999-9999-4999-a999-99999999999#  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/number_of_posts | www         | 99000099-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/engagement      | yyy         | 99000099-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/likes           | MONTHs      | 99000099-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/unlikes         | we3EK       | 99000099-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/reach           | D@YS        | 99000099-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/followers       | M0nth       | 99000099-9999-4999-a999-999999999999  | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/reach           | week        | ?99000099-9999-4999-a999-999999999999 | 2015-12-03  | 2015-12-03  |
      | /analytics/facebook/reach           | week        | 99000099-9999-4999-a999-999999999999  | 2015-12-03  | -2015-12-03 |
      | /analytics/facebook/reach           | week        | 99000099-9999-4999-a999-999999999999  | -2015-12-03 | 2015-12-03  |

  @Smoke
  Scenario Outline: Validate that metrics have valid value in the db
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "application/json"
    And Response code is 200
    And The metric count is <count>

    Examples:
      | url                                 | granularity | count | since      | until      | property                             |
      | /analytics/facebook/number_of_posts | day         | 7488  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | day         | 7397  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | day         | 7548  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | day         | 7829  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | day         | 7710  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | day         | 7517  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | week        | 7709  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | 7664  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | week        | 7745  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | week        | 8033  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | week        | 7905  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | week        | 7773  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | month       | 7411  | 2015-10-30 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | month       | 7358  | 2015-10-30 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | 7481  | 2015-10-30 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | 7751  | 2015-10-30 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | month       | 7635  | 2015-10-30 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | month       | 7441  | 2015-10-30 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |

  @Smoke
  Scenario Outline: Validate that metrics have valid value in the db for all metrics
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains "<count>" values of global stats dto

    Examples:
      | url                 | granularity | count                              | since      | until      | property                             |
      | /analytics/facebook | day         | 7488, 7397, 7548, 7829, 7710, 7517 | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook | week        | 7709, 7664, 7745, 8033, 7905, 7773 | 2015-12-07 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook | month       | 7411, 7358, 7481, 7751, 7635, 7441 | 2015-11-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |

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
      | /analytics/facebook/number_of_posts | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/facebook/engagement      | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/facebook/likes           | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/facebook/unlikes         | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/facebook/reach           | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/reach           | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/facebook/followers       | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | day         | 366   | today - 365 days | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |


  @Smoke
  Scenario Outline: Get specific analytics data from API for a given granularity and check it`s count for /facebook/
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> amount of values for global stats dto

    Examples:
      | url                  | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics/facebook/ | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/ | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Getting non-existent analytics data
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Body is empty

    Examples:
      | url                             | granularity | property                             | since      | until      | response_code |
      | /analytics/facebook/not_present | day         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | 404           |

  Scenario Outline: Getting mismatched metrics analytics data
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Body is empty

    Examples:
      | url                        | granularity | property                             | since      | until      | response_code |
      | /analytics/facebook/tweets | day         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | 404           |

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

#    Make new negative tests - missing granularity, since and until params
  Scenario Outline: Get analytics data from API for correct granularity
    When Get facebook "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<expected_granularity>"

    Examples:
      | url                                 | granularity | expected_granularity | since           | until      |
      | /analytics/facebook                 | day         | day                  | 2015-12-03      | 2015-12-03 |
      | /analytics/facebook/number_of_posts | week        | week                 | 2015-11-03      | 2015-12-03 |
      | /analytics/facebook/engagement      | month       | month                | 2015-11-03      | 2015-12-03 |
      | /analytics/facebook/likes           | day         | day                  | today - 1 month |  today     |


  Scenario Outline: Get analytics data from API from 1800s
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<start_date>" until "<end_date>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains 0 values

    Examples:
      | url                                 | granularity | start_date | end_date   | property                             |
      | /analytics/facebook/reach           | week        | 1888-11-01 | 1889-01-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | 1888-11-01 | 1889-01-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | day         | 1888-11-01 | 1889-01-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | 1888-11-01 | 1889-01-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | 1888-11-01 | 1889-01-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | day         | 1888-11-01 | 1889-01-01 | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Get facebook analytics data from API from 1800s
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<start_date>" until "<end_date>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains 0 values for all metrics

    Examples:
      | url                  | granularity | start_date | end_date   | property                             |
      | /analytics/facebook/ | day         | 1888-11-01 | 1889-01-01 | 99000099-9999-4999-a999-999999999999 |


  Scenario Outline: Get analytics data with wrong time interval
    When Get facebook "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is 63

    Examples:
      | url                                 | granularity | since | until            | property                             |
      | /analytics/facebook/reach           | week        | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/likes           | month       | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/number_of_posts | day         | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/engagement      | week        | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/unlikes         | month       | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook/followers       | day         | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/facebook                 | day         | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |

