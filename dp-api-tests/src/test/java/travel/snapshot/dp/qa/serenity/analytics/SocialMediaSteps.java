package travel.snapshot.dp.qa.serenity.analytics;

import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class SocialMediaSteps extends AnalyticsBaseSteps {


    public SocialMediaSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(SOCIAL_MEDIA_BASE_URI));
    }

    @Step
    public void verifySumOfMetricFromSocialMedia(String metric, String granularity, String property, String since, String until) {
        List<Integer> facebookValues = given().spec(spec)
                .header("x-property", property)
                .param("granularity", granularity)
                .param("since", since)
                .param("until", until)
                .get("/social_media/analytics/facebook/{metric}", metric).jsonPath().getList("values");
        int facebookSum = facebookValues.stream().mapToInt(i -> i).sum();

        List<Integer> twitterValues = given().spec(spec)
                .header("x-property", property)
                .param("granularity", granularity)
                .param("since", since)
                .param("until", until)
                .get("/social_media/analytics/twitter/{metric}", metric).jsonPath().getList("values");
        int twitterSum = twitterValues.stream().mapToInt(i -> i).sum();

        List<Integer> instagramValues = given().spec(spec)
                .header("x-property", property)
                .param("granularity", granularity)
                .param("since", since)
                .param("until", until)
                .get("/social_media/analytics/instagram/{metric}", metric).jsonPath().getList("values");
        int instagramSum = instagramValues.stream().mapToInt(i -> i).sum();

        List<Integer> totalValues = given().spec(spec)
                .header("x-property", property)
                .param("granularity", granularity)
                .param("since", since)
                .param("until", until)
                .get("/social_media/analytics/{metric}", metric).jsonPath().getList("values");
        int totalSum = totalValues.stream().mapToInt(i -> i).sum();

        assertEquals(facebookSum + twitterSum + instagramSum, totalSum);
    }
<<<<<<< Upstream, based on origin/master
=======

    @Step
    public void getData(String url, String granularity, String propertyId, String since, String until) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);

        RequestSpecification requestSpecification = given().spec(spec)
                .header("x-property", propertyId).parameter("access_token", "aaa");

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

        List<Map<String,Object>> metrics = (List<Map<String, Object>>) responseMap.get("data");
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


>>>>>>> 2fb93fa Complete automated tests for the rate shopper API
}
