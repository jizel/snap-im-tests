package travel.snapshot.dp.qa.serenity.analytics;

import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

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
}
