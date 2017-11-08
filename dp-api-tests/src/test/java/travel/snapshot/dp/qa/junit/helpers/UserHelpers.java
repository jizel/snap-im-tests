package travel.snapshot.dp.qa.junit.helpers;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;

import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.qa.cucumber.serenity.users.UsersSteps;

import java.util.UUID;

@Log
public class UserHelpers extends UsersSteps {

    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();
    private final RelationshipsHelpers relationshipHelpers = new RelationshipsHelpers();

    public UserHelpers() {
        super();
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

    public void deleteExistingUserCustomerRelationship(UUID userId) {
        UUID existingRelationshipId = getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class,
                buildQueryParamMapForPaging(null, null, "user_id==" + userId.toString(), null, null, null))
                .get(0).getId();
        entityIsDeleted(USER_CUSTOMER_RELATIONSHIPS_PATH, existingRelationshipId);
    }

}
