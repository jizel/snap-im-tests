@Identity
Feature: Customers create update delete

  #TODO add etag things to get/update/create
  Background:
    Given Database is cleaned and default entities are created
    Given The following customers exist with random address
      | Id                                   | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      | isActive |
      | a792d2b2-3836-4207-a705-42bbecf3d881 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     |
    Given The following users exist for customer "a792d2b2-3836-4207-a705-42bbecf3d881" as primary "true"
      | userId                               | userType | userName     | firstName | lastName     | email                         | timezone      | culture |
      | a63edcc6-6830-457c-89b1-7801730bd0ae | snapshot | Snapshotuser | Snapshot  | SnapshotUser | snapshotUser1@snapshot.travel | Europe/Prague | cs-CZ   |

  @Smoke
  Scenario: Creating Customer
    When Customer is created with random address
      | companyName           | email          | salesforceId           | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | Creation test company | s1@tenants.biz | salesforceid_created_1 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Then Response code is "201"
    And Body contains entity with attribute "name" value "Creation test company"
    And Body contains entity with attribute "email" value "s1@tenants.biz"
    And Body contains entity with attribute "salesforce_id" value "salesforceid_created_1"
    And Body contains entity with attribute "phone" value "+420123456789"
    And Body contains entity with attribute "customer_code"
    And Body contains entity with attribute "is_active" value "false"

  Scenario Outline: Checking error codes for creating customer
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"
    Examples:
      | json_input_file                                                        | method | module   | url                 | error_code | custom_code    |
      | /messages/identity/customers/create_customer_missing_company_name.json | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_email_value.json    | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_field_email.json    | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_vatid_value.json    | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_country_value.json  | POST   | identity | /identity/customers | 422        | 42202          |
      | /messages/identity/customers/create_customer_wrong_phone_value.json    | POST   | identity | /identity/customers | 422        | 42201          |
      | /messages/identity/customers/create_customer_wrong_website_value.json  | POST   | identity | /identity/customers | 422        | 42201          |

