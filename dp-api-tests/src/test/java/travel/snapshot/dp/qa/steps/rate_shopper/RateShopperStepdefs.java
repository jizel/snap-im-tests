package travel.snapshot.dp.qa.steps.rate_shopper;

import cucumber.api.Transform;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;

import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.analytics.RateShopperSteps;

public class RateShopperStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private RateShopperSteps steps;

    @When("^Getting rate data for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\" fetched \"([^\"]*)\"$")
    public void getting_rate_data_for_since_until(String property_id, String since, String until, String fetched) {
        steps.getPropertyRateData(property_id, since, until, fetched);
    }
    
    @When("^Getting BAR values for a given market for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void getting_market_rate_data_for_since_until(String property_id, String since, String until) {
       steps.getMarketRateData(property_id, since, until);
    }
}
