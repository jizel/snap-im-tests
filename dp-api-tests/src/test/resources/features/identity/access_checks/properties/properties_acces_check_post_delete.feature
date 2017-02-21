@Identity
Feature: Properties access check feature - POST and DELETE
  - User can update/delete specific property and it's relationships only if he owns it
  - Check positive scenarios
  - In negative tests, ETAG needs to be obtained by different user first
  - Various cases of when user should have access to a property (instance) are covered in GET feature (user is a member of userGroup, property set etc.)
  - 404 is returned for unauthorized users

  Background:
    Given Database is cleaned
    Given Default Snapshot user is created
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
      | 0d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithProp   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | 1d829079-48f0-4f00-9bec-e2329a8bdaac | customer | userWithNoProp | Customer2 | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property is created with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     | ttiId  |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 654123 |


  Scenario: User with access updates property
    When Property with code "p1_code" is updated with data by user "userWithProp"
      | salesforceId   | name         | website                  | email            | isDemoProperty |
      | updated_sf_id  | updated_name | https://www.upddated.com | updated@email.cz | false          |
    Then Response code is "204"
    When Property with code "p1_code" is requested by user "userWithProp"
    Then Response code is "200"
    And Body contains entity with attribute "name" value "updated_name"
    And Body contains entity with attribute "salesforce_id" value "updated_sf_id"
    And Body contains entity with attribute "website" value "https://www.upddated.com"
    And Body contains entity with attribute "email" value "updated@email.cz"
    And Body contains entity with attribute "is_demo_property" value "false"

  Scenario: User without access tries to update property
    When Property with code "p1_code" is updated with data by user "userWithNoProp"
      | salesforceId   | name         | website                  | email            | isDemoProperty |
      | updated_sf_id  | updated_name | https://www.upddated.com | updated@email.cz | false          |
    Then Response code is "404"
    When Property with code "p1_code" is requested by user "userWithProp"
    Then Response code is "200"

  Scenario: Deleting Property by user who owns it
    When Property with code "p1_code" is deleted by user "userWithProp"
    Then Response code is "204"
    And Body is empty
    And Property with same id doesn't exist

  Scenario: Deleting Property by user without access to it
    When Property with code "p1_code" is deleted by user "userWithNoProp"
    Then Response code is "404"
    When Property with code "p1_code" is requested by user "userWithProp"
    Then Response code is "200"

  Scenario: Anchor_customer_id of not accessible customer cannot be used when creating property
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    When The following property is created with random address and billing address for user "0d829079-48f0-4f00-9bec-e2329a8bdaac"
      | salesforceId   | name         | propertyCode | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_2 | p2_name      | p2_code      | p2@tenants.biz | true           | Europe/Prague | 2348fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "404"
    And Custom code is 40402

  Scenario: Anchor_customer_id of not accessible customer cannot be used when updating property
    Given The following customers exist with random address
      | customerId                           | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    When Property with code "p1_code" is updated with data by user "userWithProp"
      | anchorCustomerId                     |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "404"
    And Custom code is 40402


  #    ----------------------------< Tti >----------------------------------

  Scenario: Add tti to booking.com mapping to property with defined tti_id by use who has access to the property
    When Add ttiId to booking.com id "1234" mapping to property with code "p1_code" by user "userWithProp"
    Then Response code is "201"
    And Body contains entity with attribute "code" and integer value 1234

  Scenario: Add tti to booking.com mapping to property with defined tti_id by use who does not have access to the property
    When Add ttiId to booking.com id "1234" mapping to property with code "p1_code" by user "userWithNoProp"
    Then Response code is "404"