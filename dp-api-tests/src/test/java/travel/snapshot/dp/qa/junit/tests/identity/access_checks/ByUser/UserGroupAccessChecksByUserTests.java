package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.Map;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertySetRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

public class UserGroupAccessChecksByUserTests extends CommonAccessCheckByUserTest {
    UUID requestorId;
    UUID targetGroupId;
    UUID relationId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        requestorId = entityIsCreated(testUser1);
        targetGroupId = entityIsCreated(testUserGroup1);
        relationId = entityIsCreated(constructUserGroupUserRelationship(targetGroupId, requestorId, false));
    }

    @Test
    void directAccessTest() {
        getEntityFails(requestorId, USER_GROUPS_PATH, targetGroupId);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId, ACTIVATE_RELATION);
        getEntitySucceeds(requestorId, USER_GROUPS_PATH, targetGroupId);
    }

    @ParameterizedTest
    @CsvSource({"name=='Test UserGroup*', 1",
                "is_active==false, 1",
                "is_active==true, 0"})
    void filteringUserGroupsAccessCheck(String filter, Integer numOfOccurrences) {
        testUserGroup2.setIsActive(false);
        UUID groupId = entityIsCreated(testUserGroup2);
        entityIsCreated(constructUserGroupUserRelationship(groupId, requestorId, true));
        Map<String, String> params = QueryParams.builder().filter(filter).build();
        assertThat(getEntitiesAsTypeByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, USER_GROUPS_PATH, UserGroupDto.class, params)).hasSize(numOfOccurrences);
    }

    @Test
    void getGroupPropertiesGet() {
        entityIsCreated(constructUserGroupPropertyRelationship(targetGroupId, DEFAULT_PROPERTY_ID, true));
        assertThat(getEntitiesAsTypeByUserForApp(requestorId,
                                                 DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                                                 USER_GROUP_PROPERTY_RELATIONSHIPS_PATH,
                                                 UserGroupPropertyRelationshipDto.class,
                                                 emptyQueryParams())).hasSize(0);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId, ACTIVATE_RELATION);
        assertThat(getEntitiesAsTypeByUserForApp(requestorId,
                                                 DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                                                 USER_GROUP_PROPERTY_RELATIONSHIPS_PATH,
                                                 UserGroupPropertyRelationshipDto.class,
                                                 emptyQueryParams())).hasSize(1);
    }

    @Test
    void getGroupPropertySetsGet() {
        UUID psId = entityIsCreated(testPropertySet1);
        entityIsCreated(constructUserGroupPropertySetRelationship(targetGroupId, psId, true));
        assertThat(getEntitiesAsTypeByUserForApp(requestorId,
                DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH,
                UserGroupPropertySetRelationshipDto.class,
                emptyQueryParams())).hasSize(0);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId, ACTIVATE_RELATION);
        assertThat(getEntitiesAsTypeByUserForApp(requestorId,
                DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH,
                UserGroupPropertySetRelationshipDto.class,
                emptyQueryParams())).hasSize(1);
    }

    @Test
    void updateDeleteGroup() {
        UserGroupUpdateDto update = new UserGroupUpdateDto();
        update.setName("New name");
        updateEntityFails(requestorId, USER_GROUPS_PATH, targetGroupId, update);
        deleteEntityFails(requestorId, USER_GROUPS_PATH, targetGroupId, SC_NOT_FOUND);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId, ACTIVATE_RELATION);
        updateEntitySucceeds(requestorId, USER_GROUPS_PATH, targetGroupId, update);
        deleteEntityFails(requestorId, USER_GROUPS_PATH, targetGroupId, SC_CONFLICT);
    }

    @Test
    void userGroupPropertiesCRUD() {
        UserGroupPropertyRelationshipCreateDto relation = constructUserGroupPropertyRelationship(targetGroupId, DEFAULT_PROPERTY_ID, true);
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, DEFAULT_PROPERTY_ID, true));
        createEntityFails(requestorId, relation);
        UUID targetRelationId = entityIsCreated(relation);
        updateEntityFails(requestorId, USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, targetRelationId, INACTIVATE_RELATION);
        deleteEntityFails(requestorId, USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, targetRelationId, SC_NOT_FOUND);
        entityIsDeleted(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, targetRelationId);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId, ACTIVATE_RELATION);
        UUID newRelationId = createEntitySucceeds(requestorId, relation).as(UserGroupPropertyRelationshipDto.class).getId();
        updateEntitySucceeds(requestorId, USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, newRelationId, INACTIVATE_RELATION);
        deleteEntitySucceeds(requestorId, USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, newRelationId);
    }

    @Test
    void userGroupPropertySetsCRUD() {
        UUID psId = entityIsCreated(testPropertySet1);
        UserGroupPropertySetRelationshipCreateDto relation = constructUserGroupPropertySetRelationship(targetGroupId, psId, true);
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, psId, true));
        createEntityFails(requestorId, relation);
        UUID targetRelationId = entityIsCreated(relation);
        updateEntityFails(requestorId, USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, targetRelationId, INACTIVATE_RELATION);
        deleteEntityFails(requestorId, USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, targetRelationId, SC_NOT_FOUND);
        entityIsDeleted(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, targetRelationId);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId, ACTIVATE_RELATION);
        UUID newRelationId = createEntitySucceeds(requestorId, relation).as(UserGroupPropertySetRelationshipDto.class).getId();
        updateEntitySucceeds(requestorId, USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, newRelationId, INACTIVATE_RELATION);
        deleteEntitySucceeds(requestorId, USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, newRelationId);
    }

    @Test
    void userGroupUserCRUD() {
        // setup
        UUID targetUserId = entityIsCreated(testUser2);
        UUID targetRelationId = entityIsCreated(constructUserGroupUserRelationship(targetGroupId, targetUserId, true));
        getEntityFails(requestorId, USER_GROUP_USER_RELATIONSHIPS_PATH, targetRelationId);
        assertThat(getEntitiesAsTypeByUserForApp(requestorId,
                                                 DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                                                 USER_GROUP_USER_RELATIONSHIPS_PATH,
                                                 UserGroupUserRelationshipDto.class,
                                                 emptyQueryParams())).hasSize(0);
        updateEntityFails(requestorId, USER_GROUP_USER_RELATIONSHIPS_PATH, targetRelationId, INACTIVATE_RELATION);
        deleteEntityFails(requestorId, USER_GROUP_USER_RELATIONSHIPS_PATH, targetRelationId, SC_NOT_FOUND);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId, ACTIVATE_RELATION);
        getEntitySucceeds(requestorId, USER_GROUP_USER_RELATIONSHIPS_PATH, targetRelationId);
        assertThat(getEntitiesAsTypeByUserForApp(requestorId,
                                                 DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                                                 USER_GROUP_USER_RELATIONSHIPS_PATH,
                                                 UserGroupUserRelationshipDto.class,
                                                 emptyQueryParams())).hasSize(2);
        updateEntitySucceeds(requestorId, USER_GROUP_USER_RELATIONSHIPS_PATH, targetRelationId, INACTIVATE_RELATION);
        deleteEntitySucceeds(requestorId, USER_GROUP_USER_RELATIONSHIPS_PATH, targetRelationId);
    }
}
