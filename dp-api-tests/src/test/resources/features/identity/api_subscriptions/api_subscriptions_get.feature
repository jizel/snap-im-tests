@Identity
Feature: Api subscription get

  Background:
    Given Database is cleaned and default entities are created



    Given The following application versions exists
      | versionId                            | apiManagerId | versionName | status   | description            | applicationId                        |
      | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 1   | inactive | Versions description 1 | 11111111-0000-4000-a000-111111111111 |
      | c595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 2   | inactive | Versions description 2 | 11111111-0000-4000-a000-111111111111 |
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1238fd9a-a05d-42d8-8e84-42e904ace123 | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 5d829079-48f0-4f00-9bec-e2329a8bdaac | customer | default1 | Default1  | User1    | def1@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address for user "5d829079-48f0-4f00-9bec-e2329a8bdaac"
      | propertyId                           | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 742529dd-481f-430d-b6b6-686fbb687cab | salesforceid_1 | p1_name      | p1_code      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1238fd9a-a05d-42d8-8e84-42e904ace123 |
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 8e238f8e-2c9c-4e32-9a63-40474a9728eb | 1238fd9a-a05d-42d8-8e84-42e904ace123 | 742529dd-481f-430d-b6b6-686fbb687cab | 11111111-0000-4000-a000-111111111111 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | commercialSubscriptionId             |
      | 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | b595fc9d-f5ca-45e7-a15d-c8a97108d884 | 8e238f8e-2c9c-4e32-9a63-40474a9728eb |


  @Smoke
  Scenario: Getting api subscription
    When Api subscription with id "5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f" is got
    Then Response code is 200
    And Content type is "application/json"
    And Etag header is present
    And Body contains entity with attribute "api_subscription_id" value "5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f"
    And Body contains entity with attribute "application_version_id" value "b595fc9d-f5ca-45e7-a15d-c8a97108d884"
    And Body contains entity with attribute "commercial_subscription_id" value "8e238f8e-2c9c-4e32-9a63-40474a9728eb"

  Scenario: Checking error code for getting nonExisting api subscription
    When Api subscription with id "nonExistingApi" is got
    Then Response code is 404
    And Content type is "application/json"
    And Custom code is 40402

  Scenario Outline: Getting list of api subscriptions with valid query parameters
    Given The following application versions exists
      | versionId                            | apiManagerId | versionName    | status   | description             | applicationId                        |
      | a595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 1 List | inactive | Versions description 1  | 11111111-0000-4000-a000-111111111111 |
      | ab343111-12d3-4fde-ba8a-5ddff45d06d4 | 2            | Version 2 List | inactive | Versions description 2  | 11111111-0000-4000-a000-111111111111 |
      | 1820d135-4f75-4c64-a570-a89e8236229b | 3            | Version 3 List | inactive | Versions description 3  | 11111111-0000-4000-a000-111111111111 |
      | ac39d04c-bb58-4955-a7ab-c216e5444a54 | 4            | Version 4 List | inactive | Versions description 4  | 11111111-0000-4000-a000-111111111111 |
      | 315404f9-3ac8-4b75-8b54-1ea15702d046 | 5            | Version 5 List | inactive | Versions description 5  | 11111111-0000-4000-a000-111111111111 |
      | f29ce9d8-e4b9-4d99-9531-e1e1513ab4e5 | 6            | Version 6 List | inactive | Versions description 6  | 11111111-0000-4000-a000-111111111111 |
      | 2e215979-49df-488c-8cea-a4abeb4c0265 | 7            | Version 7 List | inactive | Versions description 7  | 11111111-0000-4000-a000-111111111111 |
      | 0b29cc24-d2bc-49e3-9944-c58532f00534 | 8            | Version 8 List | inactive | Versions description 8  | 11111111-0000-4000-a000-111111111111 |
      | 75d47daa-21fa-431d-a2b9-b490bba5250d | 9            | Version 9 List | inactive | Versions description 9  | 11111111-0000-4000-a000-111111111111 |
      | 4e701c5f-aa7e-4a68-9f96-967550c2442c | 10           | Version 10List | inactive | Versions description 10 | 11111111-0000-4000-a000-111111111111 |
      | 5ca16080-f21d-4d35-b257-b18f0d7b2a14 | 11           | Version 11List | inactive | Versions description 11 | 11111111-0000-4000-a000-111111111111 |
      | 49dad233-3199-4cf2-827a-0efbb6899acd | 12           | Version 12List | inactive | Versions description 12 | 11111111-0000-4000-a000-111111111111 |
      | 9701fcc1-5d31-4f1f-80d3-5c2cee3bbaed | 13           | Version 13List | inactive | Versions description 13 | 11111111-0000-4000-a000-111111111111 |
      | 6c591036-2c6c-4362-a465-39515466163c | 14           | Version 14List | inactive | Versions description 14 | 11111111-0000-4000-a000-111111111111 |
      | 3b02cb4e-1cca-4d70-8bd6-c7efbc817493 | 15           | Version 15List | inactive | Versions description 15 | 11111111-0000-4000-a000-111111111111 |
      | a4e30390-8e6c-404f-b719-e344dfad4519 | 16           | Version 16List | inactive | Versions description 16 | 11111111-0000-4000-a000-111111111111 |
      | 2579aeb0-95bb-4789-98ac-99659b5b77b0 | 17           | Version 17List | inactive | Versions description 17 | 11111111-0000-4000-a000-111111111111 |
      | bcc3efa9-fd93-4c9d-add7-67545971d1b9 | 18           | Version 18List | inactive | Versions description 18 | 11111111-0000-4000-a000-111111111111 |
      | bc4bed55-de3d-471e-a1e8-fce6f6e029aa | 19           | Version 19List | inactive | Versions description 19 | 11111111-0000-4000-a000-111111111111 |
      | bf7b9b34-14de-4987-82c7-6251aa43a188 | 20           | Version 20List | inactive | Versions description 20 | 11111111-0000-4000-a000-111111111111 |
    Given The following customers exist with random address
      | customerId                           | companyName      | email           | salesforceId          | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | Given company 1  | c1@tenants.biz  | salesforceid_given_1  | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 5e904539-68fa-4c54-a3ff-4b3fd1a9b683 | Given company 2  | c2@tenants.biz  | salesforceid_given_2  | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 83f16fc2-9e87-435c-9632-1aa8c08a7501 | Given company 3  | c3@tenants.biz  | salesforceid_given_3  | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 59f91161-ae55-4369-b963-0db1410b00d7 | Given company 4  | c4@tenants.biz  | salesforceid_given_4  | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | b7306a7d-851c-4f59-856d-907eff0aa9b7 | Given company 5  | c5@tenants.biz  | salesforceid_given_5  | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | ddea7528-fefa-4a93-8b4d-de6b27db3e30 | Given company 6  | c6@tenants.biz  | salesforceid_given_6  | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 65bf60db-9da1-419b-88b7-2b232872c786 | Given company 7  | c7@tenants.biz  | salesforceid_given_7  | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 2c441da4-0669-425f-aa00-62aa1375fb43 | Given company 8  | c8@tenants.biz  | salesforceid_given_8  | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | c2e72a45-36a3-4973-be6d-32134e4801fc | Given company 9  | c9@tenants.biz  | salesforceid_given_9  | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | b377a0fb-76c6-48ef-b594-b3fe53575927 | Given company 10 | c10@tenants.biz | salesforceid_given_10 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 16d55fcb-b2a1-4209-9fc3-103dedb5ed47 | Given company 11 | c11@tenants.biz | salesforceid_given_11 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | eff32916-1ed9-420e-abdf-92ac2c8a732b | Given company 12 | c12@tenants.biz | salesforceid_given_12 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | b053ddd4-12dd-469c-ad2e-0b6765075367 | Given company 13 | c13@tenants.biz | salesforceid_given_13 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 873d6310-90cb-452b-8627-28c00e3fbbe8 | Given company 14 | c14@tenants.biz | salesforceid_given_14 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 1b825c33-17f1-43bc-b3ab-22549137a0dd | Given company 15 | c15@tenants.biz | salesforceid_given_15 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 3822afa6-5b94-4294-951a-24fd05963e08 | Given company 16 | c16@tenants.biz | salesforceid_given_16 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | c5eb2185-b558-45a1-bb51-8f1bce3fa0a6 | Given company 17 | c17@tenants.biz | salesforceid_given_17 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 96b3106a-82cc-4fa0-a35c-5e9e32194304 | Given company 18 | c18@tenants.biz | salesforceid_given_18 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | faf0b7b5-5f1e-48e9-9ded-529e59baec6c | Given company 19 | c19@tenants.biz | salesforceid_given_19 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | e0ddefaf-05d4-4330-ae67-1310af0d0b45 | Given company 20 | c20@tenants.biz | salesforceid_given_20 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 6d829079-48f0-4f00-9bec-e2329a8bdaac | customer | default2 | Default2  | User2    | def2@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address
      | propertyId                           | salesforceId    | name         | propertyCode | website                    | email           | isDemoProperty | timezone      | anchorCustomerId                     |
      | 23fe2b58-de46-4330-b361-482f07286cce | salesforceid_1  | p1_list      | p1_list      | http://www.snapshot.travel | p1@tenants.biz  | true           | Europe/Prague | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 5e334ae2-c3a1-486e-b7e7-dd3209656188 | salesforceid_2  | p2_list      | p2_list      | http://www.snapshot.travel | p2@tenants.biz  | true           | Europe/Prague | 5e904539-68fa-4c54-a3ff-4b3fd1a9b683 |
      | 4701c194-e39d-46fd-b70f-44dd15a82278 | salesforceid_3  | p3_list      | p3_list      | http://www.snapshot.travel | p3@tenants.biz  | true           | Europe/Prague | 83f16fc2-9e87-435c-9632-1aa8c08a7501 |
      | b9e2b6cc-4453-4935-a8c5-8763ae7873d7 | salesforceid_4  | p4_list      | p4_list      | http://www.snapshot.travel | p4@tenants.biz  | true           | Europe/Prague | 59f91161-ae55-4369-b963-0db1410b00d7 |
      | 0567c8c6-523d-4c2b-9941-eb4bd7b61a7d | salesforceid_5  | p5_list      | p5_list      | http://www.snapshot.travel | p5@tenants.biz  | true           | Europe/Prague | b7306a7d-851c-4f59-856d-907eff0aa9b7 |
      | ac1e411c-1c68-4178-9871-62b4728c6c9d | salesforceid_6  | p6_list      | p6_list      | http://www.snapshot.travel | p6@tenants.biz  | true           | Europe/Prague | ddea7528-fefa-4a93-8b4d-de6b27db3e30 |
      | a5fb70c4-4be6-4eb4-bb54-87e56d86e751 | salesforceid_7  | p7_list      | p7_list      | http://www.snapshot.travel | p7@tenants.biz  | true           | Europe/Prague | 65bf60db-9da1-419b-88b7-2b232872c786 |
      | d320aaad-d465-4be5-97f5-41254bf19038 | salesforceid_8  | p8_list      | p8_list      | http://www.snapshot.travel | p8@tenants.biz  | true           | Europe/Prague | 2c441da4-0669-425f-aa00-62aa1375fb43 |
      | 4faa9765-c16c-4a63-9df9-8c60cb5303cf | salesforceid_9  | p9_list      | p9_list      | http://www.snapshot.travel | p9@tenants.biz  | true           | Europe/Prague | c2e72a45-36a3-4973-be6d-32134e4801fc |
      | ad51c66f-330b-4201-8374-92f138a7629d | salesforceid_10 | p10_list     | p10_list     | http://www.snapshot.travel | p10@tenants.biz | true           | Europe/Prague | b377a0fb-76c6-48ef-b594-b3fe53575927 |
      | 24284ba4-6d29-4a5a-9187-363e168fac9a | salesforceid_11 | p11_list     | p11_list     | http://www.snapshot.travel | p11@tenants.biz | true           | Europe/Prague | 16d55fcb-b2a1-4209-9fc3-103dedb5ed47 |
      | 3ad7fec4-e484-497d-bcc4-ebe587a2d836 | salesforceid_12 | p12_list     | p12_list     | http://www.snapshot.travel | p12@tenants.biz | true           | Europe/Prague | eff32916-1ed9-420e-abdf-92ac2c8a732b |
      | f7c51321-1dde-4d98-a999-35a3b6379196 | salesforceid_13 | p13_list     | p13_list     | http://www.snapshot.travel | p13@tenants.biz | true           | Europe/Prague | b053ddd4-12dd-469c-ad2e-0b6765075367 |
      | 62bcf845-fc26-46fb-9480-7e539c10dbbc | salesforceid_14 | p14_list     | p14_list     | http://www.snapshot.travel | p14@tenants.biz | true           | Europe/Prague | 873d6310-90cb-452b-8627-28c00e3fbbe8 |
      | ad7cfddb-ecf3-401d-a93e-d68bc4b4e3da | salesforceid_15 | p15_list     | p15_list     | http://www.snapshot.travel | p15@tenants.biz | true           | Europe/Prague | 1b825c33-17f1-43bc-b3ab-22549137a0dd |
      | 6d455567-b952-4503-97de-9c608b6a8c28 | salesforceid_16 | p16_list     | p16_list     | http://www.snapshot.travel | p16@tenants.biz | true           | Europe/Prague | 3822afa6-5b94-4294-951a-24fd05963e08 |
      | 691a104c-47f4-4dab-9c22-040c00b10c03 | salesforceid_17 | p17_list     | p17_list     | http://www.snapshot.travel | p17@tenants.biz | true           | Europe/Prague | c5eb2185-b558-45a1-bb51-8f1bce3fa0a6 |
      | 202ed76e-e3c4-4359-9461-b4ccb7cb9a20 | salesforceid_18 | p18_list     | p18_list     | http://www.snapshot.travel | p18@tenants.biz | true           | Europe/Prague | 96b3106a-82cc-4fa0-a35c-5e9e32194304 |
      | 98b50129-72f8-4b55-af15-9507db2cca95 | salesforceid_19 | p19_list     | p19_list     | http://www.snapshot.travel | p19@tenants.biz | true           | Europe/Prague | faf0b7b5-5f1e-48e9-9ded-529e59baec6c |
      | d94a9119-6936-466c-8cfd-693acebd39ac | salesforceid_20 | p20_list     | p20_list     | http://www.snapshot.travel | p20@tenants.biz | true           | Europe/Prague | e0ddefaf-05d4-4330-ae67-1310af0d0b45 |
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 83443d01-aa91-4e3d-bcd4-ede0c86e1875 | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | 23fe2b58-de46-4330-b361-482f07286cce | 11111111-0000-4000-a000-111111111111 |
      | 76245d74-f596-4bcc-9e1a-4d3298fd0ae5 | 5e904539-68fa-4c54-a3ff-4b3fd1a9b683 | 5e334ae2-c3a1-486e-b7e7-dd3209656188 | 11111111-0000-4000-a000-111111111111 |
      | 309a2a7f-c400-4f39-b203-54869feebd5b | 83f16fc2-9e87-435c-9632-1aa8c08a7501 | 4701c194-e39d-46fd-b70f-44dd15a82278 | 11111111-0000-4000-a000-111111111111 |
      | 00c7dece-b9a9-4bbc-a445-304723bc887e | 59f91161-ae55-4369-b963-0db1410b00d7 | b9e2b6cc-4453-4935-a8c5-8763ae7873d7 | 11111111-0000-4000-a000-111111111111 |
      | 02db47f4-0726-400f-89bd-e2fd6d8c8226 | b7306a7d-851c-4f59-856d-907eff0aa9b7 | 0567c8c6-523d-4c2b-9941-eb4bd7b61a7d | 11111111-0000-4000-a000-111111111111 |
      | 7cf28741-d8cd-44ec-b757-d3e52fc77139 | ddea7528-fefa-4a93-8b4d-de6b27db3e30 | ac1e411c-1c68-4178-9871-62b4728c6c9d | 11111111-0000-4000-a000-111111111111 |
      | cdfcd25e-60d6-4a1c-bcfa-ebfece98fac5 | 65bf60db-9da1-419b-88b7-2b232872c786 | a5fb70c4-4be6-4eb4-bb54-87e56d86e751 | 11111111-0000-4000-a000-111111111111 |
      | 5d32c412-5bba-4779-90a9-4aca694750da | 2c441da4-0669-425f-aa00-62aa1375fb43 | d320aaad-d465-4be5-97f5-41254bf19038 | 11111111-0000-4000-a000-111111111111 |
      | 976d87bd-0ca6-459a-9e95-deb71cc2d6c5 | c2e72a45-36a3-4973-be6d-32134e4801fc | 4faa9765-c16c-4a63-9df9-8c60cb5303cf | 11111111-0000-4000-a000-111111111111 |
      | c4944f0a-bfe0-47f7-a154-ce68c61cb47a | b377a0fb-76c6-48ef-b594-b3fe53575927 | ad51c66f-330b-4201-8374-92f138a7629d | 11111111-0000-4000-a000-111111111111 |
      | 17c8557e-855c-489a-aea3-56e0ca5fb52d | 16d55fcb-b2a1-4209-9fc3-103dedb5ed47 | 24284ba4-6d29-4a5a-9187-363e168fac9a | 11111111-0000-4000-a000-111111111111 |
      | ee158cc0-56cd-4f5d-8f6e-5ea9096a1de8 | eff32916-1ed9-420e-abdf-92ac2c8a732b | 3ad7fec4-e484-497d-bcc4-ebe587a2d836 | 11111111-0000-4000-a000-111111111111 |
      | 06ece0dd-e109-435b-b843-94d4b5a37e2e | b053ddd4-12dd-469c-ad2e-0b6765075367 | f7c51321-1dde-4d98-a999-35a3b6379196 | 11111111-0000-4000-a000-111111111111 |
      | 14b6de03-4749-44b9-90bd-233e7478ceb7 | 873d6310-90cb-452b-8627-28c00e3fbbe8 | 62bcf845-fc26-46fb-9480-7e539c10dbbc | 11111111-0000-4000-a000-111111111111 |
      | 4986841e-df90-42e3-a964-be7a8bb9fddc | 1b825c33-17f1-43bc-b3ab-22549137a0dd | ad7cfddb-ecf3-401d-a93e-d68bc4b4e3da | 11111111-0000-4000-a000-111111111111 |
      | 496ed083-917d-47ec-811d-394c59410f7b | 3822afa6-5b94-4294-951a-24fd05963e08 | 6d455567-b952-4503-97de-9c608b6a8c28 | 11111111-0000-4000-a000-111111111111 |
      | 05b188b8-2646-4b85-88e4-87c84d03ba2f | c5eb2185-b558-45a1-bb51-8f1bce3fa0a6 | 691a104c-47f4-4dab-9c22-040c00b10c03 | 11111111-0000-4000-a000-111111111111 |
      | 13e77a71-9f3e-403b-aa18-14b93fb101a7 | 96b3106a-82cc-4fa0-a35c-5e9e32194304 | 202ed76e-e3c4-4359-9461-b4ccb7cb9a20 | 11111111-0000-4000-a000-111111111111 |
      | 2e8a2b25-c7d7-46ba-a161-1b4dfcb2189c | faf0b7b5-5f1e-48e9-9ded-529e59baec6c | 98b50129-72f8-4b55-af15-9507db2cca95 | 11111111-0000-4000-a000-111111111111 |
      | 5f1c295b-d75f-48db-9203-c377b45702e6 | e0ddefaf-05d4-4330-ae67-1310af0d0b45 | d94a9119-6936-466c-8cfd-693acebd39ac | 11111111-0000-4000-a000-111111111111 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | commercialSubscriptionId             |
      | 1c6f61ff-810c-43da-96e2-ff6c8c9b8b2f | a595fc9d-f5ca-45e7-a15d-c8a97108d884 | 83443d01-aa91-4e3d-bcd4-ede0c86e1875 |
      | 598cfe31-9583-4c0b-86a0-82e9bf662849 | ab343111-12d3-4fde-ba8a-5ddff45d06d4 | 76245d74-f596-4bcc-9e1a-4d3298fd0ae5 |
      | 87eb138c-23c1-43d8-bc88-d6fbbd7e4359 | 1820d135-4f75-4c64-a570-a89e8236229b | 309a2a7f-c400-4f39-b203-54869feebd5b |
      | e2c258a3-a3e0-4895-82ee-5170934c5555 | ac39d04c-bb58-4955-a7ab-c216e5444a54 | 00c7dece-b9a9-4bbc-a445-304723bc887e |
      | c432f76f-8fdd-4229-b8eb-e9007024b385 | 315404f9-3ac8-4b75-8b54-1ea15702d046 | 02db47f4-0726-400f-89bd-e2fd6d8c8226 |
      | 512da950-3ff0-43b9-a521-064fe4d060e7 | f29ce9d8-e4b9-4d99-9531-e1e1513ab4e5 | 7cf28741-d8cd-44ec-b757-d3e52fc77139 |
      | 541539e8-4e6f-40fd-b0cf-260d4c1801f6 | 2e215979-49df-488c-8cea-a4abeb4c0265 | cdfcd25e-60d6-4a1c-bcfa-ebfece98fac5 |
      | 4c3a0311-d27d-452d-af04-2b61a4c61202 | 0b29cc24-d2bc-49e3-9944-c58532f00534 | 5d32c412-5bba-4779-90a9-4aca694750da |
      | 9332ec8b-cb57-4781-822b-60e1250d7d27 | 75d47daa-21fa-431d-a2b9-b490bba5250d | 976d87bd-0ca6-459a-9e95-deb71cc2d6c5 |
      | 82f66099-83f1-4008-8e3d-b34b60fb1089 | 4e701c5f-aa7e-4a68-9f96-967550c2442c | c4944f0a-bfe0-47f7-a154-ce68c61cb47a |
      | 6b4800ca-e149-42f0-8aa0-cffbe0a1bff8 | 5ca16080-f21d-4d35-b257-b18f0d7b2a14 | 17c8557e-855c-489a-aea3-56e0ca5fb52d |
      | 1303a49c-c150-4af2-880a-52c0fdbc3534 | 49dad233-3199-4cf2-827a-0efbb6899acd | ee158cc0-56cd-4f5d-8f6e-5ea9096a1de8 |
      | f8ef0353-7ec7-493e-b5d4-7773ac3c11cf | 9701fcc1-5d31-4f1f-80d3-5c2cee3bbaed | 06ece0dd-e109-435b-b843-94d4b5a37e2e |
      | 6c3309ab-3213-4f23-a72d-dbc6a4fc88c6 | 6c591036-2c6c-4362-a465-39515466163c | 14b6de03-4749-44b9-90bd-233e7478ceb7 |
      | 18a1d3a5-36ce-4c12-aba3-37ad25b0ca13 | 3b02cb4e-1cca-4d70-8bd6-c7efbc817493 | 4986841e-df90-42e3-a964-be7a8bb9fddc |
      | 25a6873f-578e-422c-8f50-0833c204a36f | a4e30390-8e6c-404f-b719-e344dfad4519 | 496ed083-917d-47ec-811d-394c59410f7b |
      | c92b6ca0-095f-485b-9452-d9c2f9f0b833 | 2579aeb0-95bb-4789-98ac-99659b5b77b0 | 05b188b8-2646-4b85-88e4-87c84d03ba2f |
      | f7adde17-9e7a-4484-850d-406fc833d0a2 | bcc3efa9-fd93-4c9d-add7-67545971d1b9 | 13e77a71-9f3e-403b-aa18-14b93fb101a7 |
      | f7f46e2c-2204-4b4b-939f-251f3fcb6e8f | bc4bed55-de3d-471e-a1e8-fce6f6e029aa | 2e8a2b25-c7d7-46ba-a161-1b4dfcb2189c |
      | b0edd868-e1f4-429e-b912-297e22c95bc5 | bf7b9b34-14de-4987-82c7-6251aa43a188 | 5f1c295b-d75f-48db-9203-c377b45702e6 |

    When List of api subscriptions is got with limit "<limit>" and cursor "<cursor>" and filter "/null" and sort "/null" and sort_desc "/null"
    Then Response code is 200
    And Content type is "application/json"
    And There are "<returned>" api subscriptions returned
    And Total count is "21"

    Examples:
      | limit | cursor | returned | note                                                                           |
      | /null | /null  | 21       | # all api subscriptions should be returned                                     |
      | 10    | /null  | 10       | # limit parameter should be user, 10 subscriptions should be returned          |
      |       |        | 21       | # empty parameter are ignored, all api subscriptions should be returned        |
      |       | /null  | 21       | # empty limit parameter, all api subscriptions should be returned              |
      | /null |        | 21       | # empty cursor parameter, all api subscriptions should be returned             |
      | 10    | 1      | 10       | # cursor < limit, limit param is used, 10 api subscriptions should be returned |
      | 1     | 10     | 1        | # cursor > limit, limit param is used, 1 api subscriptions should be returned  |
      | /null | 0      | 21       | # cursor can be 0 (?), all api subscriptions should be returned                |

  Scenario Outline: Getting list of api subscriptions with invalid query parameters
    When List of api subscriptions is got with limit "<limit>" and cursor "<cursor>" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is 400
    And Custom code is <customCode>

    Examples:
      | limit | cursor | filter           | sort        | sort_desc   | customCode | note                                                                                                                                                                                                        |
      | -1    | /null  | /null            | /null       | /null       | 40002      | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
      | text  | /null  | /null            | /null       | /null       | 40002      | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
      | 9999  | /null  | /null            | /null       | /null       | 40002      | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
      | 0     | /null  | /null            | /null       | /null       |40002         | # The value is invalid. The limit should be more than 0 and less than or equal to 200.                                                                                                                      |
      | /null | -1     | /null            | /null       | /null       | 40002      | # The value is invalid. The cursor should be a positive number.                                                                                                                                             |
      | /null | text   | /null            | /null       | /null       | 40002      | # The value is invalid. The cursor should be a positive number.                                                                                                                                             |
      | /null | /null  | -1               | /null       | /null       | 40002      | # The value is invalid. Param 'arg0' The query parameter 'filter' has invalid FIQL syntax; provided filter=-1                                                                                               |
      | /null | /null  | ==               | /null       | /null       | 40002      | # The value is invalid. Param 'arg0' The query parameter 'filter' has invalid FIQL syntax; provided filter===                                                                                               |
      | /null | /null  | code==           | /null       | /null       | 40002      | # The value is invalid. Param 'arg0' The query parameter 'filter' has invalid FIQL syntax; provided filter=code==                                                                                           |
      | /null | /null  | is_active==      | /null       | /null       | 40002      | # The value is invalid. Param 'arg0' The query parameter 'filter' has invalid FIQL syntax; provided filter=is_active==                                                                                      |
