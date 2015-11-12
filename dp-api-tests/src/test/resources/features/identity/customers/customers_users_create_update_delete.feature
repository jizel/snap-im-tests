Feature: customers_users_create_update_delete

  Background:
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel |


    Given The following users exist
      | userType | userName  | firstName  | lastName  | email                | timezone  | culture |
      | customer | default1  | Default1   | User1     | def1@snapshot.travel | UTC+01:00 | cz      |
      | customer | default2  | Default2   | User2     | def2@snapshot.travel | UTC+01:00 | cz      |
      | customer | default3  | Default3   | User3     | def2@snapshot.travel | UTC+01:00 | cz      |

    Given Relation between user with username "default1" and customer with code "c1t" exists with isPrimary "true"
    Given Relation between user with username "default2" and customer with code "c1t" exists with isPrimary "false"


  Scenario: Adding user to customer with isPrimary set

    When User with username "default3" is added to customer with code "c2t" with isPrimary "true"
    Then Response code is "201"
    And Etag header is present

  #validate just one primary user, notexistent user, already present user
  #validate different type of users


  Scenario: Removing user from customer

    When User with username "default2" is removed from customer with code "c1t"
    Then Response code is "204"
    And Body is empty
    And User with username "default2" isn't there for customer with code "c1t"


  Scenario: Checking error code for removing user from customer
    When Nonexistent user is removed from customer with code "c1t"
    Then Response code is "204"

