package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_property_set_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserPropertySetRelationshipTests extends CommonTest {
    private PropertySetDto createdPropertySet1;
    private UserDto createdUser1;
    private UserPropertySetRelationshipDto testUserPropertySetRelationship;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdPropertySet1 = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        createdUser1 = userHelpers.userIsCreated(testUser1);
        testUserPropertySetRelationship = relationshipsHelpers.constructUserPropertySetRelationshipDto(createdUser1.getId(), createdPropertySet1.getId(), true);
    }

    @Test
    public void createUserPropertySetRelationship() {
        Response response = commonHelpers.createEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserPropertySetRelationshipDto returnedRelationship = response.as(UserPropertySetRelationshipDto.class);
        assertThat(returnedRelationship.getPropertySetId(), is(createdPropertySet1.getId()));
        assertThat(returnedRelationship.getUserId(), is(createdUser1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserPropertySetRelationshipDto requestedRelationship = commonHelpers.getEntityAsType(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserPropertySetRelationshipErrors() {
        testUserPropertySetRelationship = relationshipsHelpers.constructUserPropertySetRelationshipDto(NON_EXISTENT_ID, createdPropertySet1.getId(), true);
        commonHelpers.createEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        testUserPropertySetRelationship = relationshipsHelpers.constructUserPropertySetRelationshipDto(createdUser1.getId(), NON_EXISTENT_ID, true);
        commonHelpers.createEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
    }

    @Test
    public void updateUserPropertySetRelationship() throws Exception {
        UserPropertySetRelationshipDto UserPropertySetRelationship = commonHelpers.entityWithTypeIsCreated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, testUserPropertySetRelationship);
        UserPropertySetRelationshipUpdateDto update = relationshipsHelpers.constructUserPropertySetRelationshipUpdateDto(false);
        commonHelpers.updateEntityWithEtag(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserPropertySetRelationshipDto returnedRelationship = commonHelpers.getEntityAsType(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, UserPropertySetRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserPropertySetRelationship(){
        UserPropertySetRelationshipDto UserPropertySetRelationship = commonHelpers.entityWithTypeIsCreated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, testUserPropertySetRelationship);
        commonHelpers.deleteEntityWithEtag(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
