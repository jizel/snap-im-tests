@Identity
Feature: Commercial subscription create update delete

  Background:
    Given Database is cleaned and default entities are created
    And Relation between property "11111111-0000-4000-a000-666666666666" and customer with id "11111111-0000-4000-a000-555555555555" exists

  @Smoke
  Scenario: Create update get delete commercial subscription
    # --------------------- CREATE ---------------------
    Given Commercial subscription is created
      | Id                                   | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728eb | 11111111-0000-4000-a000-555555555555 | 11111111-0000-4000-a000-666666666666 | 11111111-0000-4000-a000-111111111111 |
    Then Response code is "201"
    And Body contains entity with attribute "commercial_subscription_id" value "8e238f8e-2c9c-4e32-9a63-40474a9728eb"
    And Body contains entity with attribute "customer_id" value "11111111-0000-4000-a000-555555555555"
    And Body contains entity with attribute "property_id" value "11111111-0000-4000-a000-666666666666"
    And Body contains entity with attribute "application_id" value "11111111-0000-4000-a000-111111111111"
    And Body contains entity with attribute "is_active" value "true"
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
    And Body contains entity with attribute "application_id" value "11111111-0000-4000-a000-111111111111"
    And Body contains entity with attribute "customer_id" value "11111111-0000-4000-a000-555555555555"
    And Body contains entity with attribute "property_id" value "11111111-0000-4000-a000-666666666666"
    # --------------------- DELETE ---------------------
    When Commercial subscription with id "8e238f8e-2c9c-4e32-9a63-40474a9728eb" is deleted
    Then Response code is "204"
    And Body is empty
    And Commercial subscription with same id does not exist
