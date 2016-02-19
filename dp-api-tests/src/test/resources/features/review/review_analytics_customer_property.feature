Feature: review_multiproperty_customer_property
  #TODO add paging tests - currently paging is only prepared but not implemented
  #TODO request with invalid access token ?

  Background:
    # 5 property, 1 customer, 1 user, with all needed relations
    Given Database is cleaned
    Given The following properties exist with random address and billing address
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      |
      | 99000199-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |
      | 99000299-9999-4999-a999-999999999999 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague |
      | 99000399-9999-4999-a999-999999999999 | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague |

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

    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p2_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p3_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"

    Given Relation between user with username "default1" and property with code "p1_code" exists
    Given Relation between user with username "default1" and property with code "p2_code" exists
    Given Relation between user with username "default1" and property with code "p3_code" exists


#------------
# GET /review/analytics/customer/{customer_id}/popularity_index_rank

  Scenario Outline: Get Review analytics data from API for a given wrong granularity, since, until, limit and cursor
    When Get "<metric>" for list of properties for customer "<customer_code>" with since "<since>" until "<until>" granularity "<granularity>" limit "<limit>" and cursor "<cursor>"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "63"

    Examples:
      | metric                | customer_code | granularity | since      | until      | limit        | cursor       |
      | popularity_index_rank | c1t           | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | popularity_index_rank | c1t           | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | popularity_index_rank | c1t           | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | popularity_index_rank | c1t           | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | popularity_index_rank | c1t           | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | popularity_index_rank | c1t           | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | popularity_index_rank | c1t           | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | popularity_index_rank | c1t           | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | popularity_index_rank | c1t           | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | popularity_index_rank | c1t           | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |

      | aspects_of_business   | c1t           | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | aspects_of_business   | c1t           | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | aspects_of_business   | c1t           | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | aspects_of_business   | c1t           | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | aspects_of_business   | c1t           | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | aspects_of_business   | c1t           | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | aspects_of_business   | c1t           | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | aspects_of_business   | c1t           | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | aspects_of_business   | c1t           | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | aspects_of_business   | c1t           | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |

      | number_of_reviews     | c1t           | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | number_of_reviews     | c1t           | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | number_of_reviews     | c1t           | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | number_of_reviews     | c1t           | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | number_of_reviews     | c1t           | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | number_of_reviews     | c1t           | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | number_of_reviews     | c1t           | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | number_of_reviews     | c1t           | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | number_of_reviews     | c1t           | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | number_of_reviews     | c1t           | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |

      | overall_bubble_rating | c1t           | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | overall_bubble_rating | c1t           | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | overall_bubble_rating | c1t           | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | overall_bubble_rating | c1t           | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | overall_bubble_rating | c1t           | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | overall_bubble_rating | c1t           | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | overall_bubble_rating | c1t           | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | overall_bubble_rating | c1t           | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | overall_bubble_rating | c1t           | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | overall_bubble_rating | c1t           | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |

  Scenario Outline: Checking default values for response
    When Get "<metric>" for list of properties for customer "<customer_code>" with since "/null" until "/null" granularity "/null" limit "/null" and cursor "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "day"
    And Data is owned by "tripadvisor"

    Examples:
      | metric                | customer_code |
      | popularity_index_rank | c1t           |
      | aspects_of_business   | c1t           |
      | number_of_reviews     | c1t           |
      | overall_bubble_rating | c1t           |

  Scenario Outline: Get specific amount of analytics data from API for a given granularity
    When Get "<metric>" for list of properties for customer "<customer_code>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains 3 properties
    And Response properties contains "<count>" values

    Examples:
      | metric                | customer_code | granularity | count | since             | until | real_since        | real_until |
      | popularity_index_rank | c1t           | day         | 1     | today             | today | today             | today      |
      | popularity_index_rank | c1t           | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | popularity_index_rank | c1t           | day         | 366   | today - 40 months | today | today - 365 days  | today      |
      | popularity_index_rank | c1t           | week        | 1     | today - 14 days   | today | today - 14 days   | today      |
      | popularity_index_rank | c1t           | week        | 3     | today - 1 month   | today | today - 1 month   | today      |
      | popularity_index_rank | c1t           | week        | 51    | today - 40 months | today | today - 12 months | today      |
      | popularity_index_rank | c1t           | month       | 1     | today - 2 months  | today | today - 2 months  | today      |
      | popularity_index_rank | c1t           | month       | 3     | today - 4 months  | today | today - 4 months  | today      |
      | popularity_index_rank | c1t           | month       | 11    | today - 40 months | today | today - 12 months | today      |

      | aspects_of_business   | c1t           | day         | 1     | today             | today | today             | today      |
      | aspects_of_business   | c1t           | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | aspects_of_business   | c1t           | day         | 366   | today - 40 months | today | today - 365 days  | today      |
      | aspects_of_business   | c1t           | week        | 1     | today - 14 days   | today | today - 14 days   | today      |
      | aspects_of_business   | c1t           | week        | 3     | today - 1 month   | today | today - 1 month   | today      |
      | aspects_of_business   | c1t           | week        | 51    | today - 40 months | today | today - 12 months | today      |
      | aspects_of_business   | c1t           | month       | 1     | today - 2 months  | today | today - 2 months  | today      |
      | aspects_of_business   | c1t           | month       | 3     | today - 4 months  | today | today - 4 months  | today      |
      | aspects_of_business   | c1t           | month       | 11    | today - 40 months | today | today - 12 months | today      |

      | number_of_reviews     | c1t           | day         | 1     | today             | today | today             | today      |
      | number_of_reviews     | c1t           | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | number_of_reviews     | c1t           | day         | 366   | today - 40 months | today | today - 365 days  | today      |
      | number_of_reviews     | c1t           | week        | 1     | today - 14 days   | today | today - 14 days   | today      |
      | number_of_reviews     | c1t           | week        | 3     | today - 1 month   | today | today - 1 month   | today      |
      | number_of_reviews     | c1t           | week        | 51    | today - 40 months | today | today - 12 months | today      |
      | number_of_reviews     | c1t           | month       | 1     | today - 2 months  | today | today - 2 months  | today      |
      | number_of_reviews     | c1t           | month       | 3     | today - 4 months  | today | today - 4 months  | today      |
      | number_of_reviews     | c1t           | month       | 11    | today - 40 months | today | today - 12 months | today      |

      | overall_bubble_rating | c1t           | day         | 1     | today             | today | today             | today      |
      | overall_bubble_rating | c1t           | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | overall_bubble_rating | c1t           | day         | 366   | today - 40 months | today | today - 365 days  | today      |
      | overall_bubble_rating | c1t           | week        | 1     | today - 14 days   | today | today - 14 days   | today      |
      | overall_bubble_rating | c1t           | week        | 3     | today - 1 month   | today | today - 1 month   | today      |
      | overall_bubble_rating | c1t           | week        | 51    | today - 40 months | today | today - 12 months | today      |
      | overall_bubble_rating | c1t           | month       | 1     | today - 2 months  | today | today - 2 months  | today      |
      | overall_bubble_rating | c1t           | month       | 3     | today - 4 months  | today | today - 4 months  | today      |
      | overall_bubble_rating | c1t           | month       | 11    | today - 40 months | today | today - 12 months | today      |

  Scenario Outline: Checking data corectness for analitics data
    When Get "<metric>" for list of properties for customer "<customer_code>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "<json_input_file>" equals to previous response

    Examples:
      | metric                | json_input_file                                        | customer_code | granularity | since      | until      |
      | popularity_index_rank | /multiproperty/customer/popularity_index_day.json      | c1t           | day         | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | /multiproperty/customer/popularity_index_week.json     | c1t           | week        | 2015-11-12 | 2015-12-03 |
      | popularity_index_rank | /multiproperty/customer/popularity_index_month.json    | c1t           | month       | 2015-08-26 | 2015-12-03 |

      | aspects_of_business   | /multiproperty/customer/aspects_of_business_day.json   | c1t           | day         | 2015-12-03 | 2015-12-03 |
      | aspects_of_business   | /multiproperty/customer/aspects_of_business_week.json  | c1t           | week        | 2015-11-12 | 2015-12-03 |
      | aspects_of_business   | /multiproperty/customer/aspects_of_business_month.json | c1t           | month       | 2015-08-26 | 2015-12-03 |

      | number_of_reviews     | /multiproperty/customer/number_of_reviews_day.json     | c1t           | day         | 2015-12-03 | 2015-12-03 |
      | number_of_reviews     | /multiproperty/customer/number_of_reviews_week.json    | c1t           | week        | 2015-11-12 | 2015-12-03 |
      | number_of_reviews     | /multiproperty/customer/number_of_reviews_month.json   | c1t           | month       | 2015-08-26 | 2015-12-03 |

      | overall_bubble_rating | /multiproperty/customer/bubble_for_day.json            | c1t           | day         | 2015-12-03 | 2015-12-03 |
      | overall_bubble_rating | /multiproperty/customer/bubble_for_week.json           | c1t           | week        | 2015-11-12 | 2015-12-03 |
      | overall_bubble_rating | /multiproperty/customer/bubble_for_month.json          | c1t           | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Get analytics data from TA API that are more than year old
    When Get "<metric>" for list of properties for customer "<customer_code>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "properties"

    Examples:
      | metric                | customer_code | granularity | since      | until      |
      | popularity_index_rank | c1t           | day         | 1880-12-01 | 1880-12-03 |
      | popularity_index_rank | c1t           | week        | 1880-11-01 | 1880-12-03 |
      | popularity_index_rank | c1t           | month       | 1880-10-01 | 1880-12-03 |

      | aspects_of_business   | c1t           | day         | 1880-12-01 | 1880-12-03 |
      | aspects_of_business   | c1t           | week        | 1880-11-01 | 1880-12-03 |
      | aspects_of_business   | c1t           | month       | 1880-10-01 | 1880-12-03 |

      | number_of_reviews     | c1t           | day         | 1880-12-01 | 1880-12-03 |
      | number_of_reviews     | c1t           | week        | 1880-11-01 | 1880-12-03 |
      | number_of_reviews     | c1t           | month       | 1880-10-01 | 1880-12-03 |

      | overall_bubble_rating | c1t           | day         | 1880-12-01 | 1880-12-03 |
      | overall_bubble_rating | c1t           | week        | 1880-11-01 | 1880-12-03 |
      | overall_bubble_rating | c1t           | month       | 1880-10-01 | 1880-12-03 |


  Scenario Outline: Get analytics data from TA API that has wrong time interval
    When Get "<metric>" for list of properties for customer "<customer_code>" with since "<since>" until "<until>" granularity "<granularity>" limit "/null" and cursor "/null"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "properties"

    Examples:
      | metric                | customer_code | granularity | until      | since      |
      | popularity_index_rank | c1t           | day         | 2015-12-02 | 2015-12-03 |
      | popularity_index_rank | c1t           | week        | 2015-11-02 | 2015-12-03 |
      | popularity_index_rank | c1t           | month       | 2015-10-02 | 2015-12-03 |

      | aspects_of_business   | c1t           | day         | 2015-12-02 | 2015-12-03 |
      | aspects_of_business   | c1t           | week        | 2015-11-02 | 2015-12-03 |
      | aspects_of_business   | c1t           | month       | 2015-10-02 | 2015-12-03 |

      | number_of_reviews     | c1t           | day         | 2015-12-02 | 2015-12-03 |
      | number_of_reviews     | c1t           | week        | 2015-11-02 | 2015-12-03 |
      | number_of_reviews     | c1t           | month       | 2015-10-02 | 2015-12-03 |

      | overall_bubble_rating | c1t           | day         | 2015-12-02 | 2015-12-03 |
      | overall_bubble_rating | c1t           | week        | 2015-11-02 | 2015-12-03 |
      | overall_bubble_rating | c1t           | month       | 2015-10-02 | 2015-12-03 |
