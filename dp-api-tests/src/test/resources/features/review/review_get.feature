Feature: review
  Testing of api for review modul alias trip_advisor with mock data in db - testing property id is "99000199-9999-4999-a999-999999999999"
  data in db are mostly increasing some of the data also includes nulls
  #todo granularity (date values) new rules when finished add tests

  Background:
    Given Database is cleaned
    Given The following properties exist with random address and billing address
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      |
      | 99000199-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |

    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |

    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone      | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The password of user "default1" is "Password1"
    Given Relation between user with username "default1" and customer with code "c1t" exists with isPrimary "true"

    Given Get token for user "default1" with password "Password1"
    Given Set access token from session for customer steps defs
    Given Set access token for review steps defs

    Given Relation between user with username "default1" and property with code "p1_code" exists
    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"


  Scenario Outline: Get trip advisor analytics data from API for a given wrong granularity
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is "63"

    Examples:
      | url                                         | granularity | property                             | since      | until      |
      | /analytics/                                 | dd          | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/overall_bubble_rating            | yy          | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/number_of_reviews                | mm          | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/aspects_of_business              | 1dd         | 99000199-9999-4999-a999-999999999999 | 2015-11-03 | 2015-12-03 |
      | /analytics/rating_score                     | MONTHS      | 99000199-9999-4999-a999-999999999999 | 2015-11-03 | 2015-12-03 |
      | /analytics/travellers/                      | W33K        | 99000199-9999-4999-a999-999999999999 | 2015-11-03 | 2015-12-03 |
      | /analytics/travellers/overall_bubble_rating | WEEKS       | 99000199-9999-4999-a999-999999999999 | 2015-10-03 | 2015-12-03 |
      | /analytics/travellers/number_of_reviews     | m0nth       | 99000199-9999-4999-a999-999999999999 | 2015-10-03 | 2015-12-03 |
      | /analytics/travellers/aspects_of_business   | years       | 99000199-9999-4999-a999-999999999999 | 2015-10-03 | 2015-12-03 |

  Scenario Outline: Checking error codes for analytics data
    When Get trip advisor "<url>" with missing property header
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is "52"

    Examples:
      | url                                         |
      | /analytics/                                 |
      | /analytics/overall_bubble_rating            |
      | /analytics/number_of_reviews                |
      | /analytics/aspects_of_business              |
      | /analytics/rating_score                     |
      | /analytics/travellers/                      |
      | /analytics/travellers/overall_bubble_rating |
      | /analytics/travellers/number_of_reviews     |
      | /analytics/travellers/aspects_of_business   |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> values

    Examples:
      | url                              | granularity | count | since             | until | real_since        | real_until | property                             |
      #min value
      | /analytics/aspects_of_business   | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      #average
      | /analytics/aspects_of_business   | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      # try to return more than max
      | /analytics/aspects_of_business   | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      #---weeks--- #min value
      | /analytics/aspects_of_business   | week        | 1     | today - 14 days   | today | today - 14 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | week        | 1     | today - 14 days   | today | today - 14 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | week        | 1     | today - 14 days   | today | today - 14 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | week        | 1     | today - 14 days   | today | today - 14 days   | today      | 99000199-9999-4999-a999-999999999999 |
      # average
      | /analytics/aspects_of_business   | week        | 4     | today - 1 month   | today | today - 1 month   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | week        | 4     | today - 1 month   | today | today - 1 month   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | week        | 4     | today - 1 month   | today | today - 1 month   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | week        | 4     | today - 1 month   | today | today - 1 month   | today      | 99000199-9999-4999-a999-999999999999 |
      # try to return more than max
      | /analytics/aspects_of_business   | week        | 51    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | week        | 51    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | week        | 51    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | week        | 51    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      #---month--- #min value
      | /analytics/aspects_of_business   | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      #average
      | /analytics/aspects_of_business   | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      # try to return more than max
      | /analytics/aspects_of_business   | month       | 11    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | month       | 11    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | month       | 11    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | month       | 11    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get specific analytics data from API for a given granularity of more complex endpoints
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of analytics

    Examples:
      | url         | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics/ | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | week        | 1     | today - 14 days   | today | today - 14 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | week        | 4     | today - 1 month   | today | today - 1 month   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | week        | 51    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | month       | 11    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |


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

