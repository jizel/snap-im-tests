package travel.snapshot.dp.qa.steps.social_media.analytics;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;

import java.util.List;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class AnalyticsStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private AnalyticsSteps analyticsSteps;


    @When("^Property is missing for \"([^\"]*)\"$")
    public void property_is_missing_for(String url) throws Throwable {
        analyticsSteps.getDataWithoutProperty(url);
    }
    
    @When("^Access token is missing for \"([^\"]*)\"$")
    public void access_token_is_missing_for(String url) throws Throwable {
        analyticsSteps.getDataWithoutAccessToken(url);
    }

    @When("^Getting \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void getting_data_with_granularity_for_since_until(String url, String granularity, String property, String since, String until) throws Throwable {
    analyticsSteps.getData(url, granularity, property, since, until);
    }

    @Then("^Response contains (\\d+) values$")
    public void Response_contains_values(int count) throws Throwable {
        analyticsSteps.responseContainsValues(count);
    }

    @Then("^Response contains (\\d+) values for all metrics$")
    public void Response_contains_values_for_all_metrics(int count) throws Throwable {
        analyticsSteps.responseContainsValuesForAllMetrics(count);
    }
    
    @When("^List of \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter empty and sort empty$")
    public void List_of_items_is_got_with_limit_and_cursor_and_filter_empty_and_sort_empty(String url, String limit, String cursor) throws Throwable {
        analyticsSteps.getItems(url, limit, cursor);
    }
        
    @Then("^There are at most (\\d+) items returned$")
    public void There_are_at_most_items_returned(int count) throws Throwable {
        analyticsSteps.maximumNumberOfItemsInResponse(count);
    }

    @Then("^Response granularity is \"([^\"]*)\"$")
    public void Response_granularity_is(String granularity) throws Throwable {
        analyticsSteps.textFieldIs("granularity", granularity);
    }

    @Then("^Response since is \"([^\"]*)\"$")
    public void Response_since_is(String since) throws Throwable {
        analyticsSteps.dateFieldIs("since", since);
    }

    @Then("^Response until is \"([^\"]*)\"$")
    public void Response_until_is(String until) throws Throwable {
        analyticsSteps.dateFieldIs("until", until);
    }
    
    @Then("^Data is owned by \"([^\"]*)\"$")
    public void Data_is_owned_by(String data_owner) throws Throwable {
        analyticsSteps.fieldContains("data_owners", data_owner);
    }
}
