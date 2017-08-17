package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.ASSET_MANAGEMENT;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
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
    private LocalDate validFrom = LocalDate.now();
    private LocalDate validTo = LocalDate.now().plusYears(1).plusMonths(2).plusDays(3);
    private CustomerPropertyRelationshipDto testCustomerPropertyRelationship;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdProperty1 = propertyHelpers.propertyIsCreated(testProperty1);
        createdCustomer1 = customerHelpers.customerIsCreated(testCustomer1);
        testCustomerPropertyRelationship = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                createdCustomer1.getId(), createdProperty1.getId(), true, ASSET_MANAGEMENT, validFrom, validTo);
    }

    @Test
    public void createCustomerPropertyRelationship() {
        Response createResponse = commonHelpers.createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, testCustomerPropertyRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        CustomerPropertyRelationshipDto returnedRelationship = createResponse.as(CustomerPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1.getId()));
        assertThat(returnedRelationship.getCustomerId(), is(createdCustomer1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        assertThat(returnedRelationship.getType(), is(ASSET_MANAGEMENT));
        assertThat(returnedRelationship.getValidFrom(), is(validFrom));
        assertThat(returnedRelationship.getValidTo(), is(validTo));

        CustomerPropertyRelationshipDto requestedRelationship = commonHelpers.getEntityAsType(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH,
                CustomerPropertyRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createCustomerPropertyRelationshipErrors() {
        CustomerPropertyRelationshipDto invalidCustomerPropertyRelationship = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                NON_EXISTENT_ID, createdProperty1.getId(), true, ASSET_MANAGEMENT, validFrom, validTo);
        commonHelpers.createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, invalidCustomerPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);

        invalidCustomerPropertyRelationship = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                createdCustomer1.getId(), NON_EXISTENT_ID, true, ASSET_MANAGEMENT, validFrom, validTo);
        commonHelpers.createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, invalidCustomerPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);

        invalidCustomerPropertyRelationship = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                createdCustomer1.getId(), createdProperty1.getId(), true, ASSET_MANAGEMENT, validTo, validFrom);
        commonHelpers.createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, invalidCustomerPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @Test
    public void updateCustomerPropertyRelationship() throws Exception {
        LocalDate updatedValidFrom = LocalDate.now().minusMonths(5);
        LocalDate updatedValidTo = LocalDate.now();
        CustomerPropertyRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(
                CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, CustomerPropertyRelationshipDto.class, testCustomerPropertyRelationship);
        CustomerPropertyRelationshipUpdateDto update = relationshipsHelpers.constructCustomerPropertyRelationshipUpdate(false, CHAIN, updatedValidFrom, updatedValidTo);

        commonHelpers.updateEntityWithEtag(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);

        CustomerPropertyRelationshipDto returnedRelationship = commonHelpers.getEntityAsType(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH,
                CustomerPropertyRelationshipDto.class, createdRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
        assertThat(returnedRelationship.getType(), is(CHAIN));
        assertThat(returnedRelationship.getValidFrom(), is(updatedValidFrom));
        assertThat(returnedRelationship.getValidTo(), is(updatedValidTo));
    }

    @Test
    public void deleteCustomerPropertyRelationship() {
        CustomerPropertyRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(
                CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, CustomerPropertyRelationshipDto.class, testCustomerPropertyRelationship);
        commonHelpers.deleteEntityWithEtag(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
