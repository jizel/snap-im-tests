package travel.snapshot.dp.qa.serenity.analytics;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class FacebookSteps extends AnalyticsBaseSteps {


    public FacebookSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(FACEBOOK_BASE_URI));
    }


}
