package travel.snapshot.dp.qa.junit.tests.identity.customers;


import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;

import com.jayway.restassured.response.Response;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;


/**
 * Sample customer tests using YAML data
 */
public class CustomersCRUDTests extends CommonTest {

    @Test
    public void createAllCustomersTest() {
        customerDtos.values().forEach(customer -> {
            customerHelpers.followingCustomerIsCreated(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
//            TODO: Use existing/make new matchers for DTOs. Assert that createdCustomer has all the attributes the customer has
    }

    @Test
    public void updateCustomer() throws Exception {
        UUID customerId = commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setName("Updated name");
        customerUpdate.setEmail("updated@snapshot.travel");
        customerUpdate.setIsActive(false);
        customerUpdate.setNotes("Updated notes");
        customerUpdate.setParentId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        Response updateResponse = commonHelpers.updateEntity(CUSTOMERS_PATH, customerId, customerUpdate);
        responseCodeIs(SC_OK);
        CustomerDto updateResponseCustomer = updateResponse.as(CustomerDto.class);
        CustomerDto requestedCustomer = commonHelpers.getEntityAsType(CUSTOMERS_PATH, CustomerDto.class, testCustomer1.getId());
        assertThat("Update response body differs from the same customer requested by GET ", updateResponseCustomer, is(requestedCustomer));
    }
}
