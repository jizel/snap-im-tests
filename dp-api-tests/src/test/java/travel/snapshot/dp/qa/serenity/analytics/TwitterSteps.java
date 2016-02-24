package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;

import java.util.HashMap;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class TwitterSteps extends AnalyticsBaseSteps {

    public TwitterSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(TWITTER_BASE_URI));
    }


    public void getTwitterTweets(String url, String limit, String cursor, String sortAsc, String sortDesc, String property) {

        Response response = getEntities(url, limit, cursor, null, sortAsc, sortDesc, new HashMap<String, String>() {{
            put("property", property);
        }});

        setSessionResponse(response);
    }
}
