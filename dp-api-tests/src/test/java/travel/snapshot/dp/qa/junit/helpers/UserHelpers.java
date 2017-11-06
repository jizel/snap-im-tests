package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;

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
    private final RelationshipsHelpers relationshipHelpers = new RelationshipsHelpers();

    public UserHelpers() {
        super();
    }

    public UserDto userIsCreated(UserCreateDto createdUser) {
        Response response = createUser(createdUser);
        responseCodeIs(SC_CREATED);
        return response.as(UserDto.class);
    }


    public UUID userIsCreatedWithAuth(UserCreateDto userObject) {
        UUID userId = authorizationHelpers.entityIsCreated(userObject);
        // now we need to mark default user_customer relationship for deletion
        UserCustomerRelationshipDto relation = relationshipHelpers.getUserCustomerRelationsForUserWithAuth(userId).get(0);
        authorizationHelpers.updateRegistryOfDeletables(USER_CUSTOMER_RELATIONSHIPS_PATH, relation.getId());
        return userId;
    }

    public void getAllUserPropertiesByUserForApp(UUID requestorId, UUID applicationVersionId, UUID userId) {
        Response response = getSecondLevelEntitiesByUserForApp(requestorId, applicationVersionId, userId, PROPERTIES_RESOURCE, null, null, null, null, null, null);
        setSessionResponse(response);
    }

    public void createUserByUserForApp(UUID requestorId, UUID applicationVersionId, UserCreateDto user) {
        createEntityByUserForApplication(requestorId, applicationVersionId, user);
    }

    public void updateUserByUserForApp(UUID requestorId, UUID applicationVersionId, UUID userId, UserUpdateDto userUpdate) {
        try {
            JSONObject userData = retrieveData(userUpdate);
            updateEntityByUserForApplication(requestorId, applicationVersionId, userId, userData.toString(), getEntityEtag(userId));
        } catch (JsonProcessingException e) {
            fail("Error while retrieving data from UserUpdate object: " + e.getMessage());
        }
    }

    public void deleteUserByUserForApp(UUID requestorId, UUID applicationVersionId, UUID userId) {
        deleteEntityByUserForApplication(requestorId, applicationVersionId, userId, getEntityEtag(userId));
    }

    public void deleteExistingUserCustomerRelationship(UUID userId) {
        UUID existingRelationshipId = getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class,
                buildQueryParamMapForPaging(null, null, "user_id==" + userId.toString(), null, null, null))
                .get(0).getId();
        entityIsDeleted(USER_CUSTOMER_RELATIONSHIPS_PATH, existingRelationshipId);
    }

}
