package travel.snapshot.dp.qa.serenity.analytics;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class TwitterSteps extends AnalyticsBaseSteps {


    public TwitterSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(TWITTER_BASE_URI));
    }


}
