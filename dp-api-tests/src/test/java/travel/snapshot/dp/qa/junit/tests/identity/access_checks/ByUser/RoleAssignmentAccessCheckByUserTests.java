package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.api.identity.model.RoleAssignmentDto;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLE_ASSIGNMENTS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructRoleAssignment;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RoleHelpers.constructRole;

public class RoleAssignmentAccessCheckByUserTests extends CommonAccessCheckByUserTest {

    UUID customerId;
    UUID targetUserId;
    UUID roleId;
    UUID roleAssignmentId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        requestorId = entityIsCreated(testUser1);
        customerId = entityIsCreated(testCustomer1);
        testUser2.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customerId, true, true));
        targetUserId = entityIsCreated(testUser2);
        roleId = entityIsCreated(constructRole(DEFAULT_SNAPSHOT_APPLICATION_ID, "user"));
        roleAssignmentId = entityIsCreated(constructRoleAssignment(roleId, requestorId));
        dbSteps.populateRolePermissionsForRole(DEFAULT_SNAPSHOT_APPLICATION_ID, roleId);
    }

    @Test
    void roleAssignmentCRUDAccessCheck() {
        createEntityFails(requestorId, constructRoleAssignment(roleId, targetUserId));
        UUID targetAssignmentId = entityIsCreated(constructRoleAssignment(roleId, targetUserId));
        getEntityFails(requestorId, ROLE_ASSIGNMENTS_PATH, targetAssignmentId);
        deleteEntityFails(requestorId, ROLE_ASSIGNMENTS_PATH, targetAssignmentId, SC_NOT_FOUND);
        assertThat(getEntitiesAsTypeByUserForApp(requestorId,
                                      DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                                      ROLE_ASSIGNMENTS_PATH,
                                      RoleAssignmentDto.class,
                                      emptyQueryParams())).hasSize(1);
    }
}
