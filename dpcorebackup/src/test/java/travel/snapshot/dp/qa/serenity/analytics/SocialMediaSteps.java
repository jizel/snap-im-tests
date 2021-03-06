package travel.snapshot.dp.qa.serenity.analytics;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;

import travel.snapshot.dp.qa.helpers.StringUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class SocialMediaSteps extends AnalyticsBaseSteps {


    public SocialMediaSteps() {
        super();
        spec.baseUri(propertiesHelper.getProperty(SOCIAL_MEDIA_BASE_URI));
    }

    @Step
    public void verifySumOfMetricFromSocialMedia(String metric, String granularity, String property, String since, String until) {
        RequestSpecification requestSpecification = given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        List<Integer> facebookValues = requestSpecification
                .baseUri(propertiesHelper.getProperty(FACEBOOK_BASE_URI))
                .header("x-property", property)
                .param("granularity", granularity)
                .param("since", since)
                .param("until", until)
                .get("/social_media/analytics/facebook/{metric}", metric).jsonPath().getList("values.value");
        int facebookSum = facebookValues.stream().mapToInt(i -> i).sum();

        List<Integer> twitterValues = requestSpecification
                .baseUri(propertiesHelper.getProperty(TWITTER_BASE_URI))
                .header("x-property", property)
                .param("granularity", granularity)
                .param("since", since)
                .param("until", until)
                .get("/social_media/analytics/twitter/{metric}", metric).jsonPath().getList("values.value");
        int twitterSum = twitterValues.stream().mapToInt(i -> i).sum();

        List<Integer> instagramValues = requestSpecification
                .baseUri(propertiesHelper.getProperty(INSTAGRAM_BASE_URI))
                .header("x-property", property)
                .param("granularity", granularity)
                .param("since", since)
                .param("until", until)
                .get("/social_media/analytics/instagram/{metric}", metric).jsonPath().getList("values.value");
        int instagramSum = instagramValues.stream().mapToInt(i -> i).sum();

        List<Integer> totalValues = requestSpecification
                .baseUri(propertiesHelper.getProperty(SOCIAL_MEDIA_BASE_URI))
                .header("x-property", property)
                .param("granularity", granularity)
                .param("since", since)
                .param("until", until)
                .get("/social_media/analytics/{metric}", metric).jsonPath().getList("values.value");
        int totalSum = totalValues.stream().mapToInt(i -> i).sum();

        assertEquals(facebookSum + twitterSum + instagramSum, totalSum);
    }

    @Step
    public void getPropertiesWithDate(String url, String granularity, UUID propertyId, String since, String until) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);

        RequestSpecification requestSpecification = given().spec(spec)
                .header("x-property", propertyId)
                .header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);

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
        setSessionResponse(response);
    }


    @Step
    public void getPropertiesWithPaging(String url, UUID propertyId, String limit, String cursor) {
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath(url)
                .header("x-property", propertyId)
                .header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
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
            Map<String, Object> metric = (Map<String, Object>) m.entrySet().iterator().next().getValue();
            List values = (List) metric.get("values");
            assertEquals("Bad number of values for metric " + metric.get("name"), count, values.size());
        });
    }

    @Step
    public void maximumNumberOfItemsInResponse(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("size()", lessThanOrEqualTo(count));
    }

    @Step
    public void valuesAreLessThanOrEqualTo(String pathToLowerValues, String pathToHigherValues) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);

        List<Float> lowerValues = response.jsonPath().getList(pathToLowerValues, float.class);
        List<Float> higherValues = response.jsonPath().getList(pathToHigherValues, float.class);
        double sumLowValues = lowerValues.stream().mapToDouble(i -> i).sum();
        double sumHighValues = higherValues.stream().mapToDouble(i -> i).sum();

        assertThat(sumLowValues, lessThanOrEqualTo(sumHighValues));
    }

    @Step
    public void validateMinAvgMax(String pathToValue1, String pathToValue2) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(pathToValue1, lessThanOrEqualTo(response.body().path(pathToValue2)));
    }

    @Step
    public void dateFieldIs(String fieldName, String value) {
        LocalDate expectedDate = StringUtil.parseDate(value);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);

        LocalDate actualDate = LocalDate.parse(response.getBody().path(fieldName));
        assertEquals(expectedDate, actualDate);
    }

    /**
     * This method trims dates to start/end of week/month depending on parameter and granularity
     * week - since must be monday, until must be sunday month - since must be first of month, until
     * must be last of month
     *
     * @param fieldName   - since/until
     * @param value       - 20150101/today
     * @param granularity - granularity for date interval, day/week/month
     */
    public void dateFieldIs(String fieldName, String value, String granularity) {
        LocalDate expectedDate = StringUtil.parseDate(value);

        //calculation for until
        if (fieldName.equals("until")) {
            //calculation for week; if not end of the week, go back(minus) to last sunday
            if (expectedDate.getDayOfWeek() != DayOfWeek.SUNDAY && granularity.equals("week")) {
                expectedDate = expectedDate.minusDays(expectedDate.getDayOfWeek().getValue());
            }
            //calculation for month; if not end of the month, go back(minus) to last ending of month
            if (expectedDate.getDayOfMonth() != LocalDate.parse(expectedDate.toString()).lengthOfMonth() && granularity.equals("month")) {
                expectedDate = expectedDate.minusDays(expectedDate.getDayOfMonth());
            }
        }

        //calculation for since
        if (fieldName.equals("since")) {
            //calculation for week; if not start of week go to future(plus) to start of the week
            if (expectedDate.getDayOfWeek() != DayOfWeek.MONDAY && granularity.equals("week")) {
                expectedDate = expectedDate.plusDays(7 - expectedDate.getDayOfWeek().getValue() + 1);
            }
            //calculation for month; if not start of month go into future(plus) to start of next month
            if (expectedDate.getDayOfMonth() != 1 && granularity.equals("month")) {
                expectedDate = expectedDate.plusDays(LocalDate.parse(expectedDate.toString()).lengthOfMonth());
                expectedDate = expectedDate.minusDays(expectedDate.getDayOfMonth() - 1);
            }
        }
        dateFieldIs(fieldName, expectedDate.toString());
    }
}
