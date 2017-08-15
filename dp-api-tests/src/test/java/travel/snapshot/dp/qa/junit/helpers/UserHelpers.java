package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.cucumber.serenity.users.UsersSteps;

import java.util.UUID;

@Log
public class UserHelpers extends UsersSteps {

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
        authorizationHelpers.createEntity(USERS_PATH, userObject);
    }

    public UUID userIsCreatedWithAuth(UserCreateDto userObject) throws Throwable {
        createUserWithAuth(userObject);
        responseCodeIs(SC_CREATED);
        UUID userId = getSessionResponse().as(UserDto.class).getId();
        commonHelpers.updateRegistryOfDeleTables(USERS, userId);

        // now we need to mark default user_customer relationship for deletion
        UserCustomerRelationshipDto relation = relationshipHelpers.getUserCustomerRelationsForUserWithAuth(userId).get(0);
        commonHelpers.updateRegistryOfDeleTables(CUSTOMER_USERS, relation.getId());

        return getSessionResponse().as(UserDto.class).getId();
    }

    public UserDto userWithCustomerIsCreated(UserCreateDto createdUser, UUID customerId) {
        Response response = createUserWithCustomer(createdUser, customerId, true, true);
        responseCodeIs(SC_CREATED);
        return response.as(UserDto.class);
    }

    public Response getUserByUserForApp(UUID requestorId, UUID applicationVersionId, UUID userId) {
        Response response = getEntityByUserForApplication(requestorId, applicationVersionId, userId);
        setSessionResponse(response);
        return response;
    }

    public Response getAllUserPropertiesByUserForApp(UUID requestorId, UUID applicationVersionId, UUID userId) {
        Response response = getSecondLevelEntitiesByUserForApp(requestorId, applicationVersionId, userId, PROPERTIES_RESOURCE, null, null, null, null, null, null);
        setSessionResponse(response);
        return response;
    }

    public Response createUserByUserForApp(UUID requestorId, UUID applicationVersionId, UserCreateDto user) {
        return createEntityByUserForApplication(requestorId, applicationVersionId, user);
    }

    public Response updateUserByUserForApp(UUID requestorId, UUID applicationVersionId, UUID userId, UserUpdateDto userUpdate) {
        Response response = null;
        try {
            JSONObject userData = retrieveData(userUpdate);
            response = updateEntityByUserForApplication(requestorId, applicationVersionId, userId, userData.toString(), getEntityEtag(userId));
        } catch (JsonProcessingException e) {
            fail("Error while retrieving data from UserUpdate object: " + e.getMessage());
        }
        return response;
    }

    public Response deleteUserByUserForApp(UUID requestorId, UUID applicationVersionId, UUID userId) {
        return deleteEntityByUserForApplication(requestorId, applicationVersionId, userId, getEntityEtag(userId));
    }

    public void getPartnersForUserByUserForApp(UUID userId, UUID requestorId, UUID appVersionId) {
        Response response = getSecondLevelEntitiesByUserForApp(requestorId, appVersionId, userId, PARTNERS_RESOURCE, null, null, null, null, null, null);
        setSessionResponse(response);
    }

}
