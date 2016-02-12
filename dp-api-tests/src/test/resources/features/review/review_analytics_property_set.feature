Feature: review_multiproperty_property_set
  GET /review/analytics/property_set/{property_set_id}/popularity_index_rank
  Returns popularity index rank statistics for the given time period aggregated for the specified property set.

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
#      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
#      | Given company 3 | c3@tenants.biz | c3t  | salesforceid_given_3 | CZ10000003 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |

    Given The following users exist
      | userType | userName | firstName | lastName | email                | timezone      | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
#      | customer | default2 | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The password of user "default1" is "Password1"
#    Given The password of user "default2" is "Password1"

    Given Relation between user with username "default1" and customer with code "c1t" exists with isPrimary "true"
#    Given Relation between user with username "default2" and customer with code "c2t" exists with isPrimary "true"

    Given The following property sets exist for customer with code "c1t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
#      | ps2_name        | ps2_description        | branch          |

#    Given Get token for user "default1" with password "Password1"
    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p2_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p3_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p4_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p5_code" and property set with name "ps1_name" for customer with code "c1t" exists

#    Given Get token for user "default2" with password "Password1"
#    Given Relation between property with code "p1_code" and customer with code "c2t" exists with type "anchor" from "2015-01-01" to "2016-12-31"
#    Given Relation between property with code "p2_code" and customer with code "c2t" exists with type "anchor" from "2015-01-01" to "2016-12-31"

#------------
# GET /review/analytics/property_set/{property_set_id}/popularity_index_rank

  Scenario Outline: Get trip advisor analytics data from API for a given wrong granularity
    When Get "<metric>" for statistics agregated for property set "<property_set_name>" for customer "c1t" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Content type is "application/json"
    And Response code is "400"
    And Custom code is "63"

    Examples:
      | metric                | property_set_name | granularity | since      | until      |
      | popularity_index_rank | ps1_name          | dd          | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | ps1_name          | yy          | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | ps1_name          | mm          | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | ps1_name          | 1dd         | 2015-12-03 | 2015-12-03 |

  Scenario Outline: Checking error codes for analytics data
    When Get "<metric>" for statistics agregated for property set "<property_set_name>" for customer "c1t" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is "400"
    And Content type is "application/json "
    And Custom code is "52"

    Examples:
      | metric                | property_set_name |
      | popularity_index_rank | ps1_name          |
      | popularity_index_rank | ps1_name          |
      | popularity_index_rank | ps1_name          |
      | popularity_index_rank | ps1_name          |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get "<metric>" for list of properties for customer "<property_set_name>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body contains entity with attribute "since" value "<real_since>"
    And Body contains entity with attribute "until" value "<real_until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response contains <count> values

    Examples:
      | metric                | property_set_name | granularity | count | since      | until      | real_since | real_until |
      | popularity_index_rank | ps1_name          | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | ps1_name          | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | ps1_name          | day         | 1     | 2015-12-03 | 2015-12-03 | 2015-12-03 | 2015-12-03 |


  Scenario Outline: Checking data corectness for analitics
    When Get "<metric>" for list of properties for customer "<property_set_name>" with since "<since>" until "<until>" and granularity "<granularity>"
    And Response code is "200"
    And Content type is "application/json"
    Then Review file "<json_input_file>" is equals to previous response for analytics

    Examples:
      | metric                | json_input_file | property_set_name | granularity | since      | until      |
      | popularity_index_rank | /xxx.json       | ps1_name          | day         | 2015-12-03 | 2015-12-03 |
      | popularity_index_rank | /xxx.json       | ps1_name          | week        | 2015-11-12 | 2015-12-03 |
      | popularity_index_rank | /xxx.json       | ps1_name          | month       | 2015-08-26 | 2015-12-03 |

  Scenario Outline: Get analytics data from TA API that are more than year old
    When Get "<metric>" for list of properties for customer "<property_set_name>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "values"

    Examples:
      | metric                | property_set_name | granularity | since      | until      |
      | popularity_index_rank | ps1_name          | day         | 1880-12-01 | 1880-12-03 |
      | popularity_index_rank | ps1_name          | day         | 1880-12-01 | 1880-12-03 |

  Scenario Outline: Get analytics data from TA API that has wrong time interval
    When Get "<metric>" for list of properties for customer "<property_set_name>" with since "<since>" until "<until>" and granularity "<granularity>"
    Then Response code is 200
    And Data is owned by "tripadvisor"
    And Content type is "application/json"
    And Body does not contain property with attribute "values"

    Examples:
      | metric                | property_set_name | granularity | until      | since      |
      | popularity_index_rank | ps1_name          | day         | 2015-12-02 | 2015-12-03 |
      | popularity_index_rank | ps1_name          | day         | 2015-12-02 | 2015-12-03 |
