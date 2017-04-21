package travel.snapshot.dp.qa.steps.identity.api_subscriptions;

import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;

import com.sun.jna.platform.win32.Netapi32Util;
import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.identity.model.ApiSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.api_subscriptions.ApiSubscriptionSteps;
import travel.snapshot.dp.qa.serenity.applications.ApplicationVersionsSteps;
import travel.snapshot.dp.qa.serenity.applications.ApplicationsSteps;
import travel.snapshot.dp.qa.serenity.commercial_subscription.CommercialSubscriptionSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;

import java.util.List;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

/**
 * Created by vlcek on 3/17/2016.
 */
public class ApiSubscriptionStepsdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private PropertySteps propertySteps;

    @Steps
    private ApiSubscriptionSteps apiSteps;

    @Steps
    private CommercialSubscriptionSteps commercialSubscriptionSteps;

    @Steps
    private ApplicationsSteps applicationsSteps;

    @Steps
    private ApplicationVersionsSteps applicationVersionsSteps;

    @Steps
    private UsersSteps usersSteps;

    @Given("^The following api subscriptions exist$")
    public void theFollowingApiSubscriptionsExist(List<ApiSubscriptionDto> listApiSubscriptions) throws Throwable {
        apiSteps.followingApiSubscriptionExist(listApiSubscriptions);
    }

    @Given("^The following api subscriptions is created$")
    public void theFollowingApiSubscriptionsIsCreated(List<ApiSubscriptionDto> listApiSubscriptions) throws Throwable {
        apiSteps.followingApiSubscriptionIsCreated(listApiSubscriptions.get(0));
    }

    @When("^Api subscription with id \"([^\"]*)\" is got$")
    public void apiSubscriptionWithIdIsGot(String apiSubscriptionId) throws Throwable {
        apiSteps.apiWithIdIsGot(apiSubscriptionId);
    }

    @When("^Trying to create second api subscription with the same versionID$")
    public void tryingToCreateSecondApiSubscriptionWithTheSameVersionID() throws Throwable {
        apiSteps.createExistingApiSubscription();
    }

    @When("^Api subscription with id \"([^\"]*)\" is activated$")
    public void apiSubscriptionWithIdIsActivated(String apiSubscriptionId) throws Throwable {
        apiSteps.setActivateFieldApiSubscription(apiSubscriptionId, true);
    }

    @When("^Api subscription with id \"([^\"]*)\" is deactivated$")
    public void apiSubscriptionWithIdIsDeactivated(String apiSubscriptionId) throws Throwable {
        apiSteps.setActivateFieldApiSubscription(apiSubscriptionId, false);
    }

    @When("^Api subscription with id \"([^\"]*)\" is deleted$")
    public void apiSubscriptionWithIdIsDeleted(String apiSubscriptionId) throws Throwable {
        apiSteps.deleteApiSubscription(apiSubscriptionId);
    }

    @When("^List of api subscriptions is (?:requested|got)( with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfApiSubscriptionsIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc,
            @Transform(NullEmptyStringConverter.class) String userName,
            @Transform(NullEmptyStringConverter.class) String appVersionName) {
        String appVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        String userId = usersSteps.resolveUserId(userName);
        apiSteps.listApiSubscriptiosIsGot(userId, appVersionId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^Api subscription with id \"([^\"]*)\" is updated with following data$")
    public void apiSubscriptionWithIdIsUpdatedWithFollowingData(String apiSubscriptionId, List<ApiSubscriptionDto> updateData) throws Throwable {
        apiSteps.updateApiSubscription(apiSubscriptionId, updateData.get(0));
    }


    @Then("^Api subscription with id \"([^\"]*)\" is among all api subscriptions$")
    public void apiSubscriptionWithIdIsAmongAllApiSubscriptions(String apiSubscriptionId) throws Throwable {
        apiSteps.apiSubscriptionInListOfAll(apiSubscriptionId, true);
    }

    @Then("^Api subscription with id \"([^\"]*)\" is not among all api subscriptions$")
    public void apiSubscriptionWithIdIsNotAmongAllApiSubscriptions(String apiSubscriptionId) throws Throwable {
        apiSteps.apiSubscriptionInListOfAll(apiSubscriptionId, false);
    }

    @Then("^Api subscription with id \"([^\"]*)\" is active$")
    public void apiSubscriptionWithIdIsActive(String apiSubscriptionId) throws Throwable {
        apiSteps.apiSubscriptionActivity(apiSubscriptionId, true);
    }

    @Then("^Api subscription with id \"([^\"]*)\" is not active$")
    public void apiSubscriptionWithIdIsNotActive(String apiSubscriptionId) throws Throwable {
        apiSteps.apiSubscriptionActivity(apiSubscriptionId, false);
    }

    @Then("^There are \"([^\"]*)\" api subscriptions returned$")
    public void thereAreApiSubscriptionsReturned(Integer numberOfApiSubscriptions) throws Throwable {
        apiSteps.numberOfEntitiesInResponse(ApiSubscriptionDto.class, numberOfApiSubscriptions);
    }

    @Then("^There are api subscriptions with following IDs returned in order: \"([^\"]*)\"$")
    public void thereAreApiSubscriptionsWithFollowingIDsReturnedInOrder(List<String> order) throws Throwable {
        apiSteps.responceSortIs(order);
    }

    @Given("^API subscriptions exist for(?: default)? application(?: \"([^\"]*)\")? and customer (?:with id)? \"([^\"]*)\"(?: and property \"([^\"]*)\")?$")
    public void apiSubscriptionsExistForDefaultApplicationAndCustomerWithId(String applicationName, String customerId, String propertyCode) throws Throwable {
        String propertyId = ((propertyCode==null) ? DEFAULT_PROPERTY_ID : propertySteps.resolvePropertyId(propertyCode));
        String applicationId = applicationsSteps.resolveApplicationId(applicationName);
        CommercialSubscriptionDto commercialSubscription = new CommercialSubscriptionDto();
        commercialSubscription.setPropertyId(propertyId);
        commercialSubscription.setApplicationId(applicationId);
        commercialSubscription.setIsActive(true);
        commercialSubscription.setCustomerId(customerId);
        String commercialSubscriptionId = randomUUID().toString();
        commercialSubscription.setId(commercialSubscriptionId);
        commercialSubscriptionSteps.comSubscriptionsExists(singletonList(commercialSubscription));

        ApiSubscriptionDto apiSubscription = new ApiSubscriptionDto();
        apiSubscription.setCommercialSubscriptionId(commercialSubscriptionId);
        apiSubscription.setApplicationVersionId(DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        apiSubscription.setIsActive(true);
        apiSteps.followingApiSubscriptionExist(singletonList(apiSubscription));
    }
}
