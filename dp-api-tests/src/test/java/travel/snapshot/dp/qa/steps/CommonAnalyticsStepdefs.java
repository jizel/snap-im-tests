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

    @Then("^There are at most (\\d+) items returned$")
    public void There_are_at_most_items_returned(int count) throws Throwable {
        steps.maximumNumberOfItemsInResponse(count);
    }

    @Then("^\"([^\"]*)\" value is not more than \"([^\"]*)\" value$")
    public void value_is_no_more_than(String value1, String value2) throws Throwable {
        steps.valueIsLessThanOrEqualTo(value1, value2);
    }

    @Then("^Response granularity is \"([^\"]*)\"$")
    public void Response_granularity_is(String granularity) throws Throwable {
        steps.bodyContainsEntityWith("granularity", granularity);
    }

    @Then("^Response since is \"([^\"]*)\"$")
    public void Response_since_is(String since) throws Throwable {
        steps.dateFieldIs("since", since);
    }

    @Then("^Response until is \"([^\"]*)\"$")
    public void Response_until_is(String until) throws Throwable {
        steps.dateFieldIs("until", until);
    }

    @Then("^Data is owned by \"([^\"]*)\"$")
    public void Data_is_owned_by(String data_owner) throws Throwable {
        steps.bodyContainsCollectionWith("data_owners", data_owner);
    }

    @Then("^There are (\\d+) posts returned$")
    public void There_are_count_posts_returned(int count) throws Throwable {
        steps.numberOfEntitiesInResponse(Map.class, count);
    }

    @Then("^The metric count is (\\d+[,.]?\\d*)$")
    public void Metric_count_is(int value) throws Throwable {
        steps.integerPartOfValueIs("values", value);
    }

    @Then("^Response contains correct number of values for granularity \"([^\"]*)\" between \"([^\"]*)\" and \"([^\"]*)\"$")
    public void Response_contains_correct_number_of_values_for_granularity_between_and(String granularity, String since, String until) throws Throwable {
        steps.responseContainsCorrectValuesFor(granularity, since, until);
    }
}
