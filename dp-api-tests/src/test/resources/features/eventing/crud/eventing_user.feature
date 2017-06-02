Feature: Eventing tests for User

  Background:
    Given Database is cleaned and default entities are created
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Following snapshot user is created without customer
      | type     | username      | firstName | lastName | email                | timezone      | languageCode |
      | snapshot | eventing_user | Default0  | User0    | def0@snapshot.travel | Europe/Prague | cs-CZ   |


  Scenario: Eventing user created
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Following snapshot user is created without customer
      | username            | firstName | lastName | email               | timezone      | languageCode |
      | event_user_create_1 | Snap      | Shot     | snp@snapshot.travel | Europe/Prague | cs-CZ   |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Create"
    And Notification in session id stands for user with username "event_user_create_1"

  Scenario: Eventing user deleted
    Given User with username "eventing_user" is stored in session under key "EVENTING_USER"
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When User "eventing_user" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Delete"
    And Notification in session id stands for user in session on key "EVENTING_USER"

  Scenario: Eventing user updated
    Given User with username "eventing_user" is stored in session under key "EVENTING_USER"
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When User "eventing_user" is updated with data
      | username          |
      | updated_user_name |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Update"
    And Notification in session id stands for user in session on key "EVENTING_USER"

    #    -------------------< Second level entities >-----------------

  Scenario: Add role to user and then remove it from him
    Given Switch for user customer role tests
    Given The following customers exist with random address
      | id                                   | name        | email          | salesforceId   | vatId      | isDemo         | phone         | website                    | timezone      |
      | 12300000-0000-4000-a000-000000000000 | Company 1   | c1@tenants.biz | salesforceid_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given Relation between user "eventing_user" and customer with id "12300000-0000-4000-a000-000000000000" exists
    Given The following roles exist
      | roleName      | description              | applicationId                        | id                                   |
      | eventing_role | background description 1 | 11111111-0000-4000-a000-111111111111 | 22222222-0000-4000-a000-111111111111 |
    Given Subscription with name "Test" for topic "Notifications.crud" is created
#    Create
    When User "eventing_user" assigns role "eventing_role" to relation between user "eventing_user" and customer "12300000-0000-4000-a000-000000000000"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "CustomerRole"
    And Notification in session id stands for role with name "eventing_role"
    And Notification in session operation is "Create"
    And Notification in session parent entity type is "User"
#    Delete
    When User "eventing_user" deletes role "eventing_role" from relation between user "eventing_user" and customer "12300000-0000-4000-a000-000000000000"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "CustomerRole"
    And Notification in session id stands for role with name "eventing_role"
    And Notification in session operation is "Delete"
    And Notification in session parent entity type is "User"
