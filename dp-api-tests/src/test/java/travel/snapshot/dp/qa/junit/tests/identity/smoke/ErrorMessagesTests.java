package travel.snapshot.dp.qa.junit.tests.identity.smoke;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.api.type.HttpMethod;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;

import java.util.Map;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLE_PERMISSIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_ENCRYPTED_PASSWORD;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PASSWORD;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.WRONG_ENCRYPTED_PASSWORD;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.constructCommercialSubscriptionDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

public class ErrorMessagesTests extends CommonSmokeTest {
    private static UUID customerId;
    private static UUID appVersionId;
    private static UUID userId;
    private static UUID appId;
    private static UUID roleId;
    private static UUID permissionId;
    private static UUID subscriptionId;
    private static String ERROR = "error_description";

    @BeforeEach
    public void setUp() {
        super.setUp();
        customerId = authorizationHelpers.entityIsCreated(testCustomer1);
        testUser1.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customerId, true, true));
        userId = userHelpers.userIsCreatedWithAuth(testUser1);
        appId = authorizationHelpers.entityIsCreated(testApplication1);
        dbSteps.populateApplicationPermissionsTableForApplication(appId);
        testAppVersion1.setApplicationId(appId);
        appVersionId = authorizationHelpers.entityIsCreated(testAppVersion1);
        authorizationHelpers.entityIsCreated(constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true));
        // Create keycloak secret for created app version
        Map<String, Object> testClient2 = loader.getClients().get("client2");
        try {
            keycloakHelpers.createClient(testClient2);
        } catch (Exception e) {
            fail("Exception during client creation: " + e.getMessage());
        }
        clientId = testClient2.get("clientId").toString();
        clientSecret = testClient2.get("secret").toString();
        subscriptionId = authorizationHelpers.entityIsCreated(constructCommercialSubscriptionDto(appId, customerId, DEFAULT_PROPERTY_ID));
        testRole1.setApplicationId(appId);
        testRole1.setIsActive(true);
        roleId = authorizationHelpers.entityIsCreated(testRole1);
        permissionId = authorizationHelpers.entityIsCreated(relationshipsHelpers.constructRolePermission(roleId, HttpMethod.GET, String.format("/identity%s", PROPERTIES_PATH), true));
        dbSteps.setUserPassword(userId, DEFAULT_ENCRYPTED_PASSWORD);
    }

    @Test
    public void testErrorMessages() {
        // missing role
        authorizationSteps.getToken(testUser1.getUsername(), DEFAULT_PASSWORD, clientId, clientSecret)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .assertThat()
                .body(ERROR, is("Looks like you don''t have a role assigned for this application. Please contact your administrator to get set up."));

        // missing subscription
        authorizationHelpers.entityIsDeleted(ROLE_PERMISSIONS_PATH, permissionId);
        authorizationHelpers.entityIsDeleted(ROLES_PATH, roleId);
        authorizationHelpers.entityIsDeleted(COMMERCIAL_SUBSCRIPTIONS_PATH, subscriptionId);
        authorizationSteps.getToken(testUser1.getUsername(), DEFAULT_PASSWORD, clientId, clientSecret)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .assertThat()
                .body(ERROR, is("Looks like this application hasn''t been purchased for your property. Please contact your administrator."));

        // wrong password
        subscriptionId = authorizationHelpers.entityIsCreated(constructCommercialSubscriptionDto(appId, customerId, DEFAULT_PROPERTY_ID));
        dbSteps.setUserPassword(userId, WRONG_ENCRYPTED_PASSWORD);
        authorizationSteps.getToken(testUser1.getUsername(), DEFAULT_PASSWORD, clientId, clientSecret)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .assertThat()
                .body(ERROR, is("Invalid user credentials"));
    }
}
