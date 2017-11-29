package travel.snapshot.dp.qa.junit.tests.identity_smoke;

import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.tests.Tags.AUTHORIZATION_TEST;

import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;

import java.util.UUID;


@Tag(AUTHORIZATION_TEST)
public class PropertySmokeTests extends CommonSmokeTest {

    protected RequestSpecification spec = null;

    @Test
    public void propertyCRUD() {
        // create
        testProperty1.setIsActive(false);
        UUID propertyId = authorizationHelpers.entityIsCreated(testProperty1);
        // request
        authorizationHelpers.getEntity(PROPERTIES_PATH, propertyId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("property_code", "property1code");
        bodyContainsEntityWith("property_id", "08000000-0000-4444-8888-000000000002");
        bodyContainsEntityWith("address");
        bodyContainsEntityWith("name", "Property 1");
        bodyContainsEntityWith("website", "http://www.snapshot.travel");
        bodyContainsEntityWith("timezone", "Europe/Prague");
        bodyContainsEntityWith("is_active", "false");
        // update
        authorizationHelpers.entityIsUpdated(PROPERTIES_PATH, propertyId, ACTIVATE_RELATION);
        // make sure changes applied
        authorizationHelpers.getEntity(PROPERTIES_PATH, propertyId);
        bodyContainsEntityWith("is_active", "true");
        // delete
        authorizationHelpers.entityIsDeleted(PROPERTIES_PATH, propertyId);
    }

    @Test
    public void propertyPropertySetCRUD() {
        // create PS
        UUID propertySetId = authorizationHelpers.entityIsCreated(testPropertySet1);
        // create propertyset-property relation
        PropertySetPropertyRelationshipCreateDto relation = constructPropertySetPropertyRelationship(propertySetId, DEFAULT_PROPERTY_ID, true);
        UUID relationId = authorizationHelpers.entityIsCreated(relation);
        // request
        authorizationHelpers.getEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("property_set_id", propertySetId.toString());
        bodyContainsEntityWith("property_id", DEFAULT_PROPERTY_ID.toString());
        bodyContainsEntityWith("is_active", "true");
        // update
        authorizationHelpers.entityIsUpdated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        // delete
        authorizationHelpers.entityIsDeleted(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId);
    }

    @Test
    public void userPropertyCRUD() throws Throwable {
        // create user
        UUID userId = userHelpers.userIsCreatedWithAuth(testUser1);
        // create property-user relation
        UserPropertyRelationshipCreateDto relation = constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true);
        UUID relationId = authorizationHelpers.entityIsCreated(relation);
        // request
        authorizationHelpers.getEntity(USER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("property_id", DEFAULT_PROPERTY_ID.toString());
        bodyContainsEntityWith("user_id", userId.toString());
        bodyContainsEntityWith("is_active", "true");
        // update
        authorizationHelpers.entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        // make sure changes applied
        authorizationHelpers.getEntity(USER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("is_active", "false");
        // delete
        authorizationHelpers.entityIsDeleted(USER_PROPERTY_RELATIONSHIPS_PATH, relationId);
    }
}
