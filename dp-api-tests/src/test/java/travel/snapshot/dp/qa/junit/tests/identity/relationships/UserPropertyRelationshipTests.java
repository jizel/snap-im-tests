package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipUpdateDto;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_property_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserPropertyRelationshipTests extends CommonTest {
    private UUID createdProperty1Id;
    private UUID createdUserId;
    private UserPropertyRelationshipCreateDto testUserPropertyRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdProperty1Id = entityIsCreated(testProperty1);
        createdUserId = entityIsCreated(testUser1);
        testUserPropertyRelationship = constructUserPropertyRelationshipDto(createdUserId, createdProperty1Id, true);
    }

    
    @Category(Categories.SmokeTests.class)
    @Test
    public void createUserPropertyRelationship() {
        Response response = createEntity(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserPropertyRelationshipCreateDto returnedRelationship = response.as(UserPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1Id));
        assertThat(returnedRelationship.getUserId(), is(createdUserId));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserPropertyRelationshipCreateDto requestedRelationship = getEntityAsType(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserPropertyRelationshipErrors() {
        testUserPropertyRelationship = constructUserPropertyRelationshipDto(NON_EXISTENT_ID, createdProperty1Id, true);
        createEntity(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserPropertyRelationship = constructUserPropertyRelationshipDto(createdUserId, NON_EXISTENT_ID, true);
        createEntity(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updateUserPropertyRelationship() throws Exception {
        UserPropertyRelationshipCreateDto UserPropertyRelationship = entityIsCreatedAs(UserPropertyRelationshipDto.class, testUserPropertyRelationship);
        UserPropertyRelationshipUpdateDto update = constructUserPropertyRelationshipUpdateDto(false);
        commonHelpers.updateEntityPost(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserPropertyRelationshipCreateDto returnedRelationship = getEntityAsType(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, UserPropertyRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserPropertyRelationship(){
        UserPropertyRelationshipCreateDto UserPropertyRelationship = entityIsCreatedAs(UserPropertyRelationshipDto.class, testUserPropertyRelationship);
        deleteEntity(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        getEntity(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }

    @Test
    public void addDuplicateUserPropertyRelationship() {
        entityIsCreated(testUserPropertyRelationship);
        createEntity(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship);
        responseIsConflictValues();
    }
}
