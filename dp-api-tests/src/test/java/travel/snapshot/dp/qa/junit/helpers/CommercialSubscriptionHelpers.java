package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.cucumber.serenity.commercial_subscription.CommercialSubscriptionSteps;

import java.util.UUID;

@Log
public class CommercialSubscriptionHelpers extends CommercialSubscriptionSteps {
    private final CommonHelpers commonHelpers = new CommonHelpers();

    public CommercialSubscriptionHelpers() { super();}

    private void createCommercialSubscription(UUID customerId, UUID propertyId, UUID applicationId) {
        CommercialSubscriptionDto subscription = new CommercialSubscriptionDto();
        subscription.setApplicationId(applicationId);
        subscription.setCustomerId(customerId);
        subscription.setPropertyId(propertyId);
        commonHelpers.createEntity(COMMERCIAL_SUBSCRIPTIONS_PATH, subscription);
    }

    public UUID commercialSubscriptionIsCreated(UUID customerId, UUID propertyId, UUID applicationId) {
        createCommercialSubscription(customerId, propertyId, applicationId);
        responseCodeIs(SC_CREATED);
        return getSessionResponse().as(CommercialSubscriptionDto.class).getId();
    }
}
