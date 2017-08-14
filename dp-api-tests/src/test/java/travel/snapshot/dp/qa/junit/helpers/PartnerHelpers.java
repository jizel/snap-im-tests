package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_RESOURCE;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.qa.cucumber.serenity.partners.PartnerSteps;

import java.util.UUID;

/**
 * Help methods for JUnit test for the Partner entity
 */
public class PartnerHelpers extends PartnerSteps{

    public PartnerDto partnerIsCreated(PartnerDto partner) {
        Response response = followingPartnerIsCreated(partner);
        assertEquals(String.format("Failed to create partner: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(PartnerDto.class);
    }

    public void getUsersForPartnerByUserForApp(UUID partnerId, UUID userId, UUID appVersionId) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, appVersionId, partnerId, USERS_RESOURCE, null, null, null, null, null, null);
        setSessionResponse(response);
    }

}
