package travel.snapshot.dp.qa.junit.tests.identity.propertysets;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetType;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.junit.Assert.fail;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.USERS;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerContains;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

@RunWith(JUnitParamsRunner.class)
public class ParametersPropertySetTests extends CommonTest {

    private static final String EXAMPLES = "src/test/resources/csv/property_sets/";


    @FileParameters(EXAMPLES + "getPropertySets.csv")
    @Test
    @Category(Categories.SlowTests.class)
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
            commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        });

        // Get list of property sets
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, null, null, null, null);
        commonHelpers.getEntities(PROPERTY_SETS_PATH, params);
        numberOfEntitiesInResponse(PropertySetDto.class, Integer.parseInt(returned));
        headerIs("X-Total-Count", total);
        headerContains("Link", linkHeader);
    }

    @FileParameters(EXAMPLES + "incorrectPropertySets.csv")
    @Test
    public void createIncorrectPropertySets(String sourceFile,
                                String statusCode,
                                String customCode) throws Exception {
        commonHelpers.useFileForSendDataTo(sourceFile, "POST", PROPERTY_SETS_PATH, "identity");
        responseCodeIs(Integer.valueOf(statusCode));
        customCodeIs(Integer.valueOf(customCode));
    }

    @FileParameters(EXAMPLES + "sendPOSTRequestWithEmptyBodyToAllPropertySetEndpoints.csv")
    @Test
    public void sendPOSTRequestWithEmptyBodyToAllPropertySetEndpoints(String endpoint) {
        commonHelpers.sendBlankPost(PROPERTY_SETS_PATH, "identity");
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @FileParameters(EXAMPLES + "filteringListOfPropertySets.csv")
    @Test
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
            testPropertySet1.setName(names.get(n));
            testPropertySet1.setDescription(String.format("Some_desc_%d", n));
            testPropertySet1.setType(types.get(n));
            commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        });
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null);
        commonHelpers.getEntities(PROPERTY_SETS_PATH, params);
        numberOfEntitiesInResponse(PropertySetDto.class, Integer.valueOf(returned));
        headerIs("X-Total-Count", total);
    }

    // PropertySets - Properties

    @FileParameters(EXAMPLES + "filteringListOfPropertiesForPropertySet.csv")
    @Test
    public void filteringListOfPropertiesForPropertySet(
        String limit,
        String cursor,
        String returned,
        String filter
    ) throws Exception {
        UUID psId = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        UUID p1Id = commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty1);
        UUID p2Id = commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty2);
        UUID p3Id = commonHelpers.entityIsCreated(PROPERTIES_PATH, testProperty3);
        PropertySetPropertyRelationshipDto relation1 = relationshipsHelpers.constructPropertySetPropertyRelationship(psId, p1Id, true);
        PropertySetPropertyRelationshipDto relation2 = relationshipsHelpers.constructPropertySetPropertyRelationship(psId, p2Id, true);
        PropertySetPropertyRelationshipDto relation3 = relationshipsHelpers.constructPropertySetPropertyRelationship(psId, p3Id, false);
        commonHelpers.entityIsCreated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relation1);
        commonHelpers.entityIsCreated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relation2);
        commonHelpers.entityIsCreated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relation3);
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, null, null, null);
        commonHelpers.getEntities(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, params);
        numberOfEntitiesInResponse(PropertySetPropertyRelationshipDto.class, Integer.valueOf(returned));
    }

    // PropertySet - Users

    @FileParameters(EXAMPLES + "filteringListOfUsersForPropertySet.csv")
    @Test
    public void filteringListOfUsersForPropertySet(
        String limit,
        String cursor,
        String returned,
        String filter
    ) throws Exception {
        List<UserUpdateDto.UserType> types = Arrays.asList(
                UserUpdateDto.UserType.CUSTOMER,
                UserUpdateDto.UserType.CUSTOMER,
                UserUpdateDto.UserType.GUEST,
                UserUpdateDto.UserType.CUSTOMER,
                UserUpdateDto.UserType.PARTNER,
                UserUpdateDto.UserType.CUSTOMER
        );
        UUID psId = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        IntStream.range(0, 5).forEachOrdered(n -> {
            testUser1.setType(types.get(n));
            testUser1.setId(null);
            testUser1.setFirstName(String.format("FirstName%d", n));
            testUser1.setLastName(String.format("LastName%d", n));
            testUser1.setUsername(String.format("UserName%d", n));
            testUser1.setEmail(String.format("username%d@snapshot.travel", n));
            UUID userId = commonHelpers.entityIsCreated(USERS_PATH, testUser1);
            UserPropertySetRelationshipDto relation = relationshipsHelpers.constructUserPropertySetRelationshipDto(userId, psId, true);
            commonHelpers.entityIsCreated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relation);
        });
        // Disable one of the relationships
        Map<String, String> relationParams = buildQueryParamMapForPaging(null, null, String.format("property_set_id==%s", String.valueOf(psId)), null, null, null);
        UUID relationId = commonHelpers.getEntitiesAsType(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, relationParams).get(0).getId();
        UserPropertySetRelationshipUpdateDto update = new UserPropertySetRelationshipUpdateDto();
        update.setIsActive(false);
        commonHelpers.entityIsUpdated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, update);
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, null, null, null);
        commonHelpers.getEntities(USER_PROPERTY_SET_RELATIONSHIPS_PATH, params);
        numberOfEntitiesInResponse(UserPropertySetRelationshipDto.class, Integer.valueOf(returned));
    }
}
