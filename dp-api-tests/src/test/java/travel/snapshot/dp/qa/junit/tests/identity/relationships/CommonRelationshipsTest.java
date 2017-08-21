package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.ASSET_MANAGEMENT;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.MEMBERSHIP;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMap.endpointEntityMap;

import com.jayway.restassured.response.Response;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Common tests for all relationships endpoints
 */
@RunWith(JUnitParamsRunner.class)
public class CommonRelationshipsTest extends CommonTest {

    private static final String EXAMPLES = "src/test/resources/csv/relationships/";

    private static final List<String> ALL_RELATIONSHIPS_ENDPOINTS = Arrays.asList(
            CUSTOMER_PROPERTY_RELATIONSHIPS_PATH,
            PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH,
            USER_CUSTOMER_RELATIONSHIPS_PATH,
            USER_PARTNER_RELATIONSHIPS_PATH,
            USER_PROPERTY_RELATIONSHIPS_PATH,
            USER_PROPERTY_SET_RELATIONSHIPS_PATH,
            USER_GROUP_PROPERTY_RELATIONSHIPS_PATH,
            USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH,
            USER_GROUP_USER_RELATIONSHIPS_PATH
    );

    private LocalDate validFrom = LocalDate.now();
    private LocalDate validTo = LocalDate.now().plusYears(1).plusMonths(2).plusDays(3);
    private PropertySetPropertyRelationshipDto testPropertySetPropertyRelationship1;
    private PropertySetPropertyRelationshipDto testPropertySetPropertyRelationship2;
    private CustomerPropertyRelationshipDto testCustomerPropertyRelationship1;
    private CustomerPropertyRelationshipDto testCustomerPropertyRelationship2;
    private CustomerPropertyRelationshipDto testCustomerPropertyRelationship3;
    private UserPropertySetRelationshipDto testUserPropertySetRelationship1;
    private UserPropertySetRelationshipDto testUserPropertySetRelationship2;
    private UserCustomerRelationshipDto testUserCustomerRelationship1;
    private UserCustomerRelationshipDto testUserCustomerRelationship2;
    private UserCustomerRelationshipDto testUserCustomerRelationship3;
    private UserGroupPropertyRelationshipDto testUserGroupPropertyRelationship1;
    private UserGroupPropertyRelationshipDto testUserGroupPropertyRelationship2;
    private UserGroupUserRelationshipDto testUserGroupUserRelationship1;
    private UserPropertyRelationshipDto testUserPropertyRelationship1;
    private UserPropertyRelationshipDto testUserPropertyRelationship2;
    private UserPropertyRelationshipDto testUserPropertyRelationship3;
    private UserPropertyRelationshipDto testUserPropertyRelationship4;
    private UserPartnerRelationshipDto testUserPartnerRelationship;
    private UserGroupPropertySetRelationshipDto testUserGroupPropertySetRelationship;


    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    @Jira("DPIM-56")
    @FileParameters(EXAMPLES + "getParamsExamples.csv")
    public void getAllFilteringSorting(String limit, String cursor, String filter, String sort, String sortDesc, String returned) throws Exception {
        // Create relationships for test
        constructAndCreateTestRelationshipsDtos();
        ALL_RELATIONSHIPS_ENDPOINTS.forEach(endpoint -> {
            commonHelpers.getEntities(endpoint, buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null));
            responseCodeIs(SC_OK);
            try {
                numberOfEntitiesInResponse(endpointEntityMap.get(endpoint), Integer.valueOf(returned));
            } catch (IOException e) {
                throw new RuntimeException("Exception when trying to get number of entities in response: " + e.getMessage());
            }
        });
    }

    @Test
    @Jira("DPIM-56")
    @FileParameters(EXAMPLES + "invalidGetParamsExamples.csv")
    public void relationshipsFilteringTest(String limit, String cursor, String filter, String sort, String sortDesc) throws Exception {
        ALL_RELATIONSHIPS_ENDPOINTS.forEach(endpoint -> {
            Response response = commonHelpers.getEntities(endpoint, buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null));
            responseCodeIs(SC_BAD_REQUEST);
            customCodeIs(CC_BAD_PARAMS);
        });
    }


//    Help methods

    private void constructAndCreateTestRelationshipsDtos() throws Exception {
        //        Prepare data for tests - basic entities
        commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty2);
        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer2);
        commonHelpers.entityIsCreated(USERS_PATH, testUser1);
        commonHelpers.entityIsCreated(USERS_PATH, testUser2);
        commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        commonHelpers.entityIsCreated(USER_GROUPS_PATH, testUserGroup1);
        commonHelpers.entityIsCreated(PARTNERS_PATH, testPartner1);
        
