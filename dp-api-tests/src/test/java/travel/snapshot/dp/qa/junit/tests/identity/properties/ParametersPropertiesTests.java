package travel.snapshot.dp.qa.junit.tests.identity.properties;

import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.OWNER;
import static travel.snapshot.dp.api.identity.model.UserUpdateDto.UserType;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.PROPERTY_CODE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.VALID_FROM_VALUE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.VALID_TO_VALUE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerContains;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(JUnitParamsRunner.class)
public class ParametersPropertiesTests extends CommonTest {

    private static final String EXAMPLES = "src/test/resources/csv/properties/";

    @FileParameters(EXAMPLES + "correctPropertyCodeIsReturnedWhenNoneSent.csv")
    @Test
    public void correctPropertyCodeIsReturnedWhenNoneSent(String name) throws IOException {
        testProperty1.setName(name);
        testProperty1.setId(null);
        testProperty1.setCode(null);
        commonHelpers.entityIsCreated(testProperty1);
        String propertyCode = getAttributeValue(PROPERTY_CODE);
        assertFalse("Property code is empty", propertyCode.isEmpty());
        assertFalse("Property code contains whitespaces", propertyCode.matches("\\s"));
        assertTrue("Property code contains invalid characters", propertyCode.matches("[A-Z0-9]+"));
    }

    @FileParameters(EXAMPLES + "correctPropertyCodeIsReturnedAccordingToCustomersAddress.csv")
    @Test
    public void correctPropertyCodeIsReturnedAccordingToCustomersAddress(
        String name,
        String line1,
        String city,
        String zipCode,
        String countryCode,
        String resultingPropertyCode
    ) throws IOException {
        AddressDto address = new AddressDto();
        address.setCountryCode(countryCode);
        address.setZipCode(zipCode);
        address.setCity(city);
        address.setLine1(line1);
        testProperty1.setName(name);
        testProperty1.setId(null);
        testProperty1.setCode(null);
        testProperty1.setAddress(address);
        commonHelpers.entityIsCreated(testProperty1);
        String propertyCode = getAttributeValue(PROPERTY_CODE);
        assertThat("Passed and returned property code mismatch", resultingPropertyCode, is(propertyCode));
    }

    @FileParameters(EXAMPLES + "propertyCodeCanBeFilledManually.csv")
    @Test
    public void propertyCodeCanBeFilledManually(String code) throws IOException {
        testProperty1.setId(null);
        testProperty1.setCode(code);
        commonHelpers.entityIsCreated(testProperty1);
        String propertyCode = getAttributeValue(PROPERTY_CODE);
        assertThat("Passed and returned property code mismatch", propertyCode, is(code));
    }

