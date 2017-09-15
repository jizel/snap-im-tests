package travel.snapshot.dp.qa.junit.tests.identity.propertysets;


import static org.apache.http.HttpStatus.SC_CONFLICT;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.getSessionResponse;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipPartialDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;


/**
 * Sample customer tests using YAML data
 */
public class PartialPropertySetPropertyTests extends CommonTest {

    //    Load this test class specific test data
    private static UUID createdPropertyId;
    private static UUID createdPropertySetId;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        UserCustomerRelationshipPartialDto relation = new UserCustomerRelationshipPartialDto();
        relation.setCustomerId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        createdPropertyId = commonHelpers.entityIsCreated(testProperty1);
        createdPropertySetId = commonHelpers.entityIsCreated(testPropertySet1);

    }

    @Test
    public void addRemovePropertyToPropertySet() {
        UUID propertySetId = createdPropertySetId;
        UUID propertyId = createdPropertyId;
        PropertySetPropertyRelationshipCreateDto relation = relationshipsHelpers.constructPropertySetPropertyRelationship(propertySetId, propertyId, true);
        commonHelpers.entityIsCreated(relation);
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
