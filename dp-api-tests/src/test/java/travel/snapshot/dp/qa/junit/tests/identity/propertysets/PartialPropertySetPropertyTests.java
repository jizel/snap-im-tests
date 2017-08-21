package travel.snapshot.dp.qa.junit.tests.identity.propertysets;


import static org.apache.http.HttpStatus.SC_CONFLICT;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.getSessionResponse;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipPartialDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.util.UUID;


/**
 * Sample customer tests using YAML data
 */
public class PartialPropertySetPropertyTests extends CommonTest {

    //    Load this test class specific test data
    private static PropertyDto createdProperty = null;
    private static PropertySetDto createdPropertySet = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        UserCustomerRelationshipPartialDto relation = new UserCustomerRelationshipPartialDto();
        relation.setCustomerId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        createdProperty = propertyHelpers.propertyIsCreated(testProperty1);
        createdPropertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);

    }

    @Test
    public void addRemovePropertyToPropertySet() throws IOException {
        UUID propertySetId = createdPropertySet.getId();
        UUID propertyId = createdProperty.getId();
        PropertySetPropertyRelationshipDto relation = new PropertySetPropertyRelationshipDto();
        relation.setPropertySetId(propertySetId);
        relation.setPropertyId(propertyId);
        relation.setIsActive(true);
        commonHelpers.entityIsCreated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relation);
        UUID relationId = getSessionResponse().as(PropertySetPropertyRelationshipDto.class).getId();
        commonHelpers.deleteEntity(PROPERTIES_PATH, propertyId);
        responseCodeIs(SC_CONFLICT);
        commonHelpers.entityIsDeleted(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId);
        commonHelpers.getEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId);
        responseIsEntityNotFound();
        commonHelpers.entityIsDeleted(PROPERTIES_PATH, propertyId);
        commonHelpers.getEntity(PROPERTIES_PATH, propertyId);
        responseIsEntityNotFound();
    }
}
