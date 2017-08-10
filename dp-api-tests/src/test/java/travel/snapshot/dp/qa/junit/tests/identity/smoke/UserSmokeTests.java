package travel.snapshot.dp.qa.junit.tests.identity.smoke;

import org.junit.Test;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;

public class UserSmokeTests extends CommonSmokeTest {
    protected String userId;
    protected String userCustomerRelationId;


    @Test
    public void userCRUD() throws Throwable {
        // create
        String userId = userHelpers.userIsCreatedWithAuth(testUser1);
        userCustomerRelationId = relationshipsHelpers
               .getUserCustomerRelationsForUserWithAuth(userId)
               .get(0)
               .getId();
        // request
        authorizationHelpers.getEntity(USERS_PATH, userId);
        responseCodeIs(SC_OK);
        // update
        UserUpdateDto update = new UserUpdateDto();
        update.setFirstName("Modified");
        authorizationHelpers.entityIsUpdated(USERS_PATH, userId, update);
        // make sure changes applied
        authorizationHelpers.getEntity(USERS_PATH, userId);
        bodyContainsEntityWith("first_name", "Modified");
        // delete
        authorizationHelpers.deleteEntity(USERS_PATH, userId);
        responseCodeIs(SC_CONFLICT);
        authorizationHelpers.entityIsDeleted(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationId);
        authorizationHelpers.entityIsDeleted(USERS_PATH, userId);
    }

/*
// DPIM-41
    @Test
    public void assignRoleToUserCustomer() {
        // Create role
        String roleId = roleHelpers.customerRoleIsCreatedWithAuth(testCustomerRole1);
        // Assign role to relation
        String relationRoleId = relationshipsHelpers.userCustomerRoleRelationIsCreatedWithAuth(userCustomerRelationId, roleId);
        // get relation role
        authorizationHelpers.getEntity(USER_CUSTOMER_ROLE_RELATIONSHIPS_PATH, relationRoleId);
        responseCodeIs(SC_OK);
        // update for role relationships is not supported
        // delete
        authorizationHelpers.entityIsDeleted(USER_CUSTOMER_ROLE_RELATIONSHIPS_PATH, relationRoleId);
    }
*/
}
