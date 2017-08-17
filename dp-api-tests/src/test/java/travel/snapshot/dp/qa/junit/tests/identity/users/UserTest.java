package travel.snapshot.dp.qa.junit.tests.identity.users;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.model.UserUpdateDto.UserType.GUEST;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Basic tests for IM User entity
 */
public class UserTest extends CommonTest{

    private UUID createdUserId;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdUserId = commonHelpers.entityIsCreated(USERS_PATH, testUser1);
    }

    @Test
    public void updateUser() throws Exception {
        UserUpdateDto userUpdate = new UserUpdateDto();
        userUpdate.setUsername("UpdatedUsername");
        userUpdate.setEmail("updated@snapshot.travel");
        userUpdate.setLastName("NewLastName");
        userUpdate.setPhone("+1666999666");
        userUpdate.setType(GUEST);
        Response updateResponse = commonHelpers.updateEntity(USERS_PATH, createdUserId, userUpdate);
        responseCodeIs(SC_OK);
        UserDto updateResponseCustomer = updateResponse.as(UserDto.class);
        UserDto requestedCustomer = commonHelpers.getEntityAsType(USERS_PATH, UserDto.class, createdUserId);
        assertThat("Update response body differs from the same user requested by GET ", updateResponseCustomer, is(requestedCustomer));
    }
}
