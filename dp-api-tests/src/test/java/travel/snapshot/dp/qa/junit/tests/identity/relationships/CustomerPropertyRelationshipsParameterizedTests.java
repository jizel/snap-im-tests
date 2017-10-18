package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import com.jayway.restassured.response.Response;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonRelationshipsTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.IS_ACTIVE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.RELATIONSHIP_TYPE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.VALID_FROM;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.VALID_TO;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.sendPostWithBody;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;

@RunWith(JUnitParamsRunner.class)
public class CustomerPropertyRelationshipsParameterizedTests extends CommonRelationshipsTest {

    private static final String EXAMPLES = "src/test/resources/csv/relationships/";
    private UUID createdPropertyId1 = null;

    @Before
    public void setUp() {
        super.setUp();
        entityIsCreated(testCustomer1);
        createdPropertyId1 = entityIsCreated(testProperty1);
        entityIsCreated(constructCustomerPropertyRelationshipDto(
                DEFAULT_SNAPSHOT_CUSTOMER_ID,
                createdPropertyId1,
                true,
                CustomerPropertyRelationshipType.CHAIN,
                validFrom, validTo));
    }

    @FileParameters(EXAMPLES + "checkingErrorCodesForCreatingCustomerProperty.csv")
    @Test
    public void checkingErrorCodesForCreatingCustomerProperty(String property_code,
                                                              String type,
                                                              Integer customer_index,
                                                              String valid_from,
                                                              String valid_to,
                                                              String error_code,
                                                              String custom_code) {
        Map<String, String> params = buildQueryParamMapForPaging(null, null, String.format("property_code==%s", property_code), null, null, null);
        UUID propertyId = null;
        try {
            propertyId = getEntitiesAsType(PROPERTIES_PATH, PropertyDto.class, params).get(0).getId();
        } catch (IndexOutOfBoundsException e) {
            propertyId = NON_EXISTENT_ID;
        }
        UUID customerId = getEntitiesAsType(CUSTOMERS_PATH, CustomerDto.class, emptyQueryParams()).get(customer_index).getId();
        Map<String, String> customerPropertyRelationMap = new HashMap<>();
        customerPropertyRelationMap.put(RELATIONSHIP_TYPE, type.toUpperCase());
        customerPropertyRelationMap.put(VALID_FROM, valid_from);
        customerPropertyRelationMap.put(VALID_TO, valid_to);
        customerPropertyRelationMap.put(IS_ACTIVE, "true");
        customerPropertyRelationMap.put(PROPERTY_ID, propertyId.toString());
        customerPropertyRelationMap.put(CUSTOMER_ID, customerId.toString());
        sendPostWithBody(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, customerPropertyRelationMap)
                .then()
                .statusCode(Integer.valueOf(error_code))
                .assertThat()
                .body(RESPONSE_CODE, is(Integer.valueOf(custom_code)));
    }
}
