Feature: common

  Scenario Outline: Get collective analytics data from API for a given granularity
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response contains <count> values

    Examples:
      | url                  | granularity | count | since      | until      | property                             | content_type     | response_code |
      | /analytics           | day         | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | day         | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | day         | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | day         | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics           | week        | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | week        | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | week        | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | week        | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics           | month       | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | month       | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | month       | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | month       | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Validate that metrics have valid value in the db
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And The metric count is <count>

    Examples:
      | url                  | granularity | count | since      | until      | property                             | content_type     | response_code |
      | /analytics           | day         | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | day         | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | day         | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | day         | 1     | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Get collective analytics data from API for a given wrong time period
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"

    Examples:
      | url                  | granularity | since      | until      | property                             | content_type     | response_code |
      | /analytics           | day         | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | day         | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | day         | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | day         | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics           | week        | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | week        | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | week        | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | week        | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics           | month       | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | month       | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | month       | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | month       | 2016-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Get collective analytics data from API for a not given since granularity
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"

    Examples:
      | url                  | granularity | since | until      | property                             | content_type     | response_code |
      | /analytics           | day         |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | day         |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | day         |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | day         |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics           | week        |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | week        |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | week        |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | week        |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics           | month       |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | month       |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | month       |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | month       |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Get collective analytics data from API for a not given until granularity
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"

    Examples:
      | url                  | granularity | until | since      | property                             | content_type     | response_code |
      | /analytics           | day         |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | day         |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | day         |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | day         |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics           | week        |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | week        |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | week        |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | week        |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics           | month       |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/facebook  | month       |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/twitter   | month       |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |
      | /analytics/instagram | month       |       | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           |

  Scenario Outline: Get specific analytics data from API for a given granularity
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Body contains entity with attribute "since" value "<calculated_statistics_real_since>"
    And Body contains entity with attribute "until" value "<until>"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Data is owned by "<data_owner>"
    And Response contains <count> values

    Examples:
      | url                   | granularity | count | since      | until      | calculated_statistics_real_since | property                             | content_type     | response_code | data_owner |
      | /analytics            | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/reach      | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/followers  | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/engagement | day         | 1     | 2015-12-07 | 2015-12-07 | 2015-12-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics            | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/reach      | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/followers  | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/engagement | week        | 1     | 2015-11-07 | 2015-11-13 | 2015-11-07                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics            | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/reach      | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/followers  | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/engagement | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/followers  | day         | 11    | 2015-12-03 | 2015-12-13 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/engagement | day         | 11    | 2015-12-03 | 2015-12-13 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/engagement | day         | 17    | 2015-12-07 | 2015-12-23 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/reach      | day         | 17    | 2015-12-07 | 2015-12-23 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/reach      | day         | 90    | 2015-06-07 | 2015-12-07 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/followers  | week        | 1     | 2015-12-07 | 2015-12-13 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/followers  | week        | 1     | 2015-12-03 | 2015-12-13 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/engagement | week        | 1     | 2015-12-03 | 2015-12-13 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/engagement | week        | 2     | 2015-12-07 | 2015-12-23 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/reach      | week        | 2     | 2015-12-07 | 2015-12-23 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/reach      | week        | 26    | 2015-01-07 | 2015-12-23 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/reach      | week        | 26    | 2015-01-07 | 2015-12-23 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/followers  | month       | 1     | 2015-11-01 | 2015-11-30 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/engagement | month       | 1     | 2015-02-01 | 2015-03-23 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |
      | /analytics/reach      | month       | 36    | 2013-02-01 | 2016-12-31 | 2015-11-01                       | 99999999-9999-4999-a999-999999999999 | application/json | 200           | google     |

  Scenario Outline: Get specific analytics data from API for a given until earlier than since
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response contains <count> values

    Examples:
      | url                   | granularity | since      | until      | property                             | content_type     | response_code | count |
      | /analytics            | day         | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics/reach      | day         | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics/followers  | day         | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics/engagement | day         | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics            | week        | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics/reach      | week        | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics/followers  | week        | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics/engagement | week        | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics            | month       | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics/reach      | month       | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics/followers  | month       | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |
      | /analytics/engagement | month       | 2015-12-03 | 2014-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 5     |


  Scenario Outline: Get analytics data from API for a given wrong granularity
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Custom code is "<custom_code>"

    Examples:
      | url                  | granularity | property                             | since      | until      | content_type     | response_code | custom_code |
      | /analytics           | ddd         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/facebook  | www         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/twitter   | yyy         | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |
      | /analytics/instagram | MONTHs      | 99999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 | application/json | 400           | 63          |


  Scenario Outline: Getting large period analytics data
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"

    Examples:
      | url        | granularity | property                             | since      | until      | content_type     | response_code |
      | /analytics | day         | 99999999-9999-4999-a999-999999999999 | 1888-09-01 | 2015-09-01 | application/json | 200           |
      | /analytics | week        | 99999999-9999-4999-a999-999999999999 | 1888-09-01 | 2015-09-01 | application/json | 200           |
      | /analytics | month       | 99999999-9999-4999-a999-999999999999 | 1888-09-01 | 2015-09-01 | application/json | 200           |

  Scenario Outline: Getting non-existent analytics data
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Custom code is "<custom_code>"

    Examples:
      | url                               | granularity | since      | until      | property                             | content_type     | response_code | custom_code |
      | /analytics/not_present            | day         | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 404           | 151         |
      | /analytics/reach/not_present      | day         | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 404           | 151         |
      | /analytics/followers/not_present  | day         | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 404           | 151         |
      | /analytics/engagement/not_present | day         | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 404           | 151         |
      | /analytics/facebook/tweets        | day         | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 404           | 151         |
      | /analytics/twitter/likes          | day         | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 404           | 151         |
      | /analytics/instagram/impressions  | day         | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 404           | 151         |

  Scenario Outline: Checking error codes for analytics data
    When Get social_media "<url>" with missing property header
    Then Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples:
      | url                   | response_code | custom_code |
      | /analytics/           | 400           | 52          |
      | /analytics/followers  | 400           | 52          |
      | /analytics/engagement | 400           | 52          |
      | /analytics/reach      | 400           | 52          |

  Scenario Outline: Get analytics data from API with missing parameters
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response contains <count> values

    Examples:
      | url                   | granularity | since      | until      | property                             | content_type     | response_code | count |
      | /analytics/           |             | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/           |             |            | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/           |             | 2015-09-01 |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/           |             |            |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/           | day         |            | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/           | day         | 2015-09-01 |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/           | day         |            |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/reach      |             | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/reach      |             |            | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/reach      |             | 2015-09-01 |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/reach      |             |            |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/reach      | day         |            | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/reach      | day         | 2015-09-01 |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/reach      | day         |            |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/followers  |             | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/followers  |             |            | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/followers  |             | 2015-09-01 |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/followers  |             |            |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/followers  | day         |            | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/followers  | day         | 2015-09-01 |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/followers  | day         |            |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/engagement |             | 2015-09-01 | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/engagement |             |            | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/engagement |             | 2015-09-01 |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/engagement |             |            |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/engagement | day         |            | 2015-09-01 | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/engagement | day         | 2015-09-01 |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |
      | /analytics/engagement | day         |            |            | 99999999-9999-4999-a999-999999999999 | application/json | 200           | 151   |

  Scenario Outline: Checking default parameter values for all metrics
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Response contains correct number of values for granularity "<granularity>" between "<expected_since>" and "<expected_until>"

    Examples:
      | url         | granularity | since             | until          | expected_granularity | expected_since    | expected_until | property                             | response_code | content_type     |
      | /analytics/ |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/ | day         | today - 3 days    | today - 2 days | day                  | today - 3 days    | today - 2 days | 99999999-9999-4999-a999-999999999999 | 200           | application/json |

  Scenario Outline: Checking default parameter values for specific metrics
  Empty column in examples section means default value will be used for this parameter.
  if text is empty, returns null
  if text is date in ISO format (2015-01-01), it returns this date
  text can contain keywords: 'today' and operations '+-n days', '+-n weeks', '+-n months' which will add or substract
  particular number of days/weeks/months from first part of expression

    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response granularity is "<expected_granularity>"
    And Response since is "<expected_since>"
    And Response until is "<expected_until>"
    And Response contains correct number of values for granularity "<granularity>" between "<expected_since>" and "<expected_until>"

    Examples:
      | url                   | granularity | since             | until          | expected_granularity | expected_since    | expected_until | property                             | response_code | content_type     |
      | /analytics/reach      |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/reach      |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/reach      | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/reach      | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/reach      | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/reach      | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/reach      | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/reach      | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/reach      | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/reach      | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/reach      | day         | today - 3 days    | today - 2 days | day                  | today - 3 days    | today - 2 days | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/followers  | day         | today - 3 days    | today - 2 days | day                  | today - 3 days    | today - 2 days | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/engagement | day         | today - 3 days    | today - 2 days | day                  | today - 3 days    | today - 2 days | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/facebook   | day         | today - 3 days    | today - 2 days | day                  | today - 3 days    | today - 2 days | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter/   |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/twitter    | day         | today - 3 days    | today - 2 days | day                  | today - 3 days    | today - 2 days | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  |             |                   |                | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  |             | 2015-12-03        | 2015-12-03     | day                  | 2015-12-03        | 2015-12-03     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  | day         |                   | today          | day                  | today - 1 month   | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  | day         | today             |                | day                  | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  | week        |                   | today          | week                 | today - 13 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  | week        | today             |                | week                 | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  | month       |                   | today          | month                | today - 6 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  | month       | today             |                | month                | today             | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  | day         | today - 4 months  | today          | day                  | today - 3 months  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  | week        | today - 30 weeks  | today          | week                 | today - 26 weeks  | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  | month       | today - 40 months | today          | month                | today - 36 months | today          | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics/instagram  | day         | today - 3 days    | today - 2 days | day                  | today - 3 days    | today - 2 days | 99999999-9999-4999-a999-999999999999 | 200           | application/json |

  Scenario Outline: Get collective analytics data from API for all metrics
    When Get social media "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    And Response contains <count> values for all metrics

    Examples:
      | url        | granularity | since      | until      | count | property                             | response_code | content_type     |
      | /analytics | day         | 2015-12-07 | 2015-12-07 | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics | day         | 2015-12-03 | 2015-12-13 | 11    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics | day         | 2015-12-07 | 2015-12-23 | 17    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics | week        | 2015-12-07 | 2015-12-13 | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics | week        | 2015-12-03 | 2015-12-13 | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics | week        | 2015-12-07 | 2015-12-23 | 2     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics | month       | 2015-11-01 | 2015-11-30 | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics | month       | 2015-02-01 | 2015-03-23 | 1     | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | /analytics | month       | 2013-02-01 | 2016-12-31 | 36    | 99999999-9999-4999-a999-999999999999 | 200           | application/json |

  Scenario Outline: Verify overall analytics values
    When Verifying sum of "<metric>" from Facebook, Twitter, and Instagram with "<granularity>" granularity for property "<property>", since "<since>", until "<until>"
    Then Response code is <response_code>
    And Content type is "<content_type>"

    Examples:
      | metric     | granularity | since      | until      | property                             | response_code | content_type     |
      | followers  | day         | 2015-12-12 | 2015-12-12 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | engagement | week        | 2015-12-01 | 2015-12-12 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
      | reach      | month       | 2015-11-01 | 2015-12-12 | 99999999-9999-4999-a999-999999999999 | 200           | application/json |
