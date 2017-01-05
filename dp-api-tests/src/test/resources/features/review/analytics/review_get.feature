Feature: Review
  Testing of api for review modul alias trip_advisor with mock data in db - testing property id is "99000199-9999-4999-a999-999999999999"
  data in db are mostly increasing some of the data also includes nulls

  NOTE: every monday will some tests fails because date calculation
  #todo granularity (date values) new rules when finished add tests

  Background:
    Given Database is cleaned

    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |

    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "true"
      | userId                               | userType | userName  | firstName | lastName | email                     | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snapUser1 | Snapshot  | User1    | snapUser1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 99000199-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

    Given Default Snapshot user is created
    Given The password of user "snapUser1" is "Password1"
    Given Relation between user with username "snapUser1" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with isPrimary "true"

    Given Get token for user "snapUser1" with password "Password1"
    Given Set access token from session for customer steps defs
    Given Set access token for review steps defs

    Given Relation between user with username "snapUser1" and property with code "p1_code" exists
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2016-12-31"
    Given Default partner is created
    Given Default application is created


  Scenario Outline: Get trip advisor analytics data from API for a given wrong granularity
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is "40002"

    Examples:
      | url                                         | granularity | property                             | since      | until      |
      | /analytics/                                 | dd          | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/                                 |             | 99000199-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/rating_score                     | MONTHS      | 99000199-9999-4999-a999-999999999999 | 2015-11-03 | 2015-12-03 |
      | /analytics/rating_score                     |             | 99000199-9999-4999-a999-999999999999 | 2015-11-03 | 2015-12-03 |
      | /analytics/travellers/                      | W33K        | 99000199-9999-4999-a999-999999999999 | 2015-11-03 | 2015-12-03 |
      | /analytics/travellers/                      |             | 99000199-9999-4999-a999-999999999999 | 2015-11-03 | 2015-12-03 |
      | /analytics/travellers/overall_bubble_rating | WEEKS       | 99000199-9999-4999-a999-999999999999 | 2015-10-03 | 2015-12-03 |
      | /analytics/travellers/number_of_reviews     | m0nth       | 99000199-9999-4999-a999-999999999999 | 2015-10-03 | 2015-12-03 |
      | /analytics/travellers/aspects_of_business   | years       | 99000199-9999-4999-a999-999999999999 | 2015-10-03 | 2015-12-03 |

  Scenario Outline: Checking error codes for analytics data without property
    When Get trip advisor "<url>" with missing property header
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is "40002"

    Examples:
      | url                                         |
      | /analytics/                                 |
      | /analytics/rating_score                     |
      | /analytics/travellers/                      |
      | /analytics/travellers/overall_bubble_rating |
      | /analytics/travellers/number_of_reviews     |
      | /analytics/travellers/aspects_of_business   |

  Scenario Outline: Get specific analytics data from API for a given granularity, correct dates and values
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> values

    Examples:
      | url                     | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics/rating_score | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |

  @Smoke
  Scenario Outline: Get specific analytics data from API for a given granularity of more complex endpoints
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of review analytics

    Examples:
      | url        | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /analytics | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |

  @Smoke
  Scenario Outline: Checking data corectness for analitics for analytics
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property_id>" since "<since>" until "<until>"
    And Response code is "200"
    And Content type is "application/json"
    Then Review file "<json_input_file>" is equals to previous response for analytics

    Examples:
      | json_input_file                     | url         | property_id                          | granularity | since      | until      |
      | /analytics/analytics_for_day.json   | /analytics/ | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | /analytics/analytics_for_week.json  | /analytics/ | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | /analytics/analytics_for_month.json | /analytics/ | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |


  Scenario Outline: Checking data corectness for analitics for /analytics/rating_score
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    And Content type is "application/json"
    And Response code is "200"
    And Review file "<json_input_file>" is equals to previous response for rating score

    Examples:
      | json_input_file                                 | url                     | property                             | granularity | since      | until      |
      | /multiproperty/property/rating_score_day.json   | /analytics/rating_score | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | /multiproperty/property/rating_score_week.json  | /analytics/rating_score | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | /multiproperty/property/rating_score_month.json | /analytics/rating_score | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |


  Scenario Outline: Get analytics data from TA API that are more than year old for /analytics/rating_score
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Response contains 0 values

    Examples:
      | url                     | granularity | since      | until      | property                             |
      | /analytics/rating_score | day         | 1880-12-01 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | week        | 1880-10-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | month       | 1880-01-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get specific analytics data from TA API that are more than year old for analytics
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    Then Response contains 0 values of attribute named "aspects_of_business"
    Then Response contains 0 values of attribute named "overall_bubble_rating"
    Then Response contains 0 values of attribute named "number_of_reviews"
    Then Response contains 0 values of attribute named "rating_score"
    Then Response contains 0 values of attribute named "popularity_index_rank"

    Examples:
      | url         | granularity | since      | until      | property                             |
      | /analytics/ | day         | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | week        | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | month       | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |


