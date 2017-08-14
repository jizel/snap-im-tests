package travel.snapshot.dp.qa.cucumber.steps.rate_shopper;

import cucumber.api.Transform;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.cucumber.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.cucumber.serenity.analytics.RateShopperSteps;

import java.util.UUID;

public class RateShopperStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private RateShopperSteps steps;

    @When("^Sending an empty request to \"([^\"]*)\"$")
    public void sending_an_empty_request_to(String url) throws Throwable {
        steps.emptyGetRequest(url);
    }

    @When("^Getting rate data for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\" fetched \"([^\"]*)\"$")
    public void getting_rate_data_for_since_until(UUID property_id,
                                                  @Transform(NullEmptyStringConverter.class) String since,
                                                  @Transform(NullEmptyStringConverter.class) String until,
                                                  @Transform(NullEmptyStringConverter.class) String fetched) {
        steps.getPropertyRateData(property_id, since, until, fetched);
    }

    @When("^Getting BAR values for a given market for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void getting_market_rate_data_for_since_until(@Transform(NullEmptyStringConverter.class) UUID property_id,
                                                         @Transform(NullEmptyStringConverter.class) String since,
                                                         @Transform(NullEmptyStringConverter.class) String until) {
        steps.getMarketRateData(property_id, since, until);
    }

    @When("^List of properties for market of \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void List_of_items_is_got_with_limit_and_cursor(@Transform(NullEmptyStringConverter.class) UUID propertyId,
                                                           @Transform(NullEmptyStringConverter.class) String limit,
                                                           @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        steps.getProperties(propertyId, limit, cursor, null);
    }

    @Then("^Response \"([^\"]*)\" for property \"([^\"]*)\" is \"([^\"]*)\"$")
    public void response_since_for_property(String fieldName, UUID propertyId, String value) {
        steps.dateFieldForProperty(fieldName, propertyId, value);
    }

    @When("^List of properties for market of \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" fetched \"([^\"]*)\"$")
    public void listOfPropertiesForMarketOfIsGotWithLimitAndCursorFetched(UUID propertyId,
                                                                          @Transform(NullEmptyStringConverter.class) String limit,
                                                                          @Transform(NullEmptyStringConverter.class) String cursor,
                                                                          @Transform(NullEmptyStringConverter.class) String fetchDateTime) throws Throwable {
        steps.getProperties(propertyId, limit, cursor, fetchDateTime);
    }

    @Then("^Response contains (\\d+) properties$")
    public void responseContainsCountProperties(int count) throws Throwable {
        steps.responseContainsValues(count);
    }
}
