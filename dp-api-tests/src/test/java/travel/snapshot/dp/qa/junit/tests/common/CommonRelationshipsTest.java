package travel.snapshot.dp.qa.junit.tests.common;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.ASSET_MANAGEMENT;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.MEMBERSHIP;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertySetRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPartnerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMapping.endpointDtoMap;

import com.jayway.restassured.response.Response;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipCreateDto;

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

    protected LocalDate validFrom = LocalDate.now();
    protected LocalDate validTo = LocalDate.now().plusYears(1).plusMonths(2).plusDays(3);
    private PropertySetPropertyRelationshipCreateDto testPropertySetPropertyRelationship1;
    private PropertySetPropertyRelationshipCreateDto testPropertySetPropertyRelationship2;
    private CustomerPropertyRelationshipCreateDto testCustomerPropertyRelationship1;
    private CustomerPropertyRelationshipCreateDto testCustomerPropertyRelationship2;
    private CustomerPropertyRelationshipCreateDto testCustomerPropertyRelationship3;
    private UserPropertySetRelationshipCreateDto testUserPropertySetRelationship1;
    private UserPropertySetRelationshipCreateDto testUserPropertySetRelationship2;
    private UserCustomerRelationshipCreateDto testUserCustomerRelationship1;
    private UserCustomerRelationshipCreateDto testUserCustomerRelationship2;
    private UserCustomerRelationshipCreateDto testUserCustomerRelationship3;
    private UserGroupPropertyRelationshipCreateDto testUserGroupPropertyRelationship1;
    private UserGroupPropertyRelationshipCreateDto testUserGroupPropertyRelationship2;
    private UserGroupUserRelationshipCreateDto testUserGroupUserRelationship1;
    private UserPropertyRelationshipCreateDto testUserPropertyRelationship1;
    private UserPropertyRelationshipCreateDto testUserPropertyRelationship2;
    private UserPropertyRelationshipCreateDto testUserPropertyRelationship3;
    private UserPropertyRelationshipCreateDto testUserPropertyRelationship4;
    private UserPartnerRelationshipCreateDto testUserPartnerRelationship;
    private UserGroupPropertySetRelationshipCreateDto testUserGroupPropertySetRelationship;


    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    @Jira("DPIM-56")
    @FileParameters(EXAMPLES + "getParamsExamples.csv")
    public void getAllFilteringSorting(String limit, String cursor, String filter, String sort, String sortDesc, String returned) throws Exception {
        // Create relationships for test
        constructAndCreateTestRelationshipsDtos();
        ALL_RELATIONSHIPS_ENDPOINTS.forEach(endpoint -> {
            getEntities(endpoint, buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null));
            responseCodeIs(SC_OK);
            numberOfEntitiesInResponse(endpointDtoMap.get(endpoint), Integer.valueOf(returned));
        });
    }

    @Test
    @Jira("DPIM-56")
    @FileParameters(EXAMPLES + "invalidGetParamsExamples.csv")
    public void relationshipsFilteringTest(String limit, String cursor, String filter, String sort, String sortDesc) throws Exception {
        ALL_RELATIONSHIPS_ENDPOINTS.forEach(endpoint -> {
            Response response = getEntities(endpoint, buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null));
            responseCodeIs(SC_BAD_REQUEST);
            customCodeIs(CC_BAD_PARAMS);
        });
    }


