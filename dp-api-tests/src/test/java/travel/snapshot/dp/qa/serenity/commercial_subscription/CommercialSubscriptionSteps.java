package travel.snapshot.dp.qa.serenity.commercial_subscription;

import com.jayway.restassured.response.Response;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.http.HttpStatus;
import org.junit.Assert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.api.identity.model.CommercialSubscriptionBaseDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CommercialSubscriptionSteps extends BasicSteps {

    private static final String COMMERCIAL_SUBSCRIPTION_PATH = "/identity/commercial_subscriptions";
    private static final String SESSION_COMMERCIAL_SUBSCRIPTIONS = "commercial_subcsriptions";
    private static final String SESSION_COMMERCIAL_SUBSCRIPTION_ID = "commercial_subscription_id";

    public CommercialSubscriptionSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(COMMERCIAL_SUBSCRIPTION_PATH);
    }

    @Step
    public void comSubscriptionsExists(List<CommercialSubscriptionDto> commSubcsriptions) {
        commSubcsriptions.forEach(s -> {
            CommercialSubscriptionDto existingSubscription = getSubscriptionById(s.getCommercialSubscriptionId());
            if (existingSubscription != null) {
                deleteEntity(existingSubscription.getCommercialSubscriptionId());
            }

            Response createResponse = createEntity(s);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Commercial subscription cannot be created " + createResponse.getBody().asString());
            }
        });
        Serenity.setSessionVariable(SESSION_COMMERCIAL_SUBSCRIPTIONS).to(commSubcsriptions);
    }

    @Step
    public void comSubscriptionIsCreated(CommercialSubscriptionDto commercialSubscription) {
        Response resp = createEntity(commercialSubscription);
        setSessionResponse(resp);
    }

    @Step
    public void deleteCommSubscriptionWithId(String commSubcriptionId) {
        Response response = deleteEntity(commSubcriptionId);
        setSessionResponse(response);
        Serenity.setSessionVariable(SESSION_COMMERCIAL_SUBSCRIPTION_ID).to(commSubcriptionId);
    }

    @Step
    public void commSubscriptionWithSameIdDoesNotExist() {
        String commSubcriptionId = Serenity.sessionVariableCalled(SESSION_COMMERCIAL_SUBSCRIPTION_ID);
        Response response = getEntity(commSubcriptionId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void updateCommSubscriptionWithId(String commSubscriptionId, CommercialSubscriptionBaseDto updatedCommSubscription) {
        Response tempResponse = getEntity(commSubscriptionId);
        Map<String, Object> mapToUpdate = new HashMap<>();


        if (updatedCommSubscription.getCustomerId() != null) {
            mapToUpdate.put("customer_id", updatedCommSubscription.getCustomerId());
        }

        if (updatedCommSubscription.getPropertyId() != null) {
            mapToUpdate.put("property_id", updatedCommSubscription.getPropertyId());
        }

        if (updatedCommSubscription.getApplicationId() != null) {
            mapToUpdate.put("application_id", updatedCommSubscription.getApplicationId());
        }

        mapToUpdate.put("is_active", updatedCommSubscription.getIsActive());

        Response response = updateEntity(commSubscriptionId, mapToUpdate, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void setCommSubscriptionIsActiveField(String commSubscriptionId, int isActive) {
        CommercialSubscriptionBaseDto comSub = new CommercialSubscriptionBaseDto();
        comSub.setIsActive(isActive);

        updateCommSubscriptionWithId(commSubscriptionId, comSub);
    }

    @Step
    public void commSubscriptionWithIdIsGot(String commSubscriptionId) {
        Response response = getEntity(commSubscriptionId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void commSubscriptionWithIdIsGotWithEtag(String commSubscriptionId) {
        Response tempResponse = getEntity(commSubscriptionId, null);
        Response resp = getEntity(commSubscriptionId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void commSubscriptionWithIdIsGotWithExpiredEtag(String commSubscriptionId) {
        Response tempResponse = getEntity(commSubscriptionId, null);
        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("start_date", "2016-03-01");
        mapForUpdate.put("end_date", "2017-01-01");

        Response updateResponse = updateEntity(commSubscriptionId, mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Commercial Subscription cannot be updated: " + updateResponse.asString());
        }
    }

    @Step
    public void listOfCommSubscriptionsIsGotWith(String limit, String cursor, String filter, String sort,
                                                 String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void idsInResponseInOrder(List<String> commSubscriptionIds) {
        if (commSubscriptionIds.isEmpty()) {
            return;
        }

        Response response = getSessionResponse();
        CommercialSubscriptionDto[] commSubscriptions = response.as(CommercialSubscriptionDto[].class);
        int i = 0;
        for (CommercialSubscriptionDto s : commSubscriptions) {
            assertEquals("Commercial subscription on index=" + i + " is not expected", commSubscriptionIds.get(i),
                    s.getCommercialSubscriptionId());
            i++;
        }
    }

    public CommercialSubscriptionDto getSubscriptionById(String commSubscriptionId) {
        CommercialSubscriptionDto[] comSubscription = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST,
                "commercial_subscription_id==" + commSubscriptionId, null, null).as(CommercialSubscriptionDto[].class);
        return Arrays.asList(comSubscription).stream().findFirst().orElse(null);
    }

    public void checkIsActive(String commSubscriptionId, int isActive) {
        CommercialSubscriptionDto comSub = getSubscriptionById(commSubscriptionId);
        Assert.assertEquals(comSub.getIsActive(), isActive);
    }
}
