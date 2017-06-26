package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.qa.cucumber.serenity.properties.PropertySteps;

/**
 * Help methods for JUnit test for the Property entity
 */
public class PropertyHelpers extends PropertySteps{


    public PropertyDto propertyIsCreated(PropertyDto property) {
        return propertyIsCreatedByUser(DEFAULT_SNAPSHOT_USER_ID, property);
    }

    public PropertyDto propertyIsCreatedByUser(String userId, PropertyDto property) {
        Response response = createProperty(userId, property);
        assertThat(String.format("Failed to create property: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(PropertyDto.class);
    }

}
