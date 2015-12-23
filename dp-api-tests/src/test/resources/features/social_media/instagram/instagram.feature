Feature: instagram

  #Testing of api for instagram with mock data in db - testing property id is "99999999-9999-4999-a999-999999999999"
  #data in db are increasing for all metrics, inserted to db according to following pattern:
  Scenario Outline: Get instagram analytics data from API for a given wrong granularity
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples: 
      | url                             | granularity | property                             | since      | until      | content_type     | response_code | custom_code |
      | /analytics/instagram            | dd          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/pictures   | yy          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/engagement | mm          | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/followers  | 1dd         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/tags       | MONTHS      | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/reach      | DAYs        | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/likes      | W33K        | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/comments   | WEEKS       | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |

  Scenario Outline: Validate that metrics have valid value in the db
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And The metric count is <count>

    Examples: 
      | url                             | granularity | count | since      | until      | property                             | content_type     | response_code |
      | /analytics/instagram            | day         | 1     | 2015-11-03 | 2015-12-04 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/pictures   | day         | 701   | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/engagement | day         | 8412  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/followers  | day         | 1402  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/tags       | day         | 20701 | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/reach      | day         | 1402  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/likes      | day         | 3505  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/comments   | day         | 4907  | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram            | week        | 2     | 2015-11-03 | 2015-11-23 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/pictures   | week        | 683   | 2015-11-03 | 2015-11-16 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/engagement | week        | 8196  | 2015-11-03 | 2015-11-16 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/followers  | week        | 1366  | 2015-11-03 | 2015-11-16 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/tags       | week        | 20683 | 2015-11-03 | 2015-11-16 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/reach      | week        | 1366  | 2015-11-03 | 2015-11-16 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/likes      | week        | 3415  | 2015-11-03 | 2015-11-16 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/comments   | week        | 4781  | 2015-11-03 | 2015-11-16 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram            | month       | 3     | 2015-12-03 | 2015-12-03 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/pictures   | month       | 698   | 2015-10-03 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/engagement | month       | 8376  | 2015-10-03 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/followers  | month       | 1396  | 2015-10-03 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/tags       | month       | 20698 | 2015-10-03 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/reach      | month       | 1396  | 2015-10-03 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/likes      | month       | 3490  | 2015-10-03 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/comments   | month       | 4886  | 2015-10-03 | 2015-12-09 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Data is owned by "<data_owner>"
    And Response contains <count> values
    And Body contains entity with attribute "since" value "<expected_since>"
    And Body contains entity with attribute "until" value "<expected_until>"

    Examples: 
      | url                             | granularity | count | since      | until      | expected_since | expected_until | property                             | content_type     | response_code | data_owner |
      | /analytics/instagram            | day         | 1     | 2015-11-07 | 2015-11-07 | 2015-11-07     | 2015-11-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | day         | 1     | 2015-11-07 | 2015-11-07 | 2015-11-07     | 2015-11-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | day         | 1     | 2015-11-07 | 2015-11-07 | 2015-11-07     | 2015-11-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | day         | 1     | 2015-11-07 | 2015-11-07 | 2015-11-07     | 2015-11-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | day         | 1     | 2015-11-07 | 2015-11-07 | 2015-11-07     | 2015-11-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | day         | 1     | 2015-11-07 | 2015-11-07 | 2015-11-07     | 2015-11-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | day         | 1     | 2015-11-07 | 2015-11-07 | 2015-11-07     | 2015-11-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | day         | 1     | 2015-11-07 | 2015-11-07 | 2015-11-07     | 2015-11-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram            | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | day         | 11    | 2015-11-03 | 2015-11-13 | 2015-11-03     | 2015-11-13     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram            | day         | 23    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | day         | 17    | 2015-11-07 | 2015-11-23 | 2015-11-07     | 2015-11-23     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram            | day         | 90    | 2015-06-07 | 2015-12-07 | 2015-09-09     | 2015-12-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | day         | 90    | 2015-01-07 | 2015-12-07 | 2015-09-09     | 2015-12-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | day         | 90    | 2015-01-07 | 2015-12-07 | 2015-09-09     | 2015-12-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | day         | 90    | 2015-01-07 | 2015-12-07 | 2015-09-09     | 2015-12-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | day         | 90    | 2015-01-07 | 2015-12-07 | 2015-09-09     | 2015-12-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | day         | 90    | 2015-01-07 | 2015-12-07 | 2015-09-09     | 2015-12-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | day         | 90    | 2015-01-07 | 2015-12-07 | 2015-09-09     | 2015-12-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | day         | 90    | 2015-06-07 | 2015-12-07 | 2015-09-09     | 2015-12-07     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram            | week        | 1     | 2015-11-07 | 2015-11-16 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | week        | 1     | 2015-11-07 | 2015-11-16 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | week        | 1     | 2015-11-07 | 2015-11-16 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | week        | 1     | 2015-11-07 | 2015-11-16 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | week        | 1     | 2015-11-07 | 2015-11-16 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | week        | 1     | 2015-11-07 | 2015-11-16 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | week        | 1     | 2015-11-07 | 2015-11-16 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | week        | 1     | 2015-11-07 | 2015-11-16 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram            | week        | 1     | 2015-11-03 | 2015-11-17 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | week        | 1     | 2015-11-03 | 2015-11-17 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | week        | 1     | 2015-11-03 | 2015-11-17 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | week        | 1     | 2015-11-03 | 2015-11-17 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | week        | 1     | 2015-11-03 | 2015-11-17 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | week        | 1     | 2015-11-03 | 2015-11-17 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | week        | 1     | 2015-11-03 | 2015-11-17 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | week        | 1     | 2015-11-03 | 2015-11-17 | 2015-11-09     | 2015-11-15     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram            | week        | 2     | 2015-11-07 | 2015-11-23 | 2015-11-09     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | week        | 2     | 2015-11-07 | 2015-11-22 | 2015-11-09     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | week        | 2     | 2015-11-07 | 2015-11-22 | 2015-11-09     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | week        | 2     | 2015-11-07 | 2015-11-22 | 2015-11-09     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | week        | 2     | 2015-11-07 | 2015-11-22 | 2015-11-09     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | week        | 2     | 2015-11-07 | 2015-11-22 | 2015-11-09     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | week        | 2     | 2015-11-07 | 2015-11-22 | 2015-11-09     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | week        | 2     | 2015-11-07 | 2015-11-22 | 2015-11-09     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram            | week        | 26    | 2014-01-07 | 2015-11-23 | 2015-05-25     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | week        | 26    | 2014-01-07 | 2015-11-23 | 2015-05-25     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | week        | 26    | 2014-01-07 | 2015-11-23 | 2015-05-25     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | week        | 26    | 2014-01-07 | 2015-11-23 | 2015-05-25     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | week        | 26    | 2014-01-07 | 2015-11-23 | 2015-05-25     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | week        | 26    | 2014-01-07 | 2015-11-23 | 2015-05-25     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | week        | 26    | 2014-01-07 | 2015-11-23 | 2015-05-25     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | week        | 26    | 2014-01-07 | 2015-11-23 | 2015-05-25     | 2015-11-22     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram            | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram            | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-02-01     | 2015-02-28     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram            | month       | 11    | 2013-02-01 | 2015-12-10 | 2015-01-01     | 11             | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/pictures   | month       | 11    | 2013-02-01 | 2015-12-10 | 2015-01-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/engagement | month       | 11    | 2013-02-01 | 2015-12-10 | 2015-01-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/followers  | month       | 11    | 2013-02-01 | 2015-12-10 | 2015-01-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/tags       | month       | 11    | 2013-02-01 | 2015-12-10 | 2015-01-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/reach      | month       | 11    | 2013-02-01 | 2015-12-10 | 2015-01-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/likes      | month       | 11    | 2013-02-01 | 2015-12-10 | 2015-01-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |
      | /analytics/instagram/comments   | month       | 11    | 2013-02-01 | 2015-12-10 | 2015-01-01     | 2015-11-30     | 99999999-9999-4999-a999-999999999999 | application/json | 200           | instagram  |

  Scenario Outline: Getting non-existent analytics data
    When Get instagram "<url>" data with "<granularity<" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>

    Examples: 
      | url                              | granularity | property                             | since      | until      | content_type     | response_code |
      | /analytics/instagram/not_present | day         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 404           |

  Scenario Outline: Getting mismatched metrics analytics data
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>

    Examples: 
      | url                         | granularity | property                             | since      | until      | content_type     | response_code |
      | /analytics/instagram/tweets | day         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 404           |

  Scenario Outline: Checking error codes for analytics data
    When Get instagram "<url>" with missing property header
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Custom code is "<custom_code>"

    Examples: 
      | url                             | response_code | custom_code | content_type     |
      | /analytics/instagram            | 400           | 52          | application/json |
      | /analytics/instagram/pictures   | 400           | 52          | application/json |
      | /analytics/instagram/engagement | 400           | 52          | application/json |
      | /analytics/instagram/followers  | 400           | 52          | application/json |
      | /analytics/instagram/tags       | 400           | 52          | application/json |
      | /analytics/instagram/reach      | 400           | 52          | application/json |
      | /analytics/instagram/likes      | 400           | 52          | application/json |
      | /analytics/instagram/comments   | 400           | 52          | application/json |

  Scenario Outline: Get analytics data from API with missing parameters
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response contains <count> values
    And Data is owned by "<data_owner>"

    Examples: 
      | url                             | granularity | since      | until      | count | property                             | response_code | content_type     | data_owner |
      | /analytics/instagram/pictures   |             | 2015-12-03 | 2015-12-03 | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json | instagram  |
      | /analytics/instagram/engagement | day         |            | 2015-12-03 | 31    | 99999999-9999-4999-a999-999999999999 | 200           | application/json | instagram  |
      | /analytics/instagram/followers  | day         | 2015-12-03 |            | 17    | 99999999-9999-4999-a999-999999999999 | 200           | application/json | instagram  |
      | /analytics/instagram/tags       | day         |            |            | 31    | 99999999-9999-4999-a999-999999999999 | 200           | application/json | instagram  |
      | /analytics/instagram/reach      |             |            |            | 31    | 99999999-9999-4999-a999-999999999999 | 200           | application/json | instagram  |
      | /analytics/instagram/likes      |             | 2015-11-09 | 2015-11-02 | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json | instagram  |
      | /analytics/instagram/comments   |             | 2015-11-02 | 2015-12-02 | 31    | 99999999-9999-4999-a999-999999999999 | 200           | application/json | instagram  |

  Scenario Outline: Get analytics data from API from 1800s
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Custom code is "<custom_code>"

    #And Body contains entity with attribute "since" value "since>"
    #And Body contains entity with attribute "until" value "<until>"
    Examples: 
      | url                             | granularity | since      | until      | property                            | since      | until      | content_type     | response_code | custom_code |
      | /analytics/instagram            | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/pictures   | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/engagement | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/followers  | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/tags       | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/reach      | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/likes      | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/comments   | month       | 1888-09-01 | 1890-10-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram            | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/pictures   | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/engagement | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/followers  | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/tags       | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/reach      | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/likes      | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram/comments   | day         | 1888-09-01 | 1888-09-01 | 99999999-9999-4999-a999-99999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |

  Scenario Outline: Checking default parameter values
    Empty column in examples section means default value will be used for this parameter.
    if text is empty, returns null
    if text is date in ISO format (2015-01-01), it returns this date
    text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which will add or substract
    particular number of days/weeks/months from first part of expression

    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Response contains correct number of values for granularity "<granularity>" between "<expected_since>" and "<expected_until>"

    Examples: 
      | url                             | granularity | since          | until             | expected_granularity | expected_since    | expected_until | property                             | response_code | content_type     |
      | /analytics/instagram/pictures   |             |                |                   | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   | day         |                | today             | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   | day         | today          |                   | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   | week        |                | today             | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   | week        | today          |                   | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   | month       |                | today             | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   | month       | today          |                   | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   | month       | today          | today - 40 months | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/pictures   | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement |             |                |                   | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement | day         |                | today             | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement | day         | today          |                   | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement | week        |                | today             | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement | week        | today          |                   | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement | month       |                | today             | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement | month       | today          |                   | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement | month       | today          | today - 40 months | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/engagement | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  |             |                |                   | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  | day         |                | today             | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  | day         | today          |                   | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  | week        |                | today             | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  | week        | today          |                   | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  | month       |                | today             | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  | month       | today          |                   | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  | month       | today          | today - 40 months | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/followers  | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       |             |                |                   | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       | day         |                | today             | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       | day         | today          |                   | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       | week        |                | today             | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       | week        | today          |                   | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       | month       |                | today             | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       | month       | today          |                   | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       | month       | today          | today - 40 months | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/tags       | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      |             |                |                   | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      | day         |                | today             | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      | day         | today          |                   | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      | week        |                | today             | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      | week        | today          |                   | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      | month       |                | today             | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      | month       | today          |                   | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      | month       | today          | today - 40 months | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/reach      | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      |             |                |                   | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      | day         |                | today             | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      | day         | today          |                   | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      | week        |                | today             | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      | week        | today          |                   | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      | month       |                | today             | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      | month       | today          |                   | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      | month       | today          | today - 40 months | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/likes      | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   |             |                |                   | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   |             | 2015-12-03     | 2015-12-03        | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   | day         |                | today             | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   | day         | today          |                   | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   | week        |                | today             | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   | week        | today          |                   | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   | month       |                | today             | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   | month       | today          |                   | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   | day         | today          | today - 100 days  | day                  | today - 90 days   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   | week        | today          | today - 30 weeks  | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   | month       | today          | today - 40 months | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram/comments   | day         | today + 2 days | today + 3 days    | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |

  Scenario Outline: Checking number of values in response for various granularities
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Content type is "<content_type>"
    And Response code is <response_code>
    And Response contains <count> values

    Examples: 
      | url                             | granularity | since           | until | count | property                             | content_type     | response_code |
      | /analytics/instagram/pictures   | day         | today - 1 day   | today | 2     | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/engagement | day         | today - 6 days  | today | 7     | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/followers  | day         | today - 7 days  | today | 8     | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/tags       | day         | today - 8 days  | today | 9     | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/reach      | day         | today - 29 days | today | 30    | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/likes      | day         | today - 30 days | today | 31    | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram/comments   | week        | today - 13 days | today | 2     | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