#  Scenario Outline: Validate that aspects of business have valid value in the db
#    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
#    And Content type is "application/json"
#    And Response code is "200"
#    And Review file "<json_input_file>" is equals to previous response for aspects of business
#
#    Examples:
#      | json_input_file                                            | url                            | property                             | granularity | since      | until      |
#      | /multiproperty/property/aspects_of_business_for_day.json   | /analytics/aspects_of_business | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
#      | /multiproperty/property/aspects_of_business_for_week.json  | /analytics/aspects_of_business | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
#      | /multiproperty/property/aspects_of_business_for_month.json | /analytics/aspects_of_business | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Validate that overall bubble rating have valid value in the db
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    And Content type is "application/json"
    And Response code is "200"
    And Response of bubble rating contains "<values>" values

    Examples:
      | url                              | granularity | values                    | since      | until      | property                             |
      | /analytics/overall_bubble_rating | day         | 7.4                       | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | week        | 7.08571, 7.2              | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | month       | 7.42667, 7.48387, 7.16667 | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Validate that number of reviews have valid value in the db
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    And Content type is "application/json"
    And Response code is "200"
    And Response of number of reviews contains "<values>" values
    Examples:
      | url                          | granularity | values              | since      | until      | property                             |
      | /analytics/number_of_reviews | day         | 37050               | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews | week        | 37013, 37037        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews | month       | 36760, 36907, 37050 | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |

  Scenario Outline: Get analytics data from TA API that are more than year old
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "values"

    Examples:
      | url                              | granularity | since      | until      | property                             |
      | /analytics/aspects_of_business   | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |

      | /analytics/aspects_of_business   | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |

      | /analytics/aspects_of_business   | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get specific analytics data from TA API that are more than year old
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "aspects_of_business"
    And Body does not contain property with attribute "overall_bubble_rating"
    And Body does not contain property with attribute "number_of_reviews"
    And Body does not contain property with attribute "rating_score"
    And Body does not contain property with attribute "popularity_index_rank"

    Examples:
      | url         | granularity | since      | until      | property                             |
      | /analytics/ | day         | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | week        | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | month       | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get analytics data from TA API that has wrong time interval
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "values"

    Examples:
      | url                              | granularity | until      | since      | property                             |
      | /analytics/aspects_of_business   | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |

      | /analytics/aspects_of_business   | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |

      | /analytics/aspects_of_business   | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/overall_bubble_rating | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/number_of_reviews     | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score          | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get specific analytics data from TA API has wrong time interval
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "aspects_of_business"
    And Body does not contain property with attribute "overall_bubble_rating"
    And Body does not contain property with attribute "number_of_reviews"
    And Body does not contain property with attribute "rating_score"
    And Body does not contain property with attribute "popularity_index_rank"

    Examples:
      | url         | granularity | until      | since      | property                             |
      | /analytics/ | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |

# DONE for new popularity index
#----------------------------------------------------------------------------------------------------------------------
  Scenario Outline: Get analytics data from TA API that are more than year old
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "properties"

    Examples:
      | url                              | granularity | since      | until      | property                             |
      | /analytics/popularity_index_rank | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 |

  Scenario Outline: Validate that popularity_index_rank have valid value in the db
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    And Content type is "application/json"
    And Response code is "200"
    And Review file "<json_input_file>" equals to previous response

    Examples:
      | url                              | granularity | json_input_file                                     | since      | until      | property                             |
#      | /analytics/popularity_index_rank | day         | /multiproperty/property/popularity_index_day.json   | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /analytics/popularity_index_rank | week        | /multiproperty/property/popularity_index_week.json  | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /analytics/popularity_index_rank | month       | /multiproperty/property/popularity_index_month.json | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |

      | /analytics/aspects_of_business | day         | /multiproperty/property/aspects_of_business_day.json   | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/aspects_of_business | week        | /multiproperty/property/aspects_of_business_week.json  | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/aspects_of_business | month       | /multiproperty/property/aspects_of_business_month.json | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |

  Scenario Outline: Get analytics data from TA API that has wrong time interval
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "properties"

    Examples:
      | url                              | granularity | until      | since      | property                             |
      | /analytics/popularity_index_rank | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains 1 properties
    And Response properties contains "<count>" values

    Examples:
      | url                              | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics/popularity_index_rank | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | week        | 1     | today - 14 days   | today | today - 14 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | week        | 4     | today - 1 month   | today | today - 1 month   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | week        | 51    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/popularity_index_rank | month       | 11    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Checking error codes for analytics data asd
    When Get review "<url>" data with "<granularity>" granularity with since "<since>" until "<until>" limit "<limit>" and cursor "<cursor>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is "63"

    Examples:
      | url                              | granularity | since      | until      | limit        | cursor       |
      | /analytics/popularity_index_rank | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | /analytics/popularity_index_rank | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | /analytics/popularity_index_rank | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | /analytics/popularity_index_rank | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | /analytics/popularity_index_rank | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | /analytics/popularity_index_rank | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | /analytics/popularity_index_rank | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | /analytics/popularity_index_rank | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | /analytics/popularity_index_rank | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | /analytics/popularity_index_rank | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |
