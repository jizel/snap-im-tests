package travel.snapshot.dp.qa.junit.tests.configurations;

import static java.lang.Boolean.TRUE;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static travel.snapshot.dp.api.configuration.model.ValueType.STRING;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.sendBlankPost;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadYamlTables;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.selectExamplesForTestFromTable;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.configuration.model.ConfigurationRecordDto;
import travel.snapshot.dp.api.configuration.model.ConfigurationTypeDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Configurations CRUD tests. Replacing Cucumber configurations.feature.
 */
@RunWith(SerenityRunner.class)
public class ConfigurationsTests extends CommonTest {

    private static Map<String, Map<String, List<String>>> configurationsTestsTables = loadYamlTables(String.format(YAML_DATA_PATH, "configurations/configuration_crud.yaml"));
    private ConfigurationRecordDto testConfigurationRecord1;
    private static final String CONF_TYPE_IDENTIFIER = "NotificationTestConfType";

    @Before
    public void setUp() {
        ConfigurationTypeDto testConfigurationType1 = new ConfigurationTypeDto();
        testConfigurationType1.setIdentifier(CONF_TYPE_IDENTIFIER);
        testConfigurationType1.setDescription("Notification Test Configuration Type Description");
        configurationSteps.followingConfigurationTypeIsCreated(testConfigurationType1);
        testConfigurationRecord1 = new ConfigurationRecordDto();
        testConfigurationRecord1.setKey("notificationKey1");
        testConfigurationRecord1.setType(STRING);
        testConfigurationRecord1.setValue("String value");
    }

    @Test
    @Category(Categories.SmokeTests.class)
    public void createGetConfigurationsKeyValueTest() throws IOException {
        List<Map<String, String>> listOfExamples = selectExamplesForTestFromTable(configurationsTestsTables, "createConfigurationsKeyValueTest");
        for (Map<String, String> configurationMap : listOfExamples) {
            ConfigurationRecordDto confRecord = configurationHelpers.constructConfigurationRecord(configurationMap);
            String key = configurationMap.get("key");
            String value = configurationMap.get("value");
            String type = configurationMap.get("type").toUpperCase();
            configurationHelpers.followingConfigurationIsCreated(confRecord, CONF_TYPE_IDENTIFIER);
            responseCodeIs(SC_CREATED);
            configurationHelpers.bodyContainsConfiguration(key, value, type);
            configurationHelpers.compareHeaderWithKey("location", key);
//            GET
            configurationHelpers.getConfigurationWithKeyForIdentifier(key, CONF_TYPE_IDENTIFIER);
            responseCodeIs(SC_OK);
            contentTypeIs("application/json");
            configurationHelpers.bodyContainsConfiguration(key, value, type);
        }
    }

    @Test
    @Jira("DPNP-176")
    public void deleteConfigurationTest() {
        ConfigurationRecordDto configurationRecord = configurationHelpers.configurationIsCreated(CONF_TYPE_IDENTIFIER, testConfigurationRecord1);
        configurationHelpers.tryDeleteConfiguration(configurationRecord.getKey(), CONF_TYPE_IDENTIFIER);
        responseCodeIs(SC_NO_CONTENT);
        bodyIsEmpty();
        configurationHelpers.configurationDoesntExistForIdentifier(configurationRecord.getKey(), CONF_TYPE_IDENTIFIER);
        configurationHelpers.tryDeleteConfiguration(configurationRecord.getKey(), "nonexistent");
        responseCodeIs(SC_NOT_FOUND);
    }

    @Test
    public void updateConfigurationTest() {
        ConfigurationRecordDto configurationRecord = configurationHelpers.configurationIsCreated(CONF_TYPE_IDENTIFIER, testConfigurationRecord1);
        configurationHelpers.updateConfigurationValue(CONF_TYPE_IDENTIFIER, configurationRecord.getKey(), TRUE.toString(), "boolean");
        responseCodeIs(SC_NO_CONTENT);
        configurationHelpers.configurationHasValue(CONF_TYPE_IDENTIFIER, configurationRecord.getKey(), TRUE);
    }

