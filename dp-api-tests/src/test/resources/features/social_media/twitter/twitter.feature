@SocialMedia
Feature: Twitter metrics
  Testing of api for twitter with mock data in db - testing property id is "99000099-9999-4999-a999-999999999999"

  Scenario Outline: Get twitter analytics data from API for a given wrong granularity
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "today" until "today"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is "63"

    Examples:
      | url                                 | granularity | property                             |
      | /analytics/twitter                  | dd          | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/number_of_tweets | mm          | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | yy          | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | 1dd         | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | m1n         | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | 444         | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | 6655665     | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | MONTH1      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | DAY3        | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | WEEKs1      | 99000099-9999-4999-a999-999999999999 |

  @Smoke
  Scenario Outline: Validate that metrics have valid value in the db
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And The metric count is <count>
    And Body contains entity with attribute "granularity" value "<granularity>"

    Examples:
      | url                                 | granularity | count | since      | until      | property                             |
      | /analytics/twitter/number_of_tweets | day         | 7697  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | day         | 7745  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | day         | 7462  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | day         | 7506  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | day         | 7167  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | day         | 7521  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | day         | 7394  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | day         | 7487  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | day         | 7741  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |

      | /analytics/twitter/number_of_tweets | week        | 7924  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | week        | 7944  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | week        | 7668  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | week        | 7719  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | week        | 7359  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | week        | 7782  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | week        | 7627  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | week        | 7737  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | week        | 7958  | 2015-12-05 | 2015-12-14 | 99000099-9999-4999-a999-999999999999 |

      | /analytics/twitter/number_of_tweets | month       | 7623  | 2015-11-01 | 2015-12-08 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | month       | 7674  | 2015-11-01 | 2015-12-08 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | month       | 7387  | 2015-11-01 | 2015-12-08 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | month       | 7411  | 2015-11-01 | 2015-12-08 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | month       | 7073  | 2015-11-01 | 2015-12-08 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | month       | 7451  | 2015-11-01 | 2015-12-08 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | month       | 7301  | 2015-11-01 | 2015-12-08 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | month       | 7431  | 2015-11-01 | 2015-12-08 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | month       | 7672  | 2015-11-01 | 2015-12-08 | 99000099-9999-4999-a999-999999999999 |

  @Smoke
  Scenario Outline: Validate that metrics have valid value in the db for all metrics
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains "<count>" values of global stats dto

    Examples:
      | url                | granularity | count                                                         | since      | until      | property                             |
      | /analytics/twitter | day         | 10748, 10477, 10212, 10265, 9993, 10279, 10114, 10315, 10437  | 2016-04-07 | 2016-04-07 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter | week        | 10797, 10531, 10293, 10338, 10051, 10304, 10153, 10397, 10519 | 2016-04-04 | 2016-04-10 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter | month       | 11242, 11013, 10748, 10826, 10510, 10773, 10530, 10867, 10885 | 2016-04-01 | 2016-04-30 | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Get specific analytics data from API for a given granularity for overall twitter metrics
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Data is owned by "twitter"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> values for all metrics

    Examples:
      | url                | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics/twitter | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |


  Scenario Outline: Get specific analytics data from API for a given granularity for particular twitter metric
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Data is owned by "twitter"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> values

    Examples:
      | url                                 | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics/twitter/number_of_tweets | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/number_of_tweets | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/number_of_tweets | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/number_of_tweets | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/number_of_tweets | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/number_of_tweets | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/number_of_tweets | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/number_of_tweets | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/number_of_tweets | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/twitter/engagement       | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/twitter/followers        | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/twitter/impressions      | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/twitter/reach            | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/twitter/retweets         | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/twitter/retweet_reach    | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/twitter/mentions         | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/twitter/mention_reach    | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |


  Scenario Outline: Getting non-existent analytics data
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 404
    And Body is empty

    Examples:
      | url                            | granularity | property                             | since      | until      |
      | /analytics/twitter/not_present | day         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |

  Scenario Outline: Getting miss mached non-existent analytics data
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 404
    And Body is empty

    Examples:
      | url                      | granularity | property                             | since      | until      |
      | /analytics/twitter/posts | day         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |

  Scenario Outline: Checking error codes for analytics data
    When Get twitter "<url>" with missing property header
    Then Response code is 400
    And Custom code is "52"

    Examples:
      | url                                 |
      | /analytics/twitter                  |
      | /analytics/twitter/number_of_tweets |
      | /analytics/twitter/engagement       |
      | /analytics/twitter/followers        |
      | /analytics/twitter/impressions      |
      | /analytics/twitter/reach            |
      | /analytics/twitter/retweets         |
      | /analytics/twitter/retweet_reach    |
      | /analytics/twitter/mentions         |
      | /analytics/twitter/mention_reach    |

  Scenario Outline: Get analytics data from API with missing parameters
    When Get twitter "<url>" data with "<granularity>" granularity for "99000099-9999-4999-a999-999999999999" since "<since>" until "<until>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is 52

    Examples:
      | url                                 | granularity | since           | until      |
      | /analytics/twitter/number_of_tweets |             | 2015-12-03      | 2015-12-03 |
      | /analytics/twitter/engagement       | week        | 2015-12-03      |            |
      | /analytics/twitter/followers        | month       |                 | today      |
      | /analytics/twitter/impressions      | day         | today - 1 month |            |


  Scenario Outline: Get analytics data from API from 1800s
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains 0 values

    Examples:
      | url                                 | granularity | since      | until      | property                             |
      | /analytics/twitter/number_of_tweets | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | day         | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | day         | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Get twitter analytics data from API from 1800s
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains 0 values for all metrics

    Examples:
      | url                | granularity | since      | until      | property                             |
      | /analytics/twitter | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Get analytics data with wrong time interval
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is 63

    Examples:
      | url                                 | granularity | since | until            | property                             |
      | /analytics/twitter/number_of_tweets | day         | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/engagement       | week        | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/followers        | month       | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/impressions      | day         | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/reach            | week        | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweets         | month       | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/retweet_reach    | day         | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mentions         | week        | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
      | /analytics/twitter/mention_reach    | month       | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Get twitter analytics data with wrong time interval
    When Get twitter "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is 63

    Examples:
      | url                | granularity | since | until            | property                             |
      | /analytics/twitter | day         | today | today - 3 months | 99000099-9999-4999-a999-999999999999 |