#    TODO: error codes for updating customer
  Scenario Outline: Create foreign customers
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is 201
    And Content type is "application/json"
    And Body contains entity with attribute "name" value "<name>"
    Examples:
      | json_input_file                                                              | method | module   | url                 | name |
      | /messages/identity/customers/positive/create_customer_arabic_symbols.json    | POST   | identity | /identity/customers | ݑݒݓ  |
      | /messages/identity/customers/positive/create_customer_malayalam_symbols.json | POST   | identity | /identity/customers | ണതഥ  |
      | /messages/identity/customers/positive/create_customer_greek_symbols.json     | POST   | identity | /identity/customers | ᴦᴧᴨ  |
      | /messages/identity/customers/positive/create_customer_katakana_symbols.json  | POST   | identity | /identity/customers | ラリル  |
      | /messages/identity/customers/positive/create_customer_ancient_symbols.json   | POST   | identity | /identity/customers | 㐱㐲㐳  |
      | /messages/identity/customers/positive/create_customer_chinese_symbols.json   | POST   | identity | /identity/customers | 笅笆笇  |

  @Smoke
  Scenario: Deleting Customer
    When Customer with customer id "a792d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is "204"
    And Body is empty
    And Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" doesn't exist

  Scenario: Checking error code for deleting customer
    When Customer with customer id "NotExistentCustomer" is deleted
    Then Response code is "412"
    And Customer with id "NotExistentCustomer" doesn't exist

  #TODO update with error fields, bad values, missing fields
  #TODO update nonexistent field
  Scenario Outline: Updating customer
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is updated with data by user "SnapshotUser"
      | companyName   | email   | salesforceId   | vatId   | phone   | website   | notes   | timezone   |
      | <companyName> | <email> | <salesforceId> | <vatId> | <phone> | <website> | <notes> | <timezone> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And User with id "a63edcc6-6830-457c-89b1-7801730bd0ae" checks updated customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" has data
      | companyName   | email   | salesforceId   | vatId   | phone   | website   | notes   | timezone   |
      | <companyName> | <email> | <salesforceId> | <vatId> | <phone> | <website> | <notes> | <timezone> |
    Examples:
      | companyName        | email             | salesforceId         | vatId       | phone         | website              | notes         | timezone      |
      | updatedCompanyName | /null             | /null                | /null       | /null         | /null                | /null         | /null         |
      | /null              | updated@email.com | /null                | /null       | /null         | /null                | /null         | /null         |
      | /null              | /null             | salesforceid_updated | /null       | /null         | /null                | /null         | /null         |
      | /null              | /null             | /null                | CZ987654321 | /null         | /null                | /null         | /null         |
      | /null              | /null             | /null                | /null       | +420987654321 | /null                | /null         | /null         |
      | /null              | /null             | /null                | /null       | /null         | http://update.com    | /null         | /null         |
      | /null              | /null             | /null                | /null       | /null         | /null                | updatedNotes  | /null         |
      | /null              | /null             | /null                | /null       | /null         | /null                | /null         | Pacific/Fiji  |
      | severalUpdates     | /null             | /null                | /null       | +420111222333 | http://several.cz    | several_notes | Europe/Prague |
      | allUpdates         | all@all.com       | allUpdated_sf_id     | CZ999888777 | +420444555666 | http://allUpdated.cz | all_notes     | Asia/Tokyo    |


  #TODO update cutomer with not matched etag/empty etag/missing etag
  Scenario: Updating customer with outdated etag
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is updated with outdated etag
    Then Response code is "412"
    And Custom code is "41202"


  @Smoke
  Scenario: Customer is activated
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is activated
    Then Response code is "204"
    And Body is empty
    And Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is active

  Scenario: Customer is inactivated
    Given Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is activated
    When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is inactivated
    Then Response code is "204"
    And Body is empty
    And Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is not active

  Scenario: Headquarters timezone parameter is not optional (DP-1695)
    When Customer is created with random address
      | companyName           | email          | salesforceId           | vatId      | isDemoCustomer | phone         | website                    |
      | Creation test company | s1@tenants.biz | salesforceid_created_1 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel |
    Then Response code is "422"
    And Custom code is 42201

  Scenario Outline: Validate that customer regions belong to the correct country
    When A customer with following country "<country>", region "<region>", vatId "<vatId>" is created
    Then Content type is "application/json"
    And Response code is 201
    And Body contains entity with attribute "address.region" value "<region>"
    Examples:
      | country | region                            | vatId       |
      | US      | Alabama                           | US12345678  |
      | US      | Alaska                            | US12345678  |
      | US      | Arizona                           | US12345678  |
      | US      | Arkansas                          | US12345678  |
      | US      | California                        | US12345678  |
      | US      | Colorado                          | US12345678  |
      | US      | Connecticut                       | US12345678  |
      | US      | Delaware                          | US12345678  |
      | US      | Florida                           | US12345678  |
      | US      | Georgia                           | US12345678  |
      | US      | Hawaii                            | US12345678  |
      | US      | Idaho                             | US12345678  |
      | US      | Illinois                          | US12345678  |
      | US      | Indiana                           | US12345678  |
      | US      | Iowa                              | US12345678  |
      | US      | Kansas                            | US12345678  |
      | US      | Kentucky                          | US12345678  |
      | US      | Louisiana                         | US12345678  |
      | US      | Maine                             | US12345678  |
      | US      | Maryland                          | US12345678  |
      | US      | Massachusetts                     | US12345678  |
      | US      | Michigan                          | US12345678  |
      | US      | Minnesota                         | US12345678  |
      | US      | Mississippi                       | US12345678  |
      | US      | Missouri                          | US12345678  |
      | US      | Montana                           | US12345678  |
      | US      | Nebraska                          | US12345678  |
      | US      | Nevada                            | US12345678  |
      | US      | New Hampshire                     | US12345678  |
      | US      | New Jersey                        | US12345678  |
      | US      | New Mexico                        | US12345678  |
      | US      | New York                          | US12345678  |
      | US      | North Carolina                    | US12345678  |
      | US      | North Dakota                      | US12345678  |
      | US      | Ohio                              | US12345678  |
      | US      | Oklahoma                          | US12345678  |
      | US      | Oregon                            | US12345678  |
      | US      | Pennsylvania                      | US12345678  |
      | US      | Rhode Island                      | US12345678  |
      | US      | South Carolina                    | US12345678  |
      | US      | South Dakota                      | US12345678  |
      | US      | Tennessee                         | US12345678  |
      | US      | Texas                             | US12345678  |
      | US      | Utah                              | US12345678  |
      | US      | Vermont                           | US12345678  |
      | US      | Virginia                          | US12345678  |
      | US      | Washington                        | US12345678  |
      | US      | West Virginia                     | US12345678  |
      | US      | Wisconsin                         | US12345678  |
      | US      | Wyoming                           | US12345678  |
     # Australia regions
      | AU      | Ashmore and Cartier Islands       | 12345678901 |
      | AU      | Australian Antarctic Territory    | 12345678901 |
      | AU      | Australian Capital Territory      | 12345678901 |
      | AU      | Christmas Island                  | 12345678901 |
      | AU      | Cocos (Keeling) Islands           | 12345678901 |
      | AU      | Coral Sea Islands                 | 12345678901 |
      | AU      | Heard Island and McDonald Islands | 12345678901 |
      | AU      | Jervis Bay Territory              | 12345678901 |
      | AU      | New South Wales                   | 12345678901 |
      | AU      | Norfolk Island                    | 12345678901 |
      | AU      | Northern Territory                | 12345678901 |
      | AU      | Queensland                        | 12345678901 |
      | AU      | South Australia                   | 12345678901 |
      | AU      | Tasmania                          | 12345678901 |
      | AU      | Victoria                          | 12345678901 |
    # Canada regions
      | CA      | Ontario                           | 123456789   |
      | CA      | Quebec                            | 123456789   |
      | CA      | Nova Scotia                       | 123456789   |
      | CA      | New Brunswick                     | 123456789   |
      | CA      | Manitoba                          | 123456789   |
      | CA      | British Columbia                  | 123456789   |
      | CA      | Prince Edward Island              | 123456789   |
      | CA      | Saskatchewan                      | 123456789   |
      | CA      | Alberta                           | 123456789   |
      | CA      | Newfoundland and Labrador         | 123456789   |
      | CA      | Northwest Territories             | 123456789   |
      | CA      | Yukon                             | 123456789   |
      | CA      | Nunavut                           | 123456789   |

  Scenario Outline: Checking error codes for regions
    When A customer with following country "<country>", region "<region>", vatId "<vatId>" is created
    Then Content type is "application/json"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "message" value "Reference does not exist. The entity Region with ID <region> cannot be found."

    Examples:
      | country | region           | vatId    | response_code | custom_code |
      | DE      | invalid          | 12345678 | 422           | 42202       |
      | BG      | invalid          | 12345678 | 422           | 42202       |
      | US      | invalid          | 12345678 | 422           | 42202       |
      | CZ      | invalid          | 12345678 | 422           | 42202       |
      | AU      | invalid          | 12345678 | 422           | 42202       |
      | US      | bg_region        | 12345678 | 422           | 42202       |
      | CZ      | us_region        | 12345678 | 422           | 42202       |
      | AU      | bg_region        | 12345678 | 422           | 42202       |
      | AU      | VictoriaRegion   | 12345678 | 422           | 42202       |
      | AU      | TheGreatTasmania | 12345678 | 422           | 42202       |
      | CA      | Yukon region     | 12345678 | 422           | 42202       |
      | CA      | TheGreatYukon    | 12345678 | 422           | 42202       |

  Scenario Outline: Checking error codes for regions with invalid VatId
    When A customer with following country "<country>", region "<region>", vatId "<vatId>" is created
    Then Content type is "application/json"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    Examples:
      | country | region   | vatId      | response_code | custom_code |
      | AU      | Tasmania | notValidId | 422           | 42201       |
      | AU      | Tasmania | 1234       | 422           | 42201       |
      | CA      | Yukon    | notValid   | 422           | 42201      |
      | CA      | Yukon    | CZ1234578  | 422           | 42201       |

  Scenario Outline: Validate that customer has invalid VAT ID
    When A customer with following country "<country>", region "/null", vatId "<vatId>" is created
    Then Content type is "application/json"
    And Response code is 422
    And Custom code is 42201

    Examples:
      | country | vatId                |
      | AL      | K414A4801U           |
      | AL      | K414A4801U           |
      | AL      | K41424801            |
      | AL      | K4142481U            |
      | AU      | 5634572455           |
      | AU      | 5634572455z          |
      | AU      | 5634572455Z          |
      | AU      | 563457245533         |
      | AU      | 53 004 085 616       |
      | BY      | 10058192             |
      | BY      | 10058192A            |
      | BY      | 1005819211           |
      | BY      | A10058192            |
      | CA      | CZ12345678           |
      | IS      | 1234567              |
      | IN      | 5634572455V          |
      | IN      | 563V5724553V         |
      | ID      | 99.999.999.9-999.99  |
      | ID      | 99.999.999.9-99.999  |
      | ID      | 99.999.99.9-999.999  |
      | ID      | 99.99.999.9-999.9999 |
      | ID      | 99.999.9A9.9-999.999 |
      | MC      | 9999999999X          |
      | MC      | 9999999999           |
      | NO      | 87654321MVA          |
      | NO      | 987654321            |
      | NO      | 987654321XYZ         |
      | NO      | 9AB654321MVA         |
      | PH      | 999.999.999.999N     |
      | PH      | 999.999.999.999V     |
      | RU      | 123456789            |
      | RU      | A234567890           |
      | RU      | 12345678901          |
      | SM      | 1ABC25               |
      | SM      | 123456               |
      | SM      | 1234                 |
      | RS      | 1234567890           |
      | RS      | 12345678             |
      | RS      | 123456A89            |
      | CH      | CHE-999.999.999      |
      | CH      | CHE-999.999.999MWST  |
      | CH      | CHE-999.ABC.999 MWST |
      | CH      | CHE999.999.999MWST   |
      | CH      | CHE-999999999 MWST   |
      | TR      | 123456789            |
      | TR      | 12345678901          |
      | TR      | 123A567890           |
      | UA      | 123456789            |
      | UA      | 12345678901          |
      | UA      | 123A567890           |
      | AR      | 123456789012         |
      | AR      | 1234567890           |
      | AR      | 123456789A1          |
      | BR      | 12.123.123/1234-123  |
      | BR      | 12.123.123 1234-12   |
      | BR      | 123.123.123/1234-12  |
      | CL      | 12345678-12          |
      | CL      | 123456789-1          |
      | CL      | 12345A78-1           |
      | CR      | CZ12345678           |
      | EC      | '123456789012        |
      | EC      | 12345678901234       |
      | EC      | 123456789012A        |
      | GT      | 123456A-1            |
      | GT      | 12345678-1           |
      | GT      | 1234567-11           |
      | GT      | 1234567-Z            |
      | MX      | ABCD123456ABCD       |
      | MX      | ABCD123Z56ABC        |
      | MX      | ABCD123456AB         |
      | PE      | 1234567890           |
      | PE      | 1234567890A          |
      | PE      | 123456789012         |
      | DO      | 12345678             |
      | DO      | 1234567890           |
      | DO      | 12345678Z            |
      | VE      | V-1234567890         |
      | VE      | Z23456789            |
      | AT      | ATU1234567           |
      | AT      | ATA12345678          |
      | AT      | AT12345678           |
      | AT      | ATU123456789         |
      | AT      | ATUABC45678          |
      | BE      | BE11234567890        |
      | BE      | BE0ABC456789         |
      | BE      | BE012345678          |
      | BE      | BE8123456789         |
      | BG      | BG12345678           |
      | BG      | BG12345678901        |
      | BG      | BG123ABC789          |
      | CY      | CY1234567X           |
      | CY      | CY123456789X         |
      | CY      | CY123456789          |
      | CY      | CYABC45678X          |
      | CZ      | CZ1234567            |
      | CZ      | CZ12345678901        |
      | CZ      | CZ123456ABC          |
      | DE      | DE12345678           |
      | DE      | DE1234567891         |
      | DE      | DEABC456789          |
      | DK      | DK1234567            |
      | DK      | DK123456789          |
      | DK      | DK12  34  56  78     |
      | EE      | EE12345678           |
      | EE      | EE1234567891         |
      | EE      | EEABC456789          |
      | ES      | ESxx123456789        |
      | ES      | ESX1234567           |
      | ES      | ES12345678           |
      | ES      | ESX12345678X         |
      | FI      | FI1234567            |
      | FI      | FI912345678          |
      | FI      | FIABC45678           |
      | FR      | FR123456789012       |
      | FR      | FR1234567890         |
      | FR      | FR1234567XXX1        |
      | GB      | GB12 1234 12 123     |
      | GB      | GB123 123 12 123     |
      | GB      | GB123 1234 1 123     |
      | GB      | GB123 1234 12 12     |
      | GB      | GBHA59               |
      | GB      | GBH1599              |
      | GB      | GB1A599              |
      | GB      | GBGD01               |
      | GB      | GBG1001              |
      | GB      | GB1D001              |
      | GB      | GB12 4567 89         |
      | GB      | GB123 456 89         |
      | GB      | GB123 4567 8         |
      | GB      | GB12345678           |
      | GB      | GB1234567XX          |
      | HU      | HU1234567            |
      | HU      | HU912345678          |
      | HU      | HUABC45678           |
      | IT      | IT1234567890         |
      | IT      | IT123456789012       |
      | IT      | IT123XXX78901        |
      | LT      | LT12345678           |
      | LT      | LT1234567890123      |
      | LT      | LT123XXX789          |
      | LT      | LT123456XXX012       |
      | LU      | LU1234567            |
      | LU      | LU912345678          |
      | LU      | LUABC45678           |
      | LV      | LV1234567890         |
      | LV      | LV123456789012       |
      | LV      | LV123XXX78901        |
      | MT      | MT1234567            |
      | MT      | MT912345678          |
      | MT      | MTABC45678           |
      | NL      | NL123456789B0        |
      | NL      | NL123456789B012      |
      | NL      | NL123456789A01       |
      | NL      | NL123456789101       |
      | PL      | PL123456789          |
      | PL      | PL12345678901        |
      | PL      | PL123XXX789          |
      | PT      | PT12345678           |
      | PT      | PT1234567891         |
      | PT      | PTABC456789          |
      | RO      | RO1                  |
      | RO      | RO12345678901        |
      | RO      | RO12X                |
      | SE      | SE12345678901        |
      | SE      | SE1234567890123      |
      | SE      | SE1234XXX89012       |
      | SI      | SI1234567            |
      | SI      | SI912345678          |
      | SI      | SIABC45678           |
      | SK      | SK123456789          |
      | SK      | SK12345678901        |
      | SK      | SK123XXX789          |
      | HR      | HR1234567890         |
      | HR      | HR123456789012       |
      | HR      | HR123XXX78901        |

  Scenario Outline: Validate that customer has valid VAT ID
    When A customer with following country "<country>", region "/null", vatId "<vatId>" is created
    Then Content type is "application/json"
    And Response code is 201
    And Body contains entity with attribute "vat_id" value "<vatId>"

    Examples:
      | country | vatId                |
      | AL      | K71501003R           |
      | AL      | K91811014B           |
      | AL      | K41424801U           |
      | AU      | 53004085616          |
      | BY      | 100581921            |
      | CA      | 123456789BC0001      |
      | CA      | 123456789            |
      | CA      | 123456789 BC0001     |
      | CA      | 123456789 BW0001     |
      | CA      | 123456789 BB0001     |
      | IS      | 12ABCD               |
      | IS      | ABCDEF               |
      | IS      | 123456               |
      | IS      | 789BC1               |
      | IN      | 56345724553V         |
      | IN      | 56345724553C         |
      | ID      | 99.999.999.9-999.999 |
      | ID      | 02.271.824.1-413.000 |
      | MC      | 99999999999          |
      | MC      | X9999999999          |
      | MC      | 9X999999999          |
      | MC      | xx999999999          |
      | MC      | XX999999999          |
      | NO      | 987654321MVA         |
      | NO      | 190190190MVA         |
      | PH      | 123 123 123 123      |
      | RU      | 2190190190           |
      | RU      | 1234567890           |
      | SM      | 12345                |
      | SM      | 99999                |
      | RS      | 190190190            |
      | RS      | 123456789            |
      | CH      | CHE-123.456.789 TVA  |
      | CH      | 123456               |
      | CH      | CHE-999.999.999 MWST |
      | CH      | CHE-999.999.999 IVA  |
      | CH      | CHE-999.999.999 TVA  |
      | TR      | 2190190190           |
      | TR      | 1234567890           |
      | UA      | 2190190190           |
      | UA      | 1234567890           |
      | AR      | 23190190190          |
      | AR      | 12345678901          |
      | BR      | 11.111.111/0001-55   |
      | BR      | 12.123.123/1234-12   |
      | CL      | 12345678-1           |
      | CL      | 12345678-K           |
      | CL      | 1234567-1            |
      | CR      | 1234567890           |
      | CR      | 12345678901          |
      | CR      | 123456789012         |
      | EC      | 1234567890123        |
      | GT      | 1234567-1            |
      | MX      | AAGB860519G31        |
      | MX      | ABCD123456ABC        |
      | MX      | ABC123456ABC         |
      | MX      | ABCD123456123        |
      | PE      | 23190190190          |
      | PE      | 12345678901          |
      | DO      | 190190190            |
      | DO      | 123456789            |
      | VE      | J-305959918          |
      | VE      | V-123456789          |
      | VE      | E-123456789          |
      | VE      | J-123456789          |
      | VE      | G-123456789          |
      | AT      | ATU12345678          |
      | AT      | ATU00000000          |
      | BE      | BE1987654321         |
      | BE      | BE0987654321         |
      | BG      | BG123456789          |
      | BG      | BG1234567890         |
      | CY      | CY12345678X          |
      | CZ      | CZ12345678           |
      | CZ      | CZ123456789          |
      | CZ      | CZ1234567890         |
      | DE      | DE123456789          |
      | DK      | DK00000000           |
      | DK      | DK12345678           |
      | EE      | EE123456789          |
      | GR      | EL123456789          |
      | ES      | ESX12345678          |
      | ES      | ES12345678X          |
      | ES      | ESX1234567X          |
      | FI      | FI12345678           |
      | FR      | FR12345678901        |
      | FR      | FRX1234567890        |
      | FR      | FR1X123456789        |
      | FR      | FRXX123456789        |
      | FR      | FRXX 123456789       |
      | GB      | GB123456789          |
      | GB      | GB123 4567 89        |
      | GB      | GBGD001              |
      | GB      | GBHA599              |
      | GB      | GB123 1234 12 123    |
      | HU      | HU12345678           |
      | IE      | IE1234567X           |
      | IE      | IE1X23456X           |
      | IE      | IE1+23456X           |
      | IE      | IE1*23456X           |
      | IT      | IT12345678901        |
      | LT      | LT123456789          |
      | LT      | LT123456789012       |
      | LU      | LU12345678           |
      | LV      | LV12345678901        |
      | MT      | MT12345678           |
      | NL      | NL123456789B01       |
      | PL      | PL1234567890         |
      | PT      | PT123456789          |
      | RO      | RO12                 |
      | RO      | RO123                |
      | RO      | RO1234               |
      | RO      | RO12345              |
      | RO      | RO123456             |
      | RO      | RO1234567            |
      | RO      | RO12345678           |
      | RO      | RO123456789          |
      | RO      | RO1234567890         |
      | SE      | SE123456789012       |
      | SI      | SI12345678           |
      | SK      | SK1234567890         |
      | HR      | HR12345678901        |

  @Bug
  Scenario: Creating customer with same name as previously deleted one - DP-1380
    Given The following customers exist with random address
      | Id                                   | companyName | email              | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 0002d2b2-3836-4207-a705-42bbecf3d881 | Company 1   | c1@snapshot.travel | salesforceid_1 | CZ11100001 | true           | +420321456789 | http://www.snapshot.travel | Europe/Prague |
    Then Response code is 201
    When Customer with customer id "0002d2b2-3836-4207-a705-42bbecf3d881" is deleted
    Then Response code is 204
    Given The following customers exist with random address
      | Id                                   | companyName | email              | salesforceId   | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1112d2b2-3836-4207-a705-42bbecf3d881 | Company 1   | c1@snapshot.travel | salesforceid_1 | CZ11100001 | true           | +420321456789 | http://www.snapshot.travel | Europe/Prague |
    Then Response code is 201
    And Body contains entity with attribute "customer_id" value "1112d2b2-3836-4207-a705-42bbecf3d881"
    And Body contains entity with attribute "salesforce_id" value "salesforceid_1"
    And Body contains entity with attribute "name" value "Company 1"

  Scenario Outline: Send POST request with empty body to all configurations endpoints
    When Empty POST request is sent to "<url>" on module "identity"
    Then Response code is "422"
    And Custom code is "42201"
    Examples:
      | url                                                                                                |
      | identity/customers/                                                                                |
      | identity/customers/a792d2b2-3836-4207-a705-42bbecf3d881                                            |
      | identity/customers/a792d2b2-3836-4207-a705-42bbecf3d881/properties                                 |
      | identity/customers/a792d2b2-3836-4207-a705-42bbecf3d881/users                                      |
      | identity/customers/a792d2b2-3836-4207-a705-42bbecf3d881/users/a63edcc6-6830-457c-89b1-7801730bd0ae |

    Scenario: Customer ID must be unique when creating customer - DP-1661
      Given The following customers exist with random address
        | Id                                   | companyName       | email              | salesforceId   | vatId      | isDemoCustomer | timezone      |
        | 00011222-3836-4207-a705-42bbecf3d881 | Original Customer | oc@snapshot.travel | original_sf_id | CZ10000001 | true           | Europe/Prague |
      When Customer is created with random address
        | Id                                   | companyName  | email                   | salesforceId | vatId      | isDemoCustomer | timezone      |
        | 00011222-3836-4207-a705-42bbecf3d881 | New Customer | newcust@snapshot.travel | sf_id2       | CZ20000002 | true           | Europe/Prague |
      Then Response code is "409"
      And Custom code is 40902

      Scenario: CustomerId and parentId must be different - DP-1528
        When Customer is created with random address
          | Id                                   | parentId                             | companyName               | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      | isActive |
          | a792d2b2-3836-4207-a705-42bbecf3d881 | a792d2b2-3836-4207-a705-42bbecf3d881 | Already existing custoemr | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     |
