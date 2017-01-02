Feature: Applications commercial subscriptions

  Background:
    Given Database is cleaned

  Scenario: Getting applications commercial subscriptions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        | partnerId                            |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      | customerId                           |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Commercial subscription is created
      | status | startDate  | endDate    | applicationId                        | customerId                           | referenceId | commercialSubscriptionId             |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a9728eb |
    When Applications commercial subscriptions for application id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Body contains entity with attribute "commercial_subscription_id"

  Scenario Outline: Getting list of applications commercial subscriptions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        | partnerId                            |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      | customerId                           |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given Commercial subscription is created
      | status | startDate  | endDate    | applicationId                        | customerId                           | referenceId | commercialSubscriptionId             |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a9728e1 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a9728e2 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a9728e3 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a9728e4 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a9728e5 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a9728e6 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a9728e7 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a9728e8 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a9728e9 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972810 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972811 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972812 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972813 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972814 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972815 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972816 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972817 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972818 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972819 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972820 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972821 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972822 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972823 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972824 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972825 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972826 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972827 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972828 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972829 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972830 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972831 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972832 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972833 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972834 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972835 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972836 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972837 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972838 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972839 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972840 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972841 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972842 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972843 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972844 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972845 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972846 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972847 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972848 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972849 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972850 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972851 |
      | active | 2016-01-01 | 2016-12-31 | a318fd9a-a05d-42d8-8e84-42e904ace123 | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 123abc123   | 8e238f8e-2c9c-4e32-9a63-40474a972852 |
    When List of application commercial subscriptions is got for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" and limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> applications commercial subscriptions returned
    And Link header is '<link_header>'
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | link_header                                                                                                                                                                                                                                 |
      | /null |        | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=50&cursor=50>; rel="next"                                                                                                                       |
      | /null | /null  | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=50&cursor=50>; rel="next"                                                                                                                       |
      |       |        | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=50&cursor=50>; rel="next"                                                                                                                       |
      |       | /null  | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=50&cursor=50>; rel="next"                                                                                                                       |
      | 15    |        | 15       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=15&cursor=15>; rel="next"                                                                                                                       |
      |       | 1      | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=50&cursor=51>; rel="next", </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=50&cursor=0>; rel="prev" |
      | 20    | 0      | 20       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=20&cursor=20>; rel="next"                                                                                                                       |
      | 10    | 0      | 10       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=10&cursor=10>; rel="next"                                                                                                                       |
      | 5     | 10     | 5        | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=5&cursor=15>; rel="next", </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/commercial_subscriptions?limit=5&cursor=5>; rel="prev"   |

  Scenario Outline: Checking error codes for getting list of applications commercial subscriptions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        | partnerId                            |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    When List of application commercial subscriptions is got for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" and limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter | sort                       | sort_desc                  | response_code | custom_code |
      | /null       | -1     | /null  | /null                      | /null                      | 400           | 63          |
      |             | -1     | /null  | /null                      | /null                      | 400           | 63          |
      | /null       | text   | /null  | /null                      | /null                      | 400           | 63          |
      |             | text   | /null  | /null                      | /null                      | 400           | 63          |
      | -1          |        | /null  | /null                      | /null                      | 400           | 63          |
      | -1          | /null  | /null  | /null                      | /null                      | 400           | 63          |
      | 201         | /null  | /null  | /null                      | /null                      | 400           | 63          |
      | 21474836470 | /null  | /null  | /null                      | /null                      | 400           | 63          |
      | text        |        | /null  | /null                      | /null                      | 400           | 63          |
      | text        | /null  | /null  | /null                      | /null                      | 400           | 63          |
      | 10          | -1     | /null  | /null                      | /null                      | 400           | 63          |
      | text        | 0      | /null  | /null                      | /null                      | 400           | 63          |
      | 10          | text   | /null  | /null                      | /null                      | 400           | 63          |
      | 10          | 0      | /null  | commercial_subscription_id | commercial_subscription_id | 400           | 64          |
      | 10          | 0      | /null  | /null                      | nonexistent                | 400           | 63          |
      | 10          | 0      | /null  | nonexistent                | /null                      | 400           | 63          |
      | 10          | 0      | code== | /null                      | /null                      | 400           | 63          |
