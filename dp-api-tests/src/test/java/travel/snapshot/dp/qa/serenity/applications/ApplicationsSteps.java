package travel.snapshot.dp.qa.serenity.applications;

import com.jayway.restassured.response.Response;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.VersionDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

        Serenity.setSessionVariable(SESSION_CREATED_APPLICATION).to(application);
        ApplicationDto existingApplication = getApplicationById(application.getApplicationId());
        if (existingApplication != null) {
            deleteEntity(existingApplication.getApplicationId());
        }
        Response response = createEntity(application);
        setSessionResponse(response);
    }

    @Step
    public void followingApplicationsExist(List<ApplicationDto> application) {
        application.forEach(t -> {
            Response createResponse = createEntity(t);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Application cannot be created! Status:" + createResponse.getStatusCode() + " " + createResponse.body().asString());
            }
        });
    }

    @Step
    public void applicationWithIdIsDeleted(String applicationId) {
        ApplicationDto app = getApplicationById(applicationId);
        if (app == null) {
            return;
        }

        Response response = deleteEntity(applicationId);
        setSessionResponse(response);
        Serenity.setSessionVariable(SESSION_APPLICATION_ID).to(applicationId);
    }

    @Step
    public void applicationIdInSessionDoesntExist() {
        String applicationId = Serenity.sessionVariableCalled(SESSION_APPLICATION_ID);

        Response response = getEntity(applicationId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void deleteApplicationWithId(String id) {
        Response response = deleteEntity(id);
        setSessionResponse(response);
    }

    @Step
    public void updateApplicationWithId(String applicationId, ApplicationDto applicationUpdates) throws Throwable {
        ApplicationDto original = getApplicationById(applicationId);
        Response tempResponse = getEntity(original.getApplicationId(), null);

        Map<String, Object> applicationData = retrieveData(ApplicationDto.class, applicationUpdates);

        Response response =
                updateEntity(original.getApplicationId(), applicationData, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void updateApplicationWithIdIfUpdatedBefore(String applicationId, ApplicationDto application) throws Throwable {
        ApplicationDto original = getApplicationById(applicationId);

        Map<String, Object> customerData = retrieveData(ApplicationDto.class, application);

        Response response = updateEntity(original.getApplicationId(), customerData, "fake-etag");
        setSessionResponse(response);
    }

    @Step
    public void applicationWithIdHasData(String applicationId, ApplicationDto applicationData) throws Throwable {
        Map<String, Object> originalData = retrieveData(ApplicationDto.class, getApplicationById(applicationId));
        Map<String, Object> expectedData = retrieveData(ApplicationDto.class, applicationData);

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

        Response tempResponse = getEntity(applicationId, null);
        Response resp = getEntity(applicationId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void listOfApplicationsIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
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
                getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, "application_id==" + applicationId, null, null)
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
    public void getApplicationsRolesForApplicationId(String applicationId) {
        Response customerUsersResponse = getSecondLevelEntities(applicationId, SECOND_LEVEL_OBJECT_ROLES, LIMIT_TO_ALL,
                CURSOR_FROM_FIRST, null, null, null);
        setSessionResponse(customerUsersResponse);
    }

    @Step
    public void listOfApplicationsRolesIsGotWith(String applicationId, String limit, String cursor, String filter,
                                                 String sort, String sortDesc) {
        Response response =
                getSecondLevelEntities(applicationId, SECOND_LEVEL_OBJECT_ROLES, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void roleNamesInResponseInOrder(List<String> roleNames) {
        Response response = getSessionResponse();
        RoleDto[] roles = response.as(RoleDto[].class);
        int i = 0;
        for (RoleDto r : roles) {
            assertEquals("Application role on index=" + i + " is not expected", roleNames.get(i), r.getRoleName());
            i++;
        }
    }

    @Step
    public void followingApplicationVersionsAreCreated(String applicationId, VersionDto applicationVersion) {

        Serenity.setSessionVariable(SESSION_CREATED_APPLICATION_VERSIONS).to(applicationVersion);
        VersionDto existingAppVersion =
                getApplicationVersionByName(applicationId, applicationVersion.getVersionName());
        if (existingAppVersion != null) {
            deleteSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, existingAppVersion.getVersionId());
        }
        Response response = createApplicationVersion(applicationVersion, applicationId);
        setSessionResponse(response);
    }

    @Step
    public void followingApplicationVersionsExists(String applicationId, List<VersionDto> applicationVersions) {

        applicationVersions.forEach(t -> {
            VersionDto existingAppVersion = getApplicationVersionByName(applicationId, t.getVersionName());
            if (existingAppVersion != null) {
                deleteSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, existingAppVersion.getVersionId());
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
        VersionDto appVersion = getApplicationVersionById(applicationId, appVersionId);
        if (appVersion == null) {
            return;
        }

        Response response = deleteSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId);
        setSessionResponse(response);
        Serenity.setSessionVariable(SESSION_APPLICATION_VERSION_ID).to(appVersionId);
    }

    @Step
    public void applicationVersionIdInSessionDoesntExist(String applicationId) {
        String appVersionId = Serenity.sessionVariableCalled(SESSION_APPLICATION_VERSION_ID);

        Response response = getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void deleteAppVersionWithId(String id, String versionId) {
        Response response = deleteSecondLevelEntity(id, SECOND_LEVEL_OBJECT_VERSIONS, versionId);
        setSessionResponse(response);
    }

    @Step
    public void updateApplicationVersionWithId(String appVersionId, String applicationId,
                                               VersionDto applicationVersionUpdates) throws Throwable {
        VersionDto original = getApplicationVersionById(applicationId, appVersionId);
        Response tempResponse =
                getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, original.getVersionId(), null);

        Map<String, Object> applicationVersionData = retrieveData(VersionDto.class, applicationVersionUpdates);

        Response response = updateSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS,
                original.getVersionId(), applicationVersionData, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void applicationVersionWithIdHasData(String appVersionId, String applicationId,
                                                VersionDto applicationVersion) throws Throwable {
        Map<String, Object> originalData =
                retrieveData(VersionDto.class, getApplicationVersionById(applicationId, appVersionId));
        Map<String, Object> expectedData = retrieveData(VersionDto.class, applicationVersion);

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
                                                        VersionDto applicationVersion) throws Throwable {

        Map<String, Object> applicationVersionData = retrieveData(VersionDto.class, applicationVersion);

        Response updateResponse = updateSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId,
                applicationVersionData, "invalid");
        setSessionResponse(updateResponse);
    }

    @Step
    public void applicationVersionWithIdIsGot(String appVersionId, String applicationId) {
        Response resp = getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId, null);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }

    @Step
    public void applicationVersionWithIdIsGotWithEtag(String appVersionId, String applicationId) {
        Response tempResponse = getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId, null);
        Response resp = getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId,
                tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void applicationVersionWithIdIsGotWithEtagAfterUpdate(String appVersionId, String applicationId) {
        Response tempResponse = getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId, null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("name", "Version 123");
        mapForUpdate.put("api_key", "123");
        mapForUpdate.put("status", "inactive");
        mapForUpdate.put("release_date", "2016-02-22");
        mapForUpdate.put("description", "UpdatedDescription");

        Response updateResponse = updateSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId,
                mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Application version cannot be updated: " + updateResponse.asString());
        }

        Response resp = getSecondLevelEntity(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, appVersionId,
                tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void listOfApplicationVersionsIsGotWith(String applicationId, String limit, String cursor, String filter,
                                                   String sort, String sortDesc) {
        Response response = getSecondLevelEntities(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, limit, cursor, filter,
                sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void versionNamesInResponseInOrder(List<String> versionNames) {
        Response response = getSessionResponse();
        VersionDto[] appVersions = response.as(VersionDto[].class);
        int i = 0;
        for (VersionDto a : appVersions) {
            assertEquals("Application version on index=" + i + " is not expected", versionNames.get(i),
                    a.getVersionName());
            i++;
        }
    }

    @Step
    public void getCommSubscriptionForApplicationId(String applicationId) {
        Response appCommSubscriptionResponse = getSecondLevelEntities(applicationId, "", LIMIT_TO_ALL,
                CURSOR_FROM_FIRST, null, null, null);
        setSessionResponse(appCommSubscriptionResponse);
    }

    @Step
    public void listOfApplicationCommSubscriptionsIsGotWith(String applicationId, String limit, String cursor, String filter,
                                                            String sort, String sortDesc) {
        Response response = getSecondLevelEntities(applicationId, "", limit, cursor, filter,
                sort, sortDesc);
        setSessionResponse(response);
    }

    private Response createApplicationVersion(VersionDto applicationVersion, String applicationId) {

        return given().spec(spec).body(applicationVersion).when().post("/{id}/application_versions", applicationId);
    }

    public VersionDto getApplicationVersionByName(String applicationId, String versionName) {
        VersionDto[] applicationVersion =
                getSecondLevelEntities(applicationId, SECOND_LEVEL_OBJECT_VERSIONS, LIMIT_TO_ONE, CURSOR_FROM_FIRST,
                        "name=='" + versionName + "'", null, null).as(VersionDto[].class);
        return Arrays.asList(applicationVersion).stream().findFirst().orElse(null);
    }

    public VersionDto getApplicationVersionById(String applicationId, String versionId) {
        VersionDto[] applicationVersion = getSecondLevelEntities(applicationId, SECOND_LEVEL_OBJECT_VERSIONS,
                LIMIT_TO_ONE, CURSOR_FROM_FIRST, "application_version_id==" + versionId, null, null).as(VersionDto[].class);
        return Arrays.asList(applicationVersion).stream().findFirst().orElse(null);
    }

}
