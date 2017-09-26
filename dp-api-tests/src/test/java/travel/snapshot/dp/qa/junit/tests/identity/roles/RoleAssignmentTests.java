package travel.snapshot.dp.qa.junit.tests.identity.roles;

import lombok.extern.java.Log;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.RoleAssignmentDto;
import travel.snapshot.dp.api.identity.model.RolePermissionDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.type.HttpMethod;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLE_ASSIGNMENTS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLE_PERMISSIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ALL_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;

@Log
public class RoleAssignmentTests extends CommonTest {
    protected UUID createdUserId = null;
    protected UUID createdAppId = null;
    protected UUID createdAppVersionId = null;
    protected UUID roleId = null;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdUserId = commonHelpers.entityIsCreated(testUser1);

        testApplication1.setIsInternal(false);
        createdAppId = commonHelpers.entityIsCreated(testApplication1);
        commercialSubscriptionHelpers.commercialSubscriptionIsCreated(DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID, createdAppId);

        testAppVersion1.setApplicationId(createdAppId);
        createdAppVersionId = commonHelpers.entityIsCreated(testAppVersion1);

        UserPropertyRelationshipCreateDto relationship = relationshipsHelpers.constructUserPropertyRelationshipDto(createdUserId, DEFAULT_PROPERTY_ID, true);
        commonHelpers.entityIsCreated(relationship);

        testRole1.setApplicationId(createdAppId);
        roleId = commonHelpers.entityIsCreated(testRole1);

        dbSteps.populateApplicationPermissionsTableForApplication(createdAppId);
    }

    @Test
    public void userWithNoRoleIsNotAllowedToCallAnyEndpoints() {
        ALL_ENDPOINTS.forEach(endpoint -> {
            log.info("Endpoint is " + endpoint);
            commonHelpers.getEntitiesByUserForApp(createdUserId, createdAppVersionId, endpoint, emptyQueryParams())
                    .then()
                    .statusCode(SC_NOT_FOUND);
        });
    }

    @Test
    public void roleWithWithoutPermissions() {
        commonHelpers.entityIsCreated(relationshipsHelpers.constructRoleAssignment(roleId, createdUserId));
        ALL_ENDPOINTS.forEach(endpoint -> {
            log.info("Endpoint is " + endpoint);
            commonHelpers.getEntitiesByUserForApp(createdUserId, createdAppVersionId, endpoint, emptyQueryParams())
                    .then()
                    .statusCode(SC_NOT_FOUND);
            commonHelpers.entityIsCreated(relationshipsHelpers.constructRolePermission(roleId, HttpMethod.GET, String.format("/identity%s", endpoint)));
            commonHelpers.getEntitiesByUserForApp(createdUserId, createdAppVersionId, endpoint, emptyQueryParams())
                    .then()
                    .statusCode(SC_OK);
        });
    }

    @Test
    public void roleAssignmentCRUD() {
        UUID assignmentId = commonHelpers.entityIsCreated(relationshipsHelpers.constructRoleAssignment(roleId, createdUserId));
        RoleAssignmentDto assignmentDto = commonHelpers.getEntityAsType(ROLE_ASSIGNMENTS_PATH, RoleAssignmentDto.class, assignmentId);
        assertThat(assignmentId, is(assignmentDto.getId()));
        // Role assignments do not support update, therefore - delete
        commonHelpers.entityIsDeleted(ROLE_ASSIGNMENTS_PATH, assignmentId);
    }

    @Test
    public void rolePermissionsCRUD() {
        UUID rolePermissionId = commonHelpers.entityIsCreated(relationshipsHelpers.constructRolePermission(roleId, HttpMethod.GET, String.format("/identity%s", PROPERTIES_PATH)));
        RolePermissionDto rolePermission = commonHelpers.getEntityAsType(ROLE_PERMISSIONS_PATH, RolePermissionDto.class, rolePermissionId);
        assertThat(rolePermissionId, is(rolePermission.getId()));
        // Role permissions do not support update, therefore - delete
        commonHelpers.entityIsDeleted(ROLE_PERMISSIONS_PATH, rolePermissionId);
    }
}
