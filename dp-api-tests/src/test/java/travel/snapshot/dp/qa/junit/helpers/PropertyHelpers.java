package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.qa.cucumber.serenity.properties.PropertySteps;

import java.util.UUID;

/**
 * Help methods for JUnit test for the Property entity
 */
public class PropertyHelpers extends PropertySteps{

    private final CommonHelpers commonHelpers = new CommonHelpers();
    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();


    public PropertyDto propertyIsCreated(PropertyDto property) {
        return propertyIsCreatedByUser(DEFAULT_SNAPSHOT_USER_ID, property);
    }

    public PropertyDto propertyIsCreatedByUser(UUID userId, PropertyDto property) {
        Response response = createPropertyByUser(userId, property);
        assertThat(String.format("Failed to create property: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(PropertyDto.class);
    }

    public Response createPropertyByUserForApp(UUID userId, UUID appVersionId, PropertyDto property) {
        Response response = createEntityByUserForApplication(userId, appVersionId, property);
        setSessionResponse(response);
        return response;
    }
}
