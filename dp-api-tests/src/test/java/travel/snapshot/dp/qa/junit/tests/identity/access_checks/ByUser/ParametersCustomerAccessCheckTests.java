package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByUserForApp;


public class ParametersCustomerAccessCheckTests extends CommonAccessCheckByUserTest {

    private UUID createdUserId;
    private List<String> endpoints;

    @BeforeEach
    public void setUp() {
        super.setUp();
        UUID createdCustomerId = entityIsCreated(testCustomer1);
        createdUserId = entityIsCreated(testUser1);
        String customerPath = String.format("%s/%s", CUSTOMERS_PATH, createdCustomerId);
        String customerProperties = String.format("%s%s", customerPath, PROPERTIES_PATH);
        String customerPropertySets = String.format("%s%s", customerPath, PROPERTY_SETS_PATH);
        String customerCommercialSubscriptions = String.format("%s%s", customerPath, COMMERCIAL_SUBSCRIPTIONS_PATH);
        String customerUsers = String.format("%s%s", customerPath, USERS_PATH);
        endpoints = Arrays.asList(customerProperties, customerPropertySets, customerCommercialSubscriptions, customerUsers);
    }

    @Test
    void userAccessToSecondLevelEntities() {
        endpoints.forEach(endpoint->{
            getEntitiesByUserForApp(createdUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, endpoint, emptyQueryParams())
                    .then()
                    .statusCode(SC_NOT_FOUND)
                    .assertThat()
                    .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
        });
    }
}
