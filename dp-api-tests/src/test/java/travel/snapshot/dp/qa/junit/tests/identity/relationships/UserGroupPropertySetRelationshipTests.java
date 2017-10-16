package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_group_property_set_relationships endpoint
 */
public class UserGroupPropertySetRelationshipTests extends CommonTest {
    private UUID createdPropertySetId;
    private UUID createdUserGroupId;
    private UserGroupPropertySetRelationshipCreateDto testUserGroupPropertySetRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdPropertySetId = entityIsCreated(testPropertySet1);
        createdUserGroupId = entityIsCreated(testUserGroup1);
        testUserGroupPropertySetRelationship = relationshipsHelpers.constructUserGroupPropertySetRelationship(createdUserGroupId, createdPropertySetId, true);
    }

    @Test
    public void createUserGroupPropertySetRelationship() {
        Response response = createEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, testUserGroupPropertySetRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserGroupPropertySetRelationshipCreateDto returnedRelationship = response.as(UserGroupPropertySetRelationshipDto.class);
        assertThat(returnedRelationship.getPropertySetId(), is(createdPropertySetId));
        assertThat(returnedRelationship.getUserGroupId(), is(createdUserGroupId));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserGroupPropertySetRelationshipCreateDto requestedRelationship = getEntityAsType(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, UserGroupPropertySetRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserGroupPropertySetRelationshipErrors() {
        testUserGroupPropertySetRelationship = relationshipsHelpers.constructUserGroupPropertySetRelationship(NON_EXISTENT_ID, createdPropertySetId, true);
        createEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, testUserGroupPropertySetRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserGroupPropertySetRelationship = relationshipsHelpers.constructUserGroupPropertySetRelationship(createdUserGroupId, NON_EXISTENT_ID, true);
        createEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, testUserGroupPropertySetRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updateUserGroupPropertySetRelationship() throws Exception {
        UserGroupPropertySetRelationshipCreateDto userGroupPropertySetRelationship = entityIsCreatedAs(UserGroupPropertySetRelationshipDto.class, testUserGroupPropertySetRelationship);
        UserGroupPropertySetRelationshipUpdateDto update = relationshipsHelpers.constructUserGroupPropertySetRelationshipUpdate(false);
        commonHelpers.updateEntityPost(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, userGroupPropertySetRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserGroupPropertySetRelationshipCreateDto returnedRelationship = getEntityAsType(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, UserGroupPropertySetRelationshipDto.class, userGroupPropertySetRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserGroupPropertySetRelationship(){
        UserGroupPropertySetRelationshipCreateDto userGroupPropertySetRelationship = entityIsCreatedAs(UserGroupPropertySetRelationshipDto.class, testUserGroupPropertySetRelationship);
        deleteEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, userGroupPropertySetRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        getEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, userGroupPropertySetRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
