package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_group_property_set_relationships endpoint
 */
public class UserGroupPropertySetRelationshipTests extends CommonTest {
    private PropertySetDto createdPropertySet1;
    private UserGroupDto createdUserGroup1;
    private UserGroupPropertySetRelationshipDto testUserGroupPropertySetRelationship;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdPropertySet1 = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        createdUserGroup1 = userGroupHelpers.userGroupIsCreated(testUserGroup1);
        testUserGroupPropertySetRelationship = relationshipsHelpers.constructUserGroupPropertySetRelationship(createdUserGroup1.getId(), createdPropertySet1.getId(), true);
    }

    @Test
    public void createUserGroupPropertySetRelationship() {
        Response response = commonHelpers.createEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, testUserGroupPropertySetRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserGroupPropertySetRelationshipDto returnedRelationship = response.as(UserGroupPropertySetRelationshipDto.class);
        assertThat(returnedRelationship.getPropertySetId(), is(createdPropertySet1.getId()));
        assertThat(returnedRelationship.getUserGroupId(), is(createdUserGroup1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserGroupPropertySetRelationshipDto requestedRelationship = commonHelpers.getEntityAsType(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, UserGroupPropertySetRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserGroupPropertySetRelationshipErrors() {
        testUserGroupPropertySetRelationship = relationshipsHelpers.constructUserGroupPropertySetRelationship(NON_EXISTENT_ID, createdPropertySet1.getId(), true);
        commonHelpers.createEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, testUserGroupPropertySetRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserGroupPropertySetRelationship = relationshipsHelpers.constructUserGroupPropertySetRelationship(createdUserGroup1.getId(), NON_EXISTENT_ID, true);
        commonHelpers.createEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, testUserGroupPropertySetRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updateUserGroupPropertySetRelationship() throws Exception {
        UserGroupPropertySetRelationshipDto userGroupPropertySetRelationship = commonHelpers.entityWithTypeIsCreated(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, UserGroupPropertySetRelationshipDto.class, testUserGroupPropertySetRelationship);
        UserGroupPropertySetRelationshipUpdateDto update = relationshipsHelpers.constructUserGroupPropertySetRelationshipUpdate(false);
        commonHelpers.updateEntityPost(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, userGroupPropertySetRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserGroupPropertySetRelationshipDto returnedRelationship = commonHelpers.getEntityAsType(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, UserGroupPropertySetRelationshipDto.class, userGroupPropertySetRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserGroupPropertySetRelationship(){
        UserGroupPropertySetRelationshipDto userGroupPropertySetRelationship = commonHelpers.entityWithTypeIsCreated(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, UserGroupPropertySetRelationshipDto.class, testUserGroupPropertySetRelationship);
        commonHelpers.deleteEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, userGroupPropertySetRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, userGroupPropertySetRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
