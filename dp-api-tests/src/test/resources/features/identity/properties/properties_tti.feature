@Identity
Feature: Properties tti code - DP-757

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given Default Snapshot user is created
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      |propertyId                           | ttiId  | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId         |
      |999e833e-50e8-4854-a233-289f00b54a09 | 654123 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |

    Scenario: Add tti to booking.com mapping to property with defined tti_id (tti_id doesn't exist in OTA)
    When Add ttiId to booking.com id "1234" mapping to property with code "p1_code"
    Then Response code is "201"
    Then Body contains entity with attribute "code" and integer value 1234

    Scenario: Duplicate booking.com id send to property with defined tti_id (tti_id doesn't exist in OTA)
      When Add ttiId to booking.com id "1234" mapping to property with code "p1_code"
      Then Response code is "201"
      When Add ttiId to booking.com id "1234" mapping to property with code "p1_code"
      Then Response code is "409"
      And Custom code is 40901
      And Body contains entity with attribute "message" value "The field code must be unique."

    Scenario: Add tti to booking.com mapping to property with defined tti_id (tti_id exists in OTA)
      Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        | propertyId                           | ttiId | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId         |
        | 888e833e-50e8-4854-a233-289f00b54a09 | 998   | salesforceid_1 | p2_name      | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      When Add ttiId to booking.com id "1234" mapping to property with code "p2_code"
      Then Response code is "201"
      Then Body contains entity with attribute "code" and integer value 1234

    Scenario: Duplicate booking.com id send to properties with defined tti_id (tti_id exists in OTA)
      Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        | propertyId                           | ttiId | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId         |
        | 888e833e-50e8-4854-a233-289f00b54a09 | 998   | salesforceid_1 | p2_name      | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      When Add ttiId to booking.com id "123" mapping to property with code "p2_code"
      Then Response code is "201"
      When Add ttiId to booking.com id "123" mapping to property with code "p1_code"
      Then Response code is "409"
      And Custom code is 40901
      And Body contains entity with attribute "message" value "The field code must be unique."

    Scenario: Duplicate booking.com id send to property without defined tti_id (the original property has ttiId)
      Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        | propertyId                           | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
        | 888e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p2_name      | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      When Add ttiId to booking.com id "123" mapping to property with code "p1_code"
      Then Response code is "201"
      When Add ttiId to booking.com id "123" mapping to property with code "p2_code"
      Then Response code is "409"
      And Custom code is 40901
      And Body contains entity with attribute "message" value "The field code must be unique."

    Scenario: Booking.com id shall not be null
      When Add ttiId to booking.com id mapping to property with code "p1_code" without booking.com code
      Then Response code is "422"
      And Custom code is 42201

    Scenario: Map property with nonexistent ttiCode - new ttiCode is created and added to property
      Given The following property is created with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        |propertyId                           | salesforceId   | propertyName      | propertyCode| website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
        |888e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | no_tti_prop_name  | no_tti_prop | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Body does not contain property with attribute "tti_id"
      When Add ttiId to booking.com id "123" mapping to property with code "no_tti_prop"
      When Property with code "no_tti_prop" is requested
      Then Body contains entity with attribute "tti_id"

    Scenario: Notification is pushed to Notifications.tti when non-existing tti is used for create/update of property
      Given Subscription with name "Test" for topic "Notifications.tti" does not exist
      Given Subscription with name "Test" for topic "Notifications.tti" is created
      Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        | propertyId                           | ttiId | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId         |
        | 888e833e-50e8-4854-a233-289f00b54a09 | 998   | salesforceid_1 | p2_name      | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Message is received with subscription "Test" from topic "Notifications.tti" and stored in session
      And Stored notification has key "propertyId" and value "888e833e-50e8-4854-a233-289f00b54a09"
      When Property with code "p2_code" is updated with data
        | ttiId   |
        | 123456  |
      Then Message is received with subscription "Test" from topic "Notifications.tti" and stored in session
      And Stored notification has key "propertyId" and value "888e833e-50e8-4854-a233-289f00b54a09"
      And Response code is "204"
      And Subscription with name "Test" for topic "Notifications.tti" is unsubscribed

    Scenario: Duplicate booking.com id send to properties without defined tti_id
      Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
        | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
        | salesforceid_3 | p3_name      | p3_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      When Add ttiId to booking.com id "1234" mapping to property with code "p2_code"
      Then Response code is "201"
      When Add ttiId to booking.com id "1234" mapping to property with code "p3_code"
      Then Response code is "409"
      And Body contains entity with attribute "message" value "The field code must be unique."