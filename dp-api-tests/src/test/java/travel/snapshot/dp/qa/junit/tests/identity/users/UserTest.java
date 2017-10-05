package travel.snapshot.dp.qa.junit.tests.identity.users;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.model.UserUpdateDto.UserType.GUEST;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;
import java.util.UUID;

/**
 * Basic tests for IM User entity
 */
public class UserTest extends CommonTest{

    private UUID createdUserId;

    @Before
    public void setUp() {
        super.setUp();
        createdUserId = entityIsCreated(testUser1);
    }

    @Test
    public void updateUser() throws Exception {
        UserUpdateDto userUpdate = new UserUpdateDto();
        userUpdate.setUsername("UpdatedUsername");
        userUpdate.setEmail("updated@snapshot.travel");
        userUpdate.setLastName("NewLastName");
        userUpdate.setPhone("+1666999666");
        userUpdate.setType(GUEST);
        Response updateResponse = updateEntity(USERS_PATH, createdUserId, userUpdate);
        responseCodeIs(SC_OK);
        UserDto updateResponseCustomer = updateResponse.as(UserDto.class);
        UserDto requestedCustomer = getEntityAsType(USERS_PATH, UserDto.class, createdUserId);
        assertThat("Update response body differs from the same user requested by GET ", updateResponseCustomer, is(requestedCustomer));
    }

    @Test
    public void invalidUpdateUser() throws Exception {
        Map<String, String> invalidUpdate = singletonMap("invalid_key", "whatever");
        updateEntity(USERS_PATH, createdUserId, invalidUpdate);
        responseIsUnprocessableEntity();

        invalidUpdate = singletonMap("email", "invalid_value");
        updateEntity(USERS_PATH, createdUserId, invalidUpdate);
        responseIsUnprocessableEntity();

        updateEntity(USERS_PATH, randomUUID(), invalidUpdate);
        responseIsEntityNotFound();
    }
}
