package travel.snapshot.dp.qa.serenity.users;

import com.jayway.restassured.response.Response;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.RoleViewDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    public void followingUsersExist(List<UserDto> users) {
        users.forEach(u -> {
            UserDto existingUser = getUserByUsername(u.getUserName());
            if (existingUser != null) {
                deleteEntity(existingUser.getUserId());
            }
            Response createResponse = createEntity(u);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("User cannot be created");
            }
        });
    }

    @Step
    public void followingUserIsCreated(UserDto user) {
        UserDto existingUser = null;
        if (user != null && !user.getUserName().isEmpty()) {
            existingUser = getUserByUsername(user.getUserName());
        }
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
        UserDto originalUser = getSessionVariable(SESSION_CREATED_USER);
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
        UserDto u = getUserByUsername(userName);
        if (u == null) {
            return;
        }
        String userId = u.getUserId();
        Response response = deleteEntity(userId);

        setSessionResponse(response);
        setSessionVariable(SESSION_USER_ID, userId);
    }

    @Step
    public void userIdInSessionDoesNotExist() {
        String roleId = getSessionVariable(SESSION_USER_ID);

        Response response = getEntity(roleId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void updateUserWithUserName(String userName, UserDto updatedUser) throws Throwable {
        UserDto original = getUserByUsername(userName);
        Response originalResponse = getEntity(original.getUserId());

        Map<String, Object> userData = retrieveData(UserDto.class, updatedUser);

        Response response = updateEntity(original.getUserId(), userData, originalResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void userWithUserNameHasData(String userName, UserDto user) throws Throwable {
        Map<String, Object> originalData = retrieveData(UserDto.class, getUserByUsername(userName));
        Map<String, Object> expectedData = retrieveData(UserDto.class, user);

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
    public void updateUserWithUserNameIfUpdatedBefore(String userName, UserDto updatedUser) throws Throwable {
        UserDto original = getUserByUsername(userName);

        Map<String, Object> userData = retrieveData(UserDto.class, updatedUser);

        Response response = updateEntity(original.getUserId(), userData, "fake-etag");
        setSessionResponse(response);
    }

    public UserDto getUserByUsername(String username) {
        UserDto[] users = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_name==" + username, null, null).as(UserDto[].class);
        return Arrays.asList(users).stream().findFirst().orElse(null);
    }


    @Step
    public void userWithUsernameIsGot(String username) {
        UserDto user = getUserByUsername(username);
        Response response = getEntity(user.getUserId(), null);
        setSessionResponse(response);
    }

    public void userWithUsernameIsGotWithEtag(String username) {
        UserDto user = getUserByUsername(username);
        Response tempResponse = getEntity(user.getUserId(), null);
        Response response = getEntity(user.getUserId(), tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    public void userWithUsernameIsGotWithEtagAfterUpdate(String username) {
        UserDto user = getUserByUsername(username);
        Response tempResponse = getEntity(user.getUserId(), null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("culture", "sk-SK");

        Response updateResponse = updateEntity(user.getUserId(), mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("User cannot be updated: " + updateResponse.asString());
        }

        Response response = getEntity(user.getUserId(), tempResponse.getHeader(HEADER_ETAG));
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
        UserDto[] users = response.as(UserDto[].class);
        int i = 0;
        for (UserDto u : users) {
            assertEquals("User on index=" + i + " is not expected", usernames.get(i), u.getUserName());
            i++;
        }

    }

    public void roleIsAddedToUserWithRelationshipTypeEntity(RoleDto r, String username, String relationshipType, String entityId) {
        UserDto u = getUserByUsername(username);

        Response response = addRoleToUserWithRelationshipTypeEntity(r.getRoleId(), u.getUserId(), relationshipType, entityId);
        setSessionResponse(response);
    }


    public void relationExistsBetweenRoleAndUserWithRelationshipTypeEntity(RoleDto r, String username, String relationshipType, String entityId) {
        UserDto u = getUserByUsername(username);

        RoleViewDto existingUserRole = getRoleForUserWithRelationshipTypeEntity(r.getRoleId(), u.getUserId(), relationshipType, entityId);
        if (existingUserRole != null) {

            Response deleteResponse = deleteRoleFromUserWithRelationshipTypeEntity(r.getRoleId(), u.getUserId(), relationshipType, entityId);
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("PropertyUser cannot be deleted - status: " + deleteResponse.getStatusCode() + ", " + deleteResponse.asString());
            }
        }
        Response createResponse = addRoleToUserWithRelationshipTypeEntity(r.getRoleId(), u.getUserId(), relationshipType, entityId);
        if (createResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("PropertyUser cannot be created - status: " + createResponse.getStatusCode() + ", " + createResponse.asString());
        }
    }

    private Response deleteRoleFromUserWithRelationshipTypeEntity(String roleId, String userId, String relationshipType, String entityId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("relationship_type", relationshipType);
        queryParams.put("relationship_id", entityId);
        return deleteSecondLevelEntity(userId, SECOND_LEVEL_OBJECT_ROLES, roleId, queryParams);
    }

    private Response addRoleToUserWithRelationshipTypeEntity(String roleId, String userId, String relationshipType, String entityId) {
        Map<String, String> data = new HashMap<>();
        data.put("relationship_type", relationshipType);
        data.put("relationship_id", entityId);
        data.put("role_id", roleId);

        return given().spec(spec)
                .body(data)
                .when().post("/{userId}/roles", userId);
    }

    private RoleViewDto getRoleForUserWithRelationshipTypeEntity(String roleId, String userId, String relationshipType, String entityId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("relationship_type", relationshipType);
        queryParams.put("relationship_id", entityId);
        Response userRolesResponse = getSecondLevelEntities(userId, SECOND_LEVEL_OBJECT_ROLES, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "role_id==" + roleId, null, null, queryParams);
        return Arrays.asList(userRolesResponse.as(RoleViewDto[].class)).stream().findFirst().orElse(null);
    }

    public void roleIsDeletedFromUserWithRelationshipTypeEntity(RoleDto r, String username, String relationshipType, String entityId) {
        UserDto u = getUserByUsername(username);
        Response deleteResponse = deleteRoleFromUserWithRelationshipTypeEntity(r.getRoleId(), u.getUserId(), relationshipType, entityId);
        setSessionResponse(deleteResponse);
    }

    public void roleDoesntExistForUserWithRelationshipTypeEntity(RoleDto r, String username, String relationshipType, String entityId) {
        UserDto u = getUserByUsername(username);
        RoleViewDto existingUserRole = getRoleForUserWithRelationshipTypeEntity(r.getRoleId(), u.getUserId(), relationshipType, entityId);
        assertNull("Role should not be present for User", existingUserRole);
    }

    public void listOfRolesIsGotForRelationshipTypeEntityWIth(String username, String relationshipType, String entityId, String limit, String cursor, String filter, String sort, String sortDesc) {
        UserDto u = getUserByUsername(username);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("relationship_type", relationshipType);
        queryParams.put("relationship_id", entityId);
        Response response = getSecondLevelEntities(u.getUserId(), SECOND_LEVEL_OBJECT_ROLES, limit, cursor, filter, sort, sortDesc, queryParams);
        setSessionResponse(response);
    }

    public void rolenamesAreInResponseInOrder(List<String> rolenames) {
        Response response = getSessionResponse();
        RoleViewDto[] userRoles = response.as(RoleViewDto[].class);
        int i = 0;
        for (RoleViewDto ur : userRoles) {
            assertEquals("UserRole on index=" + i + " is not expected", rolenames.get(i), ur.getRoleName());
            i++;
        }
    }

    public void setUserPasswordByUsername(String username, String password) {
        UserDto u = getUserByUsername(username);
        setUserPassword(u.getUserId(), password);
    }

    public void setUserPassword(String id, String password) {
        given().spec(spec).body(password).post("{id}/password", id);
    }

    @Step
    public void activateUserWithName(String username) {
        UserDto user = getUserByUsername(username);
        String id = user.getUserId();
        Response response = activateUser(id);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void inactivateUserWithName(String username) {
        UserDto user = getUserByUsername(username);
        String id = user.getUserId();
        Response response = inactivateUser(id);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    public Response activateUser(String id) {
        return given().spec(spec).basePath(USERS_PATH)
                .when().post("/{id}/active", id);
    }

    public Response inactivateUser(String id) {
        return given().spec(spec).basePath(USERS_PATH)
                .when().post("/{id}/inactive", id);
    }

    public void inactivateNotExistingUser(String id) {
        Response response = inactivateUser(id);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    public void activateNotExistingUser(String id) {
        Response response = inactivateUser(id);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void isActiveSetTo(boolean activeFlag, String name) {
        UserDto user = getUserByUsername(name);

        if (activeFlag) {
            assertNotNull("user should be returned", user);
            assertEquals("user should have name=" + name, name, user.getUserName());
            assertEquals("is_active parameter should be set to 0", Integer.valueOf(1), user.getIsActive());
        } else {
            assertNotNull("user should be returned", user);
            assertEquals("is_active parameter should be set to 0", Integer.valueOf(0), user.getIsActive());
        }
    }

}
