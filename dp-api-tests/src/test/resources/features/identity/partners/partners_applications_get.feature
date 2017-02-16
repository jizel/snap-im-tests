Feature: Partners applications get

  Background:
    Given Database is cleaned
    Given Default Snapshot user is created
    Given Default partner is created
    Given Default application is created

  # DP-1607 Missing endpoint for identity/partners/{{partner_id}}/applications
#  TODO: Wait until DP-1607 is resolved. If the endpoint is not implemented delete this feature file!

#  @Bug
#  Scenario: Getting partners applications
#
#    When Partners applications for partner with id "11111111-0000-4000-a000-222222222222" is got
#    Then Response code is "200"
#    And Content type is "application/json"
#    And Etag header is present
#    And Body contains entity with attribute "application_id"
#    And Body contains entity with attribute "application_name"
#
#  Scenario Outline: Getting list of partner applications
#    Given The following applications exist
#      | applicationName             | description                | website                    | applicationId                        | partnerId                            | isInternal |
#      | Application test company 1  | Application description 1  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace111 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 2  | Application description 2  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace112 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 3  | Application description 3  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace113 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 4  | Application description 4  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace114 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 5  | Application description 5  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace115 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 6  | Application description 6  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace116 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 7  | Application description 7  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace117 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 8  | Application description 8  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace118 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 9  | Application description 9  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace119 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 10 | Application description 10 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace120 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 11 | Application description 11 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace121 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 12 | Application description 12 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace122 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 13 | Application description 13 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 14 | Application description 14 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace124 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 15 | Application description 15 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace125 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 16 | Application description 16 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace126 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 17 | Application description 17 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace127 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 18 | Application description 18 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace128 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 19 | Application description 19 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace129 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 20 | Application description 20 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace130 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 21 | Application description 21 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace131 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 22 | Application description 22 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace132 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 23 | Application description 23 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace133 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 24 | Application description 24 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace134 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 25 | Application description 25 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace135 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 26 | Application description 26 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace136 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 27 | Application description 27 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace137 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 28 | Application description 28 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace138 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 29 | Application description 29 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace139 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 30 | Application description 30 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace140 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 31 | Application description 31 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace141 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 32 | Application description 32 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace142 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 33 | Application description 33 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace143 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 34 | Application description 34 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace144 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 35 | Application description 35 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace145 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 36 | Application description 36 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace146 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 37 | Application description 37 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace147 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 38 | Application description 38 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace148 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 39 | Application description 39 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace149 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 40 | Application description 40 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace150 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 41 | Application description 41 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace151 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 42 | Application description 42 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace152 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 43 | Application description 43 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace153 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 44 | Application description 44 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace154 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 45 | Application description 45 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace155 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 46 | Application description 46 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace156 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 47 | Application description 47 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace157 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 48 | Application description 48 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace158 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 49 | Application description 49 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace159 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 50 | Application description 50 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace160 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 51 | Application description 51 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace161 | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 52 | Application description 52 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace162 | 11111111-0000-4000-a000-222222222222 | false      |
#
#    When List of partner applications is got for partner with id "11111111-0000-4000-a000-222222222222" with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
#    Then Response code is "200"
#    And Content type is "application/json"
#    And There are <returned> partner applications returned
#    And Link header is '<link_header>'
#    And Total count is "<total>"
#    Examples:
#      | limit | cursor | returned | total | link_header                                                                                                                                                                                                 |
#      | /null |        | 50       | 52    | </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=50&cursor=50>; rel="next"                                                                                                       |
#      | /null | /null  | 50       | 52    | </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=50&cursor=50>; rel="next"                                                                                                       |
#      |       |        | 50       | 52    | </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=50&cursor=50>; rel="next"                                                                                                       |
#      |       | /null  | 50       | 52    | </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=50&cursor=50>; rel="next"                                                                                                       |
#      | 15    |        | 15       | 52    | </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=15&cursor=15>; rel="next"                                                                                                       |
#      |       | 1      | 50       | 52    | </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=50&cursor=51>; rel="next", </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=50&cursor=0>; rel="prev" |
#      | 20    | 0      | 20       | 52    | </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=20&cursor=20>; rel="next"                                                                                                       |
#      | 10    | 0      | 10       | 52    | </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=10&cursor=10>; rel="next"                                                                                                       |
#      | 5     | 10     | 5        | 52    | </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=5&cursor=15>; rel="next", </identity/partners/11111111-0000-4000-a000-222222222222/applications?limit=5&cursor=5>; rel="prev"   |
#
#  Scenario Outline: Checking error codes for getting list of partner applications
#    When List of partner applications is got for partner with id "11111111-0000-4000-a000-222222222222" with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
#    Then Response code is "<response_code>"
#    And Custom code is "<custom_code>"
#
#    Examples:
#      | limit       | cursor | filter | sort        | sort_desc   | response_code | custom_code |
#      | /null       | -1     | /null  | /null       | /null       | 400           | 63          |
#      |             | -1     | /null  | /null       | /null       | 400           | 63          |
#      | /null       | text   | /null  | /null       | /null       | 400           | 63          |
#      |             | text   | /null  | /null       | /null       | 400           | 63          |
#      | -1          |        | /null  | /null       | /null       | 400           | 63          |
#      | -1          | /null  | /null  | /null       | /null       | 400           | 63          |
#      | 201         | /null  | /null  | /null       | /null       | 400           | 63          |
#      | 21474836470 | /null  | /null  | /null       | /null       | 400           | 63          |
#      | text        |        | /null  | /null       | /null       | 400           | 63          |
#      | text        | /null  | /null  | /null       | /null       | 400           | 63          |
#      | 10          | -1     | /null  | /null       | /null       | 400           | 63          |
#      | text        | 0      | /null  | /null       | /null       | 400           | 63          |
#      | 10          | text   | /null  | /null       | /null       | 400           | 63          |
#      | 10          | 0      | /null  | notes       | notes       | 400           | 64          |
#      | 10          | 0      | /null  | /null       | nonexistent | 400           | 63          |
#      | 10          | 0      | /null  | nonexistent | /null       | 400           | 63          |
#      | 10          | 0      | code== | /null       | /null       | 400           | 63          |
#
#  Scenario Outline: Filtering list of partner applications
#    Given The following partner exist
#      | name           | email          | website                     | vatId      | notes        | partnerId                            |
#      | Company name 1 | p1@tenants.biz | http://www.snapshot1.travel | CZ10000001 | Test notes 1 | 11111111-0000-4000-a000-222222222222 |
#    Given The following applications exist
#      | applicationName             | description                | website                      | partnerId                            | isInternal |
#      | Application test company 1  | Application description 1  | http://www.snapshot1.travel  | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 2  | Application description 2  | http://www.snapshot2.travel  | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 3  | Application description 3  | http://www.snapshot3.travel  | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 4  | Application description 4  | http://www.snapshot4.travel  | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 5  | Application description 5  | http://www.snapshot5.travel  | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 6  | Application description 6  | http://www.snapshot6.travel  | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 7  | Application description 7  | http://www.snapshot7.travel  | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 8  | Application description 8  | http://www.snapshot8.travel  | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 9  | Application description 9  | http://www.snapshot9.travel  | 11111111-0000-4000-a000-222222222222 | false      |
#      | Application test company 10 | Application description 10 | http://www.snapshot10.travel | 11111111-0000-4000-a000-222222222222 | false      |
#    When List of partner applications is got for partner with id "11111111-0000-4000-a000-222222222222" with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
#    Then Response code is "200"
#    And Content type is "application/json"
#    And There are <returned> partners returned
#    And There are partners with following names returned in order: <expected_names>
#    And Total count is "<total>"
#
#    Examples:
#      | limit | cursor | returned | total | filter                                | sort             | sort_desc        | expected_names                                                                                                                              |
#      | 5     | 0      | 5        | 10    | application_name=='Application test*' | application_name |                  | Application test company 1, Application test company 10, Application test company 2, Application test company 3, Application test company 4 |
#      | 5     | 0      | 5        | 10    | application_name=='Application test*' |                  | application_name | Application test company 9, Application test company 8, Application test company 7, Application test company 6, Application test company 5  |
#      | 5     | 2      | 5        | 10    | application_name=='Application test*' | application_name |                  | Application test company 2, Application test company 3, Application test company 4, Application test company 5, Application test company 6  |
#      | 5     | 2      | 5        | 10    | application_name=='Application test*' |                  | application_name | Application test company 7, Application test company 6, Application test company 5,Application test company 4, Application test company 3   |
#      | 5     | 3      | 5        | 10    | application_name=='Application test*' | application_name |                  | Application test company 3, Application test company 4, Application test company 5, Application test company 6,Application test company 7   |