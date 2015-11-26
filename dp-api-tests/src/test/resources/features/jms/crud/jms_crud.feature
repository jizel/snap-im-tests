Feature: Message Transactions

  Scenario: Eventing customer created
    Customer clean if existed, then subscription is clean and recreated,
    then is customer is created and then notification is checked.

    Given Customer with code "ev1t" does not exist
    Given Subscription with name "Test" for topic "Notifications" does not exist
    Given Subscription with name "Test" for topic "Notifications" is created
    When Customer is created with random address
      | companyName       | email           | code | salesforceId         | vatId     | isDemoCustomer | phone         | website                    |
      | Eventing  company | ev1@tenants.biz | ev1t | salesforceid_event_1 | CZ0000333 | true           | +420123456789 | http://www.snapshot.travel |
    Then Message is received with subscription "Test" from topic "Notifications" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "create"
    And Notification in session id stands for customer with code "ev1t"
    And Subscription with name "Test" for topic "Notifications" is unsubscribed


  Scenario: Eventing customer deleted
    Given Subscription with name "Test" for topic "Notifications" does not exist

    Given The following customers exist with random address
      | companyName              | email            | code  | salesforceId             | vatId      | isDemoCustomer | phone         | website                    |
      | Delete Eventing  company | dev1@tenants.biz | dev1t | salesforceid_del_event_1 | CZ00001111 | true           | +420123456789 | http://www.snapshot.travel |

    Given Subscription with name "Test" for topic "Notifications" is created
    Given Customer with code "dev1t" is stored in session under key "EVENTING_CUSTOMER"
    
    When Customer with code "dev1t" is deleted
    Then Message is received with subscription "Test" from topic "Notifications" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "delete"
    And Notification in session id stands for customer in session on key "EVENTING_CUSTOMER"
    And Subscription with name "Test" for topic "Notifications" is unsubscribed
    
    