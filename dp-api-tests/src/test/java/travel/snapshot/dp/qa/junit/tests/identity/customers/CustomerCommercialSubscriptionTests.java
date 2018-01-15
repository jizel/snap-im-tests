package travel.snapshot.dp.qa.junit.tests.identity.customers;

import static java.util.Arrays.stream;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.constructCommercialSubscriptionDto;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.NotFoundException;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionCreateDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Tests for customers/customer_id/commercial_subscriptions endpoint.
 */
public class CustomerCommercialSubscriptionTests extends CommonTest{

    private UUID createdCustomerId;
    private UUID createdPropertyId;
    private CommercialSubscriptionCreateDto testCommercialSubscription;


    @Override
    @Before
    public void setUp(){
        super.setUp();
        createdCustomerId = entityIsCreated(testCustomer1);
        createdPropertyId = entityIsCreated(testProperty1);
        testCommercialSubscription = constructCommercialSubscriptionDto(DEFAULT_SNAPSHOT_APPLICATION_ID, createdCustomerId, createdPropertyId);
    }

    @Test
    public void getCustomerCommSubscriptionTest() throws Exception {
        entityIsCreated(testCommercialSubscription);
        Response getResponse = commonHelpers.getRelationships(CUSTOMERS_PATH, createdCustomerId, COMMERCIAL_SUBSCRIPTIONS_RESOURCE, null);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(CommercialSubscriptionDto.class, 1);

        CommercialSubscriptionDto returnedSubscription = stream(getResponse.as(CommercialSubscriptionDto[].class)).findFirst().orElseThrow(NotFoundException::new);
        assertThat(returnedSubscription.getCustomerId(), is(createdCustomerId));
        assertThat(returnedSubscription.getPropertyId(), is(createdPropertyId));
    }

    @Test
    public void customerWithSubscriptionCannotBeDeleted() throws Exception {
        UUID createdCommSubscriptionId = entityIsCreated(testCommercialSubscription);

        deleteEntity(CUSTOMERS_PATH, createdCustomerId);
        responseIsEntityReferenced();
        entityIsDeleted(COMMERCIAL_SUBSCRIPTIONS_PATH, createdCommSubscriptionId);
        deleteEntity(CUSTOMERS_PATH, createdCustomerId);
        responseCodeIs(SC_NO_CONTENT);
    }
}
