package travel.snapshot.dp.qa.junit.tests.identity.access_checks.service_token;

import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.commercialSubscriptionIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;
import static travel.snapshot.dp.qa.junit.utils.DpEndpoints.READ_WRITE_ENDPOINTS;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.qa.cucumber.serenity.DbUtilsSteps;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.List;
import java.util.UUID;

public class ServiceTokenTests extends CommonTest {

    private static UUID userId1;
    private static UUID createdAppVersionId;
    private static PropertyDto createdProperty2;
    private static CustomerDto createdCustomer2;
    private static PropertySetDto createdPropertySet3;
    private static UserDto createdUser2;
    private static UserGroupDto createdUserGroup;


    @BeforeAll
    static void setUpRelationships() {
        cleanDbAndLoadDefaultEntities();
        createTestingEntities();


    }

    @Override
    @BeforeEach
    public void setUp() {
//        Override default - don't delete DB before every test
    }

    @Test
    void userIdHeaderIsOptional() {
        READ_WRITE_ENDPOINTS.forEach(endpoint -> {
            getEntitiesByUserForApp(null, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, endpoint, null)
                    .then().statusCode(SC_OK);
        });
    }

    @Test
    void userRestrictionsDontApplyToServiceToken() {
        assertAll(
                () -> assertThat(getPropertiesByUser(userId1)).hasSize(1),
                () -> assertThat(getPropertiesByUser(null)).hasSize(2),
                () -> assertThat(getPropertySetsByUser(userId1)).hasSize(1),
                () -> assertThat(getPropertySetsByUser(null)).hasSize(2),
                () -> assertThat(getCustomersByUser(userId1)).hasSize(1),
                () -> assertThat(getCustomersByUser(null)).hasSize(2),
                () -> assertThat(getUsersByUser(userId1)).hasSize(1),
                () -> assertThat(getUsersByUser(null)).hasSize(2),
                () -> assertThat(getUserGroupsByUser(userId1)).hasSize(1),
                () -> assertThat(getUserGroupsByUser(null)).hasSize(2)
        );
    }

    @Test
    void accessIsBasedOnCommSubscription() {
        createExternalAppAndItsEntities();
        setInternalAttrributesNull();
        assertAll(
                () -> assertThat(getPropertiesByApp(createdAppVersionId)).containsOnly(createdProperty2),
                () -> assertThat(getPropertySetsByApp(createdAppVersionId)).containsOnly(createdPropertySet3),
                () -> assertThat(getCustomersByApp(createdAppVersionId)).containsOnly(createdCustomer2),
                () -> assertThat(getUsersByApp(createdAppVersionId)).containsOnly(createdUser2),
                () -> assertThat(getUserGroupsByApp(createdAppVersionId)).containsOnly(createdUserGroup)
        );
    }

    private List<PropertyDto> getPropertiesByUser(UUID userId) {
        return getEntitiesAsTypeByUserForApp(
                userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTIES_PATH, PropertyDto.class, emptyQueryParams());
    }

    private List<PropertySetDto> getPropertySetsByUser(UUID userId) {
        return getEntitiesAsTypeByUserForApp(
                userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTY_SETS_PATH, PropertySetDto.class, emptyQueryParams());
    }

    private List<CustomerDto> getCustomersByUser(UUID userId) {
        return getEntitiesAsTypeByUserForApp(
                userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, CUSTOMERS_PATH, CustomerDto.class, emptyQueryParams());
    }

    private List<UserDto> getUsersByUser(UUID userId) {
        return getEntitiesAsTypeByUserForApp(
                userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, USERS_PATH, UserDto.class, emptyQueryParams());
    }

    private List<UserGroupDto> getUserGroupsByUser(UUID userId) {
        return getEntitiesAsTypeByUserForApp(
                userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, USER_GROUPS_PATH, UserGroupDto.class, emptyQueryParams());
    }

