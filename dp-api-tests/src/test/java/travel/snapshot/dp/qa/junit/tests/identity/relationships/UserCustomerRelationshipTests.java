package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_customer_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserCustomerRelationshipTests extends CommonTest{
    private UserDto createdUser1;
    private UUID createdCustomerId;
    private UserCustomerRelationshipCreateDto testUserCustomerRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdUser1 = userHelpers.userIsCreated(testUser1);
        createdCustomerId = commonHelpers.entityIsCreated(testCustomer1);
        testUserCustomerRelationship = relationshipsHelpers.constructUserCustomerRelationshipDto(createdUser1.getId(),
                createdCustomerId, true, true);
    }

    @Test
    @Category(Categories.SmokeTests.class)
    public void createUserCustomerRelationship() {
        Response response = commonHelpers.createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserCustomerRelationshipCreateDto returnedRelationship = response.as(UserCustomerRelationshipDto.class);
        assertThat(returnedRelationship.getCustomerId(), is(createdCustomerId));
        assertThat(returnedRelationship.getUserId(), is(createdUser1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        assertThat(returnedRelationship.getIsPrimary(), is(true));
        UserCustomerRelationshipCreateDto requestedRelationship = commonHelpers.getEntityAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, returnedRelationship.getId());
        assertThat(requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserCustomerRelationshipErrors() {
        testUserCustomerRelationship = relationshipsHelpers.constructUserCustomerRelationshipDto(NON_EXISTENT_ID, createdCustomerId, true, false);
        commonHelpers.createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserCustomerRelationship = relationshipsHelpers.constructUserCustomerRelationshipDto(createdUser1.getId(), NON_EXISTENT_ID, true, false);
        commonHelpers.createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserCustomerRelationship = relationshipsHelpers.constructUserCustomerRelationshipDto(createdUser1.getId(), createdCustomerId, null, null);
        commonHelpers.createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @Test
    @Jira("DPIM-52")
    public void updateUserCustomerRelationship() throws Exception {
        UserCustomerRelationshipCreateDto userCustomerRelationship = commonHelpers.entityIsCreatedAs(UserCustomerRelationshipDto.class, testUserCustomerRelationship);
        UserCustomerRelationshipUpdateDto update = relationshipsHelpers.constructUserCustomerRelationshipUpdate( false, false);
        commonHelpers.updateEntityPost(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserCustomerRelationshipCreateDto returnedRelationship = commonHelpers.getEntityAsType(USER_CUSTOMER_RELATIONSHIPS_PATH,
                UserCustomerRelationshipDto.class, userCustomerRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
        assertThat(returnedRelationship.getIsPrimary(), is(false));

        //        Errors
        update = relationshipsHelpers.constructUserCustomerRelationshipUpdate( null, null);
        commonHelpers.updateEntityPost(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId(), update);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @Test
    public void deleteUserCustomerRelationship(){
        UserCustomerRelationshipDto userCustomerRelationship = commonHelpers.entityIsCreatedAs(UserCustomerRelationshipDto.class, testUserCustomerRelationship);
        commonHelpers.deleteEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }

}
