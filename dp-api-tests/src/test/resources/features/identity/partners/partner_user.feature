Feature: Partners users relationship feature

  Background:
    Given Database is cleaned and default entities are created



    Scenario: Create Partner-User relationship
      Given The following partner exist
        | name     | email          | website                    | vatId      | notes        | partnerId                            |
        | Partner1 | p1@tenants.biz | http://www.snapshot.travel | CZ10000001 | Test notes 1 | abc00011-a05d-42d8-8e84-42e904ace123 |
      Given The following customers exist with random address
        | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
        | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
        | userId                               | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
        | 0d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snapUser   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      When user "snapUser" is added to partner "Partner1"
      Then Response code is "201"
      And Body contains entity with attribute "user_id" value "0d829079-48f0-4f00-9bec-e2329a8bdaac"
