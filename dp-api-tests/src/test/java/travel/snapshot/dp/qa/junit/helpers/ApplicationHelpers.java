package travel.snapshot.dp.qa.junit.helpers;


/**
 * Created by zelezny on 6/30/2017.
 */

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.qa.cucumber.serenity.DbUtilsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.applications.ApplicationsSteps;

import java.util.UUID;

@Log
public class ApplicationHelpers extends ApplicationsSteps {

    public ApplicationHelpers() { super();}
    private DbUtilsSteps dbSteps = new DbUtilsSteps();

    public Response createApplication(ApplicationDto application) {
        Response createResponse = null;
        try {
            JSONObject jsonApplication = retrieveData(application);
            createResponse = createEntity(jsonApplication.toString());
        } catch (JsonProcessingException e) {
            log.severe("Unable to convert application object to json");
        }
        setSessionResponse(createResponse);
        return createResponse;
    }

    public ApplicationDto applicationIsCreated(ApplicationDto createdApplication) {
        Response response = createApplication(createdApplication);
        assertEquals(String.format("Failed to create application: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        setSessionResponse(response);
        return response.as(ApplicationDto.class);
    }

    public void grantAllPermissions(UUID applicationId) {
        dbSteps.populateApplicationPermissionsTableForApplication(applicationId);
    }
}
