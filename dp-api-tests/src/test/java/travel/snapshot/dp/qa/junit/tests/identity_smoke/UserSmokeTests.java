package travel.snapshot.dp.qa.junit.tests.identity_smoke;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_ENCRYPTED_PASSWORD;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PASSWORD;
import static travel.snapshot.dp.qa.junit.tests.Tags.AUTHORIZATION_TEST;

import org.junit.Test;
import org.junit.jupiter.api.Tag;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;

import java.util.UUID;

@Tag(AUTHORIZATION_TEST)
public class UserSmokeTests extends CommonSmokeTest {
    protected UUID userId;
    protected UUID userCustomerRelationId;


    @Test
    public void userCRUD() throws Throwable {
        // create
        UUID userId = userHelpers.userIsCreatedWithAuth(testUser1);
        userCustomerRelationId = relationshipsHelpers
               .getDefaultUserCustomerRelationForUserWithAuth(userId)
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

    @Test
    public void loginWithPlus() {
        String username = "test+1@snapshot.travel";
        testUser1.setUsername(username);
        userId = userHelpers.userIsCreatedWithAuth(testUser1);
        dbSteps.setUserPassword(userId, DEFAULT_ENCRYPTED_PASSWORD);
        authorizationSteps.getToken(username, DEFAULT_PASSWORD, clientId, clientSecret)
                .then()
                .statusCode(SC_OK);
    }
}
