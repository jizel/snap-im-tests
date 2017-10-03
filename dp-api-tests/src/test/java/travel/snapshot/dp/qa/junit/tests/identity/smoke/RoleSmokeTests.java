package travel.snapshot.dp.qa.junit.tests.identity.smoke;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionCreateDto;
import travel.snapshot.dp.api.identity.model.RoleAssignmentDto;
import travel.snapshot.dp.api.identity.model.RolePermissionDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;
import travel.snapshot.dp.api.type.HttpMethod;

import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLE_ASSIGNMENTS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLE_PERMISSIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.constructCommercialSubscriptionDto;

public class RoleSmokeTests extends CommonSmokeTest {
    private UUID createdUserId;
    private UUID createdAppVersionId;
    private UUID createdRoleId;

    @Before
    public void setUp() {
        super.setUp();
        createdUserId = userHelpers.userIsCreatedWithAuth(testUser1);
        testApplication1.setIsInternal(false);
        UUID createdAppId = authorizationHelpers.entityIsCreated(testApplication1);
        UUID createdCustomerId = authorizationHelpers.entityIsCreated(testCustomer1);
        CommercialSubscriptionCreateDto subscription = constructCommercialSubscriptionDto(createdAppId, createdCustomerId, DEFAULT_PROPERTY_ID);
        authorizationHelpers.entityIsCreated(subscription);

        testAppVersion1.setApplicationId(createdAppId);
        createdAppVersionId = authorizationHelpers.entityIsCreated(testAppVersion1);

        UserPropertyRelationshipCreateDto relationship = relationshipsHelpers.constructUserPropertyRelationshipDto(createdUserId, DEFAULT_PROPERTY_ID, true);
        authorizationHelpers.entityIsCreated(relationship);

        testRole1.setApplicationId(createdAppId);
        testRole1.setIsActive(true);
        createdRoleId = authorizationHelpers.entityIsCreated(testRole1);
        dbSteps.populateApplicationPermissionsTableForApplication(createdAppId);
    }

    @Test
    public void roleAssignmentCRUD() {
        UUID assignmentId = authorizationHelpers.entityIsCreated(relationshipsHelpers.constructRoleAssignment(createdRoleId, createdUserId));
        RoleAssignmentDto assignmentDto = authorizationHelpers.getEntity(ROLE_ASSIGNMENTS_PATH, assignmentId).as(RoleAssignmentDto.class);
        assertThat(assignmentId, is(assignmentDto.getId()));
        // Role assignments do not support update, therefore - delete
        authorizationHelpers.entityIsDeleted(ROLE_ASSIGNMENTS_PATH, assignmentId);
    }

    @Test
    public void rolePermissionsCRUD() {
        UUID rolePermissionId = authorizationHelpers.entityIsCreated(relationshipsHelpers.constructRolePermission(createdRoleId, HttpMethod.GET, String.format("/identity%s", PROPERTIES_PATH)));
        RolePermissionDto rolePermission = authorizationHelpers.getEntity(ROLE_PERMISSIONS_PATH, rolePermissionId).as(RolePermissionDto.class);
        assertThat(rolePermissionId, is(rolePermission.getId()));
        // Role permissions do not support update, therefore - delete
        authorizationHelpers.entityIsDeleted(ROLE_PERMISSIONS_PATH, rolePermissionId);
    }
}
