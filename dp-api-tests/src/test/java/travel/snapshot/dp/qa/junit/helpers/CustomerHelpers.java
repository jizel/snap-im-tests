package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;


@Log
public class CustomerHelpers extends CustomerSteps {

    public CustomerHelpers() {
        super();
    }

    public void createRandomCustomer(CustomerCreateDto customer) {
        customer.setId(null);
        createCustomer(customer);
    }

    public Response createCustomer(CustomerCreateDto customer) {
        return createCustomerByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, customer);
    }

    /**
     * I made a dedicated createCustomerWithAuthorization method only because of salesforceId field
     * that does not get translated from yaml properly and requires additional magic performed by retrieveData
     * method. This applies only to customers. For all other entities it is enough to call
     * entity%Helpers.createEntityWithAuthorization(entity) directly
     */
    public Response createCustomerWithAuthorization(CustomerCreateDto customer) {
        JSONObject jsonCustomer = null;
        try {
            jsonCustomer = retrieveData(customer);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
        Response createResponse = createEntityWithAuthorization(jsonCustomer.toString());
        setSessionResponse(createResponse);
        return createResponse;
    }

    public Response createCustomerByUserForApp(String userId, String applicationId, CustomerCreateDto customer) {
        JSONObject jsonCustomer = null;
        try {
            jsonCustomer = retrieveData(customer);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
        Response createResponse = createEntityByUserForApplication(userId, applicationId, jsonCustomer.toString());
        setSessionResponse(createResponse);
        return createResponse;
    }

    public CustomerDto customerIsCreated(CustomerCreateDto customer) throws JsonProcessingException {
        Response response = createCustomerByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, customer);
        assertThat(String.format("Failed to create customer: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
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
