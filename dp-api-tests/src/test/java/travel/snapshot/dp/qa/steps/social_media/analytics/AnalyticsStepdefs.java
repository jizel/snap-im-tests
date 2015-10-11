package travel.snapshot.dp.qa.steps.social_media.analytics;

import cucumber.api.PendingException;
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
}