Feature: User groups get

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName        | email          | code | salesforceId | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | UserGroupsCustomer | ug@tenants.biz | ug1  | ug_sf_1      | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following user groups exist
      | userGroupId                          | customerId                           | name        | isActive | description          |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_1 | false    | userGroupDescription |

  @Smoke
  Scenario: Getting user group
    When User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is got
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "user_group_id" value "a8b40d08-de38-4246-bb69-ad39c31c025c"
    And Body contains entity with attribute "name" value "userGroup_1"
    And Body contains entity with attribute "customer_id" value "45a5f9e4-5351-4e41-9d20-fdb4609e9353"
    And Body contains entity with attribute "is_active" value "false"
    And Body contains entity with attribute "description" value "userGroupDescription"

  Scenario: Getting user group with etag
    When User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is got with etag
    Then Response code is "304"
    And Body is empty

  Scenario: Getting user group with not current etag
    When User group with id "a8b40d08-de38-4246-bb69-ad39c31c025c" is got for etag, updated and got with previous etag
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "user_group_id" value "a8b40d08-de38-4246-bb69-ad39c31c025c"
    And Body contains entity with attribute "name" value "userGroup_1"
    And Body contains entity with attribute "customer_id" value "45a5f9e4-5351-4e41-9d20-fdb4609e9353"
    And Body contains entity with attribute "is_active" value "false"
    And Body contains entity with attribute "description" value "updatedDescription"

  Scenario: Getting nonExistent user group
    When User group with id "nonExistentUserGroup" is got
    Then Response code is "404"
    And Custom code is 152


  Scenario Outline: Getting list of user groups using limit and cursor
    Given The following user groups exist
      | customerId                           | name         | isActive | description            |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_1  | false    | userGroupDescription1  |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_2  | false    | userGroupDescription2  |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_3  | false    | userGroupDescription3  |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_4  | false    | userGroupDescription4  |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_5  | false    | userGroupDescription5  |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_6  | false    | userGroupDescription6  |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_7  | false    | userGroupDescription7  |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_8  | false    | userGroupDescription8  |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_9  | false    | userGroupDescription9  |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_10 | false    | userGroupDescription10 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_11 | false    | userGroupDescription11 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_12 | false    | userGroupDescription12 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_13 | false    | userGroupDescription13 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_14 | false    | userGroupDescription14 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_15 | false    | userGroupDescription15 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_16 | false    | userGroupDescription16 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_17 | false    | userGroupDescription17 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_18 | false    | userGroupDescription18 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_19 | false    | userGroupDescription19 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_20 | false    | userGroupDescription20 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_21 | false    | userGroupDescription21 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_22 | false    | userGroupDescription22 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_23 | false    | userGroupDescription23 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_24 | false    | userGroupDescription24 |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_25 | false    | userGroupDescription25 |
    When List of user groups is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is 200
    And Content type is "application/json"
    And There are "<returned>" user groups returned
    And Total count is "26"
    Examples:
      | limit | cursor | returned | note                                                         |
      | /null | /null  | 26       | # all should be returned                                     |
      | 10    | /null  | 10       | # limit parameter should be used                             |
      |       |        | 26       | # empty parameter are ignored, all should be returned        |
      |       | /null  | 26       | # empty limit parameter, all should be returned              |
      | /null |        | 26       | # empty cursor parameter, all should be returned             |
      | 10    | 1      | 10       | # cursor < limit, limit param is used, 10 should be returned |
      | 1     | 10     | 1        | # cursor > limit, limit param is used, 1 should be returned  |
      | 10    | 22     | 4        | # cursor > limit, last 4 should be returned                  |
      | /null | 0      | 26       | # cursor can be 0, all should be returned                    |


  Scenario Outline: Getting list of user groups with invalid query parameters
    When List of user groups is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is 400
    And Custom code is <customCode>

    Examples:
      | limit | cursor | filter      | sort        | sort_desc   | customCode | note                                                                                                                                                                                                        |
      | -1    | /null  | /null       | /null       | /null       | 63         | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
      | text  | /null  | /null       | /null       | /null       | 63         | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
      | 9999  | /null  | /null       | /null       | /null       | 63         | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
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

  Scenario Outline: Getting list of user groups
    Given The following user groups exist
      | userGroupId                          | customerId                           | name         | isActive | description |
      | 580bc988-885c-4ca6-b369-37dfcd123665 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_1  | false    | ug1         |
      | bfbdf91e-523b-4ad9-9ed5-178a1f67e785 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_2  | false    | ug2         |
      | 4c5693af-57c1-4986-b86e-0f5e6f3df95a | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_3  | false    | ug3         |
      | 166a3476-8bbb-4ca0-8d15-a5190755af2f | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_4  | false    | ug4         |
      | a482ba3c-ecee-4531-b773-e7fdad4b2873 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_5  | false    | ug5         |
      | 4e4330b5-f0e4-47e0-9781-b183104b05ec | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_6  | false    | ug6         |
      | 0a130595-1d36-4b04-b329-f177a9d31d46 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_7  | false    | ug7         |
      | edb2fe48-9481-483b-849a-abee1deced8a | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_8  | false    | ug8         |
      | 6633c329-7449-4aef-9356-ff86773f64e5 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_9  | false    | ug9         |
      | 30557c23-fb07-4dd9-b6f3-d391902fcc8c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_10 | false    | ug10        |
      | 0339675f-9f48-4112-8aa6-6b51dd491ba5 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_11 | false    | ug11        |
      | 95a18d0e-7c5b-4542-a772-258833bd730d | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_12 | false    | ug12        |
      | 64b37114-6abb-4ef8-ab4d-f1d53c817a20 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_13 | false    | ug13        |
      | 5c6a5ab1-93d6-43a8-88e7-c334a6017d68 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_14 | false    | ug14        |
      | 41fbad33-b50d-482d-bc7f-a859471459a8 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_15 | false    | ug15        |
      | 88a6a59b-c90c-4bf7-b907-a6d0eac85a0a | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_16 | false    | ug16        |
      | d8f6919f-66ce-42c6-9a22-208205aa08d3 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_17 | false    | ug17        |
      | abae7fb2-bcf7-4b40-8888-893ffb4471de | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_18 | false    | ug18        |
      | e0f1d172-cea0-4170-a87e-f756f9effde4 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_19 | false    | ug19        |
      | 3c7f8c36-23e1-46ce-a5ba-2e63a45bca4e | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_20 | false    | ug20        |
      | 7b1f2430-af3d-4cd6-ab18-9f75f8d4179e | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_21 | false    | ug21        |
      | 25edda7f-4f95-499e-a9c0-b7ec9aea52b7 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_22 | false    | ug22        |
      | 48ba7ff5-1ca7-4a9c-a985-befb31a56c99 | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_23 | false    | ug23        |
      | 5dbc2c4e-4ab3-4858-b8bd-ed536b00206e | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_24 | false    | ug24        |
      | 657a54b4-60ea-4356-bc7b-9fe1cad9f22a | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_25 | false    | ug25        |
    When List of user groups is got with limit "/null" and cursor "/null" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is 200
    And Content type is "application/json"
    And There are "<returned>" user groups returned
    And There are user groups with following description returned in order: "<user_group_description_order>"
    Examples:
      | filter                                              | sort        | sort_desc   | returned | user_group_description_order |
      | /null                                               | customer_id | /null       | 26       |                              |
      | /null                                               | is_active   | /null       | 26       |                              |
      | /null                                               | description | /null       | 26       |                              |
      | /null                                               | name        | /null       | 26       |                              |
      | /null                                               | /null       | customer_id | 26       |                              |
      | /null                                               | /null       | is_active   | 26       |                              |
      | /null                                               | /null       | description | 26       |                              |
      | /null                                               | /null       | name        | 26       |                              |
      | user_group_id=='5*'                                 | /null       | /null       | 3        |                              |
      | user_group_id=='*a'                                 | /null       | /null       | 4        |                              |
      | user_group_id=='*-4*'                               | /null       | /null       | 26       |                              |
      | user_group_id=='z*'                                 | /null       | /null       | 0        |                              |
      | user_group_id=='5*'                                 | customer_id | /null       | 3        | ug1,ug14,ug24                |
      | user_group_id=='5*'                                 | is_active   | /null       | 3        | ug1,ug14,ug24                |
      | user_group_id=='5*'                                 | description | /null       | 3        | ug1,ug14,ug24                |
      | user_group_id=='5*'                                 | name        | /null       | 3        | ug1,ug14,ug24                |
      | user_group_id=='5*'                                 | /null       | customer_id | 3        | ug1,ug14,ug24                |
      | user_group_id=='5*'                                 | /null       | is_active   | 3        | ug1,ug14,ug24                |
      | user_group_id=='5*'                                 | /null       | description | 3        | ug24,ug14,ug1                |
      | user_group_id=='5*'                                 | /null       | name        | 3        | ug24,ug14,ug1                |
      | customer_id=='45a5f9e4-5351-4e41-9d20-fdb4609e9353' | /null       | /null       | 26       |                              |
      | customer_id=='NotExistentShit'                      | /null       | /null       | 0        |                              |
      | is_active==false                                    | /null       | /null       | 26       |                              |
      | is_active==true                                     | /null       | /null       | 0        |                              |
      | description=='ug1'                                  | /null       | /null       | 1        |                              |
      | description=='ug1*'                                 | /null       | /null       | 11       |                              |
      | description=='NotExistentShit'                      | /null       | /null       | 0        |                              |
      | name=='userGroup*'                                  | /null       | /null       | 26       |                              |
      | name=='NotExistentShit'                             | /null       | /null       | 0        |                              |
      | is_active==false and name=='userGroup*'             | /null       | /null       | 26       |                              |
      | is_active==false or name=='userGroup*'              | /null       | /null       | 26       |                              |
      | is_active==true and name=='userGroup*'              | /null       | /null       | 0        |                              |
      | is_active==true or name=='userGroup*'               | /null       | /null       | 26       |                              |