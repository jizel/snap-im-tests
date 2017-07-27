package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.ASSET_MANAGEMENT;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.OWNER;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.time.LocalDate;

/**
 * Integration tests for /identity/customer_property_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class CustomerPropertyRelationshipTests extends CommonTest {
    private PropertyDto createdProperty1;
    private CustomerDto createdCustomer1;
    private Response response;
    private LocalDate validFrom = LocalDate.now();
    private LocalDate validTo = LocalDate.now().plusYears(1).plusMonths(2).plusDays(3);

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdProperty1 = propertyHelpers.propertyIsCreated(testProperty1);
        createdCustomer1 = customerHelpers.customerIsCreated(testCustomer1);
    }

    @Test
    public void createCustomerPropertyRelationship() {
        response = relationshipsHelpers.createCustomerPropertyRelationship(createdCustomer1.getId(), createdProperty1.getId(),
                true, ASSET_MANAGEMENT, validFrom, validTo);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        CustomerPropertyRelationshipDto returnedRelationship = response.as(CustomerPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1.getId()));
        assertThat(returnedRelationship.getCustomerId(), is(createdCustomer1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        assertThat(returnedRelationship.getType(), is(ASSET_MANAGEMENT));
        assertThat(returnedRelationship.getValidFrom(), is(validFrom));
        assertThat(returnedRelationship.getValidTo(), is(validTo));
        CustomerPropertyRelationshipDto requestedRelationship = relationshipsHelpers.getCustomerPropertyRelationship(returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createCustomerPropertyRelationshipErrors() {
        relationshipsHelpers.createCustomerPropertyRelationship(NON_EXISTENT_ID, createdProperty1.getId(),
                true, ASSET_MANAGEMENT, validFrom, validTo);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        relationshipsHelpers.createCustomerPropertyRelationship(createdCustomer1.getId(), NON_EXISTENT_ID,
                true, ASSET_MANAGEMENT, validFrom, validTo);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        response = relationshipsHelpers.createCustomerPropertyRelationship(createdCustomer1.getId(), createdProperty1.getId(),
                true, ASSET_MANAGEMENT, validTo, validFrom);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(SEMANTIC_ERRORS_CUSTOM_CODE);
    }

    @Test
    public void updateCustomerPropertyRelationship() throws Exception {
        LocalDate updatedValidFrom = LocalDate.now().minusMonths(5);
        LocalDate updatedValidTo = LocalDate.now();
        CustomerPropertyRelationshipDto customerPropertyRelationship = relationshipsHelpers.customerPropertyRelationshipIsCreated(createdCustomer1.getId(), createdProperty1.getId(),
                true, OWNER, validFrom, validTo);
        relationshipsHelpers.updateCustomerPropertyRelationship(customerPropertyRelationship.getId(), false, CHAIN, updatedValidFrom, updatedValidTo);
        responseCodeIs(SC_NO_CONTENT);
        CustomerPropertyRelationshipDto returnedRelationship = relationshipsHelpers.getCustomerPropertyRelationship(customerPropertyRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
        assertThat(returnedRelationship.getType(), is(CHAIN));
        assertThat(returnedRelationship.getValidFrom(), is(updatedValidFrom));
        assertThat(returnedRelationship.getValidTo(), is(updatedValidTo));
    }

    @Test
    public void deleteCustomerPropertyRelationship() {
        CustomerPropertyRelationshipDto customerPropertyRelationship = relationshipsHelpers.customerPropertyRelationshipIsCreated(createdCustomer1.getId(), createdProperty1.getId(),
                true, OWNER, validFrom, validTo);
        relationshipsHelpers.deleteCustomerPropertyRelationship(customerPropertyRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        CustomerPropertyRelationshipDto returnedRelationship = relationshipsHelpers.getCustomerPropertyRelationship(customerPropertyRelationship.getId());
        assertNull(returnedRelationship);
    }
}
