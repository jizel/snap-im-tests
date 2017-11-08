package travel.snapshot.dp.qa.junit.tests.identity.customers;


import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static javax.servlet.http.HttpServletResponse.SC_PRECONDITION_FAILED;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_ETAG;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityWithEtag;

import com.jayway.restassured.response.Response;
import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerType;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;
import java.util.UUID;


/**
 * CRUD tests for Customer entity. Positive and negative cases.
 */
public class CustomersCRUDTests extends CommonTest {

    @Test
    public void createAllCustomersTest() {
        customerDtos.values().forEach(customer -> {
            createEntity(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
//            TODO: Use existing/make new matchers for DTOs. Assert that createdCustomer has all the attributes the customer has
    }

    @Test
    public void customerIdMustBeUnique() throws Exception {
        UUID createdCustomerId = entityIsCreated(testCustomer1);
        testCustomer1.setId(createdCustomerId);
        createEntity(testCustomer1);
        responseIsConflictId();
    }

    @Test
    public void parentChildRelationCannotContainLoops() throws Exception {
        UUID customer1Id = entityIsCreated(testCustomer1);
        testCustomer2.setParentId(customer1Id);
        UUID customer2Id = entityIsCreated(testCustomer2);
        testCustomer3.setParentId(customer2Id);
        UUID customer3Id = entityIsCreated(testCustomer3);
        CustomerUpdateDto update = new CustomerUpdateDto();
        update.setParentId(customer3Id);
        updateEntity(CUSTOMERS_PATH, customer1Id, update);

        responseCodeIs(SC_CONFLICT);
    }

    @Jira("DPIM-133")
    @Test
    public void updatePropertyCustomerCountry() {
        UUID customerId = entityIsCreated(testCustomer1);
        AddressDto address = new AddressDto();
        address.setCountryCode("CZ");
        address.setLine1("CoreQA");
        address.setZipCode("123456");
        address.setCity("Brno");
        CustomerUpdateDto update = new CustomerUpdateDto();
        update.setVatId("CZ123456789");
        update.setAddress(address);
        entityIsUpdated(CUSTOMERS_PATH, customerId, update);
    }

    @Test
    public void createAllCustomerTypes() throws Exception {
        for (CustomerType type : CustomerType.values()) {
            testCustomer1.setType(type);
            testCustomer1.setId(null);
            Response response = createEntity(testCustomer1);
            responseCodeIs(SC_CREATED);
            assertThat(response.as(CustomerDto.class).getType(), is(type));
        }
    }

    @Test
    public void updateCustomer() {
        UUID customerId = entityIsCreated(testCustomer1);
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setName("Updated name");
        customerUpdate.setEmail("updated@snapshot.travel");
        customerUpdate.setIsActive(false);
        customerUpdate.setNotes("Updated notes");
        customerUpdate.setParentId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        Response updateResponse = updateEntity(CUSTOMERS_PATH, customerId, customerUpdate);
        responseCodeIs(SC_OK);
        CustomerDto updateResponseCustomer = updateResponse.as(CustomerDto.class);
        CustomerDto requestedCustomer = getEntityAsType(CUSTOMERS_PATH, CustomerDto.class, testCustomer1.getId());
        assertThat("Update response body differs from the same customer requested by GET ", updateResponseCustomer, is(requestedCustomer));
    }

    @Test
    public void invalidUpdateCustomer() throws Exception {
        UUID customerId = entityIsCreated(testCustomer1);
        Map<String, String> invalidUpdate = singletonMap("invalid_key", "whatever");
        updateEntity(CUSTOMERS_PATH, customerId, invalidUpdate);
        responseIsUnprocessableEntity();

        invalidUpdate = singletonMap("email", "invalid_value");
        updateEntity(CUSTOMERS_PATH, customerId, invalidUpdate);
        responseIsUnprocessableEntity();

        updateEntity(CUSTOMERS_PATH, randomUUID(), invalidUpdate);
        responseIsEntityNotFound();
    }

    @Test
    public void updateCustomerWithInvalidEtag() throws Exception {
        UUID customerId = entityIsCreated(testCustomer1);
        updateEntityWithEtag(CUSTOMERS_PATH, customerId, new CustomerUpdateDto(), DEFAULT_SNAPSHOT_ETAG);
        responseCodeIs(SC_PRECONDITION_FAILED);
        customCodeIs(CC_INVALID_ETAG);

        updateEntityWithEtag(CUSTOMERS_PATH, customerId, new CustomerUpdateDto(), "Invalid Etag");
        responseCodeIs(SC_PRECONDITION_FAILED);
        customCodeIs(CC_INVALID_ETAG);

        updateEntityWithEtag(CUSTOMERS_PATH, customerId, new CustomerUpdateDto(), "");
        responseCodeIs(SC_PRECONDITION_FAILED);
        customCodeIs(CC_INVALID_ETAG);

        updateEntityWithEtag(CUSTOMERS_PATH, customerId, new CustomerUpdateDto(), null);
        responseCodeIs(SC_PRECONDITION_FAILED);
        customCodeIs(CC_INVALID_ETAG);
    }

    @Test
    public void deleteCustomer() throws Exception {
        UUID customerId = entityIsCreated(testCustomer1);
        deleteEntity(CUSTOMERS_PATH, customerId);
        responseCodeIs(SC_NO_CONTENT);
    }

    @Test
    public void customerWithParentChildRelationshipCannotBeDeleted() throws Exception {
        UUID customer1Id = entityIsCreated(testCustomer1);
        testCustomer2.setParentId(customer1Id);
        UUID customer2Id = entityIsCreated(testCustomer2);
//        Customer cannot be deleted when it has children
        deleteEntity(CUSTOMERS_PATH, customer1Id);
        responseIsEntityReferenced();
//        But once all children are deleted, the first customer can be deleted as well
        entityIsDeleted(CUSTOMERS_PATH, customer2Id);
        deleteEntity(CUSTOMERS_PATH, customer1Id);
        responseCodeIs(SC_NO_CONTENT);
    }

    @Test
    public void getNonExistentCustomer() {
        getEntity(CUSTOMERS_PATH, NON_EXISTENT_ID);
        responseIsEntityNotFound();
    }
}
