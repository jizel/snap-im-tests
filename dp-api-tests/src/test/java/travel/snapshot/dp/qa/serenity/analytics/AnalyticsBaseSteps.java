package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class AnalyticsBaseSteps extends BasicSteps {


    public AnalyticsBaseSteps() {

    }

    //GET Requests

    @Step
    public void getDataWithoutProperty(String url) {
        Response response = given().spec(spec).parameter("access_token", "aaa").get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void getData(String url, String granularity, String propertyId, String since, String until) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);

        RequestSpecification requestSpecification = given().spec(spec);

        if (StringUtils.isNotBlank(propertyId)) {
            requestSpecification.header("x-property", propertyId);
        }
        if (StringUtils.isNotBlank(granularity)) {
            requestSpecification.parameter("granularity", granularity);
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
    public void getItems(String url, String propertyId, String limit, String cursor) {
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath(url)
                .header("x-property", propertyId)
                .parameter("access_token", "aaa");

        if (cursor != null) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null) {
            requestSpecification.parameter("limit", limit);
        }
        Response response = requestSpecification.when().get();
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    //Response Validation

    @Step
    public void responseContainsValues(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("values.size()", is(count));
    }

    @Step
    public void responseContainsValuesForAllMetrics(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        Map responseMap = response.as(Map.class);

        List<Map<String, Object>> metrics = (List<Map<String, Object>>) responseMap.get("data");
        metrics.forEach(m -> {
            List values = (List) m.get("values");
            assertEquals("Bad number of values for metric " + m.get("name"), count, values.size());
        });
    }

    @Step
    public void maximumNumberOfItemsInResponse(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("size()", lessThanOrEqualTo(count));
    }

    @Step
    public void referralsAreSorted(String metric, boolean ascending) {
        BiPredicate<Double, Double> direction = ascending ? (a, b) -> a <= b : (a, b) -> a >= b;
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        List<Double> values = response.body().jsonPath().getList("values." + metric, double.class);
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue("\nValue at index " + i + ": " + values.get(i) + "\n"
                            + "Value at index " + (i + 1) + ": " + values.get(i + 1),
                    direction.test(values.get(i), values.get(i + 1)));
        }
    }

    @Step
    public void valueIsLessThanOrEqualTo(String pathToValue1, String pathToValue2) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(pathToValue1, lessThanOrEqualTo(pathToValue2));
    }

    @Step
    public void dateFieldIs(String fieldName, String value) {
        LocalDate expectedDate = StringUtil.parseDate(value);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);

        LocalDate actualDate = LocalDate.parse(response.getBody().path(fieldName));
        assertEquals(expectedDate, actualDate);
    }

    //@Step
    //public void fieldContainsIntegerValue(String fieldName, int value) {
    //    Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
    //    response.then().body(fieldName, hasItem(value));
    //}


    public void responseContainsCorrectValuesFor(String granularity, String since, String until) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);


        int count = 0;
        LocalDate d = sinceDate;
        while (!d.isAfter(untilDate)) {
            switch (granularity) {
                case "":
                case "day": {
                    count++;
                    break;
                }
                case "week": {
                    if (DayOfWeek.SUNDAY.equals(d.getDayOfWeek())) {
                        count++;
                    }
                    break;
                }
                case "month": {
                    if (d.getDayOfMonth() == d.lengthOfMonth()) {
                        count++;
                    }
                    break;
                }
            }

            d = d.plusDays(1);
        }


        responseContainsValues(count);
    }
}
