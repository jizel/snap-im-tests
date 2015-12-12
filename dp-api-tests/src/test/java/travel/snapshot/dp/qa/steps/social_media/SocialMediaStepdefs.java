package travel.snapshot.dp.qa.steps.social_media;

import cucumber.api.Transform;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.analytics.SocialMediaSteps;

import java.util.Map;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class SocialMediaStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private SocialMediaSteps steps;


    @When("^Property is missing for \"([^\"]*)\"$")
    public void property_is_missing_for(String url) throws Throwable {
        steps.getDataWithoutProperty(url);
    }

    @When("^Getting \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void getting_data_with_granularity_for_since_until(String url, String granularity, String property, String since, String until) throws Throwable {
        steps.getData(url, granularity, property, since, until);
    }

    @Then("^Response contains (\\d+) values$")
    public void Response_contains_values(int count) throws Throwable {
        steps.responseContainsValues(count);
    }
    
    @Then("^Response contains no more than (\\d+) values$")
    public void Response_contains__no_more_than_values(int count) throws Throwable {
        steps.maximumNumberOfItemsInResponse(count);
    }
    
    @Then("^Response contains \"([^\"]*)\" values$")
    public void Response_contains_values(String count) throws Throwable {
        steps.responseContainsStringValues(count);
    }
    
    @Then("^Response contains \"([^\"]*)\"  values for all metrics$")
    public void response_contains_values_for_all_metrics(int count) throws Throwable {
        steps.responseContainsValues(count);
    }

    @Then("^Response contains (\\d+) values for all metrics$")
    public void Response_contains_values_for_all_metrics(int count) throws Throwable {
        steps.responseContainsValuesForAllMetrics(count);
    }

     
    @When("^List of \"([^\"]*)\" for property id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void List_of_items_is_got_with_limit_and_cursor(String url, String propertyId,
                                                           @Transform(NullEmptyStringConverter.class) String limit,
                                                           @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        steps.getItems(url, propertyId, limit, cursor);
    }  
    
    //@When("^List of \"([^\"]*)\" for property id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    //public void List_of_items_is_got_with_limit_and_cursor(String url, String propertyId,
    //                                                       @Transform(NullEmptyStringConverter.class) String limit,
    //                                                       @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
    //    steps.getItems(url, propertyId, limit, cursor);
    //} 

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

    @Then("^The metric count is \"([^\"]*)\"$")
    public void Metric_count_is(int values) throws Throwable {
        steps.fieldContainsIntegerValue("values", values);
    }

    @Then("^Response contains correct number of values for granularity \"([^\"]*)\" between \"([^\"]*)\" and \"([^\"]*)\"$")
    public void Response_contains_correct_number_of_values_for_granularity_between_and(String granularity, String since, String until) throws Throwable {
        steps.responseContainsCorrectValuesFor(granularity, since, until);
    }
}
