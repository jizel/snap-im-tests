package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_DETAILS;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPartnerRelationshipDto;
import static travel.snapshot.dp.qa.junit.utils.DpEndpoints.READ_WRITE_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMapping.endpointDtoMap;

public class PartnerAccessChecksTests extends CommonTest {
    UUID createdCustomerUserId;
    UUID createdPartnerUserId;
    UUID createdPartnerId;
    UUID relationId;
    Map<String, UUID> inaccessibles = new HashMap<>();


    @BeforeEach
    public void setUp() {
        super.setUp();
        createdPartnerId = entityIsCreated(testPartner1);
        createdCustomerUserId = entityIsCreated(testUser1);
        createdPartnerUserId = entityIsCreated(testPartnerUser1);
        relationId = entityIsCreated(constructUserPartnerRelationshipDto(createdPartnerUserId, DEFAULT_SNAPSHOT_PARTNER_ID, true));
        inaccessibles.put(PARTNERS_PATH, createdPartnerId);
        inaccessibles.put(CUSTOMERS_PATH, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        inaccessibles.put(PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        inaccessibles.put(USERS_PATH, createdCustomerUserId);
    }

    @Test
    void partnerAccessCheckTest() {
        assertThat(getPartnersBy(createdCustomerUserId)).hasSize(1);
        assertThat(getPartnersBy(createdPartnerUserId)).hasSize(1);
        entityIsCreated(constructUserPartnerRelationshipDto(createdPartnerUserId, createdPartnerId, true));
        assertThat(getPartnersBy(createdPartnerUserId)).hasSize(2);
        getEntityByUserForApplication(createdCustomerUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, USER_PARTNER_RELATIONSHIPS_PATH, relationId)
                .then()
                .statusCode(SC_NOT_FOUND)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
        getEntityByUserForApplication(createdCustomerUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, USERS_PATH, createdPartnerUserId)
                .then()
                .statusCode(SC_NOT_FOUND)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
    }

    @Test
    void partnerUserWithOutRelationDoesNotSeeAnything() {
        entityIsDeleted(USER_PARTNER_RELATIONSHIPS_PATH, relationId);
        getEntityByUserForApplication(createdPartnerUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTIES_PATH, DEFAULT_PROPERTY_ID)
                .then().statusCode(SC_FORBIDDEN)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_INSUFFICIENT_PERMISSIONS));
    }

    private List<PartnerDto> getPartnersBy(UUID userId) {
        return getEntitiesAsTypeByUserForApp(
                userId,
                DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                PARTNERS_PATH,
                PartnerDto.class,
                emptyQueryParams());
    }

}
