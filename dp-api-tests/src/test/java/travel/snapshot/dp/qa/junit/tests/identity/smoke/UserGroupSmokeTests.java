package travel.snapshot.dp.qa.junit.tests.identity.smoke;

import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.UserGroupUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;

import java.util.UUID;

@RunWith(SerenityRunner.class)
public class UserGroupSmokeTests extends CommonSmokeTest {

    @Test
    public void userGroupCRUD() {
        // create
        UUID userGroupId = userGroupHelpers.userGroupIsCreatedWithAuth(testUserGroup1);
        // request
        authorizationHelpers.getEntity(USER_GROUPS_PATH, userGroupId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("name", "Test UserGroup 1");
        bodyContainsEntityWith("description", "Test UserGroup 1 description");
        bodyContainsEntityWith("customer_id", "06000000-0000-4444-8888-000000000001");
        // update
        UserGroupUpdateDto update = new UserGroupUpdateDto();
        update.setName("New name");
        authorizationHelpers.entityIsUpdated(USER_GROUPS_PATH, userGroupId, update);
        // make sure changes applied
        authorizationHelpers.getEntity(USER_GROUPS_PATH, userGroupId);
        bodyContainsEntityWith("name", "New name");
        // delete
        authorizationHelpers.entityIsDeleted(USER_GROUPS_PATH, userGroupId);
    }
}
