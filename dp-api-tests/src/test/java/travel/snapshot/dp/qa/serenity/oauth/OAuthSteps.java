package travel.snapshot.dp.qa.serenity.oauth;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.sun.javafx.collections.MappingChange;

import net.thucydides.core.annotations.Step;
import net.serenitybdd.core.Serenity;

import java.util.HashMap;
import java.util.Map;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by vlcek on 2/9/2016.
 */
public class OAuthSteps extends BasicSteps{

    private static final String BASE_PATH__OAUTH = "/oauth/authorize";
    private static final String OAUTH_CODE = "s24fet6yhd7";
    public static final String OAUTH_PARAMETER_NAME = "access_token";
    
    public OAuthSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH__OAUTH);
    }

    @Step
    public void getToken (String username, String password)
    {
        setSessionResponse(getTokenResponse(username, password));
    }

    @Step
    public void setTokenToSession(String username, String password) {
        String token = getTokenString(username, password);
        setSessionVariable(OAUTH_PARAMETER_NAME, token);
        spec.parameter(OAUTH_PARAMETER_NAME, token);
    }


    private String getTokenString(String username, String password){
        return getTokenResponse(username, password).getBody().asString();
    }

    private Response getTokenResponse(String username, String password){
        // Prepare query parameters
        Map<String,String> queryParams = new HashMap<String,String>() {{
            put("username", username);
            put("password", password);
            put("code", OAUTH_CODE);
        }};

        spec.parameters(queryParams);
        return given().spec(spec).get();

    }

}
