Feature: Eventing tests for Property Sets

  Background:
    Given Database is cleaned and default entities are created
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "a792d2b2-3836-4207-a705-42bbecf3d881" as primary "false"
      | Id                                   | userType | userName  | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | eventUser | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following property sets exist for customer with id "a792d2b2-3836-4207-a705-42bbecf3d881"
      | name                     | description            | type            |
      | event_background_propset | description            | brand           |


  Scenario: Eventing property set created
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When The following property set is created for customer with id "a792d2b2-3836-4207-a705-42bbecf3d881"
      | name              | description            | type            |
      | ps1_event_created | ps1_description        | brand           |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "PropertySet"
    And Notification in session operation is "Create"
    And Notification in session id stands for property set "ps1_event_created"

  Scenario: Eventing property set deleted
    Given Property set with name "event_background_propset" for customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is stored in session under key "EVENTING_PROPERTY_SET"
    Given Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property set "event_background_propset" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "PropertySet"
    And Notification in session operation is "Delete"
    And Notification in session id stands for property set in session on key "EVENTING_PROPERTY_SET"

  Scenario: Eventing property set updated
    Given Property set with name "event_background_propset" for customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is stored in session under key "EVENTING_PROPERTY_SET"
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property set "event_background_propset" is updated with following data
      | name                  |
      | udpated_prop_set_name |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "PropertySet"
    And Notification in session operation is "Update"
    And Notification in session id stands for property set in session on key "EVENTING_PROPERTY_SET"


#    -------------------< Second level entities >-----------------

#  DP-1913 - tests done for current implementation. Should be changed when (if) DP-1913 is done.
  Scenario: Add and remove property to and from property set
    Given The following properties exist with random address and billing address
      | salesforceId   | name         | propertyCode   | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | event_property | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |
    Given Subscription with name "Test" for topic "Notifications.crud" is created
#    Add
    When Property with code "event_property" is added to property set "event_background_propset"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "PropertySet"
    And Notification in session operation is "Create"
    And Notification in session id stands for property set "event_background_propset"
    And Notification in session parent entity type is "Property"
    And Notification in session parent id stands for property with code "event_property"
#    Remove
    When Property with code "event_property" is removed from property set "event_background_propset"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Delete"
    And Notification in session id stands for property with code "event_property"
    And Notification in session parent entity type is "PropertySet"
    And Notification in session parent id stands for property set with name "event_background_propset"


  Scenario: Add and remove user to and from property set
    Given Subscription with name "Test" for topic "Notifications.crud" is created
#    Add
    When User "eventUser" is added to property set with name "event_background_propset"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "PropertySet"
    And Notification in session operation is "Create"
    And Notification in session id stands for property set "event_background_propset"
    And Notification in session parent entity type is "User"
    And Notification in session parent id stands for user "eventUser"
#    Delete
    When User "eventUser" is removed from property set "event_background_propset"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "PropertySet"
    And Notification in session operation is "Delete"
    And Notification in session id stands for property set "event_background_propset"
    And Notification in session parent entity type is "User"
    And Notification in session parent id stands for user "eventUser"
