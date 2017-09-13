package travel.snapshot.dp.qa.junit.helpers;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_RESOURCE;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.qa.cucumber.serenity.partners.PartnerSteps;

import java.util.UUID;

/**
 * Help methods for JUnit test for the Partner entity
 */
public class PartnerHelpers extends PartnerSteps{

    public void getUsersForPartnerByUserForApp(UUID partnerId, UUID userId, UUID appVersionId) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, appVersionId, partnerId, USERS_RESOURCE, null, null, null, null, null, null);
        setSessionResponse(response);
    }

}
