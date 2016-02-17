Feature: review
  Testing of api for review modul alias trip_advisor with mock data in db - testing property id is "99000199-9999-4999-a999-999999999999"
  data in db are mostly increasing some of the data also includes nulls

  Scenario Outline: Get trip advisor analytics data from API for a given wrong granularity
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples:
      | url                                         | granularity | property                             | since      | until      | content_type     | response_code | custom_code |
      | /analytics/                                 | dd          | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/overall_bubble_rating            | yy          | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/number_of_reviews                | mm          | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/aspects_of_business              | 1dd         | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/rating_score                     | MONTHS      | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/popularity_index_rank            | DAYs        | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/travellers/                      | W33K        | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/travellers/overall_bubble_rating | WEEKS       | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/travellers/number_of_reviews     | m0nth       | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/travellers/aspects_of_business   | years       | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |

  Scenario Outline: Checking error codes for analytics data
    When Get trip advisor "<url>" with missing property header
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Custom code is "<custom_code>"

    Examples:
      | url                                         | response_code | custom_code | content_type     |
      | /analytics/                                 | 400           | 52          | application/json |
      | /analytics/overall_bubble_rating            | 400           | 52          | application/json |
      | /analytics/number_of_reviews                | 400           | 52          | application/json |
      | /analytics/aspects_of_business              | 400           | 52          | application/json |
      | /analytics/rating_score                     | 400           | 52          | application/json |
      | /analytics/popularity_index_rank            | 400           | 52          | application/json |
      | /analytics/travellers/                      | 400           | 52          | application/json |
      | /analytics/travellers/overall_bubble_rating | 400           | 52          | application/json |
      | /analytics/travellers/number_of_reviews     | 400           | 52          | application/json |
      | /analytics/travellers/aspects_of_business   | 400           | 52          | application/json |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body contains entity with attribute "since" value "<real_since>"
    And Body contains entity with attribute "until" value "<real_until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> values

    Examples:
      | url                              | granularity | count | since      | until      | real_since | real_until | property                             | content_type     | response_code | data_owner  |
#      #min value
      | /analytics/aspects_of_business   | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /analytics/aspects_of_business   | day         | 123   | 2015-08-03 | 2015-12-03 | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | day         | 123   | 2015-08-03 | 2015-12-03 | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | day         | 123   | 2015-08-03 | 2015-12-03 | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | day         | 123   | 2015-08-03 | 2015-12-03 | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | day         | 123   | 2015-08-03 | 2015-12-03 | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      # try to return more than max
#      | /analytics/aspects_of_business   | day         | 366   | 2014-11-05 | 2016-01-25 | 2015-01-25 | 2016-01-25 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/overall_bubble_rating | day         | 366   | 2014-11-05 | 2016-01-25 | 2015-01-25 | 2016-01-25 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/number_of_reviews     | day         | 366   | 2014-11-05 | 2016-01-25 | 2015-01-25 | 2016-01-25 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/rating_score          | day         | 366   | 2014-11-05 | 2016-01-25 | 2015-01-25 | 2016-01-25 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/popularity_index_rank | day         | 366   | 2014-11-05 | 2016-01-25 | 2015-01-25 | 2016-01-25 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      #---weeks---
      #min value
      | /analytics/aspects_of_business   | week        | 1     | 2015-11-18 | 2015-12-03 | 2015-11-23 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | week        | 1     | 2015-11-18 | 2015-12-03 | 2015-11-23 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | week        | 1     | 2015-11-18 | 2015-12-03 | 2015-11-23 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | week        | 1     | 2015-11-18 | 2015-12-03 | 2015-11-23 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | week        | 1     | 2015-11-18 | 2015-12-03 | 2015-11-23 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /analytics/aspects_of_business   | week        | 42    | 2015-02-05 | 2015-12-03 | 2015-02-09 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | week        | 42    | 2015-02-05 | 2015-12-03 | 2015-02-09 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | week        | 42    | 2015-02-05 | 2015-12-03 | 2015-02-09 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | week        | 42    | 2015-02-05 | 2015-12-03 | 2015-02-09 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | week        | 42    | 2015-02-05 | 2015-12-03 | 2015-02-09 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      # try to return more than max
#      | /analytics/aspects_of_business   | week        | 52    | 2014-11-05 | 2016-01-25 | 2015-01-26 | 2016-01-24 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/overall_bubble_rating | week        | 52    | 2014-11-05 | 2016-01-25 | 2015-01-26 | 2016-01-24 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/number_of_reviews     | week        | 52    | 2014-11-05 | 2016-01-25 | 2015-01-26 | 2016-01-24 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/rating_score          | week        | 52    | 2014-11-05 | 2016-01-25 | 2015-01-26 | 2016-01-24 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/popularity_index_rank | week        | 52    | 2014-11-05 | 2016-01-25 | 2015-01-26 | 2016-01-24 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      #---month---
      #min value
      | /analytics/aspects_of_business   | month       | 1     | 2015-10-18 | 2015-12-03 | 2015-11-01 | 2015-11-30 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | month       | 1     | 2015-10-18 | 2015-12-03 | 2015-11-01 | 2015-11-30 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | month       | 1     | 2015-10-18 | 2015-12-03 | 2015-11-01 | 2015-11-30 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | month       | 1     | 2015-10-18 | 2015-12-03 | 2015-11-01 | 2015-11-30 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | month       | 1     | 2015-10-18 | 2015-12-03 | 2015-11-01 | 2015-11-30 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /analytics/aspects_of_business   | month       | 10    | 2015-03-05 | 2016-02-10 | 2015-04-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | month       | 10    | 2015-03-05 | 2016-02-10 | 2015-04-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | month       | 10    | 2015-03-05 | 2016-02-10 | 2015-04-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | month       | 10    | 2015-03-05 | 2016-02-10 | 2015-04-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | month       | 10    | 2015-03-05 | 2016-02-10 | 2015-04-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      # try to return more than max
