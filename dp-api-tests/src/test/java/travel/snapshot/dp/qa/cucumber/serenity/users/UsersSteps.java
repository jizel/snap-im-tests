package travel.snapshot.dp.qa.cucumber.serenity.users;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.model.UserUpdateDto.UserType.CUSTOMER;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.RoleBaseDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.RoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UsersSteps extends BasicSteps {

    private static final String SESSION_USER_ID = "user_id";
    private static final String SESSION_CREATED_USER = "created_user";


    public UsersSteps() {
        super();
        spec.baseUri(propertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(USERS_PATH);
    }

    public void followingUsersExist(List<UserCreateDto> users, UUID customerId, Boolean isPrimary, Boolean isActive) {
        users.forEach(user -> {
            UserCustomerRelationshipPartialDto relation = new UserCustomerRelationshipPartialDto();
            relation.setCustomerId(customerId);
            relation.setIsPrimary(isPrimary);
            relation.setIsActive(isActive);
            user.setUserCustomerRelationship(relation);

            Response createResp = createEntity(user);
            if (createResp.getStatusCode() != SC_CREATED) {
                fail("User cannot be created! Status:" + createResp.getStatusCode() + " " + createResp.getBody().asString());
            }
        });
    }

    @Step
    public Response createUserWithCustomer(UserCreateDto user, UUID customerId, Boolean isPrimary, Boolean isActive) {
        if (customerId != null) {
            UserCustomerRelationshipPartialDto relation = new UserCustomerRelationshipPartialDto();
            relation.setCustomerId(customerId);
            relation.setIsPrimary(isPrimary);
            relation.setIsActive(isActive);
            user.setUserCustomerRelationship(relation);
        } else {
            if (user.getType().equals(CUSTOMER)) {
                fail("Please either provide CustomerId, or change userType to \"partner\" or \"snapshot\"");
            }
        }
        Response response = createEntity(user);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response createUser(UserCreateDto user) {
        Response response = createEntity(user);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response createUserByUser(UUID requestorID, UserCreateDto user) {
        Response response = createEntityByUser(requestorID, user);
        setSessionResponse(response);
        return response;
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
                .body("user_name", is(originalUser.getUsername()))
                .body("first_name", is(originalUser.getFirstName()))
                .body("last_name", is(originalUser.getLastName()))
                .body("email", is(originalUser.getEmail()))
                .body("phone", is(originalUser.getPhone()))
                .body("culture", is(originalUser.getLanguageCode()))
                .body("timezone", is(originalUser.getTimezone()));

    }

    @Step
    public void deleteUser(UUID userId) {
        setSessionVariable(SESSION_USER_ID, userId);
        String etag = getEntityEtag(userId);
        deleteUserWithEtag(userId, etag);
    }

    @Step
    public void deleteUserWithEtag(UUID userId, String etag) {
        Response response = deleteEntity(userId, etag);
        setSessionResponse(response);
    }

    @Step
    public void userIdInSessionDoesNotExist() {
        UUID roleId = getSessionVariable(SESSION_USER_ID);

        Response response = getEntity(roleId);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

//    Delete?

    @Step
    public void updateUser(UUID userId, UserUpdateDto updatedUser) {
        updateUserByUser(DEFAULT_SNAPSHOT_USER_ID, userId, updatedUser);
    }

    @Step
    public void updateUserByUser(UUID performerId, UUID targetId, UserUpdateDto updatedUser) {
        try {
            Response originalResponse = getEntity( targetId );
            JSONObject userData = retrieveData( updatedUser );
            Response response = updateEntityByUser( performerId, targetId, userData.toString(), originalResponse.getHeader( HEADER_ETAG ) );
            setSessionResponse( response );
        } catch(JsonProcessingException jsonException) {
            fail("Error while converting object to JSON: " + jsonException);
        }
    }

    @Step
    public void userWithUserNameHasData(String userName, UserDto user) throws Throwable {
        Map<String, Object> originalData = retrieveDataOld(UserDto.class, getUserByUsername(userName));
        Map<String, Object> expectedData = retrieveDataOld(UserDto.class, user);

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

        Map<String, Object> userData = retrieveDataOld(UserDto.class, updatedUser);

        Response response = updateEntity(original.getId(), userData, "fake-etag");
        setSessionResponse(response);
    }


    public UserDto getUserByUsername(String username) {
        UserDto[] users = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_name==" + username, null, null, null).as(UserDto[].class);
        return stream(users).findFirst().orElse(null);
    }

    @Step
    public void userWithUsernameIsGot(String username) {
        UserDto user = getUserByUsername(username);
        Response response = getEntity(user.getId());
        setSessionResponse(response);
    }

    public void userWithIdIsGot(UUID userId) {
        Response response = getEntity(userId);
        setSessionResponse(response);
    }

    public void listOfUsersIsGotByUser(UUID requestorId, String limit, String cursor, String filter, String sort, String sortDesc) {
        listOfUsersIsGotByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, limit, cursor, filter, sort, sortDesc);
    }

    public void listOfUsersIsGotByUserForApp(UUID requestorId, UUID appVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUserForApp(requestorId, appVersionId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        UserDto[] users = response.as(UserDto[].class);
        int i = 0;
        for (UserDto u : users) {
            assertEquals("User on index=" + i + " is not expected", usernames.get(i), u.getUsername());
            i++;
        }
    }

    @Step
    public Response createRoleBetweenUserAndEntity(String entityName, UUID roleId, UUID userId, UUID entityId, Boolean isActive) {
        Response createResponse = addRoleToUserEntity(roleId, userId, entityId, entityName);
        setSessionResponse(createResponse);
        return createResponse;
    }

    @Step
    public void roleExistsBetweenUserAndEntity(String entityName, UUID roleId, UUID  userId, UUID entityId, Boolean isActive) {
        Response response = createRoleBetweenUserAndEntity(entityName, roleId, userId, entityId, isActive);
        assertThat("Role can not be assigned: " + response.body().toString(), response.statusCode(), is(SC_CREATED));
    }

    private Response deleteRoleFromUserWithRelationshipTypeEntity(UUID roleId, UUID userId, String relationshipType, UUID entityId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("relationship_type", relationshipType);
        queryParams.put("relationship_id", entityId.toString());
        return deleteSecondLevelEntity(userId, ROLES_RESOURCE, roleId, queryParams);
    }

    private Response addRoleToUserEntity(UUID roleId, UUID userId, UUID entityId, String entityName) {
        String path = buildPathForRoles(entityName, userId, entityId);
        RoleRelationshipDto role = new RoleRelationshipDto();
        role.setRoleId(roleId);
        JSONObject jsonRole = null;
        try {
            jsonRole = retrieveData(role);
        }
        catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from RoleRelationshipDto object");
        }
        return given().spec(spec).header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID)
                .body(jsonRole.toString())
                .when().post(path);
    }

    private RoleDto getRoleForUserWithRelationshipTypeEntity(UUID roleId, UUID userId, String relationshipType, UUID entityId) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("relationship_type", relationshipType);
        queryParams.put("relationship_id", entityId.toString());
        Response userRolesResponse = getSecondLevelEntities(userId, ROLES_RESOURCE, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "role_id==" + roleId, null, null, queryParams);
        return stream((userRolesResponse.as(RoleDto[].class))).findFirst().orElse(null);
    }

    private RoleDto getRoleForUserEntity(UUID roleId, UUID userId, UUID entityId, String entityName) {
        String path = buildPathForRoles(entityName, userId, entityId);
        Response userRolesUserEntity = getEntities(path, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "role_id==" + roleId, null, null, null);
        return stream((userRolesUserEntity.as(RoleDto[].class))).findFirst().orElse(null);
    }

    public void roleIsDeletedFromUserWithRelationshipTypeEntity(RoleBaseDto r, String username, String relationshipType, UUID entityId) {
        UserDto u = getUserByUsername(username);
        Response deleteResponse = deleteRoleFromUserWithRelationshipTypeEntity(r.getId(), u.getId(), relationshipType, entityId);
        setSessionResponse(deleteResponse);
    }

    public void roleDoesntExistForUserWithRelationshipTypeEntity(RoleBaseDto r, String username, String relationshipType, UUID entityId) {
        UserDto u = getUserByUsername(username);
        RoleDto existingUserRole = getRoleForUserWithRelationshipTypeEntity(r.getId(), u.getId(), relationshipType, entityId);
        assertNull("Role should not be present for User", existingUserRole);
    }

    public void listOfRolesIsGotForRelationshipTypeEntityWIth(String username, String relationshipType, UUID entityId, String limit, String cursor, String filter, String sort, String sortDesc) {
        UserDto u = getUserByUsername(username);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("relationship_type", relationshipType);
        queryParams.put("relationship_id", entityId.toString());
        Response response = getSecondLevelEntities(u.getId(), ROLES_RESOURCE, limit, cursor, filter, sort, sortDesc, queryParams);
        setSessionResponse(response);
    }

    public void rolenamesAreInResponseInOrder(List<String> rolenames) {
        Response response = getSessionResponse();
        RoleDto[] userRoles = response.as(RoleDto[].class);
        int i = 0;
        for (RoleDto ur : userRoles) {
            assertEquals("UserRole on index=" + i + " is not expected", rolenames.get(i), ur.getName());
            i++;
        }
    }

    public Response setUserPasswordByUser(UUID requestorId, UUID userId, String password) {
        Response response = given().spec(spec)
                .header(HEADER_XAUTH_USER_ID, requestorId)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
                .body(password).post("{id}/password", userId);
        setSessionResponse(response);
        return response;
    }

    public Response setUserPassword(UUID userId, String password) {
        return setUserPasswordByUser(DEFAULT_SNAPSHOT_USER_ID, userId, password);
    }

    @Step
    public void setUserIsActive(UUID userId, Boolean isActive) throws Throwable {
        UserUpdateDto userUpdate = new UserCreateDto();
        userUpdate.setIsActive(isActive);
        updateUser(userId, userUpdate);
    }

    @Step
    public Boolean getUserIsActive(UUID userId){
        return getUserById(userId).getIsActive();
    }

    @Step
    public UserDto getUserById(UUID userId) {
        Response response = getEntity(userId);
        UserDto user = response.as(UserDto.class);
        setSessionResponse(response);
        return user;
    }

    @Step
    public void roleBetweenUserAndEntityIsDeleted(String entityName, UUID roleId, UUID userId, UUID entityId, String nonExistent) {
        String etag;
        if (nonExistent == null) {
            etag = getThirdLevelEntityEtag(userId, entityName, entityId, ROLES_RESOURCE, roleId);
        } else {
            etag = DEFAULT_SNAPSHOT_ETAG;
        }
        Response deleteResponse = deleteThirdLevelEntity(userId, entityName, entityId, ROLES_RESOURCE, roleId, etag);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void roleBetweenUserAndEntityNotExists(String entityName, UUID roleId, String userName, UUID entityId) {
        RoleDto roleDto = getRoleForUserEntity(roleId, resolveUserId(userName), entityId, entityName);
        if (roleDto != null) {
            fail("Role exists and should not ! Role id = " + roleId);
        }
    }

    @Step
    public void roleNameExistsBetweenUserAndEntity(String entityName, UUID roleId, UUID userId, UUID entityId, Boolean isActive) {
        Response resp = addRoleToUserEntity(roleId, userId, entityId, entityName);
        assertTrue("Failed to add role to relationship between user and entity id " + entityId.toString(), resp.statusCode() == SC_CREATED);
    }

    @Step
    public void getRolesBetweenUserAndEntity(String entityName, UUID userId, UUID entityId, String limit, String cursor, String filter, String sort, String sortDesc) {
        String path = buildPathForRoles(entityName, userId, entityId);
        Response resp = getEntities(path, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(resp);
    }

    @Step
    public void updateUserPropertyRelationship(UUID userId, UUID propertyId, UserPropertyRelationshipUpdateDto userPropertyRelationship) {
        updateUserPropertyRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, userId, propertyId, userPropertyRelationship);
    }

    @Step
    public void updateUserPropertyRelationshipByUser(UUID performerId, UUID userId, UUID propertyId, UserPropertyRelationshipUpdateDto userPropertyRelationship) {
        try {
            JSONObject jsonUpdate = retrieveData(userPropertyRelationship);
            jsonUpdate.remove(SESSION_USER_ID);
            String etag = getSecondLevelEntityEtag(userId, PROPERTIES_RESOURCE, propertyId);
            Response response = updateSecondLevelEntityByUser(performerId, userId, PROPERTIES_RESOURCE, propertyId, jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from UserPropertyRelationshipUpdateDto object");
        }
    }

    @Step
    public Response addPropertySetToUser(UUID propertySetId, UUID userId){
        Response response =  addPropertySetToUserByUser(DEFAULT_SNAPSHOT_USER_ID, propertySetId, userId);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response addPropertySetToUserByUser(UUID performerId, UUID propertySetId, UUID userId){
        return createSecondLevelRelationshipByUser(performerId, userId, PROPERTY_SETS_RESOURCE, singletonMap("property_set_id", propertySetId));
    }

    private String buildPathForRoles(String entityName, UUID userId, UUID entityId) {
        return String.format("%s/%s/%s/%s", userId, entityName, entityId, ROLES_RESOURCE);
    }

    public void createUserForCustomerByUser(UUID performerId, UUID customerId, UserCreateDto user, Boolean isPrimary) {
        UserCustomerRelationshipPartialDto relation = new UserCustomerRelationshipPartialDto();
        relation.setCustomerId(customerId);
        relation.setIsPrimary(isPrimary);
        user.setUserCustomerRelationship(relation);
        Response createResp = createEntityByUser(performerId, user);
        setSessionResponse(createResp);
    }

    public void deleteUserByUser(UUID performerId, UUID targetId) {
        String etag = getEntityEtag(targetId);
        deleteEntityByUser( performerId, targetId, etag);
    }

    public void getUserCustomerRelationByUserForApp(UUID requestorId, UUID appVersionId, UUID customerId, UUID targetUserId) {
        Response response = getSecondLevelEntityByUserForApp(requestorId, appVersionId, targetUserId, CUSTOMERS_RESOURCE, customerId);
        setSessionResponse(response);
    }

    public void listUserCustomersByUser(UUID requestorId, UUID targetUserId) {
        Response response = getSecondLevelEntitiesByUser(requestorId, targetUserId, CUSTOMERS_RESOURCE, null, null, null, null, null, null);
        setSessionResponse(response);
    }

    public void listRolesForRelationByUserForApp(UUID requestorId,  UUID appVersionId, UUID targetUserId, String secondLevelName, UUID secondLevelId) {
        Response response = getThirdLevelEntitiesByUserForApp(requestorId, appVersionId, targetUserId, resolveObjectName(secondLevelName), secondLevelId, ROLES_RESOURCE, null, null, null, null, null, null);
        setSessionResponse(response);
    }

    public UUID resolveUserId(String userName) {
        if (userName == null) return DEFAULT_SNAPSHOT_USER_ID;

        UUID userId;
        if (isUUID(userName)) {
            userId = UUID.fromString(userName);
        } else {
            UserDto user = getUserByUsername(userName);
            assertThat(String.format("User with username \"%s\" does not exist", userName), user, is(notNullValue()));
            userId = user.getId();
        }
        return userId;
    }

    public Map<String, UUID> getUsersIds(String requestorName, String targetName) {
        Map<String, UUID> userIdMap = new HashMap<>();
        userIdMap.put(REQUESTOR_ID, resolveUserId(requestorName));
        userIdMap.put(TARGET_ID, resolveUserId(targetName));
        return userIdMap;
    }

    public void userAssignsRoleToRelationWithApp(UUID requestorId, UUID appVersionId, UUID targetUserId, String secondLevelName, UUID secondLevelId, UUID roleId) {
        Response response = createThirdLevelEntityByUserForApplication(requestorId, appVersionId, targetUserId, resolveObjectName(secondLevelName), secondLevelId, ROLES_RESOURCE, singletonMap(ROLE_ID, roleId));
        setSessionResponse(response);
    }

    public void userDeletesRoleFromRelationWithApp(UUID requestorId, UUID appVersionId, UUID targetUserId, String secondLevelName, UUID secondLevelId, UUID roleId) {
        String eTag = getThirdLevelEntityEtag(targetUserId, resolveObjectName(secondLevelName), secondLevelId, ROLES_RESOURCE, roleId);
        Response response = deleteThirdLevelEntityByUserForApplication(requestorId, appVersionId, targetUserId, resolveObjectName(secondLevelName), secondLevelId, ROLES_RESOURCE, roleId, eTag);
        setSessionResponse(response);
    }

    @Step
    public UserCustomerRelationshipPartialDto getCustomerForUser(UUID userId, UUID customerId) {
        Response userCustomerResponse = getSecondLevelEntities(userId, CUSTOMERS_RESOURCE, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "customer_id==" + customerId, null, null, null);
        return stream(userCustomerResponse.as(UserCustomerRelationshipPartialDto[].class)).findFirst().orElse(null);
    }

    @Step
    public Response deleteUserPartnerRelationship(UUID userId, UUID partnerId){
        return deleteSecondLevelEntity(userId, PARTNERS_RESOURCE, partnerId, null);
    }

    @Step
    public void userPartnerRelationshipExists(UUID userId, UUID partnerId) {
        createUserPartnerPartialRelationship(userId, partnerId);
        Response response = getSessionResponse();
        assertThat("Failed to create relationship: " + response.body().toString(), response.statusCode(), is(SC_CREATED));

    }

    @Step
    public void createUserPartnerPartialRelationship(UUID userId, UUID partnerId){
        UserPartnerRelationshipPartialDto relation = new UserPartnerRelationshipPartialDto();
        relation.setPartnerId(partnerId);
        JSONObject jsonRelation = null;
        try {
            jsonRelation = retrieveData(relation);
        } catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from UserPartnerRelationshipPartialDto object");
        }
        Response response = createSecondLevelRelationship(userId, PARTNERS_RESOURCE, jsonRelation.toString());
        setSessionResponse(response);
    }
}
