package travel.snapshot.dp.qa.steps.identity.api_subscriptions;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import java.util.List;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.api.identity.subscription.model.ApiSubscriptionDto;
import travel.snapshot.dp.qa.serenity.api_subscriptions.ApiSubscriptionSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;

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


    @Given("^The following api subscriptions exist$")
    public void theFollowingApiSubscriptionsExist(List<ApiSubscriptionDto> apis) throws Throwable {
        apiSteps.followingApiSubscriptionExist(apis);
    }

    @When("^Api subscription with id \"([^\"]*)\" is got$")
    public void apiSubscriptionWithIdIsGot(String apiSubscriptionId) throws Throwable {
        apiSteps.apiWithIdIsGot(apiSubscriptionId);
    }

    @When("^Api subscription with id \"([^\"]*)\" is got with etag$")
    public void apiSubscriptionWithIdIsGotWithEtag(String apiSubscriptionId) throws Throwable {
        apiSteps.apiWithIdIsGotWithEtag(apiSubscriptionId);
    }

    @When("^Trying to create second api subscription with the same versionID$")
    public void tryingToCreateSecondApiSubscriptionWithTheSameVersionID() throws Throwable {
        apiSteps.createExistingApiSubscription();
    }

    @When("^Api subscription with id \"([^\"]*)\" is activated$")
    public void apiSubscriptionWithIdIsActivated(String apiSubscriptionId) throws Throwable {
        apiSteps.activateApiSubscription(apiSubscriptionId);
    }

    @When("^Api subscription with id \"([^\"]*)\" is deactivated$")
    public void apiSubscriptionWithIdIsDeactivated(String apiSubscriptionId) throws Throwable {
        apiSteps.deactivateApiSubscription(apiSubscriptionId);
    }

    @Then("^Api subscription with id \"([^\"]*)\" is among all api subscriptions$")
    public void apiSubscriptionWithIdIsAmongAllApiSubscriptions(String apiSubscriptionId) throws Throwable {
        apiSteps.apiSubscriptionInListOfAll(apiSubscriptionId, true);
    }

    @Then("^Api subscription with id \"([^\"]*)\" is not among all api subscriptions$")
    public void apiSubscriptionWithIdIsNotAmongAllApiSubscriptions(String apiSubscriptionId) throws Throwable {
        apiSteps.apiSubscriptionInListOfAll(apiSubscriptionId, false);
    }
}
