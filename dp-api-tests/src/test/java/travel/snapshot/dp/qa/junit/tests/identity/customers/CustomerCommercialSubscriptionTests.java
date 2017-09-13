package travel.snapshot.dp.qa.junit.tests.identity.customers;

import static java.util.Arrays.stream;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NotFoundException;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Tests for customers/customer_id/commercial_subscriptions endpoint.
 */
public class CustomerCommercialSubscriptionTests extends CommonTest{

    private UUID createdCustomerId;
    private UUID createdPropertyId;
    private CommercialSubscriptionDto testCommercialSubscription;


    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdCustomerId = commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        createdPropertyId = commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        testCommercialSubscription = commercialSubscriptionHelpers.constrcutCommercialSubscriptionDto(DEFAULT_SNAPSHOT_APPLICATION_ID, createdCustomerId, createdPropertyId);
    }

    @Test
    public void getCustomerCommSubscriptionTest() throws Exception {
        commonHelpers.entityIsCreated(COMMERCIAL_SUBSCRIPTIONS_PATH, testCommercialSubscription);
        Response getResponse = commonHelpers.getRelationships(CUSTOMERS_PATH, createdCustomerId, COMMERCIAL_SUBSCRIPTIONS_RESOURCE, null);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(CommercialSubscriptionDto.class, 1);

        CommercialSubscriptionDto returnedSubscription = stream(getResponse.as(CommercialSubscriptionDto[].class)).findFirst().orElseThrow(NotFoundException::new);
        assertThat(returnedSubscription.getCustomerId(), is(createdCustomerId));
        assertThat(returnedSubscription.getPropertyId(), is(createdPropertyId));
    }

    @Test
    public void customerWithSubscriptionCannotBeDeleted() throws Exception {
        UUID createdCommSubscriptionId = commonHelpers.entityIsCreated(COMMERCIAL_SUBSCRIPTIONS_PATH, testCommercialSubscription);

        commonHelpers.deleteEntity(CUSTOMERS_PATH, createdCustomerId);
        responseIsEntityReferenced();
        commonHelpers.entityIsDeleted(COMMERCIAL_SUBSCRIPTIONS_PATH, createdCommSubscriptionId);
        commonHelpers.deleteEntity(CUSTOMERS_PATH, createdCustomerId);
        responseCodeIs(SC_NO_CONTENT);
    }
}