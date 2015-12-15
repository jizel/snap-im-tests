Feature: web_performance_countries

  #not yet implemented
  Scenario Outline: Getting a list of items
    When List of "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is <response_code>
    And Content type is "<content_type>"
    #And Data is owned by ""
    And There are <count> posts returned

    Examples: 
      | url                                                   | limit | cursor | count | response_code | content_type     | property                             |
      | /web_performance/analytics/visits/countries           |       |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | /null |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           |       | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | /null | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | 51    |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | 51    | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           |       | 1      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | /null | 1      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | 20    | 0      | 20    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | 60    | 0      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | 5     | 5      | 5     | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    |       |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | /null |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    |       | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | /null | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | 51    |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | 51    | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    |       | 1      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | /null | 1      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | 20    | 0      | 20    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | 60    | 0      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | 5     | 5      | 5     | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries |       |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | /null |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries |       | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | /null | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | 51    |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | 51    | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries |       | 1      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | /null | 1      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | 20    | 0      | 20    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | 60    | 0      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | 5     | 5      | 5     | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  |       |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | /null |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  |       | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | /null | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | 51    |        | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | 51    | /null  | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  |       | 1      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | /null | 1      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | 20    | 0      | 20    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | 60    | 0      | 50    | 200           | application/json | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | 5     | 5      | 5     | 200           | application/json | 99999999-9999-4999-a999-999999999999 |

  #not yet implemented, other metrics are not pageable
  Scenario Outline: Checking error codes for getting list of items
    When List of "<url>" for property id "<property>" is got with limit "<limit>" and cursor "<cursor>"
    Then Response code is <response_code>
    And Custom code is "<custom_code>"

    Examples: 
      | url                                                   | limit | cursor | response_code | custom_code | property                             |
      | /web_performance/analytics/visits/countries           | /null | -1     | 400           | 52          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           |       | -1     | 400           | 52          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | 10    | -1     | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | text  | 0      | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | text  | /null  | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | text  |        | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits/countries           | 10    | text   | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | /null | -1     | 400           | 52          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    |       | -1     | 400           | 52          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | 10    | -1     | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | text  | 0      | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | text  | /null  | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | text  |        | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/visits_unique/countries    | 10    | text   | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | /null | -1     | 400           | 52          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries |       | -1     | 400           | 52          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | 10    | -1     | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | text  | 0      | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | text  | /null  | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | text  |        | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/conversion_rates/countries | 10    | text   | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | /null | -1     | 400           | 52          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  |       | -1     | 400           | 52          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | 10    | -1     | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | text  | 0      | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | text  | /null  | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | text  |        | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
      | /web_performance/analytics/referrals                  | 10    | text   | 400           | 63          | 99999999-9999-4999-a999-999999999999 |
