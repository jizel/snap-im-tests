package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipUpdateDto;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_property_set_relationships endpoint
 */
public class UserPropertySetRelationshipTests extends CommonTest {
    private UUID createdPropertySetId;
    private UUID createdUserId;
    private UserPropertySetRelationshipCreateDto testUserPropertySetRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdPropertySetId = entityIsCreated(testPropertySet1);
        createdUserId = entityIsCreated(testUser1);
        testUserPropertySetRelationship = constructUserPropertySetRelationshipDto(createdUserId, createdPropertySetId, true);
    }

    @Test
    public void createUserPropertySetRelationship() {
        Response response = createEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserPropertySetRelationshipCreateDto returnedRelationship = response.as(UserPropertySetRelationshipDto.class);
        assertThat(returnedRelationship.getPropertySetId(), is(createdPropertySetId));
        assertThat(returnedRelationship.getUserId(), is(createdUserId));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserPropertySetRelationshipCreateDto requestedRelationship = getEntityAsType(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserPropertySetRelationshipErrors() {
        testUserPropertySetRelationship = constructUserPropertySetRelationshipDto(NON_EXISTENT_ID, createdPropertySetId, true);
        createEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserPropertySetRelationship = constructUserPropertySetRelationshipDto(createdUserId, NON_EXISTENT_ID, true);
        createEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updateUserPropertySetRelationship() throws Exception {
        UserPropertySetRelationshipCreateDto UserPropertySetRelationship = entityIsCreatedAs(UserPropertySetRelationshipDto.class, testUserPropertySetRelationship);
        UserPropertySetRelationshipUpdateDto update = constructUserPropertySetRelationshipUpdateDto(false);
        commonHelpers.updateEntityPost(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserPropertySetRelationshipCreateDto returnedRelationship = getEntityAsType(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, UserPropertySetRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserPropertySetRelationship(){
        UserPropertySetRelationshipDto userPropertySetRelationship = entityIsCreatedAs(UserPropertySetRelationshipDto.class, testUserPropertySetRelationship);
        entitiesInRelationshipCannotBeDeleted(userPropertySetRelationship);
        deleteEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, userPropertySetRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, userPropertySetRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }

    private void entitiesInRelationshipCannotBeDeleted(UserPropertySetRelationshipDto userPropertySetRelationship){
        deleteEntity(PROPERTY_SETS_PATH, userPropertySetRelationship.getPropertySetId()).then().statusCode(SC_CONFLICT);
        deleteEntity(USERS_PATH, userPropertySetRelationship.getUserId()).then().statusCode(SC_CONFLICT);
    }
}
