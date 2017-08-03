package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.*;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;

import java.time.LocalDate;


@Log
public class CustomerHelpers extends CustomerSteps {

    private final CommonHelpers commonHelpers = new CommonHelpers();
    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();

    public CustomerHelpers() {
        super();
    }

    public void createRandomCustomer(CustomerCreateDto customer) {
        customer.setId(null);
        createCustomer(customer);
    }

    private void createCustomer(CustomerCreateDto customer) {
        createCustomerByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, customer);
    }

    /**
     * I made a dedicated createCustomerWithAuthorization method only because of salesforceId field
     * that does not get translated from yaml properly and requires additional magic performed by retrieveData
     * method. This applies only to customers. For all other entities it is enough to call
     * entity%Helpers.createEntityWithAuthorization(entity) directly
     */
    public void createCustomerWithAuthorization(CustomerCreateDto customer) {
        JSONObject jsonCustomer = null;
        try {
            jsonCustomer = retrieveData(customer);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
        Response createResponse = authorizationHelpers.createEntity(BASE_PATH_CUSTOMERS, jsonCustomer.toString());
        setSessionResponse(createResponse);
    }

    public String customerIsCreatedWithAuth(CustomerCreateDto customer) {
        createCustomerWithAuthorization(customer);
        responseCodeIs(SC_CREATED);
        String customerId = getSessionResponse().as(CustomerDto.class).getId();
        commonHelpers.updateRegistryOfDeletables("customers", customerId);
        return customerId;
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

    public void setCustomerIsActiveWithAuthorization(String id, boolean isActive) throws Throwable {
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setIsActive(isActive);
        String updatedCustomerString = retrieveData(customerUpdate).toString();
        assertThat("Empty property update", updatedCustomerString, not(equalToIgnoringCase(CURLY_BRACES_EMPTY)));
        String etag = authorizationHelpers.getEntityEtag(BASE_PATH_CUSTOMERS, id);
        authorizationHelpers.updateEntity(BASE_PATH_CUSTOMERS, id, updatedCustomerString, etag);
    }

    public String addPropertyToCustomerWithAuthUsingPartialDto(String propertyId, String customerId) {
        CustomerPropertyRelationshipPartialDto relation = new CustomerPropertyRelationshipPartialDto();
        relation.setPropertyId(DEFAULT_PROPERTY_ID);
        relation.setIsActive(true);
        relation.setType(CustomerPropertyRelationshipType.CHAIN);
        relation.setValidFrom(LocalDate.parse("2015-01-01"));
        relation.setValidTo(LocalDate.parse("2019-01-01"));
        CustomerPropertyRelationshipPartialDto customerProperty = authorizationHelpers.createSecondLevelRelation(BASE_PATH_CUSTOMERS, DEFAULT_SNAPSHOT_CUSTOMER_ID, SECOND_LEVEL_OBJECT_PROPERTIES, relation)
                .as(CustomerPropertyRelationshipPartialDto.class);
        String relationId = customerProperty.getId();
        commonHelpers.updateRegistryOfDeletables(CUSTOMER_PROPERTIES, relationId);
        return relationId;
    }

    public void removeCustomerPropertyWithAuthUsingPartialDto(String customerId, String propertyId) {
        authorizationHelpers.deleteSecondLevelEntity(BASE_PATH_CUSTOMERS, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId);
    }
}
