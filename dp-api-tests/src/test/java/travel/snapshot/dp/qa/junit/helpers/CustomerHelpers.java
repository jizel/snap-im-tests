package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;

import java.time.LocalDate;
import java.util.UUID;


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
    public void createCustomerWithAuth(CustomerCreateDto customer) {
        JSONObject jsonCustomer = null;
        try {
            jsonCustomer = retrieveData(customer);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
        Response createResponse = authorizationHelpers.createEntity(CUSTOMERS_PATH, jsonCustomer.toString());
        setSessionResponse(createResponse);
    }

    public UUID customerIsCreatedWithAuth(CustomerCreateDto customer) {
        createCustomerWithAuth(customer);
        responseCodeIs(SC_CREATED);
        UUID customerId = getSessionResponse().as(CustomerDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(CUSTOMERS_RESOURCE, customerId);
        return customerId;
    }

    public Response createCustomerByUserForApp(UUID userId, UUID applicationId, CustomerCreateDto customer) {
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

    public void customerIsUpdated(UUID customerId, CustomerUpdateDto customerUpdate) {
        Response response = updateCustomer(customerId, customerUpdate);
        assertThat(String.format("Failed to update customer: %s", response.toString()), response.getStatusCode(), is(SC_NO_CONTENT));
    }

    public void customerIsDeleted(UUID customerId) {
        Response response = deleteCustomer(customerId);
        assertThat(String.format("Failed to delete customer: %s", response.toString()), response.getStatusCode(), is(SC_NO_CONTENT));
    }

    public void setCustomerIsActiveWithAuthorization(UUID id, boolean isActive) throws Throwable {
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setIsActive(isActive);
        String updatedCustomerString = retrieveData(customerUpdate).toString();
        assertThat("Empty property update", updatedCustomerString, not(equalToIgnoringCase(CURLY_BRACES_EMPTY)));
        String etag = authorizationHelpers.getEntityEtag(CUSTOMERS_PATH, id);
        authorizationHelpers.updateEntity(CUSTOMERS_PATH, id, updatedCustomerString);
    }

    public UUID addPropertyToCustomerWithAuthUsingPartialDto(UUID propertyId, UUID customerId) {
        CustomerPropertyRelationshipPartialDto relation = new CustomerPropertyRelationshipPartialDto();
        relation.setPropertyId(propertyId);
        relation.setIsActive(true);
        relation.setType(CustomerPropertyRelationshipType.CHAIN);
        relation.setValidFrom(LocalDate.parse("2015-01-01"));
        relation.setValidTo(LocalDate.parse("2019-01-01"));
        CustomerPropertyRelationshipPartialDto customerProperty = authorizationHelpers.createSecondLevelRelation(CUSTOMERS_PATH, customerId, PROPERTIES_RESOURCE, relation)
                .as(CustomerPropertyRelationshipPartialDto.class);
        UUID relationId = customerProperty.getId();
        commonHelpers.updateRegistryOfDeletables(CUSTOMER_PROPERTIES, relationId);
        return relationId;
    }

    public void removeCustomerPropertyWithAuthUsingPartialDto(UUID customerId, UUID propertyId) {
        authorizationHelpers.deleteSecondLevelEntity(CUSTOMERS_PATH, customerId, PROPERTIES_RESOURCE, propertyId);
    }
}
