package travel.snapshot.dp.qa.serenity.api_subscriptions;

import com.jayway.restassured.response.Response;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.api.identity.model.ApiSubscriptionDto;
import travel.snapshot.dp.api.identity.model.ApiSubscriptionUpdateDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

    public void followingApiSubscriptionExist(List<ApiSubscriptionDto> apiSubscriptionList) {
        apiSubscriptionList.forEach(t -> {
            Response createResponce = createEntity(t);
            if (createResponce.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("App Subscription cannot be created");
            }
        });
    }

    public void followingApiSubscriptionIsCreated(ApiSubscriptionDto apiSubscription) {
        Response createdResponse = createEntity(apiSubscription);
        setSessionResponse(createdResponse);
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

    public void setActivateFieldApiSubscription(String apiSubscriptionId, Boolean active) {
        ApiSubscriptionDto apiSubscription = getEntity(apiSubscriptionId).as(ApiSubscriptionDto.class);

        if (active) {
            apiSubscription.setIsActive(1);
        } else {
            apiSubscription.setIsActive(0);
        }

        List<ApiSubscriptionUpdateDto> listToSend = new ArrayList<ApiSubscriptionUpdateDto>();
        listToSend.add(apiSubscription);

        updateApiSubscription(apiSubscriptionId, listToSend);
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

    public void updateApiSubscription(String apiSubscriptionId, List<ApiSubscriptionUpdateDto> updateData) {
        Response apiSub = getEntity(apiSubscriptionId);
        Map<String, Object> mapToUpdate = new HashMap<>();

        if (updateData.get(0).getApiVersion() != null) {
            mapToUpdate.put("api_version", updateData.get(0).getApiVersion());
        }

        if (updateData.get(0).getApplicationVersionId() != null) {
            mapToUpdate.put("application_version_id", updateData.get(0).getApplicationVersionId());
        }

        mapToUpdate.put("is_active", updateData.get(0).getIsActive());

        Response resp = updateEntity(apiSubscriptionId, mapToUpdate, apiSub.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    public void apiSubscriptionActivity(String apiSubscriptionId, boolean activity) {
        ApiSubscriptionDto api = getEntity(apiSubscriptionId).as(ApiSubscriptionDto.class);
        assertNotEquals(activity, api.getIsActive());
    }

    public void listApiSubscriptiosIsGot(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void responceSortIs(List<String> order) {
        if (order.isEmpty()) {
            return;
        }

        ApiSubscriptionDto[] apiSubscriptios = getSessionResponse().as(ApiSubscriptionDto[].class);
        int i = 0;
        for (ApiSubscriptionDto api : apiSubscriptios) {
            assertEquals(api.getApiVersion(), order.get(i));
            i++;
        }
    }
}