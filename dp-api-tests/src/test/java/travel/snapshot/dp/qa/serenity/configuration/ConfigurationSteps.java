package travel.snapshot.dp.qa.serenity.configuration;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Configuration;
import travel.snapshot.dp.qa.model.ConfigurationType;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class ConfigurationSteps extends BasicSteps {

    private static final String SESSION_CONFIGURATION_TYPES = "configuration_types";
    private static final String SESSION_CREATED_CONFIGURATION_TYPE = "created_configuration_type";
    private static final String SESSION_CONFIGURATIONS = "configurations";
    public static final String CONFIGURATION_BASE_PATH = "/configurations";

    public ConfigurationSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(CONFIGURATION_BASE_URI)).basePath(CONFIGURATION_BASE_PATH);
    }

    private ConfigurationType getConfigurationTypeFromString(String jsonData) {
        return from(jsonData).getObject("", ConfigurationType.class);
    }

    private boolean isConfigurationExist(String key, String identifier) {
        Response response = getSecondLevelEntity(identifier, SECOND_LEVEL_OBJECT_RECORDS, key, null);
        int statusCode = response.statusCode();
        if (statusCode == HttpStatus.SC_OK) {
            return true;
        } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
            return false;
        } else {
            throw new RuntimeException("invalid server status code during getting configuration[identifier=" + identifier + ", key=" + key + "]: " + statusCode);
        }
    }

    private Response createValueForKey(String identifier, String key, String value, String type) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonValue = stringToJsonField(type, value);
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("value", jsonValue);
        map.put("type", type);
        return given().spec(spec)
                .body(map)
                .when().post("/{id}/records", identifier);
    }

    private Response updateValueForKey(String identifier, String key, String value, String type, String etag) {
        JsonNode jsonValue = stringToJsonField(type, value);

        Map<String, Object> map = new HashMap<>();
        map.put("value", jsonValue);
        map.put("type", type);
        RequestSpecification requestSpecification = given().spec(spec);
        if (!StringUtils.isBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        return requestSpecification.body(map).when().post("/{id}/records/{key}", identifier, key);
    }

    private JsonNode stringToJsonField(String type, String value) {
        if (value == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
            switch (type) {
                case "string":
                case "date":
                case "datetime": {
                    actualObj = mapper.readTree("\"" + value + "\"");
                    break;
                }
                case "integer":
                case "long":
                case "boolean":
                case "double":
                case "object": {
                    actualObj = mapper.readTree(value);
                    break;
                }
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return actualObj;
    }

    private Boolean isConfigurationTypeExist(String identifier) {
        if (StringUtils.isBlank(identifier)) {
            return false;
        }
        Response response = getEntity(identifier);
        int statusCode = response.statusCode();
        if (statusCode == HttpStatus.SC_OK) {
            return true;
        } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
            return false;
        } else {
            throw new RuntimeException("invalid server status code for getting configuration type [identifier=" + identifier + "]: " + statusCode);
        }
    }

    @Step
    public void tryDeleteConfigurationType(String identifier) {
        Response resp = deleteEntity(identifier);
        setSessionResponse(resp);
    }

    @Step
    public void getConfigurationTypeWithId(String configType) {
        Response resp = getEntity(configType, null);
        setSessionResponse(resp);
    }

    @Step
    public void followingConfigurationTypesExist(List<ConfigurationType> configurationTypes, Integer count) {
        configurationTypes.forEach(t -> {

            if (isConfigurationTypeExist(t.getIdentifier())) {
                deleteEntity(t.getIdentifier());
            }

            Response createResponse = createEntity(t);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Configuration type cannot be created");
            }
            IntStream.rangeClosed(1, count).forEach(i -> {
                Response createKeyResponse = createValueForKey(t.getIdentifier(), String.format("key_%d_%s", i, RandomStringUtils.randomNumeric(4)), RandomStringUtils.randomAlphanumeric(20), "string");
                if (createKeyResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                    fail("Configuration key cannot be created");
                }
            });

        });
        Serenity.setSessionVariable(SESSION_CONFIGURATION_TYPES).to(configurationTypes);
    }

    @Step
    public void dataIsUsedForCreation(String jsonData, boolean deleteBeforeCreate) {
        ConfigurationType ct = getConfigurationTypeFromString(jsonData);

        if (deleteBeforeCreate && isConfigurationTypeExist(ct.getIdentifier())) {
            deleteEntity(ct.getIdentifier());
        }

        Response response = createEntity(ct);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }


    @Step
    public void followingConfigurationTypeIsCreated(ConfigurationType configurationType) {
        Serenity.setSessionVariable(SESSION_CREATED_CONFIGURATION_TYPE).to(configurationType);

        if (isConfigurationTypeExist(configurationType.getIdentifier())) {
            deleteEntity(configurationType.getIdentifier());
        }
        Response response = createEntity(configurationType);
        setSessionResponse(response);
    }

    @Step
    public void listOfConfigurationTypesisGot(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void configurationTypeDoesntExist(String identifier) {
        Response response = getEntity(identifier);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
        //TODO validate more that it doesnt exist
    }

    @Step
    public void compareHeaderWithIdentifier(String header, String identifier) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().header(header, endsWith(CONFIGURATION_BASE_PATH + "/" + identifier));
    }

    @Step
    public void bodyContainsConfigurationType(String identifier, String description) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("identifier", is(identifier)).body("description", is(description));
    }

    @Step
    public void followingConfigurationIsCreated(Configuration c, String identifier) {
        Response response = createValueForKey(identifier, c.getKey(), c.getValue(), c.getType());
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    /**
     * validates confoguration key, value, type in body if key is null, then key is not validated
     */
    @Step
    public void bodyContainsConfiguration(String key, String value, String type) throws IOException {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        ValidatableResponse validatable = response.then();
        if (key != null) {
            validatable.body("key", is(key));
        }
        validatable.body("type", is(type));
        JsonNode valueJson = response.as(JsonNode.class).get("value");
        switch (type) {
            case "string": {
                assertEquals(value, valueJson.asText());
                break;
            }
            case "integer": {
                assertEquals(Integer.valueOf(value), Integer.valueOf(valueJson.asInt()));
                break;
            }
            case "long": {
                assertEquals(Long.valueOf(value), Long.valueOf(valueJson.longValue()));
                break;
            }
            case "double": {
                assertEquals(Double.valueOf(value), Double.valueOf(valueJson.doubleValue()));
                break;
            }
            case "boolean": {
                assertEquals(Boolean.valueOf(value), valueJson.booleanValue());
                break;
            }
            case "date": {
                LocalDate expectedDate = LocalDate.parse(value);
                LocalDate actualDate = LocalDate.parse(valueJson.asText());
                assertEquals(expectedDate, actualDate);
                break;
            }
            case "datetime": {
                LocalDateTime expectedDateTime = LocalDateTime.parse(value);
                LocalDateTime actualDateTime = LocalDateTime.parse(valueJson.asText());
                assertEquals(expectedDateTime, actualDateTime);
                break;
            }
            case "object": {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj = mapper.readTree(value);
                assertEquals(actualObj, valueJson);
                break;
            }
            default:
                break;
        }
    }

    @Step
    public void compareHeaderWithKey(String header, String key) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().header(header, endsWith(key));
    }

    @Step
    public void configurationDoesntExistForIdentifier(String key, String identifier) {
        Response response = getSecondLevelEntity(identifier, SECOND_LEVEL_OBJECT_RECORDS, key, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
        //TODO validate more that it doesnt exist
    }

    @Step
    public void followingConfigurationsExist(List<Configuration> configurations, String identifier) {
        configurations.forEach(c -> {
            if (isConfigurationExist(c.getKey(), identifier)) {
                deleteSecondLevelEntity(identifier, SECOND_LEVEL_OBJECT_RECORDS, c.getKey());
            }
            Response createResponse = createValueForKey(identifier, c.getKey(), c.getValue(), c.getType());
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Configuration cannot be created");
            }
        });
        Serenity.setSessionVariable(SESSION_CONFIGURATIONS).to(configurations);
    }

    @Step
    public void getConfigurationWithKeyForIdentifier(String key, String identifier) {
        Response response = getSecondLevelEntity(identifier, SECOND_LEVEL_OBJECT_RECORDS, key, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfConfigurationsIsGot(String limit, String cursor, String filter, String sort, String sortDesc, String identifier) {
        Response response = getSecondLevelEntities(identifier, SECOND_LEVEL_OBJECT_RECORDS, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void valueInResponseIs(String value, String type) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(is(value));
    }

    @Step
    public void tryDeleteConfiguration(String key, String identifier) {
        Response resp = deleteSecondLevelEntity(identifier, SECOND_LEVEL_OBJECT_RECORDS, key);
        setSessionResponse(resp);
    }

    @Step
    public void updateConfigurationTypeDescription(String identifier, String newDescription) {
        Response tempResponse = getEntity(identifier);
        Map<String, Object> updateObject = new HashMap<>();
        updateObject.put("description", newDescription);
        Response resp = updateEntity(identifier, updateObject, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void configurationTypeHasDescription(String identifier, String description) {
        String filter = String.format("identifier==%s", identifier);
        Response response = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null);
        ConfigurationType ct = Arrays.asList(response.as(ConfigurationType[].class)).get(0);

        assertEquals(description, ct.getDescription());
    }

    @Step
    public void updateConfigurationValue(String identifier, String key, String value, String type) {
        Response tempResponse = getSecondLevelEntity(identifier, SECOND_LEVEL_OBJECT_RECORDS, key, null);

        Response resp = updateValueForKey(identifier, key, value, type, tempResponse.header(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void configurationHasValue(String identifier, String key, String value) {
        Response response = getSecondLevelEntity(identifier, SECOND_LEVEL_OBJECT_RECORDS, key, null);
        response.then().body("value", is(value));
    }

    public void keysAreInResponseInOrder(List<String> keys) {
        Response response = getSessionResponse();
        Configuration[] configs = response.as(Configuration[].class);

        int i = 0;
        for (Configuration u : configs) {
            assertEquals("Config on index=" + i + " is not expected", keys.get(i), u.getKey());
            i++;
        }
    }

    public void typesAreInResponseInOrder(List<String> configs) {
        Response response = getSessionResponse();
        ConfigurationType[] responseConfigs = response.as(ConfigurationType[].class);

        int i = 0;
        for (ConfigurationType c : responseConfigs) {
            assertEquals("Config on index=" + i + " is not expected", configs.get(i), c.getIdentifier());
            i++;
        }
    }
}
