Feature: Applications commercial subscriptions

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following properties exist with random address and billing address
      | propertyId                          | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      |c37c3501-d309-4702-ad0b-fd53a98c01fd | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |


  Scenario: Getting applications commercial subscriptions
    Given The following commercial subscriptions exist
      | applicationId                        | id                                   | propertyId                           |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | c37c3501-d309-4702-ad0b-fd53a98c01fd |
    When Applications commercial subscriptions for application id "11111111-0000-4000-a000-111111111111" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "commercial_subscription_id"

  Scenario Outline: Getting list of applications commercial subscriptions
    Given The following commercial subscriptions exist
      | applicationId                        | id                                   | propertyId                          |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
      | 11111111-0000-4000-a000-111111111111 | 1238fd9a-a05d-42d8-8e84-42e904ace123 |c37c3501-d309-4702-ad0b-fd53a98c01fd |
    When List of application commercial subscriptions is got for application with id "11111111-0000-4000-a000-111111111111" and limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> applications commercial subscriptions returned
    And Link header is '<link_header>'
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | link_header                                                                                                                                                                                                                                 |
      | /null |        | 50       | 52    | </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=50&cursor=50>; rel="next"                                                                                                                       |
      | /null | /null  | 50       | 52    | </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=50&cursor=50>; rel="next"                                                                                                                       |
      |       |        | 50       | 52    | </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=50&cursor=50>; rel="next"                                                                                                                       |
      |       | /null  | 50       | 52    | </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=50&cursor=50>; rel="next"                                                                                                                       |
      | 15    |        | 15       | 52    | </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=15&cursor=15>; rel="next"                                                                                                                       |
      |       | 1      | 50       | 52    | </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=50&cursor=0>; rel="prev", </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=50&cursor=51>; rel="next" |
      | 20    | 0      | 20       | 52    | </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=20&cursor=20>; rel="next"                                                                                                                       |
      | 10    | 0      | 10       | 52    | </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=10&cursor=10>; rel="next"                                                                                                                       |
      | 5     | 10     | 5        | 52    | </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=5&cursor=5>; rel="prev", </identity/applications/11111111-0000-4000-a000-111111111111/commercial_subscriptions?limit=5&cursor=15>; rel="next"   |

  Scenario Outline: Checking error codes for getting list of applications commercial subscriptions
    When List of application commercial subscriptions is got for application with id "11111111-0000-4000-a000-111111111111" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter | sort                       | sort_desc                  | response_code | custom_code |
      | /null       | -1     | /null  | /null                      | /null                      | 400           | 40002       |
      |             | -1     | /null  | /null                      | /null                      | 400           | 40002       |
      | /null       | text   | /null  | /null                      | /null                      | 400           | 40002       |
      |             | text   | /null  | /null                      | /null                      | 400           | 40002       |
      | -1          |        | /null  | /null                      | /null                      | 400           | 40002       |
      | -1          | /null  | /null  | /null                      | /null                      | 400           | 40002       |
      | 201         | /null  | /null  | /null                      | /null                      | 400           | 40002       |
      | 21474836470 | /null  | /null  | /null                      | /null                      | 400           | 40002       |
      | text        |        | /null  | /null                      | /null                      | 400           | 40002       |
      | text        | /null  | /null  | /null                      | /null                      | 400           | 40002       |
      | 10          | -1     | /null  | /null                      | /null                      | 400           | 40002       |
      | text        | 0      | /null  | /null                      | /null                      | 400           | 40002       |
      | 10          | text   | /null  | /null                      | /null                      | 400           | 40002       |
      | 10          | 0      | /null  | commercial_subscription_id | commercial_subscription_id | 400           | 40002       |
      | 10          | 0      | /null  | /null                      | nonexistent                | 400           | 40002       |
      | 10          | 0      | /null  | nonexistent                | /null                      | 400           | 40002       |
      | 10          | 0      | code== | /null                      | /null                      | 400           | 40002       |
