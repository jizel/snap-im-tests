@Identity
Feature: Users get

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName  | email                    | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 728c45dd-a964-4f78-afe0-88d108c682ed | UserCustomer | userCustomer@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "728c45dd-a964-4f78-afe0-88d108c682ed" as primary "false"
      | userType | userName | firstName | lastName | email                | timezone      | culture |
      | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | default2 | Default2  | User1    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | default3 | Default3  | User3    | def3@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | default4 | Default4  | User4    | def4@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The password of user "default3" is "Password01"

  @Smoke
  Scenario: Getting user
    When User with username "default1" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "user_id"
    And Body contains entity with attribute "user_type" value "customer"
    And Body contains entity with attribute "user_name" value "default1"
    And Body contains entity with attribute "first_name" value "Default1"
    And Body contains entity with attribute "last_name" value "User1"
    And Body contains entity with attribute "email" value "def1@snapshot.travel"
    And Body contains entity with attribute "timezone" value "Europe/Prague"
    And Body contains entity with attribute "culture" value "cs-CZ"

  Scenario: Getting user with etag
    When User with username "default1" is got with etag
    Then Response code is "304"
    And Body is empty

  Scenario: Get token for deleted user
    Given The password of user "default4" is "Password01"
    Given User with userName "default4" is deleted
    When Get token for user "default4" with password "Password01"
    Then Response code is 401

  Scenario Outline: Checking error codes for work with tokens
    When Get token for user "<username>" with password "<password>"
    Then Response code is "<response_code>"
    Examples:
      | username | password         | response_code |
      | default3 | Password01       | 200           |
      | default3 | NonValidPassword | 401           |
      | default4 |                  | 401           |
      |          | NonExistingUser  | 401           |
      |          |                  | 401           |

  Scenario: Getting user with not current etag
    When User with username "default1" is got for etag, updated and got with previous etag
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "user_id"
    And Body contains entity with attribute "user_type" value "customer"
    And Body contains entity with attribute "user_name" value "default1"
    And Body contains entity with attribute "first_name" value "Default1"
    And Body contains entity with attribute "last_name" value "User1"
    And Body contains entity with attribute "email" value "def1@snapshot.travel"
    And Body contains entity with attribute "timezone" value "Europe/Prague"
    And Body contains entity with attribute "culture" value "sk-SK"

  Scenario: Checking error code for getting user
    When Nonexistent user id is got
    Then Response code is "404"
    And Custom code is "40402"

  Scenario Outline: Getting list of users
    Given The following users exist for customer "728c45dd-a964-4f78-afe0-88d108c682ed" as primary "false"
      | userType | userName        | firstName     | lastName   | email                       | timezone      | culture |
      | customer | list_default_1  | ListDefault1  | ListUser1  | list_user1@snapshot.travel  | Europe/Prague | cs-CZ   |
      | customer | list_default_2  | ListDefault2  | ListUser2  | list_user2@snapshot.travel  | Europe/Prague | cs-CZ   |
      | customer | list_default_3  | ListDefault3  | ListUser3  | list_user3@snapshot.travel  | Europe/Prague | cs-CZ   |
      | customer | list_default_4  | ListDefault4  | ListUser4  | list_user4@snapshot.travel  | Europe/Prague | cs-CZ   |
      | customer | list_default_5  | ListDefault5  | ListUser5  | list_user5@snapshot.travel  | Europe/Prague | cs-CZ   |
      | customer | list_default_6  | ListDefault6  | ListUser6  | list_user6@snapshot.travel  | Europe/Prague | cs-CZ   |
      | customer | list_default_7  | ListDefault7  | ListUser7  | list_user7@snapshot.travel  | Europe/Prague | cs-CZ   |
      | customer | list_default_8  | ListDefault8  | ListUser8  | list_user8@snapshot.travel  | Europe/Prague | cs-CZ   |
      | customer | list_default_9  | ListDefault9  | ListUser9  | list_user9@snapshot.travel  | Europe/Prague | cs-CZ   |
      | customer | list_default_10 | ListDefault10 | ListUser10 | list_user10@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_11 | ListDefault11 | ListUser11 | list_user11@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_12 | ListDefault12 | ListUser12 | list_user12@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_13 | ListDefault13 | ListUser13 | list_user13@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_14 | ListDefault14 | ListUser14 | list_user14@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_15 | ListDefault15 | ListUser15 | list_user15@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_16 | ListDefault16 | ListUser16 | list_user16@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_17 | ListDefault17 | ListUser17 | list_user17@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_18 | ListDefault18 | ListUser18 | list_user18@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_19 | ListDefault19 | ListUser19 | list_user19@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_20 | ListDefault20 | ListUser20 | list_user20@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_21 | ListDefault21 | ListUser21 | list_user21@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_22 | ListDefault22 | ListUser22 | list_user22@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_23 | ListDefault23 | ListUser23 | list_user23@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_24 | ListDefault24 | ListUser24 | list_user24@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_25 | ListDefault25 | ListUser25 | list_user25@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_26 | ListDefault26 | ListUser26 | list_user26@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_27 | ListDefault27 | ListUser27 | list_user27@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_28 | ListDefault28 | ListUser28 | list_user28@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_29 | ListDefault29 | ListUser29 | list_user29@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_30 | ListDefault30 | ListUser30 | list_user30@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_31 | ListDefault31 | ListUser31 | list_user31@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_32 | ListDefault32 | ListUser32 | list_user32@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_33 | ListDefault33 | ListUser33 | list_user33@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_34 | ListDefault34 | ListUser34 | list_user34@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_35 | ListDefault35 | ListUser35 | list_user35@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_36 | ListDefault36 | ListUser36 | list_user36@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_37 | ListDefault37 | ListUser37 | list_user37@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_38 | ListDefault38 | ListUser38 | list_user38@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_39 | ListDefault39 | ListUser39 | list_user39@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_40 | ListDefault40 | ListUser40 | list_user40@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_41 | ListDefault41 | ListUser41 | list_user41@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_42 | ListDefault42 | ListUser42 | list_user42@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_43 | ListDefault43 | ListUser43 | list_user43@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_44 | ListDefault44 | ListUser44 | list_user44@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_45 | ListDefault45 | ListUser45 | list_user45@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_46 | ListDefault46 | ListUser46 | list_user46@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_47 | ListDefault47 | ListUser47 | list_user47@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_48 | ListDefault48 | ListUser48 | list_user48@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_49 | ListDefault49 | ListUser49 | list_user49@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_50 | ListDefault50 | ListUser50 | list_user50@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_51 | ListDefault51 | ListUser51 | list_user51@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_52 | ListDefault52 | ListUser52 | list_user52@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_53 | ListDefault53 | ListUser53 | list_user53@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_54 | ListDefault54 | ListUser54 | list_user54@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_55 | ListDefault55 | ListUser55 | list_user55@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_56 | ListDefault56 | ListUser56 | list_user56@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_57 | ListDefault57 | ListUser57 | list_user57@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_58 | ListDefault58 | ListUser58 | list_user58@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | list_default_59 | ListDefault59 | ListUser59 | list_user59@snapshot.travel | Europe/Prague | cs-CZ   |

    When List of users is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> users returned
    And Link header is '<link_header>'
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | link_header                                                                                       |
      | /null |        | 50       | 63    | </identity/users?limit=50&cursor=50>; rel="next"                                                  |
      | /null | /null  | 50       | 63    | </identity/users?limit=50&cursor=50>; rel="next"                                                  |
      |       |        | 50       | 63    | </identity/users?limit=50&cursor=50>; rel="next"                                                  |
      |       | /null  | 50       | 63    | </identity/users?limit=50&cursor=50>; rel="next"                                                  |
      | 15    |        | 15       | 63    | </identity/users?limit=15&cursor=15>; rel="next"                                                  |
      |       | 1      | 50       | 63    | </identity/users?limit=50&cursor=51>; rel="next", </identity/users?limit=50&cursor=0>; rel="prev" |
      | 20    | 0      | 20       | 63    | </identity/users?limit=20&cursor=20>; rel="next"                                                  |
      | 10    | 0      | 10       | 63    | </identity/users?limit=10&cursor=10>; rel="next"                                                  |
      | 5     | 10     | 5        | 63    | </identity/users?limit=5&cursor=15>; rel="next", </identity/users?limit=5&cursor=5>; rel="prev"   |

  Scenario Outline: Checking error codes for getting list of users
    When List of users is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit | cursor | filter      | sort         | sort_desc    | response_code | custom_code |
      #limit and cursor
      | /null | -1     | /null       | /null        | /null        | 400           | 63          |
      |       | -1     | /null       | /null        | /null        | 400           | 63          |
      | /null | text   | /null       | /null        | /null        | 400           | 63          |
      |       | text   | /null       | /null        | /null        | 400           | 63          |
      | -1    |        | /null       | /null        | /null        | 400           | 63          |
      | -1    | /null  | /null       | /null        | /null        | 400           | 63          |
      | text  |        | /null       | /null        | /null        | 400           | 63          |
      | text  | /null  | /null       | /null        | /null        | 400           | 63          |
      | 10    | -1     | /null       | /null        | /null        | 400           | 63          |
      | text  | 0      | /null       | /null        | /null        | 400           | 63          |
      | 10    | text   | /null       | /null        | /null        | 400           | 63          |
      #filtering and sorting
      | 10    | 0      | /null       | user_name    | user_name    | 400           | 64          |
      | 10    | 0      | /null       | /null        | nonexistent  | 400           | 63          |
      | 10    | 0      | /null       | nonexistent  | /null        | 400           | 63          |
      | 10    | 0      | /null       | company_name |              | 400           | 63          |
      | 10    | 0      | /null       |              | company_name | 400           | 63          |
      | 10    | 0      | user_name== | /null        | /null        | 400           | 63          |
      | 10    | 0      | aaa==CZ*    | /null        | /null        | 400           | 63          |

  Scenario Outline: Checking that empty parameters will be ignored and request will be returned
    When List of users is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"

    Examples:
      | limit | cursor | filter | sort  | sort_desc |
      |       | /null  | /null  | /null | /null     |
      | /null |        | /null  | /null | /null     |
      | /null | /null  |        | /null | /null     |
      | /null | /null  | /null  |       | /null     |
      | /null | /null  | /null  | /null |           |

  Scenario Outline: Filtering list of users
    Given The following users exist for customer "728c45dd-a964-4f78-afe0-88d108c682ed" as primary "false"
      | userType | userName         | firstName      | lastName    | email                        | phone        | timezone          | culture |
      | customer | filter_default_1 | FilterDefault1 | FilterUser1 | filter_user1@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | customer | filter_default_2 | FilterDefault2 | FilterUser2 | filter_user2@snapshot.travel | +42010111213 | Europe/Bratislava | cs-CZ   |
      | guest    | filter_default_3 | FilterDefault3 | FilterUser3 | filter_user3@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | customer | filter_default_4 | FilterDefault4 | FilterUser4 | filter_user4@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | partner  | filter_default_5 | FilterDefault5 | FilterUser5 | filter_user5@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | customer | filter_default_6 | FilterDefault6 | FilterUser6 | filter_user6@snapshot.travel | +42010111213 | Europe/Prague     | cs-CZ   |
      | customer | other_default_7  | FilterDefault7 | FilterUser7 | filter_user7@snapshot.travel | +42010111218 | Europe/Prague     | cs-CZ   |
      | customer | other_default_8  | FilterDefault8 | FilterUser8 | filter_user8@snapshot.travel | +42010111213 | Europe/Prague     | sk-SK   |
      | snapshot | other_default_9  | FilterDefault9 | FilterUser9 | filter_user9@snapshot.travel | +42010111213 | Europe/Prague     | sk-SK   |

    When List of users is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> users returned
    And There are users with following usernames returned in order: <expected_usernames>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                                        | sort      | sort_desc | expected_usernames                                                                       |
      | 5     | 0      | 5        | 6     | user_name=='filter_default*'                  | user_name |           | filter_default_1, filter_default_2, filter_default_3, filter_default_4, filter_default_5 |
      | 5     | 0      | 5        | 6     | user_name=='filter_default*'                  |           | user_name | filter_default_6, filter_default_5, filter_default_4, filter_default_3, filter_default_2 |
      | 5     | 2      | 4        | 6     | user_name=='filter_default*'                  | user_name |           | filter_default_3, filter_default_4, filter_default_5, filter_default_6                   |
      | 5     | 2      | 4        | 6     | user_name=='filter_default*'                  |           | user_name | filter_default_4, filter_default_3, filter_default_2, filter_default_1                   |
      | /null | /null  | 1        | 1     | user_name==filter_default_6                   | /null     | /null     | filter_default_6                                                                         |
      | /null | /null  | 2        | 2     | user_name==other_default_* and culture==sk-SK | user_name | /null     | other_default_8, other_default_9                                                         |
      | /null | /null  | 1        | 1     | user_type==snapshot                           | /null     | /null     | other_default_9                                                                          |
      | /null | /null  | 1        | 1     | email==filter_user4@snapshot.travel           | /null     | /null     | filter_default_4                                                                         |
      | /null | /null  | 1        | 1     | timezone==Europe/Bratislava                   | /null     | /null     | filter_default_2                                                                         |
      | /null | /null  | 1        | 1     | phone==+42010111218                           | /null     | /null     | other_default_7                                                                          |
  #add all fields
