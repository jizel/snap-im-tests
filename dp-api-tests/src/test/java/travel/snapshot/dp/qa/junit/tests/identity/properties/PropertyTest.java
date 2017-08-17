package travel.snapshot.dp.qa.junit.tests.identity.properties;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Basic tests for IM Property entity
 */
public class PropertyTest extends CommonTest {

    private UUID createdPropertyId;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdPropertyId = commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
    }

    @Test
    public void updateUser() throws Exception {
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setName("UpdatedName");
        propertyUpdate.setEmail("updated@snapshot.travel");
        propertyUpdate.setIsDemo(false);
        propertyUpdate.setTtiId(123456);
        propertyUpdate.setTimezone("Europe/London");
        Response updateResponse = commonHelpers.updateEntity(PROPERTIES_PATH, createdPropertyId, propertyUpdate);
        responseCodeIs(SC_OK);
        PropertyDto updateResponseProperty = updateResponse.as(PropertyDto.class);
        PropertyDto requestedProperty = commonHelpers.getEntityAsType(PROPERTIES_PATH, PropertyDto.class, createdPropertyId);
        assertThat("Update response body differs from the same user requested by GET ", updateResponseProperty, is(requestedProperty));
    }
}
