package travel.snapshot.dp.qa.junit.tests.identity.propertysets;


import static org.apache.http.HttpStatus.SC_CONFLICT;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.getSessionResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;

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
    public void setUp() {
        super.setUp();
        UserCustomerRelationshipPartialDto relation = new UserCustomerRelationshipPartialDto();
        relation.setCustomerId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        createdPropertyId = entityIsCreated(testProperty1);
        createdPropertySetId = entityIsCreated(testPropertySet1);

    }

    @Test
    public void addRemovePropertyToPropertySet() {
        UUID propertySetId = createdPropertySetId;
        UUID propertyId = createdPropertyId;
        PropertySetPropertyRelationshipCreateDto relation = constructPropertySetPropertyRelationship(propertySetId, propertyId, true);
        entityIsCreated(relation);
        UUID relationId = getSessionResponse().as(PropertySetPropertyRelationshipDto.class).getId();
        deleteEntity(PROPERTIES_PATH, propertyId);
        responseCodeIs(SC_CONFLICT);
        entityIsDeleted(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId);
        getEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId);
        responseIsEntityNotFound();
        entityIsDeleted(PROPERTIES_PATH, propertyId);
        getEntity(PROPERTIES_PATH, propertyId);
        responseIsEntityNotFound();
    }
}
