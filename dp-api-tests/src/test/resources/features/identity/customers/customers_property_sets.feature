Feature: customers_property_sets

  Background:

    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel |
      | Given company 2 | c2@tenants.biz | c2t  | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel |

    Given All property sets are deleted for customers with codes: c1t, c2t

    Given The following property sets exist for customer with code "c1t"
      | propertySetName | propertySetDescription | propertySetType |
      | ps1_name        | ps1_description        | branch          |
      | ps2_name        | ps2_description        | branch          |
      | ps3_name        | ps3_description        | branch          |

