Feature: Review multiproperty single property
  GET /review/analytics/property/{property_id}/popularity_index_rank
  Returns popularity index rank statistics for the given time period for the specified property.
  #TODO add paging tests - currently paging is only prepared but not implemented
  #TODO request with invalid access token ? how ?

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

    Given Relation between user with username "default1" and property with code "p1_code" exists
    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"

    Given Set access token for review steps defs

#------------
# GET /review/analytics/property/{property_id}/popularity_index_rank

  Scenario Outline: Get trip advisor analytics data from API for a given wrong granularity
    When Get "<metric>" for single property "<property_id>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "63"

    Examples:
      | metric                | property_id                          | granularity | since      | until      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | dd          | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | yy          | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | day         | 2015-12    | 2015-12-03 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-03    |

      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | dd          | 2015-12-03 | 2015-12-03 |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | yy          | 2015-12-03 | 2015-12-03 |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | day         | 2015-12    | 2015-12-03 |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-03    |

      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | dd          | 2015-12-03 | 2015-12-03 |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | yy          | 2015-12-03 | 2015-12-03 |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | day         | 2015-12    | 2015-12-03 |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-03    |

      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | dd          | 2015-12-03 | 2015-12-03 |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | yy          | 2015-12-03 | 2015-12-03 |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | day         | 2015-12    | 2015-12-03 |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-03    |


  Scenario Outline: Checking error codes for analytics data
    When Get "<metric>" for single property "<property_id>" with since "/null" until "/null" and granularity "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "day"
    And Data is owned by "tripadvisor"

    Examples:
      | metric                | property_id                          |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get "<metric>" for single property "<property_id>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains 1 properties
    And Response properties contains "<count>" values
    Examples:
      | metric                | property_id                          | granularity | count | since             | until | real_since        | real_until |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | day         | 1     | today             | today | today             | today      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | day         | 366   | today - 40 months | today | today - 365 days  | today      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | week        | 1     | today - 13 days   | today | today - 13 days   | today      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | week        | 3     | today - 27 days   | today | today - 27 days   | today      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | week        | 51    | today - 363 days  | today | today - 363 days  | today      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | month       | 1     | today - 2 months  | today | today - 2 months  | today      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | month       | 3     | today - 4 months  | today | today - 4 months  | today      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | month       | 11    | today - 40 months | today | today - 12 months | today      |

      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | day         | 1     | today             | today | today             | today      |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | day         | 366   | today - 40 months | today | today - 365 days  | today      |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | week        | 1     | today - 13 days   | today | today - 13 days   | today      |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | week        | 3     | today - 27 days   | today | today - 27 days   | today      |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | week        | 51    | today - 363 days  | today | today - 363 days  | today      |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | month       | 1     | today - 2 months  | today | today - 2 months  | today      |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | month       | 3     | today - 4 months  | today | today - 4 months  | today      |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | month       | 11    | today - 40 months | today | today - 12 months | today      |

      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | day         | 1     | today             | today | today             | today      |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | day         | 366   | today - 40 months | today | today - 365 days  | today      |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | week        | 1     | today - 13 days   | today | today - 13 days   | today      |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | week        | 3     | today - 27 days   | today | today - 27 days   | today      |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | week        | 51    | today - 363 days  | today | today - 363 days  | today      |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | month       | 1     | today - 2 months  | today | today - 2 months  | today      |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | month       | 3     | today - 4 months  | today | today - 4 months  | today      |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | month       | 11    | today - 40 months | today | today - 12 months | today      |

      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | day         | 1     | today             | today | today             | today      |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | day         | 366   | today - 40 months | today | today - 365 days  | today      |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | week        | 1     | today - 13 days   | today | today - 13 days   | today      |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | week        | 3     | today - 27 days   | today | today - 27 days   | today      |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | week        | 51    | today - 363 days  | today | today - 363 days  | today      |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | month       | 1     | today - 2 months  | today | today - 2 months  | today      |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | month       | 3     | today - 4 months  | today | today - 4 months  | today      |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | month       | 11    | today - 40 months | today | today - 12 months | today      |

  Scenario Outline: Checking data corectness for popularity_index_rank
    When Get "<metric>" for single property "<property_id>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "<json_input_file>" equals to previous response for popularity index

    Examples:
      | metric                | json_input_file                                     | property_id                          | granularity | since      | until      |
      | popularity_index_rank | /multiproperty/property/popularity_index_day.json   | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | /multiproperty/property/popularity_index_week.json  | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | popularity_index_rank | /multiproperty/property/popularity_index_month.json | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Checking data corectness for aspects_of_business
    When Get "<metric>" for single property "<property_id>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "<json_input_file>" equals to previous response for aspects of business

    Examples:
      | metric              | json_input_file                                        | property_id                          | granularity | since      | until      |
      | aspects_of_business | /multiproperty/property/aspects_of_business_day.json   | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | aspects_of_business | /multiproperty/property/aspects_of_business_week.json  | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | aspects_of_business | /multiproperty/property/aspects_of_business_month.json | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Checking data corectness for number_of_reviews
    When Get "<metric>" for single property "<property_id>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "<json_input_file>" equals to previous response for number of reviews

    Examples:
      | metric            | json_input_file                                      | property_id                          | granularity | since      | until      |
      | number_of_reviews | /multiproperty/property/number_of_reviews_day.json   | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | number_of_reviews | /multiproperty/property/number_of_reviews_week.json  | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | number_of_reviews | /multiproperty/property/number_of_reviews_month.json | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

  @Smoke
  Scenario Outline: Checking data corectness for overall_bubble_rating
    When Get "<metric>" for single property "<property_id>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "<json_input_file>" equals to previous response for overall bubble rating

    Examples:
      | metric                | json_input_file                               | property_id                          | granularity | since      | until      |
      | overall_bubble_rating | /multiproperty/property/bubble_for_day.json   | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | overall_bubble_rating | /multiproperty/property/bubble_for_week.json  | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | overall_bubble_rating | /multiproperty/property/bubble_for_month.json | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Get analytics data from TA API that are more than year old
    When Get "<metric>" for single property "<property_id>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "properties"

    Examples:
      | metric                | property_id                          | granularity | since      | until      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | day         | 1880-12-01 | 1880-12-03 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | week        | 1880-11-01 | 1880-12-03 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | month       | 1880-10-01 | 1880-12-03 |

      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | day         | 1880-12-01 | 1880-12-03 |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | week        | 1880-11-01 | 1880-12-03 |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | month       | 1880-10-01 | 1880-12-03 |

      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | day         | 1880-12-01 | 1880-12-03 |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | week        | 1880-11-01 | 1880-12-03 |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | month       | 1880-10-01 | 1880-12-03 |

      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | day         | 1880-12-01 | 1880-12-03 |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | week        | 1880-11-01 | 1880-12-03 |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | month       | 1880-10-01 | 1880-12-03 |

  Scenario Outline: Get analytics data from TA API that has wrong time interval
    When Get "<metric>" for single property "<property_id>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "properties"

    Examples:
      | metric                | property_id                          | granularity | until      | since      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-02 | 2015-12-03 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-02 | 2015-12-03 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | month       | 2015-10-02 | 2015-12-03 |

      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-02 | 2015-12-03 |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-02 | 2015-12-03 |
      | aspects_of_business   | 99000199-9999-4999-a999-999999999999 | month       | 2015-10-02 | 2015-12-03 |

      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-02 | 2015-12-03 |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-02 | 2015-12-03 |
      | number_of_reviews     | 99000199-9999-4999-a999-999999999999 | month       | 2015-10-02 | 2015-12-03 |

      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-02 | 2015-12-03 |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-02 | 2015-12-03 |
      | overall_bubble_rating | 99000199-9999-4999-a999-999999999999 | month       | 2015-10-02 | 2015-12-03 |
