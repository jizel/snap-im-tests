@SocialMedia
Feature: Instagram
  - Testing of api for instagram with mock data in db - testing property id is "99000099-9999-4999-a999-999999999999"

  Background:
    Given Database is cleaned and default entities are created


  Scenario Outline: Get instagram analytics data from API for a given wrong granularity
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is "40002"

    Examples:
      | url                             | granularity | property                             | since      | until      |
      | /analytics/instagram            | dd          | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/instagram            | day         | #9999999-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/instagram            | day         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-   |
      | /analytics/instagram            | day         | 99000099-9999-4999-a999-999999999999 | 2015-12-   | 2015-12-03 |
      | /analytics/instagram/pictures   | yy          | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/instagram/engagement | mm          | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/instagram/followers  | 1dd         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/instagram/tags       | MONTHS      | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/instagram/reach      | DAYs        | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/instagram/likes      | W33K        | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |
      | /analytics/instagram/comments   | WEEKS       | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |

  Scenario Outline: Validate that metrics have valid value in the db for single metric
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And The metric count is <count>

    Examples:
      | url                             | granularity | count | since      | until      | property                             |
      | /analytics/instagram/pictures   | day         | 7606  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | day         | 15321 | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | day         | 7558  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | day         | 7470  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | day         | 7558  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | day         | 7508  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | day         | 7813  | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures   | week        | 7197  | 2015-11-03 | 2015-11-16 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | week        | 14474 | 2015-11-03 | 2015-11-16 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | week        | 7224  | 2015-11-03 | 2015-11-16 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | week        | 7024  | 2015-11-03 | 2015-11-16 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | week        | 7224  | 2015-11-03 | 2015-11-16 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | week        | 7079  | 2015-11-03 | 2015-11-16 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | week        | 7395  | 2015-11-03 | 2015-11-16 | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures   | month       | 7542  | 2015-10-03 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | month       | 15202 | 2015-10-03 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | month       | 7471  | 2015-10-03 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | month       | 7385  | 2015-10-03 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | month       | 7471  | 2015-10-03 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | month       | 7440  | 2015-10-03 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | month       | 7762  | 2015-10-03 | 2015-12-09 | 99000099-9999-4999-a999-999999999999 |

  @Smoke
  Scenario Outline: Validate that metrics have valid value in the db - smoke
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains "<count>" values of global stats dto

    Examples:
      | url                  | granularity | count                                     | since      | until      | property                             |
      | /analytics/instagram | day         | 7470, 7508, 7558, 7558, 7606, 7813, 15321 | 2015-12-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Validate that metrics have valid value in the db for all metrics
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains "<count>" values of global stats dto

    Examples:
      | url                  | granularity | count                                     | since      | until      | property                             |
      | /analytics/instagram | week        | 7024, 7079, 7197, 7224, 7224, 7395, 14474 | 2015-11-13 | 2015-11-20 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram | month       | 7385, 7440, 7471, 7471, 7542, 7762, 15202 | 2015-11-03 | 2015-12-03 | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Get specific analytics data from API for at given granularity
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Data is owned by "instagram"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> values

    Examples:
      | url                             | granularity | count | since             | until | real_since        | real_until | property                             |

      | /analytics/instagram/pictures   | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures   | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures   | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures   | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures   | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures   | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures   | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures   | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures   | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Get specific analytics data from instagram API for a given granularity
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Data is owned by "instagram"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> amount of values for global stats dto

    Examples:
      | url                   | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics/instagram/ | day         | 41    | today - 40 days   | today | today - 40 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/ | day         | 366   | today - 365 days  | today | today - 365 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/ | week        | 3     | today - 13 days   | today | today - 13 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/ | week        | 5     | today - 27 days   | today | today - 27 days   | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/ | week        | 53    | today - 363 days  | today | today - 363 days  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/ | month       | 3     | today - 2 months  | today | today - 2 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/ | month       | 5     | today - 4 months  | today | today - 4 months  | today      | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/ | month       | 13    | today - 12 months | today | today - 12 months | today      | 99000099-9999-4999-a999-999999999999 |

  @Smoke
  Scenario Outline: Get specific analytics data from instagram API for a given granularity - smoke
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Data is owned by "instagram"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> amount of values for global stats dto

    Examples:
      | url                   | granularity | count | since             | until | real_since        | real_until | property                             |
      | /analytics/instagram/ | day         | 1     | today             | today | today             | today      | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Getting non-existent analytics data
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "today" until "today"
    Then Response code is 404

    Examples:
      | url                              | granularity | property                             |
      | /analytics/instagram/not_present | day         | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Getting mismatched metrics analytics data
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 404

    Examples:
      | url                         | granularity | property                             | since      | until      |
      | /analytics/instagram/tweets | day         | 99000099-9999-4999-a999-999999999999 | 2015-12-03 | 2015-12-03 |

  Scenario Outline: Checking error codes for analytics data
    When Get instagram "<url>" with missing property header
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is "40002"

    Examples:
      | url                             |
      | /analytics/instagram            |
      | /analytics/instagram/pictures   |
      | /analytics/instagram/engagement |
      | /analytics/instagram/followers  |
      | /analytics/instagram/tags       |
      | /analytics/instagram/reach      |
      | /analytics/instagram/likes      |
      | /analytics/instagram/comments   |


  Scenario Outline: Get analytics data from API from 1800s
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains 0 values

    Examples:
      | url                             | granularity | since      | until      | property                             |
      | /analytics/instagram/pictures   | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | month       | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/pictures   | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | week        | 1888-09-01 | 1890-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/pictures   | day         | 1888-09-01 | 1888-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | day         | 1888-09-01 | 1888-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | day         | 1888-09-01 | 1888-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | day         | 1888-09-01 | 1888-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | day         | 1888-09-01 | 1888-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | day         | 1888-09-01 | 1888-10-01 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | day         | 1888-09-01 | 1888-10-01 | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Get instagram analytics data from API from 1800s
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Response contains 0 values for all metrics

    Examples:
      | url                  | granularity | since      | until      | property                             |
      | /analytics/instagram | day         | 1888-09-01 | 1888-10-01 | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Get analytics data with wrong time interval
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is 40002

    Examples:
      | url                  | granularity | since | until           | property                             |
      | /analytics/instagram | day         | today | today - 10 days | 99000099-9999-4999-a999-999999999999 |

  Scenario Outline: Get analytics data with wrong time interval asd
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 400
    And Content type is "application/json"
    And Custom code is 40002

    Examples:
      | url                             | granularity | since | until           | property                             |
      | /analytics/instagram/pictures   | day         | today | today - 10 days | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement | day         | today | today - 10 days | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers  | day         | today | today - 10 days | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags       | day         | today | today - 10 days | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach      | day         | today | today - 10 days | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes      | day         | today | today - 10 days | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments   | day         | today | today - 10 days | 99000099-9999-4999-a999-999999999999 |

  # TODO: DP-2014 - time based collection pagination is disabled, the test fails when it is enabled
  Scenario Outline: Get analytics data with large interval
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Data is owned by "instagram"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> amount of values for global stats dto

    Examples:
      | url                  | granularity | count | since      | until      | real_since | real_until | property                             |
      | /analytics/instagram | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |

  # TODO: DP-2014 - time based collection pagination is disabled, the test fails when it is enabled
  Scenario Outline: Get specific analytics data with large time interval
    When Get instagram "<url>" data with "<granularity>" granularity for "<property>" since "<since>" until "<until>"
    Then Response code is 200
    And Content type is "application/json"
    And Data is owned by "instagram"
    And Body contains entity with attribute "granularity" value "<granularity>"
    And Response since is "<real_since>" for granularity "<granularity>"
    And Response until is "<real_until>" for granularity "<granularity>"
    And Response contains <count> values

    Examples:
      | url                                  | granularity | count | since      | until      | real_since | real_until | property                             |
      | /analytics/instagram/pictures        | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement      | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers       | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags            | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach           | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes           | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments        | day         | 1461  | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures        | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement      | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers       | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags            | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach           | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes           | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments        | week        | 210   | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |

      | /analytics/instagram/pictures        | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/engagement      | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/followers       | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/tags            | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/reach           | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/likes           | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
      | /analytics/instagram/comments        | month       | 48    | 2015-01-01 | 2018-12-31 | 2015-01-01 | 2018-12-31 | 99000099-9999-4999-a999-999999999999 |
