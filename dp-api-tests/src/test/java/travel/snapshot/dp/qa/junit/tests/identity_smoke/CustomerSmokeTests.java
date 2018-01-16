package travel.snapshot.dp.qa.junit.tests.identity_smoke;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.DATA_OWNER;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.tests.Tags.AUTHORIZATION_TEST;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;
import travel.snapshot.dp.qa.junit.utils.NonNullMapDecorator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

@Tag(AUTHORIZATION_TEST)
public class CustomerSmokeTests extends CommonSmokeTest {

    private NonNullMapDecorator<String, CustomerCreateDto> customerDtos = null;

    @Before
    public void setUp() {
        super.setUp();
        customerDtos = entitiesLoader.getCustomerDtos();
    }

    @Test
    public void customerCRUDWithAuthorization() throws Throwable {
        ArrayList<UUID> customerIds = new ArrayList<UUID>();
        //create
        customerDtos.values().forEach(customer -> {
            customerIds.add(authorizationHelpers.entityIsCreated(customer));
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
        //request
        authorizationHelpers.getEntity(CUSTOMERS_PATH, customerIds.get(0));
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("customer_code");
        bodyContainsEntityWith("name", "Creation test company1");
        bodyContainsEntityWith("email", "s1@tenants.biz");
        //update
        authorizationHelpers.entityIsUpdated(CUSTOMERS_PATH, customerIds.get(0), INACTIVATE_RELATION);
        //delete
        authorizationHelpers.entityIsDeleted(CUSTOMERS_PATH, customerIds.get(0));
    }

    @Test
    public void customerPropertyCRUD() {
        // create
        CustomerPropertyRelationshipCreateDto relation = constructCustomerPropertyRelationshipDto(
                DEFAULT_SNAPSHOT_CUSTOMER_ID,
                DEFAULT_PROPERTY_ID,
                true,
                CHAIN,
                LocalDate.parse("2015-01-01"),
                LocalDate.parse("2020-01-01"));
        UUID relationId = authorizationHelpers.entityIsCreated(relation);

        // get
        authorizationHelpers.getEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("property_id", DEFAULT_PROPERTY_ID.toString());
        bodyContainsEntityWith("customer_id", DEFAULT_SNAPSHOT_CUSTOMER_ID.toString());
        bodyContainsEntityWith("valid_from", "2015-01-01");

        // update
        CustomerPropertyRelationshipUpdateDto update = new CustomerPropertyRelationshipUpdateDto();
        update.setType(DATA_OWNER);
        authorizationHelpers.entityIsUpdated(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId, update);
        // delete
        authorizationHelpers.entityIsDeleted(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
    }

/*
    Please uncomment the following test once multi-tenance is enabled for user.
    @Test
    public void customerUserCRUD() throws Throwable {
        // create a user
        UUID userId = userHelpers.userIsCreatedWithAuth(testUser1);
        // list relations
        Map<String, String> queryParams = buildQueryParamMapForPaging(
                null,
                null,
                String.format("user_id==%s", userId.toString()),
                null,
                null,
                null);
        UUID relationId = authorizationHelpers.getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, queryParams)
                .get(0).getId();
        // get relation
        authorizationHelpers.getEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId);
        responseCodeIs(SC_OK);
        bodyContainsEntityWith("user_id", userId.toString());
        bodyContainsEntityWith("customer_id", DEFAULT_SNAPSHOT_CUSTOMER_ID.toString());
        bodyContainsEntityWith("is_active", "true");
        bodyContainsEntityWith("is_primary", "false");
        // update
        authorizationHelpers.entityIsUpdated(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        // make sure changes applied
        authorizationHelpers.getEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("is_active", "false");
        // delete relation
        authorizationHelpers.entityIsDeleted(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId);
    }
*/
}