#        Already existing customerId
        Then Response code is "409"
        And Custom code is 40902
        When Customer is created with random address
          | Id                                   | parentId                             | companyName  | email                   | salesforceId | vatId      | isDemoCustomer | timezone      |
          | 00011222-3836-4207-a705-42bbecf3d881 | 00011222-3836-4207-a705-42bbecf3d881 | New Customer | newcust@snapshot.travel | sf_id2       | CZ20000002 | true           | Europe/Prague |
#        Nonexistent customerId
        Then Response code is "422"
        And Custom code is 42202
        Then Customer with id "00011222-3836-4207-a705-42bbecf3d881" doesn't exist

      Scenario: Parent-child relationship should not contain loops - DP-1395
        Given The following customers exist with random address
          | Id                                   | parentId                             | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      | isActive |
          | 00011222-3836-4207-a705-42bbecf3d881 | a792d2b2-3836-4207-a705-42bbecf3d881 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     |
          | 10011222-3836-4207-a705-42bbecf3d881 | 00011222-3836-4207-a705-42bbecf3d881 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000002 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague | true     |
        When Customer with id "a792d2b2-3836-4207-a705-42bbecf3d881" is updated with data by user "Snapshotuser"
          | parentId                             |
          | 10011222-3836-4207-a705-42bbecf3d881 |
        Then Response code is "409"
        Then Custom code is 40911