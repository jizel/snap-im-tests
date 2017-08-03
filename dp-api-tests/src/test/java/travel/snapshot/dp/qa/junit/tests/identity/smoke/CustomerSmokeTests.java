package travel.snapshot.dp.qa.junit.tests.identity.smoke;

import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;
import travel.snapshot.dp.qa.junit.utils.EntityNonNullMap;

import java.time.LocalDate;

import static org.apache.http.HttpStatus.*;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps.BASE_PATH_CUSTOMERS;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.CUSTOMER_PROPERTY_RELATIONSHIP_PATH;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.USER_CUSTOMER_RELATIONSHIP_PATH;

@RunWith(SerenityRunner.class)
public class CustomerSmokeTests extends CommonSmokeTest {

    private EntityNonNullMap<String, CustomerCreateDto> customerDtos = entitiesLoader.getCustomerDtos();
    protected RequestSpecification spec = null;

    @Test
    public void customerCRUDWithAuthorization() throws Throwable {
        String customerId = customerDtos.get("customer1").getId();
        //create
        customerDtos.values().forEach(customer -> {
            customerHelpers.customerIsCreatedWithAuth(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
        //request
        authorizationHelpers.getEntity(BASE_PATH_CUSTOMERS, customerId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("customer_code");
        bodyContainsEntityWith("name", "Creation test company1");
        bodyContainsEntityWith("email", "s1@tenants.biz");
        //update
        customerHelpers.setCustomerIsActiveWithAuthorization(testCustomer1.getId(), false);
        responseCodeIs(SC_NO_CONTENT);
        //delete
        authorizationHelpers.entityIsDeleted(BASE_PATH_CUSTOMERS, customerId);
    }

    @Test
    public void addRemovePropertyToCustomerUsingOldWay() {
        customerHelpers.addPropertyToCustomerWithAuthUsingPartialDto(DEFAULT_PROPERTY_ID, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        bodyContainsEntityWith("property_id", DEFAULT_PROPERTY_ID);
        bodyContainsEntityWith("relationship_type", "chain");
        customerHelpers.removeCustomerPropertyWithAuthUsingPartialDto(DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID);
        responseCodeIs(SC_METHOD_NOT_ALLOWED);
    }

    @Test
    public void customerPropertyCRUD() {
        // create
        String relationId = relationshipsHelpers.customerPropertyRelationIsCreatedWithAuth(
                DEFAULT_SNAPSHOT_CUSTOMER_ID,
                DEFAULT_PROPERTY_ID,
                true,
                CustomerPropertyRelationshipType.CHAIN,
                LocalDate.parse("2015-01-01"),
                LocalDate.parse("2020-01-01"));
        // get
        authorizationHelpers.getEntity(CUSTOMER_PROPERTY_RELATIONSHIP_PATH, relationId);
        bodyContainsEntityWith("property_id", DEFAULT_PROPERTY_ID);
        bodyContainsEntityWith("customer_id", DEFAULT_SNAPSHOT_CUSTOMER_ID);
        bodyContainsEntityWith("valid_from", "2015-01-01");

        // update
        relationshipsHelpers.updateCustomerPropertyRelationshipWithAuth(relationId, null, CustomerPropertyRelationshipType.DATA_OWNER, null, null);
        responseCodeIs(SC_NO_CONTENT);
        // delete
        relationshipsHelpers.deleteCustomerPropertyRelationshipWithAuth(relationId);
        responseCodeIs(SC_NO_CONTENT);
    }

    @Test
    public void customerUserCRUD() throws Throwable {
        // create a user
        String userId = userHelpers.userIsCreatedWithAuth(testUser1);
        // create a customer
        String customerId = customerHelpers.customerIsCreatedWithAuth(testCustomer1);
        // create relation
        String relationId = relationshipsHelpers.userCustomerRelationIsCreatedWithAuth(
                userId,
                customerId,
                true,
                false);
        // get relation
        authorizationHelpers.getEntity(USER_CUSTOMER_RELATIONSHIP_PATH, relationId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("user_id", userId);
        bodyContainsEntityWith("customer_id", customerId);
        bodyContainsEntityWith("is_active", "true");
        bodyContainsEntityWith("is_primary", "false");
        // delete relation
        authorizationHelpers.deleteEntity(USER_CUSTOMER_RELATIONSHIP_PATH, relationId);
        responseCodeIs(SC_NO_CONTENT);
    }
}
