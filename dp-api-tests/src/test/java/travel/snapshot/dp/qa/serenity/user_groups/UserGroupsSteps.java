package travel.snapshot.dp.qa.serenity.user_groups;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import travel.snapshot.dp.api.identity.model.RoleIdDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUpdateDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.RegexValueConverter;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class UserGroupsSteps extends BasicSteps {

    private static final String USER_GROUPS_PATH = "/identity/user_groups";

    public UserGroupsSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(USER_GROUPS_PATH);
    }

    public void followingUserGroupIsCreated(UserGroupDto userGroup) throws JsonProcessingException {
        JSONObject removeNulls = retrieveDataNew(userGroup);
        JSONObject getRegexExample = RegexValueConverter.transform(removeNulls);
        Response response = createEntity(getRegexExample.toString());
        setSessionResponse(response);
    }

    public void followingUserGroupsExist(List<UserGroupDto> user_groups) {
        user_groups.forEach(ug ->
        {
            Response createResponse = createEntity(ug);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("User group cannot be created: " + createResponse.getStatusCode() + " " + createResponse.getBody().asString());
            }
        });
    }

    public void userGroupWithIdGot(String userGroupId) {
        Response resp = getEntity(userGroupId, null);
        setSessionResponse(resp);
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

        Response updateResponse = updateEntity(userGroupId, retrieveDataNew(userGroupUpdate).toString(), tempResponse.getHeader(HEADER_ETAG));

        Response resp = getEntity(userGroupId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    public void listUserGroupsIsGot(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void listGroupRoleIsGot(String userGroupId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(userGroupId, SECOND_LEVEL_OBJECT_ROLES, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
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

    public void deleteUserGroup(String userGroupId) {
        Response response = deleteEntity(userGroupId);
        setSessionResponse(response);
    }

    public void checkUserGroupExistency(String userGroupId, boolean existency) {
        UserGroupDto[] userGroup = getEntities(null, null, "user_group_id=='" + userGroupId + "'", null, null).as(UserGroupDto[].class);
        if (existency) {
            assertNotNull(Arrays.asList(userGroup).stream().findFirst().orElse(null));
            return;
        }
        assertNull(Arrays.asList(userGroup).stream().findFirst().orElse(null));
    }

    public void updateUserGroup(String userGroupId, UserGroupUpdateDto userGroupUpdateDto) throws JsonProcessingException {
        Response tempResponse = getEntity(userGroupId);

        JSONObject dataForUpdate = retrieveDataNew(userGroupUpdateDto);

        Response updateResponse = updateEntity(userGroupId, RegexValueConverter.transform(dataForUpdate).toString(), tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(updateResponse);
    }

    public void checkUserGroupData(String userGroupId, UserGroupUpdateDto userGroupUpdateDto) throws JsonProcessingException {
        UserGroupDto fromDatabase = getEntity(userGroupId).as(UserGroupDto.class);
        JSONObject fromDatabaseJson = retrieveDataNew(fromDatabase);
        JSONObject updatedData = retrieveDataNew(userGroupUpdateDto);

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
    public void relationshipGroupPropertySetExist(String userGroupId, String propertySetId, Boolean isActive) {
        UserGroupPropertySetRelationshipDto relation = new UserGroupPropertySetRelationshipDto();
        relation.setPropertySetId(propertySetId);
        relation.setIsActive(isActive);

        Response resp = createSecondLevelRelationship(userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, relation);
        setSessionResponse(resp);
    }

    /*
    Method to establish relationship between UserGroup and Property
    @param: userGroupId
    @param: propertyId
    @param: isActive
     */
    public void relationshipGroupPropertyExist(String userGroupId, String propertyId, Boolean isActive) {
        UserGroupPropertyRelationshipDto relation = new UserGroupPropertyRelationshipDto();
        relation.setPropertyId(propertyId);
        relation.setIsActive(isActive);

        Response resp = createSecondLevelRelationship(userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, relation);
        setSessionResponse(resp);
    }

    public void getUserGroupsProperty(String userGroupId, String propertyId) {
        Response resp = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        setSessionResponse(resp);
    }

    public void getUserGroupsPropertySet(String userGroupId, String propertySetId) {
        Response resp = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null);
        setSessionResponse(resp);
    }

    public void relationshipGroupPropertyIsDeleted(String userGroupId, String propertyId) {
        Response resp = deleteSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId);
        setSessionResponse(resp);
    }

    public void relationshipGroupPropertySetIsDeleted(String userGroupId, String propertySetId) {
        Response resp = deleteSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId);
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

    public void setGroupPropertyActivity(String userGroupId, String propertyId, boolean activity) throws JsonProcessingException {
        Response tempReponse = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        UserGroupPropertyRelationshipUpdateDto relation = new UserGroupPropertyRelationshipDto();
        relation.setIsActive(activity);

        JSONObject obj = retrieveDataNew(relation);

        Response resp = updateSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, obj, tempReponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    public void setGroupPropertySetActivity(String userGroupId, String propertySetId, boolean b) throws JsonProcessingException {
        Response tempReponse = getSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null);
        UserGroupPropertySetRelationshipDto relation = new UserGroupPropertySetRelationshipDto();
        relation.setIsActive(b);

        JSONObject obj = retrieveDataNew(relation);

        Response resp = updateSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, obj, tempReponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    public void relationshipGroupRoleExist(String userGroupId, String roleId) throws JsonProcessingException {
        RoleIdDto roleObject = new RoleIdDto();
        roleObject.setRoleId(roleId);

        JSONObject roleInJson = retrieveDataNew(roleObject);

        Response response = createSecondLevelRelationship(userGroupId, SECOND_LEVEL_OBJECT_ROLES, roleInJson.toString());
        setSessionResponse(response);
    }

    public void checkUserGroupRoleRelationExistency(String userGroupId, String roleId, Boolean existency) {
        RoleIdDto[] listOfRoles = getSecondLevelEntities(userGroupId, SECOND_LEVEL_OBJECT_ROLES, null, null, null, null, null).as(RoleIdDto[].class);
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

    public void deleteUserGroupRoleRelationship(String userGroupId, String roleId) {
        Response resp = deleteSecondLevelEntity(userGroupId, SECOND_LEVEL_OBJECT_ROLES, roleId);
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
}