package travel.snapshot.dp.qa.junit.tests.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_customer_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserCustomerRelationshipTests extends CommonTest{
    private UserDto createdUser1;
    private CustomerDto createdCustomer1;
    private Response response;

    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        createdUser1 = userHelpers.userIsCreated(testUser1);
        createdCustomer1 = customerHelpers.customerIsCreated(testCustomer1);
    }

    @After
    public void cleanUp() throws Exception {
    }

    @Test
    public void createUserCustomerRelationship() {
        response = relationshipsHelpers.createUserCustomerRelationship(createdUser1.getId(), createdCustomer1.getId(), true, true);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserCustomerRelationshipDto returnedRelationship = response.as(UserCustomerRelationshipDto.class);
        assertThat(returnedRelationship.getCustomerId(), is(createdCustomer1.getId()));
        assertThat(returnedRelationship.getUserId(), is(createdUser1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        assertThat(returnedRelationship.getIsPrimary(), is(true));
        UserCustomerRelationshipDto requestedRelationship = relationshipsHelpers.getUserCustomerRelationship(returnedRelationship.getId());
        assertThat(requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserCustomerRelationshipErrors() {
        relationshipsHelpers.createUserCustomerRelationship(NON_EXISTENT_ID, createdCustomer1.getId(), true, false);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        relationshipsHelpers.createUserCustomerRelationship(createdUser1.getId(), NON_EXISTENT_ID, true, false);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        relationshipsHelpers.createUserCustomerRelationship(createdUser1.getId(), createdCustomer1.getId(), null, null);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(SEMANTIC_ERRORS_CUSTOM_CODE);
    }

    @Test
    public void updateUserCustomerRelationship() throws Exception {
        UserCustomerRelationshipDto userCustomerRelationship = relationshipsHelpers.userCustomerRelationshipIsCreated(createdUser1.getId(), createdCustomer1.getId(), true, true);
        relationshipsHelpers.updateUserCustomerRelationship(userCustomerRelationship.getId(), false, false);
        responseCodeIs(SC_NO_CONTENT);
        UserCustomerRelationshipDto returnedRelationship = relationshipsHelpers.getUserCustomerRelationship(userCustomerRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
        assertThat(returnedRelationship.getIsPrimary(), is(false));
//        Errors
        relationshipsHelpers.updateUserCustomerRelationship(userCustomerRelationship.getId(), null, null);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(SEMANTIC_ERRORS_CUSTOM_CODE);
    }

    @Test
    public void deleteUserCustomerRelationship(){
        UserCustomerRelationshipDto userCustomerRelationship = relationshipsHelpers.userCustomerRelationshipIsCreated(createdUser1.getId(), createdCustomer1.getId(), true, true);
        relationshipsHelpers.deleteUserCustomerRelationship(userCustomerRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        UserCustomerRelationshipDto returnedRelationship = relationshipsHelpers.getUserCustomerRelationship(userCustomerRelationship.getId());
        assertNull(returnedRelationship);
    }

}
