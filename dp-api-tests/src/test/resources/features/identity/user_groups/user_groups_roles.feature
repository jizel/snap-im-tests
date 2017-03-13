Feature: User groups roles

  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | customerId                           | companyName        | email          | salesforceId | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | UserGroupsCustomer | ug@tenants.biz | ug_sf_1      | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following user groups exist
      | userGroupId                          | customerId                           | name        | isActive | description          |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | 45a5f9e4-5351-4e41-9d20-fdb4609e9353 | userGroup_1 | false    | userGroupDescription |

    Given The following partner exist
      | partnerId                            | name         | email                   | website                    |
      | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | PartnerName1 | partner@snapshot.travel | http://www.snapshot.travel |
    Given The following applications exist
      | applicationName                       | website                    | applicationId                        | partnerId                            | isInternal |
      | Application for UserGroup-Roles tests | http://www.snapshot.travel | a318fd9a-a05d-42d8-8e84-42e904ace123 | e595fc9d-f5ca-45e7-a15d-c8a97108d884 | true       |
    Given Switch for user customer role tests
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 2d6e7db2-2ab8-40ae-8e71-3904d1512ec8 | 11111111-0000-4000-a000-111111111111 | UG role1 |
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" exists

  @Smoke
  Scenario: Create relationship UserGroup-Role
    Given The following roles exist
      | roleId                               | applicationId                        | roleName |
      | 65e928fc-fbe5-4863-95af-8ec1f24baa0d | a318fd9a-a05d-42d8-8e84-42e904ace123 | UG role2 |
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "65e928fc-fbe5-4863-95af-8ec1f24baa0d" exists
    Then Response code is 201
    And Body contains entity with attribute "role_id" value "65e928fc-fbe5-4863-95af-8ec1f24baa0d"
    And Relation between user group "userGroup_1" and role with id "65e928fc-fbe5-4863-95af-8ec1f24baa0d" is established

  Scenario Outline: Create relationship UserGroup-Role invalid
    When Relation between user group "<userGroupId>" and role "<roleId>" exists
    Then Response code is <error_code>
    And Custom code is <custom_code>
    Examples:
      | userGroupId                          | roleId                               |error_code | custom_code | # note                            |
      | NotExisting                          | /null                                | 422       | 42201      | # Empty body, invalid userGroupId |
      | NotExisting                          | NotExisting                          | 422       | 42201      | # Not in UUID                     |
      | NotExisting                          | b7b40d08-de38-4246-bb69-ad39c31c025c | 404       | 40402      | # UserGroup not found             |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | /null                                | 422       | 42201      | # Empty body                      |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | NotExisting                          | 422       | 42201      | # Not valid RoleId                |
      | a8b40d08-de38-4246-bb69-ad39c31c025c | b7b40d08-de38-4246-bb69-ad39c31c025c | 404       | 40402      | # Role not found                  |

  Scenario: Delete relationship UserGroup-Role
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is deleted
    Then Response code is 204
    And Body is empty
    And Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "2d6e7db2-2ab8-40ae-8e71-3904d1512ec8" is not established

  Scenario: Delete relationship UserGroup-Role invalid
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "NotExistingOne" is deleted
    Then Response code is 204
    And Body is empty
    And Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "NotExistingOne" is not established

  Scenario Outline: Get list of userGroup's role - valid
    Given The following roles exist
      | roleId                               | applicationId                        | roleName        |
      | 5184fb6b-0ebd-4726-9481-4858a15a37a0 | 11111111-0000-4000-a000-111111111111 | UG_filter_role1 |
      | 19e8d1c2-c4f7-44d7-b436-dd4e9249065d | 11111111-0000-4000-a000-111111111111 | UG_filter_role2 |
      | 540be550-1702-4e2e-b094-394de63f6c48 | 11111111-0000-4000-a000-111111111111 | UG_filter_role3 |
      | 7b570693-daf5-4208-8d09-370ff9a950b6 | 11111111-0000-4000-a000-111111111111 | UG_filter_role4 |
      | f40a9bf7-aa5e-473a-be31-2011324942fc | 11111111-0000-4000-a000-111111111111 | UG_filter_role5 |
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "5184fb6b-0ebd-4726-9481-4858a15a37a0" exists
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "19e8d1c2-c4f7-44d7-b436-dd4e9249065d" exists
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "540be550-1702-4e2e-b094-394de63f6c48" exists
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "7b570693-daf5-4208-8d09-370ff9a950b6" exists
    When Relation between user group "a8b40d08-de38-4246-bb69-ad39c31c025c" and role "f40a9bf7-aa5e-473a-be31-2011324942fc" exists
    When List of relationships userGroups-Roles for userGroup "userGroup_1" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is 200
    And Content type is "application/json"
    And There are "<returned>" relationships returned
    And There are relationships start with following IDs returned in order: "<order>"
    Examples:
      | limit | cursor | filter                           | sort    | sort_desc | returned | order                                                      | #note                                          |
      | /null | /null  | /null                            | /null   | /null     | 6        |                                                            | # all should be returned                       |
      |       |        | /null                            | /null   | /null     | 6        |                                                            | # empty parameter are ignored                  |
      |       | /null  | /null                            | /null   | /null     | 6        |                                                            | # empty limit parameter are ignored            |
      | /null | 0      | /null                            | /null   | /null     | 6        |                                                            | # cursor can be 0, all should be returned      |
      | /null |        | /null                            | /null   | /null     | 6        |                                                            | # empty cursor parameter are ignored           |
      | 1     | /null  | /null                            | /null   | /null     | 1        |                                                            | # limit param used                             |
      | 3     | 1      | /null                            | /null   | /null     | 3        |                                                            | # cursor < limit, limit param is used          |
      | 1     | 10     | /null                            | /null   | /null     | 0        |                                                            | # there are < 10 records, 0 should be returned |
      | 20    | 5      | /null                            | /null   | /null     | 1        |                                                            | # cursor > limit, last 1 should be returned    |
      | /null | /null  | /null                            | role_id | /null     | 6        | 19e8d1c2, 2d6e7db2, 5184fb6b, 540be550, 7b570693, f40a9bf7 |                                                |
      | /null | /null  | /null                            | /null   | role_id   | 6        | f40a9bf7, 7b570693, 540be550, 5184fb6b, 2d6e7db2, 19e8d1c2 |                                                |
      | /null | /null  | role_id=='19*'                   | /null   | /null     | 1        |                                                            |                                                |
      | /null | /null  | role_id=='5*'                    | role_id | /null     | 2        | 5184fb6b,540be550                                          |                                                |
      | /null | /null  | role_id=='5*'                    | /null   | role_id   | 2        | 540be550,5184fb6b                                          |                                                |
      | /null | /null  | role_id=='NotExistent'           | /null   | /null     | 0        |                                                            |                                                |
      | /null | /null  | role_id=='5*' and role_id=='19*' | /null   | /null     | 0        |                                                            |                                                |
      | /null | /null  | role_id=='5*' or role_id=='19*'  | /null   | /null     | 3        |                                                            |                                                |

  Scenario Outline: Get list of userGroup's role - invalid
    When List of relationships userGroups-Roles for userGroup "userGroup_1" is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is 400
    And Custom code is <error_code>
    Examples:
      | limit | cursor | filter    | sort        | sort_desc   | error_code |
      | -1    | /null  | /null     | /null       | /null       | 40002      |
      | text  | /null  | /null     | /null       | /null       | 40002      |
      | 9999  | /null  | /null     | /null       | /null       | 40002      |
      | /null | -1     | /null     | /null       | /null       | 40002      |
      | /null | text   | /null     | /null       | /null       | 40002      |
      | /null | /null  | -1        | /null       | /null       | 40002      |
      | /null | /null  | ==        | /null       | /null       | 40002      |
      | /null | /null  | role_id== | /null       | /null       | 40002      |
      | /null | /null  | /null     | -1          | /null       | 40002      |
      | /null | /null  | /null     | 0           | /null       | 40002      |
      | /null | /null  | /null     | nonExistent | /null       | 40002      |
      | /null | /null  | /null     | /null       | -1          | 40002      |
      | /null | /null  | /null     | /null       | 0           | 40002      |
      | /null | /null  | /null     | /null       | nonExistent | 40002      |
      | /null | /null  | /null     | role_id     | role_id     | 40002      |
