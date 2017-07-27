package travel.snapshot.dp.qa.junit.helpers;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;


import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.junit.Assert.assertTrue;
import static travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper.getProperty;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.*;

/**
 * Created by ofayans on 7/23/17.
 */
public class KeycloakHelpers {

    protected RequestSpecification spec = null;

    public KeycloakHelpers() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setContentType("application/json; charset=UTF-8");
        builder.setBaseUri(getProperty("keyCloakURI"));
        spec = builder.build();
        spec.log().all();
    }

    public void createClient(Object client) throws Throwable {
        String jsonClient = new ObjectMapper().writeValueAsString(client);
        getKeyCloakToken();
        Response response = given().spec(spec)
                .header("Authorization","Bearer " + Serenity.sessionVariableCalled(KEYCLOAK_TOKEN))
                .header("Content-Type", "application/json")
                .body(jsonClient)
                .when().post("/admin/realms/Snapshot/clients");
        setSessionResponse(response);
        assertTrue(asList(SC_CREATED, SC_CONFLICT).contains(response.getStatusCode()));
    }

    public void deleteClient(String clientId) {
        getKeyCloakToken();
        Response response = given().spec(spec)
                .header("Authorization","Bearer " + Serenity.sessionVariableCalled(KEYCLOAK_TOKEN))
                .header("Content-Type", "application/json")
                .when().delete("/admin/realms/Snapshot/clients/" + clientId);
        setSessionResponse(response);
        responseCodeIs(SC_NO_CONTENT);
    }

    private void getKeyCloakToken() {
        String response = given().spec(spec)
                .contentType("application/x-www-form-urlencoded")
                .parameter("client_id", "admin-cli")
                .parameter("username", getProperty("keyCloakUsername"))
                .parameter("password", getProperty("keyCloakPassword"))
                .parameter("grant_type", "password")
                .when().post("/realms/master/protocol/openid-connect/token")
                .path("access_token");
        Serenity.setSessionVariable(KEYCLOAK_TOKEN).to(response);
    }
}
