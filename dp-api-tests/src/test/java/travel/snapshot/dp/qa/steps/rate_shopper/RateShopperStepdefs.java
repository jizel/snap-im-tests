package travel.snapshot.dp.qa.steps.rate_shopper;

import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.serenity.analytics.RateShopperSteps;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class RateShopperStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private RateShopperSteps rateShopperSteps;

    @When("^Getting rate data with for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void getting_rate_data_with_for_since_until(String property_id, String since, String until) throws Throwable {
        rateShopperSteps.getPropertyRateData(property_id, since, until);
    }
}
