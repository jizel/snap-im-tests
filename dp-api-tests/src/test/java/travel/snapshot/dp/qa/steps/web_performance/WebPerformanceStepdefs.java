package travel.snapshot.dp.qa.steps.web_performance;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import cucumber.api.Transform;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.analytics.WebPerformanceSteps;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class WebPerformanceStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private WebPerformanceSteps steps;


    @When("^Get web_performance \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void Get_web_performance_data_with_granularity_for_since_until(String url, String granularity,
                                                                          String propertyId, String since, String until) throws Throwable {
        steps.getData("/web_performance" + url, granularity, propertyId, since, until);
    }

    @When("^Get web performance referrals with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\" sorted by \"([^\"]*)\" \"([^\"]*)\"$")
    public void Get_web_performance_referrals_sorted(String granularity, String propertyId, String since, String until,
                                                     String metric, String direction) throws Throwable {
        steps.getData("/web_performance/analytics/referrals", granularity, propertyId, since, until, metric, direction);
    }

    @When("^Get web_performance \"([^\"]*)\" with missing property header$")
    public void Get_web_performance_with_missing_property_header(String url) throws Throwable {
        steps.getData("/web_performance" + url, "day", null, null, null);

    }

    @When("^List of web performance \"([^\"]*)\" for property id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void list_of_web_performance_for_property_id_is_got_with_limit_and_cursor(String url, String propertyId,
                                                                                     @Transform(NullEmptyStringConverter.class) String limit,
                                                                                     @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        steps.getItems("/web_performance" + url, propertyId, limit, cursor);
    }

    @Then("^Values are sorted by \"([^\"]*)\" in \"([^\"]*)\"$")
    public void values_are_sorted_by_in(String metric, String direction) throws Throwable {
        steps.referralsAreSorted(metric, direction.equals("ascending") ? true : false);
    }

    @Then("^All web performance analytics metrics country codes in \"([^\"]*)\" are in ISO format$")
    public void all_country_codes_are_in_ISO_format(String jsonString) throws Throwable {
        steps.isoFormatCheckerinList(jsonString);
    }

    @Then("^All web performance analytics country codes in \"([^\"]*)\" are in ISO format$")
    public void all_web_performance_analytics_country_codes_in_are_in_ISO_format(String jsonString) throws Throwable {
        steps.isoFormatChecker(jsonString);
    }

}

