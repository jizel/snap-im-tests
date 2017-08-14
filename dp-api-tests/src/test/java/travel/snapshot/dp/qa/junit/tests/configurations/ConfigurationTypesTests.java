package travel.snapshot.dp.qa.junit.tests.configurations;

import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadYamlTables;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.selectExamplesForTestFromTable;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.configuration.model.ConfigurationTypeDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Configurations types CRUD tests. Replacing Cucumber configurations-types.feature.
 */
@RunWith(SerenityRunner.class)
public class ConfigurationTypesTests extends CommonTest {

    private static Map<String, Map<String, List<String>>> configurationTypesTestsTables = loadYamlTables(String.format(YAML_DATA_PATH, "configurations/configuration_types.yaml"));
    private ConfigurationTypeDto testConfigurationType1;
    private static final String CONF1_IDENTIFIER = "conf_id_1";
    private static final String CONF1_DESCRIPTION = "Notification Test Configuration Type Description";


    @Before
    public void setUp() throws Throwable {
        testConfigurationType1 = new ConfigurationTypeDto();
        testConfigurationType1.setIdentifier(CONF1_IDENTIFIER);
        testConfigurationType1.setDescription(CONF1_DESCRIPTION);
    }

    @Test
    @Category(Categories.SmokeTests.class)
    public void createAndGetConfigurationTypeTest() {
        configurationHelpers.followingConfigurationTypeIsCreated(testConfigurationType1);
        responseCodeIs(SC_CREATED);
        configurationHelpers.bodyContainsConfigurationType(CONF1_IDENTIFIER, CONF1_DESCRIPTION);
        configurationHelpers.compareHeaderWithIdentifier("Location", CONF1_IDENTIFIER);
        configurationHelpers.getConfigurationTypeWithId(CONF1_IDENTIFIER);
        responseCodeIs(SC_OK);
        configurationHelpers.bodyContainsConfigurationType(CONF1_IDENTIFIER, CONF1_DESCRIPTION);
    }

    @Test
    @Category(Categories.SmokeTests.class)
    public void updateConfigurationTypeTest() {
        configurationHelpers.followingConfigurationTypeIsCreated(testConfigurationType1);
        String updatedDescription = "Updated description";
        configurationHelpers.updateConfigurationTypeDescription(CONF1_IDENTIFIER, updatedDescription);
        responseCodeIs(SC_NO_CONTENT);
        configurationHelpers.configurationTypeHasDescription(CONF1_IDENTIFIER, updatedDescription);
    }

    @Test
    @Category(Categories.SmokeTests.class)
    public void deleteConfigurationTypeTest() {
        configurationHelpers.followingConfigurationTypeIsCreated(testConfigurationType1);
        configurationHelpers.tryDeleteConfigurationType(CONF1_IDENTIFIER);
        responseCodeIs(SC_NO_CONTENT);
        configurationHelpers.configurationTypeDoesntExist(CONF1_IDENTIFIER);
    }

    //    DP-2219
    @Test
    @Ignore
    public void filteringConfigurationTypesTest() throws Exception {
        List<Map<String, String>> listOfConfigurationTypes = selectExamplesForTestFromTable(configurationTypesTestsTables, "configurationTypesForFiltering");
        List<Map<String, String>> listOfParams = selectExamplesForTestFromTable(configurationTypesTestsTables, "configurationTypesFilteringData");
//        Create data
        listOfConfigurationTypes.forEach(ct -> {
            ConfigurationTypeDto configurationType = new ConfigurationTypeDto();
            configurationType.setIdentifier(ct.get("identifier"));
            configurationType.setDescription(ct.get("description"));
            configurationHelpers.followingConfigurationTypeIsCreated(configurationType);
        });
//        Get data
        for(Map<String, String> params : listOfParams){
            configurationHelpers.listOfConfigurationTypesisGot(params.get("limit"), params.get("cursor"),
                    params.get("filter"), params.get("sort"), params.get("sortDesc"));
            responseCodeIs(SC_OK);
            numberOfEntitiesInResponse(ConfigurationTypeDto.class, Integer.valueOf(params.get("returned")));
            List<String> keys = Arrays.asList(params.get("expected_keys").split("\\s*,\\s*"));
            configurationHelpers.typesAreInResponseInOrder(keys);
        }
    }

    @Test
    public void createConfigurationTypeErrorCodeTest() {
        ConfigurationTypeDto invalidConfigurationType = new ConfigurationTypeDto();
        invalidConfigurationType.setDescription("identifier is missing");
        configurationHelpers.followingConfigurationTypeIsCreated(invalidConfigurationType);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(SEMANTIC_ERRORS_CUSTOM_CODE);
//        Empty identifier
        invalidConfigurationType.setIdentifier("");
        configurationHelpers.followingConfigurationTypeIsCreated(invalidConfigurationType);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(SEMANTIC_ERRORS_CUSTOM_CODE);
//        Duplicate identifier
        configurationHelpers.followingConfigurationTypeIsCreated(testConfigurationType1);
        invalidConfigurationType.setIdentifier(CONF1_IDENTIFIER);
        configurationHelpers.createConfigurationType(invalidConfigurationType);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CONFLICT_CUSTOM_CODE);
    }

    @Test
    public void getNonExistingConfType() throws Exception {
        configurationHelpers.getConfigurationTypeWithId("Nonexisting identifier");
        responseCodeIs(SC_NOT_FOUND);
    }

    //    DP-1773
    @Test
    @Ignore
    public void deleteNonExistentConfigurationTypeTest() {
        configurationHelpers.tryDeleteConfigurationType(NON_EXISTENT_ID.toString());
        responseCodeIs(SC_NOT_FOUND);
    }

    @Test
    public void configurationTypesGetErrorCodes() {
        List<Map<String, String>> listOfparams = selectExamplesForTestFromTable(configurationTypesTestsTables, "configurationTypesGetErrorCodes");
        listOfparams.forEach(params -> {
                configurationHelpers.listOfConfigurationTypesisGot(transformNull(params.get("limit")), transformNull(params.get("cursor")),
                        transformNull(params.get("filter")), transformNull(params.get("sort")), transformNull(params.get("sort_desc")));
            verifyResponseAndCustomCode(params.get("response_code"), params.get("custom_code"));
        });
    }

}
