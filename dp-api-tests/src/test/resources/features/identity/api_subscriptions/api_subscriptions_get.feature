Feature: Api subscription get

  Background:
    Given Database is cleaned
    Given The following applications exist
      | applicationId                        | applicationName            | description               | website                    |
      | 6f552105-0bae-4410-b4bb-bee31567d4fa | Application test company 1 | Application description 1 | http://www.snapshot.travel |
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | 2016-02-22  | Versions description 1 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | apiVersion |
      | 187b49db-673c-44e5-ab40-345ce5e89c37 | a318fd9a-a05d-42d8-8e84-22e904ace111 | someString |


  @Smoke
  Scenario: Getting api subscription
    When Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is got
    Then Response code is 200
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "application_version_id" value "a318fd9a-a05d-42d8-8e84-22e904ace111"
    And Body contains entity with attribute "api_version" value "b1111d9a-a05d-42d8-8e84-42e904ace999"
    And Body contains entity with attribute "api_subscription_id" value "187b49db-673c-44e5-ab40-345ce5e89c37"


  Scenario: Getting customer api subscription with etag
    When Api subscription with id "187b49db-673c-44e5-ab40-345ce5e89c37" is got with etag
    Then Response code is 304
    And Body is empty


  Scenario: Checking error code for getting nonExisting api subscription
    When Api subscription with id "nonExistingApi" is got
    Then Response code is 404
    And Content type is "application/json"
    And Custom code is 152

  Scenario Outline: Getting list of api subscriptions with valid query parameters
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description             |
      | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 1   | inactive | 2016-02-01  | Versions description 1  |
      | ab343111-12d3-4fde-ba8a-5ddff45d06d4 | 2            | Version 2   | inactive | 2016-02-02  | Versions description 2  |
      | 1820d135-4f75-4c64-a570-a89e8236229b | 3            | Version 3   | inactive | 2016-02-03  | Versions description 3  |
      | ac39d04c-bb58-4955-a7ab-c216e5444a54 | 4            | Version 4   | inactive | 2016-02-04  | Versions description 4  |
      | 315404f9-3ac8-4b75-8b54-1ea15702d046 | 5            | Version 5   | inactive | 2016-02-05  | Versions description 5  |
      | f29ce9d8-e4b9-4d99-9531-e1e1513ab4e5 | 6            | Version 6   | inactive | 2016-02-06  | Versions description 6  |
      | 2e215979-49df-488c-8cea-a4abeb4c0265 | 7            | Version 7   | inactive | 2016-02-07  | Versions description 7  |
      | 0b29cc24-d2bc-49e3-9944-c58532f00534 | 8            | Version 8   | inactive | 2016-02-08  | Versions description 8  |
      | 75d47daa-21fa-431d-a2b9-b490bba5250d | 9            | Version 9   | inactive | 2016-02-09  | Versions description 9  |
      | 4e701c5f-aa7e-4a68-9f96-967550c2442c | 10           | Version 10  | inactive | 2016-02-10  | Versions description 10 |
      | 5ca16080-f21d-4d35-b257-b18f0d7b2a14 | 11           | Version 11  | inactive | 2016-02-11  | Versions description 11 |
      | 49dad233-3199-4cf2-827a-0efbb6899acd | 12           | Version 12  | inactive | 2016-02-12  | Versions description 12 |
      | 9701fcc1-5d31-4f1f-80d3-5c2cee3bbaed | 13           | Version 13  | inactive | 2016-02-13  | Versions description 13 |
      | 6c591036-2c6c-4362-a465-39515466163c | 14           | Version 14  | inactive | 2016-02-14  | Versions description 14 |
      | 3b02cb4e-1cca-4d70-8bd6-c7efbc817493 | 15           | Version 15  | inactive | 2016-02-15  | Versions description 15 |
      | a4e30390-8e6c-404f-b719-e344dfad4519 | 16           | Version 16  | inactive | 2016-02-16  | Versions description 16 |
      | 2579aeb0-95bb-4789-98ac-99659b5b77b0 | 17           | Version 17  | inactive | 2016-02-17  | Versions description 17 |
      | bcc3efa9-fd93-4c9d-add7-67545971d1b9 | 18           | Version 18  | inactive | 2016-02-18  | Versions description 18 |
      | bc4bed55-de3d-471e-a1e8-fce6f6e029aa | 19           | Version 19  | inactive | 2016-02-19  | Versions description 19 |
      | bf7b9b34-14de-4987-82c7-6251aa43a188 | 20           | Version 20  | inactive | 2016-02-20  | Versions description 20 |

    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | apiVersion    |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | apiVersion_1  |
      | 598cfe31-9583-4c0b-86a0-82e9bf662849 | ab343111-12d3-4fde-ba8a-5ddff45d06d4 | apiVersion_2  |
      | 87eb138c-23c1-43d8-bc88-d6fbbd7e4359 | 1820d135-4f75-4c64-a570-a89e8236229b | apiVersion_3  |
      | e2c258a3-a3e0-4895-82ee-5170934c5555 | ac39d04c-bb58-4955-a7ab-c216e5444a54 | apiVersion_4  |
      | c432f76f-8fdd-4229-b8eb-e9007024b385 | 315404f9-3ac8-4b75-8b54-1ea15702d046 | apiVersion_5  |
      | 512da950-3ff0-43b9-a521-064fe4d060e7 | f29ce9d8-e4b9-4d99-9531-e1e1513ab4e5 | apiVersion_6  |
      | 541539e8-4e6f-40fd-b0cf-260d4c1801f6 | 2e215979-49df-488c-8cea-a4abeb4c0265 | apiVersion_7  |
      | 4c3a0311-d27d-452d-af04-2b61a4c61202 | 0b29cc24-d2bc-49e3-9944-c58532f00534 | apiVersion_8  |
      | 9332ec8b-cb57-4781-822b-60e1250d7d27 | 75d47daa-21fa-431d-a2b9-b490bba5250d | apiVersion_9  |
      | 82f66099-83f1-4008-8e3d-b34b60fb1089 | 4e701c5f-aa7e-4a68-9f96-967550c2442c | apiVersion_10 |
      | 6b4800ca-e149-42f0-8aa0-cffbe0a1bff8 | 5ca16080-f21d-4d35-b257-b18f0d7b2a14 | apiVersion_11 |
      | 1303a49c-c150-4af2-880a-52c0fdbc3534 | 49dad233-3199-4cf2-827a-0efbb6899acd | apiVersion_12 |
      | f8ef0353-7ec7-493e-b5d4-7773ac3c11cf | 9701fcc1-5d31-4f1f-80d3-5c2cee3bbaed | apiVersion_13 |
      | 6c3309ab-3213-4f23-a72d-dbc6a4fc88c6 | 6c591036-2c6c-4362-a465-39515466163c | apiVersion_14 |
      | 18a1d3a5-36ce-4c12-aba3-37ad25b0ca13 | 3b02cb4e-1cca-4d70-8bd6-c7efbc817493 | apiVersion_15 |
      | 25a6873f-578e-422c-8f50-0833c204a36f | a4e30390-8e6c-404f-b719-e344dfad4519 | apiVersion_16 |
      | c92b6ca0-095f-485b-9452-d9c2f9f0b833 | 2579aeb0-95bb-4789-98ac-99659b5b77b0 | apiVersion_17 |
      | f7adde17-9e7a-4484-850d-406fc833d0a2 | bcc3efa9-fd93-4c9d-add7-67545971d1b9 | apiVersion_18 |
      | f7f46e2c-2204-4b4b-939f-251f3fcb6e8f | bc4bed55-de3d-471e-a1e8-fce6f6e029aa | apiVersion_19 |
      | b0edd868-e1f4-429e-b912-297e22c95bc5 | bf7b9b34-14de-4987-82c7-6251aa43a188 | apiVersion_20 |

    When List of api subscriptions is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is 200
    And Content type is "application/json"
    And There are "<returned>" api subscriptions returned
    And Total count is "21"

    Examples:
      | limit | cursor | returned | note                                                                           |
      | /null | /null  | 21       | # all api subscriptions should be returned                                     |
      | 10    | /null  | 10       | # limit parameter should be user, 10 subscriptions should be returned          |
      |       |        | 21       | # empty parameter are ignored, all api subscriptions should be returned        |
      |       | /null  | 21       | # empty limit parameter, all api subscriptions should be returned              |
      | /null |        | 21       | # empty cursor parameter, all api subscriptions should be returned             |
      | 10    | 1      | 10       | # cursor < limit, limit param is used, 10 api subscriptions should be returned |
      | 1     | 10     | 1        | # cursor > limit, limit param is used, 1 api subscriptions should be returned  |
      | /null | 0      | 21       | # cursor can be 0 (?), all api subscriptions should be returned                |

  Scenario Outline: Getting list of api subscriptions with invalid query parameters
    When List of api subscriptions is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is 400
    And Custom code is <customCode>

    Examples:
      | limit | cursor | filter      | sort        | sort_desc   | customCode | note                                                                                                                                                                                                        |
      | -1    | /null  | /null       | /null       | /null       | 63         | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
      | text  | /null  | /null       | /null       | /null       | 63         | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
      | 9999  | /null  | /null       | /null       | /null       | 63         | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
      | 0     | /null  | /null       | /null       | /null       | 63         | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
      | /null | -1     | /null       | /null       | /null       | 63         | # The value is invalid. The cursor should be a positive number.                                                                                                                                             |
      | /null | text   | /null       | /null       | /null       | 63         | # The value is invalid. The cursor should be a positive number.                                                                                                                                             |
      | /null | /null  | -1          | /null       | /null       | 63         | # The value is invalid. Param 'arg0' The query parameter 'filter' has invalid FIQL syntax; provided filter=-1                                                                                               |
      | /null | /null  | ==          | /null       | /null       | 63         | # The value is invalid. Param 'arg0' The query parameter 'filter' has invalid FIQL syntax; provided filter===                                                                                               |
      | /null | /null  | code==      | /null       | /null       | 63         | # The value is invalid. Param 'arg0' The query parameter 'filter' has invalid FIQL syntax; provided filter=code==                                                                                           |
      | /null | /null  | is_active== | /null       | /null       | 63         | # The value is invalid. Param 'arg0' The query parameter 'filter' has invalid FIQL syntax; provided filter=is_active==                                                                                      |
      | /null | /null  | /null       | -1          | /null       | 63         | # The value is invalid. Param 'sort' must match \"[a-zA-Z0-9_]*\"                                                                                                                                           |
      | /null | /null  | /null       | 0           | /null       | 63         | # The value is invalid. Param 'sort' must match \"[a-zA-Z0-9_]*\"                                                                                                                                           |
      | /null | /null  | /null       | nonExistent | /null       | 63         | # The value is invalid. Param 'arg0' The query parameter 'sort' has illegal value; allowed values: [application_version_id, is_active, api_subscription_id, api_version], provided value: 'nonExistent'"    |
      | /null | /null  | /null       | /null       | -1          | 63         | # The value is invalid. Param 'arg0' The query parameter 'sort_desc' has illegal value; allowed values: [application_version_id, is_active, api_subscription_id, api_version], provided value: '-1          |
      | /null | /null  | /null       | /null       | 0           | 63         | # The value is invalid. Param 'arg0' The query parameter 'sort_desc' has illegal value; allowed values: [application_version_id, is_active, api_subscription_id, api_version], provided value: '0           |
      | /null | /null  | /null       | /null       | nonExistent | 63         | # The value is invalid. Param 'arg0' The query parameter 'sort_desc' has illegal value; allowed values: [application_version_id, is_active, api_subscription_id, api_version], provided value: 'nonExistent |
      | /null | /null  | /null       | is_active   | is_active   | 64         | # The query parameter 'sort_desc' is not allowed. 'sort' and 'sort_desc' parameters shouldn't be both in one request!                                                                                       |


  Scenario Outline: Filtering list of api subscriptions
    Given The following application versions for application with id "6f552105-0bae-4410-b4bb-bee31567d4fa" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 1   | inactive | 2016-02-01  | Versions description 1 |
      | ab343111-12d3-4fde-ba8a-5ddff45d06d4 | 2            | Version 2   | inactive | 2016-02-02  | Versions description 2 |
      | 1820d135-4f75-4c64-a570-a89e8236229b | 3            | Version 3   | inactive | 2016-02-03  | Versions description 3 |
      | ac39d04c-bb58-4955-a7ab-c216e5444a54 | 4            | Version 4   | inactive | 2016-02-04  | Versions description 4 |
      | 315404f9-3ac8-4b75-8b54-1ea15702d046 | 5            | Version 5   | inactive | 2016-02-05  | Versions description 5 |

    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | apiVersion          |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | filter_api_1        |
      | 598cfe31-9583-4c0b-86a0-82e9bf662849 | ab343111-12d3-4fde-ba8a-5ddff45d06d4 | filter_api_2        |
      | 87eb138c-23c1-43d8-bc88-d6fbbd7e4359 | 1820d135-4f75-4c64-a570-a89e8236229b | filter_api_3        |
      | e2c258a3-a3e0-4895-82ee-5170934c5555 | ac39d04c-bb58-4955-a7ab-c216e5444a54 | second_filter_api_4 |
      | c432f76f-8fdd-4229-b8eb-e9007024b385 | 315404f9-3ac8-4b75-8b54-1ea15702d046 | second_filter_api_5 |

    When List of api subscriptions is got with limit "/null" and cursor "/null" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is 200
    And Content type is "application/json"
    And There are "<returned>" api subscriptions returned
    And There are api subscriptions with following codes returned in order: "<order>"
    Examples:
      | filter                                             | sort                | sort_desc           | returned | order                                                                                          |
      | api_version=='filter*'                             | /null               | /null               | 3        |                                                                                                |
      | api_version=='second*'                             | /null               | /null               | 2        |                                                                                                |
      | api_version=='second*'                             | api_version         | /null               | 2        | second_filter_api_4, second_filter_api_5                                                       |
      | api_version=='second*'                             | is_active           | /null               | 2        | second_filter_api_5, second_filter_api_4                                                       |
      | api_version=='second*'                             | api_subscription_id | /null               | 2        | second_filter_api_5, second_filter_api_4                                                       |
     # | api_version=='second*'                             | application_version_id | /null                  | 2        | second_filter_api_5, second_filter_api_4 |
      | api_version=='second*'                             | /null               | api_version         | 2        | second_filter_api_5, second_filter_api_4                                                       |
      | api_version=='*filter*'                            | /null               | /null               | 5        |                                                                                                |
      | api_version=='filter*' or api_version=='second*'   | /null               | /null               | 5        |                                                                                                |
      | api_version=='*filter*' and api_version=='second*' | /null               | /null               | 2        |                                                                                                |
      #| application_version_id=='NonExisting'              | /null                  | /null                  | 0        |                                          |
      #| application_version_id=='a*'                       | /null                  | /null                  | 2        |                                          |
      | is_active=='1'                                     | /null               | /null               | 0        |                                                                                                |
      #| is_active=='nonExistingInActiveField'              | /null                  | /null                  | 0        |                                          |
      | is_active=='1'                                     | is_active           | /null               | 0        |                                                                                                |
      | api_subscription_id=='5*'                          | /null               | /null               | 2        | filter_api_2, filter_api_1                                                                     |
      | api_subscription_id=='7*'                          | /null               | /null               | 0        |                                                                                                |
      | api_subscription_id=='*'                           | is_active           | /null               | 6        |                                                                                                |
      | /null                                              | /null               | is_active           | 6        | someString, filter_api_2, filter_api_1, filter_api_3, second_filter_api_5, second_filter_api_4 |
      | /null                                              |                     | is_active           | 6        | someString, filter_api_2, filter_api_1, filter_api_3, second_filter_api_5, second_filter_api_4 |
      | /null                                              | /null               | api_version         | 6        | someString, second_filter_api_5, second_filter_api_4, filter_api_3, filter_api_2, filter_api_1 |
      | /null                                              |                     | api_version         | 6        | someString, second_filter_api_5, second_filter_api_4, filter_api_3, filter_api_2, filter_api_1 |
      | /null                                              | /null               | api_subscription_id | 6        | second_filter_api_4, second_filter_api_5, filter_api_3, filter_api_1, filter_api_2, someString |
      | /null                                              |                     | api_subscription_id | 6        | second_filter_api_4, second_filter_api_5, filter_api_3, filter_api_1, filter_api_2, someString |
     # | /null                                              | /null               | application_version_id | 6        |                                          |
     # | /null                                              |                     | application_version_id | 6        |                                          |
      |                                                    |                     |                     | 6        |                                                                                                |
