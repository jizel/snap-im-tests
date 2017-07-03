package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;

@Log
public class CustomerHelpers extends CustomerSteps {

    public CustomerHelpers() {
        super();
    }

    public Response createCustomerByUserForApp(String userId, String applicationId, CustomerCreateDto customer) throws JsonProcessingException {
        Response createResponse = createEntityByUserForApplication(userId, applicationId, retrieveData(customer).toString());
        setSessionResponse(createResponse);
        return createResponse;
    }

    public CustomerDto customerIsCreated(CustomerCreateDto customer) throws JsonProcessingException {
        Response response = createCustomerByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, customer);
        assertEquals(String.format("Failed to create customer: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        setSessionResponse(response);
        return response.as(CustomerDto.class);
    }

    public void customerIsUpdated(String customerId, CustomerUpdateDto customerUpdate) {
        Response response = updateCustomer(customerId, customerUpdate);
        assertThat(String.format("Failed to delete customer: %s", response.toString()), response.getStatusCode(), is(SC_NO_CONTENT));
    }

    public void customerIsDeleted(String customerId) {
        Response response = deleteCustomer(customerId);
        assertThat(String.format("Failed to delete customer: %s", response.toString()), response.getStatusCode(), is(SC_NO_CONTENT));
    }
}
