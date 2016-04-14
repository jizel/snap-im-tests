package travel.snapshot.dp.qa.steps.social_media;

import net.thucydides.core.annotations.Steps;

import java.util.Arrays;

import cucumber.api.Transform;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.helpers.ClassStringConverter;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.analytics.TwitterSteps;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class TwitterStepdefs {

    @Steps
    private TwitterSteps steps;

    @When("^Get twitter \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void Get_social_media_data_with_granularity_for_since_until(String url, String granularity, String propertyId, String since, String until) throws Throwable {
        // Express the Regexp above with the code you wish you had
        steps.getPropertiesWithDate("/social_media" + url, granularity, propertyId, since, until);
    }

    @When("^Get twitter \"([^\"]*)\" with missing property header$")
    public void Get_social_media_with_missing_property_header(String url) throws Throwable {
        steps.getPropertiesWithDate("/social_media" + url, "day", null, null, null);
    }

    @When("^List of twitter items \"([^\"]*)\" for property id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void list_of_twitter_items_for_property_id_is_got_with_limit_and_cursor(String url,
                                                                                   String propertyId,
                                                                                   @Transform(NullEmptyStringConverter.class) String limit,
                                                                                   @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        steps.getTwitterTweets("/social_media" + url, limit, cursor, null, null, propertyId);
    }

    @When("^List of twitter items \"([^\"]*)\" for property id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and sort by \"([^\"]*)\" ascendingly$")
    public void list_of_twitter_items_for_property_id_is_got_with_limit_and_cursor_and_sort_ascendingly(String url, String propertyId,
                                                                                                        @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                        @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                        String sort) throws Throwable {
        steps.getTwitterTweets("/social_media" + url, limit, cursor, sort, null, propertyId);
    }

    @When("^List of twitter items \"([^\"]*)\" for property id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and sort by \"([^\"]*)\" descendingly$")
    public void list_of_twitter_items_for_property_id_is_got_with_limit_and_cursor_and_sort_descendingly(String url, String propertyId,
                                                                                                         @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                         @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                         String sort) throws Throwable {
        steps.getTwitterTweets("/social_media" + url, limit, cursor, null, sort, propertyId);
    }

    @Then("^All Twitter Tweets data are owned by \"([^\"]*)\"$")
    public void all_twitter_tweets_data_are_owned_by(String owner) {
        steps.responseContainsObjectsAllWithPropertyAndValues("data_owners", Arrays.asList(new String[]{owner}));
    }

    @Then("^Records are sorted ascendingly by \"([^\"]*)\" of type \"([^\"]*)\"$")
    public void recordsAreSortedAscendinglyByOfType(String property, @Transform(ClassStringConverter.class) Class<?> type) throws Throwable {
        steps.listOfObjectsAreSortedAccordingToProperty(property, true, type);
    }

    @Then("^Records are sorted descendingly by \"([^\"]*)\" of type \"([^\"]*)\"$")
    public void recordsAreSortedDecendinglyByOfType(String property, @Transform(ClassStringConverter.class) Class<?> type) throws Throwable {
        steps.listOfObjectsAreSortedAccordingToProperty(property, false, type);
    }

    @Then("^There are (\\d+) Twitter posts returned$")
    public void There_are_count_posts_returned(int count) throws Throwable {
        steps.numberOfTwitterPostsInResponse(count);
    }

}
