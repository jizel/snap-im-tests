Feature: Eventing_identity_module

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
    And Notification in session operation is "Create"
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
    And Notification in session operation is "Delete"
    And Notification in session id stands for customer in session on key "EVENTING_CUSTOMER"
    And Subscription with name "Test" for topic "Notifications" is unsubscribed

  Scenario: Eventing customer updated
    Given Subscription with name "Test" for topic "Notifications" does not exist

    Given The following customers exist with random address
      | companyName              | email            | code  | salesforceId             | vatId      | isDemoCustomer | phone         | website                    |
      | Update Eventing  company | uev1@tenants.biz | uev1t | salesforceid_del_event_1 | CZ00011111 | true           | +420123456789 | http://www.snapshot.travel |

    Given Subscription with name "Test" for topic "Notifications" is created
    Given Customer with code "uev1t" is stored in session under key "EVENTING_CUSTOMER"

    When Customer with code "uev1t" is updated with data
      | companyName                   | email | code  | salesforceId | vatId | phone | website | notes |
      | Updated Eventing company name | /null | /null | /null        | /null | /null | /null   | /null |
    Then Message is received with subscription "Test" from topic "Notifications" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "Update"
    And Notification in session id stands for customer in session on key "EVENTING_CUSTOMER"
    And Subscription with name "Test" for topic "Notifications" is unsubscribed

  Scenario: Eventing property created

    Given Property with code "event_prop_1_create" is deleted
    Given Subscription with name "Test" for topic "Notifications" does not exist
    Given Subscription with name "Test" for topic "Notifications" is created
    When Property is created with random address and billing address
      | salesforceId    | propertyName             | propertyCode        | website                    | email           | isDemoProperty | timezone      |
      | salesforceid_n1 | Eventing property create | event_prop_1_create | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague |
    Then Message is received with subscription "Test" from topic "Notifications" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Create"
    And Notification in session id stands for property with code "event_prop_1_create"
    And Subscription with name "Test" for topic "Notifications" is unsubscribed


  Scenario: Eventing user created

    Given User with userName "event_user_create_1" is deleted
    Given Subscription with name "Test" for topic "Notifications" does not exist
    Given Subscription with name "Test" for topic "Notifications" is created
    When User is created
      | userType | userName            | firstName | lastName | email               | timezone      | culture |
      | customer | event_user_create_1 | Snap      | Shot     | snp@snapshot.travel | Europe/Prague | cs-CZ   |
    Then Message is received with subscription "Test" from topic "Notifications" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Create"
    And Notification in session id stands for user with username "event_user_create_1"
    And Subscription with name "Test" for topic "Notifications" is unsubscribed

  Scenario: Eventing role created

    Given Role with name "event_role_create_1" for application id "10" is deleted
    Given Subscription with name "Test" for topic "Notifications" does not exist
    Given Subscription with name "Test" for topic "Notifications" is created
    When Role is created
      | applicationId | roleName            | roleDescription        |
      | 10            | event_role_create_1 | optional description 1 |
    Then Message is received with subscription "Test" from topic "Notifications" and stored in session
    And Notification in session entity_type is "Role"
    And Notification in session operation is "Create"
    And Notification in session id stands for role with name "event_role_create_1" for application id  "10"
    And Subscription with name "Test" for topic "Notifications" is unsubscribed

  Scenario: Eventing property set created

    Given The following customers exist with random address
      | companyName                   | email             | code   | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Eventing property_set company | epsc1@tenants.biz | epsc1t | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |
    Given All property sets are deleted for customers with codes: epsc1t
    Given Subscription with name "Test" for topic "Notifications" does not exist
    Given Subscription with name "Test" for topic "Notifications" is created
    When Property set is created for customer with code "epsc1t"
      | propertySetName   | propertySetDescription | propertySetType |
      | ps1_event_created | ps1_description        | branch          |
    Then Message is received with subscription "Test" from topic "Notifications" and stored in session
    And Notification in session entity_type is "PropertySet"
    And Notification in session operation is "Create"
    And Notification in session id stands for property set with name "ps1_event_created" for customer with code "epsc1t"
    And Subscription with name "Test" for topic "Notifications" is unsubscribed

