@Identity
Feature: Commercial subscription create update delete

  Background:
    Given Database is cleaned and default entities are created
    And Relation between property "08000000-0000-4444-8888-000000000001" and customer with id "06000000-0000-4444-8888-000000000001" exists
    Given The following property is created with random address and billing address
      | id                                   | salesforceId   | name    | code         | website                    | email          | isDemo         | timezone      | customerId                           |
      | 999e833e-50e8-4854-a233-289f00b54a09 | salesforceid_1 | p1_name | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 06000000-0000-4444-8888-000000000001 |

  @Smoke
  Scenario: Create update get delete commercial subscription
    # --------------------- CREATE ---------------------
    Given Commercial subscription is created
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728eb | 06000000-0000-4444-8888-000000000001 | 999e833e-50e8-4854-a233-289f00b54a09 | 03000000-0000-4444-8888-000000000000 |
    Then Response code is "201"
    And Body contains entity with attribute "commercial_subscription_id" value "8e238f8e-2c9c-4e32-9a63-40474a9728eb"
    And Body contains entity with attribute "customer_id" value "06000000-0000-4444-8888-000000000001"
    And Body contains entity with attribute "property_id" value "999e833e-50e8-4854-a233-289f00b54a09"
    And Body contains entity with attribute "application_id" value "03000000-0000-4444-8888-000000000000"
    And Body contains entity with attribute "is_active" value "true"
    # ----------- Create duplicate --------------------
    When Commercial subscription is created
      | id                                   | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728ec | 06000000-0000-4444-8888-000000000001 | 999e833e-50e8-4854-a233-289f00b54a09 | 03000000-0000-4444-8888-000000000000 |
    Then Response code is "409"
    # --------------------- UPDATE ---------------------
    When Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is deactivated
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is not activate
    # --------------------- GET ------------------------
    When Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is requested
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "commercial_subscription_id" value "8e238f8e-2c9c-4e32-9a63-40474a9728eb"
    And Body contains entity with attribute "application_id" value "03000000-0000-4444-8888-000000000000"
    And Body contains entity with attribute "customer_id" value "06000000-0000-4444-8888-000000000001"
    And Body contains entity with attribute "property_id" value "999e833e-50e8-4854-a233-289f00b54a09"
    # --------------------- DELETE ---------------------
    When Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is deleted
    Then Response code is "204"
    And Body is empty
    And Commercial subscription with same id does not exist
