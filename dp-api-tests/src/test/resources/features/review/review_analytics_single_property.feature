Feature: review_multiproperty_single_property
  GET /review/analytics/property/{property_id}/popularity_index_rank
  Returns popularity index rank statistics for the given time period for the specified property.
  #TODO add paging tests - currently paging is only prepared but not implemented
  #TODO request with invalid access token ? how ?

  Background:
    Given Database is cleaned
    Given The following properties exist with random address and billing address
      | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      |
      | 99000199-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |
      | 99000299-9999-4999-a999-999999999999 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague |
      | 99000399-9999-4999-a999-999999999999 | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague |
      | 99000499-9999-4999-a999-999999999999 | salesforceid_4 | p4_name      | p4_code      | http://www.snapshot.travel | p4@tenants.biz | true           | Europe/Prague |
      | 99000599-9999-4999-a999-999999999999 | salesforceid_5 | p5_name      | p5_code      | http://www.snapshot.travel | p5@tenants.biz | true           | Europe/Prague |

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
    Given Relation between user with username "default1" and property with code "p2_code" exists
    Given Relation between user with username "default1" and property with code "p3_code" exists
    Given Relation between user with username "default1" and property with code "p4_code" exists
    Given Relation between user with username "default1" and property with code "p5_code" exists

    Given Relation between property with code "p1_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p2_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p3_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p4_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p5_code" and customer with code "c1t" exists with type "anchor" from "2015-01-01" to "2016-12-31"

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


  Scenario Outline: Checking error codes for analytics data
    When Get "<metric>" for single property "<property_id>" with since "/null" until "/null" and granularity "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "granularity" value "day"
    And Data is owned by "tripadvisor"

    Examples:
      | metric                | property_id                          |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 |

#todo figure out how to test max DP is limited to return results maximum one year back, what are maximum values that can be returned ?
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
      | metric                | property_id                          | granularity | count | since      | until      | real_since        | real_until |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03        | 2015-12-03 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | day         | 31    | 2015-11-03 | 2015-12-03 | 2015-11-03        | 2015-12-03 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | day         | 366   | 2015-01-01 | today      | today - 365 days  | today      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | week        | 1     | 2015-11-17 | 2015-12-03 | 2015-11-23        | 2015-11-29 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | week        | 3     | 2015-11-03 | 2015-12-03 | 2015-11-09        | 2015-11-29 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | week        | 52    | 2015-01-01 | today      | today - 12 months | today      |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | month       | 1     | 2015-10-26 | 2015-12-03 | 2015-11-01        | 2015-11-30 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | month       | 3     | 2015-08-03 | 2015-12-03 | 2015-09-01        | 2015-11-30 |
      | popularity_index_rank | 99000199-9999-4999-a999-999999999999 | month       | 11    | 2015-01-01 | today      | today - 12 months | today      |

  Scenario Outline: Checking data corectness for analitics
    When Get "<metric>" for single property "<property_id>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "<json_input_file>" equals to previous response

    Examples:
      | metric                | json_input_file                                     | property_id                          | granularity | since      | until      |
      | popularity_index_rank | /multiproperty/property/popularity_index_day.json   | 99000199-9999-4999-a999-999999999999 | day         | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | /multiproperty/property/popularity_index_week.json  | 99000199-9999-4999-a999-999999999999 | week        | 2015-11-12 | 2015-12-03 |
      | popularity_index_rank | /multiproperty/property/popularity_index_month.json | 99000199-9999-4999-a999-999999999999 | month       | 2015-08-26 | 2015-12-03 |

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
