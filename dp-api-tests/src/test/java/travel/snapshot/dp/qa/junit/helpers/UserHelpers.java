package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.cucumber.serenity.users.UsersSteps;

@Log
public class UserHelpers extends UsersSteps {

    public static final String BASE_PATH_USERS = "/identity/users";
    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();
    private final CommonHelpers commonHelpers = new CommonHelpers();
    private final RelationshipsHelpers relationshipHelpers = new RelationshipsHelpers();

    public UserHelpers() {
        super();
    }

    public UserDto userIsCreated(UserCreateDto createdUser) {
        Response response = createUser(createdUser);
        responseCodeIs(SC_CREATED);
        return response.as(UserDto.class);
    }

    public void createUserWithAuth(UserCreateDto userObject) {
        authorizationHelpers.createEntity(BASE_PATH_USERS, userObject);
    }

    public String userIsCreatedWithAuth(UserCreateDto userObject) throws Throwable {
        createUserWithAuth(userObject);
        responseCodeIs(SC_CREATED);
        String userId = getSessionResponse().as(UserDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(USERS, userId);

        // now we need to mark default user_customer relationship for deletion
        UserCustomerRelationshipDto relation = relationshipHelpers.getUserCustomerRelationsForUserWithAuth(userId).get(0);
        commonHelpers.updateRegistryOfDeletables(CUSTOMER_USERS, relation.getId());

        return getSessionResponse().as(UserDto.class).getId();
    }

    public UserDto userWithCustomerIsCreated(UserCreateDto createdUser, String customerId) {
        Response response = createUserWithCustomer(createdUser, customerId, true, true);
        responseCodeIs(SC_CREATED);
        return response.as(UserDto.class);
    }

    public Response getUserByUserForApp(String requestorId, String applicationVersionId, String userId) {
        Response response = getEntityByUserForApplication(requestorId, applicationVersionId, userId);
        setSessionResponse(response);
        return response;
    }

    public Response getAllUserPropertiesByUserForApp(String requestorId, String applicationVersionId, String userId) {
        Response response = getSecondLevelEntitiesByUserForApp(requestorId, applicationVersionId, userId, SECOND_LEVEL_OBJECT_PROPERTIES, null, null, null, null, null, null);
        setSessionResponse(response);
        return response;
    }

    public Response createUserByUserForApp(String requestorId, String applicationVersionId, UserCreateDto user) {
        return createEntityByUserForApplication(requestorId, applicationVersionId, user);
    }

    public Response updateUserByUserForApp(String requestorId, String applicationVersionId, String userId, UserUpdateDto userUpdate) {
        Response response = null;
        try {
            JSONObject userData = retrieveData(userUpdate);
            response = updateEntityByUserForApplication(requestorId, applicationVersionId, userId, userData.toString(), getEntityEtag(userId));
        } catch (JsonProcessingException e) {
            fail("Error while retrieving data from UserUpdate object: " + e.getMessage());
        }
        return response;
    }

    public Response deleteUserByUserForApp(String requestorId, String applicationVersionId, String userId) {
        return deleteEntityByUserForApplication(requestorId, applicationVersionId, userId, getEntityEtag(userId));
    }

    public void getPartnersForUserByUserForApp(String userId, String requestorId, String appVersionId) {
        Response response = getSecondLevelEntitiesByUserForApp(requestorId, appVersionId, userId, SECOND_LEVEL_OBJECT_PARTNERS, null, null, null, null, null, null);
        setSessionResponse(response);
    }

}
