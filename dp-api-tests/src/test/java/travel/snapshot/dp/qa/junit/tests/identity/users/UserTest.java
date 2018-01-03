package travel.snapshot.dp.qa.junit.tests.identity.users;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static travel.snapshot.dp.api.identity.model.UserType.GUEST;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_DETAILS;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_MESSAGE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.parseResponseAsListOfObjects;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserType;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

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
    @EnumSource(UserType.class)
    void CRUDAllUserTypes(UserType type) {
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

    @ParameterizedTest
    @EnumSource(value = UserType.class)
    void usersDontNeedCustomerRelationship(UserType type) {
        testUser1.setUserCustomerRelationship(null);
        testUser1.setType(type);
        createEntity(testSnapshotUser1).then().statusCode(SC_CREATED);
    }

    @Test
    void updateUser() throws Exception {
        UserDto updateResponseUser = updateEntity(USERS_PATH, createdUserId, getTestUpdate())
                .then().statusCode(SC_OK)
                .extract().response().as(UserDto.class);
        UserDto requestedCustomer = getEntityAsType(USERS_PATH, UserDto.class, createdUserId);
        assertThat("Update response body differs from the same user requested by GET ", updateResponseUser, is(requestedCustomer));
    }

    @Test
    void invalidUpdateUser() throws Exception {
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


    @Test
    @Jira("DPIM-185")
    void dontAllowAssociationsOfSnapshotUsersWithAnyEntity() {
        String message = "SNAPSHOT user cannot be used.";
        createEntity(constructUserCustomerRelationshipDto(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_CUSTOMER_ID, true, true))
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY).assertThat().body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS))
                .body("details", hasItem(message));
        createEntity(constructUserPropertyRelationshipDto(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_PROPERTY_ID, true))
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY).assertThat().body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS))
                .body("details", hasItem(message));
        UUID psId = entityIsCreated(testPropertySet1);
        createEntity(constructUserPropertySetRelationshipDto(DEFAULT_SNAPSHOT_USER_ID, psId, true))
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY).assertThat().body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS))
                .body("details", hasItem(message));
        UUID groupID = entityIsCreated(testUserGroup1);
        createEntity(constructUserGroupUserRelationship(groupID,  DEFAULT_SNAPSHOT_USER_ID, true))
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY).assertThat().body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS))
                .body("details", hasItem(message));
    }

    @Test
    @Jira("DPIM-176")
    void testCultureUpdate() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setLanguageCode("En-us-AU");
        updateEntity(USERS_PATH, createdUserId, updateDto)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS))
                .body(RESPONSE_DETAILS, hasItem("The 'culture' attribute is not valid IETF language tag"));
        updateDto.setLanguageCode("en-AU");
        updateEntity(USERS_PATH, createdUserId, updateDto).then().statusCode(SC_OK);
    }

    @Test
    @Jira("DPIM-69")
    void testCombinedSorting() {
        // Setup users
        // 1
        testUser1.setId(null);
        testUser1.setEmail("adamh@snapshot.travel");
        testUser1.setUsername("adamt");
        testUser1.setFirstName("Adam");
        testUser1.setLastName("Team");
        entityIsCreated(testUser1);
        // 2
        testUser1.setId(null);
        testUser1.setEmail("adamw@snapshot.travel");
        testUser1.setUsername("adamw");
        testUser1.setFirstName("Adam");
        testUser1.setLastName("Walczynski");
        entityIsCreated(testUser1);
        // Test
        Map<String, String> params1 = QueryParams.builder().sort("first_name,last_name").build();
        getEntities(USERS_PATH, params1);
        assertThat(parseResponseAsListOfObjects(UserDto.class).get(0).getUsername(), is("adamt"));

        Map<String, String> params2 = QueryParams.builder().sort("first_name,-last_name").build();
        getEntities(USERS_PATH, params2);
        assertThat(parseResponseAsListOfObjects(UserDto.class).get(0).getUsername(), is("adamw"));

        Map<String, String> params = QueryParams.builder().sort("last_name,first_name").build();
        getEntities(USERS_PATH, params);
        assertThat(parseResponseAsListOfObjects(UserDto.class).get(0).getUsername(), is("johnSmith"));

    }

    // Help methods

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
