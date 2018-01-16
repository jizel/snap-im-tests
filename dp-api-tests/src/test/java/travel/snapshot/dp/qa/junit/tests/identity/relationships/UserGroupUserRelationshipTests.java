package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.getSessionResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationshipUpdate;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_group_user_relationships endpoint
 */
public class UserGroupUserRelationshipTests extends CommonTest {
    private UUID createdUserId;
    private UUID createdUserGroupId;
    private UserGroupUserRelationshipCreateDto testUserGroupUserRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdUserId = entityIsCreated(testUser1);
        createdUserGroupId = entityIsCreated(testUserGroup1);
        testUserGroupUserRelationship = constructUserGroupUserRelationship(createdUserGroupId, createdUserId, true);
    }

    @Test
    public void userGroupUserRelationshipCRUD() {
        UUID relationId = entityIsCreated(testUserGroupUserRelationship);
        UserGroupUserRelationshipDto returnedRelationship = getSessionResponse().as(UserGroupUserRelationshipDto.class);
        assertThat(returnedRelationship.getUserId(), is(createdUserId));
        assertThat(returnedRelationship.getUserGroupId(), is(createdUserGroupId));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserGroupUserRelationshipUpdateDto update = constructUserGroupUserRelationshipUpdate(false);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId, update);
        entityIsDeleted(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId);
    }

    @Test
    public void createUserGroupUserRelationshipErrors() {
        testUserGroupUserRelationship = constructUserGroupUserRelationship(NON_EXISTENT_ID, createdUserId, true);
        createEntity(USER_GROUP_USER_RELATIONSHIPS_PATH, testUserGroupUserRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserGroupUserRelationship = constructUserGroupUserRelationship(createdUserGroupId, NON_EXISTENT_ID, true);
        createEntity(USER_GROUP_USER_RELATIONSHIPS_PATH, testUserGroupUserRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }
}
