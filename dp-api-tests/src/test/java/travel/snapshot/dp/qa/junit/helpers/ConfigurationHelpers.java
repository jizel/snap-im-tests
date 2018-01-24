package travel.snapshot.dp.qa.junit.helpers;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static travel.snapshot.dp.qa.junit.tests.common.CommonTest.transformNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ValidatableResponse;
import com.jayway.restassured.specification.RequestSpecification;
import lombok.NonNull;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.api.configuration.model.ConfigurationRecordDto;
import travel.snapshot.dp.api.configuration.model.ConfigurationTypeDto;
import travel.snapshot.dp.api.configuration.model.ValueType;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Helper methods for Configuration tests
 */
public class ConfigurationHelpers extends BasicSteps {

    public static final String CONFIGURATION_BASE_PATH = "/configurations";
    private static final String SESSION_CONFIGURATION_TYPES = "configuration_types";
    private static final String SESSION_CREATED_CONFIGURATION_TYPE = "created_configuration_type";
    private static final String SESSION_CONFIGURATIONS = "configurations";
    private static final String RECORDS_RESOURCE = "records";

    public ConfigurationHelpers() {
        spec.baseUri(propertiesHelper.getProperty(CONFIGURATION_BASE_URI)).basePath(CONFIGURATION_BASE_PATH);
    }

    private ConfigurationTypeDto getConfigurationTypeFromString(String jsonData) {
        return from(jsonData).getObject("", ConfigurationTypeDto.class);
    }

