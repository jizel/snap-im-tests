package travel.snapshot.dp.qa.junit.tests.identity.access_checks;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.commercialSubscriptionIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_DETAILS;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

public class RolesAccessCheck extends CommonTest {

    UUID createdCustomerId = null;
    UUID userId1 = null;
    UUID userId2 = null;
    UUID appId = null;
    UUID appVersionID = null;
    UUID roleId = null;

    @Before
    public void setUp() {
        super.setUp();
        // User1 will belong to default customer
        userId1 = entityIsCreated(testUser1);
        entityIsCreated(constructUserPropertyRelationshipDto(userId1, DEFAULT_PROPERTY_ID, true));
        // User2 will belong to created customer
        createdCustomerId = entityIsCreated(testCustomer1);
        testUser2.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(createdCustomerId, true, true));
        userId2 = entityIsCreated(testUser2);
        testApplication1.setIsInternal(false);
        appId = entityIsCreated(testApplication1);
        dbSteps.populateApplicationPermissionsTableForApplication(appId);
        testAppVersion1.setApplicationId(appId);
        appVersionID = entityIsCreated(testAppVersion1);
        testRole1.setApplicationId(appId);
        commercialSubscriptionIsCreated(DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID, appId);
        roleId = entityIsCreated(testRole1);
    }

    @Test
    public void roleAccessControl() {
        assertThat(getEntitiesAsTypeByUserForApp(userId1, appVersionID, ROLES_PATH, RoleDto.class, emptyQueryParams())).hasSize(1);
        assertThat(getEntitiesAsTypeByUserForApp(userId1, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, ROLES_PATH, RoleDto.class, emptyQueryParams())).hasSize(1);
        assertThat(getEntitiesAsTypeByUserForApp(userId2, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, ROLES_PATH, RoleDto.class, emptyQueryParams())).hasSize(0);
        getEntitiesByUserForApp(userId2, appVersionID, ROLES_PATH, emptyQueryParams())
                .then()
                .statusCode(SC_FORBIDDEN)
                .assertThat()
                .body(RESPONSE_DETAILS, hasItem("Illegal combination of User ID and Application Version ID"));
    }
}