//        Construct DTOs
        testCustomerPropertyRelationship1 = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                testCustomer1.getId(), testProperty1.getId(), true, ASSET_MANAGEMENT, validFrom, validTo);
        testCustomerPropertyRelationship2 = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                testCustomer1.getId(), testProperty2.getId(), true, CHAIN, validFrom, validTo);
        testCustomerPropertyRelationship3 = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                testCustomer2.getId(), testProperty2.getId(), true, MEMBERSHIP, validFrom, validTo);

        testUserPropertySetRelationship1 = relationshipsHelpers.constructUserPropertySetRelationshipDto(testUser1.getId(), testPropertySet1.getId(), true);
        testUserPropertySetRelationship2 = relationshipsHelpers.constructUserPropertySetRelationshipDto(testUser2.getId(), testPropertySet1.getId(), true);
        testPropertySetPropertyRelationship1 = relationshipsHelpers.constructPropertySetPropertyRelationship(
                testPropertySet1.getId(), testProperty1.getId(), true);
        testPropertySetPropertyRelationship2 = relationshipsHelpers.constructPropertySetPropertyRelationship(
                testPropertySet1.getId(), testProperty2.getId(), true);

        testUserCustomerRelationship1 = relationshipsHelpers.constructUserCustomerRelationshipDto(testUser1.getId(),
                testCustomer1.getId(), true, true);
        testUserCustomerRelationship2 = relationshipsHelpers.constructUserCustomerRelationshipDto(testUser1.getId(),
                testCustomer2.getId(), true, true);
        testUserCustomerRelationship3 = relationshipsHelpers.constructUserCustomerRelationshipDto(testUser2.getId(),
                testCustomer1.getId(), true, true);

        testUserGroupPropertySetRelationship = relationshipsHelpers.constructUserGroupPropertySetRelationship(testUserGroup1.getId(), testPropertySet1.getId(), true);

        testUserGroupPropertyRelationship1 = relationshipsHelpers.constructUserGroupPropertyRelationship(testUserGroup1.getId(), testProperty1.getId(), true);
        testUserGroupPropertyRelationship2 = relationshipsHelpers.constructUserGroupPropertyRelationship(testUserGroup1.getId(), testProperty2.getId(), true);
        testUserGroupUserRelationship1 = relationshipsHelpers.constructUserGroupUserRelationship(testUserGroup1.getId(), testUser1.getId(), true);

        testUserPropertyRelationship1 = relationshipsHelpers.constructUserPropertyRelationshipDto(testUser1.getId(), testProperty1.getId(), true);
        testUserPropertyRelationship2 = relationshipsHelpers.constructUserPropertyRelationshipDto(testUser1.getId(), testProperty2.getId(), true);
        testUserPropertyRelationship3 = relationshipsHelpers.constructUserPropertyRelationshipDto(testUser2.getId(), testProperty1.getId(), true);
        testUserPropertyRelationship4 = relationshipsHelpers.constructUserPropertyRelationshipDto(testUser2.getId(), testProperty2.getId(), true);

        testUserPartnerRelationship = relationshipsHelpers.constructUserPartnerRelationshipDto(testUser1.getId(), testPartner1.getId(), true);

        //        Relationships - create via api
        commonHelpers.entityIsCreated(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, testCustomerPropertyRelationship1);
        commonHelpers.entityIsCreated(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, testCustomerPropertyRelationship2);
        commonHelpers.entityIsCreated(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, testCustomerPropertyRelationship3);

        commonHelpers.entityIsCreated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, testPropertySetPropertyRelationship1);
        commonHelpers.entityIsCreated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, testPropertySetPropertyRelationship2);

        commonHelpers.entityIsCreated(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship1);
        commonHelpers.entityIsCreated(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship2);
        commonHelpers.entityIsCreated(USER_CUSTOMER_RELATIONSHIPS_PATH, testUserCustomerRelationship3);

        commonHelpers.entityIsCreated(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, testUserGroupPropertyRelationship1);
        commonHelpers.entityIsCreated(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, testUserGroupPropertyRelationship2);

        commonHelpers.entityIsCreated(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, testUserGroupPropertySetRelationship);

        commonHelpers.entityIsCreated(USER_GROUP_USER_RELATIONSHIPS_PATH, testUserGroupUserRelationship1);

        commonHelpers.entityIsCreated(USER_PARTNER_RELATIONSHIPS_PATH, testUserPartnerRelationship);

        commonHelpers.entityIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship1);
        commonHelpers.entityIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship2);
        commonHelpers.entityIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship3);
        commonHelpers.entityIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, testUserPropertyRelationship4);

        commonHelpers.entityIsCreated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship1);
        commonHelpers.entityIsCreated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship2);
    }
}
