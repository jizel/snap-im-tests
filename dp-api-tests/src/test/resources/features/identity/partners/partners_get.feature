Feature: Partners get

  Background:
    Given Database is cleaned and default entities are created

#
#

  Scenario: Getting partner
    Given The following partner is created
      | name           | email          | website                    | vatId      | notes        | id                                   |
      | Partner name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    When Partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "partner_id"
    And Body contains entity with attribute "name" value "Partner name 1"
    And Body contains entity with attribute "email" value "p1@tenants.biz"
    And Body contains entity with attribute "website" value "http://www.snapshot.travel"
    And Body contains entity with attribute "vat_id" value "CZ10000001"
    And Body contains entity with attribute "notes" value "Test notes 1"
    And Body contains entity with attribute "is_active" value "false"

  Scenario: Getting partner with etag
    Given The following partner is created
      | name           | email          | website                    | vatId      | notes        | id                                   |
      | Company name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    When Partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is got with etag
    Then Response code is "200"


  Scenario: Checking error code for nonexistent partner
    When Nonexistent partner id is got
    Then Response code is "404"
    And Custom code is "40402"

  Scenario Outline: Getting list of partners
    Given The following partner exist
      | name            | email           | website                    | vatId      | notes         | id                                   |
      | Company name 1  | p1@tenants.biz  | http://www.snapshot.travel | CZ10000001 | Test notes 1  | abc8fd9a-a05d-42d8-8e84-42e904ace101 |
      | Company name 2  | p2@tenants.biz  | http://www.snapshot.travel | CZ10000001 | Test notes 2  | abc8fd9a-a05d-42d8-8e84-42e904ace102 |
      | Company name 3  | p3@tenants.biz  | http://www.snapshot.travel | CZ10000001 | Test notes 3  | abc8fd9a-a05d-42d8-8e84-42e904ace103 |
      | Company name 4  | p4@tenants.biz  | http://www.snapshot.travel | CZ10000001 | Test notes 4  | abc8fd9a-a05d-42d8-8e84-42e904ace104 |
      | Company name 5  | p5@tenants.biz  | http://www.snapshot.travel | CZ10000001 | Test notes 5  | abc8fd9a-a05d-42d8-8e84-42e904ace105 |
      | Company name 6  | p6@tenants.biz  | http://www.snapshot.travel | CZ10000001 | Test notes 6  | abc8fd9a-a05d-42d8-8e84-42e904ace106 |
      | Company name 7  | p7@tenants.biz  | http://www.snapshot.travel | CZ10000001 | Test notes 7  | abc8fd9a-a05d-42d8-8e84-42e904ace107 |
      | Company name 8  | p8@tenants.biz  | http://www.snapshot.travel | CZ10000001 | Test notes 8  | abc8fd9a-a05d-42d8-8e84-42e904ace108 |
      | Company name 9  | p9@tenants.biz  | http://www.snapshot.travel | CZ10000001 | Test notes 9  | abc8fd9a-a05d-42d8-8e84-42e904ace109 |
      | Company name 10 | p10@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 10 | abc8fd9a-a05d-42d8-8e84-42e904ace110 |
      | Company name 11 | p11@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 11 | abc8fd9a-a05d-42d8-8e84-42e904ace111 |
      | Company name 12 | p12@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 12 | abc8fd9a-a05d-42d8-8e84-42e904ace112 |
      | Company name 13 | p13@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 13 | abc8fd9a-a05d-42d8-8e84-42e904ace113 |
      | Company name 14 | p14@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 14 | abc8fd9a-a05d-42d8-8e84-42e904ace114 |
      | Company name 15 | p15@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 15 | abc8fd9a-a05d-42d8-8e84-42e904ace115 |
      | Company name 16 | p16@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 16 | abc8fd9a-a05d-42d8-8e84-42e904ace116 |
      | Company name 17 | p17@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 17 | abc8fd9a-a05d-42d8-8e84-42e904ace117 |
      | Company name 18 | p18@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 18 | abc8fd9a-a05d-42d8-8e84-42e904ace118 |
      | Company name 19 | p19@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 19 | abc8fd9a-a05d-42d8-8e84-42e904ace119 |
      | Company name 20 | p20@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 20 | abc8fd9a-a05d-42d8-8e84-42e904ace120 |
      | Company name 21 | p21@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 21 | abc8fd9a-a05d-42d8-8e84-42e904ace121 |
      | Company name 22 | p22@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 22 | abc8fd9a-a05d-42d8-8e84-42e904ace122 |
      | Company name 23 | p23@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 23 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
      | Company name 24 | p24@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 24 | abc8fd9a-a05d-42d8-8e84-42e904ace124 |
      | Company name 25 | p25@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 25 | abc8fd9a-a05d-42d8-8e84-42e904ace125 |
      | Company name 26 | p26@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 26 | abc8fd9a-a05d-42d8-8e84-42e904ace126 |
      | Company name 27 | p27@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 27 | abc8fd9a-a05d-42d8-8e84-42e904ace127 |
      | Company name 28 | p28@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 28 | abc8fd9a-a05d-42d8-8e84-42e904ace128 |
      | Company name 29 | p29@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 29 | abc8fd9a-a05d-42d8-8e84-42e904ace129 |
      | Company name 30 | p30@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 30 | abc8fd9a-a05d-42d8-8e84-42e904ace130 |
      | Company name 31 | p31@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 31 | abc8fd9a-a05d-42d8-8e84-42e904ace131 |
      | Company name 32 | p32@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 32 | abc8fd9a-a05d-42d8-8e84-42e904ace132 |
      | Company name 33 | p33@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 33 | abc8fd9a-a05d-42d8-8e84-42e904ace133 |
      | Company name 34 | p34@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 34 | abc8fd9a-a05d-42d8-8e84-42e904ace134 |
      | Company name 35 | p35@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 35 | abc8fd9a-a05d-42d8-8e84-42e904ace135 |
      | Company name 36 | p36@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 36 | abc8fd9a-a05d-42d8-8e84-42e904ace136 |
      | Company name 37 | p37@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 37 | abc8fd9a-a05d-42d8-8e84-42e904ace137 |
      | Company name 38 | p38@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 38 | abc8fd9a-a05d-42d8-8e84-42e904ace138 |
      | Company name 39 | p39@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 39 | abc8fd9a-a05d-42d8-8e84-42e904ace139 |
      | Company name 40 | p40@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 40 | abc8fd9a-a05d-42d8-8e84-42e904ace140 |
      | Company name 41 | p41@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 41 | abc8fd9a-a05d-42d8-8e84-42e904ace141 |
      | Company name 42 | p42@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 42 | abc8fd9a-a05d-42d8-8e84-42e904ace142 |
      | Company name 43 | p43@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 43 | abc8fd9a-a05d-42d8-8e84-42e904ace143 |
      | Company name 44 | p44@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 44 | abc8fd9a-a05d-42d8-8e84-42e904ace144 |
      | Company name 45 | p45@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 45 | abc8fd9a-a05d-42d8-8e84-42e904ace145 |
      | Company name 46 | p46@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 46 | abc8fd9a-a05d-42d8-8e84-42e904ace146 |
      | Company name 47 | p47@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 47 | abc8fd9a-a05d-42d8-8e84-42e904ace147 |
      | Company name 48 | p48@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 48 | abc8fd9a-a05d-42d8-8e84-42e904ace148 |
      | Company name 49 | p49@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 49 | abc8fd9a-a05d-42d8-8e84-42e904ace149 |
      | Company name 50 | p50@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 50 | abc8fd9a-a05d-42d8-8e84-42e904ace150 |
      | Company name 51 | p51@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 51 | abc8fd9a-a05d-42d8-8e84-42e904ace151 |
      | Company name 52 | p52@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 52 | abc8fd9a-a05d-42d8-8e84-42e904ace152 |

    When List of partners is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> partners returned
    And Link header is '<link_header>'
    And Total count is "<total>"
    Examples:
      | limit | cursor | returned | total | link_header                                                                                             |
      | /null |        | 50       | 52    | </identity/partners?limit=50&cursor=50>; rel="next"                                                     |
      | /null | /null  | 50       | 52    | </identity/partners?limit=50&cursor=50>; rel="next"                                                     |
      |       |        | 50       | 52    | </identity/partners?limit=50&cursor=50>; rel="next"                                                     |
      |       | /null  | 50       | 52    | </identity/partners?limit=50&cursor=50>; rel="next"                                                     |
      | 15    |        | 15       | 52    | </identity/partners?limit=15&cursor=15>; rel="next"                                                     |
      |       | 1      | 50       | 52    | </identity/partners?limit=50&cursor=0>; rel="prev", </identity/partners?limit=50&cursor=51>; rel="next" |
      | 20    | 0      | 20       | 52    | </identity/partners?limit=20&cursor=20>; rel="next"                                                     |
      | 10    | 0      | 10       | 52    | </identity/partners?limit=10&cursor=10>; rel="next"                                                     |
      | 5     | 10     | 5        | 52    | </identity/partners?limit=5&cursor=5>; rel="prev", </identity/partners?limit=5&cursor=15>; rel="next"   |

  Scenario Outline: Checking error codes for getting list of partners
    When List of partners is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
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
      | 10          | 0      | /null  | notes       | notes       | 400           | 40002       |
      | 10          | 0      | /null  | /null       | nonexistent | 400           | 40002       |
      | 10          | 0      | /null  | nonexistent | /null       | 400           | 40002       |
      | 10          | 0      | code== | /null       | /null       | 400           | 40002       |

  Scenario Outline: Filtering list of partners
    Given The following partner exist
      | name            | email           | website                      | vatId      | notes         | id                                   |
      | Company name 1  | p1@tenants.biz  | http://www.snapshot1.travel  | CZ10000001 | Test notes 1  | abc8fd9a-a05d-42d8-8e84-42e904ace101 |
      | Company name 2  | p2@tenants.biz  | http://www.snapshot2.travel  | CZ10000001 | Test notes 2  | abc8fd9a-a05d-42d8-8e84-42e904ace102 |
      | Company name 3  | p3@tenants.biz  | http://www.snapshot3.travel  | CZ10000001 | Test notes 3  | abc8fd9a-a05d-42d8-8e84-42e904ace103 |
      | Company name 4  | p4@tenants.biz  | http://www.snapshot4.travel  | CZ10000001 | Test notes 4  | abc8fd9a-a05d-42d8-8e84-42e904ace104 |
      | Company name 5  | p5@tenants.biz  | http://www.snapshot5.travel  | CZ10000001 | Test notes 5  | abc8fd9a-a05d-42d8-8e84-42e904ace105 |
      | Company name 6  | p6@tenants.biz  | http://www.snapshot6.travel  | CZ10000001 | Test notes 6  | abc8fd9a-a05d-42d8-8e84-42e904ace106 |
      | Company name 7  | p7@tenants.biz  | http://www.snapshot7.travel  | CZ10000001 | Test notes 7  | abc8fd9a-a05d-42d8-8e84-42e904ace107 |
      | Company name 8  | p8@tenants.biz  | http://www.snapshot8.travel  | CZ10000001 | Test notes 8  | abc8fd9a-a05d-42d8-8e84-42e904ace108 |
      | Company name 9  | p9@tenants.biz  | http://www.snapshot9.travel  | CZ10000001 | Test notes 9  | abc8fd9a-a05d-42d8-8e84-42e904ace109 |
      | Company name 10 | p10@tenants.biz | http://www.snapshot10.travel | CZ10000001 | Test notes 10 | abc8fd9a-a05d-42d8-8e84-42e904ace110 |
    When List of partners is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> partners returned
    And There are partners with following names returned in order: <expected_names>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                                      | sort  | sort_desc | expected_names                                                                  |
      | 5     | 0      | 5        | 10    | name=='Company name*'                       | notes |           | Company name 1, Company name 10, Company name 2, Company name 3, Company name 4 |
      | 5     | 0      | 5        | 10    | name=='Company name*'                       |       | notes     | Company name 9, Company name 8, Company name 7, Company name 6, Company name 5  |
      | 5     | 2      | 5        | 10    | name=='Company name*'                       | notes |           | Company name 2, Company name 3, Company name 4, Company name 5, Company name 6  |
      | 5     | 2      | 5        | 10    | name=='Company name*'                       |       | notes     | Company name 7, Company name 6, Company name 5, Company name 4, Company name 3  |
      | 5     | 3      | 5        | 10    | name=='Company name*'                       | notes | /null     | Company name 3, Company name 4, Company name 5, Company name 6,Company name 7   |
      | /null | /null  | 1        | 1     | notes=='*notes 2'                           | /null | /null     | Company name 2                                                                  |
      | /null | /null  | 1        | 1     | website==http://www.snapshot8.travel        | /null | /null     | Company name 8                                                                  |
      | /null | /null  | 1        | 1     | name=='Company name*' and notes=='*notes 2' | /null | /null     | Company name 2                                                                  |
