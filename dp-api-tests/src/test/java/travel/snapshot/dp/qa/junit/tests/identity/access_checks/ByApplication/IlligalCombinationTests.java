package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.commercialSubscriptionIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.utils.DpEndpoints.READ_WRITE_ENDPOINTS;

public class IlligalCombinationTests extends CommonTest {

    private static UUID customer1Id;
    private static UUID appVersionId;
    private static UUID user1Id;
    private static UUID appId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        customer1Id = entityIsCreated(testCustomer1);
        testUser1.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customer1Id, true, true));
        user1Id = entityIsCreated(testUser1);
        appId = entityIsCreated(testApplication1);
        dbSteps.populateApplicationPermissionsTableForApplication(appId);
        testAppVersion1.setApplicationId(appId);
        appVersionId = entityIsCreated(testAppVersion1);
        entityIsCreated(constructUserPropertyRelationshipDto(user1Id, DEFAULT_PROPERTY_ID, true));
    }

    @Test
    public void appHasNoAccessToUserIfThereIsNoCommercialSubscriptionToCustomer() {
        READ_WRITE_ENDPOINTS.forEach(endpoint -> {
            getEntitiesByUserForApp(user1Id, appVersionId, endpoint, emptyQueryParams())
                    .then()
                    .statusCode(SC_FORBIDDEN)
                    .assertThat()
                    .body("details", hasItem("Illegal combination of User ID and Application Version ID"));
        });
        commercialSubscriptionIsCreated(customer1Id, DEFAULT_PROPERTY_ID, appId);
        READ_WRITE_ENDPOINTS.forEach(endpoint -> {
            getEntitiesByUserForApp(user1Id, appVersionId, endpoint, emptyQueryParams())
                    .then()
                    .statusCode(SC_OK);
        });
    }
}
