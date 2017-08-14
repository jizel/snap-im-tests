package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;
import static travel.snapshot.dp.qa.junit.tests.common.CommonTest.transformNull;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.configuration.model.ConfigurationRecordDto;
import travel.snapshot.dp.api.configuration.model.ConfigurationTypeDto;
import travel.snapshot.dp.api.configuration.model.ValueType;
import travel.snapshot.dp.qa.cucumber.serenity.configuration.ConfigurationSteps;

import java.util.Map;


/**
 * Helper methods for Configuration tests
 */
public class ConfigurationHelpers extends ConfigurationSteps {

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
