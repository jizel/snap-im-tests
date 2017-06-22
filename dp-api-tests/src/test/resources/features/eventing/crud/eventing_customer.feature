Feature: Eventing tests for Customer related operations

  Background:
    Given Database is cleaned and default entities are created
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | id                                   | name                | email             | vatId       | isDemo         | phone         | website                    | timezone      |
      | 00000000-3836-4207-a705-000000000000 | Eventing Background | evbck@tenants.biz | CZ000123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |


  Scenario: Eventing customer created
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Customer is created with random address
      | id                                   | name              | email           | salesforceId    | vatId       | isDemo         | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | SALESFORCEID001 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Then Response code is "201"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "create"
    And Notification in session id stands for customer with id "a792d2b2-3836-4207-a705-42bbecf3d881"

  Scenario: Eventing customer deleted
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given Customer with id "00000000-3836-4207-a705-000000000000" is stored in session under key "EVENTING_CUSTOMER"
    When Customer with id "00000000-3836-4207-a705-000000000000" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "Delete"
    And Notification in session id stands for customer in session on key "EVENTING_CUSTOMER"

  Scenario: Eventing customer updated
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given Customer with id "00000000-3836-4207-a705-000000000000" is stored in session under key "EVENTING_CUSTOMER"
    When Customer with id "00000000-3836-4207-a705-000000000000" is updated with data
      | name                          |
      | Updated Eventing company name |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "Update"
    And Notification in session id stands for customer in session on key "EVENTING_CUSTOMER"
    When Customer with id "00000000-3836-4207-a705-000000000000" is activated
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "Update"
    And Notification in session id stands for customer in session on key "EVENTING_CUSTOMER"


#    -------------------< Second level entities >-----------------

  Scenario: Adding property to customer
    Given The following properties exist with random address and billing address
      | name                              | code                         | website                    | email           | isDemo         | timezone      | customerId                           |
      | Eventing property add to customer | cust_prop_add_property_event | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 00000000-3836-4207-a705-000000000000 |
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Relation between property with code "cust_prop_add_property_event" and customer with id "00000000-3836-4207-a705-000000000000" exists
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "CustomerPropertyRelationship"
    And Notification in session operation is "Create"
    And Notification in session parent entity type is "Customer"
    And Notification in session parent id stands for customer with id "00000000-3836-4207-a705-000000000000"

  Scenario: Updating property for customer
    Given The following properties exist with random address and billing address
      | name                              | code                            | website                    | email           | isDemo         | timezone      | customerId                           |
      | Eventing customer property update | cust_prop_update_property_event | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague | 00000000-3836-4207-a705-000000000000 |
    Given Relation between property with code "cust_prop_update_property_event" and customer with id "00000000-3836-4207-a705-000000000000" exists
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property with code "cust_prop_update_property_event" for customer with id "00000000-3836-4207-a705-000000000000" is updating field "valid_from" to value "2015-01-01"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "CustomerPropertyRelationship"
    And Notification in session operation is "Update"
    And Notification in session parent entity type is "Customer"
    And Notification in session parent id stands for customer with id "00000000-3836-4207-a705-000000000000"


  Scenario: Add and remove user to/from customer
    Given Following snapshot user is created without customer
      | type     | username   | firstName | lastName | email                        | timezone      | languageCode |
      | snapshot | event_user | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Subscription with name "Test" for topic "Notifications.crud" is created
#    Add
    When User "event_user" is added to customer with id "00000000-3836-4207-a705-000000000000" with isPrimary "true"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Create"
    And Notification in session id stands for user "event_user"
    And Notification in session parent entity type is "Customer"
    And Notification in session parent id stands for customer with id "00000000-3836-4207-a705-000000000000"
#    Remove
    When User "event_user" is removed from customer with id "00000000-3836-4207-a705-000000000000"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Delete"
    And Notification in session id stands for user "event_user"
    And Notification in session parent entity type is "Customer"
    And Notification in session parent id stands for customer with id "00000000-3836-4207-a705-000000000000"
