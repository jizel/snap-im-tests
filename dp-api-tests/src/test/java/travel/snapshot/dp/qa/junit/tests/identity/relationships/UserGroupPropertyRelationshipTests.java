package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_group_property_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserGroupPropertyRelationshipTests extends CommonTest {
    private PropertyDto createdProperty1;
    private UserGroupDto createdUserGroup1;
    private UserGroupPropertyRelationshipDto testUserGroupPropertyRelationship;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdProperty1 = propertyHelpers.propertyIsCreated(testProperty1);
        createdUserGroup1 = userGroupHelpers.userGroupIsCreated(testUserGroup1);
        testUserGroupPropertyRelationship = relationshipsHelpers.constructUserGroupPropertyRelationship(createdUserGroup1.getId(), createdProperty1.getId(), true);
    }

    @Test
    public void createUserGroupPropertyRelationship() {
        Response response = commonHelpers.createEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, testUserGroupPropertyRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserGroupPropertyRelationshipDto returnedRelationship = response.as(UserGroupPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1.getId()));
        assertThat(returnedRelationship.getUserGroupId(), is(createdUserGroup1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserGroupPropertyRelationshipDto requestedRelationship = commonHelpers.getEntityAsType(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, UserGroupPropertyRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserGroupPropertyRelationshipErrors() {
        testUserGroupPropertyRelationship = relationshipsHelpers.constructUserGroupPropertyRelationship(NON_EXISTENT_ID, createdProperty1.getId(), true);
        commonHelpers.createEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, testUserGroupPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserGroupPropertyRelationship = relationshipsHelpers.constructUserGroupPropertyRelationship(createdUserGroup1.getId(), NON_EXISTENT_ID, true);
        commonHelpers.createEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, testUserGroupPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updateUserGroupPropertyRelationship() throws Exception {
        UserGroupPropertyRelationshipDto userGroupPropertyRelationship = commonHelpers.entityWithTypeIsCreated(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, UserGroupPropertyRelationshipDto.class, testUserGroupPropertyRelationship);
        UserGroupPropertyRelationshipUpdateDto update = relationshipsHelpers.constructUserGroupPropertyRelationshipUpdate(false);
        commonHelpers.updateEntityPost(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, userGroupPropertyRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserGroupPropertyRelationshipDto returnedRelationship = commonHelpers.getEntityAsType(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, UserGroupPropertyRelationshipDto.class, userGroupPropertyRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserGroupPropertyRelationship(){
        UserGroupPropertyRelationshipDto userGroupPropertyRelationship = commonHelpers.entityWithTypeIsCreated(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, UserGroupPropertyRelationshipDto.class, testUserGroupPropertyRelationship);
        commonHelpers.deleteEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, userGroupPropertyRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, userGroupPropertyRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