    @FileParameters(EXAMPLES + "filteringListOfCustomerProperties.csv")
    @Test
    public void filteringListOfCustomerProperties(
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
            UUID customerId = commonHelpers.entityIsCreated(testCustomer1);
            customerIds.add(customerId);
            CustomerPropertyRelationshipCreateDto relation = relationshipsHelpers
                    .constructCustomerPropertyRelationshipDto(customerId, DEFAULT_PROPERTY_ID, true, OWNER,
                            LocalDate.parse(VALID_FROM_VALUE),
                            LocalDate.parse(VALID_TO_VALUE));
            commonHelpers.entityIsCreated(relation);
        });
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, String.format(filter, customerIds.get(0).toString()), null, null, null);
        commonHelpers.getEntities(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, params);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(CustomerPropertyRelationshipDto.class, Integer.valueOf(returned));
        headerIs("X-Total-Count", total);
    }

    @FileParameters(EXAMPLES + "validateThatPropertyRegionsBelongToTheCorrectCountry.csv")
    @Test
    public void validateThatPropertyRegionsBelongToTheCorrectCountry(String country, String region) {
        AddressDto address = testProperty1.getAddress();
        address.setCountryCode(country);
        address.setRegionCode(region);
        testProperty1.setAddress(address);
        commonHelpers.entityIsCreated(testProperty1);
        bodyContainsEntityWith("address.region", region);
    }

    @FileParameters(EXAMPLES + "checkingErrorCodesForRegions.csv")
    @Test
    public void checkingErrorCodesForRegions(String country, String region) {
        AddressDto address = testProperty1.getAddress();
        address.setCountryCode(country);
        address.setRegionCode(region);
        testProperty1.setAddress(address);
        commonHelpers.createEntity(PROPERTIES_PATH, testProperty1);
        responseIsReferenceDoesNotExist();
    }

    @FileParameters(EXAMPLES + "gettingListOfProperties.csv")
    @Test
    @Category(Categories.SlowTests.class)
    public void gettingListOfProperties(
        String limit,
        String cursor,
        String sort,
        String returned,
        String total,
        String linkHeader
    ) {
        range(0, 59).forEachOrdered(n -> {
            testProperty1.setName(String.format("prop_name_%d", n));
            testProperty1.setId(null);
            testProperty1.setCode(null);
            commonHelpers.entityIsCreated(testProperty1);
        });
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, null, sort, null, null);
        commonHelpers.getEntities(PROPERTIES_PATH, params);
        numberOfEntitiesInResponse(PropertyDto.class, Integer.parseInt(returned));
        headerIs("X-Total-Count", total);
        headerContains("Link", linkHeader);

    }

    @FileParameters(EXAMPLES + "gettingListOfPropertiesPropertySets.csv")
    @Test
    @Category(Categories.SlowTests.class)
    public void gettingListOfPropertiesPropertySets(String limit, String cursor, String returned,  String total) {
        range(0, 30).forEachOrdered(n -> {
            testPropertySet1.setId(null);
            testPropertySet1.setName(String.format("New_set_%d", n));
            UUID psId = commonHelpers.entityIsCreated(testPropertySet1);
            PropertySetPropertyRelationshipCreateDto relation = relationshipsHelpers
                    .constructPropertySetPropertyRelationship(psId, DEFAULT_PROPERTY_ID, true);
            commonHelpers.entityIsCreated(relation);
        });
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, null, null, null, null);
        commonHelpers.getEntities(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, params);
        numberOfEntitiesInResponse(PropertySetPropertyRelationshipDto.class, Integer.parseInt(returned));
        headerIs("X-Total-Count", total);
    }

    @FileParameters(EXAMPLES + "filteringListOfPropertiesPropertySets.csv")
    @Category(Categories.SlowTests.class)
    @Test
    public void filteringListOfPropertiesPropertySets(
            String limit,
            String cursor,
            String returned,
            String total,
            String filter) {
        range(0, 12).forEachOrdered(n -> {
            testPropertySet1.setId(null);
            testPropertySet1.setName(String.format("New_set_%d", n));
            UUID psId = commonHelpers.entityIsCreated(testPropertySet1);
            PropertySetPropertyRelationshipCreateDto relation = relationshipsHelpers
                    .constructPropertySetPropertyRelationship(psId, DEFAULT_PROPERTY_ID, true);
            commonHelpers.entityIsCreated(relation);
        });

        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, null, null, null);
        commonHelpers.getEntities(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, params);
        numberOfEntitiesInResponse(PropertySetPropertyRelationshipDto.class, Integer.parseInt(returned));
        headerIs("X-Total-Count", total);
    }

    @FileParameters(EXAMPLES + "filteringListOfUsersForProperty.csv")
    @Test
    public void filteringListOfUsersForProperty(
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
            UUID userId = commonHelpers.entityIsCreated(testUser1);
            userIds.add(userId);
            UserPropertyRelationshipCreateDto relation = relationshipsHelpers.constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true);
            commonHelpers.entityIsCreated(relation);
        });
        String filter = "/null";
        if (!listPosition.equals("/null")) {
            filter = String.format("user_id==%s", String.valueOf(userIds.get(Integer.valueOf(listPosition))));
        }
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, null, null, null);
        commonHelpers.getEntities(USER_PROPERTY_RELATIONSHIPS_PATH, params);
        numberOfEntitiesInResponse(UserPropertyRelationshipDto.class, Integer.parseInt(returned));
        headerIs("X-Total-Count", total);
    }

}
