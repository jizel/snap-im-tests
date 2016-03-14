package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class WebPerformanceSteps extends AnalyticsBaseSteps {


    public WebPerformanceSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(WEB_PERFORMANCE_BASE_URI));
    }

    // GET Requests
    // Special case for web performance

    @Step
    public void getData(String url, String granularity, String propertyId, String since, String until, String metric,
                        String direction) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);

        RequestSpecification requestSpecification = given().spec(spec);

        if (StringUtils.isNotBlank(propertyId)) {
            requestSpecification.header("x-property", propertyId);
        }
        if (StringUtils.isNotBlank(granularity)) {
            requestSpecification.parameter("granularity", granularity);
        }
        if (StringUtils.isNotBlank(metric)) {
            switch (direction) {
                case "ascending":
                    requestSpecification.parameter("sort", metric);
                    break;

                case "descending":
                    requestSpecification.parameter("sort_desc", metric);
                    break;
            }
        }
        if (sinceDate != null) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }
        if (untilDate != null) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }

        Response response = requestSpecification.when().get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void isoFormatCheckerinList(String path) {
        Response response = getSessionResponse();
        List<String> countries = response.body().jsonPath().getList(path);
        for (String c : countries) {
            assertTrue(c.matches("^[A-Z][A-Z]$"));
        }
    }

    @Step
    public void isoFormatChecker(String path) {
        Response response = getSessionResponse();
        String country = response.body().jsonPath().get(path);
        assertTrue(country.matches("^[A-Z][A-Z]$"));
    }
}
