Feature: DP returns all customer's property sets which are available to certain user.

  Scenario: Setup 1
    Given Database is cleaned
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |

    Given The following users exist
      | userType | userName   | firstName | lastName | email                | timezone      | culture |
      #all properties availabe
      | customer | everything | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      #some properties not available
      | customer | limited    | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
      #user that has everything accessible
      | snapshot | snapshot   | snapshot  | root     | def3@snapshot.travel | Europe/Prague | cs-CZ   |

    Given Relation between user with username "everything" and customer with code "c1t" exists with isPrimary "true"
    Given Relation between user with username "limited" and customer with code "c1t" exists with isPrimary "true"

    #Even though user "snapshot" do not have relation with customer he should have access to all property sets
    #Given Relation between user with username "snapshot" and customer with code "c1t" exists with isPrimary "true"

    Given The following property sets exist for customer with code "c1t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
      | ps2_name        | ps2_description        | branch          |

    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague |
      | salesforceid_2 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague |
      | salesforceid_2 | p4_name      | p4_code      | http://www.snapshot.travel | p4@tenants.biz | true           | Europe/Prague |

    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p2_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p3_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p4_code" and property set with name "ps1_name" for customer with code "c1t" exists

    Given Relation between property with code "p1_code" and property set with name "ps2_name" for customer with code "c1t" exists
    Given Relation between property with code "p2_code" and property set with name "ps2_name" for customer with code "c1t" exists

    #user everything should get both property sets
    Given Relation between user with username "everything" and property with code "p1_code" exists
    Given Relation between user with username "everything" and property with code "p2_code" exists
    Given Relation between user with username "everything" and property with code "p3_code" exists
    Given Relation between user with username "everything" and property with code "p4_code" exists

    #limited should get only property set 2
    Given Relation between user with username "limited" and property with code "p1_code" exists
    Given Relation between user with username "limited" and property with code "p2_code" exists

    #user snapshot should get all property sets because he has access to all

  # -------------------------------------------------------------------------------------------------------------------
  Scenario: Setup 2
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone          |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Bratislava |

    Given The following users exist
      | userType | userName   | firstName | lastName | email                | timezone      | culture |
      #all properties availabe
      | customer | everything | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      #some properties not available
      | customer | limited    | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
      #user that has everything accessible
      | snapshot | snapshot   | snapshot  | root     | def3@snapshot.travel | Europe/Prague | cs-CZ   |

    Given The following users exist
      | userType | userName        | firstName | lastName | email                | timezone      | culture |
      | customer | everything_for2 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | limited_for2    | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
    #users without customer
      | customer | out1            | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
      | customer | out2            | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |



    Given Relation between user with username "everything" and customer with code "c1t" exists with isPrimary "true"
    Given Relation between user with username "limited" and customer with code "c1t" exists with isPrimary "true"

    Given Relation between user with username "everything_for2" and customer with code "c2t" exists with isPrimary "true"
    Given Relation between user with username "limited_for2" and customer with code "c2t" exists with isPrimary "true"

   #Even though user "snapshot" do not have relation with customer he should have access to all property sets
   #Given Relation between user with username "snapshot" and customer with code "c1t" exists with isPrimary "true"

    Given The following property sets exist for customer with code "c1t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
      | ps2_name        | ps2_description        | branch          |

    Given The following property sets exist for customer with code "c2t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps3_name        | ps1_description        | branch          |
      | ps4_name        | ps2_description        | branch          |


    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone      |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague |
      | salesforceid_2 | p2_name      | p2_code      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague |
      | salesforceid_2 | p3_name      | p3_code      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague |
      | salesforceid_2 | p4_name      | p4_code      | http://www.snapshot.travel | p4@tenants.biz | true           | Europe/Prague |

    Given Relation between property with code "p1_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p2_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p3_code" and property set with name "ps1_name" for customer with code "c1t" exists
    Given Relation between property with code "p4_code" and property set with name "ps1_name" for customer with code "c1t" exists

    Given Relation between property with code "p1_code" and property set with name "ps2_name" for customer with code "c1t" exists
    Given Relation between property with code "p2_code" and property set with name "ps2_name" for customer with code "c1t" exists

   #user everything should get both property sets
    Given Relation between user with username "everything" and property with code "p1_code" exists
    Given Relation between user with username "everything" and property with code "p2_code" exists
    Given Relation between user with username "everything" and property with code "p3_code" exists
    Given Relation between user with username "everything" and property with code "p4_code" exists

   #limited should get only property set 2
    Given Relation between user with username "limited" and property with code "p1_code" exists
    Given Relation between user with username "limited" and property with code "p2_code" exists

    #user snapshot should get all property sets because he has access to all