    @Test
    @Jira("DPNP-6")
    public void filteringConfigurationsTest() throws Exception {
//        Prepare data
        List<Map<String, String>> listOfConfigurations = selectExamplesForTestFromTable(configurationsTestsTables, "configurationsForFiltering");
        List<Map<String, String>> listOfParams = selectExamplesForTestFromTable(configurationsTestsTables, "configurationsFilteringData");
        for (Map<String, String> configurationMap : listOfConfigurations) {
            ConfigurationRecordDto confRecord = configurationHelpers.constructConfigurationRecord(configurationMap);
            configurationHelpers.configurationIsCreated(CONF_TYPE_IDENTIFIER, confRecord);
        }
//        Get data
        for (Map<String, String> params : listOfParams) {
            configurationHelpers.listOfConfigurationsIsGot(params.get("limit"), params.get("cursor"),
                    params.get("filter"), params.get("sort"), params.get("sortDesc"), CONF_TYPE_IDENTIFIER);
            responseCodeIs(SC_OK);
            numberOfEntitiesInResponse(ConfigurationRecordDto.class, Integer.valueOf(params.get("returned")));
            List<String> keys = Arrays.asList(params.get("expected_keys").split("\\s*,\\s*"));
            configurationHelpers.keysAreInResponseInOrder(keys);
        }
    }

    @Test
    public void filteringConfigurationsErrorTests() throws Exception {
        List<Map<String, String>> listOfParams = selectExamplesForTestFromTable(configurationsTestsTables, "configurationsFilteringErrorData");
        for (Map<String, String> params : listOfParams) {
            configurationHelpers.listOfConfigurationsIsGot(params.get("limit"), params.get("cursor"),
                    params.get("filter"), params.get("sort"), params.get("sort_desc"), CONF_TYPE_IDENTIFIER);
            verifyResponseAndCustomCode(params.get("response_code"), params.get("custom_code"));
        }
    }

    @Test
    public void checkErrorCodesCreateConfigurationTest() {
        List<Map<String, String>> listOfExamples = selectExamplesForTestFromTable(configurationsTestsTables, "checkErrorCodesCreateConfiguration");
        for (Map<String, String> configurationErrorMap : listOfExamples) {
            ConfigurationRecordDto confRecord = configurationHelpers.constructConfigurationRecord(configurationErrorMap);
            configurationHelpers.followingConfigurationIsCreated(confRecord, CONF_TYPE_IDENTIFIER);
            verifyResponseAndCustomCode(configurationErrorMap.get("response_code"), configurationErrorMap.get("custom_code"));
        }
    }

    @Test
    public void checkErrorCodesGetConfigurationTest() {
        ConfigurationRecordDto configurationRecord = configurationHelpers.configurationIsCreated(CONF_TYPE_IDENTIFIER, testConfigurationRecord1);
        configurationHelpers.getConfigurationWithKeyForIdentifier("wrong key", CONF_TYPE_IDENTIFIER);
        responseCodeIs(SC_NOT_FOUND);
        customCodeIs(CC_ENDPOINT_NOT_FOUND);
        configurationHelpers.getConfigurationWithKeyForIdentifier(configurationRecord.getKey(), "wrong identifier");
        responseCodeIs(SC_NOT_FOUND);
        customCodeIs(CC_ENDPOINT_NOT_FOUND);
    }

    @Test
    public void emptyPOSTIsSentToAllConfigurationsEndpoints() throws Exception {
        List<String> endpoints = new ArrayList<>();
        endpoints.add("configurations/");
        endpoints.add("configurations/" + CONF_TYPE_IDENTIFIER);
        endpoints.add("configurations/" + CONF_TYPE_IDENTIFIER + "/records");
        endpoints.forEach(url -> sendBlankPost(url, "configurations"));
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }
}
