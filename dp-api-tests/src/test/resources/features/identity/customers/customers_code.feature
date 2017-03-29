@Identity
Feature: Customer code feature
  When creating a customer, an unique customer code is generated according to following rules:
    - Customer code is always returned (and unique)
    - Error is returned when trying to create/update customer_code field manually
    - Customer code is generated from his name, country and city according to rules specified in DP-1222
    - Customer code always contains only CAPITAL english (latin) letters. Even when customer's name contains chinese, arabic or any other characters.

  Background:
    Given Database is cleaned and default entities are created
    And Default Snapshot user is created


  Scenario Outline: Correct customer code is returned when none is sent for all company names
    Given Customer is created with random address
      | companyName     | email              | isDemoCustomer | timezone      |
      | <companyName>   | c1@snaphsot.travel | true           | Europe/Prague |
    Then Body contains entity with attribute "customer_code"
    And The "customer_code" attribute in response contains only CAPITAL latin characters or numbers

    Examples:
      | companyName              |
      | oneWordCompany           |
      | 12Company                |
      | Company A2               |
      | Comp 1024 bit            |
      | A five word company name |
      | 电脑坏了 我想玩电脑       |
      | الدولي العاشر ليونيكود   |
      | 2初ぞあ Japan            |

  Scenario: Customer code cannot be created manually
    When Customer is created with code
      | code       |  companyName | email              | isDemoCustomer | timezone      |
      | manualCode | Company 1    | c1@snaphsot.travel | true           | Europe/Prague |
    Then Response code is "422"
    And Custom code is 42201

  Scenario: Customer code cannot be updated manually
    Given Customer is created with random address
      | id                                   | companyName     | email              | isDemoCustomer | timezone      |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Company 1       | c1@snaphsot.travel | true           | Europe/Prague |
    When Customer code of customer with Id "a792d2b2-3836-4207-a705-42bbecf3d881" is updated with "updatedCustomerCode"
    Then Response code is "422"
    And Custom code is 42201

  Scenario Outline: Correct customer code is returned according to customers address
    When Customer "<companyName>" is created with address
      | addressLine1   | city   | zipCode   | country   |
      | <addressLine1> | <city> | <zipCode> | <country> |
    Then Response code is "201"
    Then Body contains entity with attribute "customer_code" value "<resultCode>"
#  Resulting code is Country Code + City Code + first 3 letters from Company Name (DP-1222)
    Examples:
      | companyName | addressLine1 | city       | zipCode | country | resultCode |
      | Čéšká firma | line 1       | Brno       | 60200   | CZ      | CZBRQCES   |
      | 21st Comp.  | line 2       | New York   | 11414   | US      | USNYC21S   |
      | Union Comp. | line 2,5     | Union City | 9307    | US      | USUCGUNI   |
      | Chinese Comp| line 3       | Beijing    | 23456   | CN      | CNBJSCHI   |

  Scenario: When generated customer code is not unique, smallest possible integer is concatenated
    When Customer "Hilton" is created with address
      | addressLine1 | city   | zipCode | country |
      | line 1       | Brno   | 60200   | CZ      |
    Then Response code is "201"
    Then Body contains entity with attribute "customer_code" value "CZBRQHIL"
    When Customer "Hilton" is created with address
      | addressLine1 | city   | zipCode | country |
      | line 1       | Brno   | 60200   | CZ      |
    Then Response code is "201"
    Then Body contains entity with attribute "customer_code" value "CZBRQHIL1"
    When Customer "Hilton" is created with address
      | addressLine1 | city   | zipCode | country |
      | line 1       | Brno   | 60200   | CZ      |
    Then Response code is "201"
    Then Body contains entity with attribute "customer_code" value "CZBRQHIL2"
