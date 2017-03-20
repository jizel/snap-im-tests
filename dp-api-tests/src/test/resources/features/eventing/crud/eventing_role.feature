Feature: Eventing tests for roles

  Background:
    Given Database is cleaned and default entities are created
    Given Switch for user customer role tests
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following roles exist
      | roleName      | description              | applicationId                        |
      | eventing_role | background description 1 | 11111111-0000-4000-a000-111111111111 |


  Scenario: Eventing role created
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Role is created
      | roleName            | description            | applicationId                        |
      | event_role_create_1 | optional description 1 | 11111111-0000-4000-a000-111111111111 |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "CustomerRole"
    And Notification in session operation is "Create"
    And Notification in session id stands for role with name "event_role_create_1"

  Scenario: Eventing role deleted
    Given Role "eventing_role" is stored in session under key "EVENTING_ROLE"
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Role with name "eventing_role" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "CustomerRole"
    And Notification in session operation is "Delete"
    And Notification in session id stands for role in session on key "EVENTING_ROLE"

#  DP-1908
  @skipped
  Scenario: Eventing role updated
    Given Role "eventing_role" is stored in session under key "EVENTING_ROLE"
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Role with name "eventing_role" for application id "11111111-0000-4000-a000-111111111111" is updated with data
      | roleName          | description |
      | Updated Role Name | updated 1   |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "CustomerRole"
    And Notification in session operation is "Update"
    And Notification in session id stands for role in session on key "EVENTING_ROLE"