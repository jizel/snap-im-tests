package travel.snapshot.dp.qa.junit.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.cucumber.serenity.DbUtilsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.applications.ApplicationsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.commercial_subscription.CommercialSubscriptionSteps;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.assertEquals;

@Log
public class CommercialSubscriptionHelpers extends CommercialSubscriptionSteps {

    public CommercialSubscriptionHelpers() { super();}

    public Response createCommercialSubscription(String customerId, String propertyId, String applicationId) {
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

    public CommercialSubscriptionDto commercialSubscriptionIsCreated(String customerId, String propertyId, String applicationId) {
        Response response = createCommercialSubscription(customerId, propertyId, applicationId);
        assertEquals(String.format("Failed to create commercial subscription: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        setSessionResponse(response);
        return response.as(CommercialSubscriptionDto.class);
    }
}
