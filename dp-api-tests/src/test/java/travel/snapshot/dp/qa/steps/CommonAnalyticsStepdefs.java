package travel.snapshot.dp.qa.steps;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import cucumber.api.java.en.Then;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;


/**
 * Created by sedlacek on 9/18/2015.
 */
public class CommonAnalyticsStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private AnalyticsBaseSteps analyticsBaseSteps;

    @Then("^Response contains (\\d+) values$")
    public void Response_contains_values(int count) throws Throwable {
        analyticsBaseSteps.responseContainsValues(count);
    }

    @Then("^Response contains no more than (\\d+) values$")
    public void Response_contains__no_more_than_values(int count) throws Throwable {
        analyticsBaseSteps.maximumNumberOfItemsInResponse(count);
    }

    @Then("^Response contains (\\d+) values for all metrics$")
    public void Response_contains_values_for_all_metrics(int count) throws Throwable {
        analyticsBaseSteps.responseContainsValuesForAllMetrics(count);
    }

    @Then("^The metric count is (\\d+[,.]?\\d*)$")
    public void Metric_count_is(int value) throws Throwable {
        analyticsBaseSteps.integerPartOfValueIs("values", value);
    }

    @Then("^Response contains correct number of values for granularity \"([^\"]*)\" between \"([^\"]*)\" and \"([^\"]*)\"$")
    public void Response_contains_correct_number_of_values_for_granularity_between_and(String granularity, String since, String until) throws Throwable {
        analyticsBaseSteps.responseContainsCorrectValuesFor(granularity, since, until);
    }
}
