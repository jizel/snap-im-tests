Feature: Eventing tests for Property

  Background:
    Given Database is cleaned and default entities are created
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | id                                   | companyName       | email           | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    And The following properties exist with random address and billing address
      | name         | propertyCode   | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | p0_name      | event_property | http://www.snapshot.travel | p0@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |


  Scenario: Eventing property created
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    And The following property is created with random address and billing address
      | name         | propertyCode        | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | p1_name      | event_prop_1_create | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | a792d2b2-3836-4207-a705-42bbecf3d881 |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Create"
    And Notification in session id stands for property with code "event_prop_1_create"

  Scenario: Eventing property created by nonsnapshot user - DP-1728
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName          | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | eventCustomerUser | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    And The following property is created with random address and billing address for user "eventCustomerUser"
      | salesforceId   | name         | propertyCode        | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | event_prop_1_create | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Create"
    And Notification in session id stands for property with code "event_prop_1_create"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Create"
    And Notification in session parent entity type is "Property"
    And Notification in session parent id stands for property with code "event_prop_1_create"

  Scenario: Eventing property deleted
    Given Property with code "event_property" is stored in session under key "EVENTING_PROPERTY"
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property with code "event_property" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Delete"
    And Notification in session id stands for property in session on key "EVENTING_PROPERTY"

  Scenario: Eventing property updated
    Given Property with code "event_property" is stored in session under key "EVENTING_PROPERTY"
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property with code "event_property" is updated with data
      | name         |
      | updated_name |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Update"
    And Notification in session id stands for property in session on key "EVENTING_PROPERTY"

#    -------------------< Second level entities >-----------------

  Scenario: Add and remove user to/from property
    Given Following snapshot user is created without customer
      | userType | userName   | firstName | lastName | email                        | timezone      | culture |
      | snapshot | event_user | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Subscription with name "Test" for topic "Notifications.crud" is created
#    Add
    When User "event_user" is added to property with code "event_property"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Create"
    And Notification in session id stands for user with username "event_user"
    And Notification in session parent entity type is "Property"
    And Notification in session parent id stands for property with code "event_property"
#    Remove
    When User "event_user" is removed from property with code "event_property"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Delete"
    And Notification in session id stands for user with username "event_user"
    And Notification in session parent entity type is "Property"
    And Notification in session parent id stands for property with code "event_property"

  Scenario: Update and Delete Property-Customer relationship events
    Given The following customers exist with random address
      | id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given Relation between property "event_property" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with is_active "false"
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property customer relationship for property with code "event_property" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is updated with
      | type  |
      | owner |
    Then Response code is "204"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "CustomerPropertyRelationship"
    And Notification in session operation is "Update"
    And Notification in session parent entity type is "Property"
    And Notification in session parent id stands for property with code "event_property"
    When Property customer relationship for property with code "event_property" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" is deleted
    Then Response code is "204"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "CustomerPropertyRelationship"
    And Notification in session operation is "Delete"
    And Notification in session parent entity type is "Property"
    And Notification in session parent id stands for property with code "event_property"