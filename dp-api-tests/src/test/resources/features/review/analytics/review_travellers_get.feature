Feature: review travelers
  Testing of api for review modul alias trip_advisor with mock data in db - testing property id is "99000199-9999-4999-a999-999999999999"
  data in db are mostly increasing some of the data also includes nulls

  NOTE: every monday will some tests fails because date calculation
  #todo - granularity (date values) new rules when finished add tests

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


  Scenario Outline: Get amount of specific analytics data from API for a given granularity for travelers overall bubble rating
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of analytics for travelers overall bubble rating

    Examples:
      | url                               | granularity | count | since             | until | real_since        | real_until | property                             |
      | /travellers/overall_bubble_rating | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/overall_bubble_rating | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/overall_bubble_rating | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/overall_bubble_rating | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/overall_bubble_rating | week        | 3     | today - 1 month   | today | today - 1 month   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/overall_bubble_rating | week        | 51    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/overall_bubble_rating | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/overall_bubble_rating | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/overall_bubble_rating | month       | 11    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get amount specific analytics data from API for a given granularity for aspect of business
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of analytics for travelers for aspect of business

    Examples:
      | url                             | granularity | count | since             | until | real_since        | real_until | property                             |
      | /travellers/aspects_of_business | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business | week        | 3     | today - 1 month   | today | today - 1 month   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business | week        | 51    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business | month       | 11    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get amount of specific analytics data from API for a given granularity for travelers number of reviews
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of analytics for travelers number of reviews

    Examples:
      | url                           | granularity | count | since             | until | real_since        | real_until | property                             |
      | /travellers/number_of_reviews | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews | week        | 3     | today - 1 month   | today | today - 1 month   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews | week        | 51    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews | month       | 11    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get amount of analytics data from API for a given granularity of more complex endpoints
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> number of analytics for travelers

    Examples:
      | url          | granularity | count | since             | until | real_since        | real_until | property                             |
      | /travellers/ | day         | 1     | today             | today | today             | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | day         | 366   | today - 40 months | today | today - 365 days  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | week        | 1     | today - 13 days   | today | today - 13 days   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | week        | 3     | today - 1 month   | today | today - 1 month   | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | week        | 51    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | month       | 1     | today - 2 months  | today | today - 2 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | month       | 3     | today - 4 months  | today | today - 4 months  | today      | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | month       | 11    | today - 40 months | today | today - 12 months | today      | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Checking data corectness for all travellers analitics
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

  Scenario Outline: Checking data corectness for number_of_reviews
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
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "data"

    Examples:
      | url                               | granularity | since      | until      | property                             |
      | /travellers/overall_bubble_rating | day         | 1880-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews     | day         | 1880-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business   | day         | 1880-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |

      | /travellers/overall_bubble_rating | week        | 1880-05-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews     | week        | 1880-05-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business   | week        | 1880-05-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |

      | /travellers/overall_bubble_rating | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews     | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business   | month       | 1880-01-03 | 1800-12-03 | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get specific analytics data from TA API that are more than year old
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "data"

    Examples:
      | url          | granularity | since      | until      | property                             |
      | /travellers/ | day         | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | week        | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | month       | 1800-12-03 | 1880-12-03 | 99000199-9999-4999-a999-999999999999 |


  Scenario Outline: Get analytics data from TA API that has wrong time interval
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "data"

    Examples:
      | url                               | granularity | until      | since      | property                             |
      | /travellers/overall_bubble_rating | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews     | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business   | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |

      | /travellers/overall_bubble_rating | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews     | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business   | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |

      | /travellers/overall_bubble_rating | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/number_of_reviews     | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/aspects_of_business   | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |

  Scenario Outline: Get specific analytics data from TA API has wrong time interval
    When Get trip advisor travellers "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "data"

    Examples:
      | url          | granularity | until      | since      | property                             |
      | /travellers/ | day         | 2015-12-02 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | week        | 2015-11-12 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
      | /travellers/ | month       | 2015-08-26 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |



  #todo - return separate travelers for all enpoints (forgoten)
#  Scenario Outline: Get error codes specific traveler
#    When Get trip advisor travellers "<url>" data for "<traveler>" with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
#    Then Response code is 404
#    And Data is owned by "tripadvisor"
#    And Content type is "application/json"
#    And Body contains entity with attribute "granularity" value "<granularity>"
#
#    Examples:
#      | url                               | granularity | traveler     | since      | until      | property                             |
#      | /travellers/                      | day         | not_existing | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/                      | week        | not_existing | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/                      | month       | not_existing | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/overall_bubble_rating | day         | not_existing | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/overall_bubble_rating | week        | not_existing | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/overall_bubble_rating | month       | not_existing | 2015-12-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/number_of_reviews     | day         | not_existing | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/number_of_reviews     | week        | not_existing | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/number_of_reviews     | month       | not_existing | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/aspects_of_business   | day         | not_existing | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/aspects_of_business   | week        | not_existing | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |
#      | /travellers/aspects_of_business   | month       | not_existing | 2015-08-03 | 2015-12-03 | 99000199-9999-4999-a999-999999999999 |




