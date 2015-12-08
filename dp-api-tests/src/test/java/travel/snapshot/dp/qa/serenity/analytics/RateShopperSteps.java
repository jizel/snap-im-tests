package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jayway.restassured.RestAssured.given;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class RateShopperSteps extends BasicSteps {


    public RateShopperSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(RATE_SHOPPER_BASE_URI));
    }

    //GET Requests

    @Step
    public void getPropertyRateData(String property_id, String since, String until) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(since);

        RequestSpecification requestSpecification = given().spec(spec)
                .parameter("access_token", "aaa");

        if (since != null && !"".equals(since)) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }

        if (until != null && !"".equals(until)) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }

        Response response = requestSpecification.when().get("rate_shopper/analytics/property/" + property_id);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

}
