package travel.snapshot.dp.qa.junit.helpers;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.tests.common.CommonTest.filterParam;
import static com.jayway.restassured.RestAssured.given;

import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;

import java.util.UUID;

@Log
public class UserHelpers extends BasicSteps {

    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();
    private final RelationshipsHelpers relationshipHelpers = new RelationshipsHelpers();

    public UserHelpers() {
        super();
        spec.basePath(USERS_PATH);
    }

    public UUID userIsCreatedWithAuth(UserCreateDto userObject) {
        UUID userId = authorizationHelpers.entityIsCreated(userObject);
        // now we need to mark default user_customer relationship for deletion
        UserCustomerRelationshipDto relation = relationshipHelpers.getDefaultUserCustomerRelationForUserWithAuth(userId);
        authorizationHelpers.updateRegistryOfDeletables(USER_CUSTOMER_RELATIONSHIPS_PATH, relation.getId());
        return userId;
    }

    public void getAllUserPropertiesByUserForApp(UUID requestorId, UUID applicationVersionId, UUID userId) {
        Response response = getSecondLevelEntitiesByUserForApp(requestorId, applicationVersionId, userId, PROPERTIES_RESOURCE, null, null, null, null, null, null);
        setSessionResponse(response);
    }

    public void deleteUserCustomerRelationshipIfExists(UUID userId) {
        getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, filterParam("user_id==" + userId))
                .stream().findFirst()
                .ifPresent(e -> entityIsDeleted(USER_CUSTOMER_RELATIONSHIPS_PATH, e.getId()));
    }

    public Response setUserPasswordByUser(UUID requestorId, UUID userId, String password) {
        Response response = given().spec(spec)
                .header(HEADER_XAUTH_USER_ID, requestorId)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
                .body(password).post("{id}/password", userId);
        return setSessionResponse(response);
    }

    public Response setUserPassword(UUID userId, String password) {
        return setUserPasswordByUser(DEFAULT_SNAPSHOT_USER_ID, userId, password);
    }

    public void getUserCustomerRelationByUserForApp(UUID requestorId, UUID appVersionId, UUID customerId, UUID targetUserId) {
        Response response = getSecondLevelEntityByUserForApp(requestorId, appVersionId, targetUserId, CUSTOMERS_RESOURCE, customerId);
        setSessionResponse(response);
    }

    public void listRolesForRelationByUserForApp(UUID requestorId,  UUID appVersionId, UUID targetUserId, String secondLevelName, UUID secondLevelId) {
        Response response = getThirdLevelEntitiesByUserForApp(requestorId, appVersionId, targetUserId, resolveObjectName(secondLevelName), secondLevelId, ROLES_RESOURCE, null, null, null, null, null, null);
        setSessionResponse(response);
    }
}
