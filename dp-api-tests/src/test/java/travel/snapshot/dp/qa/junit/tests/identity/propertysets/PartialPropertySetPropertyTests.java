package travel.snapshot.dp.qa.junit.tests.identity.propertysets;


import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.getSessionResponse;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.*;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.util.UUID;


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
    public void addRemovePropertyToPropertySet() throws IOException {
        UUID propertySetId = createdPropertySet.getId();
        UUID propertyId = createdProperty.getId();
        PropertySetPropertyRelationshipDto relation = new PropertySetPropertyRelationshipDto();
        relation.setPropertySetId(propertySetId);
        relation.setPropertyId(propertyId);
        relation.setIsActive(true);
        commonHelpers.entityIsCreated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relation);
        UUID relationId = getSessionResponse().as(PropertySetPropertyRelationshipDto.class).getId();
        commonHelpers.deleteEntityWithEtag(PROPERTIES_PATH, propertyId);
        responseCodeIs(SC_CONFLICT);
        commonHelpers.entityIsDeletedWithEtag(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId);
        commonHelpers.entityIsDeletedWithEtag(PROPERTIES_PATH, propertyId);
    }
}
