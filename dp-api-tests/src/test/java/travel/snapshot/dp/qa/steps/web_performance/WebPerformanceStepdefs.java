package travel.snapshot.dp.qa.steps.web_performance;

import cucumber.api.Transform;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;

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
    public void Get_web_performance_data_with_granularity_for_since_until(String url, String granularity, String propertyId, String since, String until) throws Throwable {
        steps.getData("/web_performance" + url, granularity, propertyId, since, until);
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
    }