//    Help methods

    private void constructAndCreateTestRelationshipsDtos() throws Exception {
        //        Prepare data for tests - basic entities
        entityIsCreated(testProperty1);
        entityIsCreated(testProperty2);
        entityIsCreated(testCustomer1);
        entityIsCreated(testCustomer2);
        entityIsCreated(testUser1);
        entityIsCreated(testUser2);
        entityIsCreated(testPropertySet1);
        entityIsCreated(testUserGroup1);
        entityIsCreated(testPartner1);
        
//        Construct DTOs
        testCustomerPropertyRelationship1 = constructCustomerPropertyRelationshipDto(
                testCustomer1.getId(), testProperty1.getId(), true, ASSET_MANAGEMENT, validFrom, validTo);
        testCustomerPropertyRelationship2 = constructCustomerPropertyRelationshipDto(
                testCustomer1.getId(), testProperty2.getId(), true, CHAIN, validFrom, validTo);
        testCustomerPropertyRelationship3 = constructCustomerPropertyRelationshipDto(
                testCustomer2.getId(), testProperty2.getId(), true, MEMBERSHIP, validFrom, validTo);

        testUserPropertySetRelationship1 = constructUserPropertySetRelationshipDto(testUser1.getId(), testPropertySet1.getId(), true);
        testUserPropertySetRelationship2 = constructUserPropertySetRelationshipDto(testUser2.getId(), testPropertySet1.getId(), true);
        testPropertySetPropertyRelationship1 = constructPropertySetPropertyRelationship(
                testPropertySet1.getId(), testProperty1.getId(), true);
        testPropertySetPropertyRelationship2 = constructPropertySetPropertyRelationship(
                testPropertySet1.getId(), testProperty2.getId(), true);

        testUserCustomerRelationship1 = constructUserCustomerRelationshipDto(testUser1.getId(),
                testCustomer1.getId(), true, true);
        testUserCustomerRelationship2 = constructUserCustomerRelationshipDto(testUser1.getId(),
                testCustomer2.getId(), true, true);
        testUserCustomerRelationship3 = constructUserCustomerRelationshipDto(testUser2.getId(),
                testCustomer1.getId(), true, true);

        testUserGroupPropertySetRelationship = constructUserGroupPropertySetRelationship(testUserGroup1.getId(), testPropertySet1.getId(), true);

        testUserGroupPropertyRelationship1 = constructUserGroupPropertyRelationship(testUserGroup1.getId(), testProperty1.getId(), true);
        testUserGroupPropertyRelationship2 = constructUserGroupPropertyRelationship(testUserGroup1.getId(), testProperty2.getId(), true);
        testUserGroupUserRelationship1 = constructUserGroupUserRelationship(testUserGroup1.getId(), testUser1.getId(), true);

        testUserPropertyRelationship1 = constructUserPropertyRelationshipDto(testUser1.getId(), testProperty1.getId(), true);
        testUserPropertyRelationship2 = constructUserPropertyRelationshipDto(testUser1.getId(), testProperty2.getId(), true);
        testUserPropertyRelationship3 = constructUserPropertyRelationshipDto(testUser2.getId(), testProperty1.getId(), true);
        testUserPropertyRelationship4 = constructUserPropertyRelationshipDto(testUser2.getId(), testProperty2.getId(), true);

        testUserPartnerRelationship = constructUserPartnerRelationshipDto(testUser1.getId(), testPartner1.getId(), true);

        //        Relationships - create via api
        entityIsCreated(testCustomerPropertyRelationship1);
        entityIsCreated(testCustomerPropertyRelationship2);
        entityIsCreated(testCustomerPropertyRelationship3);

        entityIsCreated(testPropertySetPropertyRelationship1);
        entityIsCreated(testPropertySetPropertyRelationship2);

        entityIsCreated(testUserCustomerRelationship1);
        entityIsCreated(testUserCustomerRelationship2);
        entityIsCreated(testUserCustomerRelationship3);

        entityIsCreated(testUserGroupPropertyRelationship1);
        entityIsCreated(testUserGroupPropertyRelationship2);

        entityIsCreated(testUserGroupPropertySetRelationship);

        entityIsCreated(testUserGroupUserRelationship1);

        entityIsCreated(testUserPartnerRelationship);

        entityIsCreated(testUserPropertyRelationship1);
        entityIsCreated(testUserPropertyRelationship2);
        entityIsCreated(testUserPropertyRelationship3);
        entityIsCreated(testUserPropertyRelationship4);

        entityIsCreated(testUserPropertySetRelationship1);
        entityIsCreated(testUserPropertySetRelationship2);
    }
}
