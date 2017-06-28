package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.qa.cucumber.serenity.user_groups.UserGroupsSteps;

/**
 * Created by zelezny on 6/27/2017.
 */
public class UserGroupHelpers extends UserGroupsSteps{

    public Response createUserGroup(UserGroupDto userGroup){
        return createEntity(userGroup);
    }

    public UserGroupDto userGroupIsCreated(UserGroupDto userGroup) {
        Response response = createUserGroup(userGroup);
        assertEquals(String.format("Failed to create user group: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(UserGroupDto.class);
    }
}
