package travel.snapshot.dp.qa.serenity.analytics;

import static com.jayway.restassured.RestAssured.given;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class WebPerformanceSteps extends AnalyticsBaseSteps {


    public WebPerformanceSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(WEB_PERFORMANCE_BASE_URI));
    }

    //GET Requests
    
    //Special case for web performance
    @Step
    public void getData(String url, String granularity, String propertyId, String since, String until, String metric, String direction) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);

        RequestSpecification requestSpecification = given().spec(spec)
                .parameter("access_token", "aaa");

        if (StringUtils.isNotBlank(propertyId)) {
            requestSpecification.header("x-property", propertyId);
        }
        if (StringUtils.isNotBlank(granularity)) {
            requestSpecification.parameter("granularity", granularity);
        }
        if (StringUtils.isNotBlank(metric)) {
            if(direction.equals("ascending"))requestSpecification.parameter("sort", metric);
            if(direction.equals("descending"))requestSpecification.parameter("sort_desc", metric);
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

}
