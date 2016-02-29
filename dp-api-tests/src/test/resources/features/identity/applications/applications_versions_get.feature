Feature: applications_versions_get

  Background: 
    Given Database is cleaned

  Scenario: Getting application versions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | 2016-02-22  | Versions description 1 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "version_id"
    And Body contains entity with attribute "version_name" value "Version 123"
    And Body contains entity with attribute "api_manager_id" value "123"
    And Body contains entity with attribute "status" value "inactive"
    And Body contains entity with attribute "release_date" value "2016-02-22"
    And Body contains entity with attribute "description" value "Versions description 1"

  Scenario: Getting application versions with etag
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | 2016-02-22  | Versions description 1 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is got with etag
    Then Response code is "304"
    And Body is empty

  Scenario: Getting application with not current etag
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description            |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 123 | inactive | 2016-02-22  | Versions description 1 |
    When Application version with id "a318fd9a-a05d-42d8-8e84-22e904ace111" for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" is got with etag, updated and got with previous etag
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "version_id"
    And Body contains entity with attribute "version_name" value "Version 123"
    And Body contains entity with attribute "api_manager_id" value "123"
    And Body contains entity with attribute "status" value "inactive"
    And Body contains entity with attribute "release_date" value "2016-02-22"
    And Body contains entity with attribute "description" value "UpdatedDescription"

  Scenario: Checking error code for nonexistent application versions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    When Nonexistent application version id is got for application id "a318fd9a-a05d-42d8-8e84-42e904ace123"
    Then Response code is "404"
    And Custom code is "152"

  Scenario Outline: Getting list of application versions
    Given The following applications exist
      | applicationName            | description               | website                    | applicationId                        |
      | Application test company 1 | Application description 1 | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following application versions for application with id "a318fd9a-a05d-42d8-8e84-42e904ace123" exists
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description             |
      | a318fd9a-a05d-42d8-8e84-22e904ace101 | 123          | Version 1   | inactive | 2016-02-01  | Versions description 1  |
      | a318fd9a-a05d-42d8-8e84-22e904ace102 | 123          | Version 2   | inactive | 2016-02-02  | Versions description 2  |
      | a318fd9a-a05d-42d8-8e84-22e904ace103 | 123          | Version 3   | inactive | 2016-02-03  | Versions description 3  |
      | a318fd9a-a05d-42d8-8e84-22e904ace104 | 123          | Version 4   | inactive | 2016-02-04  | Versions description 4  |
      | a318fd9a-a05d-42d8-8e84-22e904ace105 | 123          | Version 5   | inactive | 2016-02-05  | Versions description 5  |
      | a318fd9a-a05d-42d8-8e84-22e904ace106 | 123          | Version 6   | inactive | 2016-02-06  | Versions description 6  |
      | a318fd9a-a05d-42d8-8e84-22e904ace107 | 123          | Version 7   | inactive | 2016-02-07  | Versions description 7  |
      | a318fd9a-a05d-42d8-8e84-22e904ace108 | 123          | Version 8   | inactive | 2016-02-08  | Versions description 8  |
      | a318fd9a-a05d-42d8-8e84-22e904ace109 | 123          | Version 9   | inactive | 2016-02-09  | Versions description 9  |
      | a318fd9a-a05d-42d8-8e84-22e904ace110 | 123          | Version 10  | inactive | 2016-02-10  | Versions description 10 |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 11  | inactive | 2016-02-11  | Versions description 11 |
      | a318fd9a-a05d-42d8-8e84-22e904ace112 | 123          | Version 12  | inactive | 2016-02-12  | Versions description 12 |
      | a318fd9a-a05d-42d8-8e84-22e904ace113 | 123          | Version 13  | inactive | 2016-02-13  | Versions description 13 |
      | a318fd9a-a05d-42d8-8e84-22e904ace114 | 123          | Version 14  | inactive | 2016-02-14  | Versions description 14 |
      | a318fd9a-a05d-42d8-8e84-22e904ace115 | 123          | Version 15  | inactive | 2016-02-15  | Versions description 15 |
      | a318fd9a-a05d-42d8-8e84-22e904ace116 | 123          | Version 16  | inactive | 2016-02-16  | Versions description 16 |
      | a318fd9a-a05d-42d8-8e84-22e904ace117 | 123          | Version 17  | inactive | 2016-02-17  | Versions description 17 |
      | a318fd9a-a05d-42d8-8e84-22e904ace118 | 123          | Version 18  | inactive | 2016-02-18  | Versions description 18 |
      | a318fd9a-a05d-42d8-8e84-22e904ace119 | 123          | Version 19  | inactive | 2016-02-19  | Versions description 19 |
      | a318fd9a-a05d-42d8-8e84-22e904ace120 | 123          | Version 20  | inactive | 2016-02-20  | Versions description 20 |
      | a318fd9a-a05d-42d8-8e84-22e904ace121 | 123          | Version 21  | inactive | 2016-02-21  | Versions description 21 |
      | a318fd9a-a05d-42d8-8e84-22e904ace122 | 123          | Version 22  | inactive | 2016-02-22  | Versions description 22 |
      | a318fd9a-a05d-42d8-8e84-22e904ace123 | 123          | Version 23  | inactive | 2016-02-23  | Versions description 23 |
      | a318fd9a-a05d-42d8-8e84-22e904ace124 | 123          | Version 24  | inactive | 2016-02-24  | Versions description 24 |
      | a318fd9a-a05d-42d8-8e84-22e904ace125 | 123          | Version 25  | inactive | 2016-02-25  | Versions description 25 |
      | a318fd9a-a05d-42d8-8e84-22e904ace126 | 123          | Version 26  | inactive | 2016-02-26  | Versions description 26 |
      | a318fd9a-a05d-42d8-8e84-22e904ace127 | 123          | Version 27  | inactive | 2016-02-27  | Versions description 27 |
      | a318fd9a-a05d-42d8-8e84-22e904ace128 | 123          | Version 28  | inactive | 2016-02-28  | Versions description 28 |
      | a318fd9a-a05d-42d8-8e84-22e904ace129 | 123          | Version 29  | inactive | 2016-03-01  | Versions description 29 |
      | a318fd9a-a05d-42d8-8e84-22e904ace130 | 123          | Version 30  | inactive | 2016-03-02  | Versions description 30 |
      | a318fd9a-a05d-42d8-8e84-22e904ace131 | 123          | Version 31  | inactive | 2016-03-03  | Versions description 31 |
      | a318fd9a-a05d-42d8-8e84-22e904ace132 | 123          | Version 32  | inactive | 2016-03-04  | Versions description 32 |
      | a318fd9a-a05d-42d8-8e84-22e904ace133 | 123          | Version 33  | inactive | 2016-03-05  | Versions description 33 |
      | a318fd9a-a05d-42d8-8e84-22e904ace134 | 123          | Version 34  | inactive | 2016-03-06  | Versions description 34 |
      | a318fd9a-a05d-42d8-8e84-22e904ace135 | 123          | Version 35  | inactive | 2016-03-07  | Versions description 35 |
      | a318fd9a-a05d-42d8-8e84-22e904ace136 | 123          | Version 36  | inactive | 2016-03-08  | Versions description 36 |
      | a318fd9a-a05d-42d8-8e84-22e904ace137 | 123          | Version 37  | inactive | 2016-03-09  | Versions description 37 |
      | a318fd9a-a05d-42d8-8e84-22e904ace138 | 123          | Version 38  | inactive | 2016-03-10  | Versions description 38 |
      | a318fd9a-a05d-42d8-8e84-22e904ace139 | 123          | Version 39  | inactive | 2016-03-11  | Versions description 39 |
      | a318fd9a-a05d-42d8-8e84-22e904ace140 | 123          | Version 40  | inactive | 2016-03-12  | Versions description 40 |
      | a318fd9a-a05d-42d8-8e84-22e904ace141 | 123          | Version 41  | inactive | 2016-03-13  | Versions description 41 |
      | a318fd9a-a05d-42d8-8e84-22e904ace142 | 123          | Version 42  | inactive | 2016-03-14  | Versions description 42 |
      | a318fd9a-a05d-42d8-8e84-22e904ace143 | 123          | Version 43  | inactive | 2016-03-15  | Versions description 43 |
      | a318fd9a-a05d-42d8-8e84-22e904ace144 | 123          | Version 44  | inactive | 2016-03-16  | Versions description 44 |
      | a318fd9a-a05d-42d8-8e84-22e904ace145 | 123          | Version 45  | inactive | 2016-03-17  | Versions description 45 |
      | a318fd9a-a05d-42d8-8e84-22e904ace146 | 123          | Version 46  | inactive | 2016-03-18  | Versions description 46 |
      | a318fd9a-a05d-42d8-8e84-22e904ace147 | 123          | Version 47  | inactive | 2016-03-19  | Versions description 47 |
      | a318fd9a-a05d-42d8-8e84-22e904ace148 | 123          | Version 48  | inactive | 2016-03-20  | Versions description 48 |
      | a318fd9a-a05d-42d8-8e84-22e904ace149 | 123          | Version 49  | inactive | 2016-03-21  | Versions description 49 |
      | a318fd9a-a05d-42d8-8e84-22e904ace150 | 123          | Version 50  | inactive | 2016-03-22  | Versions description 50 |
      | a318fd9a-a05d-42d8-8e84-22e904ace151 | 123          | Version 51  | inactive | 2016-03-23  | Versions description 51 |
      | a318fd9a-a05d-42d8-8e84-22e904ace152 | 123          | Version 52  | inactive | 2016-03-24  | Versions description 52 |
    When List of application versions is got for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> application versions returned
    And Link header is '<link_header>'
    And Total count is "<total>"

    Examples: 
      | limit | cursor | returned | total | link_header                                                                                                                                                                                                 |
      | /null |        | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=50&cursor=50>; rel="next"                                                                                                       |
      | /null | /null  | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=50&cursor=50>; rel="next"                                                                                                       |
      |       |        | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=50&cursor=50>; rel="next"                                                                                                       |
      |       | /null  | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=50&cursor=50>; rel="next"                                                                                                       |
      | 15    |        | 15       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=15&cursor=15>; rel="next"                                                                                                       |
      |       | 1      | 50       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=50&cursor=51>; rel="next", </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=50&cursor=0>; rel="prev" |
      | 20    | 0      | 20       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=20&cursor=20>; rel="next"                                                                                                       |
      | 10    | 0      | 10       | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=10&cursor=10>; rel="next"                                                                                                       |
      | 5     | 10     | 5        | 52    | </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=5&cursor=15>; rel="next", </identity/applications/a318fd9a-a05d-42d8-8e84-42e904ace123/versions?limit=5&cursor=5>; rel="prev"   |

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
      | versionId                            | apiManagerId | versionName | status   | releaseDate | description             |
      | a318fd9a-a05d-42d8-8e84-22e904ace101 | 123          | Version 1   | inactive | 2016-02-01  | Versions description 1  |
      | a318fd9a-a05d-42d8-8e84-22e904ace102 | 123          | Version 2   | inactive | 2016-02-02  | Versions description 2  |
      | a318fd9a-a05d-42d8-8e84-22e904ace103 | 123          | Version 3   | inactive | 2016-02-03  | Versions description 3  |
      | a318fd9a-a05d-42d8-8e84-22e904ace104 | 123          | Version 4   | inactive | 2016-02-04  | Versions description 4  |
      | a318fd9a-a05d-42d8-8e84-22e904ace105 | 123          | Version 5   | inactive | 2016-02-05  | Versions description 5  |
      | a318fd9a-a05d-42d8-8e84-22e904ace106 | 123          | Version 6   | inactive | 2016-02-06  | Versions description 6  |
      | a318fd9a-a05d-42d8-8e84-22e904ace107 | 123          | Version 7   | inactive | 2016-02-07  | Versions description 7  |
      | a318fd9a-a05d-42d8-8e84-22e904ace108 | 123          | Version 8   | inactive | 2016-02-08  | Versions description 8  |
      | a318fd9a-a05d-42d8-8e84-22e904ace109 | 123          | Version 9   | inactive | 2016-02-09  | Versions description 9  |
      | a318fd9a-a05d-42d8-8e84-22e904ace110 | 123          | Version 10  | inactive | 2016-02-10  | Versions description 10 |
      | a318fd9a-a05d-42d8-8e84-22e904ace111 | 123          | Version 11  | inactive | 2016-02-11  | Versions description 11 |
      | a318fd9a-a05d-42d8-8e84-22e904ace112 | 123          | Version 12  | inactive | 2016-02-12  | Versions description 12 |
    When List of application versions is got for application id "a318fd9a-a05d-42d8-8e84-42e904ace123" with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> application versions returned
    And There are application version with following names returned in order: <expected_names>
    And Total count is "<total>"

    Examples: 
      | limit | cursor | returned | total | filter                                                     | sort        | sort_desc   | expected_names                                           |
      | 5     | 0      | 5        | 12    | version_name=='Version*'                                   | description | /null       | Version 1, Version 10, Version 11, Version 12, Version 2 |
      | 5     | 0      | 5        | 12    | version_name=='Version*'                                   | /null       | description | Version 9, Version 8, Version 7, Version 6, Version 5    |
      | 5     | 2      | 5        | 12    | version_name=='Version*'                                   | description | /null       | Version 11, Version 12, Version 2, Version 3, Version 4  |
      | 5     | 2      | 5        | 12    | version_name=='Version*'                                   | /null       | description | Version 7, Version 6, Version 5, Version 4, Version 3    |
      | 5     | 3      | 5        | 12    | version_name=='Version*'                                   | description | /null       | Version 12, Version 2, Version 3, Version 4, Version 5   |
      | /null | /null  | 1        | 1     | description=='*description 2'                              | /null       | /null       | Version 2                                                |
      | /null | /null  | 1        | 1     | release_date=='2016-02-08'                                 | /null       | /null       | Version 8                                                |
      | /null | /null  | 1        | 1     | version_name=='Version*' and description=='*description 2' | /null       | /null       | Version 2                                                |
