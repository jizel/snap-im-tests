package travel.snapshot.dp.qa.cucumber.serenity.user_groups;

import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_RESOURCE;
import static travel.snapshot.dp.qa.cucumber.serenity.roles.RoleBaseSteps.getResponseAsRoles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.RoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserGroupUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipUpdateDto;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.helpers.RegexValueConverter;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserGroupsSteps extends BasicSteps {

    private static final String USER_GROUPS_PATH = "/user_groups";

    public UserGroupsSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(USER_GROUPS_PATH);
    }

    public void followingUserGroupIsCreatedByUserForApp(UUID userId, UUID applicationVersionId, UserGroupDto userGroup) {
        Response createResponse = createEntityByUserForApplication(userId, applicationVersionId, userGroup);
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
    public UserGroupDto getUserGroup(UUID userGroupId) {
        return getUserGroupByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userGroupId);
    }

    @Step
    public UserGroupDto getUserGroupByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId) {
        Response response = getEntityByUserForApplication(userId, applicationVersionId, userGroupId);
        setSessionResponse(response);
        if (response.getStatusCode() == HttpStatus.SC_OK){
            return response.as(UserGroupDto.class);
        }
        return null;
    }

    @Step
    public void listUserGroupsIsGot(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listUserGroupsIsGotByUserForApp(UUID userId, UUID applicationVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUserForApp(userId, applicationVersionId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listGroupRoleIsGot(UUID userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(userGroupId, ROLES_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listGroupRolesIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, userGroupId, ROLES_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public Response listOfUserGroupPropertiesIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, userGroupId, PROPERTIES_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response listOfUserGroupPropertySetsIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, userGroupId, PROPERTY_SETS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response listOfUserGroupUsersIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, userGroupId, USERS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response listOfUserGroupPropertyRolesIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertyId){
        Response response = getThirdLevelEntitiesByUserForApp(userId, applicationVersionId, userGroupId, PROPERTIES_RESOURCE, propertyId, ROLES_RESOURCE, null, null, null, null, null, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response listOfUserGroupPropertySetRolesIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertyId){
        Response response = getThirdLevelEntitiesByUserForApp(userId, applicationVersionId, userGroupId, PROPERTY_SETS_RESOURCE, propertyId, ROLES_RESOURCE, null, null, null, null, null, null);
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

    public void responseRelationsAreSorted(List<String> order) {
        if (order.isEmpty()) {
            return;
        }
        RoleRelationshipDto[] roles = getSessionResponse().as(RoleRelationshipDto[].class);
        int i = 0;
        for (RoleRelationshipDto roleRelationship : roles) {
            if (!roleRelationship.getRoleId().toString().startsWith(order.get(i))) {
                fail("Expected ID: " + roleRelationship.getRoleId() + "but was starting with: " + order.get(i));
            }
            i++;
        }
    }

    public void setUserGroupActiveField(UUID userGroupId, boolean shouldBeActive) throws JsonProcessingException {
        UserGroupUpdateDto userGroupUpdate = new UserGroupUpdateDto();
        userGroupUpdate.setIsActive(shouldBeActive);
        this.updateUserGroup(userGroupId, userGroupUpdate);
    }

    public void checkUserGroupActiveField(UUID userGroupId, boolean b) {
        UserGroupDto userGroup = getEntity(userGroupId).as(UserGroupDto.class);
        assertEquals(userGroup.getIsActive(), b);
    }

    @Step
    public void deleteUserGroup(UUID userGroupId) {
       deleteEntityWithEtag(userGroupId);
    }

    @Step
    public void deleteUserGroupByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId) {
        deleteEntityWithEtagByUserForApp(userId, applicationVersionId, userGroupId);
    }

    public void checkUserGroupExistency(UUID userGroupId, boolean existency) {
        UserGroupDto[] userGroup = getEntities(null, null, null, "user_group_id=='" + userGroupId + "'", null, null, null).as(UserGroupDto[].class);
        if (existency) {
            assertNotNull(Arrays.asList(userGroup).stream().findFirst().orElse(null));
            return;
        }
        assertNull(Arrays.asList(userGroup).stream().findFirst().orElse(null));
    }

    @Step
    public void updateUserGroup(UUID userGroupId, UserGroupUpdateDto userGroupUpdateDto) {
        updateUserGroupByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userGroupId, userGroupUpdateDto);
    }

    @Step
    public void updateUserGroupByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UserGroupUpdateDto userGroupUpdateDto) {
        String etag = getEntity(userGroupId).getHeader(HEADER_ETAG);
        try {
            JSONObject dataForUpdate = retrieveData(userGroupUpdateDto);
            Response updateResponse = updateEntityByUserForApplication(userId, applicationVersionId, userGroupId, RegexValueConverter.transform(dataForUpdate).toString(), etag);
            setSessionResponse(updateResponse);
        } catch (JsonProcessingException e){
            fail("Exception while retrieving JSON from UserGroupUpdate object: " + e.toString());
        }
    }

    public void checkUserGroupData(UUID userGroupId, UserGroupUpdateDto userGroupUpdateDto) throws JsonProcessingException {
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
    public void relationshipGroupPropertySetExist(UUID userGroupId, UUID propertySetId, Boolean isActive) {
        userGroupPropertySetRelationshipIsCreatedByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userGroupId, propertySetId, isActive);
    }

    @Step
    public void userGroupPropertySetRelationshipIsCreatedByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertySetId, Boolean isActive){
        UserGroupPropertySetRelationshipPartialDto relation = new UserGroupPropertySetRelationshipPartialDto();
        relation.setPropertySetId(propertySetId);
        relation.setIsActive(isActive);

        Response resp = createSecondLevelRelationshipByUserForApplication(userId, applicationVersionId, userGroupId, PROPERTY_SETS_RESOURCE, relation);
        setSessionResponse(resp);
    }

    /*
    Method to establish relationship between UserGroup and Property
    @param: userGroupId
    @param: propertyId
    @param: isActive
     */
    @Step
    public void relationshipGroupPropertyExist(UUID userGroupId, UUID propertyId, Boolean isActive) {
        createUserGroupPropertyRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userGroupId, propertyId, isActive);
    }

    @Step
    public void createUserGroupPropertyRelationshipByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertyId, Boolean isActive) {
        UserGroupPropertyRelationshipPartialDto relation = new UserGroupPropertyRelationshipPartialDto();
        relation.setPropertyId(propertyId);
        relation.setIsActive(isActive);

        Response resp = createSecondLevelRelationshipByUserForApplication(userId, applicationVersionId, userGroupId, PROPERTIES_RESOURCE, relation);
        setSessionResponse(resp);
    }

    @Step
    public void createUserGroupPropertyRelationshipByUserForAppInvalid(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertyId, Boolean isActive) {
        Map<String, String> userGroupPropertyRelation = new HashMap<>();
        userGroupPropertyRelation.put(PROPERTY_ID, propertyId.toString());
        userGroupPropertyRelation.put(IS_ACTIVE, isActive.toString());

        Response resp = createSecondLevelRelationshipByUserForApplication(userId, applicationVersionId, userGroupId, PROPERTIES_RESOURCE, userGroupPropertyRelation);
        setSessionResponse(resp);
    }

    @Step
    public void addUserToUserGroup(UUID userId, UUID userGroupId, Boolean isActive) {
        addUserToUserGroupByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, userGroupId, isActive);
    }

    @Step
    public void addUserToUserGroupByUserForApp(UUID performerId, UUID applicationVersionId, UUID userId, UUID userGroupId, Boolean isActive) {
        UserGroupUserRelationshipPartialDto relation = new UserGroupUserRelationshipPartialDto();
        relation.setUserId(userId);
        relation.setIsActive(isActive);

        Response resp = createSecondLevelRelationshipByUserForApplication(performerId, applicationVersionId, userGroupId, USERS_RESOURCE, relation);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsProperty(UUID userGroupId, UUID propertyId) {
        Response resp = getSecondLevelEntity(userGroupId, PROPERTIES_RESOURCE, propertyId);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsPropertyByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertyId) {
        Response resp = getSecondLevelEntityByUserForApp(userId, applicationVersionId, userGroupId, PROPERTIES_RESOURCE, propertyId);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsPropertySet(UUID userGroupId, UUID propertySetId) {
        Response resp = getSecondLevelEntity(userGroupId, PROPERTY_SETS_RESOURCE, propertySetId);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsPropertySetByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertySetId) {
        Response resp = getSecondLevelEntityByUserForApp(userId, applicationVersionId, userGroupId, PROPERTY_SETS_RESOURCE, propertySetId);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsUser(UUID userGroupId, UUID userId){
        Response resp = getSecondLevelEntity(userGroupId, USERS_RESOURCE, userId);
        setSessionResponse(resp);
    }

    @Step
    public void getUserGroupsUserRelationshipByUserForApp(UUID performerId, UUID applicationVersionId, UUID userGroupId, UUID userId){
        Response resp = getSecondLevelEntityByUserForApp(performerId, applicationVersionId, userGroupId, USERS_RESOURCE, userId);
        setSessionResponse(resp);
    }

    @Step
    public void deleteUserGroupPropertyRelationship(UUID userGroupId, UUID propertyId) {
        Response resp = deleteSecondLevelEntity(userGroupId, PROPERTIES_RESOURCE, propertyId, null);
        setSessionResponse(resp);
    }

    @Step
    public void deleteUserGroupPropertyRelationshipByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertyId) {
        Response resp = deleteSecondLevelEntityByUserForApplication(userId, applicationVersionId, userGroupId, PROPERTIES_RESOURCE, propertyId, null);
        setSessionResponse(resp);
    }

    @Step
    public void relationshipGroupPropertySetIsDeleted(UUID userGroupId, UUID propertySetId) {
        Response resp = deleteSecondLevelEntity(userGroupId, PROPERTY_SETS_RESOURCE, propertySetId, null);
        setSessionResponse(resp);
    }

    @Step
    public void relationshipGroupPropertySetIsDeletedByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertySetId) {
        Response resp = deleteSecondLevelEntityByUserForApplication(userId, applicationVersionId, userGroupId, PROPERTY_SETS_RESOURCE, propertySetId, null);
        setSessionResponse(resp);
    }

    @Step
    public void relationshipGroupUserIsDeleted(UUID userGroupId, UUID userId) {
        Response resp = deleteSecondLevelEntity(userGroupId, USERS_RESOURCE, userId, null);
        setSessionResponse(resp);
    }

    @Step
    public void userGroupUserRelationshipIsDeletedByUserForApp(UUID performerId, UUID applicationVersionId, UUID userGroupId, UUID userId) {
        Response resp = deleteSecondLevelEntityByUserForApplication(performerId, applicationVersionId, userGroupId, USERS_RESOURCE, userId, null);
        setSessionResponse(resp);
    }

    public void checkGroupPropertyExistence(UUID userGroupId, UUID propertyId) {
        Response resp = getSecondLevelEntity(userGroupId, PROPERTIES_RESOURCE, propertyId);
        if (resp.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
            fail("Relationship userGroup-property still exists!");
        }
    }

    public void checkGroupPropertySetExistence(UUID userGroupId, UUID propertySetId) {
        Response resp = getSecondLevelEntity(userGroupId, PROPERTY_SETS_RESOURCE, propertySetId);
        if (resp.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
            fail("Relationship userGroup-propertySet still exists!");
        }
    }

    @Step
    public void setGroupPropertyActivity(UUID userGroupId, UUID propertyId, boolean activity) throws JsonProcessingException {
        setGroupPropertyActivityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userGroupId, propertyId, activity);
    }

    @Step
    public void setGroupPropertyActivityByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertyId, boolean activity) throws JsonProcessingException {
        String etag = getSecondLevelEntity(userGroupId, PROPERTIES_RESOURCE, propertyId).getHeader(HEADER_ETAG);
        UserGroupPropertyRelationshipUpdateDto relation = new UserGroupPropertyRelationshipPartialDto();
        relation.setIsActive(activity);
        try {
            JSONObject obj = retrieveData(relation);
            Response resp = updateSecondLevelEntityByUserForApp(userId, applicationVersionId, userGroupId, PROPERTIES_RESOURCE, propertyId, obj, etag);
            setSessionResponse(resp);
        } catch (JsonProcessingException e){
            fail("Exception while retrieving JSON from UserGroupPropertyRelationshipUpdateDto object: " + e.toString());
        }
    }

    @Step
    public void setGroupPropertySetActivity(UUID userGroupId, UUID propertySetId, boolean isActive) throws JsonProcessingException {
        setGroupPropertySetActivityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userGroupId, propertySetId, isActive);
    }

    @Step
    public void setGroupPropertySetActivityByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertySetId, boolean isActive) throws JsonProcessingException {
        String etag = getSecondLevelEntity(userGroupId, PROPERTY_SETS_RESOURCE, propertySetId).getHeader(HEADER_ETAG);
        UserGroupPropertySetRelationshipPartialDto relation = new UserGroupPropertySetRelationshipPartialDto();
        relation.setIsActive(isActive);

        JSONObject obj = retrieveData(relation);

        Response resp = updateSecondLevelEntityByUserForApp(userId, applicationVersionId, userGroupId, PROPERTY_SETS_RESOURCE, propertySetId, obj, etag);
        setSessionResponse(resp);
    }

    @Step
    public void setUserGroupUserActivity(UUID userGroupId, UUID userId, Boolean isActive) throws JsonProcessingException {
        setUserGroupUserActivityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userGroupId, userId, isActive);
    }

    @Step
    public void setUserGroupUserActivityByUserForApp(UUID performerId, UUID applicationVersionId, UUID userGroupId, UUID userId, Boolean isActive) throws JsonProcessingException {
        String etag = getSecondLevelEntityEtag(userGroupId, USERS_RESOURCE, userId);
        UserGroupUserRelationshipUpdateDto relation = new UserGroupUserRelationshipUpdateDto();
        relation.setIsActive(isActive);

        JSONObject jsonRelation = retrieveData(relation);

        Response resp = updateSecondLevelEntityByUserForApp(performerId, applicationVersionId, userGroupId, USERS_RESOURCE, userId, jsonRelation, etag);
        setSessionResponse(resp);
    }

    @Step
    public void userGroupRoleRelationshipIsCreatedByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID roleId) {
        Response response = createSecondLevelRelationshipByUserForApplication(userId, applicationVersionId, userGroupId, ROLES_RESOURCE, singletonMap(ROLE_ID, roleId));
        setSessionResponse(response);
    }

    @Step
    public void relationshipGroupRoleExist(UUID userGroupId, UUID roleId, Boolean isActive) throws JsonProcessingException {
        RoleRelationshipDto roleObject = new RoleRelationshipDto();
        roleObject.setRoleId(roleId);

        JSONObject roleInJson = retrieveData(roleObject);

        Response response = createSecondLevelRelationship(userGroupId, ROLES_RESOURCE, roleInJson.toString());
        setSessionResponse(response);
    }

    @Step
    public void userGroupPropertyRoleRelationshipIsCreatedByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertyId, UUID roleId) {
        Map<String, UUID> userGroupPropertyRoleRelation = new HashMap<>();
        userGroupPropertyRoleRelation.put(ROLE_ID, roleId);
        Response response = createThirdLevelEntityByUserForApplication(userId, applicationVersionId, userGroupId, PROPERTIES_RESOURCE, propertyId, ROLES_RESOURCE, userGroupPropertyRoleRelation);
        setSessionResponse(response);
    }

    @Step
    public void userGroupPropertySetRoleRelationshipIsCreatedByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertySetId, UUID roleId) {
        RoleRelationshipDto roleRelationship = new RoleRelationshipDto();
        roleRelationship.setRoleId(roleId);

        Response response = createThirdLevelEntityByUserForApplication(userId, applicationVersionId, userGroupId, PROPERTY_SETS_RESOURCE, propertySetId, ROLES_RESOURCE, roleRelationship);
        setSessionResponse(response);
    }

    @Step
    public void userGroupPropertyRoleRelationshipIsDeletedByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertyId, UUID roleId) {
        String etag = getThirdLevelEntityEtag(userGroupId, PROPERTIES_RESOURCE, propertyId, ROLES_RESOURCE, roleId);

        Response response = deleteThirdLevelEntityByUserForApplication(userId, applicationVersionId, userGroupId, PROPERTIES_RESOURCE, propertyId, ROLES_RESOURCE, roleId, etag);
        setSessionResponse(response);
    }

    @Step
    public void userGroupPropertySetRoleRelationshipIsDeletedByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID propertySetId, UUID roleId) {
        String etag = getThirdLevelEntityEtag(userGroupId, PROPERTY_SETS_RESOURCE, propertySetId, ROLES_RESOURCE, roleId);

        Response response = deleteThirdLevelEntityByUserForApplication(userId, applicationVersionId, userGroupId, PROPERTY_SETS_RESOURCE, propertySetId, ROLES_RESOURCE, roleId, etag);
        setSessionResponse(response);
    }

    public void checkUserGroupRoleRelationExistency(UUID userGroupId, UUID roleId, Boolean existency) {
        Response response = getSecondLevelEntities(userGroupId, ROLES_RESOURCE, null, null, null, null, null, null);
        List<RoleDto> listOfRoles = getResponseAsRoles(response);
        Boolean found = false;
        for (RoleDto role : listOfRoles) {
            if (role.getId().equals(roleId)) {
                found = true;
            }
        }

        if (found != existency) {
            fail("Expected: " + existency.toString() + " but otherwise!");
        }
    }

    @Step
    public void deleteUserGroupRoleRelationship(UUID userGroupId, UUID roleId) {
        Response resp = deleteSecondLevelEntity(userGroupId, ROLES_RESOURCE, roleId, null);
        setSessionResponse(resp);
    }

    @Step
    public void deleteUserGroupRoleRelationshipByUserForApp(UUID userId, UUID applicationVersionId, UUID userGroupId, UUID roleId) {
        Response resp = deleteSecondLevelEntityByUserForApplication(userId, applicationVersionId, userGroupId, ROLES_RESOURCE, roleId, null);
        setSessionResponse(resp);
    }

    public void checkuserGroupPropertyRelationActivity(UUID userGroupId, UUID propertyId, boolean activity) {
        UserGroupPropertyRelationshipPartialDto relation = getSecondLevelEntity(userGroupId, PROPERTIES_RESOURCE, propertyId).as(UserGroupPropertyRelationshipPartialDto.class);
        assertEquals(relation.getIsActive(), activity);
    }

    public void checkuserGroupPropertySetRelationActivity(UUID userGroupId, UUID propertySetId, boolean b) {
        UserGroupPropertySetRelationshipPartialDto relationship = getSecondLevelEntity(userGroupId, PROPERTY_SETS_RESOURCE, propertySetId).as(UserGroupPropertySetRelationshipPartialDto.class);
        assertEquals(relationship.getIsActive(), b);
    }

    public Boolean getUserGroupUserRelationIsActive(UUID userGroupId, UUID userId){
        UserGroupUserRelationshipPartialDto relationship = getSecondLevelEntity(userGroupId, USERS_RESOURCE, userId).as(UserGroupUserRelationshipPartialDto.class);
        return relationship.getIsActive();
    }

    public UserGroupDto getUserGroupByName(String userGroupName) {
        UserGroupDto[] userGroups = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "name==" + userGroupName, null, null, null).as(UserGroupDto[].class);
        return Arrays.stream(userGroups).findFirst().orElse(null);
    }

    public UUID resolveUserGroupId(String userGroupName) {
        UUID userGroupId;
        if (isUUID(userGroupName)) {
            userGroupId = UUID.fromString(userGroupName);
        } else {
            UserGroupDto userGroup = getUserGroupByName(userGroupName);
            assertThat(String.format("User group with name \"%s\" does not exist", userGroupName), userGroup, is(notNullValue()));
            userGroupId = userGroup.getId();
        }
        return userGroupId;
    }
}
