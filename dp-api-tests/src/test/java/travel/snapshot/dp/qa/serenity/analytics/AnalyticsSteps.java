package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.hamcrest.collection.IsArrayWithSize;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Stats;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class AnalyticsSteps extends BasicSteps {


    public AnalyticsSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(SOCIAL_MEDIA_BASE_URI));
    }

    @Step
    public void getDataWithoutProperty(String url) {
        Response response = given().spec(spec).get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void responseContainsValues(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("values.size()", is(5));
    }

    @Step
    public void getData(String url, String granularity, String property, String since, String until) {
        Response response = given().spec(spec)
                .header("x-property", property)
                .parameter("granularity", granularity)
                .parameter("since", since)
                .parameter("until", until)
                .parameter("access_token", "aaa")
                .get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    public void responseContainsValuesForAllMetrics(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        Stats stats = response.as(Stats.class);
        stats.getData().forEach(m -> {
            assertEquals(String.format("Size of %s should be %d, but is %d", m.getName(), count, m.getValues().size()), count, m.getValues().size());
        });
    }
}
