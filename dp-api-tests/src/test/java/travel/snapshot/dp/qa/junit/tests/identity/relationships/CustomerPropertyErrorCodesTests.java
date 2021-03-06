package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.IS_ACTIVE;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.RELATIONSHIP_TYPE;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.VALID_FROM;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.VALID_TO;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.sendPostWithBody;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonRelationshipsTest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class CustomerPropertyErrorCodesTests extends CommonRelationshipsTest {

    private static final String EXAMPLES = "/csv/relationships/";

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        entityIsCreated(testCustomer1);
        UUID createdPropertyId1 = entityIsCreated(testProperty1);
        entityIsCreated(constructCustomerPropertyRelationshipDto(
                DEFAULT_SNAPSHOT_CUSTOMER_ID,
                createdPropertyId1,
                true,
                CustomerPropertyRelationshipType.CHAIN,
                validFrom, validTo));
    }


    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "checkingErrorCodesForCreatingCustomerProperty.csv")
    public void checkingErrorCodesForCreatingCustomerProperty(String property_code,
                                                              String type,
                                                              Integer customer_index,
                                                              String valid_from,
                                                              String valid_to,
                                                              String error_code,
                                                              String custom_code) {
        Map<String, String> params = buildQueryParamMapForPaging(null, null, String.format("property_code==%s", property_code), null, null, null);
        UUID propertyId;
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
