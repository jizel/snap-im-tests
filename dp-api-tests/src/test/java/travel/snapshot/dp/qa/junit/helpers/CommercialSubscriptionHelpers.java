package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.cucumber.serenity.commercial_subscription.CommercialSubscriptionSteps;

import java.util.UUID;

@Log
public class CommercialSubscriptionHelpers extends CommercialSubscriptionSteps {

    public CommercialSubscriptionHelpers() { super();}

    public Response createCommercialSubscription(UUID customerId, UUID propertyId, UUID applicationId) {
        CommercialSubscriptionDto subscription = new CommercialSubscriptionDto();
        subscription.setApplicationId(applicationId);
        subscription.setCustomerId(customerId);
        subscription.setPropertyId(propertyId);
        Response createResponse = null;
        try {
            JSONObject jsonSubscription = retrieveData(subscription);
            createResponse = createEntity(jsonSubscription.toString());
        } catch (JsonProcessingException e) {
            log.severe("Unable to convert Commertial Subscription object to json");
        }
        setSessionResponse(createResponse);
        return createResponse;
    }

    public CommercialSubscriptionDto commercialSubscriptionIsCreated(UUID customerId, UUID propertyId, UUID applicationId) {
        Response response = createCommercialSubscription(customerId, propertyId, applicationId);
        assertEquals(String.format("Failed to create commercial subscription: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        setSessionResponse(response);
        return response.as(CommercialSubscriptionDto.class);
    }
}
