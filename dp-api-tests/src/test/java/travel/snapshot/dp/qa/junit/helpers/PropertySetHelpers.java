package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.qa.cucumber.serenity.property_sets.PropertySetSteps;

import java.util.UUID;

/**
 * Created by zelezny on 6/26/2017.
 */
public class PropertySetHelpers extends PropertySetSteps {
    private final CommonHelpers commonHelpers = new CommonHelpers();
    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();

    public PropertySetDto propertySetIsCreated(PropertySetDto propertySet) {
        Response response = followingPropertySetIsCreated(propertySet, null);
        if (response.getStatusCode() != SC_CREATED) {
            fail("Property set cannot be created: " + response.asString());
        }
        return response.as(PropertySetDto.class);
    }

    public UUID propertySetIsCreatedWithAuth(PropertySetDto propertySet) {
        createPropertySetWithAuth(propertySet);
        responseCodeIs(SC_CREATED);
        UUID propertySetId = getSessionResponse().as(PropertySetDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(PROPERTY_SETS_RESOURCE, propertySetId);
        return propertySetId;
    }

    public void createPropertySetWithAuth(PropertySetDto propertySet) {
        authorizationHelpers.createEntity(PROPERTY_SETS_PATH, propertySet);
    }
}
