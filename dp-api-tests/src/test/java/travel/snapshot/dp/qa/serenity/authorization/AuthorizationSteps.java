package travel.snapshot.dp.qa.serenity.authorization;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.model.Stats;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AuthorizationSteps extends BasicSteps {

    public AuthorizationSteps() {
        super();
    }
    
    public void getConfigurationData(String url, String access_token){
        RequestSpecification requestSpecification = given()
        		.baseUri(PropertiesHelper.getProperty(CONFIGURATION_NGINX_BASE_URI))
                .parameter("access_token", access_token);

        Response response = requestSpecification.when().get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    public void getConfigurationDataWithNewToken(String url, String username, String password){
    	String access_token = given()
        		.baseUri(PropertiesHelper.getProperty(AUTHORIZATION_BASE_URI))
        		.parameter("client_id", "fad6b992")
        		.parameter("client_secret", "133707cc5837af1b6a87a1dbb117b978")
                .parameter("grant_type", "password")
                .parameter("username", username)
                .parameter("password", password)
                .parameter("code", "abcde")
                .get("/oauth/token")
                .path("access_token");
    	System.out.println("token = " + access_token);
    	
    	getConfigurationData(url, access_token);
    }
    
    public void getIdentityData(String url, String access_token){
        RequestSpecification requestSpecification = given()
        		.baseUri(PropertiesHelper.getProperty(IDENTITY_NGINX_BASE_URI))
                .parameter("access_token", access_token);

        Response response = requestSpecification.when().get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    public void getIdentityDataWithNewToken(String url, String username, String password){
    	String access_token = given()
        		.baseUri(PropertiesHelper.getProperty(AUTHORIZATION_BASE_URI))
        		.parameter("client_id", "fad6b992")
        		.parameter("client_secret", "133707cc5837af1b6a87a1dbb117b978")
                .parameter("grant_type", "password")
                .parameter("username", username)
                .parameter("password", password)
                .parameter("code", "abcde")
                .get("/oauth/token")
                .path("access_token");
    	
    	getIdentityData(url, access_token);
    }
    
    public void getToken(String username, String password) {
        RequestSpecification requestSpecification = given()
        		.baseUri(PropertiesHelper.getProperty(AUTHORIZATION_BASE_URI))
        		.parameter("client_id", "fad6b992")
        		.parameter("client_secret", "133707cc5837af1b6a87a1dbb117b978")
                .parameter("grant_type", "password")
                .parameter("username", username)
                .parameter("password", password);

        Response response = requestSpecification.get("/oauth/token");
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
}
