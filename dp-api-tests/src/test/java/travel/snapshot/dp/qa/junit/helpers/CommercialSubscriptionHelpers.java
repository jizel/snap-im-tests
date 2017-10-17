package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;

import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionCreateDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;

import java.util.UUID;

@Log
public class CommercialSubscriptionHelpers {

    public CommercialSubscriptionHelpers() { super();}

    public static Response createCommercialSubscription(UUID customerId, UUID propertyId, UUID applicationId) {
        CommercialSubscriptionCreateDto subscription = constructCommercialSubscriptionDto(applicationId, customerId, propertyId);
        return createEntity(subscription);
    }

    public static UUID commercialSubscriptionIsCreated(UUID customerId, UUID propertyId, UUID applicationId) {
        Response response = createCommercialSubscription(customerId, propertyId, applicationId);
        response.then().statusCode(SC_CREATED);
        return response.as(CommercialSubscriptionDto.class).getId();
    }

    public static CommercialSubscriptionCreateDto constructCommercialSubscriptionDto(UUID applicationId, UUID customerId, UUID propertyId){
        CommercialSubscriptionCreateDto commercialSubscriptionDto = new CommercialSubscriptionCreateDto();
        commercialSubscriptionDto.setApplicationId(applicationId);
        commercialSubscriptionDto.setCustomerId(customerId);
        commercialSubscriptionDto.setPropertyId(propertyId);
        return commercialSubscriptionDto;
    }
}