#  DP-1782
      | /null | /null  | is_active=='NOT' | /null       | /null       | 40002      | # The value is invalid. Cannot cast 'NOT' to type boolean                                                                                                                                                   |
      | /null | /null  | /null            | -1          | /null       | 40002      | # The value is invalid. Param 'sort' must match \"[a-zA-Z0-9_]*\"                                                                                                                                           |
      | /null | /null  | /null            | 0           | /null       | 40002      | # The value is invalid. Param 'sort' must match \"[a-zA-Z0-9_]*\"                                                                                                                                           |
      | /null | /null  | /null            | nonExistent | /null       | 40002      | # The value is invalid. Param 'arg0' The query parameter 'sort' has illegal value; allowed values: [application_version_id, is_active, api_subscription_id, api_version], provided value: 'nonExistent'"    |
      | /null | /null  | /null            | /null       | -1          | 40002      | # The value is invalid. Param 'arg0' The query parameter 'sort_desc' has illegal value; allowed values: [application_version_id, is_active, api_subscription_id, api_version], provided value: '-1          |
      | /null | /null  | /null            | /null       | 0           | 40002      | # The value is invalid. Param 'arg0' The query parameter 'sort_desc' has illegal value; allowed values: [application_version_id, is_active, api_subscription_id, api_version], provided value: '0           |
      | /null | /null  | /null            | /null       | nonExistent | 40002      | # The value is invalid. Param 'arg0' The query parameter 'sort_desc' has illegal value; allowed values: [application_version_id, is_active, api_subscription_id, api_version], provided value: 'nonExistent |
      | /null | /null  | /null            | is_active   | is_active   | 40002      | # The query parameter 'sort_desc' is not allowed. 'sort' and 'sort_desc' parameters shouldn't be both in one request!                                                                                       |


  Scenario Outline: Filtering list of api subscriptions
    Given The following application versions exists
      | versionId                            | apiManagerId | versionName        | status   | description            | applicationId                        |
      | a595fc9d-f5ca-45e7-a15d-c8a97108d884 | 1            | Version 1filtering | inactive | Versions description 1 | 11111111-0000-4000-a000-111111111111 |
      | ab343111-12d3-4fde-ba8a-5ddff45d06d4 | 2            | Version 2filtering | inactive | Versions description 2 | 11111111-0000-4000-a000-111111111111 |
      | 1820d135-4f75-4c64-a570-a89e8236229b | 3            | Version 3filtering | inactive | Versions description 3 | 11111111-0000-4000-a000-111111111111 |
      | bc39d04c-bb58-4955-a7ab-c216e5444a54 | 4            | Version 4filtering | inactive | Versions description 4 | 11111111-0000-4000-a000-111111111111 |
      | 315404f9-3ac8-4b75-8b54-1ea15702d046 | 5            | Version 5filtering | inactive | Versions description 5 | 11111111-0000-4000-a000-111111111111 |
    Given The following customers exist with random address
      | customerId                           | companyName     | email          | salesforceId         | vatId      | isDemoCustomer | phone         | website                    | timezone      |
      | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | Given company 1 | c1@tenants.biz | salesforceid_given_1 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 5e904539-68fa-4c54-a3ff-4b3fd1a9b683 | Given company 2 | c2@tenants.biz | salesforceid_given_2 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 83f16fc2-9e87-435c-9632-1aa8c08a7501 | Given company 3 | c3@tenants.biz | salesforceid_given_3 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | 59f91161-ae55-4369-b963-0db1410b00d7 | Given company 4 | c4@tenants.biz | salesforceid_given_4 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
      | b7306a7d-851c-4f59-856d-907eff0aa9b7 | Given company 5 | c5@tenants.biz | salesforceid_given_5 | CZ10000001 | true           | +420123456789 | http://www.snapshot.travel | Europe/Prague |
    Given The following users exist for customer "1238fd9a-a05d-42d8-8e84-42e904ace123" as primary "false"
      | userId                               | userType | userName | firstName | lastName | email                | timezone      | culture |
      | 7d829079-48f0-4f00-9bec-e2329a8bdaac | customer | default3 | Default3  | User3    | def3@snapshot.travel | Europe/Prague | cs-CZ   |
    Given The following properties exist with random address and billing address
      | propertyId                           | salesforceId   | name         | propertyCode | website                    | email          | isDemoProperty | timezone      | anchorCustomerId                     |
      | 23fe2b58-de46-4330-b361-482f07286cce | salesforceid_1 | p1_list      | p1_list      | http://www.snapshot.travel | p1@tenants.biz | true           | Europe/Prague | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a |
      | 5e334ae2-c3a1-486e-b7e7-dd3209656188 | salesforceid_2 | p2_list      | p2_list      | http://www.snapshot.travel | p2@tenants.biz | true           | Europe/Prague | 5e904539-68fa-4c54-a3ff-4b3fd1a9b683 |
      | 4701c194-e39d-46fd-b70f-44dd15a82278 | salesforceid_3 | p3_list      | p3_list      | http://www.snapshot.travel | p3@tenants.biz | true           | Europe/Prague | 83f16fc2-9e87-435c-9632-1aa8c08a7501 |
      | b9e2b6cc-4453-4935-a8c5-8763ae7873d7 | salesforceid_4 | p4_list      | p4_list      | http://www.snapshot.travel | p4@tenants.biz | true           | Europe/Prague | 59f91161-ae55-4369-b963-0db1410b00d7 |
      | 0567c8c6-523d-4c2b-9941-eb4bd7b61a7d | salesforceid_5 | p5_list      | p5_list      | http://www.snapshot.travel | p5@tenants.biz | true           | Europe/Prague | b7306a7d-851c-4f59-856d-907eff0aa9b7 |
    Given The following commercial subscriptions exist
      | commercialSubscriptionId             | customerId                           | propertyId                           | applicationId                        |
      | 83443d01-aa91-4e3d-bcd4-ede0c86e1875 | 1e1aaece-b75b-41bd-80d4-9d5c0c7ff13a | 23fe2b58-de46-4330-b361-482f07286cce | 11111111-0000-4000-a000-111111111111 |
      | 76245d74-f596-4bcc-9e1a-4d3298fd0ae5 | 5e904539-68fa-4c54-a3ff-4b3fd1a9b683 | 5e334ae2-c3a1-486e-b7e7-dd3209656188 | 11111111-0000-4000-a000-111111111111 |
      | 309a2a7f-c400-4f39-b203-54869feebd5b | 83f16fc2-9e87-435c-9632-1aa8c08a7501 | 4701c194-e39d-46fd-b70f-44dd15a82278 | 11111111-0000-4000-a000-111111111111 |
      | 00c7dece-b9a9-4bbc-a445-304723bc887e | 59f91161-ae55-4369-b963-0db1410b00d7 | b9e2b6cc-4453-4935-a8c5-8763ae7873d7 | 11111111-0000-4000-a000-111111111111 |
      | 02db47f4-0726-400f-89bd-e2fd6d8c8226 | b7306a7d-851c-4f59-856d-907eff0aa9b7 | 0567c8c6-523d-4c2b-9941-eb4bd7b61a7d | 11111111-0000-4000-a000-111111111111 |
    Given The following api subscriptions exist
      | apiSubscriptionId                    | applicationVersionId                 | commercialSubscriptionId             |
      | 17eb138c-23c1-43d8-bc88-d6fbbd7e4359 | a595fc9d-f5ca-45e7-a15d-c8a97108d884 | 83443d01-aa91-4e3d-bcd4-ede0c86e1875 |
      | 27eb138c-23c1-43d8-bc88-d6fbbd7e4359 | ab343111-12d3-4fde-ba8a-5ddff45d06d4 | 76245d74-f596-4bcc-9e1a-4d3298fd0ae5 |
      | 37eb138c-23c1-43d8-bc88-d6fbbd7e4359 | 1820d135-4f75-4c64-a570-a89e8236229b | 309a2a7f-c400-4f39-b203-54869feebd5b |
      | 47eb138c-23c1-43d8-bc88-d6fbbd7e4359 | bc39d04c-bb58-4955-a7ab-c216e5444a54 | 00c7dece-b9a9-4bbc-a445-304723bc887e |
      | a7eb138c-23c1-43d8-bc88-d6fbbd7e4359 | 315404f9-3ac8-4b75-8b54-1ea15702d046 | 02db47f4-0726-400f-89bd-e2fd6d8c8226 |

    When List of api subscriptions is got with limit "/null" and cursor "/null" and filter "<filter>" and sort "<sort>" and sort_desc "<sort_desc>"
    Then Response code is 200
    And Content type is "application/json"
    And There are "<returned>" api subscriptions returned
    And There are api subscriptions with following IDs returned in order: "<api_subscription_id_order>"
  #application_version_id, is_active, commercial_subscription_id, api_subscription_id]
    Examples:
      | filter                                                         | sort                       | sort_desc                  | returned | api_subscription_id_order                                                                                                                                                                                                          |
      | api_subscription_id=='1*'                                      | /null                      | /null                      | 1        |                                                                                                                                                                                                                                    |
      | api_subscription_id=='*359'                                    | /null                      | /null                      | 5        |                                                                                                                                                                                                                                    |
      | api_subscription_id=='*43d8*'                                  | /null                      | /null                      | 5        |                                                                                                                                                                                                                                    |