    private List<PropertyDto> getPropertiesByApp(UUID appVersionId) {
        return getEntitiesAsTypeByUserForApp(
                null, appVersionId, PROPERTIES_PATH, PropertyDto.class, emptyQueryParams());
    }

    private List<PropertySetDto> getPropertySetsByApp(UUID appVersionId) {
        return getEntitiesAsTypeByUserForApp(
                null, appVersionId, PROPERTY_SETS_PATH, PropertySetDto.class, emptyQueryParams());
    }

    private List<CustomerDto> getCustomersByApp(UUID appVersionId) {
        return getEntitiesAsTypeByUserForApp(
                null, appVersionId, CUSTOMERS_PATH, CustomerDto.class, emptyQueryParams());
    }

    private List<UserDto> getUsersByApp(UUID appVersionId) {
        return getEntitiesAsTypeByUserForApp(
                null, appVersionId, USERS_PATH, UserDto.class, emptyQueryParams());
    }

    private List<UserGroupDto> getUserGroupsByApp(UUID appVersionId) {
        return getEntitiesAsTypeByUserForApp(
                null, appVersionId, USER_GROUPS_PATH, UserGroupDto.class, emptyQueryParams());
    }


    private static void createTestingEntities() {
        UUID createdCustomerId = entityIsCreated(testCustomer1);
        userId1 = entityIsCreated(testUser1);
        UUID propertyId = entityIsCreated(testProperty1);
        UUID propertySetId1 = entityIsCreated(testPropertySet1);
        entityIsCreated(testPropertySet2);
        UUID userGroupId1 = entityIsCreated(testUserGroup1);
        entityIsCreated(testUserGroup2);
        entityIsCreated(constructCustomerPropertyRelationshipDto(DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID, true, CHAIN, validFrom, validTo));
        entityIsCreated(constructUserPropertyRelationshipDto(userId1, propertyId, true));
        entityIsCreated(constructUserPropertySetRelationshipDto(userId1, propertySetId1, true));
        entityIsCreated(constructUserGroupUserRelationship(userGroupId1, userId1, true));
    }

    private static void createExternalAppAndItsEntities() {
        UUID createdAppId = entityIsCreated(testApplication3);
        DbUtilsSteps dbUtilsSteps = new DbUtilsSteps();
        dbUtilsSteps.populateApplicationPermissionsTableForApplication(createdAppId);
        testAppVersion1.setApplicationId(createdAppId);
        createdAppVersionId = entityIsCreated(testAppVersion1);
        createdCustomer2 = entityIsCreatedAs(CustomerDto.class, testCustomer2);
        testProperty2.setCustomerId(createdCustomer2.getId());
        createdProperty2 = entityIsCreatedAs(PropertyDto.class, testProperty2);
        commercialSubscriptionIsCreated(createdCustomer2.getId(), createdProperty2.getId(), createdAppId);
        testPropertySet3.setCustomerId(createdCustomer2.getId());
        createdPropertySet3 = entityIsCreatedAs(PropertySetDto.class, testPropertySet3);
        testUser2.setUserCustomerRelationship(null);
        createdUser2 = entityIsCreatedAs(UserDto.class, testUser2);
        entityIsCreated(constructUserCustomerRelationshipDto(createdUser2.getId(), createdCustomer2.getId(), true, true));
        entityIsCreated(constructUserPropertyRelationshipDto(createdUser2.getId(), createdProperty2.getId(), true));
        testUserGroup3.setCustomerId(createdCustomer2.getId());
        createdUserGroup = entityIsCreatedAs(UserGroupDto.class, testUserGroup3);
    }

    // External App does not see internal attributes. Hence setting the internal attributes to null so they can be
    // compared with the returned entities
    private void setInternalAttrributesNull() {
        createdProperty2.setIsDemo(null);
        createdCustomer2.setSalesforceId(null);
        createdCustomer2.setIsDemo(null);
    }
}
