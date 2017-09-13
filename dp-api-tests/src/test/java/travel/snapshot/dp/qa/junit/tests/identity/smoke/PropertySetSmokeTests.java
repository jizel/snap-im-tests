package travel.snapshot.dp.qa.junit.tests.identity.smoke;

import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;

import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.dp.api.identity.model.PropertySetType;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;

import java.util.UUID;

@Category(Categories.Authorization.class)
public class PropertySetSmokeTests extends CommonSmokeTest {
    protected RequestSpecification spec = null;

    @Test
    public void propertySetCRUD() {
        // create
        UUID propertySetId = authorizationHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        // request
        authorizationHelpers.getEntity(PROPERTY_SETS_PATH, propertySetId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("property_set_id", "09000000-0000-4444-8888-000000000001");
        bodyContainsEntityWith("name", "Test Property Set 1");
        bodyContainsEntityWith("property_set_type", "brand");
        bodyContainsEntityWith("is_active", "true");
        // update
        PropertySetUpdateDto update = new PropertySetUpdateDto();
        update.setName("New Name");
        update.setType(PropertySetType.GEOLOCATION);
        authorizationHelpers.entityIsUpdated(PROPERTY_SETS_PATH, propertySetId, update);
        // make sure changes applied
        authorizationHelpers.getEntity(PROPERTY_SETS_PATH, propertySetId);
        bodyContainsEntityWith("name", "New Name");
        bodyContainsEntityWith("property_set_type", "geolocation");
        // delete
        authorizationHelpers.entityIsDeleted(PROPERTY_SETS_PATH, propertySetId);

    }

    @Test
    public void userPropertySetCRUD() throws Throwable {
        // create user
        UUID userId = userHelpers.userIsCreatedWithAuth(testUser1);
        // create PS
        UUID propertySetId = authorizationHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        // create relation
        UserPropertySetRelationshipCreateDto relation = relationshipsHelpers.constructUserPropertySetRelationshipDto(userId, propertySetId, true);
        UUID relationId = authorizationHelpers.entityIsCreated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relation);
        // request
        authorizationHelpers.getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("user_id", userId.toString());
        bodyContainsEntityWith("property_set_id", propertySetId.toString());
        bodyContainsEntityWith("is_active", "true");
        // update
        UserPropertySetRelationshipUpdateDto update = new UserPropertySetRelationshipUpdateDto();
        update.setIsActive(false);
        authorizationHelpers.entityIsUpdated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, update);
        // make sure changes applied
        authorizationHelpers.getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("is_active", "false");
        // delete
        authorizationHelpers.entityIsDeleted(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
    }
}
