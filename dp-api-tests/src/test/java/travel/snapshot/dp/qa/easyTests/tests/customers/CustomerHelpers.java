package travel.snapshot.dp.qa.easyTests.tests.customers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;

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
        return response.as(CustomerDto.class);
    }
}
