package travel.snapshot.dp.qa.junit.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.qa.cucumber.serenity.applications.ApplicationsSteps;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.assertEquals;

@Log
public class ApplicationVersionHelpers extends ApplicationsSteps {

    public ApplicationVersionHelpers() { super();}

    public Response createApplicationVersion(ApplicationVersionDto applicationVersion) {
        Response createResponse = null;
        try {
            JSONObject jsonApplicationVersion = retrieveData(applicationVersion);
            createResponse = createEntity(jsonApplicationVersion.toString());
        } catch (JsonProcessingException e) {
            log.severe("Unable to convert applicationVersion object to json");
        }
        setSessionResponse(createResponse);
        return createResponse;
    }

    public ApplicationVersionDto applicationVersionIsCreated(ApplicationVersionDto createdApplicationVersion) {
        Response response = createApplicationVersion(createdApplicationVersion);
        assertEquals(String.format("Failed to create applicationVersion: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        setSessionResponse(response);
        return response.as(ApplicationVersionDto.class);
    }

}
