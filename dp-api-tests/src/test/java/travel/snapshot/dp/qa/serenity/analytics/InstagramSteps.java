package travel.snapshot.dp.qa.serenity.analytics;

import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class InstagramSteps extends AnalyticsBaseSteps {


    public InstagramSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(INSTAGRAM_BASE_URI));
    }


}
