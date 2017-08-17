package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_group_user_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserGroupUserRelationshipTests extends CommonTest {
    private UserDto createdUser1;
    private UserGroupDto createdUserGroup1;
    private UserGroupUserRelationshipDto testUserGroupUserRelationship;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdUser1 = userHelpers.userIsCreated(testUser1);
        createdUserGroup1 = userGroupHelpers.userGroupIsCreated(testUserGroup1);
        testUserGroupUserRelationship = relationshipsHelpers.constructUserGroupUserRelationship(createdUserGroup1.getId(), createdUser1.getId(), true);
    }

    @Test
    public void createUserGroupUserRelationship() {
        Response response = commonHelpers.createEntity(USER_GROUP_USER_RELATIONSHIPS_PATH, testUserGroupUserRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserGroupUserRelationshipDto returnedRelationship = response.as(UserGroupUserRelationshipDto.class);
        assertThat(returnedRelationship.getUserId(), is(createdUser1.getId()));
        assertThat(returnedRelationship.getUserGroupId(), is(createdUserGroup1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserGroupUserRelationshipDto requestedRelationship = commonHelpers.getEntityAsType(USER_GROUP_USER_RELATIONSHIPS_PATH, UserGroupUserRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserGroupUserRelationshipErrors() {
        testUserGroupUserRelationship = relationshipsHelpers.constructUserGroupUserRelationship(NON_EXISTENT_ID, createdUser1.getId(), true);
        commonHelpers.createEntity(USER_GROUP_USER_RELATIONSHIPS_PATH, testUserGroupUserRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserGroupUserRelationship = relationshipsHelpers.constructUserGroupUserRelationship(createdUserGroup1.getId(), NON_EXISTENT_ID, true);
        commonHelpers.createEntity(USER_GROUP_USER_RELATIONSHIPS_PATH, testUserGroupUserRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updateUserGroupUserRelationship() throws Exception {
        UserGroupUserRelationshipDto userGroupUserRelationship = commonHelpers.entityWithTypeIsCreated(USER_GROUP_USER_RELATIONSHIPS_PATH, UserGroupUserRelationshipDto.class, testUserGroupUserRelationship);
        UserGroupUserRelationshipUpdateDto update = relationshipsHelpers.constructUserGroupUserRelationshipUpdate(false);
        commonHelpers.updateEntityPost(USER_GROUP_USER_RELATIONSHIPS_PATH, userGroupUserRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserGroupUserRelationshipDto returnedRelationship = commonHelpers.getEntityAsType(USER_GROUP_USER_RELATIONSHIPS_PATH, UserGroupUserRelationshipDto.class, userGroupUserRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserGroupUserRelationship(){
        UserGroupUserRelationshipDto userGroupUserRelationship = commonHelpers.entityWithTypeIsCreated(USER_GROUP_USER_RELATIONSHIPS_PATH, UserGroupUserRelationshipDto.class, testUserGroupUserRelationship);
        commonHelpers.deleteEntity(USER_GROUP_USER_RELATIONSHIPS_PATH, userGroupUserRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_GROUP_USER_RELATIONSHIPS_PATH, userGroupUserRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
