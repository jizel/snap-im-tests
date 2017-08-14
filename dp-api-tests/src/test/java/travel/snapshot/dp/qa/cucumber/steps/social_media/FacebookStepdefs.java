package travel.snapshot.dp.qa.cucumber.steps.social_media;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

import cucumber.api.Transform;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.analytics.model.GlobalStatsDto;
import travel.snapshot.dp.qa.cucumber.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.cucumber.serenity.analytics.FacebookSteps;

import java.util.UUID;


/**
 * Created by sedlacek on 9/18/2015.
 */
public class FacebookStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private FacebookSteps facebookSteps;

    @When("^Get facebook \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void Get_social_media_data_with_granularity_for_since_until(String url, String granularity, UUID propertyId, String since, String until) throws Throwable {
        // Express the Regexp above with the code you wish you had
        facebookSteps.getPropertiesWithDate("/social_media" + url, granularity, propertyId, since, until);
    }

    @When("^Get facebook \"([^\"]*)\" with missing property header$")
    public void Get_social_media_with_missing_property_header(String url) throws Throwable {
        facebookSteps.getPropertiesWithDate("/social_media" + url, "day", null, null, null);
    }

    @When("^List of facebook items \"([^\"]*)\" for property id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void List_of_items_is_got_with_limit_and_cursor(String url,
                                                           @Transform(NullEmptyStringConverter.class) String propertyId,
                                                           @Transform(NullEmptyStringConverter.class) String limit,
                                                           @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        facebookSteps.getPropertiesWithPaging("/social_media" + url, propertyId, limit, cursor);
    }

    @Then("^Response contains (\\d+) amount of values for global stats dto$")
    public void responseContainsCountValuesOfGlobaStatsDto(int count) throws Throwable {
        facebookSteps.checkAnalyticsReturnedForType(
                t -> assertThat(t.getData(), everyItem(hasProperty("values", hasSize(count)))),
                GlobalStatsDto.class);
    }

    @Then("^Facebook posts contains datetime \"([^\"]*)\" engagement \"([^\"]*)\" content \"([^\"]*)\" and reach \"([^\"]*)\"$")
    public void facebookPostsContainsDatetimeEngagementContentAndReach(String datetime, int engagement, String content, int reach) throws Throwable {
        facebookSteps.checkFacebookPostFromResponse(datetime, engagement, content, reach);
    }
}
