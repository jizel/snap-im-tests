Feature: properties_get

  Background:
    Given The following properties exist with random address and billing address
      | salesforceId   | propertyName | propertyCode | website                    | email          | isDemoProperty | timezone  |
      | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | UTC+01:00 |


  Scenario: Getting property
    When Property with code "p1_code" exists
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present

    # existence
    And Body contains property with attribute "property_id"
    And Body contains property with attribute "address"

    # exact value
    And Body contains property with attribute "salesforce_id" value "salesforceid_1"
    And Body contains property with attribute "property_name" value "p1_name"
    And Body contains property with attribute "property_code" value "p1_code"
    And Body contains property with attribute "website" value "http://www.snapshot.travel"
    And Body contains property with attribute "email" value "p1@tenants.biz"
    And Body contains property with attribute "is_demo_property" value "java.lang.Boolean:true"
    And Body contains property with attribute "timezone" value "UTC+01:00"

    # non-existence
    And Body does not contain property with attribute "source_property"


  Scenario: Getting property with etag
    When Property with code "p1_code" exists with etag
    Then Response code is "304"
    And Body is empty


  Scenario: Getting property with expired etag
    #   1. property exists
    #   2. etag value is stored
    #   3. vat_id update changes etag
    #   4. previously stored (expired) tag is tested

    When Property with code "p1_code" exists for etag, forced new etag through update
    Then Response code is "200"
    And Content type is "application/json"
    And Etag header is present

    # exact value
    And Body contains property with attribute "property_code" value "p1_code"


  Scenario: Checking error code for nonexistent property
    When Nonexistent property id sent
    Then Response code is "404"
    And Custom code is "152"


  Scenario Outline: Getting list of properties
    Given The following properties exist with random address and billing address
      | salesforceId    | propertyName | propertyCode | website                    | email           | vatId      | isDemoProperty | timezone  |
      | salesforceid_01 | p01_name     | p01_code     | http://www.snapshot.travel | p01@tenants.biz | CZ10000001 | true           | UTC+01:00 |
      | salesforceid_02 | p02_name     | p02_code     | http://www.snapshot.travel | p02@tenants.biz | CZ10000002 | true           | UTC+01:00 |
      | salesforceid_03 | p03_name     | p03_code     | http://www.snapshot.travel | p03@tenants.biz | CZ10000003 | true           | UTC+01:00 |
      | salesforceid_04 | p04_name     | p04_code     | http://www.snapshot.travel | p04@tenants.biz | CZ10000004 | true           | UTC+01:00 |
      | salesforceid_05 | p05_name     | p05_code     | http://www.snapshot.travel | p05@tenants.biz | CZ10000005 | true           | UTC+01:00 |
      | salesforceid_06 | p06_name     | p06_code     | http://www.snapshot.travel | p06@tenants.biz | CZ10000006 | true           | UTC+01:00 |
      | salesforceid_07 | p07_name     | p07_code     | http://www.snapshot.travel | p07@tenants.biz | CZ10000007 | true           | UTC+01:00 |
      | salesforceid_08 | p08_name     | p08_code     | http://www.snapshot.travel | p08@tenants.biz | CZ10000008 | true           | UTC+01:00 |
      | salesforceid_09 | p09_name     | p09_code     | http://www.snapshot.travel | p09@tenants.biz | CZ10000009 | true           | UTC+01:00 |
      | salesforceid_10 | p10_name     | p10_code     | http://www.snapshot.travel | p10@tenants.biz | CZ10000010 | true           | UTC+01:00 |
      | salesforceid_11 | p11_name     | p11_code     | http://www.snapshot.travel | p11@tenants.biz | CZ10000011 | true           | UTC+01:00 |
      | salesforceid_12 | p12_name     | p12_code     | http://www.snapshot.travel | p12@tenants.biz | CZ10000012 | true           | UTC+01:00 |
      | salesforceid_13 | p13_name     | p13_code     | http://www.snapshot.travel | p13@tenants.biz | CZ10000013 | true           | UTC+01:00 |
      | salesforceid_14 | p14_name     | p14_code     | http://www.snapshot.travel | p14@tenants.biz | CZ10000014 | true           | UTC+01:00 |
      | salesforceid_15 | p15_name     | p15_code     | http://www.snapshot.travel | p15@tenants.biz | CZ10000015 | true           | UTC+01:00 |
      | salesforceid_16 | p16_name     | p16_code     | http://www.snapshot.travel | p16@tenants.biz | CZ10000016 | true           | UTC+01:00 |
      | salesforceid_17 | p17_name     | p17_code     | http://www.snapshot.travel | p17@tenants.biz | CZ10000017 | true           | UTC+01:00 |
      | salesforceid_18 | p18_name     | p18_code     | http://www.snapshot.travel | p18@tenants.biz | CZ10000018 | true           | UTC+01:00 |
      | salesforceid_19 | p19_name     | p19_code     | http://www.snapshot.travel | p19@tenants.biz | CZ10000019 | true           | UTC+01:00 |
      | salesforceid_20 | p20_name     | p20_code     | http://www.snapshot.travel | p20@tenants.biz | CZ10000020 | true           | UTC+01:00 |
      | salesforceid_21 | p21_name     | p21_code     | http://www.snapshot.travel | p21@tenants.biz | CZ10000021 | true           | UTC+01:00 |
      | salesforceid_22 | p22_name     | p22_code     | http://www.snapshot.travel | p22@tenants.biz | CZ10000022 | true           | UTC+01:00 |
      | salesforceid_23 | p23_name     | p23_code     | http://www.snapshot.travel | p23@tenants.biz | CZ10000023 | true           | UTC+01:00 |
      | salesforceid_24 | p24_name     | p24_code     | http://www.snapshot.travel | p24@tenants.biz | CZ10000024 | true           | UTC+01:00 |
      | salesforceid_25 | p25_name     | p25_code     | http://www.snapshot.travel | p25@tenants.biz | CZ10000025 | true           | UTC+01:00 |
      | salesforceid_26 | p26_name     | p26_code     | http://www.snapshot.travel | p26@tenants.biz | CZ10000026 | true           | UTC+01:00 |
      | salesforceid_27 | p27_name     | p27_code     | http://www.snapshot.travel | p27@tenants.biz | CZ10000027 | true           | UTC+01:00 |
      | salesforceid_28 | p28_name     | p28_code     | http://www.snapshot.travel | p28@tenants.biz | CZ10000028 | true           | UTC+01:00 |
      | salesforceid_29 | p29_name     | p29_code     | http://www.snapshot.travel | p29@tenants.biz | CZ10000029 | true           | UTC+01:00 |
      | salesforceid_30 | p30_name     | p30_code     | http://www.snapshot.travel | p30@tenants.biz | CZ10000030 | true           | UTC+01:00 |
      | salesforceid_31 | p31_name     | p31_code     | http://www.snapshot.travel | p31@tenants.biz | CZ10000031 | true           | UTC+01:00 |
      | salesforceid_32 | p32_name     | p32_code     | http://www.snapshot.travel | p32@tenants.biz | CZ10000032 | true           | UTC+01:00 |
      | salesforceid_33 | p33_name     | p33_code     | http://www.snapshot.travel | p33@tenants.biz | CZ10000033 | true           | UTC+01:00 |
      | salesforceid_34 | p34_name     | p34_code     | http://www.snapshot.travel | p34@tenants.biz | CZ10000034 | true           | UTC+01:00 |
      | salesforceid_35 | p35_name     | p35_code     | http://www.snapshot.travel | p35@tenants.biz | CZ10000035 | true           | UTC+01:00 |
      | salesforceid_36 | p36_name     | p36_code     | http://www.snapshot.travel | p36@tenants.biz | CZ10000036 | true           | UTC+01:00 |
      | salesforceid_37 | p37_name     | p37_code     | http://www.snapshot.travel | p37@tenants.biz | CZ10000037 | true           | UTC+01:00 |
      | salesforceid_38 | p38_name     | p38_code     | http://www.snapshot.travel | p38@tenants.biz | CZ10000038 | true           | UTC+01:00 |
      | salesforceid_39 | p39_name     | p39_code     | http://www.snapshot.travel | p39@tenants.biz | CZ10000039 | true           | UTC+01:00 |
      | salesforceid_40 | p40_name     | p40_code     | http://www.snapshot.travel | p40@tenants.biz | CZ10000040 | true           | UTC+01:00 |
      | salesforceid_41 | p41_name     | p41_code     | http://www.snapshot.travel | p41@tenants.biz | CZ10000041 | true           | UTC+01:00 |
      | salesforceid_42 | p42_name     | p42_code     | http://www.snapshot.travel | p42@tenants.biz | CZ10000042 | true           | UTC+01:00 |
      | salesforceid_43 | p43_name     | p43_code     | http://www.snapshot.travel | p43@tenants.biz | CZ10000043 | true           | UTC+01:00 |
      | salesforceid_44 | p44_name     | p44_code     | http://www.snapshot.travel | p44@tenants.biz | CZ10000044 | true           | UTC+01:00 |
      | salesforceid_45 | p45_name     | p45_code     | http://www.snapshot.travel | p45@tenants.biz | CZ10000045 | true           | UTC+01:00 |
      | salesforceid_46 | p46_name     | p46_code     | http://www.snapshot.travel | p46@tenants.biz | CZ10000046 | true           | UTC+01:00 |
      | salesforceid_47 | p47_name     | p47_code     | http://www.snapshot.travel | p47@tenants.biz | CZ10000047 | true           | UTC+01:00 |
      | salesforceid_48 | p48_name     | p48_code     | http://www.snapshot.travel | p48@tenants.biz | CZ10000048 | true           | UTC+01:00 |
      | salesforceid_49 | p49_name     | p49_code     | http://www.snapshot.travel | p49@tenants.biz | CZ10000049 | true           | UTC+01:00 |
      | salesforceid_50 | p50_name     | p50_code     | http://www.snapshot.travel | p50@tenants.biz | CZ10000050 | true           | UTC+01:00 |
      | salesforceid_51 | p51_name     | p51_code     | http://www.snapshot.travel | p51@tenants.biz | CZ10000051 | true           | UTC+01:00 |
      | salesforceid_52 | p52_name     | p52_code     | http://www.snapshot.travel | p52@tenants.biz | CZ10000052 | true           | UTC+01:00 |
      | salesforceid_53 | p53_name     | p53_code     | http://www.snapshot.travel | p53@tenants.biz | CZ10000053 | true           | UTC+01:00 |
      | salesforceid_54 | p54_name     | p54_code     | http://www.snapshot.travel | p54@tenants.biz | CZ10000054 | true           | UTC+01:00 |
      | salesforceid_55 | p55_name     | p55_code     | http://www.snapshot.travel | p55@tenants.biz | CZ10000055 | true           | UTC+01:00 |
      | salesforceid_56 | p56_name     | p56_code     | http://www.snapshot.travel | p56@tenants.biz | CZ10000056 | true           | UTC+01:00 |
      | salesforceid_57 | p57_name     | p57_code     | http://www.snapshot.travel | p57@tenants.biz | CZ10000057 | true           | UTC+01:00 |
      | salesforceid_58 | p58_name     | p58_code     | http://www.snapshot.travel | p58@tenants.biz | CZ10000058 | true           | UTC+01:00 |
      | salesforceid_59 | p59_name     | p59_code     | http://www.snapshot.travel | p59@tenants.biz | CZ10000059 | true           | UTC+01:00 |

    When List of properties exists with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "200"
    And Content type is "application/json"
    And There are <returned> properties returned

    Examples:
      | description   | limit | cursor | returned |
      | default limit |       |        | 50       |
      | limit at 15   | 15    |        | 15       |
      | offset by 1   |       | 1      | 50       |
      | limit by 20   | 20    | 0      | 20       |
      | limit by 10   | 10    | 0      | 10       |
      | l:5 o:5       | 5     | 5      | 5        |


  Scenario Outline: Checking error codes for lists of properties
    When List of properties exists with limit "<limit>" and cursor "<cursor>" and filter empty and sort empty
    Then Response code is "<response_code>"
    And Custom code is "<custom_code>"

    Examples:
      | description           | limit | cursor | response_code | custom_code |
      | negative cursor       |       | -1     | 400           | 63          |
      | cursor NaN            |       | text   | 400           | 63          |
      | negative limit        | -1    |        | 400           | 63          |
      | limit NaN             | text  |        | 400           | 63          |
      | limit ok, neg. cursor | 10    | -1     | 400           | 63          |
      | limit neg., cursor ok | -1    | 10     | 400           | 63          |
      | limit NaN, curson ok  | text  | 0      | 400           | 63          |
      | limit ok, cursor NaN  | 10    | text   | 400           | 63          |

  # negative values, strings, empty
  # wrong parameters (variables: parameter name, parameter value),