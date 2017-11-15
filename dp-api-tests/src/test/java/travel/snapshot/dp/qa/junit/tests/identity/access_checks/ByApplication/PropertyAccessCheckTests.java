package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonAccessChecksByApplicationTest;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityEtag;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityWithEtagByUserForApp;

public class PropertyAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        accessibleEntityId = propertyId1;
        inaccessibleEntityId = propertyId2;
        PATH = PROPERTIES_PATH;
        PATTERN = "Property*";
    }

    @Test
    public void anchorCustomerIdOfCustomerWithoutCommercialSubscriptionCannotBeUsedWhenCreatingOrUpdatingProperty() {
        UUID customerId = entityIsCreated(testCustomer1);
        PropertyUpdateDto updateDto = new PropertyUpdateDto();
        updateDto.setCustomerId(customerId);
        String etag = getEntityEtag(PATH, accessibleEntityId);
        updateEntityWithEtagByUserForApp(userId, appVersionId, PATH, accessibleEntityId, updateDto, etag)
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
}
