@Identity
Feature: Properties access check feature - POST and DELETE
  - User can update/delete specific property and it's relationships only if he owns it
  - Check positive scenarios
  - In negative tests, ETAG needs to be obtained by different user first
  - Various cases of when user should have access to a property (instance) are covered in GET feature (user is a member of userGroup, property set etc.)
  - 404 is returned for unauthorized users

  Background:
    Given Database is cleaned and default entities are created

    Given The following customers exist with random address
      | Id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userType | userName       | firstName | lastName | email                | timezone      | culture | isActive |
      | customer | userWithProp   | Customer1 | User1    | cus1@snapshot.travel | Europe/Prague | cs-CZ   | true     |
      | customer | userWithNoProp | Customer2 | User2    | cus2@snapshot.travel | Europe/Prague | cs-CZ   | true     |
    Given The following property is created with random address and billing address for user "userWithProp"
      | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     | ttiId  |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 654123 |
    Given API subscriptions exist for default application and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123" and property "p1_code"


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

  Scenario: User with inactive relation to property can not update or delete it
    When Relation between user "userWithNoProp" and property with code "p1_code" exists with is_active "false"
    When Property with code "p1_code" is updated with data by user "userWithNoProp"
      | salesforceId   | name         | website                  | email            | isDemoProperty |
      | updated_sf_id  | updated_name | https://www.upddated.com | updated@email.cz | false          |
    Then Response code is "404"
    When Property with code "p1_code" is deleted by user "userWithNoProp"
    Then Response code is "404"
    When Relation between user "userWithNoProp" and property "p1_code" is activated
    When Property with code "p1_code" is updated with data by user "userWithNoProp"
      | salesforceId   | name         | website                  | email            | isDemoProperty |
      | updated_sf_id  | updated_name | https://www.upddated.com | updated@email.cz | false          |
    Then Response code is "204"
    When Property with code "p1_code" is deleted by user "userWithNoProp"
    Then Response code is "204"

  Scenario: Anchor_customer_id of not accessible customer cannot be used when creating property
    Given The following customers exist with random address
      | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123"
    When The following property is created with random address and billing address for user "userWithNoProp"
      | salesforceId   | name         | propertyCode | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_2 | p2_name      | p2_code      | p2@tenants.biz | true           | Europe/Prague | 2348fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "404"
    And Custom code is 40402
    Given Relation between user "userWithNoProp" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with is_active "false"
    When The following property is created with random address and billing address for user "userWithNoProp"
      | salesforceId   | name         | propertyCode | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_2 | p2_name      | p2_code      | p2@tenants.biz | true           | Europe/Prague | 2348fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "404"
    And Custom code is 40402
    When Relation between user "userWithNoProp" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is activated
    When The following property is created with random address and billing address for user "userWithNoProp"
      | salesforceId   | name         | propertyCode | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | salesforceid_2 | p2_name      | p2_code      | p2@tenants.biz | true           | Europe/Prague | 2348fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "201"


  Scenario: Anchor_customer_id of not accessible customer cannot be used when updating property
    Given The following customers exist with random address
      | Id                                   | companyName | email          | salesforceId   | vatId      | isDemoCustomer | timezone      |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 | Company 2   | c2@tenants.biz | salesforceid_2 | CZ20000001 | true           | Europe/Prague |
    Given API subscriptions exist for default application and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123"
    When Property with code "p1_code" is updated with data by user "userWithProp"
      | anchorCustomerId                     |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "422"
    And Custom code is 42202
    Given Relation between user "userWithProp" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" exists with is_active "false"
    When Property with code "p1_code" is updated with data by user "userWithProp"
      | anchorCustomerId                     |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "422"
    And Custom code is 42202
    When Relation between user "userWithProp" and customer with id "2348fd9a-a05d-42d8-8e84-42e904ace123" is activated
    When Property with code "p1_code" is updated with data by user "userWithProp"
      | anchorCustomerId                     |
      | 2348fd9a-a05d-42d8-8e84-42e904ace123 |
    Then Response code is "204"

  #    ----------------------------< Tti >----------------------------------

  # DP-1816
  Scenario: Add tti to booking.com mapping to property with defined tti_id by user who has access to the property
    When Add ttiId to booking.com id "1234" mapping to property with code "p1_code" by user "userWithProp"
    Then Response code is "201"
    And Body contains entity with attribute "code" and integer value 1234

  # DP-1816
  Scenario: Add tti to booking.com mapping to property with defined tti_id by user who does not have access to the property
    When Add ttiId to booking.com id "1234" mapping to property with code "p1_code" by user "userWithNoProp"
    Then Response code is "404"
    When Relation between user "userWithNoProp" and property with code "p1_code" exists with is_active "false"
    And Add ttiId to booking.com id "1235" mapping to property with code "p1_code" by user "userWithNoProp"
    Then Response code is "404"
    When Relation between user "userWithNoProp" and property "p1_code" is activated
    And Add ttiId to booking.com id "1235" mapping to property with code "p1_code" by user "userWithNoProp"
    Then Response code is "201"