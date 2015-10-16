package travel.snapshot.dp.qa.serenity.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.RandomStringUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Configuration;
import travel.snapshot.dp.qa.model.ConfigurationType;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class ConfigurationSteps extends BasicSteps {


    private static final String SESSION_CONFIGURATION_TYPES = "configuration_types";
    private static final String SESSION_CREATED_CONFIGURATION_TYPE = "created_configuration_type";
    private static final String SESSION_CONFIGURATIONS = "configurations";

    public ConfigurationSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(CONFIGURATION_BASE_URI));
    }

    private ConfigurationType getConfigurationTypeFromString(String jsonData) {
        ConfigurationType configurationType = from(jsonData).getObject("", ConfigurationType.class);
        return configurationType;
    }

    private Response createConfigurationType(ConfigurationType t) {
        return given().spec(spec).basePath("/configuration/configurations")
                .body(t)
                .when().post();
    }
    
    private Response deleteConfiguration(String key, String identifier) {
        return given().spec(spec).basePath("/configuration")
                .when().delete("/{identifier}/{key}", identifier, key);
    }

    private boolean isConfigurationExist(String key, String identifier) {
        Response response = getConfiguration(key, identifier);
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            return true;
        } else if (statusCode == 404) {
            return false;
        } else {
            throw new RuntimeException("invalid server status code: " + statusCode);
        }
    }

    private Response getConfiguration(String key, String identifier) {
        return given().spec(spec).basePath("/configuration")
                .when().get("/{identifier}/{key}", identifier, key);
    }

    private Response createConfiguration(String key, JsonNode value, String identifier) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("value", value);
        return given().spec(spec).basePath("/configuration")
                .body(map)
                .when().post("/{id}", identifier);
    }

    private Response updateConfiguration(String key, JsonNode value, String identifier) {
        return given().spec(spec).basePath("/configuration")
                .body(value)
                .when().post("/{id}/{key}", identifier, key);
    }

    private Response createValueForKey(String identifier, String key, String value) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
            actualObj = mapper.readTree(value);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createConfiguration(key, actualObj, identifier);
    }

    private Response updateValueForKey(String identifier, String key, String value) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
            actualObj = mapper.readTree(value);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return updateConfiguration(key, actualObj, identifier);
    }


    private Response updateConfigurationType(String identifier, String description) {
        return given().spec(spec).basePath("/configuration")
                .body(description)
                .when().post("/{id}/description_update", identifier);

    }

    private Response deleteConfigurationType(String id) {
        return given().spec(spec).basePath("/configuration")
                .when().delete("/{id}", id);
    }

    private Response getConfigurationType(String id) {
        return given().spec(spec).basePath("/configuration")
                .when().get("/{id}", id);
    }

    private Boolean isConfigurationTypeExist(String identifier) {
        Response response = getConfigurationType(identifier);
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            return true;
        } else if (statusCode == 404) {
            return false;
        } else {
            throw new RuntimeException("invalid server status code: " + statusCode);
        }
    }

    /**
     * getting configuration types over rest api, if limit and cursor is null or empty, it's not added to query string
     *
     * @param limit
     * @param cursor
     * @return
     */
    private Response getConfigurationTypes(String limit, String cursor) {
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath("/configuration/configurations");

        if (cursor != null && !"".equals(cursor)) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null && !"".equals(limit)) {
            requestSpecification.parameter("limit", limit);
        }
        return requestSpecification.when().get();
    }

    /**
     * getting configurations over rest api, if limit and cursor is null or empty, it's not added to query string
     *
     * @param limit
     * @param cursor
     * @return
     */
    private Response getConfigurations(String limit, String cursor, String identifier) {
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath("/configuration");

        if (cursor != null && !"".equals(cursor)) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null && !"".equals(limit)) {
            requestSpecification.parameter("limit", limit);
        }
        return requestSpecification.when().get("/{id}", identifier);
    }

    @Step
    public void tryDeleteConfigurationType(String identifier) {
        Response resp = deleteConfigurationType(identifier);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void getConfigurationTypeWithId(String configType) {
        Response resp = getConfigurationType(configType);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void followingConfigurationTypesExist(List<ConfigurationType> configurationTypes, Integer count) {
        configurationTypes.forEach(t -> {

            if (isConfigurationTypeExist(t.getIdentifier())) {
                deleteConfigurationType(t.getIdentifier());
            }
            createConfigurationType(t);
            IntStream.rangeClosed(1, count).forEach(i -> {
                createValueForKey(t.getIdentifier(), String.format("key_%d_%s", i, RandomStringUtils.randomNumeric(4)), "\"" + RandomStringUtils.randomAlphanumeric(20) + "\"");
            });

        });
        Serenity.setSessionVariable(SESSION_CONFIGURATION_TYPES).to(configurationTypes);
    }


    @Step
    public void dataIsUsedForCreation(String jsonData) {
        ConfigurationType ct = getConfigurationTypeFromString(jsonData);

        if (isConfigurationTypeExist(ct.getIdentifier())) {
            deleteConfigurationType(ct.getIdentifier());
        }

        Response response = createConfigurationType(ct);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }


    @Step
    public void followingConfigurationTypeIsCreated(ConfigurationType configurationType) {
        Serenity.setSessionVariable(SESSION_CREATED_CONFIGURATION_TYPE).to(configurationType);

        if (isConfigurationTypeExist(configurationType.getIdentifier())) {
            deleteConfigurationType(configurationType.getIdentifier());
        }
        Response response = createConfigurationType(configurationType);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void listOfConfigurationTypesisGot(String limit, String cursor) {
        Response response = getConfigurationTypes(limit, cursor);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void numberOfConfigurationTypesIsInResponse(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        ConfigurationType[] configurationTypes = response.as(ConfigurationType[].class);
        assertEquals("There should be " + count + " configurationTypes got", count, configurationTypes.length);
    }

    @Step
    public void configurationTypeDoesntExist(String identifier) {
        Response response = getConfigurationType(identifier);
        response.then().statusCode(404);
        //TODO validate more that it doesnt exist
    }

    @Step
    public void compareHeaderWithIdentifier(String header, String identifier) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().header(header, endsWith("/configuration/" + identifier));
    }

    @Step
    public void bodyContainsConfigurationType(String identifier, String description) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("identifier", is(identifier)).body("description", is(description));
    }

    @Step
    public void followingConfigurationIsCreated(Configuration c, String identifier) {
        Response response = createValueForKey(identifier, c.getKey(), c.getValue());
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void bodyContainsConfiguration(String key, String value) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("key", is(key)).body("value", is(value));
    }

    @Step
    public void compareHeaderWithKey(String header, String key) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().header(header, endsWith(key));
    }

    @Step
    public void configurationDoesntExistForIdentifier(String key, String identifier) {
        Response response = getConfiguration(key, identifier);
        response.then().statusCode(404);
        //TODO validate more that it doesnt exist
    }

    @Step
    public void followingConfigurationsExist(List<Configuration> configurations, String identifier) {
        configurations.forEach(c -> {
            if (isConfigurationExist(c.getKey(), identifier)) {
                deleteConfiguration(c.getKey(), identifier);
            }
            createValueForKey(identifier, c.getKey(), c.getValue());
        });
        Serenity.setSessionVariable(SESSION_CONFIGURATIONS).to(configurations);
    }


    @Step
    public void getConfigurationWithKeyForIdentifier(String key, String identifier) {
        Response response = getConfiguration(key, identifier);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void listOfConfigurationsIsGot(String limit, String cursor, String identifier) {
        Response response = getConfigurations(limit, cursor, identifier);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void valueInResponseIs(String value) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(is(value));
    }

    @Step
    public void numberOfConfigurationsIsInResponse(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        Configuration[] configurations = response.as(Configuration[].class);
        assertEquals("There should be " + count + " configurations got", count, configurations.length);
    }

    @Step
    public void tryDeleteConfiguration(String key, String identifier) {
        Response resp = deleteConfiguration(key, identifier);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void updateConfigurationTypeDescription(String identifier, String newDescription) {
        Response resp = updateConfigurationType(identifier, newDescription);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void configurationTypeHasDescription(String identifier, String description) {
        Response response = getConfigurationType(identifier);
        fail("How to assert configurationType has description?");
    }

    @Step
    public void updateConfigurationValue(String identifier, String key, String value) {
        Response resp = updateValueForKey(identifier, key, value);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void configurationHasValue(String identifier, String key, String value) {
        Response response = getConfiguration(key, identifier);
        response.then().body(is(value));
    }
}
