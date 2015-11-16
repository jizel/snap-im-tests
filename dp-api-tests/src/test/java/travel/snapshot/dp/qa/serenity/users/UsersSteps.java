package travel.snapshot.dp.qa.serenity.users;

import com.jayway.restassured.response.Response;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
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
    private static final String SESSION_CREATED_USER = "created_user";

    private static final String USERS_PATH = "/identity/users";

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
        Serenity.setSessionVariable(SESSION_CREATED_USER).to(user);
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
    public void compareUserOnHeaderWithStored(String headerName) {
        User originalUser = getSessionVariable(SESSION_CREATED_USER);
        Response response = getSessionResponse();
        String customerLocation = response.header(headerName).replaceFirst(USERS_PATH, "");
        given().spec(spec).get(customerLocation).then()
                .body("user_name", is(originalUser.getUserName()))
                .body("first_name", is(originalUser.getFirstName()))
                .body("last_name", is(originalUser.getLastName()))
                .body("email", is(originalUser.getEmail()))
                .body("phone", is(originalUser.getPhone()))
                .body("culture", is(originalUser.getCulture()))
                .body("timezone", is(originalUser.getTimezone()));

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

    public User getUserByUsername(String username) {
        User[] users = getEntities("1", "0", "user_name==" + username, null, null).as(User[].class);
        return Arrays.asList(users).stream().findFirst().orElse(null);
    }


    @Step
    public void userWithUsernameIsGot(String username) {
        User user = getUserByUsername(username);
        Response response = getEntity(user.getUserId(), null);
        setSessionResponse(response);
    }

    public void userWithUsernameIsGotWithEtag(String username) {
        User user = getUserByUsername(username);
        Response tempResponse = getEntity(user.getUserId(), null);
        Response response = getEntity(user.getUserId(), tempResponse.getHeader("ETag"));
        setSessionResponse(response);
    }

    public void userWithUsernameIsGotWithEtagAfterUpdate(String username) {
        User user = getUserByUsername(username);
        Response tempResponse = getEntity(user.getUserId(), null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("culture", "sk");

        Response updateResponse = updateEntity(user.getUserId(), mapForUpdate, tempResponse.getHeader("ETag"));

        if (updateResponse.getStatusCode() != 204) {
            fail("User cannot be updated: " + updateResponse.asString());
        }

        Response response = getEntity(user.getUserId(), tempResponse.getHeader("ETag"));
        setSessionResponse(response);
    }

    public void userWithIdIsGot(String userId) {
        Response response = getEntity(userId, null);
        setSessionResponse(response);
    }

    public void listOfUsersIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        User[] users = response.as(User[].class);
        int i = 0;
        for (User u : users) {
            assertEquals("User on index=" + i + " is not expected", usernames.get(i), u.getUserName());
            i++;
        }

    }
}
