package travel.snapshot.dp.qa.steps;

import cucumber.api.Transform;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

import java.util.Map;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class CommonAnalyticsStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private AnalyticsBaseSteps steps;

    @Then("^Response contains (\\d+) values$")
    public void Response_contains_values(int count) throws Throwable {
        steps.responseContainsValues(count);
    }

    @Then("^Response contains no more than (\\d+) values$")
    public void Response_contains__no_more_than_values(int count) throws Throwable {
        steps.maximumNumberOfItemsInResponse(count);
    }

    @Then("^Response contains (\\d+) values for all metrics$")
    public void Response_contains_values_for_all_metrics(int count) throws Throwable {
        steps.responseContainsValuesForAllMetrics(count);
    }

    @Then("^\"([^\"]*)\" value is not more than \"([^\"]*)\" value$")
    public void value_is_no_more_than(String value1, String value2) throws Throwable {
        steps.valueIsLessThanOrEqualTo(value1, value2);
    }

    @Then("^The metric count is (\\d*[,.]?\\d*)")
    public void Metric_count_is(int value) throws Throwable {
        steps.bodyArrayContainsDouble("values", value);
    }

    @Then("^Response contains correct number of values for granularity \"([^\"]*)\" between \"([^\"]*)\" and \"([^\"]*)\"$")
    public void Response_contains_correct_number_of_values_for_granularity_between_and(String granularity, String since, String until) throws Throwable {
        steps.responseContainsCorrectValuesFor(granularity, since, until);
    }
}