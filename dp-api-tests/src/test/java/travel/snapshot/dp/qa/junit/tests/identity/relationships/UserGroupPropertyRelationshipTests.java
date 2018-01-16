package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertyRelationshipUpdate;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_group_property_relationships endpoint
 */
public class UserGroupPropertyRelationshipTests extends CommonTest {
    private UUID createdPropertyId;
    private UUID createdUserGroupId;
    private UserGroupPropertyRelationshipCreateDto testUserGroupPropertyRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdPropertyId = entityIsCreated(testProperty1);
        createdUserGroupId = entityIsCreated(testUserGroup1);
        testUserGroupPropertyRelationship = constructUserGroupPropertyRelationship(createdUserGroupId, createdPropertyId, true);
    }

    @Test
    public void createUserGroupPropertyRelationship() {
        Response response = createEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, testUserGroupPropertyRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserGroupPropertyRelationshipCreateDto returnedRelationship = response.as(UserGroupPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdPropertyId));
        assertThat(returnedRelationship.getUserGroupId(), is(createdUserGroupId));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserGroupPropertyRelationshipCreateDto requestedRelationship = getEntityAsType(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, UserGroupPropertyRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserGroupPropertyRelationshipErrors() {
        testUserGroupPropertyRelationship = constructUserGroupPropertyRelationship(NON_EXISTENT_ID, createdPropertyId, true);
        createEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, testUserGroupPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserGroupPropertyRelationship = constructUserGroupPropertyRelationship(createdUserGroupId, NON_EXISTENT_ID, true);
        createEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, testUserGroupPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updateUserGroupPropertyRelationship() throws Exception {
        UserGroupPropertyRelationshipCreateDto userGroupPropertyRelationship = entityIsCreatedAs(UserGroupPropertyRelationshipDto.class, testUserGroupPropertyRelationship);
        UserGroupPropertyRelationshipUpdateDto update = constructUserGroupPropertyRelationshipUpdate(false);
        commonHelpers.updateEntityPost(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, userGroupPropertyRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserGroupPropertyRelationshipCreateDto returnedRelationship = getEntityAsType(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, UserGroupPropertyRelationshipDto.class, userGroupPropertyRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserGroupPropertyRelationship(){
        UserGroupPropertyRelationshipCreateDto userGroupPropertyRelationship = entityIsCreatedAs(UserGroupPropertyRelationshipDto.class, testUserGroupPropertyRelationship);
        deleteEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, userGroupPropertyRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        getEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, userGroupPropertyRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
