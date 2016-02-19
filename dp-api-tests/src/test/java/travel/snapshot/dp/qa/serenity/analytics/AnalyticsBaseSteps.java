package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.commons.lang3.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
        super();
    }

    //GET Requests

    @Step
    public void getDataWithoutProperty(String url) {
        Response response = given().spec(spec).parameter("access_token", "aaa").get(url);
        setSessionResponse(response);
    }

    @Step
    //TODO try out test that have been affected (all social media)
    /**
     * returns entities for certain property for certain date granularity
     */
    public void getPropertiesWithDate(String url, String granularity, String propertyId, String since, String until) {
        Map<String, String> prepareParams = new HashMap<>();
        if (StringUtils.isNotBlank(propertyId)) {
            prepareParams.put("property", propertyId);
        }

        Response response = getEntitiesForURLWihDates(url, null, null, since, until, granularity, prepareParams);
        setSessionResponse(response);
    }

    @Step
    //TODO try out test that have been affected (all social media)
    /**
     * returns entities with paging for certain property
     */
    public void getPropertiesWithPaging(String url, String propertyId, String limit, String cursor) {
        Map<String, String> queryParams = new HashMap<>();
        if (StringUtils.isNotBlank(propertyId)) {
            queryParams.put("property", propertyId);
        }
        Response response_c = getEntities(url, limit, cursor, null, null, null, queryParams);
        setSessionResponse(response_c);
    }

    //Response Validation
    @Step
    public void responseContainsValues(int count) {
        Response response = getSessionResponse();
        response.then().body("values.size()", is(count));
    }

    @Step
    public void responseContainsValuesForAllMetrics(int count) {
        Response response = getSessionResponse();
        Map responseMap = response.as(Map.class);

        List<Map<String, Object>> metrics = (List<Map<String, Object>>) responseMap.get("data");
        metrics.forEach(m -> {
            List values = (List) m.get("values");
            assertEquals("Bad number of values for metric " + m.get("name"), count, values.size());
        });
    }

    @Step
    public void maximumNumberOfItemsInResponse(int count) {
        Response response = getSessionResponse();
        response.then().body("size()", lessThanOrEqualTo(count));
    }

    @Step
    public void referralsAreSorted(String metric, boolean ascending) {
        BiPredicate<Double, Double> direction = ascending ? (a, b) -> a <= b : (a, b) -> a >= b;
        Response response = getSessionResponse();
        List<Double> values = response.body().jsonPath().getList("values." + metric, double.class);
        for (int i = 0; i < values.size() - 1; i++) {
            assertTrue("\nValue at index " + i + ": " + values.get(i) + "\n"
                            + "Value at index " + (i + 1) + ": " + values.get(i + 1),
                    direction.test(values.get(i), values.get(i + 1)));
        }
    }

    @Step
    public void valueIsLessThanOrEqualTo(String pathToValue1, String pathToValue2) {
        Response response = getSessionResponse();
        response.then().body(pathToValue1, lessThanOrEqualTo(pathToValue2));
    }

    @Step
    public void dateFieldIs(String fieldName, String value) {
        LocalDate expectedDate = StringUtil.parseDate(value);
        Response response = getSessionResponse();

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
