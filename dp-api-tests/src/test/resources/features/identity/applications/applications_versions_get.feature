Feature: Applications versions get

  Background:
    Given Database is cleaned

  @Smoke
  Scenario: Getting application versions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | Versions description 1 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "application_version_id"
    And Body contains entity with attribute "name" value "Version 123"
    And Body contains entity with attribute "api_manager_id" value "123"
    And Body contains entity with attribute "status" value "inactive"
    And Body contains entity with attribute "description" value "Versions description 1"

  Scenario: Getting application versions with etag
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | Versions description 1 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is got with etag
    Then Response code is "304"
    And Body is empty

  Scenario: Getting application with not current etag
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | Versions description 1 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is got with etag, updated and got with previous etag
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "application_version_id"
    And Body contains entity with attribute "name" value "Version 123"
    And Body contains entity with attribute "api_manager_id" value "123"
    And Body contains entity with attribute "status" value "inactive"
    And Body contains entity with attribute "description" value "UpdatedDescription"

  Scenario: Checking error code for nonexistent application versions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    When Nonexistent application version id is got for application id "a318fd9a-a05d-42d8-8e84-42e904ace123"
    Then Response code is "404"
    And Custom code is "40402"

  Scenario Outline: Getting list of application versions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | description             |
      | a318fd9a-a05d-42d8-8e84-22e904ace101 | 123          | Version 1   | inactive | Versions description 1  |
      | a318fd9a-a05d-42d8-8e84-22e904ace102 | 123          | Version 2   | inactive | Versions description 2  |
      | a318fd9a-a05d-42d8-8e84-22e904ace103 | 123          | Version 3   | inactive | Versions description 3  |
      | a318fd9a-a05d-42d8-8e84-22e904ace104 | 123          | Version 4   | inactive | Versions description 4  |
      | a318fd9a-a05d-42d8-8e84-22e904ace105 | 123          | Version 5   | inactive | Versions description 5  |
      | a318fd9a-a05d-42d8-8e84-22e904ace106 | 123          | Version 6   | inactive | Versions description 6  |
      | a318fd9a-a05d-42d8-8e84-22e904ace107 | 123          | Version 7   | inactive | Versions description 7  |
      | a318fd9a-a05d-42d8-8e84-22e904ace108 | 123          | Version 8   | inactive | Versions description 8  |
      | a318fd9a-a05d-42d8-8e84-22e904ace109 | 123          | Version 9   | inactive | Versions description 9  |
      | a318fd9a-a05d-42d8-8e84-22e904ace110 | 123          | Version 10  | inactive | Versions description 10 |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 11  | inactive | Versions description 11 |
      | a318fd9a-a05d-42d8-8e84-22e904ace112 | 123          | Version 12  | inactive | Versions description 12 |
      | a318fd9a-a05d-42d8-8e84-22e904ace113 | 123          | Version 13  | inactive | Versions description 13 |
      | a318fd9a-a05d-42d8-8e84-22e904ace114 | 123          | Version 14  | inactive | Versions description 14 |
      | a318fd9a-a05d-42d8-8e84-22e904ace115 | 123          | Version 15  | inactive | Versions description 15 |
      | a318fd9a-a05d-42d8-8e84-22e904ace116 | 123          | Version 16  | inactive | Versions description 16 |
      | a318fd9a-a05d-42d8-8e84-22e904ace117 | 123          | Version 17  | inactive | Versions description 17 |
      | a318fd9a-a05d-42d8-8e84-22e904ace118 | 123          | Version 18  | inactive | Versions description 18 |
      | a318fd9a-a05d-42d8-8e84-22e904ace119 | 123          | Version 19  | inactive | Versions description 19 |
      | a318fd9a-a05d-42d8-8e84-22e904ace120 | 123          | Version 20  | inactive | Versions description 20 |
      | a318fd9a-a05d-42d8-8e84-22e904ace121 | 123          | Version 21  | inactive | Versions description 21 |
      | a318fd9a-a05d-42d8-8e84-22e904ace122 | 123          | Version 22  | inactive | Versions description 22 |
      | a318fd9a-a05d-42d8-8e84-22e904ace123 | 123          | Version 23  | inactive | Versions description 23 |
      | a318fd9a-a05d-42d8-8e84-22e904ace124 | 123          | Version 24  | inactive | Versions description 24 |
      | a318fd9a-a05d-42d8-8e84-22e904ace125 | 123          | Version 25  | inactive | Versions description 25 |
      | a318fd9a-a05d-42d8-8e84-22e904ace126 | 123          | Version 26  | inactive | Versions description 26 |
      | a318fd9a-a05d-42d8-8e84-22e904ace127 | 123          | Version 27  | inactive | Versions description 27 |
      | a318fd9a-a05d-42d8-8e84-22e904ace128 | 123          | Version 28  | inactive | Versions description 28 |
      | a318fd9a-a05d-42d8-8e84-22e904ace129 | 123          | Version 29  | inactive | Versions description 29 |
      | a318fd9a-a05d-42d8-8e84-22e904ace130 | 123          | Version 30  | inactive | Versions description 30 |
      | a318fd9a-a05d-42d8-8e84-22e904ace131 | 123          | Version 31  | inactive | Versions description 31 |
      | a318fd9a-a05d-42d8-8e84-22e904ace132 | 123          | Version 32  | inactive | Versions description 32 |
      | a318fd9a-a05d-42d8-8e84-22e904ace133 | 123          | Version 33  | inactive | Versions description 33 |
      | a318fd9a-a05d-42d8-8e84-22e904ace134 | 123          | Version 34  | inactive | Versions description 34 |
      | a318fd9a-a05d-42d8-8e84-22e904ace135 | 123          | Version 35  | inactive | Versions description 35 |
      | a318fd9a-a05d-42d8-8e84-22e904ace136 | 123          | Version 36  | inactive | Versions description 36 |
      | a318fd9a-a05d-42d8-8e84-22e904ace137 | 123          | Version 37  | inactive | Versions description 37 |
      | a318fd9a-a05d-42d8-8e84-22e904ace138 | 123          | Version 38  | inactive | Versions description 38 |
      | a318fd9a-a05d-42d8-8e84-22e904ace139 | 123          | Version 39  | inactive | Versions description 39 |
      | a318fd9a-a05d-42d8-8e84-22e904ace140 | 123          | Version 40  | inactive | Versions description 40 |
      | a318fd9a-a05d-42d8-8e84-22e904ace141 | 123          | Version 41  | inactive | Versions description 41 |
      | a318fd9a-a05d-42d8-8e84-22e904ace142 | 123          | Version 42  | inactive | Versions description 42 |
      | a318fd9a-a05d-42d8-8e84-22e904ace143 | 123          | Version 43  | inactive | Versions description 43 |
      | a318fd9a-a05d-42d8-8e84-22e904ace144 | 123          | Version 44  | inactive | Versions description 44 |
      | a318fd9a-a05d-42d8-8e84-22e904ace145 | 123          | Version 45  | inactive | Versions description 45 |
      | a318fd9a-a05d-42d8-8e84-22e904ace146 | 123          | Version 46  | inactive | Versions description 46 |
      | a318fd9a-a05d-42d8-8e84-22e904ace147 | 123          | Version 47  | inactive | Versions description 47 |
      | a318fd9a-a05d-42d8-8e84-22e904ace148 | 123          | Version 48  | inactive | Versions description 48 |
      | a318fd9a-a05d-42d8-8e84-22e904ace149 | 123          | Version 49  | inactive | Versions description 49 |
      | a318fd9a-a05d-42d8-8e84-22e904ace150 | 123          | Version 50  | inactive | Versions description 50 |
      | a318fd9a-a05d-42d8-8e84-22e904ace151 | 123          | Version 51  | inactive | Versions description 51 |
      | a318fd9a-a05d-42d8-8e84-22e904ace152 | 123          | Version 52  | inactive | Versions description 52 |
    When List of application versions is got for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> application versions returned
    And Link header is '<link_header>'
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | link_header                                                                                                                                                                                                             |
      | /null |        | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=50&cursor=50>; rel="next"                                                                                                       |
      | /null | /null  | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=50&cursor=50>; rel="next"                                                                                                       |
      |       |        | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=50&cursor=50>; rel="next"                                                                                                       |
      |       | /null  | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=50&cursor=50>; rel="next"                                                                                                       |
      | 15    |        | 15       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=15&cursor=15>; rel="next"                                                                                                       |
      |       | 1      | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=50&cursor=51>; rel="next", </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=50&cursor=0>; rel="prev" |
      | 20    | 0      | 20       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=20&cursor=20>; rel="next"                                                                                                       |
      | 10    | 0      | 10       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=10&cursor=10>; rel="next"                                                                                                       |
      | 5     | 10     | 5        | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=5&cursor=15>; rel="next", </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/application_versions?limit=5&cursor=5>; rel="prev"   |

  Scenario Outline: Checking error codes for getting list of application versions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    When List of application versions is got for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | limit       | cursor | filter | sort        | sort_desc   | response_code | custom_code |
      | /null       | -1     | /null  | /null       | /null       | 400           | 63          |
      |             | -1     | /null  | /null       | /null       | 400           | 63          |
      | /null       | text   | /null  | /null       | /null       | 400           | 63          |
      |             | text   | /null  | /null       | /null       | 400           | 63          |
      | -1          |        | /null  | /null       | /null       | 400           | 63          |
      | -1          | /null  | /null  | /null       | /null       | 400           | 63          |
      | 201         | /null  | /null  | /null       | /null       | 400           | 63          |
      | 21474836470 | /null  | /null  | /null       | /null       | 400           | 63          |
      | text        |        | /null  | /null       | /null       | 400           | 63          |
      | text        | /null  | /null  | /null       | /null       | 400           | 63          |
      | 10          | -1     | /null  | /null       | /null       | 400           | 63          |
      | text        | 0      | /null  | /null       | /null       | 400           | 63          |
      | 10          | text   | /null  | /null       | /null       | 400           | 63          |
      | 10          | 0      | /null  | description | description | 400           | 64          |
      | 10          | 0      | /null  | /null       | nonexistent | 400           | 63          |
      | 10          | 0      | /null  | nonexistent | /null       | 400           | 63          |
      | 10          | 0      | code== | /null       | /null       | 400           | 63          |

  Scenario Outline: Filtering list of application versions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | description             |
      | a318fd9a-a05d-42d8-8e84-22e904ace101 | 123          | Version 1   | inactive | Versions description 1  |
      | a318fd9a-a05d-42d8-8e84-22e904ace102 | 123          | Version 2   | inactive | Versions description 2  |
      | a318fd9a-a05d-42d8-8e84-22e904ace103 | 123          | Version 3   | inactive | Versions description 3  |
      | a318fd9a-a05d-42d8-8e84-22e904ace104 | 123          | Version 4   | inactive | Versions description 4  |
      | a318fd9a-a05d-42d8-8e84-22e904ace105 | 123          | Version 5   | inactive | Versions description 5  |
      | a318fd9a-a05d-42d8-8e84-22e904ace106 | 123          | Version 6   | inactive | Versions description 6  |
      | a318fd9a-a05d-42d8-8e84-22e904ace107 | 123          | Version 7   | inactive | Versions description 7  |
      | a318fd9a-a05d-42d8-8e84-22e904ace108 | 123          | Version 8   | inactive | Versions description 8  |
      | a318fd9a-a05d-42d8-8e84-22e904ace109 | 123          | Version 9   | inactive | Versions description 9  |
      | a318fd9a-a05d-42d8-8e84-22e904ace110 | 123          | Version 10  | inactive | Versions description 10 |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 11  | inactive | Versions description 11 |
      | a318fd9a-a05d-42d8-8e84-22e904ace112 | 123          | Version 12  | inactive | Versions description 12 |
    When List of application versions is got for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> application versions returned
    And There are application version with following names returned in order: <expected_names>
    And Total count is "<total>"

    Examples:
      | limit | cursor | returned | total | filter                                             | sort        | sort_desc   | expected_names                                           |
      | 5     | 0      | 5        | 12    | name=='Version*'                                   | description | /null       | Version 1, Version 10, Version 11, Version 12, Version 2 |
      | 5     | 0      | 5        | 12    | name=='Version*'                                   | /null       | description | Version 9, Version 8, Version 7, Version 6, Version 5    |
      | 5     | 2      | 5        | 12    | name=='Version*'                                   | description | /null       | Version 11, Version 12, Version 2, Version 3, Version 4  |
      | 5     | 2      | 5        | 12    | name=='Version*'                                   | /null       | description | Version 7, Version 6, Version 5, Version 4, Version 3    |
      | 5     | 3      | 5        | 12    | name=='Version*'                                   | description | /null       | Version 12, Version 2, Version 3, Version 4, Version 5   |
      | /null | /null  | 1        | 1     | description=='*description 2'                      | /null       | /null       | Version 2                                                |
      | /null | /null  | 1        | 1     | name=='Version*' and description=='*description 2' | /null       | /null       | Version 2                                                |