#  Change expected message when DP-1495 is fixed
  Scenario Outline: Get analytics data from TA API that has wrong time interval for /analytics/rating_score
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 400
    And Custom code is 40002
    And Body contains entity with attribute "message" value "There is a problem with some parameters. See details."

    Examples:
      | url                     | granularity | until      | since      | property                             |
      | /analytics/rating_score | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/rating_score | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |


    #  Change expected message when DP-1495 is fixed
  Scenario Outline: Get specific analytics data from TA API has wrong time interval for /analytics/
    When Get trip advisor "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 400
    And Custom code is 40002
    And Body contains entity with attribute "message" value "There is a problem with some parameters. See details."

    Examples:
      | url         | granularity | until      | since      | property                             |
      | /analytics/ | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /analytics/ | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |


# NEW
#----------------------------------------------------------------------------------------------------------------------
  Scenario Outline: Get analytics data from TA API that are more than year old for smaller review endpoints
    When Get review "<url>" data with "<granularity>" granularity with since "<since>" until "<until>" limit "/null" and cursor "/null"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    Then Response contains 0 values of attribute named "properties"

    Examples:
      | url                              | granularity | since      | until      |
      | /analytics/popularity_index_rank | day         | 1880-12-01 | 1880-12-03 |
      | /analytics/popularity_index_rank | week        | 1880-10-03 | 1880-12-03 |
      | /analytics/popularity_index_rank | month       | 1880-01-03 | 1880-12-03 |

      | /analytics/aspects_of_business   | day         | 1880-12-01 | 1880-12-03 |
      | /analytics/aspects_of_business   | week        | 1880-10-03 | 1880-12-03 |
      | /analytics/aspects_of_business   | month       | 1880-01-03 | 1880-12-03 |

      | /analytics/number_of_reviews     | day         | 1880-12-01 | 1880-12-03 |
      | /analytics/number_of_reviews     | week        | 1880-10-03 | 1880-12-03 |
      | /analytics/number_of_reviews     | month       | 1880-01-03 | 1880-12-03 |

      | /analytics/overall_bubble_rating | day         | 1880-12-01 | 1880-12-03 |
      | /analytics/overall_bubble_rating | week        | 1880-10-03 | 1880-12-03 |
      | /analytics/overall_bubble_rating | month       | 1880-01-03 | 1880-12-03 |

  Scenario Outline: Validate that popularity_index_rank have valid value in the db
    When Get review "<url>" data with "<granularity>" granularity with since "<since>" until "<until>" limit "/null" and cursor "/null"
    And Content type is "application/json"
    And Response code is "200"
    And Review file "<json_input_file>" equals to previous response for popularity index

    Examples:
      | url                              | granularity | json_input_file                                     | since      | until      |
      | /analytics/popularity_index_rank | day         | /multiproperty/property/popularity_index_day.json   | 2015-12-03 | 2015-12-03 |
      | /analytics/popularity_index_rank | week        | /multiproperty/property/popularity_index_week.json  | 2015-11-12 | 2015-12-03 |
      | /analytics/popularity_index_rank | month       | /multiproperty/property/popularity_index_month.json | 2015-08-26 | 2015-12-03 |


  Scenario Outline: Validate that aspects_of_business have valid value in the db
    When Get review "<url>" data with "<granularity>" granularity with since "<since>" until "<until>" limit "/null" and cursor "/null"
    And Content type is "application/json"
    And Response code is "200"
    And Review file "<json_input_file>" equals to previous response for aspects of business

    Examples:
      | url                            | granularity | json_input_file                                        | since      | until      |
      | /analytics/aspects_of_business | day         | /multiproperty/property/aspects_of_business_day.json   | 2015-12-03 | 2015-12-03 |
      | /analytics/aspects_of_business | week        | /multiproperty/property/aspects_of_business_week.json  | 2015-11-12 | 2015-12-03 |
      | /analytics/aspects_of_business | month       | /multiproperty/property/aspects_of_business_month.json | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Validate that number_of_reviews have valid value in the db
    When Get review "<url>" data with "<granularity>" granularity with since "<since>" until "<until>" limit "/null" and cursor "/null"
    And Content type is "application/json"
    And Response code is "200"
    And Review file "<json_input_file>" equals to previous response for number of reviews

    Examples:
      | url                          | granularity | json_input_file                                      | since      | until      |
      | /analytics/number_of_reviews | day         | /multiproperty/property/number_of_reviews_day.json   | 2015-12-03 | 2015-12-03 |
      | /analytics/number_of_reviews | week        | /multiproperty/property/number_of_reviews_week.json  | 2015-11-12 | 2015-12-03 |
      | /analytics/number_of_reviews | month       | /multiproperty/property/number_of_reviews_month.json | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Validate that overall_bubble_rating have valid value in the db
    When Get review "<url>" data with "<granularity>" granularity with since "<since>" until "<until>" limit "/null" and cursor "/null"
    And Content type is "application/json"
    And Response code is "200"
    And Review file "<json_input_file>" equals to previous response for overall bubble rating

    Examples:
      | url                              | granularity | json_input_file                               | since      | until      |
      | /analytics/overall_bubble_rating | day         | /multiproperty/property/bubble_for_day.json   | 2015-12-03 | 2015-12-03 |
      | /analytics/overall_bubble_rating | week        | /multiproperty/property/bubble_for_week.json  | 2015-11-12 | 2015-12-03 |
      | /analytics/overall_bubble_rating | month       | /multiproperty/property/bubble_for_month.json | 2015-08-26 | 2015-12-03 |

  #  Change expected message when DP-1495 is fixed
  Scenario Outline: Get analytics data from TA API that has wrong time interval for smaller review endpoints
    When Get review "<url>" data with "<granularity>" granularity with since "<since>" until "<until>" limit "/null" and cursor "/null"
    Then Response code is 400
    And Custom code is 40002
    And Body contains entity with attribute "message" value "There is a problem with some parameters. See details."
    And Body does not contain property with attribute "properties"

    Examples:
      | url                              | granularity | until      | since      |
      | /analytics/popularity_index_rank | day         | 2015-12-02 | 2015-12-03 |
      | /analytics/popularity_index_rank | week        | 2015-11-12 | 2015-12-03 |
      | /analytics/popularity_index_rank | month       | 2015-08-26 | 2015-12-03 |

      | /analytics/aspects_of_business   | day         | 2015-12-02 | 2015-12-03 |
      | /analytics/aspects_of_business   | week        | 2015-11-12 | 2015-12-03 |
      | /analytics/aspects_of_business   | month       | 2015-08-26 | 2015-12-03 |

      | /analytics/number_of_reviews     | day         | 2015-12-02 | 2015-12-03 |
      | /analytics/number_of_reviews     | week        | 2015-11-12 | 2015-12-03 |
      | /analytics/number_of_reviews     | month       | 2015-08-26 | 2015-12-03 |

      | /analytics/overall_bubble_rating | day         | 2015-12-02 | 2015-12-03 |
      | /analytics/overall_bubble_rating | week        | 2015-11-12 | 2015-12-03 |
      | /analytics/overall_bubble_rating | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Get new specific analytics data from API for a given granularity for smaller review endpoints
    When Get review "<url>" data with "<granularity>" granularity with since "<since>" until "<until>" limit "/null" and cursor "/null"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains 1 properties
    And Response properties contains "<count>" values

    Examples:
      | url                              | granularity | count | since             | until | real_since        | real_until |
      | /analytics/popularity_index_rank | day         | 1     | today             | today | today             | today      |
      | /analytics/popularity_index_rank | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | /analytics/popularity_index_rank | day         | 366   | today - 365 days  | today | today - 365 days  | today      |
      | /analytics/popularity_index_rank | week        | 3     | today - 13 days   | today | today - 13 days   | today      |
      | /analytics/popularity_index_rank | week        | 5     | today - 27 days   | today | today - 27 days   | today      |
      | /analytics/popularity_index_rank | week        | 53    | today - 363 days  | today | today - 363 days  | today      |
      | /analytics/popularity_index_rank | month       | 3     | today - 2 months  | today | today - 2 months  | today      |
      | /analytics/popularity_index_rank | month       | 5     | today - 4 months  | today | today - 4 months  | today      |
      | /analytics/popularity_index_rank | month       | 13    | today - 12 months | today | today - 12 months | today      |

      | /analytics/number_of_reviews     | day         | 1     | today             | today | today             | today      |
      | /analytics/number_of_reviews     | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | /analytics/number_of_reviews     | day         | 366   | today - 365 days  | today | today - 365 days  | today      |
      | /analytics/number_of_reviews     | week        | 3     | today - 13 days   | today | today - 13 days   | today      |
      | /analytics/number_of_reviews     | week        | 5     | today - 27 days   | today | today - 27 days   | today      |
      | /analytics/number_of_reviews     | week        | 53    | today - 363 days  | today | today - 363 days  | today      |
      | /analytics/number_of_reviews     | month       | 3     | today - 2 months  | today | today - 2 months  | today      |
      | /analytics/number_of_reviews     | month       | 5     | today - 4 months  | today | today - 4 months  | today      |
      | /analytics/number_of_reviews     | month       | 13    | today - 12 months | today | today - 12 months | today      |

      | /analytics/aspects_of_business   | day         | 1     | today             | today | today             | today      |
      | /analytics/aspects_of_business   | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | /analytics/aspects_of_business   | day         | 366   | today - 365 days  | today | today - 365 days  | today      |
      | /analytics/aspects_of_business   | week        | 3     | today - 13 days   | today | today - 13 days   | today      |
      | /analytics/aspects_of_business   | week        | 5     | today - 27 days   | today | today - 27 days   | today      |
      | /analytics/aspects_of_business   | week        | 53    | today - 363 days  | today | today - 363 days  | today      |
      | /analytics/aspects_of_business   | month       | 3     | today - 2 months  | today | today - 2 months  | today      |
      | /analytics/aspects_of_business   | month       | 5     | today - 4 months  | today | today - 4 months  | today      |
      | /analytics/aspects_of_business   | month       | 13    | today - 12 months | today | today - 12 months | today      |

      | /analytics/overall_bubble_rating | day         | 1     | today             | today | today             | today      |
      | /analytics/overall_bubble_rating | day         | 41    | today - 40 days   | today | today - 40 days   | today      |
      | /analytics/overall_bubble_rating | day         | 366   | today - 365 days  | today | today - 365 days  | today      |
      | /analytics/overall_bubble_rating | week        | 3     | today - 13 days   | today | today - 13 days   | today      |
      | /analytics/overall_bubble_rating | week        | 5     | today - 27 days   | today | today - 27 days   | today      |
      | /analytics/overall_bubble_rating | week        | 53    | today - 363 days  | today | today - 363 days  | today      |
      | /analytics/overall_bubble_rating | month       | 3     | today - 2 months  | today | today - 2 months  | today      |
      | /analytics/overall_bubble_rating | month       | 5     | today - 4 months  | today | today - 4 months  | today      |
      | /analytics/overall_bubble_rating | month       | 13    | today - 12 months | today | today - 12 months | today      |


  Scenario Outline: Checking error codes for analytics data for every field
    When Get review "<url>" data with "<granularity>" granularity with since "<since>" until "<until>" limit "<limit>" and cursor "<cursor>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is "40002"

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

      | /analytics/number_of_reviews     | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | /analytics/number_of_reviews     | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | /analytics/number_of_reviews     | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | /analytics/number_of_reviews     | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | /analytics/number_of_reviews     | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | /analytics/number_of_reviews     | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | /analytics/number_of_reviews     | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | /analytics/number_of_reviews     | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | /analytics/number_of_reviews     | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | /analytics/number_of_reviews     | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |

      | /analytics/aspects_of_business   | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | /analytics/aspects_of_business   | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | /analytics/aspects_of_business   | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | /analytics/aspects_of_business   | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | /analytics/aspects_of_business   | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | /analytics/aspects_of_business   | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | /analytics/aspects_of_business   | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | /analytics/aspects_of_business   | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | /analytics/aspects_of_business   | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | /analytics/aspects_of_business   | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |

      | /analytics/overall_bubble_rating | dd          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | /analytics/overall_bubble_rating | yy          | 2015-12-03 | 2015-12-03 | /null        | /null        |
      | /analytics/overall_bubble_rating | day         | 2015-12    | 2015-12-03 | /null        | /null        |
      | /analytics/overall_bubble_rating | day         | 2015-12-03 | 2015-03    | /null        | /null        |
      | /analytics/overall_bubble_rating | day         | 2015-12-03 | 2015-12-03 | bad          | /null        |
      | /analytics/overall_bubble_rating | week        | 2015-12-03 | 2015-12-03 | /null        | bad          |
      | /analytics/overall_bubble_rating | day         | 2015-12-03 | 2015-12-03 | /null        | 301947534059 |
      | /analytics/overall_bubble_rating | day         | 2015-12-03 | 2015-12-03 | 301947534059 | /null        |
      | /analytics/overall_bubble_rating | month       | 2015-12-03 | 2015-12-03 | -23486       | /null        |
      | /analytics/overall_bubble_rating | day         | 2015-12-03 | 2015-12-03 | /null        | -23486       |
