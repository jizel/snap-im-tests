Feature: Eventing tests for Partner

  Background:
    Given Database is cleaned and default entities are created
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following partner exist
      | name                    | email          | website                    | vatId      | notes        | Id                                   |
      | Partner background name | p0@tenants.biz | http://www.snapshot.travel | CZ00000001 | Test notes 0 | abc00000-a05d-42d8-8e84-000000000000 |

  Scenario: Eventing partner created
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When The following partner is created
      | name                 | email          | website                    | vatId      | notes        |
      | Partner company name | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Partner"
    And Notification in session operation is "Create"
    And Notification in session id stands for partner with name "Partner company name"

  Scenario: Eventing partner updated
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Partner with id "abc00000-a05d-42d8-8e84-000000000000" is updated with data
      | name         | website                  | notes         |
      | updated_name | http://www.update.travel | updated notes |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Partner"
    And Notification in session operation is "Update"
    And Notification in session id stands for partner with name "updated_name"

  Scenario: Eventing partner deleted
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Partner with name "Partner background name" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Partner"
    And Notification in session operation is "Delete"
