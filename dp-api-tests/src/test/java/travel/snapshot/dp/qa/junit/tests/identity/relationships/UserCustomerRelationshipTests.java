package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipUpdate;

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
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_customer_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserCustomerRelationshipTests extends CommonTest{
    private UUID createdUserId;
    private UUID createdCustomerId;
    private UserCustomerRelationshipCreateDto testUserCustomerRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdUserId = entityIsCreated(testUser1);
        createdCustomerId = entityIsCreated(testCustomer1);
        userHelpers.deleteExistingUserCustomerRelationship(createdUserId);
        testUserCustomerRelationship = constructUserCustomerRelationshipDto(createdUserId,
                createdCustomerId, true, true);
    }

    @Test
    @Category(Categories.SmokeTests.class)
    public void createUserCustomerRelationship() {
        Response response = createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserCustomerRelationshipCreateDto returnedRelationship = response.as(UserCustomerRelationshipDto.class);
        assertThat(returnedRelationship.getCustomerId(), is(createdCustomerId));
        assertThat(returnedRelationship.getUserId(), is(createdUserId));
        assertThat(returnedRelationship.getIsActive(), is(true));
        assertThat(returnedRelationship.getIsPrimary(), is(true));
        UserCustomerRelationshipCreateDto requestedRelationship = getEntityAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, returnedRelationship.getId());
        assertThat(requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserCustomerRelationshipErrors() {
        testUserCustomerRelationship = constructUserCustomerRelationshipDto(NON_EXISTENT_ID, createdCustomerId, true, false);
        createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserCustomerRelationship = constructUserCustomerRelationshipDto(createdUserId, NON_EXISTENT_ID, true, false);
        createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserCustomerRelationship = constructUserCustomerRelationshipDto(createdUserId, createdCustomerId, null, null);
        createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @Test
    @Jira("DPIM-52")
    public void updateUserCustomerRelationship() throws Exception {
        UserCustomerRelationshipCreateDto userCustomerRelationship = entityIsCreatedAs(UserCustomerRelationshipDto.class, testUserCustomerRelationship);
        UserCustomerRelationshipUpdateDto update = constructUserCustomerRelationshipUpdate( false, false);
        entityIsUpdated(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId(), update);
        UserCustomerRelationshipCreateDto returnedRelationship = getEntityAsType(USER_CUSTOMER_RELATIONSHIPS_PATH,
                UserCustomerRelationshipDto.class, userCustomerRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
        assertThat(returnedRelationship.getIsPrimary(), is(false));

        //        Errors
        update = constructUserCustomerRelationshipUpdate( null, null);
        assertStatusCodes(updateEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId(), update),
                                       SC_UNPROCESSABLE_ENTITY,
                                       CC_SEMANTIC_ERRORS);
    }

    @Test
    public void deleteUserCustomerRelationship(){
        UserCustomerRelationshipDto userCustomerRelationship = entityIsCreatedAs(UserCustomerRelationshipDto.class, testUserCustomerRelationship);
        entityIsDeleted(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship.getId());
    }

    @Test
    public void createDuplicateRelation() {
        entityIsCreated(testUserCustomerRelationship);
        assertStatusCodes(createEntity(testUserCustomerRelationship), SC_CONFLICT, CC_CONFLICT_VALUES);
    }

    @Test
    public void customerCanNotDeletedIfItHasUser() {
        UUID relationID = entityIsCreated(testUserCustomerRelationship);
        assertStatusCodes(deleteEntity(CUSTOMERS_PATH, createdCustomerId), SC_CONFLICT, CC_ENTITY_REFERENCED);
        assertStatusCodes(deleteEntity(USERS_PATH, createdUserId), SC_CONFLICT, CC_ENTITY_REFERENCED);
        entityIsDeleted(USER_CUSTOMER_RELATIONSHIPS_PATH, relationID);
        entityIsDeleted(USERS_PATH, createdUserId);
        entityIsDeleted(CUSTOMERS_PATH, createdCustomerId);
    }

}