package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;

import lombok.extern.java.Log;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionCreateDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.cucumber.serenity.commercial_subscription.CommercialSubscriptionSteps;

import java.util.UUID;

@Log
public class CommercialSubscriptionHelpers extends CommercialSubscriptionSteps {
    private final CommonHelpers commonHelpers = new CommonHelpers();

    public CommercialSubscriptionHelpers() { super();}

    private void createCommercialSubscription(UUID customerId, UUID propertyId, UUID applicationId) {
        CommercialSubscriptionCreateDto subscription = new CommercialSubscriptionCreateDto();
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

    public static CommercialSubscriptionCreateDto constructCommercialSubscriptionDto(UUID applicationId, UUID customerId, UUID propertyId){
        CommercialSubscriptionCreateDto commercialSubscriptionDto = new CommercialSubscriptionCreateDto();
        commercialSubscriptionDto.setApplicationId(applicationId);
        commercialSubscriptionDto.setCustomerId(customerId);
        commercialSubscriptionDto.setPropertyId(propertyId);
        return commercialSubscriptionDto;
    }
}
