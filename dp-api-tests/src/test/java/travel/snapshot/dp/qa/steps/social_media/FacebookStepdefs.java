package travel.snapshot.dp.qa.steps.social_media;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import cucumber.api.Transform;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.analytics.FacebookSteps;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class FacebookStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private FacebookSteps steps;

    @When("^Get facebook \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void Get_social_media_data_with_granularity_for_since_until(String url, String granularity, String propertyId, String since, String until) throws Throwable {
        // Express the Regexp above with the code you wish you had
        steps.getData("/social_media" + url, granularity, propertyId, since, until);
    }

    @When("^Get facebook \"([^\"]*)\" with missing property header$")
    public void Get_social_media_with_missing_property_header(String url) throws Throwable {
        steps.getData("/social_media" + url, "day", null, null, null);
    }

    @When("^List of facebook items \"([^\"]*)\" for property id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void List_of_items_is_got_with_limit_and_cursor(String url, String propertyId,
                                                           @Transform(NullEmptyStringConverter.class) String limit,
                                                           @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        steps.getItems("/social_media" + url, propertyId, limit, cursor);
    }

}
