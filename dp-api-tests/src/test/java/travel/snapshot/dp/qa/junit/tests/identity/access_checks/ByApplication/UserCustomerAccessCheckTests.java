package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import org.junit.jupiter.api.BeforeEach;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.qa.junit.helpers.CommonHelpers;
import travel.snapshot.dp.qa.junit.tests.common.CommonAccessChecksByApplicationTest;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.Map;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

public class UserCustomerAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        UUID customerId = entityIsCreated(testCustomer1);
        testUser2.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customerId, true, true));
        UUID user1Id = entityIsCreated(testUser2);
        entityIsCreated(constructUserPropertyRelationshipDto(user1Id, propertyId2, true));
        entityIsCreated(constructCustomerPropertyRelationshipDto(customerId, propertyId2, true, CustomerPropertyRelationshipType.CHAIN, validFrom, validTo));
        Map<String, String> user1Params = QueryParams.builder()
                .filter(String.format("user_id==%s", String.valueOf(userId)))
                .build();
        Map<String, String> user2Params = QueryParams.builder()
                .filter(String.format("user_id==%s", String.valueOf(user1Id)))
                .build();
        accessibleEntityId = CommonHelpers.getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, user1Params).get(0).getId();
        inaccessibleEntityId = CommonHelpers.getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, user2Params).get(0).getId();
        PATH = USER_CUSTOMER_RELATIONSHIPS_PATH;
        PATTERN = null;
        FIELD_NAME = null;
        EXPECTED_CODE = SC_NO_CONTENT;
        update.put("is_primary", false);
    }
}
