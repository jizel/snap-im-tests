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

    public Response createCustomer(CustomerCreateDto createdCustomer) {
        Response createResponse = null;
        try {
            JSONObject jsonCustomer = retrieveData(createdCustomer);
            createResponse = createEntity(jsonCustomer.toString());
        } catch (JsonProcessingException e) {
            log.severe("Unable to convert customer object to json");
        }
        return createResponse;
    }

    public CustomerDto customerIsCreated(CustomerCreateDto createdCustomer) {
        Response response = createCustomer(createdCustomer);
        assertEquals(String.format("Failed to create customer: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        setSessionResponse(response);
        return response.as(CustomerDto.class);
    }

    public void customerIsUpdated(String customerId, CustomerUpdateDto customerUpdate){
        Response response = updateCustomer(customerId, customerUpdate);
        assertThat(String.format("Failed to delete customer: %s", response.toString()), response.getStatusCode(), is(SC_NO_CONTENT));
    }

    public void customerIsDeleted(String customerId){
        Response response = deleteCustomer(customerId);
        assertThat(String.format("Failed to delete customer: %s", response.toString()), response.getStatusCode(), is(SC_NO_CONTENT));
    }
}
