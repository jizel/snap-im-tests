package travel.snapshot.dp.qa.junit.tests.identity.commercial_subscriptions;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.constructCommercialSubscriptionDto;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;

public class CommercialSubscriptionTests extends CommonTest {
    protected UUID applicationId;

    @Before
    public void setUp() {
        super.setUp();
        testApplication1.setIsInternal(false);
        applicationId = entityIsCreated(testApplication1);
    }

    @Test
    public void commercialSubscriptionCRUD() {
        UUID subscriptionId = entityIsCreated(constructCommercialSubscriptionDto(applicationId, DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID));
        getEntity(COMMERCIAL_SUBSCRIPTIONS_PATH, subscriptionId)
                .then()
                .statusCode(SC_OK)
                .assertThat()
                .body("application_id", is(String.valueOf(applicationId)))
                .body("customer_id", is(String.valueOf(DEFAULT_SNAPSHOT_CUSTOMER_ID)))
                .body("property_id", is(String.valueOf(DEFAULT_PROPERTY_ID)));
        Map<String, String> params = buildQueryParamMapForPaging(null, null, "customer_id==" + String.valueOf(DEFAULT_SNAPSHOT_CUSTOMER_ID), null, null, null);
        assertThat(getEntitiesAsType(COMMERCIAL_SUBSCRIPTIONS_PATH, CommercialSubscriptionDto.class, params)).hasSize(2);
        // There is no update on commercial subscriptions
        entityIsDeleted(COMMERCIAL_SUBSCRIPTIONS_PATH, subscriptionId);
    }

    @Test
    public void applicationWithCommercialSubscriptionCanNotBeDeleted() {
        UUID subscriptionId = entityIsCreated(constructCommercialSubscriptionDto(applicationId, DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID));
        deleteEntity(APPLICATIONS_PATH, applicationId).then().statusCode(SC_CONFLICT).assertThat().body(RESPONSE_CODE, is(CC_ENTITY_REFERENCED));
        entityIsDeleted(COMMERCIAL_SUBSCRIPTIONS_PATH, subscriptionId);
        entityIsDeleted(APPLICATIONS_PATH, applicationId);

    }
}
