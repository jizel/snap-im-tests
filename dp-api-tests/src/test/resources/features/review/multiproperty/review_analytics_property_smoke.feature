Feature: Review multiproperty customer property
  #TODO add paging tests - currently paging is only prepared but not implemented
  #TODO request with invalid access token ?

  Background:
    # 5 property, 1 customer, 1 user, with all needed relations
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | name            | email          | salesforceId         | vatId      | isDemo         | phone         | website                    | timezone          |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "true"
      | id                                   | type     | username     | firstName | lastName | email                | timezone      | languageCode |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | customer | snapshotUser | Snapshot  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | id                                   | salesforceId   | name         | code         | website                    | email          | isDemo         | timezone      | anchorCustomerId                     |
      | 99000199-9999-4999-a999-999999999999 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | 99000299-9999-4999-a999-999999999999 | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      | 99000399-9999-4999-a999-999999999999 | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Relation between user "snapshotUser" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with isPrimary "true"
    Given Relation between property with code "p1_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p2_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2016-12-31"
    Given Relation between property with code "p3_code" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "owner" from "2015-01-01" to "2016-12-31"

  @Smoke
  Scenario: Checking data correctness for popularity_index_rank and overall_bubble_rating
    When Get "popularity_index_rank" for list of properties for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" with since "2015-08-26" until "2015-12-03" granularity "month" limit "/null" and cursor "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "/multiproperty/customer/popularity_index_month.json" equals to previous response for popularity index
    When Get "overall_bubble_rating" for single property "99000199-9999-4999-a999-999999999999" with since "2015-12-03" until "2015-12-03" and granularity "day"
    Then Response code is "200"
    And Content type is "application/json"
    And Review file "/multiproperty/property/bubble_for_day.json" equals to previous response for overall bubble rating

