package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;

import net.thucydides.core.annotations.Step;

import java.util.HashMap;
import java.util.List;



import static org.junit.Assert.assertEquals;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class TwitterSteps extends AnalyticsBaseSteps {

    public TwitterSteps() {
        super();
        spec.baseUri(propertiesHelper.getProperty(TWITTER_BASE_URI));
    }

    @Step
    public void getTwitterTweets(String url, String limit, String cursor, String sortAsc, String sortDesc, String property) {

        Response response = getEntities(url, limit, cursor, null, sortAsc, sortDesc, new HashMap<String, String>() {{
            put("property", property);
        }});

        setSessionResponse(response);
    }

    @Step
    public void numberOfTwitterPostsInResponse(int count) {
        List responseList = getSessionResponse().jsonPath().get("resultList");
        assertEquals("There should be " + count + " entities got", count, responseList.size());
    }

    @Step
    public void listOfObjectsAreSortedAccordingToProperty(String property, boolean ascending, Class propertyClassType) {
        if (property == null || property.isEmpty()) {
            throw new IllegalArgumentException("Property must not be a null object.");
        }

        List values = getSessionResponse().body().jsonPath().getList("resultList." + property, propertyClassType);

        listOfObjectsAreSortedAccordingToProperty(values, ascending, propertyClassType);
    }
}
