package travel.snapshot.dp.qa.serenity.api_subscriptions;

import com.jayway.restassured.response.Response;

import org.apache.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import travel.snapshot.dp.api.identity.subscription.model.ApiSubscriptionDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.fail;

/**
 * Created by vlcek on 3/17/2016.
 */
public class ApiSubscriptionSteps extends BasicSteps {

    private static final String BASE_PATH_API_SUBSCRIPTIONS = "/identity/api_subscriptions";
    private static final String API_SUBSCRIPTIONS_ID = "api_subscription_id";

    public ApiSubscriptionSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH_API_SUBSCRIPTIONS);
    }

    public void apiWithIdIsGot(String apiCode) {
        Response response = getEntity(apiCode, null);
        setSessionResponse(response);


    }


    public void followingApiSubscriptionExist(List<ApiSubscriptionDto> apis) {
        apis.forEach(t -> {
            Response createResponce = createEntity(t);
            if (createResponce.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("App Subscription cannot be create");
            }
        });
    }

    public void apiWithIdIsGotWithEtag(String apiSubscriptionId) {
        Response temp = getEntity(apiSubscriptionId);
        Response responce = getEntity(apiSubscriptionId, temp.getHeader(HEADER_ETAG));
        setSessionResponse(responce);

    }

    public void createExistingApiSubscription() {
        ApiSubscriptionDto[] apiSubsriptions = getEntities(null, null, null, null, null).as(ApiSubscriptionDto[].class);
        ApiSubscriptionDto selected = Arrays.asList(apiSubsriptions).stream().findFirst().orElse(null);
        if (selected == null) {
            fail("There are no api subscription to recreate");
        }

        ApiSubscriptionDto apiToCreate = new ApiSubscriptionDto();
        String s = selected.getApplicationVersionId();
        apiToCreate.setApplicationVersionId(selected.getApplicationVersionId());
        apiToCreate.setApiVersion(selected.getApiVersion());

        Response response = createEntity(apiToCreate);
        setSessionResponse(response);
    }

    public void activateApiSubscription(String apiSubscriptionId) {
        Response responce = given().spec(spec).when().post("/{id}/active", apiSubscriptionId);
        setSessionResponse(responce);
    }

    public void deactivateApiSubscription(String apiSubscriptionId) {
        Response responce = given().spec(spec).when().post("/{id}/inactive", apiSubscriptionId);
        setSessionResponse(responce);
    }

    public void apiSubscriptionInListOfAll(String apiSubscriptionId, Boolean presence) {
        String filter = API_SUBSCRIPTIONS_ID + "=='" + apiSubscriptionId + "'";
        ApiSubscriptionDto[] apiSubsriptions = getEntities(LIMIT_TO_ONE, null, filter, null, null).as(ApiSubscriptionDto[].class);
        ApiSubscriptionDto apiSub = Arrays.asList(apiSubsriptions).stream().findFirst().orElse(null);

        if ((apiSub == null) && (presence)) {
            fail("Api subscription with ID " + apiSubscriptionId + " should be present among list of all, but it is not!");
        }

        if ((apiSub != null) && (!presence)) {
            fail("Api subscription with ID " + apiSubscriptionId + " should not be in list, but it is!");
        }

    }

    public void deleteApiSubscription(String apiSubscriptionId) {
        Response responce = deleteEntity(apiSubscriptionId);
        setSessionResponse(responce);
    }
}
