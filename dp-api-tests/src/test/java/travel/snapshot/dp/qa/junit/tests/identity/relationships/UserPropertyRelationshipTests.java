package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_property_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserPropertyRelationshipTests extends CommonTest {
    private PropertyDto createdProperty1;
    private UserDto createdUser1;
    private UserPropertyRelationshipDto testUserPropertyRelationship;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdProperty1 = propertyHelpers.propertyIsCreated(testProperty1);
        createdUser1 = userHelpers.userIsCreated(testUser1);
        testUserPropertyRelationship = relationshipsHelpers.constructUserPropertyRelationshipDto(createdUser1.getId(), createdProperty1.getId(), true);
    }

    
    @Category(Categories.SmokeTests.class)
    @Test
    public void createUserPropertyRelationship() {
        Response response = commonHelpers.createEntity(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserPropertyRelationshipDto returnedRelationship = response.as(UserPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1.getId()));
        assertThat(returnedRelationship.getUserId(), is(createdUser1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserPropertyRelationshipDto requestedRelationship = commonHelpers.getEntityAsType(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserPropertyRelationshipErrors() {
        testUserPropertyRelationship = relationshipsHelpers.constructUserPropertyRelationshipDto(NON_EXISTENT_ID, createdProperty1.getId(), true);
        commonHelpers.createEntity(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserPropertyRelationship = relationshipsHelpers.constructUserPropertyRelationshipDto(createdUser1.getId(), NON_EXISTENT_ID, true);
        commonHelpers.createEntity(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updateUserPropertyRelationship() throws Exception {
        UserPropertyRelationshipDto UserPropertyRelationship = commonHelpers.entityWithTypeIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, testUserPropertyRelationship);
        UserPropertyRelationshipUpdateDto update = relationshipsHelpers.constructUserPropertyRelationshipUpdateDto(false);
        commonHelpers.updateEntityPost(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserPropertyRelationshipDto returnedRelationship = commonHelpers.getEntityAsType(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, UserPropertyRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserPropertyRelationship(){
        UserPropertyRelationshipDto UserPropertyRelationship = commonHelpers.entityWithTypeIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, testUserPropertyRelationship);
        commonHelpers.deleteEntity(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
