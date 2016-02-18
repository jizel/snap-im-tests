package travel.snapshot.dp.qa.serenity.analytics;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class InstagramSteps extends AnalyticsBaseSteps {


    public InstagramSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(INSTAGRAM_BASE_URI));
    }


}
