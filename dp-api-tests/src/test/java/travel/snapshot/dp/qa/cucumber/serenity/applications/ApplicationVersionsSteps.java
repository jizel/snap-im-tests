package travel.snapshot.dp.qa.cucumber.serenity.applications;

import static java.util.Arrays.stream;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionUpdateDto;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.util.List;
import java.util.Map;

/**
 * Created by zelezny on 2/16/2017.
 */
public class ApplicationVersionsSteps extends BasicSteps {

    private static final String SESSION_CREATED_APPLICATION = "created_application";
    private static final String SESSION_APPLICATIONS = "applications";
    private static final String SESSION_APPLICATION_ID = "application_id";
    private static final String SESSION_APPLICATION_VERSION_ID = "version_id";
    private static final String SESSION_CREATED_APPLICATION_VERSIONS = "created_application_version";
    private static final String APPLICATIONS_VERSIONS_PATH = "/identity/application_versions";

    public ApplicationVersionsSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(APPLICATIONS_VERSIONS_PATH);
    }


    @Step
    public Response getApplicationVersion(String versionId){
        return getApplicationVersionByUser(DEFAULT_SNAPSHOT_USER_ID, versionId);
    }

    @Step
    public Response getApplicationVersionByUser(String requestorId, String versionId){
        Response response = getEntityByUser(requestorId, versionId);
        setSessionResponse(response);
        return response;
    }


    @Step
    public void followingApplicationVersionsExists(List<ApplicationVersionDto> applicationVersions) {
        applicationVersions.forEach(applicationVersion -> {
            Response createResponse = createApplicationVersion(applicationVersion);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Application version cannot be created" + createResponse.getBody());
            }
        });
        Serenity.setSessionVariable(SESSION_APPLICATIONS).to(applicationVersions);
    }

    @Step
    public void deleteApplicationVersion(String appVersionId) {
        String etag = getEntityEtag(appVersionId);
        Response response = deleteEntity(appVersionId, etag);
        setSessionResponse(response);
        Serenity.setSessionVariable(SESSION_APPLICATION_VERSION_ID).to(appVersionId);
    }

    @Step
    public void updateApplicationVersion(String appVersionId, ApplicationVersionUpdateDto applicationVersionUpdate, String etag) throws Throwable {
        JSONObject update = retrieveData(applicationVersionUpdate);

        Response response = updateEntity(appVersionId, update.toString(), etag);
        setSessionResponse(response);
    }

    @Step
    public void applicationVersionWithIdHasData(String appVersionId, ApplicationVersionDto applicationVersion) throws Throwable {
        Map<String, Object> originalData = retrieveDataOld(ApplicationVersionDto.class, getApplicationVersion(appVersionId).as(ApplicationVersionDto.class));
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
    public void listOfApplicationVersionsIsGotWith(String userId, String appVersionId, String limit, String cursor, String filter,
                                                   String sort, String sortDesc) {
        Response response = getEntitiesByUserForApp(userId, appVersionId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void versionNamesInResponseInOrder(List<String> versionNames) {
        Response response = getSessionResponse();
        ApplicationVersionDto[] appVersions = response.as(ApplicationVersionDto[].class);
        int i = 0;
        for (ApplicationVersionDto a : appVersions) {
            assertEquals("Application version on index=" + i + " is not expected", versionNames.get(i),
                    a.getName());
            i++;
        }
    }

    @Step
    public Response createApplicationVersion(ApplicationVersionDto applicationVersion) {
        Serenity.setSessionVariable(SESSION_CREATED_APPLICATION_VERSIONS).to(applicationVersion);
        return createEntity(applicationVersion);
    }


    // Help and non-step methods

    public ApplicationVersionDto getApplicationVersionByName(String versionName) {
        ApplicationVersionDto[] applicationVersion =
                getEntities(null, null, null, "name=='" + versionName + "'", null, null, null).as(ApplicationVersionDto[].class);
        return stream(applicationVersion).findFirst().orElse(null);
    }

    public String resolveApplicationVersionId(String applicationVersionName) {
        if (applicationVersionName == null) return DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;

        String applicationVersionId;
        if (isUUID(applicationVersionName)) {
            applicationVersionId = applicationVersionName;
        } else {
            ApplicationVersionDto applicationVersion = getApplicationVersionByName(applicationVersionName);
            assertThat(String.format("Application version with name \"%s\" does not exist", applicationVersionName), applicationVersion , is(notNullValue()));
            applicationVersionId = applicationVersion.getId();
        }
        return applicationVersionId;
    }
}