package travel.snapshot.dp.qa.junit.tests.identity.smoke;

import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;
import travel.snapshot.dp.qa.junit.utils.EntityNonNullMap;

import static org.apache.http.HttpStatus.*;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps.BASE_PATH_CUSTOMERS;

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
        String relationId = customerHelpers.addPropertyToCustomerWithAuthUsingPartialDto(DEFAULT_PROPERTY_ID, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        bodyContainsEntityWith("property_id", DEFAULT_PROPERTY_ID);
        bodyContainsEntityWith("relationship_type", "chain");
        customerHelpers.removeCustomerPropertyWithAuthUsingPartialDto(DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID);
        responseCodeIs(SC_METHOD_NOT_ALLOWED);
        relationshipsHelpers.deleteCustomerPropertyRelationshipWithAuth(relationId);
        responseCodeIs(SC_NO_CONTENT);
    }
}
