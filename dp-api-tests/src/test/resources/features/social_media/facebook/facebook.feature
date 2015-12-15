Feature: facebook
  Testing of api for facebook with mock data in db - testing property id is "99999999-9999-4999-a999-999999999999"
  data in db are increasing for all metrics, inserted to db according to following pattern:
  starting from d=2014-01-01 as i=0, i++
  INSERT INTO `DP_SOCIAL_MEDIA`.`FactFacebookPageStats` (`dim_property_id`, `dim_date_id`, `impressions`, `engagements`, `followers`, `number_of_posts`, `reach`, `likes`, `unlikes`, `collected_time_stamp`, `inserted_time_stamp` )
  VALUES ( VALUES (999999, ${d.format("yyyyMMdd")},  ${i*3},  ${i},  ${i*10},  ${i+100}, ${i*5},  ${i*2}, ${i},  CURRENT_TIMESTAMP,   '${d.format("yyyy-MM-dd HH:mm:ss")}' );

  Scenario Outline: Get facebook analytics data from API for a given wrong granularity
    When Getting "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples: 
      | url                                              | granularity | property                             | since      | until      | content_type     | response_code | custom_code |
      | /social_media/analytics/facebook/                | ddd         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /social_media/analytics/facebook/number_of_posts | www         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /social_media/analytics/facebook/engagement      | yyy         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /social_media/analytics/facebook/likes           | MONTHs      | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /social_media/analytics/facebook/unlikes         | we3EK       | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /social_media/analytics/facebook/reach           | D@YS        | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |

  Scenario Outline: Validate that metrics have valid value in the db
    When Getting "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And The metric count is <count>

    Examples: 
      | url                                              | granularity | count | since      | until      | property                             | content_type     | response_code |
      | /social_media/analytics/facebook/number_of_posts | day         | 801   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/engagement      | day         | 701   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/likes           | day         | 1402  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/unlikes         | day         | 701   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/reach           | day         | 3505  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/number_of_posts | week        | 804   | 2015-12-02 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/engagement      | week        | 704   | 2015-12-02 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/likes           | week        | 1408  | 2015-12-02 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/unlikes         | week        | 704   | 2015-12-02 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/reach           | week        | 3520  | 2015-12-02 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/number_of_posts | month       | 798   | 2015-11-02 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/engagement      | month       | 698   | 2015-11-02 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/likes           | month       | 1396  | 2015-11-02 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/unlikes         | month       | 698   | 2015-11-02 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/reach           | month       | 3490  | 2015-11-02 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Getting "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Response contains <count> values
    And Body contains entity with attribute "since" value "<calculated_statistics_real_since>"
    And Body contains entity with attribute "until" value "<until>"
    And Body contains entity with attribute "granularity" value "<granularity>"

    Examples: 
      | url                                              | granularity | count | since      | until      | calculated_statistics_real_since | property                             | content_type     | response_code | data_owner |
      | /social_media/analytics/facebook/number_of_posts | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/engagement      | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/likes           | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/unlikes         | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/reach           | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/followers       | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/number_of_posts | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/engagement      | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/likes           | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/unlikes         | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/reach           | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/followers       | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/number_of_posts | day         | 92    | 2015-06-07 | 2015-12-07 | 2015-09-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/engagement      | day         | 92    | 2015-06-07 | 2015-12-07 | 2015-09-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/likes           | day         | 92    | 2015-06-07 | 2015-12-07 | 2015-09-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/unlikes         | day         | 92    | 2015-06-07 | 2015-12-07 | 2015-09-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/reach           | day         | 92    | 2015-06-07 | 2015-12-07 | 2015-09-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/followers       | day         | 92    | 2015-06-07 | 2015-12-07 | 2015-09-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/number_of_posts | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/engagement      | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/likes           | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/unlikes         | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/reach           | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/followers       | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/number_of_posts | week        | 1     | 2015-11-09 | 2015-11-15 | 2015-11-09                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/engagement      | week        | 1     | 2015-11-09 | 2015-11-15 | 2015-11-09                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/likes           | week        | 1     | 2015-11-09 | 2015-11-15 | 2015-11-09                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/unlikes         | week        | 1     | 2015-11-09 | 2015-11-15 | 2015-11-09                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/reach           | week        | 1     | 2015-11-09 | 2015-11-15 | 2015-11-09                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/followers       | week        | 1     | 2015-11-09 | 2015-11-15 | 2015-11-09                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/number_of_posts | week        | 3     | 2015-11-07 | 2015-11-23 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/engagement      | week        | 3     | 2015-11-07 | 2015-11-23 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/likes           | week        | 3     | 2015-11-07 | 2015-11-23 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/unlikes         | week        | 3     | 2015-11-07 | 2015-11-23 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/reach           | week        | 3     | 2015-11-07 | 2015-11-23 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/followers       | week        | 3     | 2015-11-07 | 2015-11-23 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/number_of_posts | week        | 26    | 2015-01-07 | 2015-11-23 | 2015-05-25                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/engagement      | week        | 26    | 2015-01-07 | 2015-11-23 | 2015-05-25                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/likes           | week        | 26    | 2015-01-07 | 2015-11-23 | 2015-05-25                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/unlikes         | week        | 26    | 2015-01-07 | 2015-11-23 | 2015-05-25                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/reach           | week        | 26    | 2015-01-07 | 2015-11-23 | 2015-05-25                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/followers       | week        | 26    | 2015-01-07 | 2015-11-23 | 2015-05-25                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/number_of_posts | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/engagement      | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/likes           | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/unlikes         | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/reach           | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/followers       | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/number_of_posts | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/engagement      | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/likes           | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/unlikes         | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/reach           | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/followers       | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/number_of_posts | month       | 23    | 2013-02-01 | 2015-11-30 | 2013-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/engagement      | month       | 23    | 2013-02-01 | 2015-11-30 | 2013-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/likes           | month       | 23    | 2013-02-01 | 2015-11-30 | 2013-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/unlikes         | month       | 23    | 2013-02-01 | 2015-11-30 | 2013-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/reach           | month       | 23    | 2013-02-01 | 2015-11-30 | 2013-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |
      | /social_media/analytics/facebook/followers       | month       | 23    | 2013-02-01 | 2015-11-30 | 2013-02-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | facebook   |

  Scenario Outline: Getting non-existent analytics data
    When Getting "<url>" data with "<granularity<" granularity for "<property<" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples: 
      | url                                          | granularity | property                             | since      | until      | content_type     | response_code | custom_code |
      | /social_media/analytics/facebook/not_present | day         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 404           | 151         |

  Scenario Outline: Getting mismatched metrics analytics data
    When Getting "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples: 
      | url                                     | granularity | property                             | since      | until      | content_type     | response_code | custom_code |
      | /social_media/analytics/facebook/tweets | day         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 404           | 151         |

  Scenario Outline: Checking error codes for analytics data
    When Property is missing for "<url>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                                              | response_code | custom_code | content_type     |
      | /social_media/analytics/facebook                 | 400           | 52          | application/json |
      | /social_media/analytics/facebook/number_of_posts | 400           | 52          | application/json |
      | /social_media/analytics/facebook/engagement      | 400           | 52          | application/json |
      | /social_media/analytics/facebook/likes           | 400           | 52          | application/json |
      | /social_media/analytics/facebook/unlikes         | 400           | 52          | application/json |
      | /social_media/analytics/facebook/reach           | 400           | 52          | application/json |
      | /social_media/analytics/facebook/followers       | 400           | 52          | application/json |

  Scenario Outline: Get analytics data from API with missing parameters
    When Getting "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response contains <count> values

    Examples: 
      | url                                              | granularity | since      | until      | count | property                            | response_code | content_type     |
      | /social_media/analytics/facebook/number_of_posts |             | 2015-12-03 | 2015-12-03 | 1     | 99999999-9999-4999-a999-99999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | day         |            | 2015-12-03 | 31    | 99999999-9999-4999-a999-99999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | day         | 2015-11-03 |            | 31    | 99999999-9999-4999-a999-99999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | day         |            |            | 31    | 99999999-9999-4999-a999-99999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           |             |            |            | 31    | 99999999-9999-4999-a999-99999999999 | 200           | application/json |

  Scenario Outline: Get analytics data from API from 1800s
    When Getting "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "since" value "since>"
    And Body contains entity with attribute "until" value "<until>"

    Examples: 
      | url                                              | granularity | start_date | end_date   | property                            | since      | until      | content_type     | response_code | custom_code |
      | /social_media/analytics/facebook                 | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/number_of_posts | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/engagement      | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/likes           | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/unlikes         | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/reach           | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/followers       | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook                 | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/number_of_posts | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/engagement      | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/likes           | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/unlikes         | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/reach           | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |
      | /social_media/analytics/facebook/followers       | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 51          |

  Scenario Outline: Checking default parameter values
    Empty column in examples section means default value will be used for this parameter.
    if text is empty, returns null
    if text is date in ISO format (2015-01-01), it returns this date
    text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which will add or substract
    particular number of days/weeks/months from first part of expression

    When Getting "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Response contains correct number of values for granularity "<granularity>" between "<expected_since>" and "<expected_until>"

    Examples: 
      | url                                              | granularity | since             | until          | expected_granularity | expected_since    | expected_until | property                             | response_code | content_type     |
      | /social_media/analytics/facebook/number_of_posts |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/number_of_posts | day         | today - 3 days    | today - 2 days | day                  | today - 3 days    | today - 2 days | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/engagement      | day         | today - 3 days    | today - 2 days | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/likes           | day         | today - 3 days    | today - 2 days | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/unlikes         | day         | today - 3 days    | today - 2 days | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/reach           | day         | today - 3 days    | today - 2 days | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /social_media/analytics/facebook/followers       | day         | today - 3 days    | today - 2 days | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |

  Scenario Outline: Checking number of values in response for various granularities
    When Getting "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Response contains <count> values

    Examples: 
      | url                                              | granularity | since           | until | count | property                             | content_type     | response_code |
      | /social_media/analytics/facebook/number_of_posts | day         | today - 1 day   | today | 2     | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/engagement      | day         | today - 6 days  | today | 7     | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/likes           | day         | today - 7 days  | today | 8     | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/unlikes         | day         | today - 8 days  | today | 9     | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/reach           | day         | today - 29 days | today | 30    | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/followers       | day         | today - 30 days | today | 31    | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /social_media/analytics/facebook/likes           | week        | today           | today | 0     | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
