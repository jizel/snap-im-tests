package travel.snapshot.dp.qa.serenity.users;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Role;
import travel.snapshot.dp.qa.model.User;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UsersSteps extends BasicSteps {

    private static final String SESSION_USER_ID = "user_id";

    private static final String USERS_PATH= "/identity/users";

    public UsersSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(USERS_PATH);
    }

    @Step
    public void followingUsersExist(List<User> users) {
        users.forEach(u -> {
            User existingUser = getUserByUsername(u.getUserName());
            if (existingUser != null) {
                deleteEntity(existingUser.getUserId());
            }
            Response createResponse = createEntity(u);
            if (createResponse.getStatusCode() != 201) {
                fail("User cannot be created");
            }
        });
    }

    @Step
    public void followingUserIsCreated(User user) {
        User existingUser = getUserByUsername(user.getUserName());
        if (existingUser != null) {
            deleteEntity(existingUser.getUserId());
        }
        Response response = createEntity(user);
        setSessionResponse(response);
    }

    @Step
    public void bodyContainsUserWith(String attributeName, String value) {
        Response response = getSessionResponse();
        response.then().body(attributeName, is(value));

        String userId = response.getBody().jsonPath().getString("user_id");
        setSessionVariable(SESSION_USER_ID, userId);
    }

    @Step
    public void responseContainsLocationHeader() {
        Response response = getSessionResponse();
        response.then().header("Location", USERS_PATH + "/" + Serenity.sessionVariableCalled(SESSION_USER_ID));
    }

    @Step
    public void deleteUserWithId(String userId) {
        Response response = deleteEntity(userId);
        setSessionResponse(response);
    }

    @Step
    public void deleteUserWithUserName(String userName) {
        String userId = getUserByUsername(userName).getUserId();
        Response response = deleteEntity(userId);

        setSessionResponse(response);
        setSessionVariable(SESSION_USER_ID, userId);
    }

    @Step
    public void userIdInSessionDoesNotExist() {
        String roleId = getSessionVariable(SESSION_USER_ID);

        Response response = getEntity(roleId, null);
        response.then().statusCode(404);
    }

    @Step
    public void updateUserWithUserName(String userName, User updatedUser) throws Throwable {
        User original = getUserByUsername(userName);
        Response originalResponse = getEntity(original.getUserId());

        Map<String, Object> userData = retrieveData(User.class, updatedUser);

        Response response = updateEntity(original.getUserId(), userData, originalResponse.getHeader("ETag"));
        setSessionResponse(response);
    }

    @Step
    public void userWithUserNameHasData(String userName, User user) throws Throwable {
        Map<String, Object> originalData = retrieveData(User.class, getUserByUsername(userName));
        Map<String, Object> expectedData = retrieveData(User.class, user);

        expectedData.forEach((k, v) -> {
            if (v == null) {
                assertFalse("User JSON should not contains attributes with null values", originalData.containsKey(k));
                return;
            }
            assertTrue("User has no data for attribute " + k, originalData.containsKey(k));
            assertEquals(v, originalData.get(k));
        });
    }

    @Step
    public void updateUserWithUserNameIfUpdatedBefore(String userName, User updatedUser) throws Throwable {
        User original = getUserByUsername(userName);

        Map<String, Object> userData = retrieveData(User.class, updatedUser);

        Response response = updateEntity(original.getUserId(), userData, "fake-etag");
        setSessionResponse(response);
    }

    private Response getUsers(String limit, String cursor, String filter, String sort, String sortDesc) {
        RequestSpecification requestSpecification = given().spec(spec);

        if (cursor != null) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null) {
            requestSpecification.parameter("limit", limit);
        }
        if (filter != null) {
            requestSpecification.parameter("filter", filter);
        }
        if (sort != null) {
            requestSpecification.parameter("sort", sort);
        }
        if (sortDesc != null) {
            requestSpecification.parameter("sort_desc", sortDesc);
        }
        return requestSpecification.when().get();
    }

    private User getUserByUsername(String username) {
        User[] users = getUsers("100", "0", null, null, null).as(User[].class);
        return Arrays.asList(users).stream().filter(u -> username.equals(u.getUserName())).findFirst().orElse(null);
    }
}
