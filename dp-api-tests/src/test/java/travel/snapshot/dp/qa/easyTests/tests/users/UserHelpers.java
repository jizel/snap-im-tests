package travel.snapshot.dp.qa.easyTests.tests.users;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.serenity.BasicSteps;

@Log
public class UserHelpers extends BasicSteps {

    public UserHelpers() {
        super();
    }

    public Response createUser(UserCreateDto createdUser) {
        try {
            JSONObject jsonUser = retrieveData(createdUser);
            return (Response) createEntity(jsonUser.toString());
        } catch (JsonProcessingException e) {
//            log.severe("Unable to convert user object to json");
        }
        return null;
    }

    public UserDto userIsCreated(UserCreateDto createdUser) {
        Response response = createUser(createdUser);
        setSessionResponse(response);
        assertEquals(String.format("Failed to create user: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(UserDto.class);
    }

}
