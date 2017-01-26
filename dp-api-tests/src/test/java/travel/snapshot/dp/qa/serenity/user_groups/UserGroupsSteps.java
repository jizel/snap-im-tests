package travel.snapshot.dp.qa.serenity.user_groups;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.RoleIdDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipUpdateDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.RegexValueConverter;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class UserGroupsSteps extends BasicSteps {

    private static final String USER_GROUPS_PATH = "/identity/user_groups";

    public UserGroupsSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(USER_GROUPS_PATH);
    }

    public void followingUserGroupIsCreated(UserGroupDto userGroup) throws JsonProcessingException {
        JSONObject removeNulls = retrieveData(userGroup);
        JSONObject getRegexExample = RegexValueConverter.transform(removeNulls);
        Response response = createEntity(getRegexExample.toString());
        setSessionResponse(response);
    }

    public void followingUserGroupIsCreatedByUser(String userId, UserGroupDto userGroup) {
        Response createResponse = createEntityByUser(userId, userGroup);
        setSessionResponse(createResponse);
    }

    public void followingUserGroupsExist(List<UserGroupDto> userGroups) {
        userGroups.forEach(userGroup ->
        {
            Response createResponse = createEntity(userGroup);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("User group cannot be created: " + createResponse.getStatusCode() + " " + createResponse.getBody().asString());
            }
        });
    }

    @Step
    public UserGroupDto getUserGroup(String userGroupId) {
        return getUserGroupByUser(DEFAULT_SNAPSHOT_USER_ID, userGroupId);
    }

    @Step
    public UserGroupDto getUserGroupByUser(String userId, String userGroupId) {
        Response response = getEntityByUser(userId, userGroupId);
        setSessionResponse(response);
        if (response.getStatusCode() == HttpStatus.SC_OK){
            return response.as(UserGroupDto.class);
        }
        return null;
    }

    public void userGroupWithIdGotWithEtag(String userGroupId) {
        Response tempResponse = getEntity(userGroupId);
        Response resp = getEntity(userGroupId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    public void userGroupWithIdIsGotWithEtagAfterUpdate(String userGroupId) throws JsonProcessingException {
        Response tempResponse = getEntity(userGroupId);
        UserGroupUpdateDto userGroupUpdate = new UserGroupUpdateDto();
        userGroupUpdate.setDescription("updatedDescription");

        Response updateResponse = updateEntity(userGroupId, retrieveData(userGroupUpdate).toString(), tempResponse.getHeader(HEADER_ETAG));

        Response resp = getEntity(userGroupId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void listUserGroupsIsGot(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listUserGroupsIsGotByUser(String userId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUser(userId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listGroupRoleIsGot(String userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(userGroupId, SECOND_LEVEL_OBJECT_ROLES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listGroupRolesIsGotByUser(String userId, String userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_ROLES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public Response listOfUserGroupPropertiesIsGotByUser(String userId, String userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response listOfUserGroupPropertySetsIsGotByUser(String userId, String userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response listOfUserGroupUsersIsGotByUser(String userId, String userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response listOfUserGroupPropertyRolesIsGotByUser(String userId, String userGroupId, String propertyId){
        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).when().get(userGroupId + "/properties/" + propertyId + "/roles");
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response listOfUserGroupPropertySetRolesIsGotByUser(String userId, String userGroupId, String propertyId){
        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).when().get(userGroupId + "/property_sets/" + propertyId + "/roles");
        setSessionResponse(response);
        return response;
    }

    public void responseSortByDescription(List<String> order) {
        if (order.isEmpty()) {
            return;
        }

        UserGroupDto[] userGroups = getSessionResponse().as(UserGroupDto[].class);
        int i = 0;
        for (UserGroupDto api : userGroups) {
            assertEquals(api.getDescription(), order.get(i));
            i++;
        }
    }

    public void responseSortById(List<String> order) {
        if (order.isEmpty()) {
            return;
        }

        RoleIdDto[] roles = getSessionResponse().as(RoleIdDto[].class);
        int i = 0;
        for (RoleIdDto r : roles) {
            if (!r.getRoleId().startsWith(order.get(i))) {
                fail("Expected ID: " + r.getRoleId() + "but was starting with: " + order.get(i));
            }
            i++;
        }
    }

    public void setUserGroupActiveField(String userGroupId, boolean shouldBeActive) throws JsonProcessingException {
        UserGroupUpdateDto userGroupUpdate = new UserGroupUpdateDto();
        userGroupUpdate.setIsActive(shouldBeActive);
        this.updateUserGroup(userGroupId, userGroupUpdate);
    }

    public void checkUserGroupActiveField(String userGroupId, boolean b) {
        UserGroupDto userGroup = getEntity(userGroupId).as(UserGroupDto.class);
        assertEquals(userGroup.getIsActive(), b);
    }

    @Step
    public void deleteUserGroup(String userGroupId) {
       deleteEntityWithEtag(userGroupId);
    }

    @Step
    public void deleteUserGroupByUser(String userId, String userGroupId) {
        deleteEntityWithEtagByUser(userId, userGroupId);
    }

    public void checkUserGroupExistency(String userGroupId, boolean existency) {
        UserGroupDto[] userGroup = getEntities(null, null, null, "user_group_id=='" + userGroupId + "'", null, null, null).as(UserGroupDto[].class);
        if (existency) {
            assertNotNull(Arrays.asList(userGroup).stream().findFirst().orElse(null));
            return;
        }
        assertNull(Arrays.asList(userGroup).stream().findFirst().orElse(null));
    }

    @Step
    public void updateUserGroup(String userGroupId, UserGroupUpdateDto userGroupUpdateDto) {
        updateUserGroupByUser(DEFAULT_SNAPSHOT_USER_ID, userGroupId, userGroupUpdateDto);
    }

    @Step
    public void updateUserGroupByUser(String userId, String userGroupId, UserGroupUpdateDto userGroupUpdateDto) {
        String etag = getEntity(userGroupId).getHeader(HEADER_ETAG);
        try {
            JSONObject dataForUpdate = retrieveData(userGroupUpdateDto);
            Response updateResponse = updateEntityByUser(userId, userGroupId, RegexValueConverter.transform(dataForUpdate).toString(), etag);
            setSessionResponse(updateResponse);
        } catch (JsonProcessingException e){
            fail("Exception while retrieving JSON from UserGroupUpdate object: " + e.toString());
        }
    }

    public void checkUserGroupData(String userGroupId, UserGroupUpdateDto userGroupUpdateDto) throws JsonProcessingException {
        UserGroupDto fromDatabase = getEntity(userGroupId).as(UserGroupDto.class);
        JSONObject fromDatabaseJson = retrieveData(fromDatabase);
        JSONObject updatedData = retrieveData(userGroupUpdateDto);

        Iterator<?> userGroupFromDBKeys = fromDatabaseJson.keys();
        Iterator<?> updatedDataKeys = updatedData.keys();

        while (updatedDataKeys.hasNext()) {
            String key = (String) updatedDataKeys.next();

            Object updatedValue = updatedData.get(key);
            Object databaseValue = fromDatabaseJson.get(key);

            assertEquals(updatedValue, databaseValue);
        }
    }

    /*
    Method to establish relationship between UserGroup and PropertySet
    @param: userGroupId
    @param: propertySetId
    @param: isActive
     */

    @Step
    public void relationshipGroupPropertySetExist(String userGroupId, String propertySetId, Boolean isActive) {
        userGroupPropertySetRelationshipIsCreatedByUser(DEFAULT_SNAPSHOT_USER_ID, userGroupId, propertySetId, isActive);
    }

    @Step
    public void userGroupPropertySetRelationshipIsCreatedByUser(String userId, String userGroupId, String propertySetId, Boolean isActive){
        UserGroupPropertySetRelationshipDto relation = new UserGroupPropertySetRelationshipDto();
        relation.setPropertySetId(propertySetId);
        relation.setIsActive(isActive);

        Response resp = createSecondLevelRelationshipByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, relation);
        setSessionResponse(resp);
    }

    /*
    Method to establish relationship between UserGroup and Property
    @param: userGroupId
    @param: propertyId
    @param: isActive
     */
    @Step
    public void relationshipGroupPropertyExist(String userGroupId, String propertyId, Boolean isActive) {
        userGroupPropertyRelationshipIsCreatedByUser(DEFAULT_SNAPSHOT_USER_ID, userGroupId, propertyId, isActive);
    }

    @Step
    public void userGroupPropertyRelationshipIsCreatedByUser(String userId, String userGroupId, String propertyId, Boolean isActive) {
        UserGroupPropertyRelationshipDto relation = new UserGroupPropertyRelationshipDto();
        relation.setPropertyId(propertyId);
        relation.setIsActive(isActive);

        Response resp = createSecondLevelRelationshipByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, relation);
        setSessionResponse(resp);
    }

    @Step
    public void addUserToUserGroup(String userId, String userGroupId, Boolean isActive) {
        addUserToUserGroupByUser(DEFAULT_SNAPSHOT_USER_ID, userId, userGroupId, isActive);
    }

    @Step
    public void addUserToUserGroupByUser(String performerId, String userId, String userGroupId, Boolean isActive) {
        UserGroupUserRelationshipDto relation = new UserGroupUserRelationshipDto();
        relation.setUserId(userId);
        relation.setIsActive(isActive);

        Response resp = createSecondLevelRelationshipByUser(performerId, userGroupId, SECOND_LEVEL_OBJECT_USERS, relation);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsProperty(String userGroupId, String propertyId) {
        Response resp = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsPropertyByUser(String userId, String userGroupId, String propertyId) {
        Response resp = getSecondLevelEntityByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsPropertySet(String userGroupId, String propertySetId) {
        Response resp = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsPropertySetByUser(String userId, String userGroupId, String propertySetId) {
        Response resp = getSecondLevelEntityByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsUser(String userGroupId, String userId){
        Response resp = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsUserRelationshipByUser(String performerId, String userGroupId, String userId){
        Response resp = getSecondLevelEntityByUser(performerId, userGroupId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(resp);
    }

    @Step
    public void relationshipGroupPropertyIsDeleted(String userGroupId, String propertyId) {
        Response resp = deleteSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        setSessionResponse(resp);
    }

    @Step
    public void userGroupPropertyRelationshipIsDeletedByUser(String userId, String userGroupId, String propertyId) {
        Response resp = deleteSecondLevelEntityByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        setSessionResponse(resp);
    }

    @Step
    public void relationshipGroupPropertySetIsDeleted(String userGroupId, String propertySetId) {
        Response resp = deleteSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null);
        setSessionResponse(resp);
    }

    @Step
    public void relationshipGroupPropertySetIsDeletedByUser(String userId, String userGroupId, String propertySetId) {
        Response resp = deleteSecondLevelEntityByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null);
        setSessionResponse(resp);
    }

    @Step
    public void relationshipGroupUserIsDeleted(String userGroupId, String userId) {
        Response resp = deleteSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(resp);
    }

    @Step
    public void userGroupUserRelationshipIsDeletedByUser(String performerId, String userGroupId, String userId) {
        Response resp = deleteSecondLevelEntityByUser(performerId, userGroupId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(resp);
    }

    public void checkGroupPropertyExistence(String userGroupId, String propertyId) {
        Response resp = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        if (resp.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
            fail("Relationship userGroup-property still exists!");
        }
    }

    public void checkGroupPropertySetExistence(String userGroupId, String propertySetId) {
        Response resp = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null);
        if (resp.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
            fail("Relationship userGroup-propertySet still exists!");
        }
    }

    @Step
    public void setGroupPropertyActivity(String userGroupId, String propertyId, boolean activity) throws JsonProcessingException {
        setGroupPropertyActivityByUser(DEFAULT_SNAPSHOT_USER_ID, userGroupId, propertyId, activity);
    }

    @Step
    public void setGroupPropertyActivityByUser(String userId, String userGroupId, String propertyId, boolean activity) throws JsonProcessingException {
        String etag = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null).getHeader(HEADER_ETAG);
        UserGroupPropertyRelationshipUpdateDto relation = new UserGroupPropertyRelationshipDto();
        relation.setIsActive(activity);
        try {
            JSONObject obj = retrieveData(relation);
            Response resp = updateSecondLevelEntityByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, obj, etag);
            setSessionResponse(resp);
        } catch (JsonProcessingException e){
            fail("Exception while retrieving JSON from UserGroupPropertyRelationshipUpdateDto object: " + e.toString());
        }
    }

    @Step
    public void setGroupPropertySetActivity(String userGroupId, String propertySetId, boolean isActive) throws JsonProcessingException {
        setGroupPropertySetActivityByUser(DEFAULT_SNAPSHOT_USER_ID, userGroupId, propertySetId, isActive);
    }

    @Step
    public void setGroupPropertySetActivityByUser(String userId, String userGroupId, String propertySetId, boolean isActive) throws JsonProcessingException {
        String etag = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null).getHeader(HEADER_ETAG);
        UserGroupPropertySetRelationshipDto relation = new UserGroupPropertySetRelationshipDto();
        relation.setIsActive(isActive);

        JSONObject obj = retrieveData(relation);

        Response resp = updateSecondLevelEntityByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, obj, etag);
        setSessionResponse(resp);
    }

    @Step
    public void setUserGroupUserActivity(String userGroupId, String userId, Boolean isActive) throws JsonProcessingException {
        setUserGroupUserActivityByUser(DEFAULT_SNAPSHOT_APPLICATION_ID, userGroupId, userId, isActive);
    }

    @Step
    public void setUserGroupUserActivityByUser(String performerId, String userGroupId, String userId, Boolean isActive) throws JsonProcessingException {
        String etag = getSecondLevelEntityEtag(userGroupId, SECOND_LEVEL_OBJECT_USERS, userId);
        UserGroupUserRelationshipUpdateDto relation = new UserGroupUserRelationshipUpdateDto();
        relation.setIsActive(isActive);

        JSONObject jsonRelation = retrieveData(relation);

        Response resp = updateSecondLevelEntityByUser(performerId, userGroupId, SECOND_LEVEL_OBJECT_USERS, userId, jsonRelation, etag);
        setSessionResponse(resp);
    }

    @Step
    public void userGroupRoleRelationshipIsCreatedByUser(String userId, String userGroupId, String roleId) {
        RoleIdDto roleObject = new RoleIdDto();
        roleObject.setRoleId(roleId);

        Response response = createSecondLevelRelationshipByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_ROLES, roleObject);
        setSessionResponse(response);
    }

    @Step
    public void relationshipGroupRoleExist(String userGroupId, String roleId) throws JsonProcessingException {
        RoleIdDto roleObject = new RoleIdDto();
        roleObject.setRoleId(roleId);

        JSONObject roleInJson = retrieveData(roleObject);

        Response response = createSecondLevelRelationship(userGroupId, SECOND_LEVEL_OBJECT_ROLES, roleInJson.toString());
        setSessionResponse(response);
    }

    @Step
    public void userGroupPropertyRoleRelationshipIsCreatedByUser(String userId, String userGroupId, String propertyId, String roleId) {
        RoleIdDto roleObject = new RoleIdDto();
        roleObject.setRoleId(roleId);

        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).body(roleObject).when().post(userGroupId + "/" + SECOND_LEVEL_OBJECT_PROPERTIES + "/" + propertyId + "/" + SECOND_LEVEL_OBJECT_ROLES);
        setSessionResponse(response);
    }

    @Step
    public void userGroupPropertySetRoleRelationshipIsCreatedByUser(String userId, String userGroupId, String propertyId, String roleId) {
        RoleIdDto roleObject = new RoleIdDto();
        roleObject.setRoleId(roleId);

        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).body(roleObject).when().post(userGroupId + "/" + SECOND_LEVEL_OBJECT_PROPERTY_SETS + "/" + propertyId + "/" + SECOND_LEVEL_OBJECT_ROLES);
        setSessionResponse(response);
    }

    @Step
    public void userGroupPropertyRoleRelationshipIsDeletedByUser(String userId, String userGroupId, String propertyId, String roleId) {
        String url = userGroupId + "/" + SECOND_LEVEL_OBJECT_PROPERTIES + "/" + propertyId + "/" + SECOND_LEVEL_OBJECT_ROLES + roleId;
        String etag = given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).when().get(url).getHeader(HEADER_ETAG);

        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).header(HEADER_ETAG, etag).when().delete(url);
        setSessionResponse(response);
    }

    @Step
    public void userGroupPropertySetRoleRelationshipIsDeletedByUser(String userId, String userGroupId, String propertyId, String roleId) {
        String url = userGroupId + "/" + SECOND_LEVEL_OBJECT_PROPERTY_SETS + "/" + propertyId + "/" + SECOND_LEVEL_OBJECT_ROLES + roleId;
        String etag = given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).when().get(url).getHeader(HEADER_ETAG);

        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).header(HEADER_ETAG, etag).when().delete(url);
        setSessionResponse(response);
    }

    public void checkUserGroupRoleRelationExistency(String userGroupId, String roleId, Boolean existency) {
        RoleIdDto[] listOfRoles = getSecondLevelEntities(userGroupId, SECOND_LEVEL_OBJECT_ROLES, null, null, null, null, null, null).as(RoleIdDto[].class);
        Boolean found = false;
        for (RoleIdDto role : listOfRoles) {
            if (role.getRoleId().equalsIgnoreCase(roleId)) {
                found = true;
            }
        }

        if (found != existency) {
            fail("Expected: " + existency.toString() + " but otherwise!");
        }
    }

    @Step
    public void deleteUserGroupRoleRelationship(String userGroupId, String roleId) {
        Response resp = deleteSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_ROLES, roleId, null);
        setSessionResponse(resp);
    }

    @Step
    public void deleteUserGroupRoleRelationshipByUser(String userId, String userGroupId, String roleId) {
        Response resp = deleteSecondLevelEntityByUser(userId, userGroupId, SECOND_LEVEL_OBJECT_ROLES, roleId, null);
        setSessionResponse(resp);
    }

    public void checkuserGroupPropertyRelationActivity(String userGroupId, String propertyId, boolean activity) {
        UserGroupPropertyRelationshipDto relation = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null).as(UserGroupPropertyRelationshipDto.class);
        assertEquals(relation.getIsActive(), activity);
    }

    public void checkuserGroupPropertySetRelationActivity(String userGroupId, String propertySetId, boolean b) {
        UserGroupPropertySetRelationshipDto relationship = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null).as(UserGroupPropertySetRelationshipDto.class);
        assertEquals(relationship.getIsActive(), b);
    }

    public Boolean getUserGroupUserRelationIsActive(String userGroupId, String userId){
        UserGroupUserRelationshipDto relationship = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_USERS, userId, null).as(UserGroupUserRelationshipDto.class);
        return relationship.getIsActive();
    }

    public UserGroupDto getUserGroupByName(String userGroupName) {
        UserGroupDto[] userGroups = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "name==" + userGroupName, null, null, null).as(UserGroupDto[].class);
        return Arrays.stream(userGroups).findFirst().orElse(null);
    }
}
