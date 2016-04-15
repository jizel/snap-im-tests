package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;

import junit.framework.Assert;

import net.thucydides.core.annotations.Step;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;

import static groovy.util.GroovyTestCase.assertEquals;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class FacebookSteps extends AnalyticsBaseSteps {


    public FacebookSteps() {
        spec.baseUri(PropertiesHelper.getProperty(FACEBOOK_BASE_URI));
    }

    /**
     * Method is design that it will check only first facebook post for correct data, for moving to
     * another ones you can use cursor in request
     */
    @Step
    public void checkFacebookPostFromResponse(String datetimeExpected, int engagementExpected, String contentExpected, int reachExpected) throws Exception {
        Response response = getSessionResponse();
        for (int i = 0; i < (int) response.path("resultList.size()"); i++) {

            assertEquals(response.path("resultList.datetime[0]"), datetimeExpected);
            assertEquals(response.path("resultList.content[0]"), contentExpected);
            Assert.assertEquals((int) response.path("resultList.reach[0]"), reachExpected);
            Assert.assertEquals((int) response.path("resultList.engagement[0]"), engagementExpected);
        }
    }
}
