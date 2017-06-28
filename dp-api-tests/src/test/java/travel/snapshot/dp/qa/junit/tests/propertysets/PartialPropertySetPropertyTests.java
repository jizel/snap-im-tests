package travel.snapshot.dp.qa.junit.tests.propertysets;


import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.*;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.EntityNonNullMap;


import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.junit.Assert.assertNull;


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
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        UserCustomerRelationshipPartialDto relation = new UserCustomerRelationshipPartialDto();
        relation.setCustomerId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        createdProperty = propertyHelpers.propertyIsCreated(testProperty1);
        createdPropertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);

    }

    @After
    public void cleanUp() {
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