#  DP-1788
      | api_subscription_id=='*359'                                    | application_version_id     | /null                      | 5        | 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359, 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359                                       |
      | api_subscription_id=='*359'                                    | is_active                  | /null                      | 5        | 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359                                       |
      | api_subscription_id=='*359'                                    | commercial_subscription_id | /null                      | 5        | 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 17eb138c-23c1-43d8-bc88-d6fbbd7e4359                                       |
      | api_subscription_id=='*359'                                    | api_subscription_id        | /null                      | 5        | 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359                                       |
      | api_subscription_id=='*359'                                    | /null                      | application_version_id     | 5        | 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359                                       |
      | api_subscription_id=='*359'                                    | /null                      | is_active                  | 5        | 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359                                       |
      | api_subscription_id=='*359'                                    | /null                      | commercial_subscription_id | 5        | 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359                                       |
      | api_subscription_id=='*359'                                    | /null                      | api_subscription_id        | 5        | a7eb138c-23c1-43d8-bc88-d6fbbd7e4359, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 17eb138c-23c1-43d8-bc88-d6fbbd7e4359                                       |
      | api_subscription_id=='1*' or api_subscription_id=='2*'         | /null                      | /null                      | 2        |                                                                                                                                                                                                                                    |
      | api_subscription_id=='*43d8*' and application_version_id=='a*' | /null                      | /null                      | 2        |                                                                                                                                                                                                                                    |
      | application_version_id=='NonExisting'                          | /null                      | /null                      | 0        |                                                                                                                                                                                                                                    |
      | application_version_id=='b*'                                   | /null                      | /null                      | 2        |                                                                                                                                                                                                                                    |
      | is_active=='true'                                              | /null                      | /null                      | 0        |                                                                                                                                                                                                                                    |
      | is_active=='true'                                              | /null                      | /null                      | 0        |                                                                                                                                                                                                                                    |
      | commercial_subscription_id=='1'                                | /null                      | /null                      | 0        |                                                                                                                                                                                                                                    |
      | api_subscription_id=='*bc88*'                                  | /null                      | /null                      | 5        | 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359                                       |
      | /null                                                          | application_version_id     | /null                      | 6        | 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359, 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359 |
      | /null                                                          | is_active                  | /null                      | 6        | 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359 |
      | /null                                                          | commercial_subscription_id | /null                      | 6        | 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f |
      | /null                                                          | api_subscription_id        | /null                      | 6        | 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359 |
      | /null                                                          | /null                      | application_version_id     | 6        | 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359 |
      | /null                                                          | /null                      | is_active                  | 6        | 17eb138c-23c1-43d8-bc88-d6fbbd7e4359, 27eb138c-23c1-43d8-bc88-d6fbbd7e4359, 37eb138c-23c1-43d8-bc88-d6fbbd7e4359, 47eb138c-23c1-43d8-bc88-d6fbbd7e4359, 5c6f61ff-810c-43da-96e2-ff6c8c9b8b2f, a7eb138c-23c1-43d8-bc88-d6fbbd7e4359 |
      | /null                                                          | /null                      | commercial_subscription_id | 6        |                                                                                                                                                                                                                                    |
      | /null                                                          | /null                      | api_subscription_id        | 6        |                                                                                                                                                                                                                                    |
      |                                                                |                            |                            | 6        |                                                                                                                                                                                                                                    |
