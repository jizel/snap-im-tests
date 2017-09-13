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
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.sendBlankPost;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import org.junit.Test;
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
            commonHelpers.createEntity(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
//            TODO: Use existing/make new matchers for DTOs. Assert that createdCustomer has all the attributes the customer has
    }

    @Test
    public void checkMandatoryAttributesCreateCustomer() throws Exception {
        testCustomer1.setName(null);
        commonHelpers.createEntity(CUSTOMERS_PATH, testCustomer1);
        responseIsUnprocessableEntity();

        testCustomer2.setTimezone(null);
        commonHelpers.createEntity(CUSTOMERS_PATH, testCustomer2);
        responseIsUnprocessableEntity();

        testCustomer3.setEmail(null);
        commonHelpers.createEntity(CUSTOMERS_PATH, testCustomer3);
        responseIsUnprocessableEntity();

        testCustomer4.setType(null);
        commonHelpers.createEntity(CUSTOMERS_PATH, testCustomer4);
        responseIsUnprocessableEntity();
    }

    @Test
    public void customerIdMustBeUnique() throws Exception {
        UUID createdCustomerId = commonHelpers.entityIsCreated(testCustomer1);
        testCustomer1.setId(createdCustomerId);
        commonHelpers.createEntity(CUSTOMERS_PATH, testCustomer1);
        responseIsConflictId();
    }

    @Test
    public void parentChildRelationCannotContainLoops() throws Exception {
        UUID customer1Id = commonHelpers.entityIsCreated(testCustomer1);
        testCustomer2.setParentId(customer1Id);
        UUID customer2Id = commonHelpers.entityIsCreated(testCustomer2);
        testCustomer3.setParentId(customer2Id);
        UUID customer3Id = commonHelpers.entityIsCreated(testCustomer3);
        CustomerUpdateDto update = new CustomerUpdateDto();
        update.setParentId(customer3Id);
        commonHelpers.updateEntity(CUSTOMERS_PATH, customer1Id, update);

        responseCodeIs(SC_CONFLICT);
    }

    @Test
    public void createCustomerEmptyBody() throws Exception {
        sendBlankPost(CUSTOMERS_PATH, "identity");
        responseIsUnprocessableEntity();
    }

    @Test
    public void createAllCustomerTypes() throws Exception {
        for (CustomerType type : CustomerType.values()) {
            testCustomer1.setType(type);
            testCustomer1.setId(null);
            Response response = commonHelpers.createEntity(CUSTOMERS_PATH, testCustomer1);
            responseCodeIs(SC_CREATED);
            assertThat(response.as(CustomerDto.class).getType(), is(type));
        }
    }

    @Test
    public void updateCustomer() throws Exception {
        UUID customerId = commonHelpers.entityIsCreated(testCustomer1);
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

    @Test
    public void invalidUpdateCustomer() throws Exception {
        UUID customerId = commonHelpers.entityIsCreated(testCustomer1);
        Map<String, String> invalidUpdate = singletonMap("invalid_key", "whatever");
        commonHelpers.updateEntity(CUSTOMERS_PATH, customerId, invalidUpdate);
        responseIsUnprocessableEntity();

        invalidUpdate = singletonMap("email", "invalid_value");
        commonHelpers.updateEntity(CUSTOMERS_PATH, customerId, invalidUpdate);
        responseIsUnprocessableEntity();

        commonHelpers.updateEntity(CUSTOMERS_PATH, randomUUID(), invalidUpdate);
        responseIsEntityNotFound();
    }

    @Test
    public void updateCustomerWithInvalidEtag() throws Exception {
        UUID customerId = commonHelpers.entityIsCreated(testCustomer1);
        commonHelpers.updateEntityWithEtag(CUSTOMERS_PATH, customerId, new CustomerUpdateDto(), DEFAULT_SNAPSHOT_ETAG);
        responseCodeIs(SC_PRECONDITION_FAILED);
        customCodeIs(CC_INVALID_ETAG);

        commonHelpers.updateEntityWithEtag(CUSTOMERS_PATH, customerId, new CustomerUpdateDto(), "Invalid Etag");
        responseCodeIs(SC_PRECONDITION_FAILED);
        customCodeIs(CC_INVALID_ETAG);

        commonHelpers.updateEntityWithEtag(CUSTOMERS_PATH, customerId, new CustomerUpdateDto(), "");
        responseCodeIs(SC_PRECONDITION_FAILED);
        customCodeIs(CC_INVALID_ETAG);

        commonHelpers.updateEntityWithEtag(CUSTOMERS_PATH, customerId, new CustomerUpdateDto(), null);
        responseCodeIs(SC_PRECONDITION_FAILED);
        customCodeIs(CC_INVALID_ETAG);
    }

    @Test
    public void deleteCustomer() throws Exception {
        UUID customerId = commonHelpers.entityIsCreated(testCustomer1);
        commonHelpers.deleteEntity(CUSTOMERS_PATH, customerId);
        responseCodeIs(SC_NO_CONTENT);
    }

    @Test
    public void customerWithParentChildRelationshipCannotBeDeleted() throws Exception {
        UUID customer1Id = commonHelpers.entityIsCreated(testCustomer1);
        testCustomer2.setParentId(customer1Id);
        UUID customer2Id = commonHelpers.entityIsCreated(testCustomer2);
//        Customer cannot be deleted when it has children
        commonHelpers.deleteEntity(CUSTOMERS_PATH, customer1Id);
        responseIsEntityReferenced();
//        But once all children are deleted, the first customer can be deleted as well
        commonHelpers.entityIsDeleted(CUSTOMERS_PATH, customer2Id);
        commonHelpers.deleteEntity(CUSTOMERS_PATH, customer1Id);
        responseCodeIs(SC_NO_CONTENT);
    }

    @Test
    public void getNonExistentCustomer() {
        commonHelpers.getEntity(CUSTOMERS_PATH, NON_EXISTENT_ID);
        responseIsEntityNotFound();
    }
}
