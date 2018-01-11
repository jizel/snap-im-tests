package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.Map;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.api.identity.model.UserType.CUSTOMER;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

public class UserAccessChecksByUserTests extends CommonAccessCheckByUserTest {

    UUID customerId;
    UUID requestorId1;
    UUID requestorId2;
    UUID targetUserId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        customerId = entityIsCreated(testCustomer1);
        requestorId1 = entityIsCreated(testUser1);
        testUser2.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customerId, true, true));
        testUser3.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customerId, true, true));
        targetUserId = entityIsCreated(testUser2);
        requestorId2 = entityIsCreated(testUser3);
    }

    @Test
    void userGetByUser() {
        getEntityFails(requestorId1, USERS_PATH, targetUserId);
        getEntitySucceeds(requestorId2, USERS_PATH, targetUserId);
        assertThat(getEntitiesAsTypeByUserForApp(requestorId1, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, USERS_PATH, UserDto.class, emptyQueryParams())).hasSize(1);
        assertThat(getEntitiesAsTypeByUserForApp(requestorId2, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, USERS_PATH, UserDto.class, emptyQueryParams())).hasSize(2);
    }

    @Test
    void userCRUDbyUser() {
        testPartnerUser1.setType(CUSTOMER);
        testPartnerUser1.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customerId, true, true));
        createEntityFails(requestorId1, testPartnerUser1);
        UUID createdUserId = createEntitySucceeds(requestorId2, testPartnerUser1).as(UserDto.class).getId();
        UserUpdateDto update = new UserDto();
        update.setEmail("new.email@altavista.com");
        updateEntityFails(requestorId1, USERS_PATH, createdUserId, update);
        updateEntitySucceeds(requestorId2, USERS_PATH, createdUserId, update);
        deleteEntityFails(requestorId1, USERS_PATH, createdUserId, SC_NOT_FOUND);
        deleteEntityFails(requestorId2, USERS_PATH, createdUserId, SC_CONFLICT);
    }

    @Test
    void userPropertyCRUD() {
        UUID relationId1 = entityIsCreated(constructUserPropertyRelationshipDto(targetUserId, DEFAULT_PROPERTY_ID, true));
        UUID relationId2 = entityIsCreated(constructUserPropertyRelationshipDto(requestorId2, DEFAULT_PROPERTY_ID, false));
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId1, DEFAULT_PROPERTY_ID, true));
        getEntityFails(requestorId1, USERS_PATH, targetUserId);
        getEntityFails(requestorId1, USER_PROPERTY_RELATIONSHIPS_PATH, relationId1);
        getEntityFails(requestorId2, USER_PROPERTY_RELATIONSHIPS_PATH, relationId1);
        updateEntityFails(requestorId2, USER_PROPERTY_RELATIONSHIPS_PATH, relationId1, INACTIVATE_RELATION);
        deleteEntityFails(requestorId2, USER_PROPERTY_RELATIONSHIPS_PATH, relationId1, SC_NOT_FOUND);
        entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH ,relationId2, ACTIVATE_RELATION);
        getEntitySucceeds(requestorId2, USER_PROPERTY_RELATIONSHIPS_PATH, relationId1);
        updateEntitySucceeds(requestorId2, USER_PROPERTY_RELATIONSHIPS_PATH, relationId1, INACTIVATE_RELATION);
        deleteEntitySucceeds(requestorId2, USER_PROPERTY_RELATIONSHIPS_PATH, relationId1);
    }

    @Test
    void userPropertySetCRUD() {
        UUID psId = entityIsCreated(testPropertySet1);
        UUID relationId1 = entityIsCreated(constructUserPropertySetRelationshipDto(targetUserId, psId, true));
        UUID relationId2 = entityIsCreated(constructUserPropertySetRelationshipDto(requestorId2, psId, false));
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId1, psId, true));
        getEntityFails(requestorId1, USERS_PATH, targetUserId);
        getEntityFails(requestorId1, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1);
        getEntityFails(requestorId2, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1);
        updateEntityFails(requestorId2, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1, INACTIVATE_RELATION);
        deleteEntityFails(requestorId2, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1, SC_NOT_FOUND);
        entityIsUpdated(USER_PROPERTY_SET_RELATIONSHIPS_PATH ,relationId2, ACTIVATE_RELATION);
        getEntitySucceeds(requestorId2, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1);
        updateEntitySucceeds(requestorId2, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1, INACTIVATE_RELATION);
        deleteEntitySucceeds(requestorId2, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1);
    }

    @Test
    void userCustomerRUD() {
        Map<String, String> params = QueryParams.builder().filter(String.format("user_id==%s", targetUserId)).build();
        UUID relationId = getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, params).get(0).getId();
        getEntityFails(requestorId1, USER_CUSTOMER_RELATIONSHIPS_PATH, relationId);
        updateEntityFails(requestorId1, USER_CUSTOMER_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        updateEntitySucceeds(requestorId2, USER_CUSTOMER_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        deleteEntityFails(requestorId1, USER_CUSTOMER_RELATIONSHIPS_PATH, relationId, SC_NOT_FOUND);
        deleteEntityFails(requestorId2, USER_CUSTOMER_RELATIONSHIPS_PATH, relationId, SC_NOT_FOUND);
    }
}
