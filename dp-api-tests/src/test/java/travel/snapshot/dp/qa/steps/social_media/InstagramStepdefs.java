package travel.snapshot.dp.qa.steps.social_media;


import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import java.util.List;

import cucumber.api.Transform;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.analytics.InstagramSteps;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class InstagramStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private InstagramSteps instagramSteps;

    @When("^Get instagram \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void Get_social_media_data_with_granularity_for_since_until(String url,
                                                                       @Transform(NullEmptyStringConverter.class) String granularity,
                                                                       @Transform(NullEmptyStringConverter.class) String propertyId,
                                                                       @Transform(NullEmptyStringConverter.class) String since,
                                                                       @Transform(NullEmptyStringConverter.class) String until) throws Throwable {
        instagramSteps.getPropertiesWithDate("/social_media" + url, granularity, propertyId, since, until);
    }

    @When("^Get instagram \"([^\"]*)\" with missing property header$")
    public void Get_social_media_with_missing_property_header(String url) throws Throwable {
        instagramSteps.getPropertiesWithDate("/social_media" + url, "day", null, null, null);
    }

    @Then("^Response contains \"([^\"]*)\" values of global stats dto$")
    public void responseContainsCountValuesOfGlobalStatsDto(List<Integer> count) throws Throwable {
        instagramSteps.checkListofValuesFromResponse(count);
    }
}
