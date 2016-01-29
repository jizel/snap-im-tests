Feature: review travelers
  Testing of api for review modul alias trip_advisor with mock data in db - testing property id is "99000199-9999-4999-a999-999999999999"
  data in db are mostly increasing some of the data also includes nulls

  Scenario Outline: Get specific analytics data from API for a given granularity for travelers overall bubble rating
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body contains entity with attribute "since" value "<real_since>"
    And Body contains entity with attribute "until" value "<real_until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> number of analytics for travelers overall bubble rating

    Examples:
      | url                               | granularity | count | since      | until      | real_since | real_until | property                             | content_type     | response_code | data_owner  |
      #min value
      | /travellers/overall_bubble_rating | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/overall_bubble_rating | day         | 123   | 2015-08-03 | 2015-12-03 | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      # try to return more than max
      #missing
      #---weeks---
      #min value
      | /travellers/overall_bubble_rating | week        | 1     | 2015-11-18 | 2015-12-03 | 2015-11-23 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/overall_bubble_rating | week        | 42    | 2015-02-05 | 2015-12-03 | 2015-02-09 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      # try to return more than max
      #missing
      #---month---
      #min value
      | /travellers/overall_bubble_rating | month       | 1     | 2015-10-18 | 2015-12-03 | 2015-11-01 | 2015-11-30 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/overall_bubble_rating | month       | 12    | 2014-11-05 | 2016-01-31 | 2015-02-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      # try to return more than max

  Scenario Outline: Get specific analytics data from API for a given granularity for aspect of business
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body contains entity with attribute "since" value "<real_since>"
    And Body contains entity with attribute "until" value "<real_until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> number of analytics for travelers for aspect of business

    Examples:
      | url                             | granularity | count | since      | until      | real_since | real_until | property                             | content_type     | response_code | data_owner  |
      | /travellers/aspects_of_business | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business | day         | 123   | 2015-08-03 | 2015-12-03 | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business | week        | 1     | 2015-11-18 | 2015-12-03 | 2015-11-23 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business | week        | 42    | 2015-02-05 | 2015-12-03 | 2015-02-09 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business | month       | 1     | 2015-10-18 | 2015-12-03 | 2015-11-01 | 2015-11-30 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business | month       | 12    | 2014-11-05 | 2016-01-31 | 2015-02-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |


  Scenario Outline: Get specific analytics data from API for a given granularity for travelers number of reviews
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body contains entity with attribute "since" value "<real_since>"
    And Body contains entity with attribute "until" value "<real_until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> number of analytics for travelers number of reviews

    Examples:
      | url                           | granularity | count | since      | until      | real_since | real_until | property                             | content_type     | response_code | data_owner  |
      | /travellers/number_of_reviews | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews | day         | 123   | 2015-08-03 | 2015-12-03 | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews | week        | 1     | 2015-11-18 | 2015-12-03 | 2015-11-23 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews | week        | 42    | 2015-02-05 | 2015-12-03 | 2015-02-09 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews | month       | 1     | 2015-10-18 | 2015-12-03 | 2015-11-01 | 2015-11-30 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews | month       | 12    | 2014-11-05 | 2016-01-31 | 2015-02-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |


  Scenario Outline: Get specific analytics data from API for a given granularity of more complex endpoints
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body contains entity with attribute "since" value "<real_since>"
    And Body contains entity with attribute "until" value "<real_until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> number of analytics for travelers

    Examples:
      | url          | granularity | count | since      | until      | real_since | real_until | property                             | content_type     | response_code | data_owner  |
      | /travellers/ | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/ | day         | 123   | 2015-08-03 | 2015-12-03 | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/ | week        | 1     | 2015-11-18 | 2015-12-03 | 2015-11-23 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/ | week        | 42    | 2015-02-05 | 2015-12-03 | 2015-02-09 | 2015-11-29 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/ | month       | 1     | 2015-10-18 | 2015-12-03 | 2015-11-01 | 2015-11-30 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/ | month       | 12    | 2014-11-05 | 2016-01-31 | 2015-02-01 | 2016-01-31 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |


  Scenario Outline: Checking data corectness for analitics
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property_id>" since "<since>" until "<until>"
    Then Review travellers file "<json_input_file>" is equals to previous response
    And Response code is "200"
    And Content type is "application/json"

    Examples:
      | json_input_file           | url          | property_id                          | granularity | since      | until      |
      | /analytics_for_day.json   | /travellers/ | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | /analytics_for_week.json  | /travellers/ | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | /analytics_for_month.json | /travellers/ | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Checking data corectness for bubble rating
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property_id>" since "<since>" until "<until>"
    Then Review travellers file "<json_input_file>" is equals to previous response for bubble rating
    And Response code is "200"
    And Content type is "application/json"

    Examples:
      | json_input_file        | url                               | property_id                          | granularity | since      | until      |
      | /bubble_for_day.json   | /travellers/overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | /bubble_for_week.json  | /travellers/overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | /bubble_for_month.json | /travellers/overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Checking data corectness for aspects of business
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property_id>" since "<since>" until "<until>"
    Then Review travellers file "<json_input_file>" is equals to previous response for acpects of business
    And Response code is "200"
    And Content type is "application/json"

    Examples:
      | json_input_file                     | url                             | property_id                          | granularity | since      | until      |
      | /aspects_of_business_for_day.json   | /travellers/aspects_of_business | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | /aspects_of_business_for_week.json  | /travellers/aspects_of_business | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | /aspects_of_business_for_month.json | /travellers/aspects_of_business | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Checking data corectness for aspects of business
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property_id>" since "<since>" until "<until>"
    Then Review travellers file "<json_input_file>" is equals to previous response for number of reviews
    And Response code is "200"
    And Content type is "application/json"

    Examples:
      | json_input_file                   | url                           | property_id                          | granularity | since      | until      |
      | /number_of_reviews_for_day.json   | /travellers/number_of_reviews | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | /number_of_reviews_for_week.json  | /travellers/number_of_reviews | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | /number_of_reviews_for_month.json | /travellers/number_of_reviews | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |


  Scenario Outline: Get analytics data from TA API that are more than year old
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body does not contain property with attribute "data"

    Examples:
      | url                               | granularity | since      | until      | property                             | content_type     | response_code | data_owner  |
      | /travellers/overall_bubble_rating | day         | 1880-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews     | day         | 1880-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business   | day         | 1880-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /travellers/overall_bubble_rating | week        | 1880-05-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews     | week        | 1880-05-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business   | week        | 1880-05-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /travellers/overall_bubble_rating | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews     | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business   | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |


  Scenario Outline: Get specific analytics data from TA API that are more than year old
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body does not contain property with attribute "data"

    Examples:
      | url          | granularity | since      | until      | property                             | content_type     | response_code | data_owner  |
      | /travellers/ | day         | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/ | week        | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/ | month       | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |


  Scenario Outline: Get analytics data from TA API that has wrong time interval
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body does not contain property with attribute "data"

    Examples:
      | url                               | granularity | until      | since      | property                             | content_type     | response_code | data_owner  |
      | /travellers/overall_bubble_rating | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews     | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business   | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /travellers/overall_bubble_rating | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews     | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business   | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

      | /travellers/overall_bubble_rating | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/number_of_reviews     | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/aspects_of_business   | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

  Scenario Outline: Get specific analytics data from TA API has wrong time interval
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Data is owned by "<data_owner>"
    And Content type is "<content_type>"
    And Body does not contain property with attribute "data"

    Examples:
      | url          | granularity | until      | since      | property                             | content_type     | response_code | data_owner  |
      | /travellers/ | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/ | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |
      | /travellers/ | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 | application/json | 200           | tripadvisor |

  #todo
  #granularity (date values) new rules when finished add tests




