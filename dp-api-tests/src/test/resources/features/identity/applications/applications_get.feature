@Identity
Feature: Applications get

  Background:
    Given Database is cleaned and default entities are created

  Scenario: Checking error code for nonexistent applications
    When Nonexistent application id is got
    Then Response code is "404"
    And Custom code is "40402"

  Scenario Outline: Getting list of applications
    Given The following applications exist
      | name                        | description                | website                    | Id                                   | partnerId                            |
      | Application test company 1  | Application description 1  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace111 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 2  | Application description 2  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace112 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 3  | Application description 3  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace113 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 4  | Application description 4  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace114 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 5  | Application description 5  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace115 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 6  | Application description 6  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace116 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 7  | Application description 7  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace117 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 8  | Application description 8  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace118 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 9  | Application description 9  | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace119 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 10 | Application description 10 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace120 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 11 | Application description 11 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace121 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 12 | Application description 12 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace122 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 13 | Application description 13 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 14 | Application description 14 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace124 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 15 | Application description 15 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace125 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 16 | Application description 16 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace126 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 17 | Application description 17 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace127 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 18 | Application description 18 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace128 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 19 | Application description 19 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace129 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 20 | Application description 20 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace130 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 21 | Application description 21 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace131 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 22 | Application description 22 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace132 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 23 | Application description 23 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace133 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 24 | Application description 24 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace134 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 25 | Application description 25 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace135 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 26 | Application description 26 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace136 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 27 | Application description 27 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace137 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 28 | Application description 28 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace138 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 29 | Application description 29 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace139 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 30 | Application description 30 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace140 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 31 | Application description 31 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace141 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 32 | Application description 32 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace142 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 33 | Application description 33 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace143 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 34 | Application description 34 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace144 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 35 | Application description 35 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace145 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 36 | Application description 36 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace146 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 37 | Application description 37 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace147 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 38 | Application description 38 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace148 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 39 | Application description 39 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace149 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 40 | Application description 40 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace150 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 41 | Application description 41 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace151 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 42 | Application description 42 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace152 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 43 | Application description 43 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace153 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 44 | Application description 44 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace154 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 45 | Application description 45 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace155 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 46 | Application description 46 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace156 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 47 | Application description 47 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace157 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 48 | Application description 48 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace158 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 49 | Application description 49 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace159 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 50 | Application description 50 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace160 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 51 | Application description 51 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace161 | 07000000-0000-4444-8888-000000000002 |
      | Application test company 52 | Application description 52 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace162 | 07000000-0000-4444-8888-000000000002 |

    When List of applications is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> applications returned
    And Link header is '<link_header>'
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | link_header                                                                                                     |
      | /null |        | 50       | 53    | </identity/applications?limit=50&cursor=50>; rel="next"                                                         |
      | /null | /null  | 50       | 53    | </identity/applications?limit=50&cursor=50>; rel="next"                                                         |
      |       |        | 50       | 53    | </identity/applications?limit=50&cursor=50>; rel="next"                                                         |
      |       | /null  | 50       | 53    | </identity/applications?limit=50&cursor=50>; rel="next"                                                         |
      | 15    |        | 15       | 53    | </identity/applications?limit=15&cursor=15>; rel="next"                                                         |
      |       | 1      | 50       | 53    | </identity/applications?limit=50&cursor=0>; rel="prev", </identity/applications?limit=50&cursor=51>; rel="next" |
      | 30    | 0      | 30       | 53    | </identity/applications?limit=30&cursor=30>; rel="next"                                                         |
      | 10    | 0      | 10       | 53    | </identity/applications?limit=10&cursor=10>; rel="next"                                                         |
      | 5     | 10     | 5        | 53    | </identity/applications?limit=5&cursor=5>; rel="prev", </identity/applications?limit=5&cursor=15>; rel="next"   |

  Scenario Outline: Checking error codes for getting list of applications
    When List of applications is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter | sort        | sort_desc   | response_code | custom_code |
      | /null       | -1     | /null  | /null       | /null       | 400           | 40002       |
      |             | -1     | /null  | /null       | /null       | 400           | 40002       |
      | /null       | text   | /null  | /null       | /null       | 400           | 40002       |
      |             | text   | /null  | /null       | /null       | 400           | 40002       |
      | -1          |        | /null  | /null       | /null       | 400           | 40002       |
      | -1          | /null  | /null  | /null       | /null       | 400           | 40002       |
      | 201         | /null  | /null  | /null       | /null       | 400           | 40002       |
      | 21474836470 | /null  | /null  | /null       | /null       | 400           | 40002       |
      | text        |        | /null  | /null       | /null       | 400           | 40002       |
      | text        | /null  | /null  | /null       | /null       | 400           | 40002       |
      | 10          | -1     | /null  | /null       | /null       | 400           | 40002       |
      | text        | 0      | /null  | /null       | /null       | 400           | 40002       |
      | 10          | text   | /null  | /null       | /null       | 400           | 40002       |
      | 10          | 0      | /null  | description | description | 400           | 40002       |
      | 10          | 0      | /null  | /null       | nonexistent | 400           | 40002       |
      | 10          | 0      | /null  | nonexistent | /null       | 400           | 40002       |
      | 10          | 0      | code == | /null       | /null       | 400           | 40002       |

  Scenario Outline: Filtering list of applications
    Given The following applications exist
      | name                        | description                | website                      |
      | Application test company 1  | Application description 1  | http://www.snapshot1.travel  |
      | Application test company 2  | Application description 2  | http://www.snapshot2.travel  |
      | Application test company 3  | Application description 3  | http://www.snapshot3.travel  |
      | Application test company 4  | Application description 4  | http://www.snapshot4.travel  |
      | Application test company 5  | Application description 5  | http://www.snapshot5.travel  |
      | Application test company 6  | Application description 6  | http://www.snapshot6.travel  |
      | Application test company 7  | Application description 7  | http://www.snapshot7.travel  |
      | Application test company 8  | Application description 8  | http://www.snapshot8.travel  |
      | Application test company 9  | Application description 9  | http://www.snapshot9.travel  |
      | Application test company 10 | Application description 10 | http://www.snapshot10.travel |
    When List of applications is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> applications returned
    And There are applications with following names returned in order: <expected_names>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                                                      | sort        | sort_desc   | expected_names                                                                                                                              |
      | 5     | 0      | 5        | 10    | name=='Application test*'                                   | description |             | Application test company 1, Application test company 10, Application test company 2, Application test company 3, Application test company 4 |
      | 5     | 0      | 5        | 10    | name=='Application test*'                                   |             | description | Application test company 9, Application test company 8, Application test company 7, Application test company 6, Application test company 5  |
      | 5     | 2      | 5        | 10    | name=='Application test*'                                   | description |             | Application test company 2, Application test company 3, Application test company 4, Application test company 5, Application test company 6  |
      | 5     | 2      | 5        | 10    | name=='Application test*'                                   |             | description | Application test company 7, Application test company 6, Application test company 5,Application test company 4, Application test company 3   |
      | 5     | 3      | 5        | 10    | name=='Application test*'                                   | description | /null       | Application test company 3, Application test company 4, Application test company 5, Application test company 6,Application test company 7   |
      | /null | /null  | 1        | 1     | description=='*description 2'                               | /null       | /null       | Application test company 2                                                                                                                  |
      | /null | /null  | 1        | 1     | website==http://www.snapshot8.travel                        | /null       | /null       | Application test company 8                                                                                                                  |
      | /null | /null  | 1        | 1     | name=='Application test*' and description=='*description 2' | /null       | /null       | Application test company 2                                                                                                                  |
