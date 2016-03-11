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

    @When("^Verifying sum of \"([^\"]*)\" from Facebook, Twitter, and Instagram with \"([^\"]*)\" granularity for property \"([^\"]*)\", since \"([^\"]*)\", until \"([^\"]*)\"$")
    public void getting_sum_of_from_Facebook_Twitter_and_Instagram_with_for_property_since_until(String metric, String granularity, String property, String since, String until) {
        steps.verifySumOfMetricFromSocialMedia(metric, granularity, property, since, until);
    }

    @When("^Get social media \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void Get_social_media_data_with_granularity_for_since_until(String url, String granularity, String propertyId, String since, String until) throws Throwable {
        // Express the Regexp above with the code you wish you had
        steps.getPropertiesWithDate("/social_media" + url, granularity, propertyId, since, until);

    }

    @When("^Get social_media \"([^\"]*)\" with missing property header$")
    public void Get_social_media_with_missing_property_header(String url) throws Throwable {
        steps.getPropertiesWithDate("/social_media" + url, "day", null, null, null);
    }

    @When("^Property is missing for \"([^\"]*)\"$")
    public void property_is_missing_for(String url) throws Throwable {
        steps.getDataWithoutProperty(url);
    }

    @When("^List of social media items \"([^\"]*)\" for property id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void List_of_items_is_got_with_limit_and_cursor(String url, String propertyId,
                                                           @Transform(NullEmptyStringConverter.class) String limit,
                                                           @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        steps.getPropertiesWithPaging("/social_media" + url, propertyId, limit, cursor);
    }

    @Then("^There are at most (\\d+) items returned$")
    public void There_are_at_most_items_returned(int count) throws Throwable {
        steps.maximumNumberOfItemsInResponse(count);
    }

    @Then("^\"([^\"]*)\" values are not more than \"([^\"]*)\" values$")
    public void value_is_no_more_than(String lowerValues, String higherValues) throws Throwable {
        steps.valuesAreLessThanOrEqualTo("values." + lowerValues, "values." + higherValues);
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

    @Then("^Response since is \"([^\"]*)\" for granularity \"([^\"]*)\"$")
    public void responseSinceIsForGranularity(String value, String granularity) throws Throwable {
        steps.dateFieldIs("since", value, granularity);
    }

    @Then("^Response until is \"([^\"]*)\" for granularity \"([^\"]*)\"$")
    public void responseUntilIsForGranularity(String value, String granularity) throws Throwable {
        steps.dateFieldIs("until", value, granularity);
    }
}
