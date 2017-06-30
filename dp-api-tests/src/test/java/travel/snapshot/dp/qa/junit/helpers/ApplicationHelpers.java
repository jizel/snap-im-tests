package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.qa.cucumber.serenity.applications.ApplicationVersionsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.applications.ApplicationsSteps;

/**
 * Created by zelezny on 6/30/2017.
 */
public class ApplicationHelpers extends ApplicationsSteps {

    private ApplicationVersionsSteps applicationVersionsSteps = new ApplicationVersionsSteps();

    public ApplicationDto applicationIsCreated(ApplicationDto application) {
        Response response = followingApplicationIsCreated(application);
        assertEquals(String.format("Failed to create user group: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(ApplicationDto.class);
    }

    public ApplicationVersionDto applicationVersionIsCreated(ApplicationVersionDto applicationVersion) {
        Response response = applicationVersionsSteps.createApplicationVersion(applicationVersion);
        assertEquals(String.format("Failed to create user group: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(ApplicationVersionDto.class);
    }
}
