package travel.snapshot.dp.qa.junit.tests.identity.applications;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest.RESTRICTIONS_APPLICATIONS_ENDPOINT;
import static travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest.RESTRICTIONS_SINGLE_APPLICATION_ENDPOINT;

import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.type.HttpMethod;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.List;
import java.util.UUID;

/**
 * CRUD tests for /identity/applications endpoint
 */
public class ApplicationsTests extends CommonTest{

    private UUID externalAppId;

    @Test
    @Jira("DPIM-77")
    public void partnerIdIsReturnedOnlyForInternalAppAllApplications() throws Exception {
        prepareExternalAppPermissionsAndRelations(testApplication3);
        ApplicationVersionDto externalAppVersion = testAppVersion3;
        externalAppVersion.setApplicationId(externalAppId);
        externalAppVersion.setIsNonCommercial(true);
        UUID externalAppVersionId = commonHelpers.entityIsCreated(APPLICATION_VERSIONS_PATH, externalAppVersion);
//        Get all apps with internal context app
        List<ApplicationDto> applications =  commonHelpers.getEntitiesAsType(APPLICATIONS_PATH, ApplicationDto.class, null);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(ApplicationDto.class, 2);
        applications.forEach(app -> assertThat(app.getPartnerId(), not(nullValue())));
//        Get all apps with external context app
        applications =  commonHelpers.getEntitiesAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, null);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(ApplicationDto.class, 2);
        applications.forEach(app -> assertNull(app.getPartnerId()));
//        Check external commercial applications too
        externalAppVersion.setIsNonCommercial(false);
        applications =  commonHelpers.getEntitiesAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, null);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(ApplicationDto.class, 2);
        applications.forEach(app -> assertNull(app.getPartnerId()));
    }

    @Test
    @Jira("DPIM-77")
    public void partnerIdIsReturnedOnlyForInternalAppSingleApplication() throws Exception {
        prepareExternalAppPermissionsAndRelations(testApplication3);
        ApplicationVersionDto externalAppVersion = testAppVersion3;
        externalAppVersion.setApplicationId(externalAppId);
        externalAppVersion.setIsNonCommercial(true);
        UUID externalAppVersionId = commonHelpers.entityIsCreated(APPLICATION_VERSIONS_PATH, externalAppVersion);

//        Get single app with internal context app
        assertThat(commonHelpers.getEntityAsType(APPLICATIONS_PATH, ApplicationDto.class, externalAppId).getPartnerId(),
                is(testApplication3.getPartnerId()));
        assertThat(commonHelpers.getEntityAsType(APPLICATIONS_PATH, ApplicationDto.class, DEFAULT_SNAPSHOT_APPLICATION_ID).getPartnerId(),
                is(DEFAULT_SNAPSHOT_PARTNER_ID));
//        Get single app with external context app
        assertNull(
                commonHelpers.getEntityAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, externalAppId)
                        .getPartnerId());
        assertNull(
                commonHelpers.getEntityAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, DEFAULT_SNAPSHOT_APPLICATION_ID)
                        .getPartnerId());
//        Check external commercial applications too
        externalAppVersion.setIsNonCommercial(true);
        assertNull(
                commonHelpers.getEntityAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, externalAppId)
                        .getPartnerId());
        assertNull(
                commonHelpers.getEntityAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, externalAppVersionId, APPLICATIONS_PATH, ApplicationDto.class, DEFAULT_SNAPSHOT_APPLICATION_ID)
                        .getPartnerId());
    }

    //    Help methods
    private void prepareExternalAppPermissionsAndRelations(ApplicationDto application){
        externalAppId = commonHelpers.entityIsCreated(APPLICATIONS_PATH, application);
        dbSteps.addApplicationPermission(externalAppId, RESTRICTIONS_APPLICATIONS_ENDPOINT, HttpMethod.GET.toString());
        dbSteps.addApplicationPermission(externalAppId, RESTRICTIONS_SINGLE_APPLICATION_ENDPOINT, HttpMethod.GET.toString());
        commonHelpers.entityIsCreated(USER_CUSTOMER_RELATIONSHIPS_PATH, relationshipsHelpers
                .constructUserCustomerRelationshipDto(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_CUSTOMER_ID, true, true));
        commonHelpers.entityIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, relationshipsHelpers
                .constructUserPropertyRelationshipDto(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_PROPERTY_ID, true));
        commercialSubscriptionHelpers.commercialSubscriptionIsCreated(DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID, externalAppId);
    }
}
