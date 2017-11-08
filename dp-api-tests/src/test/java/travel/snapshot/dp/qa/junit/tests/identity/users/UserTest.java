package travel.snapshot.dp.qa.junit.tests.identity.users;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static travel.snapshot.dp.api.identity.model.UserUpdateDto.UserType.GUEST;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_MESSAGE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Basic tests for IM User entity
 */
public class UserTest extends CommonTest {

    private UUID createdUserId;

    private static final String UNIQUE_EMAIL_MSG = "The field email must be unique.";
    private static final String UNIQUE_USERNAME_MSG = "The field user_name must be unique.";

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        createdUserId = entityIsCreated(testUser1);
    }

    @ParameterizedTest
    @EnumSource(UserUpdateDto.UserType.class)
    public void CRUDAllUserTypes(UserUpdateDto.UserType type) {
        testUser2.setType(type);
        UUID userId = entityIsCreated(testUser2);

        updateEntity(USERS_PATH, userId, getTestUpdate()).then().statusCode(SC_OK);

        // User cannot be removed unless User customer relationship is removed first
        userHelpers.deleteUserCustomerRelationshipIfExists(userId);
        deleteEntity(USERS_PATH, userId).then().statusCode(SC_NO_CONTENT);
    }

    @Test
    void userNameAndEmailAreUniqueCreate() {
        testUser2.setEmail(testUser1.getEmail());
        createUserReturnsConflictWithMessage(testUser2, UNIQUE_EMAIL_MSG);

        testUser3.setUsername(testUser1.getUsername());
        createUserReturnsConflictWithMessage(testUser3, UNIQUE_USERNAME_MSG);
    }

    @Test
    void userNameAndEmailAreUniqueUpdate() {
        UUID createdUser2Id = entityIsCreated(testUser2);

        updateConflictEmail(createdUser2Id, testUser1.getEmail());

        updateConflictUserName(createdUser2Id, testUser1.getUsername());
    }

    @Test
    void customerUserCannotBeCreatedWithoutCustomerRelationship() {
        testUser2.setUserCustomerRelationship(null);
        createEntity(testUser2)
                .then().statusCode(SC_BAD_REQUEST)
                .assertThat().body(RESPONSE_CODE, is(CC_MISSING_PARAMS));
    }

    @ParameterizedTest
    @EnumSource(value = UserUpdateDto.UserType.class, mode = EXCLUDE, names = {"CUSTOMER"})
    void otherUsersDontNeedCustomerRelationship(UserUpdateDto.UserType type) {
        testSnapshotUser1.setType(type);
        createEntity(testSnapshotUser1).then().statusCode(SC_CREATED);
    }

    @Test
    public void updateUser() throws Exception {
        UserDto updateResponseUser = updateEntity(USERS_PATH, createdUserId, getTestUpdate())
                .then().statusCode(SC_OK)
                .extract().response().as(UserDto.class);
        UserDto requestedCustomer = getEntityAsType(USERS_PATH, UserDto.class, createdUserId);
        assertThat("Update response body differs from the same user requested by GET ", updateResponseUser, is(requestedCustomer));
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

    @Test
    void userCustomerRelationshipAttributeCreatesTheRelationship() {
        List<UserCustomerRelationshipDto> relationships = getEntitiesAsType(
                USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, filterParam("user_id==" + createdUserId)
        );
        assertThat(relationships.size(), is(1));
        UserCustomerRelationshipDto relationship = relationships.get(0);
        assertAll(
                () -> assertThat(relationship.getUserId(), is(createdUserId)),
                () -> assertThat(relationship.getCustomerId(), is(DEFAULT_SNAPSHOT_CUSTOMER_ID)),
                () -> assertFalse(relationship.getIsPrimary()),
                () -> assertTrue(relationship.getIsActive())
        );
    }

    private UserUpdateDto getTestUpdate() {
        UserUpdateDto userUpdate = new UserUpdateDto();
        userUpdate.setUsername("UpdatedUsername");
        userUpdate.setEmail("updated@snapshot.travel");
        userUpdate.setLastName("NewLastName");
        userUpdate.setPhone("+1666999666");
        userUpdate.setType(GUEST);
        userUpdate.setIsActive(false);

        return userUpdate;
    }

    private void createUserReturnsConflictWithMessage(UserCreateDto user, String message) {
        createEntity(user).then().statusCode(SC_CONFLICT)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_CONFLICT_CODE))
                .body(RESPONSE_MESSAGE, is(message));
    }

    private void updateConflictEmail(UUID userId, String email) {
        UserUpdateDto userUpdate = new UserUpdateDto();
        userUpdate.setEmail(email);
        updateEntity(USERS_PATH, userId, userUpdate).then().statusCode(SC_CONFLICT)
                .body(RESPONSE_CODE, is(CC_CONFLICT_CODE))
                .body(RESPONSE_MESSAGE, is(UNIQUE_EMAIL_MSG));
    }

    private void updateConflictUserName(UUID userId, String username) {
        UserUpdateDto userUpdate = new UserUpdateDto();
        userUpdate.setUsername(username);
        updateEntity(USERS_PATH, userId, userUpdate).then().statusCode(SC_CONFLICT)
                .body(RESPONSE_CODE, is(CC_CONFLICT_CODE))
                .body(RESPONSE_MESSAGE, is(UNIQUE_USERNAME_MSG));
    }
}
