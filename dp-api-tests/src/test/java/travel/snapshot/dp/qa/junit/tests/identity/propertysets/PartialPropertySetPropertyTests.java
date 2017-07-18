package travel.snapshot.dp.qa.junit.tests.identity.propertysets;


import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.junit.Assert.*;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipPartialDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;


/**
 * Sample customer tests using YAML data
 */

@RunWith(SerenityRunner.class)
public class PartialPropertySetPropertyTests extends CommonTest {

    //    Load this test class specific test data
    private static PropertyDto createdProperty = null;
    private static PropertySetDto createdPropertySet = null;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        UserCustomerRelationshipPartialDto relation = new UserCustomerRelationshipPartialDto();
        relation.setCustomerId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        createdProperty = propertyHelpers.propertyIsCreated(testProperty1);
        createdPropertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);

    }

    @Test
    public void addRemovePropertyToPropertySet() {
        String propertySetId = createdPropertySet.getId();
        String propertyId = createdProperty.getId();
        propertySetHelpers.addPropertyToPropertySet(propertyId, propertySetId, true);
        responseCodeIs(SC_CREATED);
        propertyHelpers.deleteProperty(propertyId);
        responseCodeIs(SC_CONFLICT);
        propertySetHelpers.removePropertyFromPropertySet(propertyId, propertySetId);
        responseCodeIs(SC_NO_CONTENT);
        PropertySetPropertyRelationshipPartialDto existingPropertySetProperty = propertySetHelpers.getPropertyForPropertySet(propertySetId, propertyId);
        assertNull("Property should not be present in propertyset", existingPropertySetProperty);
    }
}
