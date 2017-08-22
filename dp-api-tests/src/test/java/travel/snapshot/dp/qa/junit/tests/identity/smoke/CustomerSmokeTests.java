package travel.snapshot.dp.qa.junit.tests.identity.smoke;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_METHOD_NOT_ALLOWED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;
import travel.snapshot.dp.qa.junit.utils.EntityNonNullMap;

import java.time.LocalDate;
import java.util.UUID;

@Category(Categories.Authorization.class)
public class CustomerSmokeTests extends CommonSmokeTest {

    private EntityNonNullMap<String, CustomerCreateDto> customerDtos = entitiesLoader.getCustomerDtos();

    @Test
    public void customerCRUDWithAuthorization() throws Throwable {
        UUID customerId = customerDtos.get("customer1").getId();
        //create
        customerDtos.values().forEach(customer -> {
            customerHelpers.customerIsCreatedWithAuth(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
        //request
        authorizationHelpers.getEntity(CUSTOMERS_PATH, customerId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("customer_code");
        bodyContainsEntityWith("name", "Creation test company1");
        bodyContainsEntityWith("email", "s1@tenants.biz");
        //update
        customerHelpers.setCustomerIsActiveWithAuthorization(testCustomer1.getId(), false);
        responseCodeIs(SC_NO_CONTENT);
        //delete
        authorizationHelpers.entityIsDeleted(CUSTOMERS_PATH, customerId);
    }

    @Test
    public void addRemovePropertyToCustomerUsingOldWay() {
        customerHelpers.addPropertyToCustomerWithAuthUsingPartialDto(DEFAULT_PROPERTY_ID, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        bodyContainsEntityWith("property_id", DEFAULT_PROPERTY_ID.toString());
        bodyContainsEntityWith("relationship_type", "chain");
        customerHelpers.removeCustomerPropertyWithAuthUsingPartialDto(DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID);
        responseCodeIs(SC_METHOD_NOT_ALLOWED);
    }

    @Test
    public void customerPropertyCRUD() {
        // create
        UUID relationId = relationshipsHelpers.customerPropertyRelationIsCreatedWithAuth(
                DEFAULT_SNAPSHOT_CUSTOMER_ID,
                DEFAULT_PROPERTY_ID,
                true,
                CustomerPropertyRelationshipType.CHAIN,
                LocalDate.parse("2015-01-01"),
                LocalDate.parse("2020-01-01"));
        // get
        authorizationHelpers.getEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("property_id", DEFAULT_PROPERTY_ID.toString());
        bodyContainsEntityWith("customer_id", DEFAULT_SNAPSHOT_CUSTOMER_ID.toString());
        bodyContainsEntityWith("valid_from", "2015-01-01");

        // update
        relationshipsHelpers.updateCustomerPropertyRelationshipWithAuth(relationId, null, CustomerPropertyRelationshipType.DATA_OWNER, null, null);
        responseCodeIs(SC_NO_CONTENT);
        // delete
        authorizationHelpers.entityIsDeleted(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
    }

    @Test
    public void customerUserCRUD() throws Throwable {
        // create a user
        UUID userId = userHelpers.userIsCreatedWithAuth(testUser1);
        // create a customer
        UUID customerId = customerHelpers.customerIsCreatedWithAuth(testCustomer1);
        // create relation
        UUID relationId = relationshipsHelpers.userCustomerRelationIsCreatedWithAuth(
                userId,
                customerId,
                true,
                false);
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
