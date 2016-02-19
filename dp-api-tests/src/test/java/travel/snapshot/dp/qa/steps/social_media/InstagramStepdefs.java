package travel.snapshot.dp.qa.steps.social_media;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.serenity.analytics.InstagramSteps;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class InstagramStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private InstagramSteps steps;

    @When("^Get instagram \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void Get_social_media_data_with_granularity_for_since_until(String url, String granularity, String propertyId, String since, String until) throws Throwable {
        // Express the Regexp above with the code you wish you had
        steps.getData("/social_media" + url, granularity, propertyId, since, until);
    }

    @When("^Get instagram \"([^\"]*)\" with missing property header$")
    public void Get_social_media_with_missing_property_header(String url) throws Throwable {
        steps.getData("/social_media" + url, "day", null, null, null);
    }

}
