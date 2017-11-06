package travel.snapshot.dp.qa.junit.tests.identity.applications;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_PRECONDITION_FAILED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_NAME;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ETAG;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.commercialSubscriptionIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityWithEtag;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest.RESTRICTIONS_APPLICATIONS_ENDPOINT;
import static travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest.RESTRICTIONS_SINGLE_APPLICATION_ENDPOINT;

import org.junit.jupiter.api.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.ApplicationCreateDto;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationUpdateDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionCreateDto;
import travel.snapshot.dp.api.type.HttpMethod;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.List;
import java.util.UUID;

/**
 * CRUD tests for /identity/applications endpoint
 */
public class ApplicationsTests extends CommonTest {

    private UUID externalAppId;

    @Test
    @Jira("DPIM-77")
    public void partnerIdIsReturnedOnlyForInternalAppAllApplications() throws Exception {
        prepareExternalAppPermissionsAndRelations(testApplication3);
        ApplicationVersionCreateDto externalAppVersion = testAppVersion3;
        externalAppVersion.setApplicationId(externalAppId);
        externalAppVersion.setIsNonCommercial(true);
        UUID externalAppVersionId = entityIsCreated(externalAppVersion);
//        Get all apps with internal context app
        List<ApplicationDto> applications = getEntitiesAsType(APPLICATIONS_PATH, ApplicationDto.class, null);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(ApplicationDto.class, 2);
        applications.forEach(app -> assertThat(app.getPartnerId(), not(nullValue())));
//        Get all apps with external context app
        applications = getEntitiesAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, null);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(ApplicationDto.class, 2);
        applications.forEach(app -> assertNull(app.getPartnerId()));
//        Check external commercial applications too
        externalAppVersion.setIsNonCommercial(false);
        applications = getEntitiesAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, null);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(ApplicationDto.class, 2);
        applications.forEach(app -> assertNull(app.getPartnerId()));
    }

    @Test
    @Jira("DPIM-77")
    public void partnerIdIsReturnedOnlyForInternalAppSingleApplication() throws Exception {
        prepareExternalAppPermissionsAndRelations(testApplication3);
        ApplicationVersionCreateDto externalAppVersion = testAppVersion3;
        externalAppVersion.setApplicationId(externalAppId);
        externalAppVersion.setIsNonCommercial(true);
        UUID externalAppVersionId = entityIsCreated(externalAppVersion);

//        Get single app with internal context app
        assertThat(getEntityAsType(APPLICATIONS_PATH, ApplicationDto.class, externalAppId).getPartnerId(),
                is(testApplication3.getPartnerId()));
        assertThat(getEntityAsType(APPLICATIONS_PATH, ApplicationDto.class, DEFAULT_SNAPSHOT_APPLICATION_ID).getPartnerId(),
                is(DEFAULT_SNAPSHOT_PARTNER_ID));
//        Get single app with external context app
        assertNull(
                getEntityAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, externalAppId)
                        .getPartnerId());
        assertNull(
                getEntityAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, DEFAULT_SNAPSHOT_APPLICATION_ID)
                        .getPartnerId());
//        Check external commercial applications too
        externalAppVersion.setIsNonCommercial(true);
        assertNull(
                getEntityAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, externalAppId)
                        .getPartnerId());
        assertNull(
                getEntityAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, DEFAULT_SNAPSHOT_APPLICATION_ID)
                        .getPartnerId());
    }

    @Test
    public void applicationCRUD() {
        UUID applicationID = entityIsCreated(testApplication1);
        getEntity(APPLICATIONS_PATH, applicationID).then()
                .statusCode(SC_OK).assertThat()
                .body("name", is("InternalApp1"))
                .body("description", is("Desc1"))
                .body("partner_id", is(String.valueOf(DEFAULT_SNAPSHOT_PARTNER_ID)))
                .body("is_internal", is(true));
        ApplicationUpdateDto updateDto = new ApplicationUpdateDto();
        updateDto.setIsInternal(false);
        updateApplicationWithInvalidEtag(applicationID, updateDto);
        entityIsUpdated(APPLICATIONS_PATH, applicationID, updateDto);
        getEntity(APPLICATIONS_PATH, applicationID).then().assertThat()
                .body("is_internal", is(false));
        entityIsDeleted(APPLICATIONS_PATH, applicationID);
    }

    @Test
    public void createInvalidApplication() {
        testApplication1.setName(null);
        createEntity(testApplication1).then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS));
        testApplication1.setName(DEFAULT_SNAPSHOT_APPLICATION_NAME);
        createEntity(testApplication1).then()
                .statusCode(SC_CONFLICT)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_CONFLICT_CODE));
        testApplication1.setName("Some App");
        testApplication1.setId(DEFAULT_SNAPSHOT_APPLICATION_ID);
        createEntity(testApplication1).then()
                .statusCode(SC_CONFLICT)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_CONFLICT_ID));
        testApplication1.setId(null);
        testApplication1.setWebsite("http");
        createEntity(testApplication1).then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS));
        testApplication1.setWebsite("http://snapshot.travel");
        testApplication1.setPartnerId(NON_EXISTENT_ID);
        createEntity(testApplication1).then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_NON_EXISTING_REFERENCE));
    }

    //    Help methods
    private void prepareExternalAppPermissionsAndRelations(ApplicationCreateDto application) {
        externalAppId = entityIsCreated(application);
        dbSteps.addApplicationPermission(externalAppId, RESTRICTIONS_APPLICATIONS_ENDPOINT, HttpMethod.GET);
        dbSteps.addApplicationPermission(externalAppId, RESTRICTIONS_SINGLE_APPLICATION_ENDPOINT, HttpMethod.GET);
        entityIsCreated(constructUserCustomerRelationshipDto(
                DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_CUSTOMER_ID, true, true));
        entityIsCreated(constructUserPropertyRelationshipDto(
                DEFAULT_SNAPSHOT_USER_ID, DEFAULT_PROPERTY_ID, true));
        commercialSubscriptionIsCreated(DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID, externalAppId);
    }

    private void updateApplicationWithInvalidEtag(UUID appId, ApplicationUpdateDto update) {
        updateEntityWithEtag(APPLICATIONS_PATH, appId, update, NON_EXISTENT_ETAG)
                .then()
                .statusCode(SC_PRECONDITION_FAILED)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_INVALID_ETAG));
    }
}
