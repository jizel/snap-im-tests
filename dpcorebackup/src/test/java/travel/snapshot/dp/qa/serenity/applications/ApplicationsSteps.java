package travel.snapshot.dp.qa.serenity.applications;

import static java.util.Arrays.stream;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_RESOURCE;

import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationUpdateDto;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApplicationsSteps extends BasicSteps {

    private static final String SESSION_CREATED_APPLICATION = "created_application";
    private static final String SESSION_APPLICATIONS = "applications";
    private static final String SESSION_APPLICATION_ID = "application_id";
    private static final String SESSION_APPLICATION_VERSION_ID = "version_id";
    private static final String SESSION_CREATED_APPLICATION_VERSIONS = "created_application_version";
    private static final String APPLICATIONS_PATH = "/applications";

    public ApplicationsSteps() {
        super();
        spec.baseUri(propertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(APPLICATIONS_PATH);
    }

    @Step
    public Response followingApplicationIsCreated(ApplicationDto application) {
        Response response = createEntity(application);
        setSessionResponse(response);
        return response;
    }

    @Step
    public void followingApplicationsExist(List<ApplicationDto> applications) {
        applications.forEach((application) -> {
            UUID partnerId = application.getId();
            Boolean isInternal = application.getIsInternal();
            if (partnerId == null) {
                application.setPartnerId(DEFAULT_SNAPSHOT_PARTNER_ID);
            }
            if (isInternal == null) {
                application.setIsInternal(true);
            }
            Response createResponse = createEntity(application);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Application cannot be created: " + createResponse.asString());
            }
        });
    }

    @Step
    public void applicationWithIdIsDeleted(UUID applicationId) {
        deleteEntityWithEtag(applicationId);
        Serenity.setSessionVariable(SESSION_APPLICATION_ID).to(applicationId);
    }

    @Step
    public void applicationIdInSessionDoesntExist() {
        UUID applicationId = Serenity.sessionVariableCalled(SESSION_APPLICATION_ID);

        Response response = getEntity(applicationId);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void updateApplication(UUID applicationId, ApplicationUpdateDto applicationUpdate, String etag) throws Throwable {
        JSONObject update = retrieveData(applicationUpdate);
        Response response = updateEntity(applicationId, update.toString(), etag);
        setSessionResponse(response);
    }

    @Step
    public void applicationWithIdHasData(UUID applicationId, ApplicationDto applicationData) throws Throwable {
        Map<String, Object> originalData = retrieveDataOld(ApplicationDto.class, getApplicationById(applicationId));
        Map<String, Object> expectedData = retrieveDataOld(ApplicationDto.class, applicationData);

        expectedData.forEach((k, v) -> {
            if (v == null) {
                assertFalse("Application JSON should not contains attributes with null values",
                        originalData.containsKey(k));
                return;
            }
            assertTrue("Application has no data for attribute " + k, originalData.containsKey(k));
            assertEquals(v, originalData.get(k));
        });
    }

    @Step
    public void applicationWithIdIsRequested(UUID applicationId) {

        Response resp = getEntity(applicationId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }

    @Step
    public void applicationWithIdIsGotWithEtag(UUID applicationId) {
        Response resp = getEntity(applicationId);
        setSessionResponse(resp);
    }

    @Step
    public void listOfApplicationsIsGotWith(UUID userId, UUID appVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUserForApp(userId, appVersionId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }


    public ApplicationDto getApplicationById(UUID applicationId) {
        ApplicationDto[] applications =
                getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "application_id==" + applicationId, null, null, null)
                        .as(ApplicationDto[].class);
        return Arrays.stream(applications).findFirst().orElse(null);
    }

    @Step
    public void namesInResponseInOrder(List<String> applications) {
        Response response = getSessionResponse();
        ApplicationDto[] apps = response.as(ApplicationDto[].class);
        int i = 0;
        for (ApplicationDto a : apps) {
            assertEquals("Application on index=" + i + " is not expected", applications.get(i), a.getName());
            i++;
        }
    }


    @Step
    public void getCommSubscriptionForApplicationId(UUID applicationId) {
        Response appCommSubscriptionResponse = getSecondLevelEntities(applicationId, COMMERCIAL_SUBSCRIPTIONS_RESOURCE, LIMIT_TO_ALL,
                CURSOR_FROM_FIRST, null, null, null, null);
        setSessionResponse(appCommSubscriptionResponse);
    }

    @Step
    public void listOfApplicationCommSubscriptionsIsGotWith(UUID applicationId, String limit, String cursor, String filter,
                                                            String sort, String sortDesc) {
        Response response = getSecondLevelEntities(applicationId, COMMERCIAL_SUBSCRIPTIONS_RESOURCE, limit, cursor, filter,
                sort, sortDesc, null);
        setSessionResponse(response);
    }

    public ApplicationDto getApplicationByName(String applicationName) {
        ApplicationDto[] application =
                getEntities(null, null, null, "name=='" + applicationName + "'", null, null, null).as(ApplicationDto[].class);
        return stream(application).findFirst().orElse(null);
    }

    public UUID resolveApplicationId(String applicationName) {
        if (applicationName == null) return DEFAULT_SNAPSHOT_APPLICATION_ID;

        UUID applicationId;
        if (isUUID(applicationName)) {
            applicationId = UUID.fromString(applicationName);
        } else {
            ApplicationDto application = getApplicationByName(applicationName);
            assertThat(String.format("Application with name \"%s\" does not exist", applicationName), application , is(notNullValue()));
            applicationId = application.getId();
        }
        return applicationId;
    }


}
