Feature: Eventing identity module

  Background:
    Given Database is cleaned and default entities are created



  Scenario: Eventing customer created
  Customer clean if existed, then subscription is clean and recreated,
  then is customer is created and then notification is checked.

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Customer is created with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Then Response code is "201"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "Create"
    And Notification in session id stands for customer with id "a792d2b2-3836-4207-a705-42bbecf3d881"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing customer deleted
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist

    When Customer is created with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given Customer with id "da792d2b2-3836-4207-a705-42bbecf3d881" is stored in session under key "EVENTING_CUSTOMER"

    When Customer with id "da792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "Delete"
    And Notification in session id stands for customer in session on key "EVENTING_CUSTOMER"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing customer updated
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist

    When Customer is created with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given Customer with id "ua792d2b2-3836-4207-a705-42bbecf3d881" is stored in session under key "EVENTING_CUSTOMER"

    When Customer with id "ua792d2b2-3836-4207-a705-42bbecf3d881" is updated with data
      | companyName                   | email | code  | salesforceId | vatId | phone | website | notes | timezone      |
      | Updated Eventing company name | /null | /null | /null        | /null | /null | /null   | /null | Europe/Prague |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "Update"
    And Notification in session id stands for customer in session on key "EVENTING_CUSTOMER"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing customer activated
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    When Customer is created with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    And Subscription with name "Test" for topic "Notifications.crud" is created
    And Customer with id "aa792d2b2-3836-4207-a705-42bbecf3d881" is stored in session under key "EVENTING_CUSTOMER"
    When Customer with id "aa792d2b2-3836-4207-a705-42bbecf3d881" is activated
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "Activate"
    And Notification in session id stands for customer in session on key "EVENTING_CUSTOMER"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing customer deactivated
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    When Customer is created with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    And Subscription with name "Test" for topic "Notifications.crud" is created
    And Customer with id "da792d2b2-3836-4207-a705-42bbecf3d881" is stored in session under key "EVENTING_CUSTOMER"
    And Customer with id "da792d2b2-3836-4207-a705-42bbecf3d881" is activated
    And Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    When Customer with id "da792d2b2-3836-4207-a705-42bbecf3d881" is inactivated
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Customer"
    And Notification in session operation is "Deactivate"
    And Notification in session id stands for customer in session on key "EVENTING_CUSTOMER"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing property activated
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    When Customer is created with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |


    And The following properties exist with random address and billing address
      | salesforceId   | name         | propertyCode   | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | act_prop_event | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

    And Property with code "act_prop_event" is stored in session under key "EVENTING_PROPERTY"
    And Subscription with name "Test" for topic "Notifications.crud" is created
    When Property with code "act_prop_event" is activated
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Activate"
    And Notification in session id stands for property in session on key "EVENTING_PROPERTY"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing property deactivated
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following customers exist with random address
      | Id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    And The following properties exist with random address and billing address
      | salesforceId   | name         | propertyCode    | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | dact_prop_event | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

    And Property with code "dact_prop_event" is stored in session under key "EVENTING_PROPERTY"
    And Subscription with name "Test" for topic "Notifications.crud" is created
    And Property with code "dact_prop_event" is activated
    And Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    When Property with code "dact_prop_event" is inactivated
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Deactivate"
    And Notification in session id stands for property in session on key "EVENTING_PROPERTY"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing property created

    Given Property with code "event_prop_1_create" is deleted
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Customer is created with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Customer"

    And The following properties exist with random address and billing address
      | salesforceId   | name         | propertyCode        | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | event_prop_1_create | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Create"
    And Notification in session id stands for property with code "event_prop_1_create"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing property deleted

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    When Customer is created with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |

    And The following properties exist with random address and billing address
      | salesforceId   | name         | propertyCode   | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_1 | p1_name      | del_prop_event | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

    Given Property with code "del_prop_event" is stored in session under key "EVENTING_PROPERTY"
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property with code "del_prop_event" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Property"
    And Notification in session operation is "Delete"
    And Notification in session id stands for property in session on key "EVENTING_PROPERTY"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing user created

    Given User with userName "event_user_create_1" is deleted
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When User is created
      | userType | userName            | firstName | lastName | email               | timezone      | culture |
      | customer | event_user_create_1 | Snap      | Shot     | snp@snapshot.travel | Europe/Prague | cs-CZ   |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Create"
    And Notification in session id stands for user with username "event_user_create_1"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing user deleted

    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given The following users exist
      | userType | userName       | firstName | lastName | email                | timezone      | culture |
      | customer | del_user_event | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given User with username "del_user_event" is stored in session under key "EVENTING_USER"
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When User with userName "del_user_event" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "User"
    And Notification in session operation is "Delete"
    And Notification in session id stands for user in session on key "EVENTING_USER"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing role created
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given Switch for user customer role tests
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Application"
    When Role is created
      | Id                                   | roleName            | description            |
      | 11111111-0000-4000-a000-111111111111 | event_role_create_1 | optional description 1 |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Role"
    And Notification in session operation is "Create"
    And Notification in session id stands for role with name "event_role_create_1" for application id  "11111111-0000-4000-a000-111111111111"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing role deleted
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given Switch for user customer role tests
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Application"
    When Role is created
      | Id                                   | roleName          | description            |
      | 11111111-0000-4000-a000-111111111111 | event_role_delete | optional description 1 |
    Given Role with name "event_role_delete" for application id "11111111-0000-4000-a000-111111111111" is stored in session under key "EVENTING_ROLE"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Role"
    And Notification in session operation is "Create"
    When Role with name "event_role_delete" for application id "11111111-0000-4000-a000-111111111111" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Role"
    And Notification in session operation is "Delete"
    And Notification in session id stands for role in session on key "EVENTING_ROLE"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing role updated
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given Switch for user customer role tests
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Application"
    When Role is created
      | Id                                   | roleName          | description            |
      | 11111111-0000-4000-a000-111111111111 | event_role_update | optional description 1 |
    Given Role with name "event_role_update" for application id "11111111-0000-4000-a000-111111111111" is stored in session under key "EVENTING_ROLE"
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Role"
    And Notification in session operation is "Create"
    When Role with name "event_role_update" for application id "11111111-0000-4000-a000-111111111111" is updated with data
      | Id            | roleName          | description |
      |               | Updated Role Name | updated 1   |

    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Role"
    And Notification in session operation is "Update"
    And Notification in session id stands for role in session on key "EVENTING_ROLE"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing property set created

    When Customer is created with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "a792d2b2-3836-4207-a705-42bbecf3d881" as primary "false"
      | Id                                   | userType | userName     | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1 | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given All property sets are deleted for customers with ids: a792d2b2-3836-4207-a705-42bbecf3d881
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When The following property set is created for customer with id "a792d2b2-3836-4207-a705-42bbecf3d881<string>"
      | name              | description            | type            |
      | ps1_event_created | ps1_description        | brand           |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "PropertySet"
    And Notification in session operation is "Create"
    And Notification in session id stands for property set with name "ps1_event_created" for customer with id "a792d2b2-3836-4207-a705-42bbecf3d881"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing property set deleted

    When Customer is created with random address
      | Id                                   | companyName       | email           | salesforceId         | vatId       | isDemoCustomer | phone         | website                    | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Eventing  company | ev1@tenants.biz | salesforceid_event_1 | CZ123123123 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "a792d2b2-3836-4207-a705-42bbecf3d881" as primary "false"
      | Id                                   | userType | userName     | firstName | lastName | email                        | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snaphotUser1 | Snaphot   | User1    | snaphotUser1@snapshot.travel | Europe/Prague | cs-CZ   |

    When The following property set is created for customer with id "a792d2b2-3836-4207-a705-42bbecf3d881<string>"
      | name                      | description            | type            |
      | event_propset_del_propset | description            | brand           |

    Given Property set with name "event_propset_del_propset" for Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is stored in session under key "EVENTING_PROPERTY_SET"
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When Property set "event_propset_del_propset" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "PropertySet"
    And Notification in session operation is "Delete"
    And Notification in session id stands for property set in session on key "EVENTING_PROPERTY_SET"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing partner created
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    When The following partner exist
      | name                 | email          | website                    | vatId      | notes        |
      | Partner company name | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Partner"
    And Notification in session operation is "Create"
    And Notification in session id stands for partner with name "Partner company name"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing partner updated
    Given Partner with name "Partner company name" is deleted
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given The following partner exist
      | name                 | email          | website                    | vatId      | notes        | Id                                   |
      | Partner company name | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    When Partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is updated with data
      | name | email | website | vatId      | notes |
      |      |       |         | CZ12345678 |       |
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Partner"
    # At this moment returns "Create" - needs to be investigated
    And Notification in session operation is "Update"
    And Notification in session id stands for partner with name "Partner company name"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing partner deleted
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given The following partner exist
      | name                 | email          | website                    | vatId      | notes        |
      | Partner company name | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 |
    When Partner with name "Company name 1" is deleted
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Partner"
    # At this moment returns "Create" - needs to be investigated
    And Notification in session operation is "Delete"
    And Notification in session id stands for partner with name "Partner company name"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing partner activated
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given The following partner exist
      | name           | email          | website                    | vatId      | notes        | Id                                   |
      | Company name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    When Partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is activated
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Partner"
    # At this moment returns "Create" - needs to be investigated
    And Notification in session operation is "Activate"
    And Notification in session id stands for partner with name "Partner company name"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed

  Scenario: Eventing partner deactivated
    Given Subscription with name "Test" for topic "Notifications.crud" does not exist
    Given Subscription with name "Test" for topic "Notifications.crud" is created
    Given The following partner exist
      | name           | email          | website                    | vatId      | notes        | Id                                   |
      | Company name 1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 | abc8fd9a-a05d-42d8-8e84-42e904ace123 |
    When Partner with id "abc8fd9a-a05d-42d8-8e84-42e904ace123" is inactivated
    Then Message is received with subscription "Test" from topic "Notifications.crud" and stored in session
    And Notification in session entity_type is "Partner"
    # At this moment returns "Create" - needs to be investigated
    And Notification in session operation is "Deactivate"
    And Notification in session id stands for partner with name "Partner company name"
    And Subscription with name "Test" for topic "Notifications.crud" is unsubscribed
