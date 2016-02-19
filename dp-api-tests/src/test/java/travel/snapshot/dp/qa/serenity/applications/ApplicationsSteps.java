package travel.snapshot.dp.qa.serenity.applications;

import com.jayway.restassured.response.Response;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Application;
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
    private static final String APPLICATIONS_PATH = "/identity/applications";

    public ApplicationsSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(APPLICATIONS_PATH);
    }

    @Step
    public void followingApplicationIsCreated(Application application) {

        Serenity.setSessionVariable(SESSION_CREATED_APPLICATION).to(application);
        Application existingApplication = getApplicationById(application.getApplicationId());
        if (existingApplication != null) {
            deleteApplication(existingApplication.getApplicationId());
        }
        Response response = createEntity(application);
        setSessionResponse(response);
    }

    @Step
    public void followingApplicationsExist(List<Application> application) {
        application.forEach(t -> {
            Application existingApplication = getApplicationById(t.getApplicationId());
            if (existingApplication != null) {
                deleteApplication(existingApplication.getApplicationId());
            }
            Response createResponse = createEntity(t);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Application cannot be created");
            }
        });
        Serenity.setSessionVariable(SESSION_APPLICATIONS).to(application);
    }

    @Step
    public void applicationWithIdIsDeleted(String applicationId) {
        Application app = getApplicationById(applicationId);
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
        Response response = deleteApplication(id);
        setSessionResponse(response);
    }

    @Step
    public void updateApplicationWithId(String applicationId, Application applicationUpdates)
            throws Throwable {
        Application original = getApplicationById(applicationId);
        Response tempResponse = getEntity(original.getApplicationId(), null);

        Map<String, Object> applicationData = retrieveData(Application.class, applicationUpdates);

        Response response = updateEntity(original.getApplicationId(), applicationData,
                tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void updateApplicationWithIdIfUpdatedBefore(String applicationId, Application application)
            throws Throwable {
        Application original = getApplicationById(applicationId);

        Map<String, Object> customerData = retrieveData(Application.class, application);

        Response response = updateEntity(original.getApplicationId(), customerData, "fake-etag");
        setSessionResponse(response);
    }

    @Step
    public void applicationWithIdHasData(String applicationId, Application applicationData)
            throws Throwable {
        Map<String, Object> originalData =
                retrieveData(Application.class, getApplicationById(applicationId));
        Map<String, Object> expectedData = retrieveData(Application.class, applicationData);

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
    public void listOfApplicationsIsGotWith(String limit, String cursor, String filter, String sort,
                                            String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void applicationWithIdIsGotWithEtagAfterUpdate(String applicationId) {

        Response tempResponse = getEntity(applicationId, null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("application_name", "Application test company 1");
        mapForUpdate.put("description", "UpdatedDescription");
        mapForUpdate.put("website", "http://www.snapshot.travel");

        Response updateResponse =
                updateEntity(applicationId, mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Customer cannot be updated: " + updateResponse.asString());
        }

        Response resp = getEntity(applicationId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    public Application getApplicationById(String applicationId) {
        Application[] applications =
                getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, "application_id==" + applicationId, null, null)
                        .as(Application[].class);
        return Arrays.asList(applications).stream().findFirst().orElse(null);
    }

    private Response deleteApplication(String id) {
        return given().spec(spec).when().delete("/{application_id}", id);
    }

}
