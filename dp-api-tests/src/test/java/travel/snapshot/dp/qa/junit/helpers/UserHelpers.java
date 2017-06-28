package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import lombok.extern.java.Log;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.cucumber.serenity.users.UsersSteps;

@Log
public class UserHelpers extends UsersSteps {

    public UserHelpers() {
        super();
    }

    public UserDto userIsCreated(UserCreateDto createdUser) {
        Response response = createUser(createdUser);
        assertEquals(String.format("Failed to create user: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(UserDto.class);
    }

    public UserDto userWithCustomerIsCreated(UserCreateDto createdUser, String customerId) {
        Response response = createUserWithCustomer(createdUser, customerId, true, true);
        assertEquals(String.format("Failed to create user: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(UserDto.class);
    }
}
