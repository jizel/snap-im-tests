package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByPatternByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityEtag;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityWithEtagByUserForApp;

import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonAccessChecksByApplicationTest;

import java.util.UUID;

public class PropertyAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @Test
    public void filteringPropertiesWithAccessChecks() {
        assertThat(
                getEntitiesByPatternByUserForApp(userId, appVersionId, PROPERTIES_PATH, "name", "Property*")
        ).hasSize(1);
    }

    @Test
    public void getPropertyWithAndWithoutAccess() {
        getEntityByUserForApplication(userId, appVersionId, PROPERTIES_PATH, propertyId1)
                .then()
                .statusCode(SC_OK);
        getEntityByUserForApplication(userId, appVersionId, PROPERTIES_PATH, propertyId2)
                .then()
                .statusCode(SC_NOT_FOUND)
                .assertThat().body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
    }

    @Test
    public void anchorCustomerIdOfCustomerWithoutCommercialSubscriptionCannotBeUsedWhenCreatingOrUpdatingProperty() {
        UUID customerId = entityIsCreated(testCustomer1);
        PropertyUpdateDto updateDto = new PropertyUpdateDto();
        updateDto.setCustomerId(customerId);
        String etag = getEntityEtag(PROPERTIES_PATH, propertyId1);
        updateEntityWithEtagByUserForApp(userId, appVersionId, PROPERTIES_PATH, propertyId1, updateDto, etag)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_NON_EXISTING_REFERENCE));
        testProperty3.setCustomerId(customerId);
        createEntityByUserForApplication(userId, appVersionId, testProperty3)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_NON_EXISTING_REFERENCE));
    }

    @Test
    public void applicationWithAndWithoutAccessUpdatesProperty() {
        String etag = getEntityEtag(PROPERTIES_PATH, propertyId2);
        PropertyUpdateDto updateDto = new PropertyUpdateDto();
        updateDto.setDescription("new description");
        updateEntityWithEtagByUserForApp(userId, appVersionId, PROPERTIES_PATH, propertyId2, updateDto, etag)
                .then()
                .statusCode(SC_NOT_FOUND)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
        deleteEntityByUserForApp(userId, appVersionId, PROPERTIES_PATH, propertyId2)
                .then()
                .statusCode(SC_NOT_FOUND)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
        updateEntityByUserForApp(userId, appVersionId, PROPERTIES_PATH, propertyId1, updateDto)
                .then()
                .statusCode(SC_OK);
        deleteEntityByUserForApp(userId, appVersionId, PROPERTIES_PATH, propertyId1)
                .then()
                .statusCode(SC_CONFLICT)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_ENTITY_REFERENCED));
    }
}
