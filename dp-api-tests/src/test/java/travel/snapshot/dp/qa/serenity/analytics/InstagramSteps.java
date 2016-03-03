package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.analytics.model.GlobalStatsDto;
import travel.snapshot.dp.api.analytics.model.MetricDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class InstagramSteps extends AnalyticsBaseSteps {


    public InstagramSteps() {
        spec.baseUri(PropertiesHelper.getProperty(INSTAGRAM_BASE_URI));
    }


    /**
     * This method is set to check only one value for every metric in object
     *
     * @param expected list of values that each is unique
     */
    public void checkListofValuesFromResponse(List<Integer> expected) {
        Response response = getSessionResponse();
        GlobalStatsDto actual = response.body().as(GlobalStatsDto.class);

        ArrayList<Integer> actualList = new ArrayList<Integer>();
        for(MetricDto metric : actual.getData()){
            List<Long> actualValues = metric.getValues();

            if (actualValues.size() == 1){
                actualList.add(Integer.valueOf(actualValues.get(0).toString()));
            }
        }

        expected.sort(Integer::compareTo);
        actualList.sort(Integer::compareTo);

        assertEquals(expected, actualList);
    }
}
