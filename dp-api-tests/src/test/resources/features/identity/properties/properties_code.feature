@Identity
Feature: Property code feature
When creating a property, property code be entered manually or generated
Unique property code is generated according to following rules:
  - property code is always returned (and unique)
  - property code is generated from property name, country and city according to rules specified in DP-1222
  - property code always contains only CAPITAL english (latin) letters. Even when property's name contains chinese, arabic or any other characters.
For manually created property code the following rules hold
  - Property code is accepted as a field in create/update requests but it's not mandatory (but it's always returned!)
  - It does not have to be unique
  - Length is up to 50 characters
  - Once property code is updated by user, it should never be re-generated again

  Background:
    Given Database is cleaned
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | snapshot | snapUser | Snap  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given  Default Snapshot user is created

#    ----------< Automatically generated property code tests >-----------------

    Scenario Outline: Correct property code is returned when none sent
      When The following property is created with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyName   | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | <propertyName> | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Body contains entity with attribute "property_code"
      And The "property_code" attribute in response contains only CAPITAL latin characters or numbers

      Examples:
      | propertyName             |
      | oneWordProperty          |
      | 12Property               |
      | Property 2C              |
      | Prop 1024 bit            |
      | A five word propert name |
      | 电脑坏了 我想玩电脑       |
      | الدولي العاشر ليونيكود   |
      | 2初ぞあ Japan            |


    Scenario Outline: Correct customer code is returned according to customers address
      Given Property "<propertyName>" is created with address for user "snapUser" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
        | addressLine1   | city   | zipCode   | country   |
        | <addressLine1> | <city> | <zipCode> | <country> |
      Then Response code is "201"
      Then Body contains entity with attribute "property_code" value "<resultCode>"
#     Resulting code is Country Code + City Code + first 3 letters from Company Name (DP-1222)
      Examples:
        | propertyName | addressLine1 | city       | zipCode | country | resultCode |
        | Čéšký hotel  | line 1       | Brno       | 60200   | CZ      | CZBRQCES   |
        | 21st Prop.   | line 2       | New York   | 11414   | US      | USNYC21S   |
        | Union Prop.  | line 2,5     | Union City | 9307    | US      | USUCGUNI   |
        | Beijing Hotel| line 3       | Beijing    | 23456   | CN      | CNBJSBEI   |


     Scenario: When Property code is not unique, the smallest possible integer is concatenated to it
       Given Property "Brno Hilton" is created with address for user "snapUser" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
         | addressLine1 | city | zipCode | country |
         | line 1       | Brno | 60200   | CZ      |
       Then Response code is "201"
       Then Body contains entity with attribute "property_code" value "CZBRQBRN"
       Given Property "Brno Hilton" is created with address for user "snapUser" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
         | addressLine1 | city | zipCode | country |
         | line 1       | Brno | 60200   | CZ      |
       Then Response code is "201"
       Then Body contains entity with attribute "property_code" value "CZBRQBRN1"
       Given Property "Brno Hilton" is created with address for user "snapUser" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
         | addressLine1 | city | zipCode | country |
         | line 1       | Brno | 60200   | CZ      |
       Then Response code is "201"
       Then Body contains entity with attribute "property_code" value "CZBRQBRN2"

#     ----------< Manually created property code tests >-----------------

    Scenario Outline: Property code can be filled manually. Property code is always returned in response.
      When The following property is created with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        | propertyCode   | propertyName | email              | isDemoProperty | timezone      | anchorCustomerId                     |
        | <propertyCode> | property1    | p1@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Body contains entity with attribute "property_code" value "<propertyCode>"
      Examples:
      | propertyCode |
      | prop_code_1  |
      | 电脑坏了      |
      | čěšký kód    |
      | ليونيكود     |


    Scenario: Property code does not have to be unique
      When The following property is created with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        | propertyCode   | propertyName | email              | isDemoProperty | timezone      | anchorCustomerId                     |
        | nonunique      | property1    | p1@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Body contains entity with attribute "property_code" value "nonunique"
      When The following property is created with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        | propertyCode   | propertyName | email              | isDemoProperty | timezone      | anchorCustomerId                     |
        | nonunique      | property1    | p1@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Body contains entity with attribute "property_code" value "nonunique"


    Scenario: Maximum property code length is 50 characters
      When The following property is created with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        | propertyCode                                       | propertyName | email              | isDemoProperty | timezone      | anchorCustomerId                     |
        | Lorem ipsum dolor sit amet, consectetuer adipiscin | property50   | p1@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Response code is "201"
      And Body contains entity with attribute "property_code"
      When The following property is created with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
        | propertyCode                                        | propertyName | email              | isDemoProperty | timezone      | anchorCustomerId                     |
        | Lorem ipsum dolor sit amet, consectetuer adipisci+1 | property51   | p1@snapshot.travel | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
      Then Response code is "422"
      And Custom code is 42201


     Scenario: Property can be updated by user and then should be never re-generated again
       Given Property "Brno Hilton" is created with address for user "snapUser" and customer with id "1238fd9a-a05d-42d8-8e84-42e904ace123"
         | addressLine1 | city | zipCode | country |
         | line 1       | Brno | 60200   | CZ      |
       Then Response code is "201"
       Then Body contains entity with attribute "property_code" value "CZBRQBRN"
       When Property with code "CZBRQBRN" is updated with data
         | propertyCode |
         | updated_code |
       Then Response code is "204"
       When Property "Brno Hilton" is requested
       Then Body contains entity with attribute "property_code" value "updated_code"
       When Property "Brno Hilton" is updated with address
         | addressLine1 | city   | zipCode | country |
         | updated line | London | 55544   | GB      |
       Then Response code is "204"
       When Property "Brno Hilton" is requested
#           The code was not changed (re-generated) by address update
       Then Body contains entity with attribute "property_code" value "updated_code"

