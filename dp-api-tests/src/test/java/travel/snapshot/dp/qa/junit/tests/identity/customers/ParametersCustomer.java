package travel.snapshot.dp.qa.junit.tests.identity.customers;


import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.qa.cucumber.helpers.AddressUtils.createRandomAddress;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_DETAILS;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_MESSAGE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.tests.Tags.SLOW_TEST;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Example of JUnit tests using Parameters. It can be used for multiline data driven testing where every line of data is
 * reported as separate test but it cannot be used with Serenity Runner. Also it needs data set per test.
 */
@Tag(SLOW_TEST)
public class ParametersCustomer extends CommonTest {

    private static final String CUSTOMER_EXAMPLES = "/csv/customers/";
    private static final String COMM_SUBSCRIPTIONS_ERROR_EXAMPLES = CUSTOMER_EXAMPLES + "getCustomerCommSubscriptionErrorCodesTestExamples.csv";
    private static final String CORRECT_REGION_EXAMPLES = CUSTOMER_EXAMPLES + "validateCustomerRegionsBelongToCorrectCountry.csv";
    private static final String INVALID_REGIONS_EXAMPLES = CUSTOMER_EXAMPLES + "invalidRegions.csv";
    private static final String INVALID_VAT_ID_EXAMPLES = CUSTOMER_EXAMPLES + "validateInvalidVatId.csv";
    private static final String VALID_VAT_ID_EXAMPLES = CUSTOMER_EXAMPLES + "validateValidVatId.csv";


    @ParameterizedTest
    @CsvFileSource(resources = COMM_SUBSCRIPTIONS_ERROR_EXAMPLES)
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingParams(String limit,
                                                                                          String cursor,
                                                                                          String filter,
                                                                                          String sort,
                                                                                          String sortDesc,
                                                                                          String responseCode,
                                                                                          String customCode) throws Throwable {
        UUID createdCustomerId = entityIsCreated(testCustomer1);
        commonHelpers.getRelationships(CUSTOMERS_PATH, createdCustomerId, COMMERCIAL_SUBSCRIPTIONS_RESOURCE,
                buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null));
        responseCodeIs(Integer.valueOf(responseCode));
        customCodeIs(Integer.valueOf(customCode));
    }

    @ParameterizedTest
    @CsvFileSource(resources = CORRECT_REGION_EXAMPLES)
    public void validateCustomerRegionsBelongToCorrectCountry(String country,
                                                              String region,
                                                              String vatId) {
        AddressDto address = createRandomAddress(5, 5, 6, country, region);
        testCustomer1.setAddress(address);
        testCustomer1.setVatId(vatId);
        createEntity(testCustomer1)
                .then()
                .statusCode(SC_CREATED)
                .body("vat_id", is(vatId));
    }

    @ParameterizedTest
    @CsvFileSource(resources = INVALID_VAT_ID_EXAMPLES)
    public void validateCustomerHasInvalidVatId(String country, String vatId) {
        AddressDto address = createRandomAddress(5, 5, 6, country, null);
        testCustomer1.setAddress(address);
        testCustomer1.setVatId(vatId);
        createEntity(testCustomer1)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY);
    }

    @ParameterizedTest
    @CsvFileSource(resources = VALID_VAT_ID_EXAMPLES)
    public void validateCustomerHasValidVatId(String country, String vatId, String region) throws Throwable {
        AddressDto address = createRandomAddress(5, 5, 6, country, null);
        address.setRegionCode(region);
        testCustomer1.setAddress(address);
        testCustomer1.setVatId(vatId);
        createEntity(testCustomer1)
                .then()
                .statusCode(SC_CREATED);
    }

    @ParameterizedTest
    @CsvFileSource(resources = INVALID_REGIONS_EXAMPLES)
    public void checkErrorCodesForRegions(String country, String region) throws Exception {
        AddressDto address = createRandomAddress(10, 10, 5, country, region);
        testCustomer1.setAddress(address);
        createEntity(CUSTOMERS_PATH, testCustomer1)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS))
                .body(RESPONSE_MESSAGE, is("Semantic validation errors in the request body."))
                .body(RESPONSE_DETAILS, hasItem("The 'region' attribute is invalid for specified country"));
    }
}