#      | /analytics/aspects_of_business   | month       | 12    | 2014-11-05 | 2016-02-10 | 2015-02-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/overall_bubble_rating | month       | 12    | 2014-11-05 | 2016-02-10 | 2015-02-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/number_of_reviews     | month       | 12    | 2014-11-05 | 2016-02-10 | 2015-02-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/rating_score          | month       | 12    | 2014-11-05 | 2016-02-10 | 2015-02-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
#      | /analytics/popularity_index_rank | month       | 12    | 2014-11-05 | 2016-02-10 | 2015-02-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |


  Scenario Outline: Get specific analytics data from API for a given granularity of more complex endpoints
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body contains entity with attribute "since" value "<real_since>"
    And Body contains entity with attribute "until" value "<real_until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> number of analytics

    Examples:
      | url         | granularity | count | since      | until      | real_since | real_until | property                             | content_type     | response_code | data_owner  |
      | /analytics/ | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/ | day         | 123   | 2015-08-03 | 2015-12-03 | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      #| /analytics/ | day         | 366   | today - 40 months | today      | today - 366 days | today      | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/ | week        | 1     | 2015-11-18 | 2015-12-03 | 2015-11-23 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/ | week        | 42    | 2015-02-05 | 2015-12-03 | 2015-02-09 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      #| /analytics/ | week        | 52    | today - 40 months | today      | today - 52 weeks | today      | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/ | month       | 1     | 2015-10-18 | 2015-12-03 | 2015-11-01 | 2015-11-30 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/ | month       | 9     | 2015-03-05 | 2016-01-01 | 2015-04-01 | 2015-12-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      #| /analytics/ | month       | 12    | today - 40 months | today      | today - 1 year   | today      | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |


  Scenario Outline: Checking data corectness for analitics
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property_id>" since "<since>" until "<until>"
    And Response code is "200"
    And Content type is "application/json"
    Then Review file "<json_input_file>" is equals to previous response for analytics

    Examples:
      | json_input_file                     | url         | property_id                          | granularity | since      | until      |
      | /analytics/analytics_for_day.json   | /analytics/ | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | /analytics/analytics_for_week.json  | /analytics/ | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | /analytics/analytics_for_month.json | /analytics/ | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Validate that aspects of business have valid value in the db
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    And Content type is "application/json"
    And Response code is "200"
    And Review file "<json_input_file>" is equals to previous response for aspects of business

    Examples:
      | json_input_file                               | url                            | property                             | granularity | since      | until      |
      | /analytics/aspects_of_business_for_day.json   | /analytics/aspects_of_business | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | /analytics/aspects_of_business_for_week.json  | /analytics/aspects_of_business | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | /analytics/aspects_of_business_for_month.json | /analytics/aspects_of_business | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Validate that overall bubble rating have valid value in the db
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Response of bubble rating contains "<values>" values

    Examples:
      | url                              | granularity | values                    | since      | until      | property                             | content_type     | response_code |
      | /analytics/overall_bubble_rating | day         | 7.4                       | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/overall_bubble_rating | week        | 7.08571, 7.2              | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/overall_bubble_rating | month       | 7.42667, 7.48387, 7.16667 | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Validate that number of reviews have valid value in the db
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Response of number of reviews contains "<values>" values
    Examples:
      | url                          | granularity | values              | since      | until      | property                             | content_type     | response_code |
      | /analytics/number_of_reviews | day         | 37050               | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/number_of_reviews | week        | 37013, 37037        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/number_of_reviews | month       | 36760, 36907, 37050 | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Get analytics data from TA API that are more than year old
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body does not contain property with attribute "values"

    Examples:
      | url                              | granularity | since      | until      | property                             | content_type     | response_code | data_owner  |
      | /analytics/aspects_of_business   | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /analytics/aspects_of_business   | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /analytics/aspects_of_business   | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

  Scenario Outline: Get specific analytics data from TA API that are more than year old
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body does not contain property with attribute "aspects_of_business"
    And Body does not contain property with attribute "overall_bubble_rating"
    And Body does not contain property with attribute "number_of_reviews"
    And Body does not contain property with attribute "rating_score"
    And Body does not contain property with attribute "popularity_index_rank"

    Examples:
      | url         | granularity | since      | until      | property                             | content_type     | response_code | data_owner  |
      | /analytics/ | day         | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/ | week        | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/ | month       | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |


  Scenario Outline: Get analytics data from TA API that has wrong time interval
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body does not contain property with attribute "values"

    Examples:
      | url                              | granularity | until      | since      | property                             | content_type     | response_code | data_owner  |
      | /analytics/aspects_of_business   | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /analytics/aspects_of_business   | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /analytics/aspects_of_business   | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/overall_bubble_rating | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/number_of_reviews     | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/rating_score          | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/popularity_index_rank | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

  Scenario Outline: Get specific analytics data from TA API has wrong time interval
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body does not contain property with attribute "aspects_of_business"
    And Body does not contain property with attribute "overall_bubble_rating"
    And Body does not contain property with attribute "number_of_reviews"
    And Body does not contain property with attribute "rating_score"
    And Body does not contain property with attribute "popularity_index_rank"

    Examples:
      | url         | granularity | until      | since      | property                             | content_type     | response_code | data_owner  |
      | /analytics/ | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/ | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /analytics/ | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

  #todo
  #granularity (date values) new rules when finished add tests




