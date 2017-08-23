package travel.snapshot.dp.qa.junit.tests.identity.properties;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HOSPITALITY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.IS_ACTIVE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.PROPERTY_CODE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.PROPERTY_ID;

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
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void propertyCRUD() throws Exception {
        createdPropertyId = commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        bodyContainsEntityWith(PROPERTY_CODE, "property1code");
        bodyContainsEntityWith("name", "Property 1");
        bodyContainsEntityWith("website", "http://www.snapshot.travel");
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

    @Test
    public void propertyActivateDeactivate() {
        createdPropertyId = commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        PropertyUpdateDto update = new PropertyUpdateDto();
        update.setIsActive(false);
        commonHelpers.entityIsUpdated(PROPERTIES_PATH, createdPropertyId, update);
        bodyContainsEntityWith(IS_ACTIVE, "false");
        update.setIsActive(true);
        commonHelpers.entityIsUpdated(PROPERTIES_PATH, createdPropertyId, update);
        bodyContainsEntityWith(IS_ACTIVE, "true");
    }


    @Test
    public void createPropertyWithCapitalIdLetters() throws Exception {
        String id = "000e833e-50b8-4854-a233-289f00bc4a09";
        String hospitalityId = "000000b2-3836-4207-a705-42bbec000000";
        commonHelpers.useFileForSendDataTo("/messages/identity/properties/property_capital_letters.json", "POST", PROPERTIES_PATH, "identity");
        bodyContainsEntityWith(PROPERTY_ID, id);
        bodyContainsEntityWith(HOSPITALITY_ID, hospitalityId);
    }

    @Test
    public void timezoneParameterIsMandatory() {
        testProperty1.setTimezone(null);
        commonHelpers.createEntity(PROPERTIES_PATH,testProperty1);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }
}