    private boolean isConfigurationExist(String key, String identifier) {
        Response response = getSecondLevelConfigurationEntity(identifier, RECORDS_RESOURCE, key);
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

    private JsonNode stringToJsonField(@NonNull String type, String value) {
        if (value == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
            switch (lowerCase(type)) {
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
        Response response = getConfigurationEntity(identifier);
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
    public Response tryDeleteConfigurationType(String identifier) {
        return deleteConfigurationEntity(identifier);
    }

    @Step
    public Response getConfigurationTypeWithId(String configType) {
        Response resp = getConfigurationEntity(configType);
        setSessionResponse(resp);
        return resp;
    }


    @Step
    public Response followingConfigurationTypeIsCreated(ConfigurationTypeDto configurationType) {
        Serenity.setSessionVariable(SESSION_CREATED_CONFIGURATION_TYPE).to(configurationType);

        if (isConfigurationTypeExist(configurationType.getIdentifier())) {
            deleteConfigurationEntity(configurationType.getIdentifier());
        }
        Response response = createEntity(configurationType);
        setSessionResponse(response);
        return response;
    }

    @Step
    public void listOfConfigurationTypesisGot(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void configurationTypeDoesntExist(String identifier) {
        Response response = getConfigurationEntity(identifier);
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
    public Response followingConfigurationIsCreated(ConfigurationRecordDto configurationRecord, String identifier) {
        String value = configurationRecord.getValue() == null ? null : String.valueOf(configurationRecord.getValue());
        String type = configurationRecord.getType() == null ? null : String.valueOf(configurationRecord.getType());
        Response response = createValueForKey(identifier, configurationRecord.getKey(), value, type);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
        return response;
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
        Response response = getSecondLevelConfigurationEntity(identifier, RECORDS_RESOURCE, key);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
        //TODO validate more that it doesnt exist
    }

    @Step
    public void followingConfigurationsExist(List<ConfigurationRecordDto> configurations, String identifier) {
        configurations.forEach(configuration -> {
            if (isConfigurationExist(configuration.getKey(), identifier)) {
                deleteSecondLevelConfigurationEntity(identifier, RECORDS_RESOURCE, configuration.getKey(), null);
            }
            Response createResponse = createValueForKey(identifier, configuration.getKey(), configuration.getValue().toString(), configuration.getType().toString());
            assertThat("Configuration cannot be created", createResponse.getStatusCode(), is(SC_CREATED));
        });
        Serenity.setSessionVariable(SESSION_CONFIGURATIONS).to(configurations);
    }

    @Step
    public void getConfigurationWithKeyForIdentifier(String key, String identifier) {
        Response response = getSecondLevelConfigurationEntity(identifier, RECORDS_RESOURCE, key);
        setSessionResponse(response);
    }

    @Step
    public void listOfConfigurationsIsGot(String limit, String cursor, String filter, String sort, String sortDesc, String identifier) {
        Response response = getSecondLevelConfigurationEntities(identifier, RECORDS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void valueInResponseIs(String value, String type) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(is(value));
    }

    @Step
    public void tryDeleteConfiguration(String key, String identifier) {
        Response resp = deleteSecondLevelConfigurationEntity(identifier, RECORDS_RESOURCE, key, null);
        setSessionResponse(resp);
    }

    @Step
    public void updateConfigurationTypeDescription(String identifier, String newDescription) {
        Map<String, Object> updateObject = new HashMap<>();
        updateObject.put("description", newDescription);
        Response resp = updateConfigurationEntity(identifier, updateObject, getConfigurationEntityEtag(identifier));
        setSessionResponse(resp);
    }

    @Step
    public void configurationTypeHasDescription(String identifier, String description) {
        String filter = String.format("identifier==%s", identifier);
        Response response = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null);
        ConfigurationTypeDto ct = Arrays.asList(response.as(ConfigurationTypeDto[].class)).get(0);

        assertEquals(description, ct.getDescription());
    }

    @Step
    public void updateConfigurationValue(String identifier, String key, String value, String type) {
        String etag = getSecondLevelConfigurationEntityEtag(identifier, RECORDS_RESOURCE, key);

        Response resp = updateValueForKey(identifier, key, value, type, etag);
        setSessionResponse(resp);
    }

    @Step
    public void configurationHasValue(String identifier, String key, Object value) {
        Response response = getSecondLevelConfigurationEntity(identifier, RECORDS_RESOURCE, key);
        response.then().body("value", is(value));
    }

    public void keysAreInResponseInOrder(List<String> keys) {
        Response response = getSessionResponse();
        ConfigurationRecordDto[] configs = response.as(ConfigurationRecordDto[].class);

        int i = 0;
        for (ConfigurationRecordDto u : configs) {
            assertEquals("Config on index=" + i + " is not expected", keys.get(i), u.getKey());
            i++;
        }
    }

    public void typesAreInResponseInOrder(List<String> configs) {
        Response response = getSessionResponse();
        ConfigurationTypeDto[] responseConfigs = response.as(ConfigurationTypeDto[].class);

        int i = 0;
        for (ConfigurationTypeDto c : responseConfigs) {
            assertEquals("Config on index=" + i + " is not expected", configs.get(i), c.getIdentifier());
            i++;
        }
    }

    public Response deleteConfigurationEntityWithEtag(String identifier, String etag) {
        Response response = given().spec(spec)
                .header(HEADER_IF_MATCH, etag)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
                .when()
                .delete("/{id}", identifier);
        setSessionResponse(response);
        return response;
    }

//    Private help methods

    private Response getConfigurationEntity(String entityId) {
        return given().spec(spec).when().get("/{id}", entityId);
    }

    private Response deleteConfigurationEntity(String identifier) {
        return deleteConfigurationEntityWithEtag(identifier, Optional.ofNullable(getConfigurationEntityEtag(identifier)).orElse(DEFAULT_SNAPSHOT_ETAG));
    }

    private String getConfigurationEntityEtag(String identifier) {
        return given().spec(spec).when().head("/{id}", identifier).getHeader(HEADER_ETAG);
    }

    private String getSecondLevelConfigurationEntityEtag(String identifier, String secondLevelObjectName, String key) {
        return given().spec(spec).when().head("/{firstLevelId}/{secondLevelName}/{secondLevelId}", identifier, secondLevelObjectName, key).getHeader(HEADER_ETAG);
    }

    private Response getSecondLevelConfigurationEntity(String identifier, String secondLevelObjectName, String key) {
        return given().spec(spec).when().get("/{firstLevelId}/{secondLevelName}/{secondLevelId}", identifier, secondLevelObjectName, key);
    }

    private Response deleteSecondLevelConfigurationEntity(String identifier, String secondLevelObjectName, String key, Map<String, String> queryParams) {
        RequestSpecification requestSpecification = given().spec(spec);
        String etag = getSecondLevelConfigurationEntityEtag(identifier, secondLevelObjectName, key);
        if (isNotBlank(etag)) {
            requestSpecification.header(HEADER_IF_MATCH, etag);
        } else {
            requestSpecification.header(HEADER_IF_MATCH, DEFAULT_SNAPSHOT_ETAG);
        }
        if (queryParams != null) {
            requestSpecification.parameters(queryParams);
        }
        return requestSpecification
                .when().delete("/{firstLevelId}/{secondLevelName}/{secondLevelId}", identifier, secondLevelObjectName, key);
    }

    private Response getSecondLevelConfigurationEntities(String identifier, String secondLevelObjectName, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, queryParams);

        Response response = given().spec(spec).parameters(params).when().get("{id}/{secondLevelName}", identifier, secondLevelObjectName);
        setSessionResponse(response);
        return response;
    }

    private Response updateConfigurationEntity(String entityId, Map<String, Object> data, String etag) {
        if (isBlank(etag)) {
            fail("Etag to be send in request header is null.");
        }
        return given().spec(spec)
                .header(HEADER_IF_MATCH, etag)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
                .body(data)
                .when()
                .post("/{id}", entityId);
    }

    public ConfigurationRecordDto configurationIsCreated(String identifier, ConfigurationRecordDto configurationRecord) {
        Response response = followingConfigurationIsCreated(configurationRecord, identifier);
        assertEquals(String.format("Failed to create configuration: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(ConfigurationRecordDto.class);
    }

    public ConfigurationRecordDto constructConfigurationRecord(Map<String, String> configurationMap) {
        ConfigurationRecordDto configurationRecord = new ConfigurationRecordDto();
        configurationRecord.setKey(transformNull(configurationMap.get("key")));
        configurationRecord.setValue(transformNull(configurationMap.get("value")));
        configurationRecord.setType(ValueType.valueOf(transformNull(configurationMap.get("type").toUpperCase())));
        return configurationRecord;
    }

    public Response createConfigurationType(ConfigurationTypeDto configurationType) {
        Response response = createEntity(configurationType);
        setSessionResponse(response);
        return response;
    }
}
