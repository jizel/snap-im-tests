Feature: customers_create_update_delete

  #TODO add etag things to get/update/create
  Background: 
    Given Database is cleaned
    Given The following customers exist with random address
      | companyName     | email          | code | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | Given company 1 | c1@tenants.biz | c1t  | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given All users are removed for customers with codes: c1t, c2t

  Scenario: Creating Customer without parent with random address
    When Customer is created with random address
      | companyName           | email          | code | salesforceId           | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | Creation test company | s1@tenants.biz | s1t  | salesforceid_created_1 | CZ00000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Then Response code is "201"
    And Body contains entity with attribute "company_name" value "Creation test company"
    And Body contains entity with attribute "email" value "s1@tenants.biz"
    And Body contains entity with attribute "code" value "s1t"
    And "Location" header is set and contains the same customer

  Scenario Outline: Checking error codes for creating customer
    When File "<json_input_file>" is used for "<method>" to "<url>" on "<module>"
    Then Response code is "<error_code>"
    And Custom code is "<custom_code>"

    Examples: 
      | json_input_file                                                                 | method | module   | url                 | error_code | custom_code |
      | /messages/identity/customers/create_customer_missing_company_name.json          | POST   | identity | /identity/customers | 400        | 53          |
      | /messages/identity/customers/create_customer_not_unique_company_name_email.json | POST   | identity | /identity/customers | 400        | 62          |
      | /messages/identity/customers/create_customer_not_unique_code_email.json         | POST   | identity | /identity/customers | 400        | 62          |
      | /messages/identity/customers/create_customer_wrong_email_value.json             | POST   | identity | /identity/customers | 400        | 59          |
      | /messages/identity/customers/create_customer_wrong_field_email.json             | POST   | identity | /identity/customers | 400        | 56          |
      | /messages/identity/customers/create_customer_wrong_vatid_value.json             | POST   | identity | /identity/customers | 400        | 59          |
      | /messages/identity/customers/create_customer_wrong_country_value.json           | POST   | identity | /identity/customers | 400        | 59          |
      | /messages/identity/customers/create_customer_wrong_phone_value.json             | POST   | identity | /identity/customers | 400        | 59          |
      | /messages/identity/customers/create_customer_wrong_website_value.json           | POST   | identity | /identity/customers | 400        | 59          |

  #add wrong web, null customer code,
  Scenario: Deleting Customer
    When Customer with code "c1t" is deleted
    Then Response code is "204"
    And Body is empty
    And Customer with same id doesn't exist

  Scenario: Checking error code for deleting customer
    When Nonexistent customer id is deleted
    Then Response code is "204"

  Scenario Outline: Updating customer
    Given The following customers with codes don't exist
      | UPDATED_CODE_c1t |
    When Customer with code "c1t" is updated with data
      | companyName   | email   | code   | salesforceId   | vatId   | phone   | website   | notes   | timezone   |
      | <companyName> | <email> | <code> | <salesforceId> | <vatId> | <phone> | <website> | <notes> | <timezone> |
    Then Response code is "204"
    And Body is empty
    And Etag header is present
    And Updated customer with code "<code>" has data
      | companyName   | email   | code   | salesforceId   | vatId   | phone   | website   | notes   | timezone   |
      | <companyName> | <email> | <code> | <salesforceId> | <vatId> | <phone> | <website> | <notes> | <timezone> |

    Examples: 
      | companyName | email | code             | salesforceId         | vatId      | phone         | website        | notes | timezone      |
      | /null       | /null | c1t              | salesforceid_updated | CZ10000001 | /null         | /null          | /null | Europe/Prague |
      | /null       | /null | c1t              | /null                | /null      | +420123456789 | http://test.cz | /null | /null         |
      | /null       | /null | UPDATED_CODE_c1t | /null                | /null      | /null         | /null          | /null | /null         |

  #TODO update cutomer with not matched etag/empty etag/missing etag
  # update with error fields, bad values, missing fields
  # update nonexistent field
  Scenario: Updating customer with outdated etag
    When Customer with code "c1t" is updated with data if updated before
      | companyName | email | code | salesforceId | vatId | phone | website        | notes |
      |             |       |      |              |       |       | http://test.cz |       |
    Then Response code is "412"
    And Custom code is "57"

  #error codes
  #Scenario Outline: Checking error codes for updating customer
  #When File "<json_input_file>" is used for "<method>"
  #Then Response code is "<error_code>"
  #And Custom code is "<custom_code>"
  # Examples:
  #   | json_input_file                           | method | error_code | custom_code |
  #   | create_customer_missing_company_name.json | POST   | 405        | 51          |
  #   | create_customer_wrong_field_name.json     | POST   | 405        | 51          |
  #   | create_customer_wrong_field_name.json     | POST   | 405        | 51          |
  #   | create_customer_faulty_address.json       | POST   | 405        | 51          |
  #field cannot be updated
  #mandatory atribute is empty
  #wrong field name
  #id doesn't exist
  #too long string in attribute
  #invalid value in attribute
  Scenario: Customer is activated
    When Customer with code "c1t" is activated
    Then Response code is "204"
    And Body is empty
    And Customer with code "c1t" is active

  #error codes
  Scenario: Customer is inactivated
    Given Customer with code "c1t" is activated
    When Customer with code "c1t" is inactivated
    Then Response code is "204"
    And Body is empty
    And Customer with code "c1t" is not active

  Scenario Outline: Validate that customer regions belong to the correct country
    When A customer from country "<country>" region "<region>" code "<code>" email "<email>" is created
    Then Content type is "application/json"
    And Response code is 201
    And Body contains entity with attribute "address.region" value "<region>"

    Examples: 
      | country | region         | code       | email           |
      | US      | Alabama        | custcode1  | mail1@mail.com  |
      | US      | Alaska         | custcode2  | mail2@mail.com  |
      | US      | Arizona        | custcode3  | mail3@mail.com  |
      | US      | Arkansas       | custcode4  | mail4@mail.com  |
      | US      | California     | custcode5  | mail5@mail.com  |
      | US      | Colorado       | custcode6  | mail6@mail.com  |
      | US      | Connecticut    | custcode7  | mail7@mail.com  |
      | US      | Delaware       | custcode8  | mail8@mail.com  |
      | US      | Florida        | custcode9  | mail9@mail.com  |
      | US      | Georgia        | custcode10 | mail10@mail.com |
      | US      | Hawaii         | custcode11 | mail11@mail.com |
      | US      | Idaho          | custcode12 | mail12@mail.com |
      | US      | Illinois       | custcode13 | mail13@mail.com |
      | US      | Indiana        | custcode14 | mail14@mail.com |
      | US      | Iowa           | custcode15 | mail15@mail.com |
      | US      | Kansas         | custcode16 | mail16@mail.com |
      | US      | Kentucky       | custcode17 | mail17@mail.com |
      | US      | Louisiana      | custcode18 | mail18@mail.com |
      | US      | Maine          | custcode19 | mail19@mail.com |
      | US      | Maryland       | custcode20 | mail20@mail.com |
      | US      | Massachusetts  | custcode21 | mail21@mail.com |
      | US      | Michigan       | custcode22 | mail22@mail.com |
      | US      | Minnesota      | custcode23 | mail23@mail.com |
      | US      | Mississippi    | custcode24 | mail24@mail.com |
      | US      | Missouri       | custcode25 | mail25@mail.com |
      | US      | Montana        | custcode26 | mail26@mail.com |
      | US      | Nebraska       | custcode27 | mail27@mail.com |
      | US      | Nevada         | custcode28 | mail28@mail.com |
      | US      | New Hampshire  | custcode29 | mail29@mail.com |
      | US      | New Jersey     | custcode30 | mail30@mail.com |
      | US      | New Mexico     | custcode31 | mail31@mail.com |
      | US      | New York       | custcode32 | mail32@mail.com |
      | US      | North Carolina | custcode33 | mail33@mail.com |
      | US      | North Dakota   | custcode34 | mail34@mail.com |
      | US      | Ohio           | custcode35 | mail35@mail.com |
      | US      | Oklahoma       | custcode36 | mail36@mail.com |
      | US      | Oregon         | custcode37 | mail37@mail.com |
      | US      | Pennsylvania   | custcode38 | mail38@mail.com |
      | US      | Rhode Island   | custcode39 | mail39@mail.com |
      | US      | South Carolina | custcode40 | mail40@mail.com |
      | US      | South Dakota   | custcode41 | mail41@mail.com |
      | US      | Tennessee      | custcode42 | mail42@mail.com |
      | US      | Texas          | custcode43 | mail43@mail.com |
      | US      | Utah           | custcode44 | mail44@mail.com |
      | US      | Vermont        | custcode45 | mail45@mail.com |
      | US      | Virginia       | custcode46 | mail46@mail.com |
      | US      | Washington     | custcode47 | mail47@mail.com |
      | US      | West Virginia  | custcode48 | mail48@mail.com |
      | US      | Wisconsin      | custcode49 | mail49@mail.com |
      | US      | Wyoming        | custcode50 | mail50@mail.com |

  Scenario Outline: Checking error codes for regions
    When A property from country "<country>" region "<region>" code "<code>" email "<email>" is created
    Then Content type is "application/json"
    And Response code is <response_code>
    And Custom code is "<custom_code>"
    And Body contains entity with attribute "message" value "Region with identifier <region> was not found."

    Examples: 
      | country | region    | code       | email           | response_code | custom_code |
      | DE      | invalid   | propcode8  | mail8@mail.com  | 400           | 63          |
      | BG      | invalid   | propcode9  | mail9@mail.com  | 400           | 63          |
      | US      | invalid   | propcode10 | mail10@mail.com | 400           | 63          |
      | CZ      | invalid   | propcode11 | mail11@mail.com | 400           | 63          |
      | AU      | invalid   | propcode12 | mail12@mail.com | 400           | 63          |
      | US      | bg_region | propcode13 | mail13@mail.com | 400           | 63          |
      | CZ      | us_region | propcode14 | mail14@mail.com | 400           | 63          |
      | AU      | bg_region | propcode15 | mail15@mail.com | 400           | 63          |

  Scenario Outline: Validate that customer has invalid VAT ID
    When A customer from country "<country>" code "<code>" email "<email>" vatId "<vatId>" company nqma "<companyName>"is created
    Then Content type is "application/json"
    And Response code is 400
    And Custom code is 59

    Examples: 
      | country | code      | email                       | vatId                | companyName    |
      | AL      | AL_code   | albania@mail.com            | K414A4801U           | companyName1   |
      | AL      | AL_code   | albania@mail.com            | K414A4801U           | companyName2   |
      | AL      | AL_code2  | albania2@mail.com           | K41424801            | companyName3   |
      | AL      | AL_code3  | albania3@mail.com           | K4142481U            | companyName4   |
      | AU      | AU_code   | australia@mail.com          | 5634572455           | companyName5   |
      | AU      | AU_code1  | australia1@mail.com         | 5634572455z          | companyName6   |
      | AU      | AU_code2  | australia2@mail.com         | 5634572455Z          | companyName7   |
      | AU      | AU_code3  | australia3@mail.com         | 563457245533         | companyName8   |
      | AU      | AU_code4  | australia4@mail.com         | 53 004 085 616       | companyName9   |
      | BY      | BY_code   | belarus@mail.com            | 10058192             | companyName10  |
      | BY      | BY_code2  | belarus1@mail.com           | 10058192A            | companyName11  |
      | BY      | BY_code3  | belarus2@mail.com           | 1005819211           | companyName12  |
      | BY      | BY_code4  | belarus3@mail.com           | A10058192            | companyName13  |
      | CA      | CA_code   | canada@mail.com             | CZ12345678           | companyName14  |
      | IS      | IS_code   | iceland@mail.com            | 1234567              | companyName15  |
      | IN      | IN_code   | india@mail.com              | 5634572455V          | companyName16  |
      | IN      | IN_code1  | india1@mail.com             | 563V5724553V         | companyName17  |
      | ID      | ID_code   | indonesia@mail.com          | 99.999.999.9-999.99  | companyName18  |
      | ID      | ID_code1  | indonesia1@mail.com         | 99.999.999.9-99.999  | companyName19  |
      | ID      | ID_code2  | indonesia2@mail.com         | 99.999.99.9-999.999  | companyName20  |
      | ID      | ID_code3  | indonesia3@mail.com         | 99.99.999.9-999.9999 | companyName21  |
      | ID      | ID_code4  | indonesia4@mail.com         | 99.999.9A9.9-999.999 | companyName22  |
      | MC      | MC_code   | Monaco@mail.com             | 9999999999X          | companyName23  |
      | MC      | MC_code1  | Monaco1@mail.com            | 9999999999           | companyName24  |
      | NO      | NO_code   | Norway@mail.com             | 87654321MVA          | companyName25  |
      | NO      | NO_code1  | Norway1@mail.com            | 987654321            | companyName26  |
      | NO      | NO_code2  | Norway2@mail.com            | 987654321XYZ         | companyName27  |
      | NO      | NO_code3  | Norway3@mail.com            | 9AB654321MVA         | companyName28  |
      | PH      | PH_code   | Philippines@mail.com        | 999.999.999.999N     | companyName29  |
      | PH      | PH_code1  | Philippines1@mail.com       | 999.999.999.999V     | companyName30  |
      | RU      | RU_code   | Russia@mail.com             | 123456789            | companyName31  |
      | RU      | RU_code1  | Russia1@mail.com            | A234567890           | companyName32  |
      | RU      | RU_code2  | Russia2@mail.com            | 12345678901          | companyName33  |
      | SM      | SM_code   | SanMarino@mail.com          | 1ABC25               | companyName34  |
      | SM      | SM_code1  | SanMarino1@mail.com         | 123456               | companyName35  |
      | SM      | SM_code2  | SanMarino2@mail.com         | 1234                 | companyName36  |
      | RS      | RS_code   | Serbia@mail.com             | 1234567890           | companyName37  |
      | RS      | RS_code1  | Serbia1@mail.com            | 12345678             | companyName38  |
      | RS      | RS_code2  | Serbia2@mail.com            | 123456A89            | companyName39  |
      | CH      | CH_code   | Switzerland@nail.com        | CHE-999.999.999      | companyName40  |
      | CH      | CH_code1  | Switzerland1@nail.com       | CHE-999.999.999MWST  | companyName41  |
      | CH      | CH_code2  | Switzerland2@nail.com       | CHE-999.ABC.999 MWST | companyName42  |
      | CH      | CH_code3  | Switzerland3@nail.com       | CHE999.999.999MWST   | companyName43  |
      | CH      | CH_code4  | Switzerland4@nail.com       | CHE-999999999 MWST   | companyName44  |
      | TR      | TR_code   | Turkey@nail.com             | 123456789            | companyName45  |
      | TR      | TR_code1  | Turkey1@nail.com            | 12345678901          | companyName46  |
      | TR      | TR_code2  | Turkey2@nail.com            | 123A567890           | companyName47  |
      | UA      | UA_code   | Ukraine@nail.com            | 123456789            | companyName48  |
      | UA      | UA_code1  | Ukraine1@nail.com           | 12345678901          | companyName49  |
      | UA      | UA_code2  | Ukraine2@nail.com           | 123A567890           | companyName50  |
      | AR      | AR_code   | Argentina@nail.com          | 123456789012         | companyName51  |
      | AR      | AR_code1  | Argentina1@nail.com         | 1234567890           | companyName52  |
      | AR      | AR_code2  | Argentina2@nail.com         | 123456789A1          | companyName53  |
      | BR      | BR_code   | Brazil@nail.com             | 12.123.123/1234-123  | companyName54  |
      | BR      | BR_code1  | Brazil1@nail.com            | 12.123.123 1234-12   | companyName55  |
      | BR      | BR_code2  | Brazil2@nail.com            | 123.123.123/1234-12  | companyName56  |
      | CL      | CL_code   | Chile@nail.com              | 12345678-12          | companyName57  |
      | CL      | CL_code2  | Chile2@nail.com             | 123456789-1          | companyName58  |
      | CL      | CL_code3  | Chile3@nail.com             | 12345A78-1           | companyName59  |
      | CR      | CR_code   | CostaRica@nail.com          | CZ12345678           | companyName60  |
      | EC      | EC_code   | Ecuador@nail.com            | '123456789012        | companyName61  |
      | EC      | EC_code1  | Ecuador1@nail.com           | 12345678901234       | companyName62  |
      | EC      | EC_code2  | Ecuador2@nail.com           | 123456789012A        | companyName63  |
      | GT      | GT_code   | Guatemala@nail.com          | 123456A-1            | companyName64  |
      | GT      | GT_code1  | Guatemala1@nail.com         | 12345678-1           | companyName65  |
      | GT      | GT_code2  | Guatemala2@nail.com         | 1234567-11           | companyName66  |
      | GT      | GT_code3  | Guatemala3@nail.com         | 1234567-Z            | companyName67  |
      | MX      | MX_code   | Mexico@nail.com             | ABCD123456ABCD       | companyName68  |
      | MX      | MX_code1  | Mexico1@nail.com            | ABCD123Z56ABC        | companyName69  |
      | MX      | MX_code2  | Mexico2@nail.com            | ABCD123456AB         | companyName70  |
      | PE      | PE_code   | Peru@nail.com               | 1234567890           | companyName71  |
      | PE      | PE_code1  | Peru1@nail.com              | 1234567890A          | companyName72  |
      | PE      | PE_code2  | Peru2@nail.com              | 123456789012         | companyName73  |
      | DO      | DO_code   | DominicanRepublic@nail.com  | 12345678             | companyName74  |
      | DO      | DO_code1  | DominicanRepublic1@nail.com | 1234567890           | companyName75  |
      | DO      | DO_code2  | DominicanRepublic2@nail.com | 12345678Z            | companyName76  |
      | VE      | VE_code   | Venezuela@nail.com          | V-1234567890         | companyName77  |
      | VE      | VE_code1  | Venezuela1@nail.com         | Z23456789            | companyName78  |
      | AT      | AT_code   | Austria@mail.com            | ATU1234567           | companyName79  |
      | AT      | AT_code1  | Austria1@mail.com           | ATA12345678          | companyName80  |
      | AT      | AT_code2  | Austria2@mail.com           | AT12345678           | companyName81  |
      | AT      | AT_code3  | Austria3@mail.com           | ATU123456789         | companyName82  |
      | AT      | AT_code4  | Austria4@mail.com           | ATUABC45678          | companyName83  |
      | BE      | BE_code   | Belgium@mail.com            | BE1123456789         | companyName84  |
      | BE      | BE_code1  | Belgium1@mail.com           | BE0ABC456789         | companyName85  |
      | BE      | BE_code2  | Belgium2@mail.com           | BE012345678          | companyName86  |
      | BE      | BE_code3  | Belgium3@mail.com           | BE8123456789         | companyName861 |
      | BG      | BG_code   | Bulgaria@mail.com           | BG12345678           | companyName87  |
      | BG      | BG_code1  | Bulgaria1@mail.com          | BG1234567890         | companyName88  |
      | BG      | BG_code2  | Bulgaria2@mail.com          | BG123ABC789          | companyName89  |
      | CY      | CY_code   | Cyprus@mail.com             | CY1234567X           | companyName90  |
      | CY      | CY_code1  | Cyprus1@mail.com            | CY123456789X         | companyName91  |
      | CY      | CY_code2  | Cyprus2@mail.com            | CY123456789          | companyName92  |
      | CY      | CY_code3  | Cyprus3@mail.com            | CYABC45678X          | companyName93  |
      | CZ      | CZ_code   | CzechR@mail.com             | CZ12345678           | companyName931 |
      | CZ      | CZ_code1  | CzechR1@mail.com            | CZ12345678           | companyName932 |
      | CZ      | CZ_code2  | CzechR2@mail.com            | CZ12345678           | companyName933 |
      | DE      | DE_code   | Germany@mail.com            | DE12345678           | companyName94  |
      | DE      | DE_code1  | Germany1@mail.com           | DE1234567891         | companyName95  |
      | DE      | DE_code2  | Germany2@mail.com           | DEABC456789          | companyName96  |
      | DK      | DK_code   | Denmark@mail.com            | DK1234567            | companyName97  |
      | DK      | DK_code1  | Denmark1@mail.com           | DK123456789          | companyName98  |
      | DK      | DK_code2  | Denmark2@mail.com           | DK12  34  56  78     | companyName99  |
      | EE      | EE_code   | Estonia@mail.com            | EE12345678           | companyName100 |
      | EE      | EE_code1  | Estonia1@mail.com           | EE1234567891         | companyName101 |
      | EE      | EE_code2  | Estonia2@mail.com           | EEABC456789          | companyName102 |
      | ES      | ES_code   | Spain@mail.com              | ES123456789          | companyName103 |
      | ES      | ES_code1  | Spain1@mail.com             | ESX1234567           | companyName104 |
      | ES      | ES_code2  | Spain2@mail.com             | ES12345678           | companyName105 |
      | ES      | ES_code3  | Spain3@mail.com             | ESX12345678X         | companyName106 |
      | FI      | FI_code   | Finland@mail.com            | FI1234567            | companyName107 |
      | FI      | FI_code1  | Finland1@mail.com           | FI912345678          | companyName108 |
      | FI      | FI_code2  | Finland2@mail.com           | FIABC45678           | companyName109 |
      | FR      | FR_code   | France@mail.com             | FR123456789012       | companyName110 |
      | FR      | FR_code1  | France1@mail.com            | FR1234567890         | companyName111 |
      | FR      | FR_code2  | France2@mail.com            | FR1234567XXX1        | companyName112 |
      | GB      | GB_code   | Uk@mail.com                 | GB12 1234 12 123     | companyName113 |
      | GB      | GB_code1  | Uk1@mail.com                | GB123 123 12 123     | companyName114 |
      | GB      | GB_code2  | Uk2@mail.com                | GB123 1234 1 123     | companyName115 |
      | GB      | GB_code3  | Uk3@mail.com                | GB123 1234 12 12     | companyName116 |
      | GB      | GB_code4  | Uk4@mail.com                | GBHA59               | companyName117 |
      | GB      | GB_code5  | Uk5@mail.com                | GBH1599              | companyName118 |
      | GB      | GB_code6  | Uk6@mail.com                | GB1A599              | companyName119 |
      | GB      | GB_code7  | Uk7@mail.com                | GBGD01               | companyName120 |
      | GB      | GB_code8  | Uk8@mail.com                | GBG1001              | companyName121 |
      | GB      | GB_code9  | Uk9@mail.com                | GB1D001              | companyName122 |
      | GB      | GB_code10 | Uk10@mail.com               | GB12 4567 89         | companyName123 |
      | GB      | GB_code11 | Uk11@mail.com               | GB123 456 89         | companyName124 |
      | GB      | GB_code12 | Uk12@mail.com               | GB123 4567 8         | companyName125 |
      | GB      | GB_code13 | Uk13@mail.com               | GB12345678           | companyName126 |
      | GB      | GB_code14 | Uk14@mail.com               | GB1234567XX          | companyName127 |
      | HU      | HU_code   | Hungary@mail.com            | HU1234567            | companyName128 |
      | HU      | HU_code1  | Hungary1@mail.com           | HU912345678          | companyName129 |
      | HU      | HU_code2  | Hungary2@mail.com           | HUABC45678           | companyName130 |
      | IT      | IT_code   | Italy@mail.com              | IT1234567890         | companyName131 |
      | IT      | IT_code1  | Italy1@mail.com             | IT123456789012       | companyName132 |
      | IT      | IT_code2  | Italy2@mail.com             | IT123XXX78901        | companyName133 |
      | LT      | LT_code   | Lithuania@mail.com          | LT12345678           | companyName134 |
      | LT      | LT_code1  | Lithuania1@mail.com         | LT1234567890123      | companyName135 |
      | LT      | LT_code2  | Lithuania2@mail.com         | LT123XXX789          | companyName136 |
      | LT      | LT_code3  | Lithuania3@mail.com         | LT123456XXX012       | companyName137 |
      | LU      | LU_code   | Luxembourg@mail.com         | LU1234567            | companyName138 |
      | LU      | LU_code1  | Luxembourg1@mail.com        | LU912345678          | companyName139 |
      | LU      | LU_code2  | Luxembourg2@mail.com        | LUABC45678           | companyName140 |
      | LV      | LV_code   | Latvia@mail.com             | LV1234567890         | companyName141 |
      | LV      | LV_code1  | Latvia1@mail.com            | LV123456789012       | companyName142 |
      | LV      | LV_code2  | Latvia2@mail.com            | LV123XXX78901        | companyName143 |
      | MT      | MT_code   | Malta@mail.com              | MT1234567            | companyName144 |
      | MT      | MT_code1  | Malta1@mail.com             | MT912345678          | companyName145 |
      | MT      | MT_code2  | Malta2@mail.com             | MTABC45678           | companyName146 |
      | NL      | NL_code   | Netherlands@mail.com        | NL123456789B0        | companyName147 |
      | NL      | NL_code1  | Netherlands1@mail.com       | NL123456789B012      | companyName148 |
      | NL      | NL_code2  | Netherlands2@mail.com       | NL123456789A01       | companyName149 |
      | NL      | NL_code3  | Netherlands3@mail.com       | NL123456789101       | companyName150 |
      | PL      | PL_code   | Poland@mail.com             | PL123456789          | companyName151 |
      | PL      | PL_code1  | Poland1@mail.com            | PL12345678901        | companyName152 |
      | PL      | PL_code2  | Poland2@mail.com            | PL123XXX789          | companyName153 |
      | PT      | PT_code   | Portugal@mail.com           | PT12345678           | companyName154 |
      | PT      | PT_code1  | Portugal1@mail.com          | PT1234567891         | companyName155 |
      | PT      | PT_code   | Portugal2@mail.com          | PTABC456789          | companyName156 |
      | RO      | RO_code   | Romania@mail.com            | RO1                  | companyName157 |
      | RO      | RO_code1  | Romania1@mail.com           | RO12345678901        | companyName158 |
      | RO      | RO_code2  | Romania2@mail.com           | RO12X                | companyName159 |
      | SE      | SE_code   | Sweden@mail.com             | SE12345678901        | companyName160 |
      | SE      | SE_code1  | Sweden1@mail.com            | SE1234567890123      | companyName161 |
      | SE      | SE_code2  | Sweden2@mail.com            | SE1234XXX89012       | companyName162 |
      | SI      | SI_code   | Slovenia@mail.com           | SI1234567            | companyName163 |
      | SI      | SI_code1  | Slovenia1@mail.com          | SI912345678          | companyName164 |
      | SI      | SI_code2  | Slovenia2@mail.com          | SIABC45678           | companyName165 |
      | SK      | SK_code   | Slovakia@mail.com           | SK123456789          | companyName166 |
      | SK      | SK_code1  | Slovakia1@mail.com          | SK12345678901        | companyName167 |
      | SK      | SK_code2  | Slovakia2@mail.com          | SK123XXX789          | companyName168 |
      | HR      | HR_code   | Hungary@mail.com            | HR1234567890         | companyName169 |
      | HR      | HR_code1  | Hungary1@mail.com           | HR123456789012       | companyName170 |
      | HR      | HR_code2  | Hungary2@mail.com           | HR123XXX78901        | companyName171 |

  Scenario Outline: Validate that customer has valid VAT ID
    When A customer from country "<country>" code "<code>" email "<email>" vatId "<vatId>" company nqma "<companyName>"is created
    Then Content type is "application/json"
    And Response code is 201
    And Body contains entity with attribute "vat_id" value "<vatId>"

    Examples: 
      | country | code     | email                       | vatId                | companyName    |
      | AL      | AL_code  | albania@mail.com            | K71501003R           | companyName1   |
      | AL      | AL_code  | albania@mail.com            | K91811014B           | companyName2   |
      | AL      | AL_code2 | albania2@mail.com           | K41424801U           | companyName3   |
      | AU      | AU_code  | australia@mail.com          | 53004085616          | companyName4   |
      | BY      | BY_code  | belarus@mail.com            | 100581921            | companyName5   |
      | CA      | CA_code  | canada@mail.com             | 123456789BC0001      | companyName6   |
      | CA      | CA_code1 | canada1@mail.com            | 123456789            | companyName7   |
      | CA      | CA_code2 | canada2@mail.com            | 123456789 BC0001     | companyName8   |
      | CA      | CA_code3 | canada3@mail.com            | 123456789 BW0001     | companyName9   |
      | CA      | CA_code4 | canada4@mail.com            | 123456789 BB0001     | companyName10  |
      | IS      | IS_code  | iceland@mail.com            | 12ABCD               | companyName11  |
      | IS      | IS_code  | iceland@mail.com            | ABCDEF               | companyName12  |
      | IS      | IS_code  | iceland@mail.com            | 123456               | companyName13  |
      | IS      | IS_code  | iceland@mail.com            | 789BC1               | companyName14  |
      | IN      | IN_code  | india@mail.com              | 56345724553V         | companyName15  |
      | IN      | IN_code1 | india1@mail.com             | 56345724553C         | companyName16  |
      | ID      | ID_code  | indonesia@mail.com          | 99.999.999.9-999.999 | companyName17  |
      | ID      | ID_code1 | indonesia1@mail.com         | 02.271.824.1-413.000 | companyName18  |
      | MC      | MC_code  | Monaco@mail.com             | 99999999999          | companyName18  |
      | MC      | MC_code1 | Monaco1@mail.com            | X9999999999          | companyName19  |
      | MC      | MC_code  | Monaco@mail.com             | 9X999999999          | companyName20  |
      | MC      | MC_code1 | Monaco1@mail.com            | xx999999999          | companyName21  |
      | MC      | MC_code1 | Monaco1@mail.com            | XX999999999          | companyName22  |
      | NO      | NO_code  | Norway@mail.com             | 987654321MVA         | companyName23  |
      | NO      | NO_code1 | Norway1@mail.com            | 190190190MVA         | companyName24  |
      | PH      | PH_code  | Philippines@mail.com        | 123 123 123 123      | companyName25  |
      | RU      | RU_code  | Russia@mail.com             | 2190190190           | companyName26  |
      | RU      | RU_code1 | Russia1@mail.com            | 1234567890           | companyName27  |
      | SM      | SM_code  | SanMarino@mail.com          | 12345                | companyName28  |
      | SM      | SM_code1 | SanMarino1@mail.com         | 99999                | companyName29  |
      | RS      | RS_code  | Serbia@mail.com             | 190190190            | companyName30  |
      | RS      | RS_code1 | Serbia1@mail.com            | 123456789            | companyName31  |
      | CH      | CH_code  | Switzerland@nail.com        | CHE-123.456.789 TVA  | companyName32  |
      | CH      | CH_code0 | Switzerland0@nail.com       | 123456               | companyName33  |
      | CH      | CH_code1 | Switzerland1@nail.com       | CHE-999.999.999 MWST | companyName34  |
      | CH      | CH_code2 | Switzerland2@nail.com       | CHE-999.999.999 IVA  | companyName35  |
      | CH      | CH_code3 | Switzerland3@nail.com       | CHE-999.999.999 TVA  | companyName36  |
      | TR      | TR_code  | Turkey@nail.com             | 2190190190           | companyName37  |
      | TR      | TR_code1 | Turkey1@nail.com            | 1234567890           | companyName38  |
      | UA      | UA_code  | Ukraine@nail.com            | 2190190190           | companyName39  |
      | UA      | UA_code1 | Ukraine1@nail.com           | 1234567890           | companyName40  |
      | AR      | AR_code  | Argentina@nail.com          | 23190190190          | companyName41  |
      | AR      | AR_code1 | Argentina1@nail.com         | 12345678901          | companyName42  |
      | BR      | BR_code  | Brazil@nail.com             | 11.111.111/0001-55   | companyName43  |
      | BR      | BR_code1 | Brazil1@nail.com            | 12.123.123/1234-12   | companyName44  |
      | CL      | CL_code  | Chile@nail.com              | 12345678-1           | companyName45  |
      | CL      | CL_code2 | Chile2@nail.com             | 12345678-K           | companyName46  |
      | CL      | CL_code3 | Chile3@nail.com             | 1234567-1            | companyName47  |
      | CR      | CR_code  | CostaRica@nail.com          | 1234567890           | companyName48  |
      | CR      | CR_code1 | CostaRica1@nail.com         | 12345678901          | companyName49  |
      | CR      | CR_code2 | CostaRica2@nail.com         | 123456789012         | companyName50  |
      | EC      | EC_code  | Ecuador@nail.com            | 1234567890123        | companyName51  |
      | GT      | GT_code  | Guatemala@nail.com          | 1234567-1            | companyName52  |
      | MX      | MX_code  | Mexico@nail.com             | AAGB860519G31        | companyName53  |
      | MX      | MX_code1 | Mexico1@nail.com            | ABCD123456ABC        | companyName54  |
      | MX      | MX_code2 | Mexico2@nail.com            | ABC123456ABC         | companyName55  |
      | MX      | MX_code3 | Mexico3@nail.com            | ABCD123456123        | companyName56  |
      | PE      | PE_code  | Peru@nail.com               | 23190190190          | companyName57  |
      | PE      | PE_code1 | Peru1@nail.com              | 12345678901          | companyName58  |
      | DO      | DO_code  | DominicanRepublic@nail.com  | 190190190            | companyName59  |
      | DO      | DO_code1 | DominicanRepublic1@nail.com | 123456789            | companyName60  |
      | VE      | VE_code  | Venezuela@nail.com          | J-305959918          | companyName61  |
      | VE      | VE_code1 | Venezuela1@nail.com         | V-123456789          | companyName62  |
      | VE      | VE_code1 | Venezuela1@nail.com         | E-123456789          | companyName63  |
      | VE      | VE_code1 | Venezuela1@nail.com         | J-123456789          | companyName64  |
      | VE      | VE_code1 | Venezuela1@nail.com         | G-123456789          | companyName65  |
      | AT      | AT_code  | Austria@mail.com            | ATU12345678          | companyName66  |
      | AT      | AT_code1 | Austria1@mail.com           | ATU00000000          | companyName67  |
      | BE      | BE_code  | Belgium@mail.com            | BE1987654321         | companyName68  |
      | BE      | BE_code1 | Belgium1@mail.com           | BE0987654321         | companyName681 |
      | BG      | BG_code  | Bulgaria@mail.com           | BG123456789          | companyName69  |
      | BG      | BG_code1 | Bulgaria1@mail.com          | BG1234567890         | companyName70  |
      | CY      | CY_code  | Cyprus@mail.com             | CY12345678X          | companyName71  |
      | CZ      | CZ_code  | CzechR@mail.com             | CZ12345678           | companyName711 |
      | CZ      | CZ_code1 | CzechR1@mail.com            | CZ123456789          | companyName712 |
      | CZ      | CZ_code2 | CzechR2@mail.com            | CZ1234567890         | companyName713 |
      | DE      | DE_code  | Germany@mail.com            | DE123456789          | companyName72  |
      | DK      | DK_code  | Denmark@mail.com            | DK00000000           | companyName73  |
      | DK      | DK_code1 | Denmark1@mail.com           | DK12345678           | companyName74  |
      | EE      | EE_code  | Estonia@mail.com            | EE123456789          | companyName75  |
      | EL      | EL_code  | Greece@mail.com             | EL123456789          | companyName76  |
      | ES      | ES_code  | Spain@mail.com              | ESX12345678          | companyName77  |
      | ES      | ES_code1 | Spain1@mail.com             | ES12345678X          | companyName78  |
      | ES      | ES_code2 | Spain2@mail.com             | ESX1234567X          | companyName79  |
      | FI      | FI_code  | Finland@mail.com            | FI12345678           | companyName80  |
      | FR      | FR_code  | France@mail.com             | FR12345678901        | companyName81  |
      | FR      | FR_code1 | France1@mail.com            | FRX1234567890        | companyName82  |
      | FR      | FR_code2 | France2@mail.com            | FR1X123456789        | companyName83  |
      | FR      | FR_code3 | France3@mail.com            | FRXX123456789        | companyName84  |
      | FR      | FR_code4 | France4@mail.com            | FRXX 123456789       | companyName841 |
      | GB      | GB_code  | Uk@mail.com                 | GB123456789          | companyName85  |
      | GB      | GB_code1 | Uk1@mail.com                | GB123 4567 89        | companyName86  |
      | GB      | GB_code2 | Uk2@mail.com                | GBGD001              | companyName87  |
      | GB      | GB_code3 | Uk3@mail.com                | GBHA599              | companyName88  |
      | GB      | GB_code4 | Uk4@mail.com                | GB123 1234 12 123    | companyName89  |
      | HU      | HU_code  | Hungary@mail.com            | HU12345678           | companyName90  |
      | IE      | IE_code  | Ireland@mail.com            | IE1234567X           | companyName91  |
      | IE      | IE_code1 | Ireland1@mail.com           | IE1X23456X           | companyName92  |
      | IE      | IE_code2 | Ireland2@mail.com           | IE1+23456X           | companyName921 |
      | IE      | IE_code3 | Ireland3@mail.com           | IE1*23456X           | companyName921 |
      | IT      | IT_code  | Italy@mail.com              | IT12345678901        | companyName93  |
      | LT      | LT_code  | Lithuania@mail.com          | LT123456789          | companyName94  |
      | LT      | LT_code1 | Lithuania1@mail.com         | LT123456789012       | companyName95  |
      | LU      | LU_code  | Luxembourg@mail.com         | LU12345678           | companyName96  |
      | LV      | LV_code  | Latvia@mail.com             | LV12345678901        | companyName97  |
      | MT      | MT_code  | Malta@mail.com              | MT12345678           | companyName98  |
      | NL      | NL_code  | Netherlands@mail.com        | NL123456789B01       | companyName99  |
      | PL      | PL_code  | Poland@mail.com             | PL1234567890         | companyName100 |
      | PT      | PT_code  | Portugal@mail.com           | PT123456789          | companyName101 |
      | RO      | RO_code  | Romania@mail.com            | RO12                 | companyName102 |
      | RO      | RO_code1 | Romania1@mail.com           | RO123                | companyName103 |
      | RO      | RO_code2 | Romania2@mail.com           | RO1234               | companyName104 |
      | RO      | RO_code3 | Romania3@mail.com           | RO12345              | companyName105 |
      | RO      | RO_code4 | Romania4@mail.com           | RO123456             | companyName106 |
      | RO      | RO_code5 | Romania5@mail.com           | RO1234567            | companyName107 |
      | RO      | RO_code6 | Romania6@mail.com           | RO12345678           | companyName108 |
      | RO      | RO_code7 | Romania7@mail.com           | RO123456789          | companyName109 |
      | RO      | RO_code8 | Romania8@mail.com           | RO1234567890         | companyName110 |
      | SE      | SE_code  | Sweden@mail.com             | SE123456789012       | companyName111 |
      | SI      | SI_code  | Slovenia@mail.com           | SI12345678           | companyName112 |
      | SK      | SK_code  | Slovakia@mail.com           | SK1234567890         | companyName113 |
      | HR      | HR_code  | Croatia@mail.com            | HR12345678901        | companyName114 |
