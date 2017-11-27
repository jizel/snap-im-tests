package travel.snapshot.dp.qa.junit.tests.identity.properties;

import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.OWNER;
import static travel.snapshot.dp.api.identity.model.UserUpdateDto.UserType;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.helpers.AddressUtils.createRandomAddress;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.PROPERTY_CODE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.VALID_FROM_VALUE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.VALID_TO_VALUE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerContains;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.tests.Tags.SLOW_TEST;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(SLOW_TEST)
class ParametersPropertiesTests extends CommonTest {

    private static final String EXAMPLES = "/csv/properties/";


    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "correctPropertyCodeIsReturnedWhenNoneSent.csv")
    void correctPropertyCodeIsReturnedWhenNoneSent(String name) throws IOException {
        testProperty1.setName(name);
        testProperty1.setId(null);
        testProperty1.setCode(null);
        entityIsCreated(testProperty1);
        String propertyCode = getAttributeValue(PROPERTY_CODE);
        assertFalse("Property code is empty", propertyCode.isEmpty());
        assertFalse("Property code contains whitespaces", propertyCode.matches("\\s"));
        assertTrue("Property code contains invalid characters", propertyCode.matches("[A-Z0-9]+"));
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "correctPropertyCodeIsReturnedAccordingToCustomersAddress.csv")
    void correctPropertyCodeIsReturnedAccordingToCustomersAddress(
            String name,
            String line1,
            String city,
            String zipCode,
            String countryCode,
            String regionCode,
            String resultingPropertyCode
    ) throws IOException {
        AddressDto address = new AddressDto();
        address.setCountryCode(countryCode);
        address.setZipCode(zipCode);
        address.setCity(city);
        address.setLine1(line1);
        address.setRegionCode(transformNull(regionCode));
        testProperty1.setName(name);
        testProperty1.setId(null);
        testProperty1.setCode(null);
        testProperty1.setAddress(address);
        entityIsCreated(testProperty1);
        String propertyCode = getAttributeValue(PROPERTY_CODE);
        assertThat("Passed and returned property code mismatch", resultingPropertyCode, is(propertyCode));
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "propertyCodeCanBeFilledManually.csv")
    void propertyCodeCanBeFilledManually(String code) throws IOException {
        testProperty1.setId(null);
        testProperty1.setCode(code);
        entityIsCreated(testProperty1);
        String propertyCode = getAttributeValue(PROPERTY_CODE);
        assertThat("Passed and returned property code mismatch", propertyCode, is(code));
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "filteringListOfCustomerProperties.csv")
    void filteringListOfCustomerProperties(
            String limit,
            String cursor,
            String total,
            String returned,
            String filter) {
        List<UUID> customerIds = new ArrayList<>();
        range(0, 4).forEachOrdered(n -> {
            testCustomer1.setId(null);
            testCustomer1.setName(String.format("Some_customer_%d", n));
            testCustomer1.setEmail(String.format("customer_%d@snapshot.travel", n));
            UUID customerId = entityIsCreated(testCustomer1);
            customerIds.add(customerId);
            CustomerPropertyRelationshipCreateDto relation = constructCustomerPropertyRelationshipDto(customerId, DEFAULT_PROPERTY_ID, true, OWNER,
                            LocalDate.parse(VALID_FROM_VALUE),
                            LocalDate.parse(VALID_TO_VALUE));
            entityIsCreated(relation);
        });
        filter = (filter==null)? null : String.format(filter, customerIds.get(0).toString());
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, null, null, null);
        getEntities(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, params);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(CustomerPropertyRelationshipDto.class, Integer.valueOf(returned));
        headerIs(TOTAL_COUNT_HEADER, total);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "validateThatPropertyRegionsBelongToTheCorrectCountry.csv")
    void validateThatPropertyRegionsBelongToTheCorrectCountry(String country, String region) {
        AddressDto address = testProperty1.getAddress();
        address.setCountryCode(country);
        address.setRegionCode(region);
        testProperty1.setAddress(address);
        entityIsCreated(testProperty1);
        bodyContainsEntityWith("address.region", region);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "checkingErrorCodesForRegions.csv")
    void checkingErrorCodesForRegions(String country, String region) {
        AddressDto address = testProperty1.getAddress();
        address.setCountryCode(country);
        address.setRegionCode(region);
        testProperty1.setAddress(address);
        createEntity(PROPERTIES_PATH, testProperty1)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS));
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "gettingListOfProperties.csv")
    void gettingListOfProperties(
            String limit,
            String cursor,
            String filter,
            String sort,
            String returned,
            String total,
            String linkHeader
    ) {
        range(0, 59).forEachOrdered(n -> {
            testProperty1.setName(String.format("prop_name_%d", n));
            testProperty1.setId(null);
            testProperty1.setCode(null);
            entityIsCreated(testProperty1);
        });
        // The following is needed to test customer filtering/sorting by address.country DPIM-116
        AddressDto address = createRandomAddress(5, 5, 6, "DE", null);
        testProperty1.setAddress(address);
        testProperty1.setName("prop_name_59");
        testProperty1.setId(null);
        entityIsCreated(testProperty1);
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, null, null);
        getEntities(PROPERTIES_PATH, params);
        numberOfEntitiesInResponse(PropertyDto.class, Integer.parseInt(returned));
        headerIs(TOTAL_COUNT_HEADER, total);
        if (!linkHeader.equals("/null")) {
            headerContains("Link", linkHeader);
        }

    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "gettingListOfPropertiesPropertySets.csv")
    void gettingListOfPropertiesPropertySets(String limit, String cursor, String returned, String total) {
        range(0, 30).forEachOrdered(n -> {
            testPropertySet1.setId(null);
            testPropertySet1.setName(String.format("New_set_%d", n));
            UUID psId = entityIsCreated(testPropertySet1);
            PropertySetPropertyRelationshipCreateDto relation = constructPropertySetPropertyRelationship(psId, DEFAULT_PROPERTY_ID, true);
            entityIsCreated(relation);
        });
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, null, null, null, null);
        getEntities(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, params);
        numberOfEntitiesInResponse(PropertySetPropertyRelationshipDto.class, Integer.parseInt(returned));
        headerIs(TOTAL_COUNT_HEADER, total);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "filteringListOfPropertiesPropertySets.csv")
    void filteringListOfPropertiesPropertySets(
            String limit,
            String cursor,
            String returned,
            String total,
            String filter) {
        range(0, 12).forEachOrdered(n -> {
            testPropertySet1.setId(null);
            testPropertySet1.setName(String.format("New_set_%d", n));
            UUID psId = entityIsCreated(testPropertySet1);
            PropertySetPropertyRelationshipCreateDto relation = constructPropertySetPropertyRelationship(psId, DEFAULT_PROPERTY_ID, true);
            entityIsCreated(relation);
        });

        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, null, null, null);
        getEntities(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, params);
        numberOfEntitiesInResponse(PropertySetPropertyRelationshipDto.class, Integer.parseInt(returned));
        headerIs(TOTAL_COUNT_HEADER, total);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "filteringListOfUsersForProperty.csv")
    void filteringListOfUsersForProperty(
            String limit,
            String cursor,
            String returned,
            String total,
            String listPosition
    ) {
        List<UserType> types = Arrays.asList(
                UserType.CUSTOMER,
                UserType.CUSTOMER,
                UserType.GUEST,
                UserType.CUSTOMER,
                UserType.PARTNER,
                UserType.CUSTOMER,
                UserType.CUSTOMER,
                UserType.CUSTOMER,
                UserType.SNAPSHOT
        );
        ArrayList<UUID> userIds = new ArrayList<UUID>();
        range(0, 8).forEachOrdered(n -> {
            testUser1.setId(null);
            testUser1.setType(types.get(n));
            testUser1.setFirstName(String.format("FirstName%d", n));
            testUser1.setLastName(String.format("LastName%d", n));
            testUser1.setUsername(String.format("UserName%d", n));
            testUser1.setEmail(String.format("username%d@snapshot.travel", n));
            UUID userId = entityIsCreated(testUser1);
            userIds.add(userId);
            UserPropertyRelationshipCreateDto relation = constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true);
            entityIsCreated(relation);
        });
        String filter = "/null";
        if (!listPosition.equals("/null")) {
            filter = String.format("user_id==%s", String.valueOf(userIds.get(Integer.valueOf(listPosition))));
        }
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, null, null, null);
        getEntities(USER_PROPERTY_RELATIONSHIPS_PATH, params);
        numberOfEntitiesInResponse(UserPropertyRelationshipDto.class, Integer.parseInt(returned));
        headerIs(TOTAL_COUNT_HEADER, total);
    }

}
