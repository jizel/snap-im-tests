package travel.snapshot.dp.qa.junit.tests.identity.properties;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.OWNER;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.PROPERTY_CODE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.VALID_FROM_VALUE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.VALID_TO_VALUE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

@RunWith(JUnitParamsRunner.class)
public class ParametersPropertiesTests extends CommonTest {

    private static final String EXAMPLES = "src/test/resources/csv/properties/";

    @FileParameters(EXAMPLES + "correctPropertyCodeIsReturnedWhenNoneSent.csv")
    @Test
    public void correctPropertyCodeIsReturnedWhenNoneSent(String name) throws IOException {
        testProperty1.setName(name);
        testProperty1.setId(null);
        testProperty1.setCode(null);
        commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        String propertyCode = commonHelpers.getAttributeValue(PROPERTY_CODE);
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
        commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        String propertyCode = commonHelpers.getAttributeValue(PROPERTY_CODE);
        assertThat("Passed and returned property code mismatch", resultingPropertyCode, is(propertyCode));
    }

    @FileParameters(EXAMPLES + "propertyCodeCanBeFilledManually.csv")
    @Test
    public void propertyCodeCanBeFilledManually(String code) throws IOException {
        testProperty1.setId(null);
        testProperty1.setCode(code);
        commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        String propertyCode = commonHelpers.getAttributeValue(PROPERTY_CODE);
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
        IntStream.range(0, 4).forEachOrdered(n -> {
            testCustomer1.setId(null);
            testCustomer1.setName(String.format("Some_customer_%d", n));
            testCustomer1.setEmail(String.format("customer_%d@snapshot.travel", n));
            UUID customerId = commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
            customerIds.add(customerId);
            CustomerPropertyRelationshipDto relation = relationshipsHelpers
                    .constructCustomerPropertyRelationshipDto(
                            customerId,
                            DEFAULT_PROPERTY_ID,
                            true,
                            OWNER,
                            LocalDate.parse(VALID_FROM_VALUE),
                            LocalDate.parse(VALID_TO_VALUE));
            commonHelpers.entityIsCreated(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relation);
        });
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, String.format(filter, customerIds.get(0).toString()), null, null, null);
        commonHelpers.getEntities(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, params);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(CustomerPropertyRelationshipDto.class, Integer.valueOf(returned));
        headerIs("X-Total-Count", total);
    }


}
