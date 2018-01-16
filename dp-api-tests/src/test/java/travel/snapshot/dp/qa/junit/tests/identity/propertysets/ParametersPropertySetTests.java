package travel.snapshot.dp.qa.junit.tests.identity.propertysets;

import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.headerContains;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.sendBlankPost;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;
import static travel.snapshot.dp.qa.junit.tests.Tags.SLOW_TEST;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetType;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserType;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;


public class ParametersPropertySetTests extends CommonTest {

    private static final String EXAMPLES = "/csv/property_sets/";


    @Tag(SLOW_TEST)
    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "getPropertySets.csv")
    public void getPropertySets(
        String limit,
        String cursor,
        String returned,
        String total,
        String linkHeader
    ) throws Exception {

        // Create property sets
        IntStream.range(0, 52).forEachOrdered(n -> {
            testPropertySet1.setName(String.format("ps_name_%d", n));
            testPropertySet1.setId(null);
            entityIsCreated(testPropertySet1);
        });

        // Get list of property sets
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, null, null, null, null);
        getEntities(PROPERTY_SETS_PATH, params);
        numberOfEntitiesInResponse(PropertySetDto.class, Integer.parseInt(returned));
        headerIs(TOTAL_COUNT_HEADER, total);
        headerContains("Link", linkHeader);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "incorrectPropertySets.csv")
    public void createIncorrectPropertySets(String sourceFile,
                                String statusCode,
                                String customCode) throws Exception {
        commonHelpers.useFileForSendDataTo(sourceFile, "POST", PROPERTY_SETS_PATH, "identity");
        responseCodeIs(Integer.valueOf(statusCode));
        customCodeIs(Integer.valueOf(customCode));
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "sendPOSTRequestWithEmptyBodyToAllPropertySetEndpoints.csv")
    public void sendPOSTRequestWithEmptyBodyToAllPropertySetEndpoints(String endpoint) {
        sendBlankPost(PROPERTY_SETS_PATH, "identity");
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "filteringListOfPropertySets.csv")
    public void filteringListOfPropertySets(
        String limit,
        String cursor,
        String returned,
        String total,
        String filter,
        String sort,
        String sortDesc
    ) throws Exception {
        List<String> names = Arrays.asList(
            "list_ps1_name",
            "list_ps2_name",
            "list_ps3_name",
            "list_ps4_name",
            "list_ps5_name",
            "second_list_ps6_name",
            "second_list_ps7_name",
            "second_list_ps8_name"
        );

        List<PropertySetType> types = Arrays.asList(
                PropertySetType.BRAND,
                PropertySetType.BRAND,
                PropertySetType.BRAND,
                PropertySetType.GEOLOCATION,
                PropertySetType.GEOLOCATION,
                PropertySetType.GEOLOCATION,
                PropertySetType.BRAND,
                PropertySetType.BRAND
        );
        IntStream.range(0, 7).forEachOrdered(n -> {
            testPropertySet1.setId(null);
            testPropertySet1.setName(names.get(n));
            testPropertySet1.setDescription(String.format("Some_desc_%d", n));
            testPropertySet1.setType(types.get(n));
            entityIsCreated(testPropertySet1);
        });
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null);
        getEntities(PROPERTY_SETS_PATH, params);
        numberOfEntitiesInResponse(PropertySetDto.class, Integer.valueOf(returned));
        headerIs(TOTAL_COUNT_HEADER, total);
    }

    // PropertySets - Properties

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "filteringListOfPropertiesForPropertySet.csv")
    public void filteringListOfPropertiesForPropertySet(
        String limit,
        String cursor,
        String returned,
        String filter
    ) throws Exception {
        UUID psId = entityIsCreated(testPropertySet1);
        UUID p1Id = entityIsCreated(testProperty1);
        UUID p2Id = entityIsCreated(testProperty2);
        UUID p3Id = entityIsCreated(testProperty3);
        PropertySetPropertyRelationshipCreateDto relation1 = constructPropertySetPropertyRelationship(psId, p1Id, true);
        PropertySetPropertyRelationshipCreateDto relation2 = constructPropertySetPropertyRelationship(psId, p2Id, true);
        PropertySetPropertyRelationshipCreateDto relation3 = constructPropertySetPropertyRelationship(psId, p3Id, false);
        entityIsCreated(relation1);
        entityIsCreated(relation2);
        entityIsCreated(relation3);
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, null, null, null);
        getEntities(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, params);
        numberOfEntitiesInResponse(PropertySetPropertyRelationshipDto.class, Integer.valueOf(returned));
    }

    // PropertySet - Users

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "filteringListOfUsersForPropertySet.csv")
    public void filteringListOfUsersForPropertySet(
        String limit,
        String cursor,
        String returned,
        String filter
    ) throws Exception {
        List<UserType> types = Arrays.asList(
                UserType.CUSTOMER,
                UserType.CUSTOMER,
                UserType.GUEST,
                UserType.CUSTOMER,
                UserType.PARTNER,
                UserType.CUSTOMER
        );
        UUID psId = entityIsCreated(testPropertySet1);
        IntStream.range(0, 5).forEachOrdered(n -> {
            testUser1.setType(types.get(n));
            testUser1.setId(null);
            testUser1.setFirstName(String.format("FirstName%d", n));
            testUser1.setLastName(String.format("LastName%d", n));
            testUser1.setUsername(String.format("UserName%d", n));
            testUser1.setEmail(String.format("username%d@snapshot.travel", n));
            UUID userId = entityIsCreated(testUser1);
            UserPropertySetRelationshipCreateDto relation = constructUserPropertySetRelationshipDto(userId, psId, true);
            entityIsCreated(relation);
        });
        // Disable one of the relationships
        Map<String, String> relationParams = QueryParams.builder()
                .filter(String.format("property_set_id==%s", String.valueOf(psId)))
                .build();
        UUID relationId = getEntitiesAsType(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, relationParams).get(0).getId();
        entityIsUpdated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, null, null, null);
        getEntities(USER_PROPERTY_SET_RELATIONSHIPS_PATH, params);
        numberOfEntitiesInResponse(UserPropertySetRelationshipDto.class, Integer.valueOf(returned));
    }
}
