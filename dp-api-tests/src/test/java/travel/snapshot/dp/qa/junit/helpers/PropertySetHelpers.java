package travel.snapshot.dp.qa.junit.helpers;

import static org.junit.Assert.fail;

import com.jayway.restassured.response.Response;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.qa.cucumber.serenity.property_sets.PropertySetSteps;

/**
 * Created by zelezny on 6/26/2017.
 */
public class PropertySetHelpers extends PropertySetSteps {

    public PropertySetDto propertySetIsCreated(PropertySetDto propertySet) {
        Response response = followingPropertySetIsCreated(propertySet, null);
        if (response.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("Property set cannot be created: " + response.asString());
        }
        return response.as(PropertySetDto.class);
    }
}
