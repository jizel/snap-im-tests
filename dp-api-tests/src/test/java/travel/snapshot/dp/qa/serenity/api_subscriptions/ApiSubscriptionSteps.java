package travel.snapshot.dp.qa.serenity.api_subscriptions;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

/**
 * Created by vlcek on 3/17/2016.
 *
 * API subscriptions are now removed from DP but might be reintroduced in the future.
 * Keep this class (some methods commented)
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
        Response response = getEntity(apiCode);
        setSessionResponse(response);
    }

//    public void followingApiSubscriptionExist(List<ApiSubscriptionDto> apiSubscriptionList) {
// The following was commented out due to api subscription code was disabled in master
// Corresponding tracker issues are: DP-1925, DP-1927
/*        apiSubscriptionList.forEach(t -> {
            Response createResponce = createEntity(t);
            if (createResponce.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("App Subscription cannot be created");
            }
        }); */
//    }

//    public void followingApiSubscriptionIsCreated(ApiSubscriptionDto apiSubscription) {
//        Response createdResponse = createEntity(apiSubscription);
//        setSessionResponse(createdResponse);
//    }
//
//
//    public void createExistingApiSubscription() {
//        ApiSubscriptionDto[] apiSubsriptions = getEntities(null, null, null, null, null, null, null).as(ApiSubscriptionDto[].class);
//        ApiSubscriptionDto selected = Arrays.asList(apiSubsriptions).stream().findFirst().orElse(null);
//        if (selected == null) {
//            fail("There are no api subscription to recreate");
//        }
//
//        Response response = createEntity(selected);
//        setSessionResponse(response);
//    }
//
//    public void setActivateFieldApiSubscription(String apiSubscriptionId, Boolean active) throws JsonProcessingException {
//        ApiSubscriptionUpdateDto apiSubscription = new ApiSubscriptionUpdateDto();
//
//        if (active) {
//            apiSubscription.setIsActive(true);
//        } else {
//            apiSubscription.setIsActive(false);
//        }
//
//        updateApiSubscription(apiSubscriptionId, apiSubscription);
//    }
//
//    public void apiSubscriptionInListOfAll(String apiSubscriptionId, Boolean presence) {
//        String filter = API_SUBSCRIPTIONS_ID + "=='" + apiSubscriptionId + "'";
//        ApiSubscriptionDto[] apiSubsriptions = getEntities(null, LIMIT_TO_ONE, null, filter, null, null, null).as(ApiSubscriptionDto[].class);
//        ApiSubscriptionDto apiSub = Arrays.asList(apiSubsriptions).stream().findFirst().orElse(null);
//
//        if ((apiSub == null) && (presence)) {
//            fail("Api subscription with ID " + apiSubscriptionId + " should be present among list of all, but it is not!");
//        }
//
//        if ((apiSub != null) && (!presence)) {
//            fail("Api subscription with ID " + apiSubscriptionId + " should not be in list, but it is!");
//        }
//
//    }
//
//    public void deleteApiSubscription(String apiSubscriptionId) {
//        deleteEntityWithEtag(apiSubscriptionId);
//    }
//
//
//    public void updateApiSubscription(String apiSubscriptionId, ApiSubscriptionUpdateDto updateData) throws JsonProcessingException {
//
//        Response originalApi = getEntity(apiSubscriptionId);
//        if (originalApi.statusCode() != HttpStatus.SC_OK) {
//            fail("Api subscription with id " + apiSubscriptionId + " not found!");
//        }
//
//        JSONObject object = retrieveData(updateData);
//
//        Response resp = updateEntity(apiSubscriptionId, object.toString(), originalApi.getHeader(HEADER_ETAG));
//        setSessionResponse(resp);
//    }
//
//    public void apiSubscriptionActivity(String apiSubscriptionId, boolean activity) {
//        ApiSubscriptionDto api = getEntity(apiSubscriptionId).as(ApiSubscriptionDto.class);
//        assertEquals(activity, api.getIsActive());
//    }
//
//    public void listApiSubscriptiosIsGot(String userId, String appVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
//        Response response = getEntitiesByUserForApp(userId, appVersionId, null, limit, cursor, filter, sort, sortDesc, null);
//        setSessionResponse(response);
//    }
//
//    public void responceSortIs(List<String> order) {
//        if (order.isEmpty()) {
//            return;
//        }
//
//        ApiSubscriptionDto[] apiSubscriptios = getSessionResponse().as(ApiSubscriptionDto[].class);
//        int i = 0;
//        for (ApiSubscriptionDto api : apiSubscriptios) {
//            assertEquals(api.getId(), order.get(i));
//            i++;
//        }
//    }
}
