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
      | country | code     | email                       | vatId                | companyName    |
      | AL      | AL_code  | albania@mail.com            | K414A4801U           | companyName1   |
      | AL      | AL_code  | albania@mail.com            | K414A4801U           | companyName12  |
      | AL      | AL_code2 | albania2@mail.com           | K41424801            | companyName13  |
      | AL      | AL_code3 | albania3@mail.com           | K4142481U            | companyName14  |
      | AU      | AU_code  | australia@mail.com          | 5634572455           | companyName2   |
      | AU      | AU_code1 | australia1@mail.com         | 5634572455z          | companyName21  |
      | AU      | AU_code2 | australia2@mail.com         | 5634572455Z          | companyName22  |
      | AU      | AU_code3 | australia3@mail.com         | 563457245533         | companyName23  |
      | AU      | AU_code4 | australia4@mail.com         | 53 004 085 616       | companyName24  |
      | BY      | BY_code  | belarus@mail.com            | 10058192             | companyName3   |
      | BY      | BY_code2 | belarus1@mail.com           | 10058192A            | companyName31  |
      | BY      | BY_code3 | belarus2@mail.com           | 1005819211           | companyName32  |
      | BY      | BY_code4 | belarus3@mail.com           | A10058192            | companyName33  |
      | CA      | CA_code  | canada@mail.com             | CZ12345678           | companyName4   |
      | IS      | IS_code  | iceland@mail.com            | 1234567              | companyName5   |
      | IN      | IN_code  | india@mail.com              | 5634572455V          | companyName6   |
      | IN      | IN_code1 | india1@mail.com             | 563V5724553V         | companyName61  |
      | ID      | ID_code  | indonesia@mail.com          | 99.999.999.9-999.99  | companyName7   |
      | ID      | ID_code1 | indonesia1@mail.com         | 99.999.999.9-99.999  | companyName71  |
      | ID      | ID_code2 | indonesia2@mail.com         | 99.999.99.9-999.999  | companyName72  |
      | ID      | ID_code3 | indonesia3@mail.com         | 99.99.999.9-999.9999 | companyName73  |
      | ID      | ID_code4 | indonesia4@mail.com         | 99.999.9A9.9-999.999 | companyName74  |
      | MC      | MC_code  | Monaco@mail.com             | 9999999999X          | companyName8   |
      | MC      | MC_code1 | Monaco1@mail.com            | 9999999999           | companyName81  |
      | NO      | NO_code  | Norway@mail.com             | 87654321MVA          | companyName9   |
      | NO      | NO_code1 | Norway1@mail.com            | 987654321            | companyName91  |
      | NO      | NO_code2 | Norway2@mail.com            | 987654321XYZ         | companyName92  |
      | NO      | NO_code3 | Norway3@mail.com            | 9AB654321MVA         | companyName93  |
      | PH      | PH_code  | Philippines@mail.com        | 999.999.999.999N     | companyName11  |
      | PH      | PH_code1 | Philippines1@mail.com       | 999.999.999.999V     | companyName111 |
      | RU      | RU_code  | Russia@mail.com             | 123456789            | companyName12  |
      | RU      | RU_code1 | Russia1@mail.com            | A234567890           | companyName121 |
      | RU      | RU_code2 | Russia2@mail.com            | 12345678901          | companyName122 |
      | SM      | SM_code  | SanMarino@mail.com          | 1ABC25               | companyName13  |
      | SM      | SM_code1 | SanMarino1@mail.com         | 123456               | companyName131 |
      | SM      | SM_code2 | SanMarino2@mail.com         | 1234                 | companyName132 |
      | RS      | RS_code  | Serbia@mail.com             | 1234567890           | companyName14  |
      | RS      | RS_code1 | Serbia1@mail.com            | 12345678             | companyName141 |
      | RS      | RS_code2 | Serbia2@mail.com            | 123456A89            | companyName142 |
      | CH      | CH_code  | Switzerland@nail.com        | CHE-999.999.999      | companyName15  |
      | CH      | CH_code1 | Switzerland1@nail.com       | CHE-999.999.999MWST  | companyName151 |
      | CH      | CH_code2 | Switzerland2@nail.com       | CHE-999.ABC.999 MWST | companyName152 |
      | CH      | CH_code3 | Switzerland3@nail.com       | CHE999.999.999MWST   | companyName153 |
      | CH      | CH_code4 | Switzerland4@nail.com       | CHE-999999999 MWST   | companyName154 |
      | TR      | TR_code  | Turkey@nail.com             | 123456789            | companyName16  |
      | TR      | TR_code1 | Turkey1@nail.com            | 12345678901          | companyName161 |
      | TR      | TR_code2 | Turkey2@nail.com            | 123A567890           | companyName162 |
      | UA      | UA_code  | Ukraine@nail.com            | 123456789            | companyName17  |
      | UA      | UA_code1 | Ukraine1@nail.com           | 12345678901          | companyName171 |
      | UA      | UA_code2 | Ukraine2@nail.com           | 123A567890           | companyName172 |
      | AR      | AR_code  | Argentina@nail.com          | 123456789012         | companyName18  |
      | AR      | AR_code1 | Argentina1@nail.com         | 1234567890           | companyName181 |
      | AR      | AR_code2 | Argentina2@nail.com         | 123456789A1          | companyName182 |
      | BR      | BR_code  | Brazil@nail.com             | 12.123.123/1234-123  | companyName19  |
      | BR      | BR_code1 | Brazil1@nail.com            | 12.123.123 1234-12   | companyName191 |
      | BR      | BR_code2 | Brazil2@nail.com            | 123.123.123/1234-12  | companyName192 |
      | CL      | CL_code  | Chile@nail.com              | 12345678-12          | companyName21  |
      | CL      | CL_code2 | Chile2@nail.com             | 123456789-1          | companyName212 |
      | CL      | CL_code3 | Chile3@nail.com             | 12345A78-1           | companyName213 |
      | CR      | CR_code  | CostaRica@nail.com          | CZ12345678           | companyName22  |
      | EC      | EC_code  | Ecuador@nail.com            | '123456789012        | companyName23  |
      | EC      | EC_code1 | Ecuador1@nail.com           | 12345678901234       | companyName231 |
      | EC      | EC_code2 | Ecuador2@nail.com           | 123456789012A        | companyName232 |
      | GT      | GT_code  | Guatemala@nail.com          | 123456A-1            | companyName24  |
      | GT      | GT_code1 | Guatemala1@nail.com         | 12345678-1           | companyName241 |
      | GT      | GT_code2 | Guatemala2@nail.com         | 1234567-11           | companyName242 |
      | GT      | GT_code3 | Guatemala3@nail.com         | 1234567-Z            | companyName243 |
      | MX      | MX_code  | Mexico@nail.com             | ABCD123456ABCD       | companyName25  |
      | MX      | MX_code1 | Mexico1@nail.com            | ABCD123Z56ABC        | companyName251 |
      | MX      | MX_code2 | Mexico2@nail.com            | ABCD123456AB         | companyName252 |
      | PE      | PE_code  | Peru@nail.com               | 1234567890           | companyName26  |
      | PE      | PE_code1 | Peru1@nail.com              | 1234567890A          | companyName261 |
      | PE      | PE_code2 | Peru2@nail.com              | 123456789012         | companyName262 |
      | DO      | DO_code  | DominicanRepublic@nail.com  | 12345678             | companyName27  |
      | DO      | DO_code1 | DominicanRepublic1@nail.com | 1234567890           | companyName271 |
      | DO      | DO_code2 | DominicanRepublic2@nail.com | 12345678Z            | companyName272 |
      | VE      | VE_code  | Venezuela@nail.com          | V-1234567890         | companyName28  |
      | VE      | VE_code1 | Venezuela1@nail.com         | Z23456789            | companyName281 |

  Scenario Outline: Validate that customer has valid VAT ID
    When A customer from country "<country>" code "<code>" email "<email>" vatId "<vatId>" company nqma "<companyName>"is created
    Then Content type is "application/json"
    And Response code is 201
    And Body contains entity with attribute "vat_id" value "<vatId>"

    Examples: 
      | country | code     | email                       | vatId                | companyName    |
      | AL      | AL_code  | albania@mail.com            | K71501003R           | companyName1   |
      | AL      | AL_code  | albania@mail.com            | K91811014B           | companyName12  |
      | AL      | AL_code2 | albania2@mail.com           | K41424801U           | companyName13  |
      | AU      | AU_code  | australia@mail.com          | 53004085616          | companyName2   |
      | BY      | BY_code  | belarus@mail.com            | 100581921            | companyName3   |
      | CA      | CA_code  | canada@mail.com             | 123456789BC0001      | companyName4   |
      | CA      | CA_code1 | canada1@mail.com            | 123456789            | companyName41  |
      | CA      | CA_code2 | canada2@mail.com            | 123456789 BC0001     | companyName42  |
      | CA      | CA_code3 | canada3@mail.com            | 123456789 BW0001     | companyName43  |
      | CA      | CA_code4 | canada4@mail.com            | 123456789 BB0001     | companyName44  |
      | IS      | IS_code  | iceland@mail.com            | 12ABCD               | companyName5   |
      | IS      | IS_code  | iceland@mail.com            | ABCDEF               | companyName5   |
      | IS      | IS_code  | iceland@mail.com            | 123456               | companyName5   |
      | IS      | IS_code  | iceland@mail.com            | 789BC1               | companyName5   |
      | IN      | IN_code  | india@mail.com              | 56345724553V         | companyName6   |
      | IN      | IN_code1 | india1@mail.com             | 56345724553C         | companyName61  |
      | ID      | ID_code  | indonesia@mail.com          | 99.999.999.9-999.999 | companyName7   |
      | ID      | ID_code1 | indonesia1@mail.com         | 02.271.824.1-413.000 | companyName71  |
      | MC      | MC_code  | Monaco@mail.com             | 99999999999          | companyName8   |
      | MC      | MC_code1 | Monaco1@mail.com            | X9999999999          | companyName81  |
      | MC      | MC_code  | Monaco@mail.com             | 9X999999999          | companyName8   |
      | MC      | MC_code1 | Monaco1@mail.com            | xx999999999          | companyName81  |
      | MC      | MC_code1 | Monaco1@mail.com            | XX999999999          | companyName81  |
      | NO      | NO_code  | Norway@mail.com             | 987654321MVA         | companyName9   |
      | NO      | NO_code1 | Norway1@mail.com            | 190190190MVA         | companyName91  |
      | PH      | PH_code  | Philippines@mail.com        | 123 123 123 123      | companyName11  |
      | RU      | RU_code  | Russia@mail.com             | 2190190190           | companyName12  |
      | RU      | RU_code1 | Russia1@mail.com            | 1234567890           | companyName121 |
      | SM      | SM_code  | SanMarino@mail.com          | 12345                | companyName13  |
      | SM      | SM_code1 | SanMarino1@mail.com         | 99999                | companyName131 |
      | RS      | RS_code  | Serbia@mail.com             | 190190190            | companyName14  |
      | RS      | RS_code1 | Serbia1@mail.com            | 123456789            | companyName141 |
      | CH      | CH_code  | Switzerland@nail.com        | CHE-123.456.789 TVA  | companyName15  |
      | CH      | CH_code0 | Switzerland0@nail.com       | 123456               | companyName150 |
      | CH      | CH_code1 | Switzerland1@nail.com       | CHE-999.999.999 MWST | companyName151 |
      | CH      | CH_code2 | Switzerland2@nail.com       | CHE-999.999.999 IVA  | companyName152 |
      | CH      | CH_code3 | Switzerland3@nail.com       | CHE-999.999.999 TVA  | companyName153 |
      | TR      | TR_code  | Turkey@nail.com             | 2190190190           | companyName16  |
      | TR      | TR_code1 | Turkey1@nail.com            | 1234567890           | companyName161 |
      | UA      | UA_code  | Ukraine@nail.com            | 2190190190           | companyName17  |
      | UA      | UA_code1 | Ukraine1@nail.com           | 1234567890           | companyName171 |
      | AR      | AR_code  | Argentina@nail.com          | 23190190190          | companyName18  |
      | AR      | AR_code1 | Argentina1@nail.com         | 12345678901          | companyName181 |
      | BR      | BR_code  | Brazil@nail.com             | 11.111.111/0001-55   | companyName19  |
      | BR      | BR_code1 | Brazil1@nail.com            | 12.123.123/1234-12   | companyName191 |
      | CL      | CL_code  | Chile@nail.com              | 12345678-1           | companyName21  |
      | CL      | CL_code2 | Chile2@nail.com             | 12345678-K           | companyName212 |
      | CL      | CL_code3 | Chile3@nail.com             | 1234567-1            | companyName213 |
      | CR      | CR_code  | CostaRica@nail.com          | 1234567890           | companyName22  |
      | CR      | CR_code1 | CostaRica1@nail.com         | 12345678901          | companyName221 |
      | CR      | CR_code2 | CostaRica2@nail.com         | 123456789012         | companyName222 |
      | EC      | EC_code  | Ecuador@nail.com            | 1234567890123        | companyName23  |
      | GT      | GT_code  | Guatemala@nail.com          | 1234567-1            | companyName24  |
      | MX      | MX_code  | Mexico@nail.com             | AAGB860519G31        | companyName25  |
      | MX      | MX_code1 | Mexico1@nail.com            | ABCD123456ABC        | companyName251 |
      | MX      | MX_code2 | Mexico2@nail.com            | ABC123456ABC         | companyName252 |
      | MX      | MX_code3 | Mexico3@nail.com            | ABCD123456123        | companyName253 |
      | PE      | PE_code  | Peru@nail.com               | 23190190190          | companyName26  |
      | PE      | PE_code1 | Peru1@nail.com              | 12345678901          | companyName261 |
      | DO      | DO_code  | DominicanRepublic@nail.com  | 190190190            | companyName27  |
      | DO      | DO_code1 | DominicanRepublic1@nail.com | 123456789            | companyName271 |
      | VE      | VE_code  | Venezuela@nail.com          | J-305959918          | companyName28  |
      | VE      | VE_code1 | Venezuela1@nail.com         | V-123456789          | companyName281 |
      | VE      | VE_code1 | Venezuela1@nail.com         | E-123456789          | companyName281 |
      | VE      | VE_code1 | Venezuela1@nail.com         | J-123456789          | companyName281 |
      | VE      | VE_code1 | Venezuela1@nail.com         | G-123456789          | companyName281 |
