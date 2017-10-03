package travel.snapshot.dp.qa.junit.tests.identity.smoke;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.DATA_OWNER;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;
import travel.snapshot.dp.qa.junit.utils.EntityNonNullMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Category(Categories.Authorization.class)
public class CustomerSmokeTests extends CommonSmokeTest {

    private EntityNonNullMap<String, CustomerCreateDto> customerDtos = null;

    @Before
    public void setUp() {
        super.setUp();
        customerDtos = entitiesLoader.getCustomerDtos();
    }

    @Test
    public void customerCRUDWithAuthorization() throws Throwable {
        ArrayList<UUID> customerIds = new ArrayList<UUID>();
        //create
        customerDtos.values().forEach(customer -> {
            customerIds.add(authorizationHelpers.entityIsCreated(customer));
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
        //request
        authorizationHelpers.getEntity(CUSTOMERS_PATH, customerIds.get(0));
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("customer_code");
        bodyContainsEntityWith("name", "Creation test company1");
        bodyContainsEntityWith("email", "s1@tenants.biz");
        //update
        CustomerUpdateDto update = new CustomerUpdateDto();
        update.setIsActive(false);
        authorizationHelpers.entityIsUpdated(CUSTOMERS_PATH, customerIds.get(0), update);
        //delete
        authorizationHelpers.entityIsDeleted(CUSTOMERS_PATH, customerIds.get(0));
    }

    @Test
    public void customerPropertyCRUD() {
        // create
        CustomerPropertyRelationshipCreateDto relation = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                DEFAULT_SNAPSHOT_CUSTOMER_ID,
                DEFAULT_PROPERTY_ID,
                true,
                CHAIN,
                LocalDate.parse("2015-01-01"),
                LocalDate.parse("2020-01-01"));
        UUID relationId = authorizationHelpers.entityIsCreated(relation);

        // get
        authorizationHelpers.getEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("property_id", DEFAULT_PROPERTY_ID.toString());
        bodyContainsEntityWith("customer_id", DEFAULT_SNAPSHOT_CUSTOMER_ID.toString());
        bodyContainsEntityWith("valid_from", "2015-01-01");

        // update
        CustomerPropertyRelationshipUpdateDto update = new CustomerPropertyRelationshipUpdateDto();
        update.setType(DATA_OWNER);
        authorizationHelpers.entityIsUpdated(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId, update);
        // delete
        authorizationHelpers.entityIsDeleted(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
    }

    @Test
    public void customerUserCRUD() throws Throwable {
        // create a user
        UUID userId = userHelpers.userIsCreatedWithAuth(testUser1);
        // create a customer
        UUID customerId = authorizationHelpers.entityIsCreated(testCustomer1);
        // create relation
        UserCustomerRelationshipCreateDto relation = relationshipsHelpers.constructUserCustomerRelationshipDto(
                userId,
                customerId,
                true,
                false);
        UUID relationId = authorizationHelpers.entityIsCreated(relation);
        // get relation
        authorizationHelpers.getEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("user_id", userId.toString());
        bodyContainsEntityWith("customer_id", customerId.toString());
        bodyContainsEntityWith("is_active", "true");
        bodyContainsEntityWith("is_primary", "false");
        // update
        UserCustomerRelationshipUpdateDto update = new UserCustomerRelationshipUpdateDto();
        update.setIsActive(false);
        authorizationHelpers.entityIsUpdated(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId, update);
        // make sure changes applied
        authorizationHelpers.getEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("is_active", "false");
        // delete relation
        authorizationHelpers.entityIsDeleted(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId);
    }
}
