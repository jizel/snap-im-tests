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
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_customer_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserCustomerRelationshipTests extends CommonTest{
    private UserDto createdUser1;
    private CustomerDto createdCustomer1;
    private UserCustomerRelationshipDto testUserCustomerRelationship;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdUser1 = userHelpers.userIsCreated(testUser1);
        createdCustomer1 = customerHelpers.customerIsCreated(testCustomer1);
        testUserCustomerRelationship = relationshipsHelpers.constructUserCustomerRelationshipDto(createdUser1.getId(),
                createdCustomer1.getId(), true, true);
    }

    @Test
    @Category(Categories.SmokeTests.class)
    public void createUserCustomerRelationship() {
        Response response = commonHelpers.createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserCustomerRelationshipDto returnedRelationship = response.as(UserCustomerRelationshipDto.class);
        assertThat(returnedRelationship.getCustomerId(), is(createdCustomer1.getId()));
        assertThat(returnedRelationship.getUserId(), is(createdUser1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        assertThat(returnedRelationship.getIsPrimary(), is(true));
        UserCustomerRelationshipDto requestedRelationship = commonHelpers.getEntityAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, returnedRelationship.getId());
        assertThat(requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserCustomerRelationshipErrors() {
        testUserCustomerRelationship = relationshipsHelpers.constructUserCustomerRelationshipDto(NON_EXISTENT_ID, createdCustomer1.getId(), true, false);
        commonHelpers.createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        testUserCustomerRelationship = relationshipsHelpers.constructUserCustomerRelationshipDto(createdUser1.getId(), NON_EXISTENT_ID, true, false);
        commonHelpers.createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        testUserCustomerRelationship = relationshipsHelpers.constructUserCustomerRelationshipDto(createdUser1.getId(), createdCustomer1.getId(), null, null);
        commonHelpers.createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(SEMANTIC_ERRORS_CUSTOM_CODE);
    }

    @Test
    @Jira("DPIM-52")
    public void updateUserCustomerRelationship() throws Exception {
        UserCustomerRelationshipDto userCustomerRelationship = commonHelpers.entityWithTypeIsCreated(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, testUserCustomerRelationship);
        UserCustomerRelationshipUpdateDto update = relationshipsHelpers.constructUserCustomerRelationshipUpdate( false, false);
        commonHelpers.updateEntityWithEtag(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserCustomerRelationshipDto returnedRelationship = commonHelpers.getEntityAsType(USER_CUSTOMER_RELATIONSHIPS_PATH,
                UserCustomerRelationshipDto.class, userCustomerRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
        assertThat(returnedRelationship.getIsPrimary(), is(false));

        //        Errors
        update = relationshipsHelpers.constructUserCustomerRelationshipUpdate( null, null);
        commonHelpers.updateEntityWithEtag(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId(), update);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(SEMANTIC_ERRORS_CUSTOM_CODE);
    }

    @Test
    public void deleteUserCustomerRelationship(){
        UserCustomerRelationshipDto userCustomerRelationship = commonHelpers.entityWithTypeIsCreated(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, testUserCustomerRelationship);
        commonHelpers.deleteEntityWithEtag(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }

}
