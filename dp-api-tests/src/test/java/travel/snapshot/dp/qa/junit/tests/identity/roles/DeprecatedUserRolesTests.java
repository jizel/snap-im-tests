package travel.snapshot.dp.qa.junit.tests.identity.roles;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_XAUTH_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.utils.RestAssuredConfig.setupRequestDefaults;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.RoleRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Tests for deprecated roles endpoints:
 * /identity/users/{user_id}/customers/{customer_id}/roles
 * /identity/users/{user_id}/properties/{property_id}/roles
 *
 * Remove when the endpoints are deleted
 */
public class DeprecatedUserRolesTests extends CommonTest {

    private UUID createdUserId;
    private UUID createdCustomerId;
    private UUID createdPropertyId;
    private RoleRelationshipDto testCustomerRoleRelationship;
    private RoleRelationshipDto testPropertyRoleRelationship;


    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        createdCustomerId = entityIsCreated(testCustomer1);
        createdUserId = entityIsCreated(testUser1);
        createdPropertyId = entityIsCreated(testProperty1);
        userHelpers.deleteUserCustomerRelationshipIfExists(createdUserId);
        entityIsCreated(constructUserCustomerRelationshipDto(createdUserId, createdCustomerId, true, true));
        entityIsCreated(constructUserPropertyRelationshipDto(createdUserId, createdPropertyId, true));
        UUID createdRoleId = entityIsCreated(testRole1);
        UUID createdRoleId2 = entityIsCreated(testPropertyRole1);
        testCustomerRoleRelationship = new RoleRelationshipDto();
        testCustomerRoleRelationship.setRoleId(createdRoleId);
        testPropertyRoleRelationship = new RoleRelationshipDto();
        testPropertyRoleRelationship.setRoleId(createdRoleId2);
    }

    @Test
    void userCustomerDeprecatedRoleCRUD() {
        UUID createdRoleRelationshipId = createDeprecatedUserRole(createdUserId, CUSTOMERS_RESOURCE, createdCustomerId, testCustomerRoleRelationship)
                .then().statusCode(SC_CREATED)
                .extract().response().as(RoleRelationshipDto.class).getRoleId();
        getDeprecatedUserRole(createdUserId, CUSTOMERS_RESOURCE, createdCustomerId, createdRoleRelationshipId);
        assertThat(getDeprecatedUserRoles(createdUserId, CUSTOMERS_RESOURCE, createdCustomerId, createdRoleRelationshipId))
                .isEqualTo(createdRoleRelationshipId);
        getViaNewRolesEndpoints(createdRoleRelationshipId);
        deleteDeprecatedUserRole(createdUserId, CUSTOMERS_RESOURCE, createdCustomerId, createdRoleRelationshipId)
                .then()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    void userPropertyDeprecatedRoleCRUD() {
        UUID createdRoleRelationshipId = createDeprecatedUserRole(createdUserId, PROPERTIES_RESOURCE, createdPropertyId, testPropertyRoleRelationship)
                .then().statusCode(SC_CREATED)
                .extract().response().as(RoleRelationshipDto.class).getRoleId();
        getDeprecatedUserRole(createdUserId, PROPERTIES_RESOURCE, createdPropertyId, createdRoleRelationshipId);
        assertThat(getDeprecatedUserRoles(createdUserId, PROPERTIES_RESOURCE, createdPropertyId, createdRoleRelationshipId))
                .isEqualTo(createdRoleRelationshipId);
        deleteDeprecatedUserRole(createdUserId, PROPERTIES_RESOURCE, createdPropertyId, createdRoleRelationshipId)
                .then()
                .statusCode(SC_NO_CONTENT);
    }

    @Test
    void userCustomerDeprecatedRoleNegativeTest() {
        createDeprecatedUserRole(NON_EXISTENT_ID, CUSTOMERS_RESOURCE, createdCustomerId, testCustomerRoleRelationship)
                .then().statusCode(SC_NOT_FOUND);
        createDeprecatedUserRole(createdUserId, CUSTOMERS_RESOURCE, NON_EXISTENT_ID, testCustomerRoleRelationship)
                .then().statusCode(SC_NOT_FOUND);
        deleteDeprecatedUserRole(createdUserId, CUSTOMERS_RESOURCE, createdCustomerId, NON_EXISTENT_ID)
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    void userPropertyDeprecatedRoleNegativeTest() {
        createDeprecatedUserRole(NON_EXISTENT_ID, PROPERTIES_RESOURCE, createdPropertyId, testPropertyRoleRelationship)
                .then().statusCode(SC_NOT_FOUND);
        createDeprecatedUserRole(createdUserId, PROPERTIES_RESOURCE, NON_EXISTENT_ID, testPropertyRoleRelationship)
                .then().statusCode(SC_NOT_FOUND);
        deleteDeprecatedUserRole(createdUserId, PROPERTIES_RESOURCE, createdPropertyId, NON_EXISTENT_ID)
                .then()
                .statusCode(SC_NOT_FOUND);
    }

    private Response createDeprecatedUserRole(UUID userId, String secondLevelResource, UUID secondLevelId, RoleRelationshipDto role) {
        return givenContext()
                .basePath(USERS_PATH)
                .body(role)
                .post("{id}/{secondLevelName}/{secondLevelId}/roles", userId, secondLevelResource, secondLevelId);
    }

    private UUID getDeprecatedUserRole(UUID userId, String secondLevelResource, UUID secondLevelId, UUID roleId) {
        return givenContext()
                .basePath(USERS_PATH)
                .get("{id}/{secondLevelName}/{secondLevelId}/roles/{roleId}", userId, secondLevelResource, secondLevelId, roleId)
                .then()
                .statusCode(SC_OK)
                .extract().response().as(RoleRelationshipDto.class).getRoleId();
    }

    private UUID getDeprecatedUserRoles(UUID userId, String secondLevelResource, UUID secondLevelId, UUID roleId) {
        return Stream.of(givenContext()
                .basePath(USERS_PATH)
                .get("{id}/{secondLevelName}/{secondLevelId}/roles", userId, secondLevelResource, secondLevelId)
                .then()
                .statusCode(SC_OK)
                .extract().response().as(RoleRelationshipDto[].class))
                .findFirst().orElseThrow(() -> new RuntimeException("Empty list returned")).getRoleId();
    }

    private Response deleteDeprecatedUserRole(UUID userId, String secondLevelResource, UUID secondLevelId, UUID roleId) {
        return givenContext()
                .basePath(USERS_PATH)
                .delete("{id}/{secondLevelName}/{secondLevelId}/roles/{roleId}", userId, secondLevelResource, secondLevelId, roleId);
    }

    private RequestSpecification givenContext() {
        return given().spec(setupRequestDefaults())
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
    }

    private void getViaNewRolesEndpoints(UUID expectedRoleId) {
        List<CustomerRoleDto> customerRoleDtos = getEntitiesAsType(USER_CUSTOMER_ROLES_PATH, CustomerRoleDto.class, emptyQueryParams());
        assertThat(customerRoleDtos.stream().findFirst().map(CustomerRoleDto::getId).orElseThrow(() -> new RuntimeException("Empty list returned")))
                .isEqualTo(expectedRoleId);
    }
}
