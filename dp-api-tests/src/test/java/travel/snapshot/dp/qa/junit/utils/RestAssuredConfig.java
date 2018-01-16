package travel.snapshot.dp.qa.junit.utils;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hamcrest.collection.IsIn.isOneOf;
import static org.hamcrest.core.IsNot.not;
import static travel.snapshot.dp.json.ObjectMappers.OBJECT_MAPPER;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.CONFIGURATION_REQUEST_HTTP_LOG_LEVEL;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.CONFIGURATION_RESPONSE_HTTP_LOG_LEVEL;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.CONFIGURATION_RESPONSE_HTTP_LOG_STATUS;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.IDENTITY_BASE_URI;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import com.jayway.restassured.specification.RequestSpecification;
import travel.snapshot.dp.qa.junit.helpers.PropertiesHelper;

/**
 * Created by zelezny on 10/16/2017.
 */
public class RestAssuredConfig {

    public static RequestSpecification setupRequestDefaults() {
        PropertiesHelper propertiesHelper = new PropertiesHelper();
        RestAssured.config = com.jayway.restassured.config.RestAssuredConfig.config().objectMapperConfig(
                new ObjectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> OBJECT_MAPPER));

        RequestSpecBuilder builder = new RequestSpecBuilder();
        String responseLogLevel = propertiesHelper.getProperty(CONFIGURATION_RESPONSE_HTTP_LOG_LEVEL);
        String requestLogLevel = propertiesHelper.getProperty(CONFIGURATION_REQUEST_HTTP_LOG_LEVEL);

        if (isNotBlank(responseLogLevel)) {
            builder.log(LogDetail.valueOf(requestLogLevel));
        }

        if (isNotBlank(responseLogLevel)) {
            RestAssured.replaceFiltersWith(
                    new ResponseLoggingFilter(
                            LogDetail.valueOf(responseLogLevel),
                            true,
                            System.out,
                            not(isOneOf(propertiesHelper.getListOfInt(CONFIGURATION_RESPONSE_HTTP_LOG_STATUS)))));
        }

        return builder.build().baseUri(propertiesHelper.getProperty(IDENTITY_BASE_URI)).contentType("application/json; charset=UTF-8");
    }
}
