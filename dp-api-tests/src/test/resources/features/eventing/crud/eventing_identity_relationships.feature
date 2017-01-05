Feature: Eventing identity module relationships

  Background:
    Given Database is cleaned

  Scenario: Adding property to customer
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist

    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |

    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "true"
      |userId                                | userType | userName            | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | role_add_event_user | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId    | propertyName                      | propertyCode                 | website                    | email           | isDemoProperty | timezone      |
      | salesforceid_n1 | Eventing property add to customer | cust_prop_add_property_event | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague |

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Relation between property with code "cust_prop_add_property_event" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "anchor" from "2015-01-01" to "2015-12-31"

    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Create"
    And Notification in session id stands for property with code "cust_prop_add_property_event"
    And Notification in session parent entity type is "Customer"
    And Notification in session parent id stands for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Updating property for customer
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist

    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "true"
      |userId                                | userType | userName            | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | role_add_event_user | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId    | propertyName                      | propertyCode                    | website                    | email           | isDemoProperty | timezone      |
      | salesforceid_n1 | Eventing property add to customer | cust_prop_update_property_event | http://www.snapshot.travel | pn1@tenants.biz | true           | Europe/Prague |

    Given Relation between property with code "cust_prop_update_property_event" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with type "anchor" from "2015-01-01" to "2015-12-31"

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property with code "cust_prop_update_property_event" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" with type "anchor" is updating field "valid_from" to value "2014-01-01"

    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Update"
    And Notification in session id stands for property with code "cust_prop_update_property_event"
    And Notification in session parent entity type is "Customer"
    And Notification in session parent id stands for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Adding role to user
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |

    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "true"
      | userType | userName            | firstName | lastName | email                | timezone      | culture |
      | snapshot | role_add_event_user | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following roles exist
      | applicationId | roleName            | roleDescription        |
      | 333           | role_add_event_role | optional description 1 |

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Relation between role with name "role_add_event_role" for application id "333" and user with username "role_add_event_user" exists with relationship_type "customer" and entity with id "1238fd9a-a05d-42d8-8e84-42e904ace123"

    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "UserRole"
    And Notification in session operation is "Create"
    And Notification in session id stands for role with name "role_add_event_role" for application id  "333"
    And Notification in session parent entity type is "User"
    And Notification in session parent id stands for user with username "role_add_event_user"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Removing role from user
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |

    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "true"
      | userType | userName            | firstName | lastName | email                | timezone      | culture |
      | snapshot | role_del_event_user | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following roles exist
      | applicationId | roleName            | roleDescription        |
      | 333           | role_del_event_role | optional description 1 |

    Given Relation between role with name "role_del_event_role" for application id "333" and user with username "role_del_event_user" exists with relationship_type "customer" and entity with id "1238fd9a-a05d-42d8-8e84-42e904ace123"

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created

    When Role with name "role_del_event_role" for application id "333" is removed from user with username "role_del_event_user" with relationship_type "customer" and entity with id "1238fd9a-a05d-42d8-8e84-42e904ace123"

    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "UserRole"
    And Notification in session operation is "Delete"
    And Notification in session id stands for role with name "role_del_event_role" for application id  "333"
    And Notification in session parent entity type is "User"
    And Notification in session parent id stands for user with username "role_del_event_user"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Adding property to property set
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1  | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "snaphotUser1"
      | propertySetName                | propertySetDescription | propertySetType |
      | add_propset_user_event_propset | ps1_description        | brand           |

    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode                | website                    | email          | isDemoProperty | timezone      |
      | salesforceid_1 | p1_name      | add_propset_prop_event_prop | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property with code "add_propset_prop_event_prop" is added to property set with name "add_propset_prop_event_propset" for customer with id "add_propset_prop_event_customer"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Create"
    And Notification in session id stands for property with code "add_propset_prop_event_prop"
    And Notification in session parent entity type is "PropertySet"
    And Notification in session parent id stands for property set with name "add_propset_prop_event_propset" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Removing property from property set
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1  | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "snaphotUser1"
      | propertySetName                | propertySetDescription | propertySetType |
      | del_propset_user_event_propset | ps1_description        | brand           |

    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode                | website                    | email          | isDemoProperty | timezone      |
      | salesforceid_1 | p1_name      | del_propset_prop_event_prop | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |

    Given Relation between property with code "del_propset_prop_event_prop" and property set with name "del_propset_prop_event_propset" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property with code "del_propset_prop_event_prop" is removed from property set with name "del_propset_prop_event_propset" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Delete"
    And Notification in session id stands for property with code "del_propset_prop_event_prop"
    And Notification in session parent entity type is "PropertySet"
    And Notification in session parent id stands for property set with name "del_propset_prop_event_propset" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Adding user to property set
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1  | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "snaphotUser1"
      | propertySetName                | propertySetDescription | propertySetType |
      | add_propset_user_event_propset | ps1_description        | brand           |

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When User with username "add_propset_user_event_user" is added to property set with name "add_propset_user_event_propset" for customer with id "add_propset_user_event_customer"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Create"
    And Notification in session id stands for user with username "add_propset_user_event_user"
    And Notification in session parent entity type is "PropertySet"
    And Notification in session parent id stands for property set with name "add_propset_user_event_propset" for customer with id "add_propset_user_event_customer"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Removing user from property set
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |

    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1  | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following property sets exist for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and user "snaphotUser1"
      | propertySetName                | propertySetDescription | propertySetType |
      | del_propset_user_event_propset | ps1_description        | brand           |


    Given Relation between user with username "del_propset_user_event_user" and property set with name "del_propset_user_event_propset" for customer with id "del_propset_user_event_customer" exists

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created

    When User with username "del_propset_user_event_user" is removed from property set with name "del_propset_user_event_propset" for customer with id "del_propset_user_event_customer"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Delete"
    And Notification in session id stands for user with username "del_propset_user_event_user"
    And Notification in session parent entity type is "PropertySet"
    And Notification in session parent id stands for property set with name "del_propset_user_event_propset" for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Adding user to customer
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |

    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1  | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When User with username "add_cust_user_event_user" is added to customer with id "add_cust_user_event_customer" with isPrimary "true"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Create"
    And Notification in session id stands for user with username "add_cust_user_event_user"
    And Notification in session parent entity type is "Customer"
    And Notification in session parent id stands for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Removing user from customer
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |

    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1  | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Relation between user with username "del_cust_user_event_user" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" exists with isPrimary "true"

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When User with username "del_cust_user_event_user" is removed from customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Delete"
    And Notification in session id stands for user with username "del_cust_user_event_user"
    And Notification in session parent entity type is "Customer"
    And Notification in session parent id stands for customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Adding user to property
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |

    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1  | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode             | website                    | email          | isDemoProperty | timezone      |
      | salesforceid_1 | p1_name      | add_prop_user_event_prop | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |
    

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When User with username "add_prop_user_event_user" is added to property with code "add_prop_user_event_prop"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Create"
    And Notification in session id stands for user with username "add_prop_user_event_user"
    And Notification in session parent entity type is "Property"
    And Notification in session parent id stands for property with code "add_prop_user_event_prop"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Removing user from property
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | customerId                           | companyName              | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Event role add company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |Europe/Prague |

    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName      | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1  | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | propertyName | propertyCode             | website                    | email          | isDemoProperty | timezone      |
      | salesforceid_1 | p1_name      | del_prop_user_event_prop | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |

    Given Relation between user with username "del_prop_user_event_user" and property with code "del_prop_user_event_prop" exists
    
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When User with username "del_prop_user_event_user" is removed from property with code "del_prop_user_event_prop"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Delete"
    And Notification in session id stands for user with username "del_prop_user_event_user"
    And Notification in session parent entity type is "Property"
    And Notification in session parent id stands for property with code "del_prop_user_event_prop"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

