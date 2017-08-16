package travel.snapshot.dp.qa.junit.tests.identity.propertysets;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertySetType;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;

@RunWith(SerenityRunner.class)
public class PropertySetTests extends CommonTest {

    // https://conhos.atlassian.net/wiki/spaces/TD/pages/97780350/PropertySet+-+Property
    @Test
    public void propertyShouldNotBelongToMoreThanOnePSOfTypeGeolocation() throws IOException {
        testPropertySet1.setType(PropertySetType.GEOLOCATION);
        UUID propertySetId1 = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        UUID propertySetId2 = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet2);
        UUID propertyId = commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        relationshipsHelpers.propertySetPropertyRelationshipIsCreated(propertySetId1, propertyId, true);
        relationshipsHelpers.createPropertySetPropertyRelationship(propertySetId2, propertyId, true);
        responseCodeIs(SC_FORBIDDEN);
    }
}
