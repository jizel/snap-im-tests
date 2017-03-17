Feature: Eventing configuration module

  Background:
    Given Database is cleaned and default entities are created
    Given Subscription with name "configuration_eventing" for topic "Notifications.crud" does not exist


  Scenario: Eventing configurationType created
    Given Subscription with name "configuration_eventing" for topic "Notifications.crud" is created
    When Configuration type is created
      | identifier    | description                                       |
      | eventing_test | Description of created configuration identifier 1 |
    Then Message is received with subscription "configuration_eventing" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "ConfigurationType"
    And Notification in session operation is "Create"
    And Notification in session id stands for configuration type with identifier "eventing_test"

  Scenario: Eventing configurationType deleted
    Given The following configuration types exist
      | identifier           | description                                       |
      | eventing_test_delete | Description of created configuration identifier 1 |
    Given Subscription with name "configuration_eventing" for topic "Notifications.crud" is created
    When Configuration type with identifier "eventing_test_delete" is deleted
    Then Message is received with subscription "configuration_eventing" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "ConfigurationType"
    And Notification in session operation is "Delete"
    And Notification in session id stands for configuration type with identifier "eventing_test_delete"

  Scenario: Eventing configurationType update
    Given The following configuration types exist
      | identifier           | description                                       |
      | eventing_test_update | Description of created configuration identifier 1 |
    Given Subscription with name "configuration_eventing" for topic "Notifications.crud" is created
    When Configuration type description is updated for identifier "eventing_test_update" with description "new description"
    Then Message is received with subscription "configuration_eventing" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "ConfigurationType"
    And Notification in session operation is "Update"
    And Notification in session id stands for configuration type with identifier "eventing_test_update"


  Scenario: Eventing configuration created
    Given The following configuration types exist
      | identifier               | description                                       |
      | eventing_test_key_create | Description of created configuration identifier 1 |
    Given Subscription with name "configuration_eventing" for topic "Notifications.crud" is created
    When Configuration is created for configuration type "eventing_test_key_create"
      | key                 | value      | type   |
      | eventing_key_create | text value | string |
    Then Message is received with subscription "configuration_eventing" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Configuration"
    And Notification in session operation is "Create"
    And Notification in session id stands for configuration type with identifier "eventing_key_create"

  Scenario: Eventing configuration deleted
    Given The following configuration types exist
      | identifier               | description                                       |
      | eventing_test_key_delete | Description of created configuration identifier 1 |
    Given The following configurations exist for configuration type identifier "eventing_test_key_delete"
      | key                   | value      | type   |
      | eventing_key_delete_1 | text value | string |
    Given Subscription with name "configuration_eventing" for topic "Notifications.crud" is created
    When Configuration with identifier "eventing_key_delete_1" is deleted from identifier "eventing_test_key_delete"
    Then Message is received with subscription "configuration_eventing" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Configuration"
    And Notification in session operation is "Delete"
    And Notification in session id stands for configuration type with identifier "eventing_key_delete_1"

  Scenario: Eventing configuration updated
    Given The following configuration types exist
      | identifier               | description                                       |
      | eventing_test_key_update | Description of created configuration identifier 1 |
    Given The following configurations exist for configuration type identifier "eventing_test_key_update"
      | key                   | value      | type   |
      | eventing_key_update_1 | text value | string |
    Given Subscription with name "configuration_eventing" for topic "Notifications.crud" is created
    When Configuration with from identifier "eventing_test_key_update" is updated
      | key                   | value   | type   |
      | eventing_key_update_1 | updated | string |
    Then Message is received with subscription "configuration_eventing" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Configuration"
    And Notification in session operation is "Update"
    And Notification in session id stands for configuration type with identifier "eventing_key_update_1"
