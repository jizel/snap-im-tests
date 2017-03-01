package travel.snapshot.dp.qa.serenity.applications;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.stream;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationUpdateDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationsSteps extends BasicSteps {

    private static final String SESSION_CREATED_APPLICATION = "created_application";
    private static final String SESSION_APPLICATIONS = "applications";
    private static final String SESSION_APPLICATION_ID = "application_id";
    private static final String SESSION_APPLICATION_VERSION_ID = "version_id";
    private static final String SESSION_CREATED_APPLICATION_VERSIONS = "created_application_version";
    private static final String APPLICATIONS_PATH = "/identity/applications";

    public ApplicationsSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(APPLICATIONS_PATH);
    }

    @Step
    public void followingApplicationIsCreated(ApplicationDto application) {
        Response response = createEntity(application);
        setSessionResponse(response);
    }

    @Step
    public void followingApplicationsExist(List<ApplicationDto> applications) {
        applications.forEach((application) -> {
            String partnerId = application.getPartnerId();
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
    public void applicationWithIdIsDeleted(String applicationId) {
        deleteEntityWithEtag(applicationId);
        Serenity.setSessionVariable(SESSION_APPLICATION_ID).to(applicationId);
    }

    @Step
    public void applicationIdInSessionDoesntExist() {
        String applicationId = Serenity.sessionVariableCalled(SESSION_APPLICATION_ID);

        Response response = getEntity(applicationId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void updateApplication(String applicationId, ApplicationUpdateDto applicationUpdate, String etag) throws Throwable {
        JSONObject update = retrieveData(applicationUpdate);
        Response response = updateEntity(applicationId, update.toString(), etag);
        setSessionResponse(response);
    }

    @Step
    public void updateApplicationWithIdIfUpdatedBefore(String applicationId, ApplicationDto application) throws Throwable {
        ApplicationDto original = getApplicationById(applicationId);

        Map<String, Object> customerData = retrieveDataOld(ApplicationDto.class, application);

        Response response = updateEntity(original.getApplicationId(), customerData, "fake-etag");
        setSessionResponse(response);
    }

    @Step
    public void applicationWithIdHasData(String applicationId, ApplicationDto applicationData) throws Throwable {
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
    public void applicationWithIdIsGot(String applicationId) {

        Response resp = getEntity(applicationId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }

    @Step
    public void applicationWithIdIsGotWithEtag(String applicationId) {
        Response resp = getEntity(applicationId, getEntityEtag(applicationId));
        setSessionResponse(resp);
    }

    @Step
    public void listOfApplicationsIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void applicationWithIdIsGotWithEtagAfterUpdate(String applicationId) {

        Response tempResponse = getEntity(applicationId, null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("name", "Application test company 1");
        mapForUpdate.put("description", "UpdatedDescription");
        mapForUpdate.put("website", "http://www.snapshot.travel");

        Response updateResponse = updateEntity(applicationId, mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Application cannot be updated: " + updateResponse.asString());
        }

        Response resp = getEntity(applicationId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    public ApplicationDto getApplicationById(String applicationId) {
        ApplicationDto[] applications =
                getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "application_id==" + applicationId, null, null, null)
                        .as(ApplicationDto[].class);
        return Arrays.asList(applications).stream().findFirst().orElse(null);
    }

    @Step
    public void namesInResponseInOrder(List<String> applications) {
        Response response = getSessionResponse();
        ApplicationDto[] apps = response.as(ApplicationDto[].class);
        int i = 0;
        for (ApplicationDto a : apps) {
            assertEquals("Application on index=" + i + " is not expected", applications.get(i), a.getApplicationName());
            i++;
        }
    }

    @Step
    public void followingApplicationVersionsExists(String applicationId, List<ApplicationVersionDto> applicationVersions) {

        applicationVersions.forEach(t -> {
            ApplicationVersionDto existingAppVersion = getApplicationVersionByName(applicationId, t.getVersionName());
            if (existingAppVersion != null) {
                deleteSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, existingAppVersion.getVersionId(), null);
            }
            Response createResponse = createApplicationVersion(t, applicationId);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Application version cannot be created");
            }
        });
        Serenity.setSessionVariable(SESSION_APPLICATIONS).to(applicationVersions);
    }

    @Step
    public void applicationVersionIsDeleted(String appVersionId, String applicationId) {
        ApplicationVersionDto appVersion = getApplicationVersionById(applicationId, appVersionId);
        if (appVersion == null) {
            return;
        }

        Response response = deleteSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId, null);
        setSessionResponse(response);
        Serenity.setSessionVariable(SESSION_APPLICATION_VERSION_ID).to(appVersionId);
    }

    @Step
    public void applicationVersionIdInSessionDoesntExist(String applicationId) {
        String appVersionId = Serenity.sessionVariableCalled(SESSION_APPLICATION_VERSION_ID);

        Response response = getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void deleteAppVersionWithId(String id, String versionId) {
        Response response = deleteSecondLevelEntity(id, SECOND_LEVEL_OBJECT_VERSIONS, versionId, null);
        setSessionResponse(response);
    }

    @Step
    public void updateApplicationVersionWithId(String appVersionId, String applicationId,
                                               ApplicationVersionDto applicationVersionUpdates) throws Throwable {
        ApplicationVersionDto original = getApplicationVersionById(applicationId, appVersionId);
        Response tempResponse =
                getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, original.getVersionId());

        Map<String, Object> applicationVersionData = retrieveDataOld(ApplicationVersionDto.class, applicationVersionUpdates);

        Response response = updateSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS,
                original.getVersionId(), applicationVersionData, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void applicationVersionWithIdHasData(String appVersionId, String applicationId,
                                                ApplicationVersionDto applicationVersion) throws Throwable {
        Map<String, Object> originalData =
                retrieveDataOld(ApplicationVersionDto.class, getApplicationVersionById(applicationId, appVersionId));
        Map<String, Object> expectedData = retrieveDataOld(ApplicationVersionDto.class, applicationVersion);

        expectedData.forEach((k, v) -> {
            if (v == null) {
                assertFalse("Application JSON should not contains attributes with null values",
                        originalData.containsKey(k));
                return;
            }
            assertTrue("Application version has no data for attribute " + k, originalData.containsKey(k));
            assertEquals(v, originalData.get(k));
        });
    }

    @Step
    public void updateApplicationVersionWithInvalidEtag(String appVersionId, String applicationId,
                                                        ApplicationVersionDto applicationVersion) throws Throwable {

        Map<String, Object> applicationVersionData = retrieveDataOld(ApplicationVersionDto.class, applicationVersion);

        Response updateResponse = updateSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId,
                applicationVersionData, "invalid");
        setSessionResponse(updateResponse);
    }

    @Step
    public void applicationVersionWithIdIsGot(String appVersionId, String applicationId) {
        Response resp = getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }

    @Step
    public void applicationVersionWithIdIsGotWithEtag(String appVersionId, String applicationId) {
        Response tempResponse = getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId);
        Response resp = getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId);
        setSessionResponse(resp);
    }

    @Step
    public void listOfApplicationVersionsIsGotWith(String applicationId, String limit, String cursor, String filter,
                                                   String sort, String sortDesc) {
        Response response = getSecondLevelEntities(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, limit, cursor, filter,
                sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void versionNamesInResponseInOrder(List<String> versionNames) {
        Response response = getSessionResponse();
        ApplicationVersionDto[] appVersions = response.as(ApplicationVersionDto[].class);
        int i = 0;
        for (ApplicationVersionDto a : appVersions) {
            assertEquals("Application version on index=" + i + " is not expected", versionNames.get(i),
                    a.getVersionName());
            i++;
        }
    }

    @Step
    public void getCommSubscriptionForApplicationId(String applicationId) {
        Response appCommSubscriptionResponse = getSecondLevelEntities(applicationId, SECOND_LEVEL_OBJECT_COMMERCIAL_SUBSCRIPTIONS, LIMIT_TO_ALL,
                CURSOR_FROM_FIRST, null, null, null, null);
        setSessionResponse(appCommSubscriptionResponse);
    }

    @Step
    public void listOfApplicationCommSubscriptionsIsGotWith(String applicationId, String limit, String cursor, String filter,
                                                            String sort, String sortDesc) {
        Response response = getSecondLevelEntities(applicationId, SECOND_LEVEL_OBJECT_COMMERCIAL_SUBSCRIPTIONS, limit, cursor, filter,
                sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public Response createApplicationVersion(ApplicationVersionDto applicationVersion, String applicationId) {
        Serenity.setSessionVariable(SESSION_CREATED_APPLICATION_VERSIONS).to(applicationVersion);
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_ID).body(applicationVersion).when().post("/{id}/application_versions", applicationId);
    }

    public ApplicationVersionDto getApplicationVersionByName(String applicationId, String versionName) {
        ApplicationVersionDto[] applicationVersion =
                getSecondLevelEntities(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, LIMIT_TO_ONE, CURSOR_FROM_FIRST,
                        "name=='" + versionName + "'", null, null, null).as(ApplicationVersionDto[].class);
        return stream(applicationVersion).findFirst().orElse(null);
    }

    public ApplicationVersionDto getApplicationVersionById(String applicationId, String versionId) {
        ApplicationVersionDto[] applicationVersion = getSecondLevelEntities(applicationId, SECOND_LEVEL_OBJECT_VERSIONS,
                LIMIT_TO_ONE, CURSOR_FROM_FIRST, "application_version_id==" + versionId, null, null, null).as(ApplicationVersionDto[].class);
        return stream(applicationVersion).findFirst().orElse(null);
    }

}
