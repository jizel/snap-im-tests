package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_RESOURCE;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.qa.cucumber.serenity.user_groups.UserGroupsSteps;

/**
 * Created by zelezny on 6/27/2017.
 */
public class UserGroupHelpers extends UserGroupsSteps{
    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();
    private final CommonHelpers commonHelpers = new CommonHelpers();

    public Response createUserGroup(UserGroupDto userGroup){
        return createEntity(userGroup);
    }

    public UserGroupDto userGroupIsCreated(UserGroupDto userGroup) {
        Response response = createUserGroup(userGroup);
        responseCodeIs(SC_CREATED);
        return response.as(UserGroupDto.class);
    }

    public void createUserGroupWithAuth(UserGroupDto userGroup) {
        authorizationHelpers.createEntity(USER_GROUPS_PATH, userGroup);
    }

    public String userGroupIsCreatedWithAuth(UserGroupDto userGroup) {
        createUserGroupWithAuth(userGroup);
        responseCodeIs(SC_CREATED);
        String groupId = getSessionResponse().as(UserGroupDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(USER_GROUPS_RESOURCE, groupId);
        return groupId;
    }
}
